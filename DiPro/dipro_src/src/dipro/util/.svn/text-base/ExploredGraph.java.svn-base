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

package dipro.util;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import dipro.graph.DirectedEdge;
import dipro.graph.DirectedGraph;
import dipro.graph.Edge;
import dipro.graph.Vertex;

public class ExploredGraph implements DirectedGraph {
	
	private class BaseEntry {
		Vertex vertex; 
		HashSet<DirectedEdge> out;
		HashSet<DirectedEdge> in;
		private BaseEntry(Vertex vertex) {
			this.vertex = vertex;
			out = new HashSet<DirectedEdge>();
			in = new HashSet<DirectedEdge>();
		}
		private void addOutgoingEdge(DirectedEdge e) {
			assert e.source().equals(vertex);
			out.add(e);
		}
		private void addIncomingEdge(DirectedEdge e) {
			assert e.target().equals(vertex);
			in.add(e);
		}
	}
	
	private Hashtable<Vertex, BaseEntry> base;
	private int numEdges;
	
	public ExploredGraph() {
		base = new Hashtable<Vertex, BaseEntry>();
		numEdges = 0;
	}
	
	public void addVertex(Vertex v) {
		BaseEntry vEntry = base.get(v);
		if(vEntry==null) {
			vEntry = new BaseEntry(v);
			base.put(v, vEntry);
		}
	}
	
	public void addEdge(DirectedEdge uv) {
		assert uv != null;
		Vertex u = uv.source();
		Vertex v = uv.target();
		BaseEntry uEntry = base.get(u);
		if(uEntry==null) {
			uEntry = new BaseEntry(u);
			base.put(u, uEntry);
		}
		BaseEntry vEntry = base.get(v);
		if(vEntry==null) {
			vEntry = new BaseEntry(v);
			base.put(v, vEntry);
		}
		uEntry.addOutgoingEdge(uv);
		vEntry.addIncomingEdge(uv);
		numEdges++;
	}
	
	public void removeEdge(DirectedEdge uv) {
		assert uv != null;
		Vertex u = uv.source();
		Vertex v = uv.target();
		BaseEntry uEntry = base.get(u);
		if(uEntry==null) {
			/* In this case, the uv is not contained in 
			 * the graph. 
			 */
			return;
		}
		boolean b = uEntry.out.remove(uv);
		if(b) {
			/* If uv existed in the outgoing list of u, then 
			 * it must also exist in the incoming list of v. */
			BaseEntry vEntry = base.get(v);
			assert vEntry != null;
			b = vEntry.in.remove(uv);
			assert b;
		}
		numEdges--;
	}
	
	@Override
	public Iterator<? extends DirectedEdge> adjacentEdges(final Vertex v) {
		return new Iterator<DirectedEdge>() {
			Iterator<? extends DirectedEdge> iterIn = incomingEdges(v); 
			Iterator<? extends DirectedEdge> iterOut = outgoingEdges(v);
			@Override
			public boolean hasNext() {
				return iterIn.hasNext()|| iterOut.hasNext();
			}
			@Override
			public DirectedEdge next() {
				if(iterIn.hasNext()) return (DirectedEdge)iterIn.next();
				else return (DirectedEdge)iterOut.next();
			}
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			} 
		};
	}

	@Override
	public Iterator<? extends DirectedEdge> edges() {
			return new Iterator<DirectedEdge>() {
				Iterator<? extends Vertex> vIter = vertices(); 
				Iterator<? extends DirectedEdge> eIter;
				@Override
				public boolean hasNext() {
					while(eIter==null || !eIter.hasNext()) {
						if(!vIter.hasNext()) return false;
						Vertex v = vIter.next();
						eIter = outgoingEdges(v);
					}
					assert eIter!=null && eIter.hasNext();
					return true;
				}
				@Override
				public DirectedEdge next() {
					return (DirectedEdge)eIter.next();
				}
				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				} 
			};
	}

	@Override
	public Iterator<? extends DirectedEdge> incomingEdges(Vertex v) {
		if(!base.containsKey(v)) return null;
		return base.get(v).in.iterator();
	}

	@Override
	public Iterator<? extends DirectedEdge> outgoingEdges(Vertex v) {
		if(!base.containsKey(v)) return null;
		return base.get(v).out.iterator();
	}

	@Override
	public void clear() throws Exception {
		base.clear();
		numEdges = 0;
	}

	@Override
	public int degree(Vertex v) throws Exception {
		if(!base.containsKey(v)) throw new DiProException("Unknown vertex "+v);
		return base.get(v).out.size();
	}

	@Override
	public int numEdges() {
		return numEdges;
	}

	@Override
	public int numVertices() {
		return base.size();
	}

	@Override
	public Iterator<? extends Vertex> vertices() {
		return base.keySet().iterator();
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
	public float weight(Edge e) throws Exception {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int vertexSize() throws Exception {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int edgeSize() throws Exception {
		throw new UnsupportedOperationException();
	}
}
