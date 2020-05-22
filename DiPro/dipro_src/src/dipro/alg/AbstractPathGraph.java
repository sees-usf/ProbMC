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

package dipro.alg;

import java.util.Iterator;
import java.util.List;

import dipro.graph.DirectedEdge;
import dipro.graph.DirectedGraph;
import dipro.graph.Vertex;

public abstract class AbstractPathGraph implements DirectedGraph {

	protected KSPAlgorithm kSearch;
	
	public AbstractPathGraph(KSPAlgorithm kSearch) {
		this.kSearch = kSearch;
	}
	
	@Override
	public Iterator<? extends DirectedEdge> adjacentEdges(Vertex v) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<? extends DirectedEdge> edges() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<? extends DirectedEdge> incomingEdges(Vertex v) {
		throw new UnsupportedOperationException();
	}


	@Override
	public void clear() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public int degree(Vertex v) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public int edgeSize() throws Exception {
		return 0;
	}

	@Override
	public Class getVertexLabelType(String label) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<String> getVertexLabels() throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public int numEdges() throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public int numVertices() throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<? extends Vertex> vertices() throws Exception {
		throw new UnsupportedOperationException();
	}

}
