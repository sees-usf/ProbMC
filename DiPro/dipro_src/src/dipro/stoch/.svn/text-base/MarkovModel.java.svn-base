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

package dipro.stoch;

import java.util.Iterator;
import java.util.List;

import dipro.graph.Edge;
import dipro.graph.State;
import dipro.graph.StateSpace;
import dipro.graph.Transition;
import dipro.graph.Vertex;

public class MarkovModel implements StateSpace {

	protected StateSpace rawProbModel;

	public MarkovModel(StateSpace raw) {
		rawProbModel = raw;
	}

	@SuppressWarnings("unchecked")
	public Iterator<? extends StochasticTransition> edges() {
		return (Iterator<? extends StochasticTransition>) rawProbModel.edges();
	}

	public boolean equals(Object obj) {
		return rawProbModel.equals(obj);
	}

	public List<String> getVertexLabels() throws Exception {
		return rawProbModel.getVertexLabels();
	}

	public Class getVertexLabelType(String label) throws Exception {
		return rawProbModel.getVertexLabelType(label);
	}

	public int hashCode() {
		return rawProbModel.hashCode();
	}

	public int numEdges() throws Exception {
		return rawProbModel.numEdges();
	}

	public int numVertices() throws Exception {
		return rawProbModel.numVertices();
	}

	public String toString() {
		return rawProbModel.toString();
	}

	@SuppressWarnings("unchecked")
	public Iterator<? extends State> vertices() throws Exception {
		return rawProbModel.vertices();
	}

	public StateSpace getRawProbModel() {
		return rawProbModel;
	}

	public float weight(Edge e) {
		assert e instanceof StochasticTransition;
		return ((StochasticTransition) e).getProbOrRate();
	}

	@SuppressWarnings("unchecked")
	public Iterator<? extends StochasticTransition> adjacentEdges(Vertex v) {
		return (Iterator<? extends StochasticTransition>) rawProbModel
				.adjacentEdges(v);
	}

	public Transition createTransition(State s1, State s2) {
		return rawProbModel.createTransition(s1, s2);
	}

	public int degree(Vertex v) throws Exception {
		return rawProbModel.degree(v);
	}

	public State getInitialState() {
		return rawProbModel.getInitialState();
	}

	@SuppressWarnings("unchecked")
	public Iterator<? extends StochasticTransition> incomingEdges(Vertex v) {
		return (Iterator<? extends StochasticTransition>) rawProbModel
				.incomingEdges(v);
	}

	public boolean isInitialState(State s) {
		return rawProbModel.isInitialState(s);
	}

	@SuppressWarnings("unchecked")
	public Iterator<? extends StochasticTransition> outgoingEdges(Vertex v) {
		return (Iterator<? extends StochasticTransition>) rawProbModel
				.outgoingEdges(v);
	}

	public int edgeSize() throws Exception {
		return rawProbModel.edgeSize();
	}

	public int vertexSize() throws Exception {
		return rawProbModel.vertexSize();
	}

	public void clear() throws Exception {
		rawProbModel.clear();
	}

}
