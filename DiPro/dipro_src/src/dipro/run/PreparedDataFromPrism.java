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


import parser.Values;
import parser.ast.ModulesFile;
import parser.ast.PropertiesFile;
import prism.PrismLog;

public class PreparedDataFromPrism {
	/* Parsed info from Prism */
	private String modelFileName;
	private String propFileName;
	private prism.Model parsedModel;
	private ModulesFile parsedModulesFile;
	private PropertiesFile parsedPropertiesFile;
	private Values parsedConstantValues;
	private Object modelCheckResult;

	private PrismLog mainLog, techLog;

	public PreparedDataFromPrism(String modelFileName, String propFileName, 
			prism.Model parsedModel,
			ModulesFile parsedModulesFile, PropertiesFile parsedPropertiesFile,
			Values parsedConstantValues, Object modelCheckResult,
			PrismLog mainLog, PrismLog techLog) {
		super();
		this.modelFileName = modelFileName;
		this.propFileName = propFileName;
		this.parsedModel = parsedModel;
		this.parsedModulesFile = parsedModulesFile;
		this.parsedPropertiesFile = parsedPropertiesFile;
		this.parsedConstantValues = parsedConstantValues;
		this.modelCheckResult = modelCheckResult;
		this.mainLog = mainLog;
		this.techLog = techLog;
	}

	public String getModelFileName() {
		return modelFileName;
	}

	public String getPropFileName() {
		return propFileName;
	}
	
	public prism.Model getParsedModel() {
		return parsedModel;
	}

	public ModulesFile getParsedModulesFile() {
		return parsedModulesFile;
	}

	public PropertiesFile getParsedPropertiesFile() {
		return parsedPropertiesFile;
	}

	public Values getParsedConstantValues() {
		return parsedConstantValues;
	}

	public Object getModelCheckResult() {
		return modelCheckResult;
	}

	public PrismLog getMainLog() {
		return mainLog;
	}

	public PrismLog getTechLog() {
		return techLog;
	}
}
