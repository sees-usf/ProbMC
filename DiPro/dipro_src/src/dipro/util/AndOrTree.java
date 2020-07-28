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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import dipro.graph.State;
import dipro.stoch.prism.PrismTransition;

public class AndOrTree {

	protected boolean isMax;
	protected Node root;
	private int numAllTraces;
	private int numORNodes;
	private int numANDNodes;
	private int numPointers;
	
	public AndOrTree(boolean max) {
		this.isMax = max;
		this.root = null;
		numORNodes = 0;
		numANDNodes = 0;
		numPointers = 0;
		numAllTraces = 0;
	}

	public boolean add(Trace trace, double prob) {
		// System.out.println("Steps = "+(trace.length()-1));
		// System.out.println("Trace: "+trace+" (prob="+prob+")");
		if (prob == 0.0d && isMax)
			return false;
		List<Object> seq = transformIntoSequence(trace);
		if (root == null) {
			if (trace.length() == 1) {
				// The initial state is a target state.
				root = new Terminal((State)trace.getFirstVertex(), trace);
			} else
				root = new ORNode((State)trace.getFirstVertex());
		}
		assert trace.getFirstVertex().equals(root.o);
		int i = 1;
		Node n = root;
		add(trace, prob, seq, i, n);
		// print();
		numAllTraces++;
		return true;
	}

	public double getSolutionProb() {
		if(root==null)
			System.out.println();
		assert root != null;
		return root.p;
	}

	public Node getRoot() {
		return root;
	}

	@SuppressWarnings("unchecked")
	public Iterator<Trace> constructSolution() {
		if (root == null)
			return Collections.EMPTY_LIST.iterator();
		final LinkedList<Terminal> solution = new LinkedList<Terminal>();
		dfs(root, solution);
		/* < ForDebugging > 
		System.out.println(solution.size()+"=="+getNumSolutionTraces()+"?");
		/* </ ForDebugging > */
		assert solution.size() == getNumSolutionTraces();
		Collections.sort(solution, new Comparator<Terminal>() {
			@Override
			public int compare(Terminal o1, Terminal o2) {
				return o1.orderIndex > o2.orderIndex? 1: (o1.orderIndex == o2.orderIndex? 0: -1);
			}
		});
		return new Iterator<Trace>() {
			Iterator<Terminal> iter = solution.iterator();
			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}
			@Override
			public Trace next() {
				return iter.next().trace;
			}
			@Override
			public void remove() {
				iter.remove();
			}
		};
	}

	public int getMemorySize() {
		/* Size of an OR node: 2x reference, 1x real (double = 8) and 1x integer */
		int orNodeSize =  2 * 4 + 8 + 4; 
		/* Size of an AND node: 1x reference, 1x real (double = 8) and 1x integer */
		int andNodeSize = 4 + 8 + 4; 
		
		return numORNodes * orNodeSize + numANDNodes * andNodeSize + numPointers * 4;
	}

	public int getNumSolutionTraces() {
		return root.t;
	}

	public int getNumNodes() {
		return numANDNodes + numORNodes;
	}

	public int getNumAllTraces() {
		return numAllTraces;
	}
	
	public int getNumPointers() {
		return numPointers;
	}

	private void dfs(Node n, LinkedList<Terminal> solution) {
		if (n.isTerminal()) {
			solution.add((Terminal) n);
		} else {
			if (n.isAndNode()) {
				for (Node c : n.children) {
					dfs(c, solution);
				}
			} else {
				dfs(((ORNode)n).m, solution);
			}
		}
	}

	private void add(Trace trace, double prob, List<Object> seq, int i, Node n) {
		if (i >= seq.size())
			return;
		Object m = seq.get(i);
		for (Node c : n.children) {
			if (sameStateOrAction(c.o, m)) {
				// c carries the same object (state or transition) as m
				add(trace, prob, seq, i + 1, c);
				n.updateValue();
				return;
			}
		}
		Node n1 = n;
		for (int j = i; j < seq.size(); j++) {
			Node n2;
			if (j == seq.size() - 1)
				n2 = new Terminal((State)seq.get(j), trace);
			else
				n2 = createNode(seq.get(j));
			n2.p = prob;
			n1.addChild(n2);
			if (!n1.isAndNode()) ((ORNode)n1).m = n2;
			n1 = n2;
		}
		n.updateValue();
	}

	
	private boolean sameStateOrAction(Object o1, Object o2) {
		if (o1 instanceof PrismTransition) {
			assert o2 instanceof PrismTransition;
			PrismTransition t1 = (PrismTransition)o1;
			PrismTransition t2 = (PrismTransition)o2;
			// Both transitions must originate at the same state
			assert t1.source().equals(t2.source()); 
			int a1 = t1.getTransitionData().getActionIndex();
			int a2 = t2.getTransitionData().getActionIndex();
			return a1==a2;
		}
		assert o1 instanceof State;
		assert o2 instanceof State;
		return o1.equals(o2);
	}

	private List<Object> transformIntoSequence(Trace trace) {
		ArrayList<Object> seq = new ArrayList<Object>(2 * trace.length() - 1);
		for (int i = 0; i < trace.length(); i++) {
			if (i > 0)
				seq.add(seq.size(), trace.getEdge(i - 1));
			seq.add(seq.size(), trace.getVertex(i));
		}
		return seq;
	}

	public void display() {

	}

	public void print() {
		System.out.println("AND/OR Tree===========");
		traverseInLevels();
		System.out.println("======================");
	}

	private void traverseInLevels() {
		if (root == null)
			return;
		int i = 1;
		LinkedList<Object> q = new LinkedList<Object>();
		Object levelSep = new Object();
		// Object sibilingSep = new Object();
		q.addLast(root);
		q.add(levelSep);
		//int level = 0;
		//int levelCount = 0;
		while (!q.isEmpty()) {
			Object o = q.removeFirst();
			if (o instanceof Integer) {
				System.out.println("--(" + o + ")--------------");
			} else {
				if (o == levelSep) {
					//level++;
					//levelCount = 0;
					if (!q.isEmpty())
						q.addLast(o);
					System.out.println();
				} else {
					//levelCount++;
					Node n = (Node) o;
					System.out.print(n);
					if (!n.children.isEmpty()) {
						System.out.print(" ->" + i);
						q.add(new Integer(i));
						q.addAll(n.children);
						i++;
					}
					System.out.println();
				}
			}
		}
	}
	
	private Node createNode(Object o) {
		if(o instanceof State) return new ORNode((State)o); 
		assert o instanceof PrismTransition; 
		return new ANDNode((PrismTransition)o);
	}

	public abstract class Node {
		protected Object o;
		protected LinkedList<Node> children;
		protected double p;
		protected int t;

		private Node(Object o) {
			assert (o instanceof State) || (o instanceof PrismTransition);
			this.o = o;
			children = new LinkedList<Node>();
			p = 0.0d;
			t = 1;
		}

		void addChild(Node n) {
			children.add(n);
			numPointers++;
		}

		int getActionIndex() {
			assert o instanceof PrismTransition;
			int a = ((PrismTransition) o).getTransitionData().getActionIndex();
			return a;
		}

		boolean isTerminal() {
			assert !children.isEmpty();
			return false;
		}

		abstract void  updateValue(); 
		abstract boolean isAndNode();
		
		public List<Node> getChildren() {
			return children;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			if (isAndNode()) {
				sb.append(getActionIndex());
			} else {
				// sb.append("OR");
				sb.append(o);
			}
			sb.append("(");
			sb.append(p);
			sb.append(")");
			return sb.toString();
		}
	}

	private class ANDNode extends Node {
		
		ANDNode(PrismTransition transition) {
			super(transition);
			numANDNodes++;
		}
		
		void updateValue() {
			p = 0.0d;
			t = 0;
			for (Node n : children) {
					p = p + n.p;
					t = t + n.t;
			}
		}
		
		boolean isAndNode() {
			return true;
		}
	}
	
	
	private class ORNode extends Node {
		protected Node m;
		
		ORNode(State state) {
			super(state);
			m = null;
			numORNodes++;
		}
		
		void updateValue() {
			p = (isMax) ? 0.0d : 1.0d;
			t = -1;
			for (Node n : children) {
				if (isMax) {
					if (p < n.p || p == n.p && (t<0 || t> n.t)) {
						p = n.p;
						t = n.t;
						m = n;
					}
				} else {
					if (p > n.p || p == n.p && (t<0 || t> n.t)) {
						p = n.p;
						m = n;
						t = n.t;
					}
				}
			}
		}
		
		boolean isAndNode() {
			return false;
		}
	}
	
	private class Terminal extends ORNode {
		
		int orderIndex;
		Trace trace;

		Terminal(State state, Trace t) {
			super(state);
			this.orderIndex = numAllTraces+1;
			this.trace = t;
		}

		boolean isTerminal() {
			assert children.isEmpty();
			return true;
		}
	}
}
