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

package dipro.stoch.prism;

import java.io.IOException;
import java.util.Collection;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import parser.ParseException;
import parser.Values;
import parser.ast.ModulesFile;
import parser.ast.PropertiesFile;
import prism.Prism;
import prism.PrismException;
//import simulator.SimulatorException;
import dipro.graph.Edge;
import dipro.graph.OnTheFlyStateSpace;
import dipro.graph.State;
import dipro.graph.Transition;
import dipro.graph.Vertex;
import dipro.run.AbstractPrismContext;
import dipro.run.PreparedDataFromPrism;
import dipro.run.UnsupportedPropertyException;
import dipro.stoch.CTMC;
import dipro.stoch.DTMC;
import dipro.stoch.MDP;
import dipro.stoch.MarkovModel;
import dipro.stoch.prism.PrismRawModel;

public class PrismDefaultModel extends OnTheFlyStateSpace implements PrismModel {

	protected AbstractPrismContext context;

	protected PrismRawModel rawModel;

	protected PrismState initState;

	//Goes to other constructor
	public PrismDefaultModel(AbstractPrismContext context)
			throws InvalidPropertiesFormatException, PrismException,
			ParseException, IOException,
			UnsupportedPropertyException {
		this(context, null);
	}

	//Constructor
	public PrismDefaultModel(AbstractPrismContext context, PreparedDataFromPrism preparedData)
	throws InvalidPropertiesFormatException, PrismException,
	ParseException, IOException, UnsupportedPropertyException {
		this.context = context;
		rawModel = new PrismRawModel(context, preparedData);
		initState = new PrismState(rawModel.initValues());
	}
	
	public State getInitialState() {
		return initState;
	}

	public boolean isInitialState(State s) {
		return initState.equals(s);
	}

	PrismRawModel getRawModel() {
		return rawModel;
	}

	/* (non-Javadoc)
	 * @see dipro.model.stoch.prism.PrismModelInterface#modulesFile()
	 */
	public ModulesFile modulesFile() {
		return rawModel.modulesFile();
	}

	/* (non-Javadoc)
	 * @see dipro.model.stoch.prism.PrismModelInterface#propertiesFile()
	 */
	public PropertiesFile propertiesFile() {
		System.out.println("Properties FIle - Prism Default");
		return rawModel.propertiesFile();
	}

	/* (non-Javadoc)
	 * @see dipro.model.stoch.prism.PrismModelInterface#externalConstantValues()
	 */
	public Properties externalConstantValues() {
		return rawModel.externalConstantValues();
	}

	/* (non-Javadoc)
	 * @see dipro.model.stoch.prism.PrismModelInterface#constantValues()
	 */
	public Values constantValues() {
		return rawModel.constantValues();
	}

	/* (non-Javadoc)
	 * @see dipro.model.stoch.prism.PrismModelInterface#type()
	 */
	public int type() {
		return rawModel.type();
	}

	/* (non-Javadoc)
	 * @see dipro.model.stoch.prism.PrismModelInterface#createMarkovModel()
	 */
	public MarkovModel createMarkovModel() {
		System.out.println("Made CTMC for now -  createMarkovModel - PrismDefaultModel");
		/*switch (rawModel.type()) {
		case ModulesFile.modelType.PROBABILISTIC:
			return new DTMC(this);
		case ModulesFile.STOCHASTIC:
			return new CTMC(this);
		case ModulesFile.NONDETERMINISTIC:
			return new MDP(this);
		default:
			throw new IllegalStateException("Unsupported model type: "
					+ rawModel.type());
		}*/
		return new CTMC(this);
	}

	public Class getVertexLabelType(String label) throws SimulatorException {
		return rawModel.getVariableType(label);
	}

	public List<String> getVertexLabels() {
		return rawModel.getVariablesNames();
	}

	public float weight(Edge e) {
		assert e instanceof PrismTransition;
		return ((PrismTransition) e).getProbOrRate();
	}

	public Iterator<PrismTransition> adjacentEdges(Vertex v) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Transition createTransition(State s1, State s2) {
		return new PrismTransition((PrismState) s1, (PrismState) s2);
	}

	public int edgeSize() {
		// 2 References (integer: 4) + one real (float: 4)
		return 2 * 4 + 4;
	}

	public int vertexSize() {
		return rawModel.getStateSize();
	}

	public String toString() {
		return rawModel.toString();
	}

	public void prepareForMRMCExport(	String mrmcPrismFileName, 
				String mrmcPrismProbFileName, String mrmcPropFileName) throws IOException,
			UnsupportedPropertyException, PrismException {
		rawModel.prepareForMRMCExport(mrmcPrismFileName,
				mrmcPrismProbFileName, mrmcPropFileName);
	}


	@Override
	public Collection<PrismTransition> generateOutgoingTransitions(State s) {
		return rawModel.generateOutgoingTransitions((PrismState) s);
	}

	@Override
	public void clear() {
		System.out.println("clear - PrismDefaultModel");
		initState = null;
		rawModel.clear();
		System.gc();
	}

	/* (non-Javadoc)
	 * @see dipro.model.stoch.prism.PrismModelInterface#getPrism()
	 */
	public Prism getPrism() {
		return rawModel.prism;
	}
}
