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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class AdjacenceHashGraph implements DirectedGraph {

	protected Hashtable<Vertex, BaseEntry> base;
	protected int numEdges;

	public AdjacenceHashGraph() {
		base = new Hashtable<Vertex, BaseEntry>();
		numEdges = 0;
	}

	public void addVertex(Vertex v) {
		if (contains(v))
			return;
		assert base.get(v) == null;
		BaseEntry entry = new BaseEntry();
		entry.vertex = v;
		entry.outgoingEdges = new HashSet<DirectedEdge>();
		base.put(v, entry);
	}

	public void addEdge(DirectedEdge uv) {
		if (contains(uv))
			return;
		Vertex u = uv.source();
		Vertex v = uv.target();
		if (!contains(u))
			addVertex(u);
		if (!contains(v))
			addVertex(v);
		base.get(u).outgoingEdges.add(uv);
		numEdges++;
	}

	public boolean contains(GraphItem item) {
		if(item instanceof Vertex) return contains((Vertex)item);
		else {
			assert item instanceof DirectedEdge;
			return contains((DirectedEdge)item);
		}
	}
	
	public boolean contains(Vertex v) {
		boolean b = base.containsKey(v);
		return b;
	}

	public boolean contains(DirectedEdge e) {
		Vertex u = e.source();
		BaseEntry uEntry = base.get(u);
		if(uEntry == null) return false;
		return uEntry.outgoingEdges.contains(e);
	}

	public Iterator<DirectedEdge> edges() {
		return new Iterator<DirectedEdge>() {
			Iterator<? extends Vertex> vIter = vertices();
			Iterator<DirectedEdge> eIter = null;

			public boolean hasNext() {
				while ((eIter == null || !eIter.hasNext()) && vIter.hasNext()) {
					Vertex v = vIter.next();
					BaseEntry vEntry = base.get(v);
					assert vEntry != null;
					eIter = vEntry.outgoingEdges.iterator();
				}
				return (eIter != null && eIter.hasNext());
			}

			public DirectedEdge next() {
				if (hasNext()) {
					return eIter.next();
				} else
					return null;
			}

			public void remove() {
				throw new UnsupportedOperationException("remove");
			}
		};
	}

	public int numVertices() {
		return base.size();
	}

	public Iterator<Vertex> vertices() {
		return base.keySet().iterator();
	}

	public int numEdges() {
		return numEdges;
	}

	public Iterator<DirectedEdge> incomingEdges(Vertex v) {
		LinkedList<DirectedEdge> list = new LinkedList<DirectedEdge>();
		for (Vertex u : base.keySet()) {
			BaseEntry uEntry = base.get(u);
			assert u!= null;
			Iterator<DirectedEdge> iter = uEntry.outgoingEdges.iterator();
			while (iter.hasNext()) {
				DirectedEdge e = iter.next();
				if (e.target().equals(v))
					list.add(e);
			}
		}
		return list.iterator();
	}

	@SuppressWarnings("unchecked")
	public Iterator<DirectedEdge> outgoingEdges(final Vertex v) {
		System.out.println("outgoingEdges - AdjacenceHashGraph");
		if (!contains(v))
			return Collections.EMPTY_LIST.iterator();
		return base.get(v).outgoingEdges.iterator();
	}

	public Iterator<DirectedEdge> connectingEdges(Vertex v1, Vertex v2) {
		LinkedList<DirectedEdge> list = new LinkedList<DirectedEdge>();
		Iterator<DirectedEdge> l = base.get(v1).outgoingEdges.iterator();
		while (l.hasNext()) {
			DirectedEdge e = l.next();
			if (e.target().equals(v1))
				list.add(e);
		}
		return list.iterator();
	}

	public int degree(Vertex v) {
		throw new UnsupportedOperationException();
	}

	// public Vertex getOwnVertex(Vertex v) {
	// for(Vertex u:base.keySet()) {
	// if(u.equals(v)) return u;
	// }
	// return null;
	// }

	public Iterator<DirectedEdge> adjacentEdges(final Vertex v) {
		return new Iterator<DirectedEdge>() {
			Iterator<DirectedEdge> iter1 = outgoingEdges(v);
			Iterator<DirectedEdge> iter2 = incomingEdges(v);

			public boolean hasNext() {
				return iter1.hasNext() || iter2.hasNext();
			}

			public DirectedEdge next() {
				if (iter1.hasNext())
					return iter1.next();
				return iter2.next();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	// public float weight(Edge e) {
	// assert e instanceof DirectedEdge;
	// if()
	// Vertex u = ((DirectedEdge)e).source();
	// Hashtable<DirectedEdge> l = base.get(u);
	// Float w = l.get(e);
	// assert w!=null;
	// return w.floatValue();
	// }

	public int edgeSize() {
		return 4 + 4;
	}

	public int vertexSize() {
		return 4;
	}

	@Override
	public void clear() {
		base.clear();
		numEdges = 0;
	}

	@Override
	public List<String> getVertexLabels() {
		ArrayList<String> l = new ArrayList<String>(1);
		l.add(DefaultVertex.VERTEX_ID_LABEL_NAME);
		return l;
	}

	public Class getVertexLabelType(String label) {
		if (label.equals(DefaultVertex.VERTEX_ID_LABEL_NAME))
			return Integer.class;
		throw new IllegalArgumentException("Invalid vertex label: " + label);
	}

	@Override
	public float weight(Edge e) {
		if (e instanceof WeightedDirectedEdge) {
			return ((WeightedDirectedEdge) e).getWeight();
		}
		return 1.0f;
	}
	
	public void loadFromFile(String fileName) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(fileName));
		String line = in.readLine();
		while (line != null) {
			String[] tokens = line.split(" ");
			int i = Integer.parseInt(tokens[0]);
			int j = Integer.parseInt(tokens[1]);
			int w = Integer.parseInt(tokens[2]);
			Vertex u = new DefaultVertex(i);
			BaseEntry uEntry = base.get(u);
			if(uEntry != null) {
				assert uEntry.vertex.equals(u);
				u = uEntry.vertex; 
			} 
			else {
				addVertex(u);
				uEntry = base.get(u);
				assert u==uEntry.vertex;
				uEntry.outgoingEdges = new HashSet<DirectedEdge>();
			}
			Vertex v = new DefaultVertex(j);
			BaseEntry vEntry = base.get(v);
			if(vEntry != null) {
				assert vEntry.vertex.equals(v);
				v = vEntry.vertex; 
			} 
			else {
				addVertex(v);
			}
			WeightedDirectedEdge uv = new WeightedDirectedEdge(u, v, w);
			if(uEntry.outgoingEdges==null) {
				uEntry.outgoingEdges = new HashSet<DirectedEdge>();
			}
			uEntry.outgoingEdges.add(uv);
			line = in.readLine();
		}
		in.close();
	}
	
	class BaseEntry {
		Vertex vertex; 
		HashSet<DirectedEdge> outgoingEdges; 
	}
}
