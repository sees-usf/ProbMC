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

import java.util.ArrayList;
import java.util.HashMap;

import parser.ast.ModulesFile;
import parser.ast.PropertiesFile;
import prism.PrismLog;
import dipro.stoch.CTMC;
import dipro.stoch.DTMC;
import dipro.stoch.MDP;
//import dipro.util.DiagnosticPath;
import dipro.util.DiProException;
import dipro.util.DiagnosticPath;

/**
 * 
 *
 */

public interface CXGenerator {

	
	/**
	 * Loads a ModulesFile object that shall then be used for the exploration of the state space and generation of counterexample(s), if any.
	 * <p>
	 * @param modelFile the model to be explored
	 * @return <li> if the model is an instance of {@link CTMC} then {@link Config.XBF},{@link Config.KSTAR} and {@link Config.EPPSTEIN}
	 * 		   <li> if the model is an instance {@link DTMC} then {@link Config.KSTAR} and {@link Config.EPPSTEIN}
	 * 		   <li> if the model is  an instance {@link MDP} then  {@link Config.KSTAR} and {@link Config.EPPSTEIN}
	 * @throws DiProException if the model is unsupported.
	 */
	public ArrayList<Integer> loadModel(ModulesFile modelFile) throws DiProException;
	
	
	/**
	 * Loads a PropertyFile object that shall then be used for the verification against the previously loaded model, thus generation of model. 
	 * <p>
	 * @param propFile a set of properties or only one property
	 * @return a list with the id's of supported property
	 * @throws DiProException no property is supported or the property does not corresponds to the loaded model.
	 */
	public ArrayList<Integer> loadProp(PropertiesFile propFile) throws UnsupportedPropertyException;
	
	/**
	 * Parse the undefined Constants for the model and/or the property, if any.
	 * <p>
	 * @param stringSet set of string containing the constant and its value. For example "N=200,T=0.2"
	 * @throws DiProException if there are still undefined Constant(s).
	 */
	public void defineUnDefConst(HashMap<String, String> k) throws DiProException;
	
//	/**
//	 * Sets the search algorithm in the state space. If the model to be explored is instance of:
//	 * <li>{@link CTMC}, then {@link Config.XBF}, {@link Config.KSTAR} and {@link Config.EPPSTEIN} are supported.
//	 * <li>{@link DTMC}, then {@link Config.KSTAR} and {@link Config.EPPSTEIN} are supported.
//	 * <li>{@link MDP}, {@link Config.XBF},{@link Config.KSTAR} and {@link Config.EPPSTEIN} are supported.
//	 * <p> 
//	 * 
//	 * @param algorithmType Type from {@link Config}. For example "Config.XBF"
//	 * @throws DiProException if the chosen algorithm does not supports the loaded model.
//	 */
//	public void setAlgorithm(int algorithmType) throws DiProException;
	
	/**
	 * Generates a set of counterexample with traces, if any, based on the chosen property with this propId from the set of supported properties.
	 * @param propId id of the supported properties
	 * @return ArrayList<DiagnosticPath> containing the trace
	 * @throws Exception 
	 */
	public ArrayList<DiagnosticPath> generateCX(int propId) throws DiProException;
//	public void generateCX(int propId) throws DiProException;
	
	/**
	 * Initializates Config object that shall then be used to set the different available options/context for both the state space exploration and the generation of a counterexample(s),if any. 
	 * @return Config containing the context for this experiment
	 */
	public Config initConfig();
	
	
}
