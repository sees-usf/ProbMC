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

import java.util.Comparator;
import java.util.LinkedList;

import dipro.graph.DirectedEdge;
import dipro.graph.Vertex;
import dipro.util.TreeHeap;
import dipro.util.TreeHeapNode;

public class EppsteinHeapIn extends TreeHeap<DirectedEdge> {
	
	private HeapInRoot top; 
	
	public EppsteinHeapIn(Comparator<DirectedEdge> comparator) {
		super(comparator);
		top = null;
	}

	@Override
	public TreeHeapNode<DirectedEdge> getRoot() {
		if(top==null) return null;
		return top;
	}

	@Override
	public void add(DirectedEdge element) {
		if(top==null) {
			top = new HeapInRoot(element);
		} else {
			int x = comparator.compare(element, top.getElement());
			if(x<0) {
				super.add(top.getElement());
				top.setElement(element);
			} else {
				super.add(element);
			}
			top.setChild((HeapInNode)root);
		}
	}

	@Override
	public void clear() {
		super.clear();
		top = null;
	}

	@Override
	public boolean contains(DirectedEdge element) {
		assert element != null;
		if(isEmpty()) return false;
		assert top!=null;
		if(top.getElement().equals(element)) return true;
		return super.contains(element);
	}

	@Override
	public LinkedList<TreeHeapNode<DirectedEdge>> getPathTo(DirectedEdge element) {
		assert element != null;
		if(isEmpty()) return new LinkedList<TreeHeapNode<DirectedEdge>>();
		assert top!=null;
		if(element.equals(top.getElement())) {
			LinkedList<TreeHeapNode<DirectedEdge>> p = new LinkedList<TreeHeapNode<DirectedEdge>>();
			p.addFirst(top);
			return p;
		} 
		else {
			LinkedList<TreeHeapNode<DirectedEdge>> p = super.getPathTo(element);
			if(!p.isEmpty()) p.addLast(top);
			return p;
		}
	}

	@Override
	public LinkedList<TreeHeapNode<DirectedEdge>> getPathToNode(TreeHeapNode<DirectedEdge> n) {
		assert n != null;
		if(isEmpty()) return new LinkedList<TreeHeapNode<DirectedEdge>>();
		assert top!=null;
		if(n == top) {
			LinkedList<TreeHeapNode<DirectedEdge>> p = new LinkedList<TreeHeapNode<DirectedEdge>>();
			p.addFirst(top);
			return p;
		} 
		else {
			LinkedList<TreeHeapNode<DirectedEdge>> p = super.getPathToNode(n);
			p.addLast(top);
			return p;
		}
	}

	@Override
	public boolean isEmpty() {
		return size()==0;
	}

	@Override
	public DirectedEdge peek() {
		if(isEmpty()) return null;
		assert top!=null;
		return top.getElement();
	}

	@Override
	public DirectedEdge remove() {
		if(isEmpty()) return null;
		assert top!=null;
		DirectedEdge result = top.getElement();
		if(!super.isEmpty()) {
			top.setElement(super.remove());
			if(root!=null) {
				top.setChild((HeapInNode) root);
			}
		}
		else top = null;
		return result;
	}

	@Override
	public int size() {
		if(top==null) {
			assert super.size()==0;
			return 0; 
		}
		return super.size()+1;
	}

	@Override
	public String printInLevels() {
		StringBuilder sb = new StringBuilder();
		if(top == null) {
			assert isEmpty();
			sb.append("Empty Heap\n");
			return sb.toString();
		}
		sb.append(top.getElement());sb.append(" \n");
		if(root!=null) {
			String s = super.printInLevels();
			sb.append(s);
		}
		return sb.toString(); 
	}

	@Override
	public boolean refresh(DirectedEdge element) {
		LinkedList<TreeHeapNode<DirectedEdge>> p = getPathTo(element);
		if(p.isEmpty()) return false;
//		if(!p.getFirst().getElement().equals(element)) {
//			System.out.println("e="+element);
//			System.out.println("p="+p);
//			System.out.println(printInLevels());
//		}
		assert p.getFirst().getElement().equals(element);
		TreeHeapNode<DirectedEdge> node = p.getFirst();
		if(shouldHeapDown(node)) {
			heapDown(node);
		}
		else {
			heapUp(p);
		}
		return true;
	}

	@Override
	protected TreeHeapNode<DirectedEdge> createNode(DirectedEdge element) {
		return new HeapInNode(element);
	}
	
	private Vertex getOwnerVertex() {
		if(top!=null) return top.getElement().target();
		else return null;
	}
	public String toString() {
		String owner = getOwnerVertex()==null? "?": getOwnerVertex().toString();
		return "HeapIn("+owner+")";
	}
}

class HeapInRoot extends HeapInNode {

	protected HeapInRoot(DirectedEdge e) {
		super(e);
	}

	protected void setChild(HeapInNode child) {
		super.setLeft(child);
	}

	@Override
	public void removeLeft() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeRight() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setLeft(TreeHeapNode<DirectedEdge> left) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setRight(TreeHeapNode<DirectedEdge> right) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void setElement(DirectedEdge element) {
		super.setElement(element);
	}
}
