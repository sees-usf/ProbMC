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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import dipro.alg.BF;
import dipro.alg.BF.SearchMark;
import dipro.graph.DirectedEdge;
import dipro.graph.DirectedGraph;
import dipro.graph.Edge;
import dipro.graph.Vertex;

public class SearchTree implements DirectedGraph {

//	protected final Attribute INCOMING_EDGES_KEY = new Attribute(
//			"INCOMING_EDGES");
	protected final Attribute TREE_EDGE_KEY = new Attribute("TREE_EDGE_KEY");

	protected BF alg;
	protected Hashtable<Vertex, BF.SearchMark> closed;
	protected Hashtable<Vertex, BF.SearchMark> open;
	protected SelectionPriorityQueue<BF.SearchMark> queue;
	protected DistanceComparator comparator;
//	protected int recordedTransitions;
	protected int maxOpenSize = 0;
	protected int idCounter = 0;
	protected double bestFValue = -1.0d;

	public SearchTree(BF alg) {
		System.out.println("searchTreeConstructor - SearchTree");
		this.alg = alg;
		this.comparator = new DistanceComparator();
		queue = new SelectionPriorityQueue<BF.SearchMark>(comparator);
		closed = new Hashtable<Vertex, BF.SearchMark>();
		open = new Hashtable<Vertex, BF.SearchMark>();
//		recordedTransitions = 0;
	}

	public double f(SearchMark mark) {
		return mark.f();
	}

	public double g(SearchMark mark) {
		return mark.g();
	}

	public double h(SearchMark mark) {
		return mark.h();
	}

	public double f(Vertex vertex) {
		SearchMark mark = isExplored(vertex);
		assert mark != null;
		return f(mark);
	}

	public double g(Vertex vertex) {
		SearchMark mark = isExplored(vertex);
		assert mark != null;
		return g(mark);
	}

	public double h(Vertex vertex) {
		SearchMark mark = isExplored(vertex);
		assert mark != null;
		return h(mark);
	}

	public double getBestFValue() {
		assert bestFValue != -1.0d;
		return bestFValue;
	}

	// Returns a searchMark which is head of queue
	public BF.SearchMark getOptimalOpen() {
		System.out.println("getOptimalOpen - SearchTree(in DiPro.util)");
		BF.SearchMark vMark = queue.peek();
		assert vMark == null || vMark == open.get(vMark.vertex());
		return vMark;
	}

	protected void insertOpen(BF.SearchMark vMark) {
		System.out.println("insertOpen - searchTree");	
		assert isOpen(vMark.vertex()) == null;
		open.put(vMark.vertex(), vMark);
		queue.offer(vMark);
		assert isOpen(vMark.vertex()) == vMark;
		maxOpenSize= Math.max(maxOpenSize, open.size());
		if (bestFValue == -1.0d
				|| comparator.compare(vMark.f(), bestFValue) < 0)
			bestFValue = vMark.f();
	}

	protected void insertClosed(BF.SearchMark vMark) {
		assert isClosed(vMark.vertex()) == null;
		closed.put(vMark.vertex(), vMark);
	}
	
	public void open(SearchMark mark) {
		System.out.println("open - SearchTree");	
		assert isExplored(mark.vertex()) == null;
		insertOpen(mark);
	}

	public SearchMark removeOpen(Vertex v) {
		BF.SearchMark vMark = open.remove(v);
		if (vMark != null) {
			boolean b = queue.remove(vMark);
			assert b;
		}
		assert isOpen(v) == null;
		assert !queue.contains(vMark);
		return vMark;
	}

	public void replaceOpen(BF.SearchMark newVMark, BF.SearchMark oldVMark) {
		assert newVMark.vertex().equals(oldVMark.vertex());
		assert isOpen(oldVMark.vertex()) == oldVMark;
		assert isOpen(newVMark.vertex()) == oldVMark;
//		SearchMark temp = removeOpen(oldVMark.vertex());
		open.put(newVMark.vertex(), newVMark);
		boolean b = queue.remove(oldVMark);
		assert b;
		queue.offer(newVMark);
//		assert temp == oldVMark;
//		assert isOpen(oldVMark.vertex()) == null;
//		assert isOpen(newVMark.vertex()) == null;
//		insertOpen(newVMark);
		assert isOpen(oldVMark.vertex()) == newVMark;
		assert isOpen(newVMark.vertex()) == newVMark;
	}

	public void replaceClose(SearchMark oldMark, SearchMark newMark) {
		assert newMark.vertex().equals(oldMark.vertex());
		assert isClosed(oldMark.vertex()) == oldMark;
		assert isClosed(newMark.vertex()) == oldMark;
		closed.put(newMark.vertex(), newMark);
		assert isClosed(oldMark.vertex()) == newMark;
		assert isClosed(newMark.vertex()) == newMark;
	}

	public void close(SearchMark mark) {
		SearchMark temp = removeOpen(mark.vertex());
		assert temp == mark;
		insertClosed(mark);
	}


	public void reopen(SearchMark oldMark, SearchMark newMark) {
		SearchMark closedMark = closed.remove(oldMark.vertex());
		assert closedMark == oldMark;
		insertOpen(newMark);
	}

	public BF.SearchMark isOpen(Vertex v) {
		return open.get(v);
	}

	public BF.SearchMark isClosed(Vertex v) {
		return closed.get(v);
	}

	public BF.SearchMark isExplored(Vertex v) {
		BF.SearchMark vMark = open.get(v);
		if (vMark != null)
			return vMark;
		vMark = closed.get(v);
		return vMark;
	}

	
	public boolean isOpenEmpty() {
		return open.isEmpty();
	}

	public boolean isEmpty() {
		return open.isEmpty() && closed.isEmpty();
	}
	
	public int maxOpenSize() {
		return maxOpenSize;
	}
	

	//@SuppressWarnings("unchecked")
	public void setTreeEdge(BF.SearchMark vMark, DirectedEdge treeEdge) {
//		Collection<DirectedEdge> c = (Collection<DirectedEdge>) vMark
//				.get(INCOMING_EDGES_KEY);
//		assert c.contains(treeEdge);
		vMark.set(TREE_EDGE_KEY, treeEdge);
	}

//	@SuppressWarnings("unchecked")
//	public boolean addNewIncomingEdge(BF.SearchMark vMark,
//			DirectedEdge edgeToNewParent) {
//		boolean b = addIncomingEdge(vMark, edgeToNewParent);
//		// if(b) recordedTransitions++;
//		return b;
//	}
//
//	private boolean addIncomingEdge(BF.SearchMark vMark, DirectedEdge uv) {
//		assert uv != null;
//		assert vMark != null;
//		assert uv.target().equals(vMark.vertex());
//		if (!vMark.has(INCOMING_EDGES_KEY)) {
//			vMark.set(INCOMING_EDGES_KEY, new LinkedList<DirectedEdge>());
//		}
//		LinkedList<DirectedEdge> incomingTrans = (LinkedList<DirectedEdge>) vMark
//				.get(INCOMING_EDGES_KEY);
//		if (!incomingTrans.contains(uv)) {
//			incomingTrans.add(incomingTrans.size(), uv);
//			return true;
//		}
//		return false;
//	}
//
//	public void incrementNumEdges(int inc) {
//		recordedTransitions = recordedTransitions + inc;
//		// //ForDebugging
//		// if(numEdges() != countRecordedEdges()) {
//		// System.out.println("Warning: "+numEdges()+" != "+countRecordedEdges());
//		// System.exit(0);
//		// }
//	}

	// public void decrementNumEdges(int dec) {
	// recordedTransitions = recordedTransitions - dec;
	// //ForDebugging
	// if(numEdges() != countRecordedEdges()) {
	// System.out.println("Warning: "+numEdges()+" != "+countRecordedEdges());
	// System.exit(0);
	// }
	// }

//	public int takeOverIncommingEdges(BF.SearchMark vMark1, BF.SearchMark vMark2) {
//		assert vMark1.vertex().equals(vMark2.vertex());
//		int k = 0;
//		LinkedList<DirectedEdge> l = (LinkedList<DirectedEdge>) vMark2
//				.get(INCOMING_EDGES_KEY);
//		for (DirectedEdge e : l) {
//			boolean b = addIncomingEdge(vMark1, e);
//			if (b)
//				k++;
//		}
//		return k;
//	}
//
//	@SuppressWarnings("unchecked")
//	public Collection<DirectedEdge> getIncomingEdges(BF.SearchMark vMark) {
//		Collection<DirectedEdge> c = (Collection<DirectedEdge>) vMark
//				.get(INCOMING_EDGES_KEY);
//		if (c == null)
//			c = Collections.EMPTY_LIST;
//		return c;
//	}
//
//	public Collection<DirectedEdge> getIncomingEdges(Vertex v) {
//		SearchMark mark = isExplored(v);
//		return getIncomingEdges(mark);
//	}
//
//	@SuppressWarnings("unchecked")
//	public void clearIncomingEdges(BF.SearchMark vMark) {
//		if (vMark.has(INCOMING_EDGES_KEY)) {
//			((Collection<DirectedEdge>) vMark.get(INCOMING_EDGES_KEY)).clear();
//			assert vMark.has(TREE_EDGE_KEY);
//			vMark.destroy(TREE_EDGE_KEY);
//		} else {
//			assert !vMark.has(TREE_EDGE_KEY);
//		}
//	}

	//@SuppressWarnings("unchecked")
	public DirectedEdge getTreeEdge(BF.SearchMark vMark) {
		if (vMark.has(TREE_EDGE_KEY)) {
//			assert vMark.has(INCOMING_EDGES_KEY);
			DirectedEdge e = (DirectedEdge) vMark.get(TREE_EDGE_KEY);
//			assert ((Collection<DirectedEdge>) vMark.get(INCOMING_EDGES_KEY))
//					.contains(e);
			return e;
		}
		return null;
	}

	public DirectedEdge getTreeEdge(Vertex v) {
		BF.SearchMark vMark = isExplored(v);
		if (vMark == null)
			return null;
		return getTreeEdge(vMark);
	}

	public int numVertices() {
		return closed.size() + open.size();
	}

//	public Iterator<DirectedEdge> edges() {
//		return new Iterator<DirectedEdge>() {
//			Iterator<? extends Vertex> iter = vertices();
//			Iterator<DirectedEdge> edges;
//
//			public boolean hasNext() {
//				while ((edges == null || !edges.hasNext()) && iter.hasNext()) {
//					Vertex v = iter.next();
//					BF.SearchMark vMark = isExplored(v);
//					edges = getIncomingEdges(vMark).iterator();
//				}
//				return (edges != null && edges.hasNext());
//			}
//
//			public DirectedEdge next() {
//				if (hasNext()) {
//					return edges.next();
//				} else
//					return null;
//			}
//
//			public void remove() {
//				throw new UnsupportedOperationException("remove");
//			}
//		};
//	}
	public Iterator<DirectedEdge> edges() {
		return new Iterator<DirectedEdge>() {
			Iterator<? extends Vertex> iter = vertices();
			DirectedEdge edge = null;
			public boolean hasNext() {
				while (edge == null) {
					if(!iter.hasNext()) return false;
					Vertex v = iter.next();
					BF.SearchMark vMark = isExplored(v);
					if(vMark.has(TREE_EDGE_KEY)) edge = (DirectedEdge)vMark.get(TREE_EDGE_KEY);
				}
				assert edge != null;
				return true;
			}
			public DirectedEdge next() {
				DirectedEdge e = edge;
				edge = null;
				return e;
			}
			public void remove() {
				throw new UnsupportedOperationException("remove");
			}
		};
	}

	// public Vertex getOwnVertex(Vertex v) {
	// Vertex ownV = isClosed(v);
	// if(ownV==null) ownV = isOpen(v);
	// return ownV;
	// }

	// public void replaceClosed(Vertex oldV, Vertex newV) {
	// removeClosed(oldV);
	// insertClosed(newV);
	// }

	public Iterator<Vertex> vertices() {
		return new Iterator<Vertex>() {
			Enumeration<Vertex> iter1 = open.keys();
			Enumeration<Vertex> iter2 = closed.keys();

			public boolean hasNext() {
				return iter1.hasMoreElements() || iter2.hasMoreElements();
			}

			public Vertex next() {
				Vertex next;
				if (iter1.hasMoreElements())
					next = iter1.nextElement();
				else {
					next = iter2.nextElement();
				}
				return next;
			}

			public void remove() {
				throw new UnsupportedOperationException("remove");
			}
		};
	}

	public Iterator<Vertex> getOpenVertices() {
		Iterator<Vertex> iter = new Iterator<Vertex>() {
			Iterator<SearchMark> qIter = queue.iterator();

			public boolean hasNext() {
				return qIter.hasNext();
			}

			public Vertex next() {
				return qIter.next().vertex();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		return iter;
	}

	public Iterator<Vertex> getClosedVertices() {
		Iterator<Vertex> iter = new Iterator<Vertex>() {
			Enumeration<Vertex> vs = closed.keys();

			public boolean hasNext() {
				return vs.hasMoreElements();
			}

			public Vertex next() {
				return vs.nextElement();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		return iter;
	}

	public int numEdges() {
		// int m = countRecordedEdges();
		// assert m == recordedTransitions;
		// assert m == numVertices() - 1;
//		return recordedTransitions;
		return numVertices() -1;
	}

	// ForDebugging
	private int countRecordedEdges() {
		Iterator<DirectedEdge> iter = edges();
		int m = 0;
		while (iter.hasNext()) {
			iter.next();
			m++;
		}
		return m;
	}

	public int numOpenVertices() {
		return open.size();
	}

	public int numClosedVertices() {
		return closed.size();
	}

	// public List<? extends BF.SearchMark> getFoundTargets() {
	// return targets;
	// }

	// public void addNewTarget(BF.SearchMark vMark) throws Exception {
	// solCollector.receiveSolution(vMark);
	// // assert !targets.contains(vMark);
	// // targets.add(vMark);
	// // if(bestSolutionValue==-1.0d || comparator.compare(bestSolutionValue,
	// vMark.f())>0) {
	// // bestSolutionValue = vMark.f();
	// // }
	// }

	public Trace backtrack(Vertex v) {
		return backtrack(isExplored(v));
	}

	public Trace backtrack(BF.SearchMark vMark) {
		Trace trace = new Trace(vMark.vertex());
		DirectedEdge uv = getTreeEdge(vMark);
		while (uv != null) {
			trace.preAppend(uv);
			uv = getTreeEdge(uv.source());
		}
		return trace;
	}

	public Iterator<DirectedEdge> incomingEdges(Vertex v) {
		BF.SearchMark vMark = isExplored(v);
		assert vMark != null;
		ArrayList<DirectedEdge> l = new ArrayList<DirectedEdge>(1);
		if(vMark.has(TREE_EDGE_KEY)) {
			l.add((DirectedEdge)vMark.get(TREE_EDGE_KEY));
		}
		return l.iterator();
	}

	public Iterator<DirectedEdge> outgoingEdges(Vertex v) {
		throw new UnsupportedOperationException();
	}

	public Iterator<DirectedEdge> connectingEdges(Vertex v1, Vertex v2) {
		throw new UnsupportedOperationException();
	}

	public int degree(Vertex v) {
		throw new UnsupportedOperationException();
	}

	public synchronized int scheduleAsNextForExpansion(Iterator<Vertex> vertices) {
		LinkedList<BF.SearchMark> selectedOpenVertices = new LinkedList<BF.SearchMark>();
		while (vertices.hasNext()) {
			Vertex v = vertices.next();
			BF.SearchMark vMark = isOpen(v);
			if (vMark != null) {
				selectedOpenVertices.addLast(vMark);
			}
		}
		int x = selectedOpenVertices.size();
		queue.select(selectedOpenVertices);
		return x;
	}

	public synchronized boolean scheduleAsNextForExpansion(Vertex vertex) {
		BF.SearchMark mark = isOpen(vertex);
		if (mark != null) {
			queue.select(mark);
			return true;
		}
		return false;
	}

	public Vector<SearchMark> peekThreeAhead() {
		Vector<SearchMark> v = new Vector<SearchMark>(5);
		synchronized(queue) {
		Iterator<SearchMark> iter = queue.iterator();
		int i =0;
		while(iter.hasNext()) {
			v.add(i, iter.next());
			if(i==4) break;
			i++;
		}
		queue.notifyAll();
		}
		Collections.sort(v, new Comparator<SearchMark>() {
			@Override
			public int compare(SearchMark o1, SearchMark o2) {
				return comparator.compare(o1, o2);
			}
		});
		Vector<SearchMark> a = new Vector<SearchMark>(3);
		if(v.size()>0) a.add(0, v.elementAt(0));
		if(v.size()>1) a.add(1, v.elementAt(1));
		if(v.size()>2) a.add(2, v.elementAt(2));
		return a;
	}
	
	public Iterator<? extends DirectedEdge> adjacentEdges(Vertex v) {
		throw new UnsupportedOperationException();
	}

	public float weight(Edge e) {
		throw new UnsupportedOperationException();
	}

	public int vertexSize() {
		throw new UnsupportedOperationException();
	}

	public int edgeSize() {
		throw new UnsupportedOperationException();
	}

	protected class DistanceComparator implements Comparator<BF.SearchMark> {

		public int compare(BF.SearchMark v1Mark, BF.SearchMark v2Mark) {
			Double d1 = v1Mark.f();
			Double d2 = v2Mark.f();
			int x = alg.getComparator().compare(d1, d2);
			if (x == 0) {
				int l1 = v1Mark.depth();
				int l2 = v2Mark.depth();
				x = l1 == l2 ? 0 : l1 > l2 ? 1 : -1;
			}
			return x;
		}

		public int compare(Double d1, Double d2) {
			return alg.getComparator().compare(d1, d2);
		}
	}

	@Override
	public void clear() {
		queue.clear();
		open.clear();
		closed.clear();
		maxOpenSize = 0;
		idCounter = 0;
		System.gc();
	}

	@Override
	public List<String> getVertexLabels() {
		throw new UnsupportedOperationException();
	}

	//@SuppressWarnings("unchecked")
	public Class getVertexLabelType(String label) {
		throw new UnsupportedOperationException();
	}
}
