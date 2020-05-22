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

import java.util.Iterator;

import parser.ast.ModulesFile;
import prism.PrismCL;
import prism.PrismFileLog;
import dipro.stoch.CTMC;
import dipro.stoch.MarkovModel;
import dipro.stoch.UniformCTMC;
import dipro.stoch.prism.PrismDefaultModel;
import dipro.stoch.prism.PrismUntil;
import dipro.stoch.prism.UntilFalseProperty;
import dipro.util.DiProException;

public class PrismDefaultContext extends AbstractPrismContext {

	private PreparedDataFromPrism preparedDataFromPrism;
	protected String modelFileName;

	protected PrismDefaultContext(int id, Config config) throws Exception {
		super(id, config);
		// modelFileName = null;
		// //config.getDiPro().makeAbsoluteFileName(config.modelFileName);
		preparedDataFromPrism = null;
	}

	/**
	 * If DiPro runs as a plugin in PRISM model checker, then some data, such as
	 * the model and properties, will be parsed by PRISM. This method should be
	 * used in order to give DiPro access on this data without repeating the
	 * parsing work.
	 * 
	 * @param preparedData
	 *            contains the data which has already been prepared by PRISM
	 */
	public void takePreparedDataFromPrism(PreparedDataFromPrism preparedData) {
		preparedDataFromPrism = preparedData;
		prismMainLog = preparedData.getMainLog();
		prismTechLog = preparedData.getTechLog();
		modelFileName = config.getDiPro().makeAbsoluteFileName(
				preparedDataFromPrism.getModelFileName());
		propFileName = config.getDiPro().makeAbsoluteFileName(
				preparedDataFromPrism.getPropFileName());
	}

	@Override
	protected void loadModel() throws Exception {
		if (config.getPrismMainLog() == null)
			prismMainLog = new PrismFileLog(propFileName + ".prism.main.log");
		else
			prismMainLog = config.getPrismMainLog();
		if (config.getPrismTechLog() == null)
			prismTechLog = new PrismFileLog(propFileName + ".prism.tech.log");
		else
			prismTechLog = config.getPrismTechLog();
		prismModel = new PrismDefaultModel(this);
		graph = prismModel.createMarkovModel();
		if (getDiPro().isPlugin()){
			propIndex = config.getPropId();
			modelFileName = config.getModelName();
			propFileName = config.getPropName();
		
		}
		if (propIndex == -1) {
			property = new UntilFalseProperty();
		} else {
			property = PrismUntil.loadProperty(prismModel, propIndex);
		}
		if (config.mc)
			performModelChecking();
		if (graph instanceof CTMC) {
			/*
			 * Uniformisation will be done if XBF is used and the usePi flag is
			 * set.
			 */
			if (config.algType == Config.XBF && config.usePi) {
				// config.algType== Config.EPPSTEIN ||
				// config.algType== Config.K_STAR) {
				if (config.uniformRate < 0)
					performModelChecking();
				assert config.uniformRate >= 0.0d;
				graph = new UniformCTMC((CTMC) graph, config.uniformRate);
			}
		}
		start = ((MarkovModel) graph).getInitialState();
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
						propIndex = Integer.parseInt(s);
						continue;
					} catch (NumberFormatException e) {
						throw new DiProException("Invalid property index: " + s);
					}
				}
			}

			if (modelFileName != null && propFileName != null
					&& constFileName != null && propIndex >= 0) {
				break;
			}
			// if (modelFileName == null && config.modelFileName == null) {
			//
			// modelFileName = config.getDiPro().makeAbsoluteFileName(param);
			// continue;
			// }else{
			// modelFileName =
			// config.getDiPro().makeAbsoluteFileName(config.modelFileName);
			// }
			// if (propFileName == null && config.propFileName == null) {
			// propFileName = config.getDiPro().makeAbsoluteFileName(param);
			// continue;
			// }else{
			// propFileName =
			// config.getDiPro().makeAbsoluteFileName(config.propFileName);
			// }
			if (modelFileName == null) {
				// assert preparedDataFromPrism == null;
				modelFileName = config.getDiPro().makeAbsoluteFileName(param);
				continue;
			}
			if (propFileName == null) {
				// assert preparedDataFromPrism == null;
				propFileName = config.getDiPro().makeAbsoluteFileName(param);
				continue;
			}
			if (constFileName == null) {
				constFileName = config.getDiPro().makeAbsoluteFileName(param);
				continue;
			}
		}
	}

	public String getModelFileName() {
		return modelFileName;
	}

	public void convertToMRMC() throws Exception {
		String newMFName = getMRMCPrismModelFileName();
		String newPFName = getMRMCPrismPropFileName();
		String mrmcPropFName = getMRMCPropFileName();
		((PrismDefaultModel) prismModel).prepareForMRMCExport(newMFName,
				newPFName, mrmcPropFName);
		String labFileName = newMFName + ".lab";
		String traFileName = newMFName + ".tra";
//		StringBuilder command = new StringBuilder();
//		command.append("prism");
//		command.append(" -fixdl ");
//		// System.out.println(newMFName);
//		command.append(newMFName);
//		command.append(" ");
//		command.append(newPFName);
//		command.append(" -exportmrmc");
//		command.append(" -exportlabels ");
//		command.append(labFileName);
//		command.append(" -exporttrans ");
//		command.append(traFileName);
//
//		Runtime builder = Runtime.getRuntime();
//		Process process = null;
//		try {
//			process = builder.exec(command.toString());
//			process.waitFor();
//		} catch (IOException e) {
//			System.err.println("IOException" + e.getMessage());
//		}
//		InputStream in = process.getInputStream();
//		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//		String line = null;
//		line = reader.readLine();
//		while (line != null) {
//			Registry.getMain().out.println("line " + line);
//			line = reader.readLine();
//		}
		String[] args = {newMFName,newPFName,"-fixdl","-exportmrmc","-exportlabels",labFileName,"-exporttrans",traFileName};
		PrismCL prismCL = new PrismCL();
		prismCL.run(args);
		
		config.parameters.add(traFileName);
		config.parameters.add(labFileName);
		config.parameters.add(mrmcPropFName);
			
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
	}

	private String getMRMCPrismModelFileName() {
		String s = getModelFileName();
		return s + ".mrmc" + s.substring(s.length() - 3);
	}

	private String getMRMCPrismPropFileName() {
		String s = propFileName + ".mrmc";
		if (prismModel.type() == ModulesFile.STOCHASTIC) {
			s = s + ".csl";
		} else
			s = s + ".pctl";
		return s;
	}

	private String getMRMCPropFileName() {
		return propFileName + ".mrmc.txt";
	}

	public String getSolutionFileName() {
		return modelFileName + ".sol";
	}
}
