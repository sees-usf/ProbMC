//==============================================================================
//	
//	Copyright (c) 2008-
//
//	Chair for Software Engineering - University of Konstanz
//	Prof. Dr. Stefan Leue
//	www.se.inf.uni-konstanz.de
//
//	Authors of this File:
//	* Husain Aljazzar (University of Konstanz)
//	* Florian Leitner-Fischer (University of Konstanz)
//	* Dimitar Simeonov (University of Konstanz)
//------------------------------------------------------------------------------
//	
// This file is part of DiPro.
//
//    DiPro is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    DiPro is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with DiPro.  If not, see <http://www.gnu.org/licenses/>.
//	
//==============================================================================

package dipro.stoch.prism;

import hybrid.PrismHybrid;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import parser.Values;
import parser.VarList;
import parser.ast.Expression;
import parser.ast.PropertiesFile;
import prism.Model;
import prism.Prism;
import prism.PrismException;
import prism.PrismFileLog;
import prism.PrismLangException;
import prism.PrismLog;
import prism.ProbModelChecker;
import prism.StateProbs;
import prism.StochModelChecker;
import dipro.run.Context;
import dipro.run.ModelChecker;
import dipro.run.PrismDefaultContext;
import dipro.run.Registry;

public class PrismModelChecker implements ModelChecker {

	public Object performModelChecking(Context context) throws PrismException {
		// Initialization
		PrismDefaultContext prismSettings = (PrismDefaultContext) context;
//		prismSettings.getConfig().getModel;
		Prism prism = prismSettings.getPrism();
		Registry.getMain().out().println("Check the model ... ");
		// Checking
		PropertiesFile pf = prismSettings.getPrismModel().propertiesFile();
		Expression f = ((PrismUntil) prismSettings.getProperty())
		.pctlFormula();
		long t = System.currentTimeMillis();
		Model m = prism.buildModel(prismSettings.getPrismModel().modulesFile());
		//The model should be built anyways
//		Model m = prismSettings.getConfig().parsedModel;

		Object result = performModelChecking(prism, m, pf , f);
//		Object result = prismSettings.getConfig().modelCheckResult;
		t = System.currentTimeMillis() - t;
		prismSettings.getPrismMainLog().flush();
		prismSettings.getPrismTechLog().flush();
		String s = "";
		// If needed, set the uniformization rate
		if (context.getConfig().uniformRate < 0.0) {
			double q = PrismHybrid.getLastUnif();
			prismSettings.adjustUniformRate(q);
			s = "Uniformization Rate is set to "+q;
//			((VisMain)Registry.getMain()).getGUI().showMessageDialog("The Uniformization Rate was too small, setting to: "+q);
		}
		m.clear();
		System.gc();
		// Reporting the result
		StringBuilder sb = new StringBuilder();
		sb.append("Result =  " + result + "\n");
		sb.append("States =  " + m.getNumStates() + "\n");
		sb.append("Trans. =  " + m.getNumTransitions() + "\n");
		sb.append("Runtime = " + t + "\n");
		if(s!=null) sb.append(s);
		Registry.getMain().out().println(sb);
		return result;
	}
	
//	public String adjustUniformRate(PrismContext context){
//		double q = PrismHybrid.getLastUnif();
//		context.adjustUniformRate(q);
//		return "Uniformization Rate is set to "+q;
//	}
	
	public Object performModelChecking(Prism prism, Model model, PropertiesFile pf, Expression f) throws PrismLangException, PrismException {
		Object result = prism.modelCheck(model, pf, f);
		return result;
	}
	
	public HashMap<PrismState,Double> performTransientAnalysis(Prism prism, Model model, double time) throws PrismException, IOException {
		StateProbs probs = null;
		if (time < 0) throw new PrismException("Cannot compute transient probabilities for negative time value");
		prism.ModelChecker mc;
		PrismLog piLog = new PrismFileLog("pi.tmp");
		if (model.getType() == Model.DTMC) {
			mc = new ProbModelChecker(prism, model, null);
			probs = ((ProbModelChecker)mc).doTransient((int)time);
		}
		else if (model.getType() == Model.CTMC) {
			mc = new StochModelChecker(prism, model, null);
			probs = ((StochModelChecker)mc).doTransient(time);
		}
		else {
			throw new PrismException("Transient probabilities only computed for DTMCs/CTMCs");
		}
		probs.print(piLog);
		piLog.flush();
		piLog.close();
		probs.clear();
		VarList varlist = model.getVarList();
		BufferedReader reader = new BufferedReader(new FileReader("pi.tmp"));
		HashMap<PrismState, Double> result = new HashMap<PrismState, Double>();
		String line = reader.readLine();
		while(line!=null) {
//			System.out.println(line);
			String[] a = line.split("[()]");
			String stateStr = a[1];
			String piStr = a[2].split("=")[1];
			Double pi = Double.parseDouble(piStr);
			String[] tokens = stateStr.split(",");
			Values values = new Values();
			boolean isTarget = false;
			for(int i=0; i<tokens.length; i++) {
				String n = varlist.getName(i);
				int t = varlist.getType(i);
				Object o;
				switch(t) {
				case Expression.BOOLEAN:
					o = Boolean.parseBoolean(tokens[i]);
					break;
				case Expression.INT:
					o = Integer.parseInt(tokens[i]);
					break;
				case Expression.DOUBLE:
					o = Double.parseDouble(tokens[i]);
					break;
				default: 
					throw new IllegalArgumentException();
				}
				if(n.equals("isTarget")) {
					isTarget = ((Integer)o)==1? true : false;
				}
				else {
					values.addValue(n, o);
				}
			}
			PrismState state = new PrismState(values);
			if(!result.containsKey(state)) {
				result.put(state, pi);
//				System.out.println("State "+state+", pi = "+pi);
			} else {
				if(isTarget) {
					result.put(state, pi);
				}
			}
			line = reader.readLine();
		}
		reader.close();
		return result;
	}
}
