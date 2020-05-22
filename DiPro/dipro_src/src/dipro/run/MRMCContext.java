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

import java.io.IOException;
import java.util.Iterator;

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
import dipro.stoch.mrmc.MRMCModel;
import dipro.stoch.mrmc.MRMCModelChecker;
import dipro.stoch.mrmc.MRMCUntil;
import dipro.util.DiProException;
import dipro.util.KDefaultSolutionCollector;
import dipro.util.SolutionCollector;

public class MRMCContext extends GraphContext {

	public static final String CSL = "csl";

	public static final String PCTL = "pctl";

	private String traFileName;

	private String labFileName;

	private String propFileName;

	private int probIndex;

	private String logic;

	private Object modelCheckingResult;

	protected MRMCContext(int id, Config config) throws Exception {
		super(id, config);
	}

	@Override
	protected void loadModel() throws IOException {
		MRMCModel model = new MRMCModel(this);
		graph = model.createMarkovModel();
		// if (graph instanceof CTMC) {
		// config.uniformRate = model.getMaxOutRate() + 0.001f;
		// graph = new UniformCTMC((CTMC) graph, config.uniformRate);
		// }
		start = ((MarkovModel) graph).getInitialState();
		// System.out.println(propFileName + probIndex);
		property = MRMCUntil.loadProperty(propFileName, probIndex);
		// System.out.println("Formula is : "
		// + ((MRMCUntil) property).toString());
		// System.out.println("Logic is : " + logic);
	}

	@Override
	public BF loadAlgorithm() throws Exception {
		BF alg;
		switch (config.algType) {
		case Config.XBF:
			if (property instanceof StochTBoundedUntil) {
				if (config.usePi)
					alg = new PiXSearchCreator().createSearch(this);
				else {
					assert graph.getClass() != UniformCTMC.class;
					if (graph.getClass() == CTMC.class) {
						alg = new ProbXSearchCreator().createSearch(this);
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
			alg = new ProbKStarCreator().createSearch(this);
			break;
		case Config.EPPSTEIN:
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
		switch (config.algType) {
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

	@Override
	protected void readParameters() throws DiProException {
		Iterator<String> iter = config.parameters.iterator();
		while (iter.hasNext()) {
			String param = iter.next();
			if (param.equals("-prop")) {
				if (!iter.hasNext())
					throw new DiProException(
							"The option -prop demands a property index (integer number)");
				else {
					String s = iter.next();
					try {
						probIndex = Integer.parseInt(s);
						continue;
					} catch (NumberFormatException e) {
						throw new DiProException("Invalid property index: " + s);
					}
				}
			}
			if (param.equals(CSL)) {
				logic = CSL;
				continue;
			}
			if (param.equals(PCTL)) {
				logic = PCTL;
				continue;
			}
			if (traFileName != null && labFileName != null
					&& propFileName != null && probIndex >= 0) {
				break;
			}
			if (traFileName == null) {
				traFileName = config.getDiPro().makeAbsoluteFileName(param);
				continue;
			}
			if (labFileName == null) {
				labFileName = config.getDiPro().makeAbsoluteFileName(param);
				continue;
			}
			if (propFileName == null) {
				propFileName = config.getDiPro().makeAbsoluteFileName(param);
				continue;
			}
		}
	}

	public String getTraFileName() {
		return traFileName;
	}

	public String getLabFileName() {
		return labFileName;
	}

	public String getLogic() {
		return logic;
	}

	public String getSolutionFileName() {
		return labFileName + tStamp + ".sol";
	}

	@Override
	public void performModelChecking() throws Exception {
		MRMCModelChecker modelChecker = new MRMCModelChecker();
		modelChecker.performModelChecking(this);
		modelCheckingResult = modelChecker.getResultProbVector();
	}

	@Override
	public Object getModelCheckingResult() throws Exception {
		return modelCheckingResult;
	}
}
