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

package dipro.graph;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class BackwardsDirectedGraph implements DirectedGraph {

	private ExternDirectedGraph graph;

	public BackwardsDirectedGraph(ExternDirectedGraph graph) {
		this.graph = graph;
	}

	@Override
	public Iterator<WeightedDirectedEdge> incomingEdges(Vertex v) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<WeightedDirectedEdge> outgoingEdges(Vertex v) {
		System.out.println("outgoingEdges - backwardsDirectedGraph");
		Iterator<? extends DirectedEdge> iter = graph.incomingEdges(v);
		LinkedList<WeightedDirectedEdge> edges = new LinkedList<WeightedDirectedEdge>();
		while (iter.hasNext()) {
			WeightedDirectedEdge e = (WeightedDirectedEdge) iter.next();
			WeightedDirectedEdge be = new WeightedDirectedEdge(e.target(), e
					.source(), e.getWeight());
			edges.add(be);
		}
		return edges.iterator();
	}

	@Override
	public Iterator<WeightedDirectedEdge> adjacentEdges(Vertex v) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		graph.clear();
	}

	@Override
	public int degree(Vertex v) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int edgeSize() {
		return graph.edgeSize();
	}

	@Override
	public Iterator<WeightedDirectedEdge> edges() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int numEdges() {
		return graph.numEdges();
	}

	@Override
	public int numVertices() {
		return graph.numVertices();
	}

	@Override
	public int vertexSize() {
		return graph.vertexSize();
	}

	@Override
	public Iterator<Vertex> vertices() {
		throw new UnsupportedOperationException();
	}

	@Override
	public float weight(Edge e) {
		return ((WeightedDirectedEdge) e).getWeight();
	}

	@Override
	public List<String> getVertexLabels() {
		return graph.getVertexLabels();
	}

	public Class getVertexLabelType(String label) {
		return graph.getVertexLabelType(label);
	}
}
