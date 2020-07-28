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



//
package dipro.run;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import prism.Prism;
import dipro.alg.BF;
import dipro.util.DiProException;


//Contains main function
public class Main {

	protected PrintStream out;
	protected PrintStream tech;

	protected boolean batchEnd = false;

	protected LinkedList<String[]> experiments;
	protected int experimentCounter;
	protected int nextExperimentIndex;

	protected String expBasePath;

	public Main() {
		initLogStreams();
		File workingDir = new File(System.getProperty("user.dir"));
		try {
			expBasePath = workingDir.getCanonicalPath();
		} catch (IOException e) {
			expBasePath = workingDir.getAbsolutePath();
			handleWarning("Failed to get the canonical path from "+expBasePath, e);
		}
		experiments = new LinkedList<String[]>();
		experimentCounter = 0;
		nextExperimentIndex = 0;
		Registry.setMain(this);
	}

	protected void initLogStreams() {
		out = System.out;
		tech = System.err;
		File f = new File("dipro.log");
		try {
			tech = new PrintStream(new FileOutputStream(f));
		} catch (FileNotFoundException e) {
			tech = System.err;
			String s = null;
			try {
				s = f.getCanonicalPath();
			} catch (IOException e1) {
				s = "dipro.log";
				e1.printStackTrace(System.err);
			}
			System.err.println("WARNING: Could not open file " + s);
			e.printStackTrace(System.err);
			System.err.println("WARNING: System.err is used as a technical log stream");
		}
		// ForDebugging 
		tech = System.err;
	}
	
	//Loads manual if invalid parameters, otherwise starts up DiPro
	public void start(String[] args) {
		if (args.length == 0) {
			System.out.println("Invalid parameters! Please, read the manual!");
			try {
				printManual();
			} catch(IOException e) {
				handleWarning("Failed to load manual file", e);
			}
			System.exit(0);
		}
		if (args.length == 1&& (args[0].equals("--help") || args[0].equals("-help"))) {
			try {
				printManual();
			} catch(IOException e) {
				handleWarning("Failed to load manual file", e);
			}
			System.exit(0);
		}
		if (args.length == 2 && args[0].equals("-batch")) {
			System.out.println("Run in batch mode...");
			out.println("Batch file: " + args[1]);
			out.println();
			batchRun(args[1]);
		} else {
			run(args);
		}
	}

	public void printManual() throws IOException {
		String srcName = "etc/manual.txt";
		URL url = ClassLoader.getSystemResource(srcName);
		if (url == null) 
		{
			throw new FileNotFoundException(srcName);
		}
		File file = null;
		try {
			file = new File(url.toURI());
		} catch (URISyntaxException e) {
			throw new IOException("Invalid URL "+url);
		}
		assert file != null;
		BufferedReader in = new BufferedReader(new FileReader(file));
		String line = in.readLine();
		while (line != null) {
			System.out.println(line);
			line = in.readLine();
		}
		in.close();
	}
	

	public void loadExperiments(String expFileName) {
		experiments.clear();
		nextExperimentIndex = 0;
		batchEnd = false;
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(expFileName));
			while (!batchEnd) {
				try {
					String[] paramters = readExpParameters(in);
					if (paramters == null)
						continue;
					if (paramters[0].startsWith("!skip")
							|| paramters[0].startsWith("!Skip")
							|| paramters[0].startsWith("!SKIP")) {
						// skip this experiment
						continue;
					}
					experiments.add(paramters);
					for (int i = 0; i < 10; i++)
						System.gc();
				} catch (IOException e) {
					handleError("Failed to read experiment parameters!", e);
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			handleFatalError("Batch file not found: "+expFileName, e);
		} catch (IOException e) {
			handleError("Batch file could not be read: "+expFileName, e);
		}
	}

	public void batchRun(String expFileName) {
		URL url = ClassLoader.getSystemResource(expFileName);
		if (url == null) {
			Registry.getMain().handleFatalError("Failed to load color scale file: "+ expFileName, 
					new DiProException("Failed to load color scale file \""
							+ expFileName + "\""));
		}
		
		try {
			File expFile = new File(url.getFile());
			if (!expFile.exists()) {
				throw new DiProException("Batch file does not exist: "+ expFileName);
			}
			try {
				expBasePath = expFile.getParentFile().getCanonicalPath();
			} catch (IOException e) {
				expBasePath = expFile.getParentFile().getAbsolutePath();
				handleWarning("Failed to get the canonical path from "+expBasePath, e);
			}
		} catch (DiProException e) {
			handleFatalError("Batch file does not exist: "+ expFileName, e);
		} 
		loadExperiments(expFileName);
		while (nextExperimentIndex < experiments.size()) {
			runNextExperiment();
		}
	}

	protected String[] readExpParameters(BufferedReader in) throws IOException {
		StringBuilder sb = new StringBuilder();
		String line = in.readLine();
		while (line != null && line.length() == 0)
			line = in.readLine();
		while (true) {
			if (line == null) {
				batchEnd = true;
				break;
			}
			if (line.equals("")) {
				break;
			}
			if (!line.startsWith("//")) {
				if (sb.length() > 0 && sb.charAt(sb.length() - 1) != ' ')
					sb.append(" ");
				sb.append(line);
			}
			line = in.readLine();
		}
		if (sb.length() == 0)
			return null;
		String[] params = sb.toString().split(" ");
		return params;
	}

	public void runNextExperiment() {
		if (nextExperimentIndex >= experiments.size())
			return;
		int expIndex = nextExperimentIndex;
		nextExperimentIndex++;
		runExperiment(expIndex);
	}

	public void reRunExperiment() {
		int expIndex = nextExperimentIndex - 1;
		assert expIndex >= 0 && expIndex < experiments.size();
		runExperiment(expIndex);
	}

	public void runExperiment(int expIndex) {
		assert expIndex < experiments.size();
		String[] params = experiments.get(expIndex);
		run(params);
	}

	//Creates DiPro object and runs algorithm to find a counterexample
	public void run(String[] params) {
		experimentCounter++;
		DiPro dipro = null;
		Config config = null;
		Context context = null;
		BF alg = null;
		try {
			dipro = createDiPro();
			config = dipro.loadConfig(params);
			out.println("Experiment " + experimentCounter
					+ " =========================================");
			// out.println(formatParams(params));
			context = dipro.loadContext(experimentCounter, config);
			System.out.println("run - Main - after loadContext");
			context.init();
			System.out.println("run - Main - after context.init");
			// out.println(config);
			out.println(context);
			try {
				alg = context.loadAlgorithm();
				System.out.println("run - Main - after loadAlgorithm");
				alg.init();
				System.out.println("run - Main - after alg.init");
				out.println("Search using " + alg + " ...");
//				if(alg.getHeuristic()!=null) {
//					out.println("Heuristic " + alg.getHeuristic().getClass().getName());
//				}
//				else {
//					out.println("No heuristic is used.");
//				}
				alg.execute();
				System.out.println("run - Main - after alg.execute");
				//out.println(alg.getSummaryReport()); - Same thing in execute
			} catch (Exception e) {
				handleError("Experiment failed! ", e);
			} finally {
				if(alg != null) {
					alg.cleanup();
					System.out.println("run - Main - after alg cleanup");
				}
			}
//			/* Just for QEST 2009 */
//			if(config.modelType == Config.PRISM_EXPLICIT_MODEL) {
//				if(config.algType== Config.K_STAR) {
//					config.algType = Config.EPPSTEIN;
////					config.algLogName = config.algLogName+".eppstein.log";
//					config.reportName = config.reportName+".eppstein.report";
//				}
//				else {
//					assert config.algType== Config.EPPSTEIN;
//					config.algType = Config.K_STAR;
////					config.algLogName = config.algLogName+".KStar.log";
//					config.reportName = config.reportName+".KStar.report";
//				}
//				try {
//					alg = context.loadAlgorithm();
//					alg.init();
//					out.println("Search using " + alg + " ...");
//	//				if(alg.getHeuristic()!=null) {
//	//					out.println("Heuristic " + alg.getHeuristic().getClass().getName());
//	//				}
//	//				else {
//	//					out.println("No heuristic is used.");
//	//				}
//					alg.execute();
//					out.println(alg.getSummaryReport());
//					alg.cleanup();
//				} catch (Exception e) {
//					handleError(e);
//					alg = null;
//				} 
//			}
//			/* End QEST 2009 */
			
		} catch (Exception e) {
			handleError("Experiment failed! ", e);
			context = null;
		} finally {
			if(context != null) {
				try {
					context.cleanup();
					System.out.println("run - Main - after context cleanup");
				} catch (Exception e) {
					handleError("Experiment failed! ", e);
				}
			}
		}
		System.gc();
		out.println("End-----------");
	}
	
	public void run()
	{
		this.out().println("Not supported in cmd-line version.");
	}

	protected DiPro createDiPro() throws Exception {
		return new DiPro();
	}

	protected String formatParams(String[] params) {
		StringBuilder sb = new StringBuilder();
		sb.append("--\nParameters\n");
		StringBuilder line = new StringBuilder();
		for (int i = 0; i < params.length; i++) {
			if (line.length() + params[i].length() > 50) {
				sb.append(line);
				sb.append("\n");
				line = new StringBuilder();
			}
			if (line.length() > 0)
				line.append(" ");
			line.append(params[i]);
		}
		sb.append(line);
		sb.append("\n--\n");
		return sb.toString();
	}

	public PrintStream out() {
		return out;
	}

	public PrintStream tech() {
		return tech;
	}

	public void handleFatalError(String msg) {
		handleFatalError(msg, null);
	}
	public void handleFatalError(String msg, Exception e) {
		if(msg != null) out.println("FATAL ERROR: " + msg);
		if(msg != null) tech.println("FATAL ERROR: " + msg);
		if(e != null) e.printStackTrace(tech);
		exit(1);
	}

	public void handleError(String e) {
		handleError(e, null);
	}
	public void handleError(String msg, Exception e) {
		if(msg != null) out.println("ERROR: "+ msg);
		if(msg != null) tech.println("ERROR: " + msg);
		if(e != null) e.printStackTrace(tech);
	}

	public void handleWarning(String msg) {
		handleWarning(msg, null);
	}
	
	public void handleWarning(String msg, Exception e) {
		if(msg != null) tech.println("WARNING: " + msg);
		if(e != null) e.printStackTrace(tech);
	}

	public String getExpBasePath() {
		return expBasePath;
	}

	public void exit(int code) {
		out.close();
		tech.close();
		out.println("System is shut down.");
		tech.println("System is shut down.");
		System.exit(code);
	}
	
	//Main Function
	public static void main(String[] args) {
		Prism a;
		Main main = new Main();
		main.start(args);
		main.exit(0);
	}
}
