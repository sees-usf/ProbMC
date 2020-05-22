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

package dipro.run;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import parser.Values;
import parser.ast.Expression;
import parser.ast.ModulesFile;
import parser.ast.PropertiesFile;
import prism.Prism;
import prism.PrismFileLog;
import prism.UndefinedConstants;
import dipro.alg.BF;
import dipro.h.pattern.PatternHeuristicProb;
import dipro.stoch.prism.PrismUntil;
import dipro.util.DiProException;
import dipro.util.DiagnosticPath;
import dipro.util.SolutionTracesRecorder;

public class DiPro implements CXGenerator {

	private PrintStream algLog;
	private boolean isPlugin;
	private Config config;

	public DiPro() throws Exception {
		this(false);
	}

	public DiPro(boolean isPlugIn) throws Exception {
		this.isPlugin = isPlugIn;
		// Registry.setDiPro(this);
	}

	// public Config loadConfig(String[] args){
	// return loadConfig(args, null);
	// }

	public Config loadConfig(String[] args) {
		Config config = parseArgs(args);
		init(config);
		if (config.logLevel > 0) {
			getAlgLog().println("Configuration loaded \n" + config);
		}
		return config;
	}

	public void init(Config config) {
		if (config.algLogName.equals("stdout")) {
			algLog = System.out;
		} else {
			try {
				String s = makeAbsoluteFileName(config.algLogName);
				algLog = new PrintStream(new FileOutputStream(s));
			} catch (FileNotFoundException e) {
				algLog = System.out;
				Registry.getMain().handleError(
						"Failed to initialise algorithm log stream!", e);
				Registry.getMain()
						.handleWarning(
								"Algorithm log stream is redirected to default output stream");
			}
		}
	}

	public String makeAbsoluteFileName(String relativeFilePath) {
		String absoluteFilePath = Registry.getMain().getExpBasePath()
				+ File.separator + relativeFilePath;
		return absoluteFilePath;
	}

	public String makeRelativeFileName(String absoluteFilePath) {
		int startIndex = (Registry.getMain().getExpBasePath() + "/").length();
		String relativeFilePath = absoluteFilePath.substring(startIndex,
				absoluteFilePath.length());
		return relativeFilePath;
	}

	public Context loadContext(int id, Config config) throws Exception {
		AbstractContext context = null;
		switch (config.modelType) {
		case Config.DIRECTED_GRAPH:
			context = new GraphContext(id, config);
			break;
		case Config.PRISM_MODEL:
			context = new PrismDefaultContext(id, config);
			break;
		// case Config.PRISM_MRMC_MODEL:
		// PrismContext tempSett = new PrismContext(id, config);
		// context = tempSett.convertToMRMC();
		// break;
		case Config.PRISM_EXPLICIT_MODEL:
			// PrismContext con = new PrismContext(id, config);
			// context = MDP2DTMC.convertToDTMC(con);
			context = new PrismExplicitContext(id, config);
			break;
		case Config.MRMC_MODEL:
			context = new MRMCContext(id, config);
			break;
		default:
			throw new IllegalArgumentException("Unsupported model type: "
					+ config.modelType);
		}
		return context;
	}

	public PrintStream getAlgLog() {
		return algLog;
	}

	public void setAlgLog(PrintStream m) {
		algLog = m;
	}

	protected Config parseArgs(String[] args) {
		// protected Config parseArgs(String[] args, CounterExampleInformation
		// info) {
		Config config = new Config(this);
		// if(info != null){
		// config.parsedModel = info.getParsedModel();
		// config.parsedModulesFile = info.getParsedModulesFile();
		// config.parsedPropertiesFile = info.getParsedPropertiesFile();
		// config.parsedConstantValues = info.getParsedConstantValues();
		// }
		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				/* Check if string is argument flag */
				String arg = args[i].trim();
				if (arg.length() == 0)
					continue;
				if (arg.startsWith("-")) {

					// Model Type
					if (arg.equals("-prism")) {
						config.modelType = Config.PRISM_MODEL;
						// if(config.pruneBound>1.0d) config.pruneBound = 0.0d;
						continue;
					}
					// if (arg.equals("-prismrmc")) {
					// config.modelType = Config.PRISM_MRMC_MODEL;
					// // if(config.pruneBound>1.0d) config.pruneBound = 0.0d;
					// continue;
					// }
					if (arg.equals("-exprism")) {
						config.modelType = Config.PRISM_EXPLICIT_MODEL;
						continue;
					}
					if (arg.equals("-mrmc")) {
						config.modelType = Config.MRMC_MODEL;
						// if(config.pruneBound>1.0d) config.pruneBound = 0.0d;
						continue;
					}
					if (arg.equals("-graph")) {
						config.modelType = Config.DIRECTED_GRAPH;
						continue;
					}
					if (arg.equals("-probgraph")) {
						config.modelType = Config.DIRECTED_GRAPH;
						// if(config.pruneBound>1.0d) config.pruneBound = 0.0d;
						continue;
					}

					// Model Options
					if (arg.equals("-mc")) {
						config.mc = true;
						continue;
					}
					if (arg.equals("-uniform")) {
						if (i < args.length - 1) {
							i++;
							try {
								config.uniformRate = Double
										.parseDouble(args[i]);
							} catch (NumberFormatException e) {
								config.uniformRate = -1.0f;
								System.out
										.println("Invalid uniformisation rate: "
												+ args[i]
												+ " \n"
												+ e.toString());
								System.exit(1);
							}
						} else {
							System.out
									.println("Warning: The option -uniform demands a uniformization rate (positive real number) as an argument.");
							System.out
									.println("         If necessary, uniformization rate will be determined by the model checker.");
						}
						continue;
					}

					if (arg.equals("-offlinevis")) {
						config.onlineVisualization = false;
						continue;
					}

					// Search Algorithm
					if (arg.equals("-kstar")) {
						config.algType = Config.K_STAR;
						continue;
					}
					if (arg.equals("-eppstein")) {
						config.algType = Config.EPPSTEIN;
						continue;
					}
					if (arg.equals("-xbf")) {
						config.algType = Config.XBF;
						if (i < args.length - 1) {
							if (args[i + 1].equals("-pi")) {
								config.usePi = true;
								i++;
							}
						}
						continue;
					}
					if (arg.equals("-bf")) {
						config.algType = Config.BF;
						continue;
					}
					if (arg.equals("-bf*")) {
						config.algType = Config.BF_STAR;
						continue;
					}

					// Search Options
					if (arg.equals("-greedy")) {
						config.greedy = true;
						continue;
					}
					if (arg.equals("-complete")) {
						config.complete = true;
						continue;
					}
					if (arg.equals("-maxiter")) {
						if (i < args.length - 1) {
							try {
								i++;
								config.maxIter = Integer.parseInt(args[i]);
							} catch (NumberFormatException e) {
								System.out
										.println("Invalid natural number maxiter :"
												+ args[i + 1]);
								System.exit(1);
							}
						} else {
							System.out
									.println("Warning: The option -maxiter demands a natural number as an argument.");
							System.out
									.println("         Default value will be used.");
						}
						continue;
					}
					if (arg.equals("-maxtime")) {
						if (i < args.length - 1) {
							try {
								i++;
								config.maxTime = Float.parseFloat(args[i]);
							} catch (NumberFormatException e) {
								System.out
										.println("Invalid number for maxtime :"
												+ args[i + 1]);
								System.exit(1);
							}
						} else {
							System.out
									.println("Warning: The option -maxiter demands a natural number as an argument.");
							System.out
									.println("         Default value will be used.");
						}
						continue;
					}
					if (arg.equals("-prune")) {
						if (i < args.length - 1) {
							i++;
							try {
								config.pruneBound = Double.parseDouble(args[i]);
							} catch (NumberFormatException e) {
								config.pruneBound = Double.MAX_VALUE - 1;
								System.out.println("Invalid prune bound: "
										+ args[i] + " \n");
								System.exit(1);
							}
						} else {
							System.out
									.println("Warning: The option -prune demands a prune bound (real number) as an argument.");
							System.out
									.println("         Default value will be used.");
						}
						continue;
					}

					if (arg.equals("-k")) {
						if (i < args.length - 1) {
							try {
								i++;
								int k = Integer.parseInt(args[i]);
								config.k = k;
							} catch (NumberFormatException e) {
								System.out.println("Invalid natural number k :"
										+ args[i]);
								System.exit(1);
							}
						} else {
							System.out
									.println("Warning: The option -k demands a natural number as an argument.");
							System.out
									.println("         Default value will be used.");
						}
						continue;
					}
					if (arg.equals("-solutionTrace")) {
						if (i < args.length - 1) {
							try {
								i++;
								int solutionTrace = Integer.parseInt(args[i]);
								config.solutionTrace = solutionTrace;
							} catch (NumberFormatException e) {
								System.out
										.println("Invalid natural number solutionTrace :"
												+ args[i]);
								System.exit(1);
							}
						} else {
							System.out
									.println("Warning: The option -solutionTrace demands a number from the set [-1,0,1]. \n -1: Diagnostic path \n 0: Txt file \n 1: Xml file");
							System.out
									.println("         Default value will be used.");
						}
						continue;
					}
					if (arg.equals("-kxsol")) {
						config.kxsol = true;
						continue;
					}
					if (arg.equals("-mrmcsol")) {
						config.mrmcsol = true;
						continue;
					}
					if (arg.equals("-mcsol")) {
						if (i < args.length - 1) {
							try {
								i++;
								int n = Integer.parseInt(args[i]);
								config.mcsol = n;
							} catch (NumberFormatException e) {
								System.out.println("Invalid natural number :"
										+ args[i]);
								System.exit(1);
							}
						} else {
							System.out
									.println("Warning: The option -mcsol demands a natural number as an argument.");
							System.out
									.println("         Default value will be used.");
						}
						continue;
					}
					if (arg.equals("-h")) {
						if (i < args.length - 1) {
							i++;
							config.heuristicName = args[i];
							boolean stop = false;
							do {
								if (i < args.length - 1) {
									if (args[i + 1].equals("-lenH")) {
										config.lengthHeuristic = true;
										i++;
										continue;
									}
									if (args[i + 1].equals("-probH")) {
										config.isProbPatternH = true;
										i++;
										continue;
									}
								}
								stop = true;
							} while (!stop);

						} else {
							System.out
									.println("Warning: The option -h demands the name of the heuristic java class as an argument.");
							System.out
									.println("         No heuristic will be used.");

						}
						continue;
					}

					if (arg.equals("-log")) {
						if (i < args.length - 1) {
							i++;
							config.algLogName = args[i];
						} else {
							System.out
									.println("Warning: The option -log demands the name of the technical log file as an argument.");
							System.out
									.println("         Technical log messages will be printed to the display.");
						}
						continue;
					}

					if (arg.equals("-cx")) {
						if (i < args.length - 1) {
							i++;
							config.cxFileName = args[i];
						} else {
							System.out
									.println("Warning: The option -cx demands the name of the file to write the CX as an argument.");
							config.cxFileName = null;
						}
						continue;
					}

					if (arg.equals("-loglevel")) {
						if (i < args.length - 1) {
							try {
								i++;
								config.logLevel = Integer.parseInt(args[i]);
							} catch (NumberFormatException e) {
								System.out
										.println("The option -loglevel demands a log level (natural number) as an argument.");
								System.exit(1);
							}
						} else {
							System.out
									.println("Warning: The option -loglevel demands the log level (natural number) as an argument.");
							System.out
									.println("         Default log level will be used. ");

						}
						continue;
					}
					if (arg.equals("-report")) {
						config.report = true;
						if (i < args.length - 1) {
							i++;
							config.reportName = args[i];
						} else {
							System.out
									.println("Report on the default output stream!");
							config.reportName = "stdout";
						}
						continue;
					}
					config.parameters.add(arg);
				} else {
					config.parameters.add(arg);
				}
			}
		}
		config.commit();
		return config;
	}

	public Config getConfig() {
		return config;

	}

	@Override
	public ArrayList<DiagnosticPath> generateCX(int propId)
			throws DiProException {
		Expression expr = config.getProp().getProperty(propId);
		if (!PrismUntil.isSupportedProperty(expr))
			throw new UnsupportedPropertyException("Unsupported property "
					+ expr.toString());

		if (config.algLogName.equals("stdout")) {
			algLog = System.out;
		} else {
			try {
				String s = makeAbsoluteFileName(config.algLogName);
				algLog = new PrintStream(new FileOutputStream(s));
			} catch (FileNotFoundException e) {
				algLog = System.out;
				Registry.getMain().handleError(
						"Failed to initialise algorithm log stream!", e);
				Registry.getMain()
						.handleWarning(
								"Algorithm log stream is redirected to default output stream");
			}
		}
		config.setPropId(propId);
		config.commit();
		Context context = null;
		BF alg = null;
		ArrayList<DiagnosticPath> paths = null;

		try {
			context = loadContext(0, config);

			context.init();
			try {
				alg = context.loadAlgorithm();
				alg.init();
				alg.execute();
				paths = alg.getDiagnosticPath();
				System.out.println(alg.getSummaryReport());
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
			} finally {
				if (alg != null) {
					alg.cleanup();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			context = null;
		} finally {
			if (context != null) {
				try {
					context.cleanup();
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println(e.getMessage());
				}
			}
		}
		System.gc();

		return paths;
	}

	public ArrayList<Integer> loadModel(ModulesFile modelFile)
			throws DiProException {

		ArrayList<Integer> supportedList = new ArrayList<Integer>();
		switch (modelFile.getType()) {

		case ModulesFile.STOCHASTIC:
			supportedList.add(Config.XBF);
			supportedList.add(Config.K_STAR);
			supportedList.add(Config.EPPSTEIN);
			break;
		case ModulesFile.PROBABILISTIC:
		case ModulesFile.NONDETERMINISTIC:
			supportedList.add(Config.K_STAR);
			supportedList.add(Config.EPPSTEIN);
			break;
		default:
			throw new DiProException("This type of model "
					+ modelFile.getTypeString() + " is not currently supported");

		}
		config.setModel(modelFile);
		return supportedList;
	}

	@Override
	public ArrayList<Integer> loadProp(PropertiesFile propFile)
			throws UnsupportedPropertyException {
		ArrayList<Integer> supportedList = new ArrayList<Integer>();
		for (int i = 0; i < propFile.getNumProperties(); i++) {
			Expression exp = propFile.getProperty(i);
			if (PrismUntil.isSupportedProperty(exp))
				supportedList.add(i);
		}
		if (supportedList.isEmpty())
			throw new UnsupportedPropertyException(
					"None supported property(ies)");
		config.setProp(propFile);

		return supportedList;
	}

	private Object parseValue(int type, String constValue) {
		switch (type) {
		case Expression.INT:
			return new Integer(constValue);
		case Expression.DOUBLE:
			return new Double(constValue);
		case Expression.BOOLEAN:
			return new Boolean(constValue);
		default:
			return constValue;
		}
	}

	@Override
	public void defineUnDefConst(HashMap<String, String> defParam)
			throws DiProException {
		ModulesFile mf = config.getModel();
		PropertiesFile pf = config.getProp();
		UndefinedConstants unDefConst = new UndefinedConstants(mf, pf);
		try {
			if (unDefConst.getMFNumUndefined() + unDefConst.getPFNumUndefined() > 0) {
				String constName, constValue;
				int type;
				for (int i = 0; i < unDefConst.getMFNumUndefined(); i++) {
					constName = unDefConst.getMFUndefinedName(i);
					constValue = defParam.get(constName);
					type = unDefConst.getMFUndefinedType(i);
					unDefConst.defineConstant(constName,
							"" + parseValue(type, constValue));
				}
				for (int i = 0; i < unDefConst.getPFNumUndefined(); i++) {
					constName = unDefConst.getPFUndefinedName(i);
					constValue = defParam.get(constName);
					type = unDefConst.getPFUndefinedType(i);
					unDefConst.defineConstant(constName,
							"" + parseValue(type, constValue));
				}
				unDefConst.checkAllDefined();
				unDefConst.initialiseIterators();
				mf.setUndefinedConstants(unDefConst.getMFConstantValues());
				pf.setUndefinedConstants(unDefConst.getPFConstantValues());

				Values constantValues = new Values();
				constantValues.addValues(mf.getConstantValues());
				constantValues.addValues(pf.getConstantValues());
				config.setConstantValues(constantValues);
			}
		} catch (Exception e) {
			new DiProException("Trying to define undefined constants:"
					+ e.getMessage());
		}

	}

	public boolean isPlugin() {
		return isPlugin;
	}

	public static void main(String... str) {
		Prism p = new Prism(new PrismFileLog("stdout"), new PrismFileLog(
				"stdout"));
		try {
			PatternHeuristicProb ph = new PatternHeuristicProb();

			/*
			 * //Constants for Airbag Model HashMap<String, String> unD = new
			 * HashMap<String, String>(); //unD.put("T", "10");
			 * 
			 * //----ORIGINAL Airbag MODEL ModulesFile mf0 =
			 * p.parseModelFile(new
			 * File("experiments/airbag/airbag_automaticmodel.pm"));
			 * PropertiesFile pf0 = p.parsePropertiesFile(mf0, new
			 * File("experiments/airbag/airbag_automaticmodel.csl"));
			 * CXGenerator dipro0 = new DiPro(true); Config config0 =
			 * dipro0.initConfig(); ArrayList<Integer> algoList0 =
			 * dipro0.loadModel(mf0); System.out.println("Algo supp:" +
			 * algoList0.toString()); ArrayList<Integer> propSupList0 =
			 * dipro0.loadProp(pf0); System.out.println("Prop supp:" +
			 * propSupList0.toString());
			 * 
			 * dipro0.defineUnDefConst(unD); config0.setAlgorithm(Config.XBF);
			 * config0.setReport(true);
			 * config0.setReportName("experiments/airbag/Uairbag.report");
			 * //config0.setHeuristicName("dipro.h.pattern.PatternHeuristic");
			 * //config0.setLengthHeuristic(true); //config0.setLogLevel(5);
			 * ArrayList<DiagnosticPath> paths0 = dipro0.generateCX(0);
			 * 
			 * 
			 * //----ABSTRACTION 1 of EMBEDDED MODEL
			 * 
			 * ModulesFile mf = p.parseModelFile(new
			 * File("experiments/airbag/sub1.pm")); PropertiesFile pf =
			 * p.parsePropertiesFile(mf, new
			 * File("experiments/airbag/sub1.csl")); CXGenerator dipro = new
			 * DiPro(true); Config config = dipro.initConfig();
			 * ArrayList<Integer> algoList = dipro.loadModel(mf);
			 * System.out.println("Algo supp:" + algoList.toString());
			 * ArrayList<Integer> propSupList = dipro.loadProp(pf);
			 * System.out.println("Prop supp:" + propSupList.toString());
			 * dipro.defineUnDefConst(unD); config.setAlgorithm(Config.XBF);
			 * config.setReport(true);
			 * config.setReportName("experiments/airbag/sub1.report");
			 * config.setReturnDiagnosticPaths(true); ArrayList<DiagnosticPath>
			 * paths = dipro.generateCX(0); ph.addAbstractDPs(paths);
			 * 
			 * //----Serialize Pattern DB for later use ph.serializePattern();
			 * 
			 * //---- ORIGINAL EMBEDDED MODEL WITH HEURISTIC ModulesFile mf1 =
			 * p.parseModelFile(new
			 * File("experiments/airbag/airbag_automaticmodel.pm"));
			 * PropertiesFile pf1 = p.parsePropertiesFile(mf1, new
			 * File("experiments/airbag/airbag_automaticmodel.csl"));
			 * CXGenerator dipro1 = new DiPro(true); Config config1 =
			 * dipro1.initConfig(); ArrayList<Integer> algoList1 =
			 * dipro1.loadModel(mf1); System.out.println("Algo supp:" +
			 * algoList1.toString()); ArrayList<Integer> propSupList1 =
			 * dipro1.loadProp(pf1); System.out.println("Prop supp:" +
			 * propSupList1.toString()); dipro1.defineUnDefConst(unD);
			 * config1.setAlgorithm(Config.XBF);
			 * config1.setHeuristicName("dipro.h.pattern.PatternHeuristicProb");
			 * //config1.setLengthHeuristic(true); //config1.setLogLevel(5);
			 * config1.setReport(true);
			 * config1.setReportName("experiments/airbag/Hairbag.report");
			 * ArrayList<DiagnosticPath> paths1 = dipro1.generateCX(0);
			 */
			/*
			 * //Constants for all Models HashMap<String, String> unD = new
			 * HashMap<String, String>(); unD.put("T", "1"); unD.put("N", "10");
			 * 
			 * //----ORIGINAL EMBEDDED MODEL ModulesFile mf0 =
			 * p.parseModelFile(new File("experiments/cluster/cluster.sm"));
			 * PropertiesFile pf0 = p.parsePropertiesFile(mf0, new
			 * File("experiments/cluster/cluster.csl")); CXGenerator dipro0 =
			 * new DiPro(true); Config config0 = dipro0.initConfig();
			 * ArrayList<Integer> algoList0 = dipro0.loadModel(mf0);
			 * System.out.println("Algo supp:" + algoList0.toString());
			 * ArrayList<Integer> propSupList0 = dipro0.loadProp(pf0);
			 * System.out.println("Prop supp:" + propSupList0.toString());
			 * 
			 * dipro0.defineUnDefConst(unD); config0.setAlgorithm(Config.XBF);
			 * //config0.setKxsol(true); config0.setReport(true);
			 * config0.setReportName("experiments/cluster/Ucluster.report");
			 * //config0.setReturnDiagnosticPaths(true);
			 * //config0.setHeuristicName("dipro.h.pattern.PatternHeuristic");
			 * //config0.setLengthHeuristic(true); //config0.setLogLevel(5);
			 * ArrayList<DiagnosticPath> paths0 = dipro0.generateCX(3);
			 * 
			 * 
			 * //----ABSTRACTION 1 of EMBEDDED MODEL
			 * 
			 * ModulesFile mf = p.parseModelFile(new
			 * File("experiments/cluster/sub1.sm")); PropertiesFile pf =
			 * p.parsePropertiesFile(mf, new
			 * File("experiments/cluster/sub1.csl")); CXGenerator dipro = new
			 * DiPro(true); Config config = dipro.initConfig();
			 * ArrayList<Integer> algoList = dipro.loadModel(mf);
			 * System.out.println("Algo supp:" + algoList.toString());
			 * ArrayList<Integer> propSupList = dipro.loadProp(pf);
			 * System.out.println("Prop supp:" + propSupList.toString());
			 * dipro.defineUnDefConst(unD); config.setAlgorithm(Config.XBF);
			 * //config.setKxsol(true); config.setReport(true);
			 * config.setReportName("experiments/cluster/sub1.report");
			 * config.setReturnDiagnosticPaths(true); ArrayList<DiagnosticPath>
			 * paths = dipro.generateCX(3); ph.addAbstractDPs(paths);
			 * 
			 * PrintStream cxOut = null; cxOut = new PrintStream(new
			 * FileOutputStream("cx.txt"));
			 * 
			 * for (DiagnosticPath diagnosticPath : paths) {
			 * cxOut.println("---------------"); cxOut.println("P: " +
			 * diagnosticPath.getProbability()); ArrayList<String> state =
			 * diagnosticPath.getStates(); for (String label : state) {
			 * cxOut.println(label); } cxOut.println("---------------"); }
			 * 
			 * 
			 * //----ABSTRACTION 2 of EMBEDDED MODEL
			 * 
			 * ModulesFile mf2 = p.parseModelFile(new
			 * File("experiments/cluster/sub2.sm")); PropertiesFile pf2 =
			 * p.parsePropertiesFile(mf2, new
			 * File("experiments/cluster/sub2.csl")); CXGenerator dipro2 = new
			 * DiPro(true); Config config2 = dipro2.initConfig();
			 * ArrayList<Integer> algoList2 = dipro2.loadModel(mf2);
			 * System.out.println("Algo supp:" + algoList2.toString());
			 * ArrayList<Integer> propSupList2 = dipro2.loadProp(pf2);
			 * System.out.println("Prop supp:" + propSupList2.toString());
			 * dipro2.defineUnDefConst(unD); config2.setAlgorithm(Config.XBF);
			 * //config2.setKxsol(true); config2.setReport(true);
			 * config2.setReportName("experiments/cluster/sub2.report");
			 * config2.setReturnDiagnosticPaths(true); paths =
			 * dipro2.generateCX(3); ph.addAbstractDPs(paths);
			 * 
			 * 
			 * for (DiagnosticPath diagnosticPath : paths) {
			 * cxOut.println("---------------"); cxOut.println("P: " +
			 * diagnosticPath.getProbability()); ArrayList<String> state =
			 * diagnosticPath.getStates(); for (String label : state) {
			 * cxOut.println(label); } cxOut.println("---------------"); }
			 * 
			 * 
			 * //----Serialize Pattern DB for later use ph.serializePattern();
			 * 
			 * //---- ORIGINAL EMBEDDED MODEL WITH HEURISTIC ModulesFile mf1 =
			 * p.parseModelFile(new File("experiments/cluster/cluster.sm"));
			 * PropertiesFile pf1 = p.parsePropertiesFile(mf1, new
			 * File("experiments/cluster/cluster.csl")); CXGenerator dipro1 =
			 * new DiPro(true); Config config1 = dipro1.initConfig();
			 * ArrayList<Integer> algoList1 = dipro1.loadModel(mf1);
			 * System.out.println("Algo supp:" + algoList1.toString());
			 * ArrayList<Integer> propSupList1 = dipro1.loadProp(pf1);
			 * System.out.println("Prop supp:" + propSupList1.toString());
			 * dipro1.defineUnDefConst(unD); config1.setAlgorithm(Config.XBF);
			 * //config1.setKxsol(true);
			 * config1.setHeuristicName("dipro.h.pattern.PatternHeuristicProb");
			 * config1.isProbPatternH = true;
			 * //config1.setLengthHeuristic(true);
			 * config1.setAlgLogName("log.txt"); config1.setLogLevel(5);
			 * config1.setReport(true);
			 * //config1.setReturnDiagnosticPaths(true);
			 * config1.setReportName("experiments/cluster/Hcluster.report");
			 * ArrayList<DiagnosticPath> paths1 = dipro1.generateCX(3);
			 */
			// Constants for all Models
			HashMap<String, String> unD = new HashMap<String, String>();
			unD.put("T", "1");
			unD.put("MAX_COUNT", "8");
			unD.put("B1", "3.0726E-4");
			unD.put("B2", "1.1413E-4");
			unD.put("B3", "0.05455");

			/*
			 * //----ABSTRACTION 1 of EMBEDDED MODEL ModulesFile mf =
			 * p.parseModelFile(new File("experiments/embedded/sub1.pm"));
			 * PropertiesFile pf = p.parsePropertiesFile(mf, new
			 * File("experiments/embedded/sub1.csl")); CXGenerator dipro = new
			 * DiPro(true); Config config = dipro.initConfig();
			 * ArrayList<Integer> algoList = dipro.loadModel(mf);
			 * System.out.println("Algo supp:" + algoList.toString());
			 * ArrayList<Integer> propSupList = dipro.loadProp(pf);
			 * System.out.println("Prop supp:" + propSupList.toString());
			 * dipro.defineUnDefConst(unD); config.setAlgorithm(Config.XBF);
			 * config.setReport(true);
			 * config.setReportName("experiments/embedded/sub1.report"); //
			 * config.setReturnDiagnosticPaths(true); config.solutionTrace =
			 * SolutionTracesRecorder.NO_TRACES; ArrayList<DiagnosticPath> paths
			 * = dipro.generateCX(0); ph.addAbstractDPs(paths);
			 * 
			 * PrintStream cxOut = null; cxOut = new PrintStream(new
			 * FileOutputStream("cx.txt"));
			 * 
			 * for (DiagnosticPath diagnosticPath : paths) {
			 * cxOut.println("---------------"); cxOut.println("P: " +
			 * diagnosticPath.getProbability()); ArrayList<String> state =
			 * diagnosticPath.getStates(); for (String label : state) {
			 * cxOut.println(label); } cxOut.println("---------------"); }
			 * 
			 * 
			 * //----ABSTRACTION 2 of EMBEDDED MODEL
			 * 
			 * ModulesFile mf2 = p.parseModelFile(new
			 * File("experiments/embedded/sub2.pm")); PropertiesFile pf2 =
			 * p.parsePropertiesFile(mf2, new
			 * File("experiments/embedded/sub2.csl")); CXGenerator dipro2 = new
			 * DiPro(true); Config config2 = dipro2.initConfig();
			 * ArrayList<Integer> algoList2 = dipro2.loadModel(mf2);
			 * System.out.println("Algo supp:" + algoList2.toString());
			 * ArrayList<Integer> propSupList2 = dipro2.loadProp(pf2);
			 * System.out.println("Prop supp:" + propSupList2.toString());
			 * dipro2.defineUnDefConst(unD); config2.setAlgorithm(Config.XBF);
			 * config2.setReport(true);
			 * config2.setReportName("experiments/embedded/sub2.report"); //
			 * config2.setReturnDiagnosticPaths(true); config.solutionTrace =
			 * SolutionTracesRecorder.NO_TRACES; paths = dipro2.generateCX(0);
			 * ph.addAbstractDPs(paths); for (DiagnosticPath diagnosticPath :
			 * paths) { cxOut.println("---------------"); cxOut.println("P: " +
			 * diagnosticPath.getProbability()); ArrayList<String> state =
			 * diagnosticPath.getStates(); for (String label : state) {
			 * cxOut.println(label); } cxOut.println("---------------"); }
			 * 
			 * //----ABSTRACTION 3 of EMBEDDED MODEL
			 * 
			 * ModulesFile mf3 = p.parseModelFile(new
			 * File("experiments/embedded/sub3.pm")); PropertiesFile pf3 =
			 * p.parsePropertiesFile(mf3, new
			 * File("experiments/embedded/sub3.csl")); CXGenerator dipro3 = new
			 * DiPro(true); Config config3 = dipro3.initConfig();
			 * ArrayList<Integer> algoList3 = dipro3.loadModel(mf3);
			 * System.out.println("Algo supp:" + algoList3.toString());
			 * ArrayList<Integer> propSupList3 = dipro3.loadProp(pf3);
			 * System.out.println("Prop supp:" + propSupList3.toString());
			 * dipro3.defineUnDefConst(unD); config3.setAlgorithm(Config.XBF);
			 * config3.setReport(true);
			 * config3.setReportName("experiments/embedded/sub3.report"); //
			 * config3.setReturnDiagnosticPaths(true); config.solutionTrace =
			 * SolutionTracesRecorder.NO_TRACES; paths = dipro3.generateCX(0);
			 * ph.addAbstractDPs(paths); for (DiagnosticPath diagnosticPath :
			 * paths) { cxOut.println("---------------"); cxOut.println("P: " +
			 * diagnosticPath.getProbability()); ArrayList<String> state =
			 * diagnosticPath.getStates(); for (String label : state) {
			 * cxOut.println(label); } cxOut.println("---------------"); }
			 * 
			 * //----ABSTRACTION 4 of EMBEDDED MODEL
			 * 
			 * ModulesFile mf4 = p.parseModelFile(new
			 * File("experiments/embedded/sub4.pm")); PropertiesFile pf4 =
			 * p.parsePropertiesFile(mf4, new
			 * File("experiments/embedded/sub4.csl")); CXGenerator dipro4 = new
			 * DiPro(true); Config config4 = dipro4.initConfig();
			 * ArrayList<Integer> algoList4 = dipro4.loadModel(mf4);
			 * System.out.println("Algo supp:" + algoList4.toString());
			 * ArrayList<Integer> propSupList4 = dipro4.loadProp(pf4);
			 * System.out.println("Prop supp:" + propSupList4.toString());
			 * dipro4.defineUnDefConst(unD); config4.setAlgorithm(Config.XBF);
			 * config4.setReport(true);
			 * config4.setReportName("experiments/embedded/sub4.report"); //
			 * config4.setReturnDiagnosticPaths(true); config.solutionTrace =
			 * SolutionTracesRecorder.NO_TRACES; paths = dipro4.generateCX(0);
			 * ph.addAbstractDPs(paths); for (DiagnosticPath diagnosticPath :
			 * paths) { cxOut.println("---------------"); cxOut.println("P: " +
			 * diagnosticPath.getProbability()); ArrayList<String> state =
			 * diagnosticPath.getStates(); for (String label : state) {
			 * cxOut.println(label); } cxOut.println("---------------"); }
			 * //----Serialize Pattern DB for later use ph.serializePattern();
			 */

			// ----ORIGINAL EMBEDDED MODEL
			ModulesFile mf00 = p.parseModelFile(new File(
					"experiments/embedded/embedded.sm"));
			PropertiesFile pf00 = p.parsePropertiesFile(mf00, new File(
					"experiments/embedded/embedded.csl"));
			CXGenerator dipro00 = new DiPro(true);
			Config config00 = dipro00.initConfig();
			ArrayList<Integer> algoList00 = dipro00.loadModel(mf00);
			System.out.println("Algo supp:" + algoList00.toString());
			ArrayList<Integer> propSupList00 = dipro00.loadProp(pf00);
			System.out.println("Prop supp:" + propSupList00.toString());

			dipro00.defineUnDefConst(unD);
			config00.setAlgorithm(Config.XBF);
			config00.setAlgLogName("ulog.txt");
			config00.setLogLevel(5);
			config00.setReport(true);
			config00.setReportName("experiments/embedded/Uembedded.report");
			// config0.setHeuristicName("dipro.h.pattern.PatternHeuristic");
			// config0.setLengthHeuristic(true);
			// config0.setLogLevel(5);
			ArrayList<DiagnosticPath> paths00 = dipro00.generateCX(0);

			// ---- ORIGINAL EMBEDDED MODEL WITH HEURISTIC
			ModulesFile mf1 = p.parseModelFile(new File(
					"experiments/embedded/embedded.sm"));
			PropertiesFile pf1 = p.parsePropertiesFile(mf1, new File(
					"experiments/embedded/embedded.csl"));
			CXGenerator dipro1 = new DiPro(true);
			Config config1 = dipro1.initConfig();
			ArrayList<Integer> algoList1 = dipro1.loadModel(mf1);
			System.out.println("Algo supp:" + algoList1.toString());
			ArrayList<Integer> propSupList1 = dipro1.loadProp(pf1);
			System.out.println("Prop supp:" + propSupList1.toString());
			dipro1.defineUnDefConst(unD);
			config1.setAlgorithm(Config.XBF);
			config1.setHeuristicName("dipro.h.pattern.PatternHeuristicProb");
			config1.isProbPatternH = true;

			// config1.setLengthHeuristic(true);
			config1.setAlgLogName("log.txt");
			config1.setLogLevel(5);
			config1.setReport(true);
			config1.setReportName("experiments/embedded/Hembedded.report");
			// config1.setReturnDiagnosticPaths(true);
			ArrayList<DiagnosticPath> paths = dipro1.generateCX(0);

			// File modelFile = new File("trainmodel/train_automaticmodel.pm");
			// File propFile = new File("trainmodel/train_automaticmodel.csl");
			// ModulesFile mf = p.parseModelFile(modelFile);
			// PropertiesFile pf = p.parsePropertiesFile(mf, propFile);
			// CXGenerator dipro = new DiPro(true);
			// Config config = dipro.initConfig();
			// config.setPropName(propFile.getName());
			// config.setModelName(modelFile.getName());
			// ArrayList<Integer> algoList = dipro.loadModel(mf);
			// System.out.println("Algo supp:" + algoList.toString());
			// ArrayList<Integer> propSupList = dipro.loadProp(pf);
			// System.out.println("Prop supp:" + propSupList.toString());
			// HashMap<String, String> unD = new HashMap<String, String>();
			// // unD.put("N", "1");
			// // unD.put("T", "1");
			// // dipro.defineUnDefConst(unD);
			// config.setAlgorithm(Config.XBF);
			// config.setLogLevel(3);
			// // config.setCxIncrementRatio(1.2d);
			// ArrayList<DiagnosticPath> k = dipro.generateCX(0);
			// System.out.println(k.toString());
			// //
			// ModulesFile mf = p.parseModelFile(new File("automatic.pm"));
			// PropertiesFile pf = p.parsePropertiesFile(mf, new File(
			// "automatic.csl"));
			// CXGenerator dipro = new DiPro(true);
			// Config config = dipro.initConfig();
			//
			// ArrayList<Integer> algoList = dipro.loadModel(mf);
			// System.out.println("Algo supp:" + algoList.toString());
			// ArrayList<Integer> propSupList = dipro.loadProp(pf);
			// System.out.println("Prop supp:" + propSupList.toString());
			// config.setMaxTime(0.2f);
			// config.setAlgorithm(Config.K_STAR);
			// dipro.generateCX(0);

			// for (DiagnosticPath path : paths)
			// System.out.println(path.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Config initConfig() {
		Registry.setMain(new Main());
		config = new Config(this);
		algLog = System.out;
		return config;
	}

	// public void handleFatalError(Exception e) {
	// config.getPrismMainLog().println("FATAL ERROR: " + e.toString());
	// config.getPrismTechLog().println("FATAL ERROR: " + e.toString());
	// e.printStackTrace();
	// System.exit(1);
	// }
	//
	// public void handleError(Exception e) {
	// config.getPrismMainLog().println("ERROR: "+e.toString());
	// config.getPrismTechLog().println("ERROR: " + e);
	// e.printStackTrace();
	// }
	//
	// public void handleWarning(Object msg) {
	// config.getPrismTechLog().println("WARNING: " + msg.toString());
	// if (msg instanceof Exception) {
	// ((Exception) msg).printStackTrace();
	// }
	// }
	//

}
