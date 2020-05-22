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
import prism.PrismFileLog;
import dipro.stoch.CTMC;
import dipro.stoch.MarkovModel;
import dipro.stoch.UniformCTMC;
import dipro.stoch.prism.PrismExplicitModel;
import dipro.stoch.prism.PrismUntil;
import dipro.util.DiProException;


public class PrismExplicitContext extends AbstractPrismContext {


	protected String traFileName, staFileName, labFileName;
	protected int modelType;
	protected String dbName;
	
	protected PrismExplicitContext(int id, Config config) throws Exception {
		super(id, config);
		modelType = ModulesFile.PROBABILISTIC;
	}
	
	public String getDatabaseName() {
		return dbName;
	}
//	public void setDatabaseName(String dbName) {
//		this.dbName = dbName;
//	}
	
	@Override
	protected void loadModel() throws Exception {
		prismMainLog = new PrismFileLog(propFileName + ".prism.main.log");
		prismTechLog = new PrismFileLog(propFileName + ".prism.tech.log");
		prismModel = new PrismExplicitModel(this);
		graph = prismModel.createMarkovModel();
		property = PrismUntil.loadProperty(prismModel, propIndex);
		if(config.mc) performModelChecking();
		if(graph instanceof CTMC) {
			// When will uniformization be done?
			// If XBF is used and the usePi flag is set.
			if (config.algType==Config.XBF && config.usePi) {
				if(config.uniformRate < 0) performModelChecking();
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
			if (param.equals("-type")) {
				if (!iter.hasNext())
					throw new DiProException(
							"The option -type demands a model type (integer number)");
				else {
					String s = iter.next();
					try {
						modelType = Integer.parseInt(s);
						if(	modelType != ModulesFile.PROBABILISTIC && 
								modelType != ModulesFile.STOCHASTIC &&
								modelType != ModulesFile.NONDETERMINISTIC) {
							throw new DiProException("Invalid model type: " + modelType);
						}
						continue;
					} catch (NumberFormatException e) {
						throw new DiProException("Invalid model type: " + s);
					}
				}
			}
			if (dbName == null) {
				dbName = param;
				continue;
			}
			if (staFileName == null) {
				staFileName = config.getDiPro().makeAbsoluteFileName(param);
				continue;
			}
			if (traFileName == null) {
				traFileName = config.getDiPro().makeAbsoluteFileName(param);
				continue;
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
			if (constFileName == null) {
				constFileName = config.getDiPro().makeAbsoluteFileName(param);
				continue;
			}
		}
	}
	
//	public int getVarType(String varName) {
//		return varTypeMap.get(varName);
//	}
//	
//	public Set<String> getVarNames() {
//		return varTypeMap.keySet();
//	}

	@Override
	public String getSolutionFileName() throws Exception {
		return traFileName + ".sol." + tStamp;
	}

	public String getTraFileName() {
		return traFileName;
	}

	public String getStaFileName() {
		return staFileName;
	}

	public String getLabFileName() {
		return labFileName;
	}

	public int getModelType() {
		return modelType;
	}

}
