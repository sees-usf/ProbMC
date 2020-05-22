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
import java.util.Iterator;
import java.util.LinkedList;

import dipro.graph.DirectedEdge;
import dipro.graph.Vertex;
import dipro.util.TreeHeap;
import dipro.util.TreeHeapNode;

public class EppsteinHeapT extends TreeHeap<TreeHeapNode<DirectedEdge>> {

	protected Vertex vertex;
	private int numNewNodes;
	
	public EppsteinHeapT(Vertex v, Comparator<TreeHeapNode<DirectedEdge>> comparator) {
		super(comparator);
		vertex = v;
		numNewNodes = 0;
	}

	private EppsteinHeapT(Vertex v, EppsteinHeapT parentHeap) {
		this(v, parentHeap.comparator);
		root = parentHeap.root;
		path = parentHeap.path;
		size = parentHeap.size;
	}
	
	public void setVertex(Vertex v) {
		assert vertex != null;
		this.vertex = v;
	}
	
	public TreeHeapNode<DirectedEdge> remove() {
		throw new UnsupportedOperationException();
	}
	
	public void add(TreeHeapNode<DirectedEdge> element) {
		throw new UnsupportedOperationException();
	}
	
	public EppsteinHeapT createEppsteinCopy(Vertex v) {
		EppsteinHeapT newHeap = new EppsteinHeapT(v, this);
		return newHeap;
	}
	
	public void doPreservingAddition(TreeHeapNode<DirectedEdge> heapInNode) {
		path = clonePath();
		if(size==0) {
			// The heap is empty
			assert root == null;
			root = createNode(heapInNode);
			path.addFirst(root);
			size = 1;
		} else {
			assert size>0;
			HeapTNode newNode = (HeapTNode) createNode(heapInNode);
			setPathToParentOfNextFreePosition();
			clonePathNodes();
			assert path.size()>= 1;
			HeapTNode parent = (HeapTNode) path.getFirst();
			if(parent.getLeft() == null) {
				parent.setLeft(newNode);
			}
			else {
				assert parent.getRight()==null;
				parent.setRight(newNode);
			}
			path.addFirst(newNode);
			numNewNodes = path.size();
			size++;
			heapUp();
		}
	}
	
	public int getNumNewNodes() {
		return numNewNodes;
	}
	
	
//	private void clonePath(TreeHeapNode<E> cpyNode) {
//		TreeHeapNode<E> cursor = cpyNode; 
//		TreeHeapNode<E> parent = cursor.getParent(); 
//		while(parent!=null) {
//			TreeHeapNode<E> cpyParent = cloneNode(parent);
//			cursor.setParent(cpyParent);
//			if(cpyParent.getLeft().getElement()==cursor.getElement()) {
//				cpyParent.setLeft(cursor);
//			} else {
//				assert cpyParent.getRight().getElement()==cursor.getElement();
//				cpyParent.setRight(cursor);
//			}
//			cursor = cpyParent; 
//			parent = cursor.getParent();
//		}
//		root = cursor; 
//	}
	
	private LinkedList<TreeHeapNode<TreeHeapNode<DirectedEdge>>> clonePath() {
		LinkedList<TreeHeapNode<TreeHeapNode<DirectedEdge>>> newPath = new LinkedList<TreeHeapNode<TreeHeapNode<DirectedEdge>>>();
		Iterator<TreeHeapNode<TreeHeapNode<DirectedEdge>>> iter = path.iterator();
		while(iter.hasNext()) {
			newPath.addLast(iter.next());
		}
		return newPath;
	}
	
	private void clonePathNodes() {
		assert !path.isEmpty();
		LinkedList<TreeHeapNode<TreeHeapNode<DirectedEdge>>> newPath = new LinkedList<TreeHeapNode<TreeHeapNode<DirectedEdge>>>();
		HeapTNode node = (HeapTNode) path.removeFirst();
		HeapTNode cpyNode = cloneNode(node);
		newPath.addLast(cpyNode);
		while(!path.isEmpty()) {
			HeapTNode parent = (HeapTNode) path.getFirst();
			HeapTNode cpyParent = cloneNode(parent);
			if(cpyParent.getLeft().getElement()==cpyNode.getElement()) {
				cpyParent.setLeft(cpyNode);
			} else {
				assert cpyParent.getRight().getElement()==cpyNode.getElement();
				cpyParent.setRight(cpyNode);
			}
			newPath.addLast(cpyParent);
			cpyNode = cpyParent; 
			path.removeFirst();
		}
		path = newPath;
		root = cpyNode;
	}
	
	private HeapTNode cloneNode(HeapTNode node) {
		HeapTNode clone = (HeapTNode) createNode(node.getElement());
		if(node.getLeft()!=null) clone.setLeft(node.getLeft());
		if(node.getRight()!=null) clone.setRight(node.getRight());
		return clone;
	}

	@Override
	protected TreeHeapNode<TreeHeapNode<DirectedEdge>> createNode(TreeHeapNode<DirectedEdge> element) {
		return new HeapTNode(element);
	}
	
	public String toString() {
		return "HeapT("+vertex+")";
	}
}
