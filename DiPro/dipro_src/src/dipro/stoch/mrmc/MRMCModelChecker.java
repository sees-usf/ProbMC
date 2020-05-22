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

package dipro.stoch.mrmc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import dipro.run.Context;
import dipro.run.MRMCContext;
import dipro.run.Main;
import dipro.run.ModelChecker;
import dipro.run.Registry;
import dipro.stoch.CTMC;
import dipro.stoch.DTMC;
import dipro.stoch.UniformCTMC;
import dipro.util.DiProException;

public class MRMCModelChecker implements ModelChecker {

	private ArrayList<Double> resultProbVector;
	private double mrmcErrorBound = 1e-20;

	public Object performModelChecking(Context settings) throws Exception {
		MRMCContext mrmcContext = (MRMCContext) settings;
		int initStateId =((MRMCState)mrmcContext.getStart()).getId();
		String prop = mrmcContext.getProperty().toString();
		return performModelChecking(mrmcContext, prop, initStateId);
	}
	
	protected void performModelChecking(MRMCContext context, String prop) throws Exception {
		String traFile = context.getTraFileName();
		String labFile = context.getLabFileName();
		String modelType = getModelTypeStr(context);
		performModelChecking(traFile, labFile, modelType, prop);
	}

	protected Object performModelChecking(MRMCContext context, String prop, int stateId)
			throws Exception {
		String traFile = context.getTraFileName();
		String labFile = context.getLabFileName();
		String modelType = getModelTypeStr(context);
		return performModelChecking(traFile, labFile, modelType, prop, stateId);
	}
	
	private String getModelTypeStr(Context context) throws DiProException {
		String modelType; 
		if(context.getGraph().getClass() == DTMC.class) {
			modelType = "dtmc"; 
		} 
		else {
			if(context.getGraph().getClass() == UniformCTMC.class || context.getGraph().getClass() == CTMC.class ) {
				modelType = "ctmc";
			}
			else {
				throw new DiProException("The model type "+context.getGraph().getClass()+" is not supported by MRCM");
			}
		}
		return modelType;
	}
	
	public double performModelChecking(String traFile, String labFile, String modelType, String prop, int stateId) throws Exception {
		Process process = Runtime.getRuntime().exec(
				"mrmc" + " " + modelType + " -flump " + traFile + " " + labFile);
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(process
				.getOutputStream()));
		writer.println("set print off");
		writer.println("set error_bound "+mrmcErrorBound);
		writer.println(prop);
		// System.out.println("Check: "+prop);
		String resStr = "$RESULT[" + stateId + "]";
		writer.println(resStr);
		writer.flush();
		writer.close();
		// Read the result
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				process.getInputStream()));
		String line = null;
		double result = -1.0d;
		while ((line = reader.readLine()) != null) {
			if (line.contains("ERROR:") || line.contains("error:")
					|| line.contains("Error:")) {
//				out.println("MRMC: " + line);
				Registry.getMain().handleWarning("MRMC: " + line);
			}
			/* < ForDebugging > 
			Registry.getMain().out().println(line);
			/* </ ForDebugging > */
			if (line.startsWith(">>" + resStr)) {
				String[] r = line.split("=");
				result = Double.parseDouble(r[1]);
				break;
			}
		}
		reader.close();
		return result;
	}
	
	public void performModelChecking(String traFile, String labFile, String modelType, String prop) throws Exception {
		Main main = Registry.getMain();
//		String cmd = "mrmc" + " " + modelType + " " + traFile + " " + labFile +" > mrmc_output.txt";
		String cmd = "mrmc" + " " + modelType + " " + traFile + " " + labFile;
		System.out.println("MRMC Command: " + cmd);
		Process process = Runtime.getRuntime().exec(cmd);
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(process
				.getOutputStream()));
		writer.println("set error_bound "+mrmcErrorBound);
		writer.println(prop);
		System.out.println("Property: "+prop);
		writer.println("quit");
		writer.close();
//		process.waitFor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				process.getInputStream()));
//		File f = new File("mrmc_output.txt");
//		System.out.println(f.getCanonicalPath()+" exits? "+f.exists());
//		BufferedReader reader = new BufferedReader(new FileReader(f));
		int n = -1;
		String line = null;
		while ((line = reader.readLine()) != null) {
//			System.out.println("MRMC: " + line);
			if (line.contains("ERROR") || line.contains("error")
					|| line.contains("Error")) {
				main.handleWarning("MRMC: " + line);
			}
			if(line.contains("States=")) {
				String s = line.split("[=,]")[1];
				n = Integer.parseInt(s);
				resultProbVector = new ArrayList<Double>(n);
			}
//			if (line.startsWith(">>syntax error"))
//				break;
			if (line.contains("$RESULT")) {
				int i = line.indexOf('(');
				assert i > 0;
				int j = line.indexOf(')');
				String[] r = line.substring(i + 1, j).split(", ");
				for (int k = 0; k < r.length; k++) {
					double p = Double.parseDouble(r[k]);
					resultProbVector.add(p);
				}
				// result.add(Float.parseFloat(r[r.length-1].split(" ")[0]));
				break;
			}
//			else {
//				main.out().println(line);
//			}
		}
		reader.close();
		assert resultProbVector.size() == n;
		System.out.println("Done");
	}

	public ArrayList<Double> getResultProbVector() {
		return resultProbVector;
	}
}
