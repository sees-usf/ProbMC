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

import prism.Prism;
import prism.PrismException;
import prism.PrismLog;
import dipro.alg.BF;
import dipro.alg.KSPAlgorithm;
import dipro.alg.PiXSearchCreator;
import dipro.alg.ProbEppsteinCreator;
import dipro.alg.ProbKStarCreator;
import dipro.alg.ProbXSearchCreator;
import dipro.alg.StochXSearchCreator;
import dipro.stoch.CTMC;
import dipro.stoch.DTMC;
import dipro.stoch.KCTMCSolutionCollector;
import dipro.stoch.KDTMCSolutionCollector;
import dipro.stoch.KUniformCTMCSolutionCollector;
import dipro.stoch.MDP;
import dipro.stoch.MDPSolutionCollector;
import dipro.stoch.MarkovModel;
import dipro.stoch.StochKXSolutionCollector;
import dipro.stoch.StochTBoundedUntil;
import dipro.stoch.StochXSolutionCollector;
import dipro.stoch.UniformCTMC;
import dipro.stoch.prism.PrismModel;
import dipro.stoch.prism.PrismModelChecker;
import dipro.util.KDefaultSolutionCollector;
import dipro.util.SolutionCollector;

public abstract class AbstractPrismContext extends AbstractContext {

	protected String propFileName;
	protected int propIndex;
	protected String constFileName;
	protected PrismLog prismMainLog, prismTechLog;
	protected Object modelCheckingResult;
	protected PrismModel prismModel;

	//Constructor
	protected AbstractPrismContext(int id, Config config) throws Exception {
		super(id, config);
		// propIndex = config.propIndex;
		modelCheckingResult = null;
			prismModel = null;
	}

	public BF loadAlgorithm() throws Exception {
		BF alg;
		System.out.println("loadAlgorithm - abstractPrismContext");
		switch (config.algType) {
			case Config.XBF:
				System.out.println("XBF");
				if (property instanceof StochTBoundedUntil) {
					if (config.usePi) {
						assert graph.getClass() == UniformCTMC.class;
						alg = new PiXSearchCreator().createSearch(this);
					} else {
						assert graph.getClass() != UniformCTMC.class;
						if (graph.getClass() == CTMC.class) {
							if (config.lengthHeuristic) {
								alg = new ProbXSearchCreator().createSearch(this,
										true);
							} else {
								alg = new ProbXSearchCreator().createSearch(this);
							}
	
						} else {
							alg = new StochXSearchCreator().createSearch(this);
						}
					}
				} else {
					assert graph.getClass() != UniformCTMC.class;
					alg = new ProbXSearchCreator().createSearch(this);
				}
				break;
			case Config.K_STAR:
				System.out.println("k-Star");
				alg = new ProbKStarCreator().createSearch(this);
				break;
			case Config.EPPSTEIN:
				System.out.println("Eppstein");
				alg = new ProbEppsteinCreator().createSearch(this);
				break;
			default:
				throw new IllegalStateException("Invalid algorithm type : "
						+ config.algType);
		}
		if (config.report)
			attachReporter(alg);
		return alg;
	}

	public SolutionCollector createSolutionCollector(BF alg) throws Exception {
		SolutionCollector solutionCollector;
		System.out.println("createSolutionCollector - AbstractPrismContext");
		switch (config.algType) {
		// case Config.BF:
		// solutionCollector = new PrismXSolutionCollector(alg);
		// break;
		// case Config.SBF:
		// solutionCollector = new PrismXSolutionCollector(alg);
		// break;
		case Config.XBF:
			solutionCollector = new StochXSolutionCollector(alg);
			break;
		case Config.K_STAR:
		case Config.EPPSTEIN:
			// case Config.DIRECTED_EPPSTEIN:
			if (graph instanceof MDP) {
				solutionCollector = new MDPSolutionCollector((KSPAlgorithm) alg);
			} else {
				if (config.kxsol) {
					solutionCollector = new StochKXSolutionCollector(
							(KSPAlgorithm) alg);
				} else {
					if (graph.getClass() == UniformCTMC.class)
						solutionCollector = new KUniformCTMCSolutionCollector(
								(KSPAlgorithm) alg);
					else {
						if (graph.getClass() == CTMC.class)
							solutionCollector = new KCTMCSolutionCollector(
									(KSPAlgorithm) alg);
						else {
							if (graph.getClass() == DTMC.class)
								solutionCollector = new KDTMCSolutionCollector(
										(KSPAlgorithm) alg);
							else
								solutionCollector = new KDefaultSolutionCollector(
										(KSPAlgorithm) alg);
						}
					}
				}
			}
			break;
		default:
			throw new IllegalStateException("Invalid algorithm type : "
					+ config.algType);
		}
		return solutionCollector;
	}

	// @Override
	// protected void readParameters() throws DiProException {
	// Iterator<String> iter = config.parameters.iterator();
	// while (iter.hasNext()) {
	// String param = iter.next();
	// if (param.equals("-xmlconst")) {
	// constFileIsXML = true;
	// continue;
	// }
	// if (param.equals("-prop")) {
	// if (!iter.hasNext())
	// throw new DiProException(
	// "The option -prop demands a property index (integer number)");
	// else {
	// String s = iter.next();
	// try {
	// probIndex = Integer.parseInt(s);
	// continue;
	// } catch (NumberFormatException e) {
	// throw new DiProException("Invalid property index: " + s);
	// }
	// }
	// }
	// if (modelFileName != null && propFileName != null
	// && constFileName != null && probIndex >= 0) {
	// break;
	// }
	// if (modelFileName == null) {
	// modelFileName = config.getDiPro().makeAbsoluteFileName(param);
	// continue;
	// }
	// if (propFileName == null) {
	// propFileName = config.getDiPro().makeAbsoluteFileName(param);
	// continue;
	// }
	// if (constFileName == null) {
	// constFileName = config.getDiPro().makeAbsoluteFileName(param);
	// continue;
	// }
	// }
	// }

	// To retrieve the given information form the main Instance of Prism.

	// public PropertiesFile getParsedPropertiesFile() {
	// return config.parsedPropertiesFile;
	// }
	//
	// public ModulesFile getParsedModulesFile() {
	// return config.parsedModulesFile;
	// }
	//
	// public Model getParsedModel() {
	// return config.parsedModel;
	// }

	public MarkovModel getMarkovModel() {
		return (MarkovModel) graph;
	}

	public String getConstFileName() {
		return constFileName;
	}

	// public String getModelFileName() {
	// return modelFileName;
	// }

	public PrismLog getPrismMainLog() {
		return prismMainLog;
	}

	public PrismModel getPrismModel() {
		return prismModel;
	}

	public Prism getPrism() {
		return prismModel.getPrism();
	}

	public PrismLog getPrismTechLog() {
		return prismTechLog;
	}

	public int getProbIndex() {
		return propIndex;
	}

	public String getPropFileName() {
		return propFileName;
	}

	public void adjustUniformRate(double newUniformRate) {
		config.uniformRate = newUniformRate;
		if (graph instanceof UniformCTMC) {
			CTMC ctmc = ((UniformCTMC) graph).getCTMC();
			graph = new UniformCTMC(ctmc, config.uniformRate);
		}
	}

	public void performModelChecking() throws PrismException {
		if (modelCheckingResult != null)
			return;
		PrismModelChecker mc = new PrismModelChecker();
		modelCheckingResult = mc.performModelChecking(this);
		// modelCheckingResult = config.modelCheckResult;
	}

	public Object getModelCheckingResult() {
		return modelCheckingResult;
	}

	// public MRMCContext convertToMRMC() throws Exception {
	// String newMFName = getMRMCPrismModelFileName();
	// String newPFName = getMRMCPrismPropFileName();
	// String mrmcPropFName = getMRMCPropFileName();
	// prismModel.prepareForMRMCExport(newMFName, newPFName, mrmcPropFName);
	// String labFileName = newMFName + ".lab";
	// String traFileName = newMFName + ".tra";
	// StringBuilder command = new StringBuilder();
	// command.append("prism");
	// command.append(" ");
	// // System.out.println(newMFName);
	// command.append(newMFName);
	// command.append(" ");
	// command.append(newPFName);
	// command.append(" ");
	// command.append("-exportmrmc");
	// command.append(" ");
	// command.append("-exportlabels");
	// command.append(" ");
	// command.append(labFileName);
	// command.append(" ");
	// command.append("-exporttrans");
	// command.append(" ");
	// command.append(traFileName);
	//
	// Runtime builder = Runtime.getRuntime();
	// Process process = null;
	// try {
	// process = builder.exec(command.toString());
	// } catch (IOException e) {
	// System.err.println("IOException" + e.getMessage());
	// }
	//
	// InputStream in = process.getInputStream();
	// BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	// String line = null;
	// line = reader.readLine();
	// while(line!=null) {
	// // Registry.getMain().out.println("line " + line);
	// line = reader.readLine();
	// }
	// config.modelType = Config.MRMC_MODEL;
	// config.parameters.clear();
	// traFileName = getDiPro().makeRelativeFileName(traFileName);
	// labFileName = getDiPro().makeRelativeFileName(labFileName);
	// mrmcPropFName = getDiPro().makeRelativeFileName(mrmcPropFName);
	// config.parameters.add(traFileName);
	// config.parameters.add(labFileName);
	// config.parameters.add(mrmcPropFName);
	// config.parameters.add("-prop");
	// config.parameters.add("0");
	// String logic = null;
	// switch (prismModel.type()) {
	// case ModulesFile.NONDETERMINISTIC:
	// logic = "pctl";
	// break;
	// case ModulesFile.STOCHASTIC:
	// logic = "csl";
	// break;
	// case ModulesFile.PROBABILISTIC:
	// logic = "pctl";
	// break;
	// }
	// config.parameters.add(logic);
	// MRMCContext settings = new MRMCContext(id, config);
	// return settings;
	// }

	// private String getMRMCPrismModelFileName() {
	// String s = getModelFileName();
	// return s + ".mrmc" + s.substring(s.length() - 3);
	// }
	//
	// private String getMRMCPrismPropFileName() {
	// String s = propFileName + ".mrmc";
	// if (prismModel.type() == ModulesFile.STOCHASTIC) {
	// s = s + ".csl";
	// } else
	// s = s + ".pctl";
	// return s;
	// }
	//
	// private String getMRMCPropFileName() {
	// return propFileName + ".mrmc.txt";
	// }
	//
	// public String getSolutionFileName() {
	// return modelFileName + tStamp+".sol";
	// }
}
