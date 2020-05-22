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

package dipro.stoch;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Iterator;
import java.util.LinkedList;

import parser.ast.Expression;
import parser.ast.ModulesFile;
import parser.ast.PropertiesFile;
import prism.Model;
import prism.Prism;
import prism.Result;
import dipro.alg.KSPAlgorithm;
import dipro.graph.DirectedEdge;
import dipro.graph.State;
import dipro.graph.Vertex;
import dipro.run.PrismDefaultContext;
import dipro.run.Registry;
import dipro.stoch.mrmc.MRMCModelChecker;
import dipro.util.Trace;

public class KCTMCSolutionCollector extends KUniformCTMCSolutionCollector {

	private MRMCModelChecker mrmcModelchecker; 
	
	public KCTMCSolutionCollector(KSPAlgorithm alg) throws Exception {
		super(alg);
		mrmcModelchecker = null; 
	}
	
	private float totalExitRate(State s) {
		float exitRate = 0.0f;
		Iterator<? extends DirectedEdge> iter = ((KSPAlgorithm)alg).getBasicSearchAlgorithm().getExploredGraph().outgoingEdges(s);
		while (iter.hasNext()) {
			StochasticTransition t = (StochasticTransition)iter.next();
			assert t.source().equals(s);
			exitRate = exitRate + t.getProbOrRate();
		}
		return exitRate;
	}

	protected double prismModelCheckTrace(Trace trace) throws Exception {
		File sFile = new File("trace.temp.s");
		File tFile = new File("trace.temp.t");
		File lFile = new File("trace.temp.l");
		PrintStream out = new PrintStream(lFile);
		out.println("0=\"init\" 1=\"deadlock\"");
		out.println("0: 0");
		out.close();
		
		out = new PrintStream(sFile);
		out.println("(id)");
		int sinkId = trace.length();
		for (int i = 0; i <= trace.length(); i++) {
			out.println(i + ":(" + i + ")");
		}
		out.close();
		out = new PrintStream(tFile);
		out.println();
		DecimalFormatSymbols symb = new DecimalFormatSymbols();
		symb.setDecimalSeparator('.');
		DecimalFormat form = new DecimalFormat(
				"######0.0############################################################",
				symb);
		for (int i = 0; i < trace.length() - 1; i++) {
			StochasticTransition transition = (StochasticTransition) trace
					.getEdge(i);
			Vertex vertex = transition.source();
			assert vertex.equals(trace.getVertex(i));
			out.println(i + " " + (i + 1) + " " + form.format(transition.getProbOrRate()));
			double exit = transition.getProbOrRate();
			double totalExit = totalExitRate((State)vertex);
			double toSink = totalExit - exit;
//			if(toSink<0) {
//				System.out.println("totalExit="+totalExit+", exit="+exit+", toSink = "+toSink);
//			}
			assert toSink >= -1e-6;
			if(toSink<0) toSink = 0.0d;
			assert toSink >= 0.0d;
			if (toSink != 0) {
				out.println(i + " " + sinkId + " " + form.format(toSink));
			}
		}
		out.close();
		StochUntil prop = (StochUntil) alg.getContext().getProperty();
		StringBuilder sb = new StringBuilder("P=? [ true U");
		if (prop instanceof StochTBoundedUntil) {
			double t = ((StochTBoundedUntil) prop).timeBound();
			sb.append("<=");
			sb.append(t);
		}
		sb.append(" id=");
		sb.append(trace.length() - 1);
		sb.append(" ]");
		File pFile = new File("trace.temp.csl");
		out = new PrintStream(pFile);
		out.println(sb);
		out.close();
		PrismDefaultContext pc = (PrismDefaultContext) alg.getContext();
		Prism prism = pc.getPrism();
		ModulesFile mf = prism.parseExplicitModel(sFile, tFile, lFile,
				ModulesFile.STOCHASTIC);
		PropertiesFile pf = prism.parsePropertiesFile(mf, pFile);
		// pf.setUndefinedConstants(ps.getPrismModel().constantValues());
		Expression f = pf.getProperty(0);
		Model m = prism.buildExplicitModel();
		Result result = prism.modelCheck(m, pf, f);
		double prob = ((Double) result.getResult()).doubleValue();
		pc.getPrismMainLog().flush();
		pc.getPrismTechLog().flush();
		m.clear();
		tFile.delete();
		sFile.delete();
		m = null;
		pf = null;
		mf = null;
		prism = null;
		sFile = null;
		tFile = null;
		System.gc();
		return prob;
	}

	protected double mrmcModelCheckTrace(Trace trace) throws Exception {
		if(mrmcModelchecker==null) mrmcModelchecker = new MRMCModelChecker();
		String labFileName = "trace.temp.lab";
		String traFileName = "trace.temp.tra";
		File labFile = new File(labFileName);
		File traFile = new File(traFileName);
		PrintStream lab = new PrintStream(labFile);
		lab.println("#DECLARATION");
		lab.println("init target");
		lab.println("#END");
		int sinkId = trace.length()+1;
		lab.println("1 init");
		lab.println(trace.length()+" target");
		lab.close();
		DecimalFormatSymbols symb = new DecimalFormatSymbols();
		symb.setDecimalSeparator('.');
		DecimalFormat form = new DecimalFormat(
				"######0.0############################################################",
				symb);
		LinkedList<String> transitions = new LinkedList<String>();
		for (int i = 0; i < trace.length() - 1; i++) {
			StochasticTransition transition = (StochasticTransition) trace
					.getEdge(i);
			Vertex vertex = transition.source();
			assert vertex.equals(trace.getVertex(i));
			transitions.add((i+1) + " " + (i + 2) + " " + form.format(transition.getProbOrRate()));
			double exit = transition.getProbOrRate();
			double loopRate = ((CTMC)alg.getContext().getGraph()).loopRate((State)vertex);
			if (loopRate > 0.0d) {
				transitions.add((i+1) + " " + (i+1) + " "
						+ form.format(loopRate));
				exit = exit + loopRate;
			}
			double totalExit = totalExitRate((State)vertex);
			double toSink = totalExit - exit;
//			if(toSink<0) {
//				System.out.println("totalExit="+totalExit+", exit="+exit+", toSink = "+toSink);
//			}
			assert toSink >= -1e-6;
			if(toSink<0) toSink = 0.0d;
			assert toSink >= 0.0d;
			if (toSink != 0) {
				transitions.add((i+1) + " " + sinkId + " " + form.format(toSink));
			}
		}
		PrintStream tra = new PrintStream(traFile);
		tra.println("STATES "+(trace.length()+1));
		tra.println("TRANSITIONS "+(transitions.size()));
		for(String t: transitions) {
			tra.println(t);
		}
		tra.close();
		String prop;
		if (alg.getContext().getProperty() instanceof StochTBoundedUntil) {
			double timeBound = ((StochTBoundedUntil) alg.getContext().getProperty()).timeBound();
			prop = "P{ <= 1.0 }[ tt U[ 0, " + timeBound + " ] target ]";
		} else {
			prop = "P{ <= 1.0 }[ tt U target ]";
		}
		double result = mrmcModelchecker.performModelChecking(traFileName, labFileName, "ctmc", prop, 1);
		return result;
	}
}
