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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import dipro.graph.DirectedEdge;
import dipro.graph.Vertex;
import dipro.util.TreeHeapNode;


public class KStarPathGraph extends PathGraph {


	private HashMap<Vertex, EppsteinHeapT> oldHeapTIndexTable; 
	
	KStarPathGraph(KStar kStar) {
		super(kStar);
	}

	public void prepareForSpaceExpansion() {
		oldHeapTIndexTable = heapTIndexTable; 
		heapTIndexTable = new HashMap<Vertex, EppsteinHeapT>();
		isEstablished = false;
	}
	
	@Override
	public void establish() {
		super.establish();
		for(Vertex vertex: oldHeapTIndexTable.keySet()) {
			if(heapTIndexTable.get(vertex)==null) {
				buildHeapT(vertex);
			}
		}
		oldHeapTIndexTable.clear();
		oldHeapTIndexTable = null;
		System.gc();
	}

	public boolean isComplete(PNode node) {
		return node.isComplete();
	}

	/*private DirectedEdge getEdge(PNode pNode) {
		return pNode.edge();
	}*/
	private DirectedEdge getEdge(TreeHeapNode hNode) {
		if(hNode.getClass()== HeapInNode.class) return ((HeapInNode)hNode).getElement();
		if(hNode.getClass()== HeapTNode.class) return ((HeapTNode)hNode).getElement().getElement();
		throw new IllegalArgumentException("Invalid node type: "+hNode.getClass());
	}
	
	void correctReference(PNode node) {
		if(node.getClass() == RootNode.class) {
			assert node == root;
			return;
		}
		if(node.getClass() == HeapInPNode.class) {
			HeapInNode n = ((HeapInPNode)node).node;
			EppsteinHeapIn heapIn = indexTable.get(node.getHeapOwner()).heapIn;
			LinkedList<TreeHeapNode<DirectedEdge>> path = heapIn.getPathTo(n.getElement());
			assert !path.isEmpty();
			HeapInNode newN = (HeapInNode) path.getFirst();
			((HeapInPNode)node).refreshReference(newN);
		}
		else {
			if(node.getClass() == HeapTPNode.class) {
				HeapTNode n = ((HeapTPNode)node).node;
				EppsteinHeapT heapT = heapTIndexTable.get(node.getHeapOwner());
				assert node.getHeapOwner() == heapT.vertex;
				LinkedList<TreeHeapNode<TreeHeapNode<DirectedEdge>>> path = heapT.getPathTo(n.getElement());
				/* < ForDebugging > 
				if(path.isEmpty()) {
					System.out.println("Node missing in tree heap of "+node.getHeapOwner()+" (owner");
					System.out.println(n.getElement().getElement());
				}
				 /* </ ForDebugging > */
				assert !path.isEmpty();
				HeapTNode newN = (HeapTNode) path.getFirst();
				((HeapTPNode)node).refreshReference(newN);
			}
		}
	}
	
	
	/** Use only for debugging (ForDebugging) */
	boolean checkEdgeValidity(PNode n1, PNode n2) {
		if(n1.getClass() == RootNode.class) {
			if(n1 != root) {
				System.out.println(n1+" != "+root);
				return false;
			}
			EppsteinHeapT tHeapT = heapTIndexTable.get(kSearch.TARGET);
			if(tHeapT == null) {
				System.out.println("Tree heap of "+kSearch.TARGET+" is null");
				return false;
			}
			if(tHeapT.size() <= 0 ) {
				System.out.println("The size of the tree heap of "+kSearch.TARGET+" is "+tHeapT.size());
				return false;
			}
			HeapTNode tHeapTRoot = (HeapTNode) tHeapT.getRoot();
			if(n2.getClass() != HeapTPNode.class) {
				System.out.println(n2+" is not of the type "+HeapTPNode.class);
				return false;
			}
			if(!n2.getHeapOwner().equals(kSearch.TARGET)) {
				System.out.println("The owner of the tree heap of "+n2+" is not "+kSearch.TARGET);
				return false;
			}
			if(!getEdge(tHeapTRoot).equals(n2.edge())) {
				System.out.println("The edge of n2 ("+n2.edge()+
						") is not the same as the edge associated with the root of the tree heap of " +
						kSearch.TARGET+" ("+getEdge(tHeapTRoot)+").");
				return false;
			}
			return true;
		}
		PNode p; 
		Vertex v = n1.getHeapOwner();
		if(n1.getClass() == HeapInPNode.class) {
			HeapInNode n = ((HeapInPNode)n1).node;
			EppsteinHeapIn heapIn = indexTable.get(v).heapIn;
			LinkedList<TreeHeapNode<DirectedEdge>> path = heapIn.getPathTo(n.getElement());
			if(path.isEmpty()) {
				System.out.println("Heap_In of "+v+" does not contain "+n.getElement());
				heapIn.printInLevels();
				return false;
			}
			HeapInNode newN = (HeapInNode) path.getFirst();
			p = new HeapInPNode(newN);
		}
		else {
			if(n1.getClass() != HeapTPNode.class) return false;
			HeapTNode n = ((HeapTPNode)n1).node;
			EppsteinHeapT heapT = heapTIndexTable.get(v);
			LinkedList<TreeHeapNode<TreeHeapNode<DirectedEdge>>> path = heapT.getPathTo(n.getElement());
			if(path.isEmpty()) {
				System.out.println("Heap_T of "+v+" does not contain "+n.getElement());
				heapT.printInLevels();
				return false;
			}
			HeapTNode newN = (HeapTNode) path.getFirst();
			p = new HeapTPNode(newN, v);
		}
		Iterator<? extends DirectedEdge> children = outgoingEdges(p);
		boolean b = false;
		while(children.hasNext()) {
			DirectedEdge e = children.next();
			PNode c = (PNode) e.target();
			if(c.getHeapOwner().equals(n2.getHeapOwner()) &&
					c.edge().equals(n2.edge())) {
				b = true;
				break;
			}
		}
		if(!b) {
			System.out.println("The node corresponding to "+n1+" (n1) is "+p);
			System.out.println("Its outgoing edges are: ");
			Iterator<? extends DirectedEdge> iter = outgoingEdges(p);
			while(iter.hasNext()) {
				System.out.println(iter.next());
			}
			if(p instanceof HeapInPNode) {
				System.out.println("HeapIn ");
				System.out.println(indexTable.get(v).heapIn.printInLevels());
			} else {
				System.out.println("HeapT ");
				System.out.println(heapTIndexTable.get(v).printInLevels());
			}
			return false;
		}
		return true;
	}
}
