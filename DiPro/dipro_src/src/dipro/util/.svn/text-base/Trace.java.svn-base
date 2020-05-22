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
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import dipro.graph.DirectedEdge;
import dipro.graph.Vertex;

public class Trace extends Decoration {
	protected Vertex first;
	protected Vector<DirectedEdge> edges;
	private double solutionValue;

	public Trace() {
		this(null);
	}

	public Trace(Vertex v) {
		first = v;
		edges = new Vector<DirectedEdge>();
	}

	public void append(DirectedEdge e) {
		edges.add(edges.size(), e);
		if (edges.size() == 1)
			first = e.source();
	}

	public void preAppend(DirectedEdge e) {
		edges.add(0, e);
		first = e.source();
	}

	public Vertex getVertex(int i) {
		Vertex v = null;
		if (i == 0) {
			v = first;
		} else {
			DirectedEdge e = getEdge(i - 1);
			v = e.target();
		}
		return v;
	}

	public DirectedEdge getEdge(int i) {
		return edges.get(i);
	}

	
	public double getSolutionValue()
	{
		return this.solutionValue;
	}
	
	public void setSolutionValue(double solutionvalue)
	{
		this.solutionValue = solutionvalue;
	}
	/** The length of a trace which is defined as the number 
	 * of vertices. Note that a trace <code>t</code> 
	 * has got <code>(t.length() - 1) </code> edges. 
	 * @return the number of vertices in the trace.
	 */
	public int length() {
		if (first == null) {
			assert edges.isEmpty();
			return 0;
		}
		return edges.size() + 1;
	}

	// public Object clone() {
	// Trace clone = new Trace(first);
	// clone.edges = (Vector<DirectedEdge>)edges.clone();
	// return clone;
	// }

	public Vertex getFirstVertex() {
		return getVertex(0);
	}

	public Set<Vertex> getVertexSet() {
		HashSet<Vertex> set = new HashSet<Vertex>();
		Iterator<Vertex> iter = getVertices();
		while(iter.hasNext()) {
			set.add(iter.next());
		}
		return set;
	}
	
	public Set<DirectedEdge> getEdgeSet() {
		HashSet<DirectedEdge> set = new HashSet<DirectedEdge>();
		Iterator<DirectedEdge> iter = getEdges();
		while(iter.hasNext()) {
			set.add(iter.next());
		}
		return set;
	}
	
	public Iterator<DirectedEdge> getEdges() {
		return edges.iterator();
	}

	public Iterator<Vertex> getVertices() {
		return new Iterator<Vertex>() {
			int index = 0;

			public boolean hasNext() {
				return index < length();
			}

			public Vertex next() {
				return getVertex(index++);
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public boolean contains(Vertex vertex) {
		Iterator<Vertex> iter = getVertices();
		while (iter.hasNext()) {
			Vertex v = iter.next();
			if (v.equals(vertex))
				return true;
		}
		return false;
	}

	public boolean contains(DirectedEdge edge) {
		Iterator<DirectedEdge> iter = getEdges();
		while (iter.hasNext()) {
			DirectedEdge e = iter.next();
			if (e.equals(edge))
				return true;
		}
		return false;
	}

	/**
	 * Checks wether this trace goes through a given state or not.
	 * 
	 * @param s
	 *            the state to check
	 * @return the index of the state inside the trace iff the trace goes
	 *         through s and -1 otherwise.
	 */
	public int touches(Vertex v) {
		for (int i = 0; i < length(); i++) {
			Vertex vi = getVertex(i);
			if (v.equals(vi))
				return i;
		}
		return -1;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		// Iterator<Vertex> iter = getVertices();
		// int i=0;
		// while(iter.hasNext()) {
		// if(i>0) {
		// sb.append("->");
		// // sb.append(getEdge(i-1));
		// }
		// sb.append(iter.next());
		// i++;
		// }
		sb.append("\n");
		for (int i = 0; i < length() - 1; i++) {
			DirectedEdge e = getEdge(i);
			sb.append(e.source());
			sb.append("\n  --");
			sb.append(e);
			sb.append("-->\n");
			if (i == length() - 2) {
				sb.append(e.target());
			}
		}
		return sb.toString();
	}

	public String toBoundedString(int length) {
		String s = toString();
		if (s.length() <= length)
			return s;
		int partLength = (length - 3) / 2;
		StringBuilder sb = new StringBuilder(s.substring(0, partLength));
		sb.append("...");
		sb.append(s.substring(s.length() - partLength));
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		for (int i = 0; i < length(); i++) {
			result = PRIME * result + getVertex(i).hashCode();
			if (i != length() - 1) {
				result = PRIME * result + getEdge(i).hashCode();
			}
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Trace))
			return false;
		final Trace other = (Trace) obj;
		if (length() != other.length())
			return false;
		for (int i = 0; i < length(); i++) {
			if (!getVertex(i).equals(other.getVertex(i)))
				return false;
			if (i != length() - 1) {
				if (!getEdge(i).equals(other.getEdge(i)))
					return false;
			}
		}
		return true;
	}

	public void clear() {
		edges.clear();
		first = null;
	}
	
	
	public String toXMLString() {
		StringBuilder sb = new StringBuilder();
		// Iterator<Vertex> iter = getVertices();
		// int i=0;
		// while(iter.hasNext()) {
		// if(i>0) {
		// sb.append("->");
		// // sb.append(getEdge(i-1));
		// }
		// sb.append(iter.next());
		// i++;
		// }
		sb.append("\n");
		for (int i = 0; i < length() - 1; i++) 
		{
			DirectedEdge e = getEdge(i);
			sb.append(e.source());
			sb.append("\n");
			sb.append("{");
			sb.append(e);
			sb.append("}");
			sb.append("\n");
			if (i == length() - 2) {
				//sb.append("target: \n");
				sb.append(e.target());
			}
		}
		return sb.toString();
	}
	// public String toVariableString(String label) {
	// StringBuilder sb = new StringBuilder("(");
	// StringBuilder var = new StringBuilder("\n");
	// Iterator<Vertex> iter = getVertices();
	// int i=0;
	// while(iter.hasNext()) {
	//			
	// Vertex vertex = iter.next();
	//			
	// if(i>0) {
	// sb.append(", ");
	// var.append(", ");
	// }
	// sb.append(vertex);
	// try {
	// var.append(vertex.getLabelValue(label));
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// i++;
	// }
	// sb.append(")");
	//		
	// // sb.append(var);
	//		
	//		
	// return var.toString();
	// }

	// public int compareTo(Trace trace) {
	// int n = Math.min(this.length(), trace.length());
	// for(int i=0; i<n; i++) {
	// int x = Double.compare(getState(i).id(), trace.getState(i).id());
	// if(x!=0) return x;
	// }
	// return Double.compare(this.length(), trace.length());
	// }

	// public StateSpace getStateSpace() {
	// return space;
	// }

	// public Trace union(Trace t) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// public boolean contains(State s) {
	// // TODO Auto-generated method stub
	// return false;
	// }
}
