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

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

public class TreeHeap<E> {
	
	protected Comparator<E> comparator;
	protected TreeHeapNode<E> root;
	protected int size;
	/** The path to the last node in the heap, i.e. the node in 
	 * the right-lower corner. 
	 */
	protected LinkedList<TreeHeapNode<E>> path;
	
	public TreeHeap (Comparator<E> comparator) {
		this.comparator = comparator;
		root = null;
		size = 0;
		path = new LinkedList<TreeHeapNode<E>>();
	}
	
	protected TreeHeapNode<E> createNode(E element) {
		return new TreeHeapNode<E>(element);
	}
	
	public void add(E element) {
		if(size==0) {
			// The heap is empty
			assert root == null;
			root = createNode(element);
			path.addFirst(root);
			size = 1;
		} else {
			assert size>0;
			TreeHeapNode<E> newNode =createNode(element);
			setPathToParentOfNextFreePosition();
			assert !path.isEmpty();
			TreeHeapNode<E> parent = path.getFirst();
			if(parent.getLeft() == null) {
				parent.setLeft(newNode);
			}
			else {
				parent.setRight(newNode);
			}
			path.addFirst(newNode);
			size++;
			heapUp();
		}
	}
	
	protected void heapUp() {
		heapUp(path);
	}
	
	
	protected void heapUp(LinkedList<TreeHeapNode<E>> p) {
		Iterator<TreeHeapNode<E>> iter = p.iterator();
		assert iter.hasNext();
		TreeHeapNode<E> child = iter.next();
		while(iter.hasNext()) {
			TreeHeapNode<E> parent = iter.next();
			if(comparator.compare(child.getElement(), parent.getElement())<0) {
				E e = child.getElement();
				child.setElement(parent.getElement());
				parent.setElement(e);
			} 
			else break;
			child = parent; 
		}
	}
	
	protected void heapDown() {
		heapDown(root);
	}
	
	protected void heapDown(TreeHeapNode<E> startNode) {
		TreeHeapNode<E> cursor = startNode;
		while(cursor!=null) {
			boolean shouldHeapDown = shouldHeapDown(cursor);
			if(shouldHeapDown){
				int x = cursor.getRight()==null? -1 : comparator.compare(cursor.getLeft().getElement(), cursor.getRight().getElement());
				E e = cursor.getElement();
				if(x<0) {
					assert cursor.getLeft()!=null;
					cursor.setElement(cursor.getLeft().getElement());
					cursor.getLeft().setElement(e);
					cursor = cursor.getLeft();
				}
				else {
					assert cursor.getRight()!=null;
					cursor.setElement(cursor.getRight().getElement());
					cursor.getRight().setElement(e);
					cursor = cursor.getRight();
				}
			}
			else {
				break;
			}
		}
	}
	
	protected boolean shouldHeapDown(TreeHeapNode<E> node) {
		boolean shouldHeapDown = false;
		int x = node.getLeft()==null? -1 : comparator.compare(node.getElement(), node.getLeft().getElement());
		shouldHeapDown = x>0;
		if(!shouldHeapDown) {
			x = node.getRight()==null? -1 : comparator.compare(node.getElement(), node.getRight().getElement());
			shouldHeapDown = x>0;
		}
		return shouldHeapDown;
	}
	
	protected void setPathToParentOfNextFreePosition() {
		assert size>0;
		assert !path.isEmpty();
		TreeHeapNode<E> cursor = path.removeFirst();
		while(!path.isEmpty()) {
			TreeHeapNode<E> parent = path.getFirst();
			assert parent.getLeft()==cursor || parent.getRight()==cursor; 
			if(parent.getLeft()== cursor) {
				break;
			} 
			cursor = path.removeFirst();
		}
		if(!path.isEmpty()) {
			// Now we go into the right sub-tree.
			TreeHeapNode<E> parent = path.getFirst();
			if(parent.getRight()==null) return;
			assert parent.getRight()!=null;
			path.addFirst(parent.getRight());
			// The node in the left-lower corner 
			// in this sub-tree is the parent of 
			// the next free position. See next 
			// while-loop.
		}
		else {
			// path is empty. 
			// This means, cursor is the root.
			assert cursor == root;
			// In this case, last is the most 
			// right node in the last level.
			// The node in the left-lower corner 
			// in the whole tree is the parent of 
			// the next free position. 
			path.addFirst(root);
			// See next while-loop. 
		}
		cursor = path.getFirst().getLeft();
		while(cursor!=null) {
			path.addFirst(cursor);
			cursor = cursor.getLeft();
		}
	}
	
	protected void setPathToPreviousPosition() {
		assert size>0;
		assert !path.isEmpty();
		TreeHeapNode<E> cursor = path.removeFirst();
		while(!path.isEmpty()) {
			TreeHeapNode<E> parent = path.getFirst();
			assert parent.getLeft()==cursor || parent.getRight()==cursor; 
			if(parent.getRight()== cursor) {
				break;
			} 
			cursor = path.removeFirst();
		}
		if(!path.isEmpty()) {
			// Now we go into the left sub-tree.
			TreeHeapNode<E> parent = path.getFirst();
			assert parent.getLeft()!=null;
			path.addFirst(parent.getLeft());
			// The node in the right-lower corner 
			// in this sub-tree is the parent of 
			// the previous position. See next 
			// while-loop.
		}
		else {
			// parent is null. 
			// This means, cursor is the root.
			assert cursor==root;
			// In this case, last is the most 
			// left node in the last level.
			// The node in the left-lower corner 
			// in the whole tree is the parent of 
			// the next free position. 
			path.addFirst(root);
			// See next while-loop. 
		}
		cursor = path.getFirst().getRight();
		while(cursor!=null) {
			path.addFirst(cursor);
			cursor = cursor.getRight();
		}
	}

	public E peek() {
		if(root == null) {
			assert isEmpty();
			return null;
		}
		return root.getElement();
	}
	
	public E remove() {
		if(root == null) {
			assert isEmpty();
			return null;
		}
		E topElement = root.getElement(); 
		if(size==1) {
			assert path.size()==1;
			root = null;
			path.removeFirst();
			assert path.isEmpty();
			size = 0;
		}
		else {
			assert size > 1;
			assert path.size()>1;
			TreeHeapNode<E> last = path.removeFirst();
			root.setElement(last.getElement());
			assert !path.isEmpty();
			TreeHeapNode<E> parent = path.getFirst();
			if(parent.getRight()==last) {
				parent.removeRight();
				assert parent.getLeft()!=null;
				path.addFirst(parent.getLeft());
			}
			else {
				parent.removeLeft();
				setPathToPreviousPosition();
				assert !path.isEmpty();
			}
			size--;
			heapDown();
		}
		return topElement;
	}
	
	

	public TreeHeapNode<E> getRoot() {
		return root;
	}
	
	public int size() {
		return size;
	}
	
	public boolean isEmpty() {
		return size==0;
	}
	
	public void clear() {
		root = null;
		path.clear();
		size = 0;
	}
	
	public boolean contains(E element) {
		LinkedList<TreeHeapNode<E>> p = getPathTo(element);
		return !p.isEmpty();
	}
	
	
	public boolean refresh(E element) {
		LinkedList<TreeHeapNode<E>> p = getPathTo(element);
		if(p.isEmpty()) return false;
//		if(!p.getFirst().getElement().equals(element)) {
//			System.out.println("e="+element);
//			System.out.println("p="+p);
//			System.out.println(printInLevels());
//		}
		assert p.getFirst().getElement().equals(element);
		TreeHeapNode<E> node = p.getFirst();
		if(shouldHeapDown(node)) {
			heapDown(node);
		}
		else {
			heapUp(p);
		}
		return true;
	}
	
	public LinkedList<TreeHeapNode<E>> getPathTo(E element) {
		LinkedList<TreeHeapNode<E>> p = new LinkedList<TreeHeapNode<E>>();
		if(root==null) return p; 
		p.addFirst(root);
		boolean b = dfs(p, element);
		if(b) return p; 
		else {
			p.removeFirst();
			assert p.isEmpty();
			return p;
		}
	}
	
	private boolean dfs(LinkedList<TreeHeapNode<E>> stack, E element) {
		TreeHeapNode<E> node = stack.getFirst();
		if(node.getElement().equals(element)) return true;
//		int x = comparator.compare(node.getElement(), element);
//		if(x==0) return true;
//		if(x>0) return false;
		if(node.getLeft()!=null) {
			stack.addFirst(node.getLeft());
			boolean b = dfs(stack, element);
			if(b) return true;
			else stack.removeFirst();
		} 
		if(node.getRight()!=null) {
			stack.addFirst(node.getRight());
			boolean b = dfs(stack, element);
			if(b) return true;
			else stack.removeFirst();
		}
		return false;
	}
	
	
	public LinkedList<TreeHeapNode<E>> getPathToNode(TreeHeapNode<E> n) {
		LinkedList<TreeHeapNode<E>> p = new LinkedList<TreeHeapNode<E>>();
		if(root==null) return p; 
		p.addFirst(root);
		boolean b = dfs(p, n);
		if(b) return p; 
		else {
			p.removeFirst();
			assert p.isEmpty();
			return p;
		}
	}
	
	private boolean dfs(LinkedList<TreeHeapNode<E>> stack,  TreeHeapNode<E> n) {
		TreeHeapNode<E> node = stack.getFirst();
		if(n==node) return true;
//		int x = comparator.compare(node.getElement(), n.getElement());
//		if(x>0) return false;
		if(node.getLeft()!=null) {
			stack.addFirst(node.getLeft());
			boolean b = dfs(stack, n);
			if(b) return true;
			else stack.removeFirst();
		} 
		if(node.getRight()!=null) {
			stack.addFirst(node.getRight());
			boolean b = dfs(stack, n);
			if(b) return true;
			else stack.removeFirst();
		}
		return false;
	}
	
	
	public String printInLevels() {
		StringBuilder sb = new StringBuilder();
		if (root == null) {
			sb.append("Empty Heap\n");
			return sb.toString();
		}
		LinkedList<Object> q = new LinkedList<Object>();
		Object newLevelBound = new Object();
		q.addLast(root);
		q.add(newLevelBound);
		int level = 0;
		int levelCount = 0;
		while (!q.isEmpty()) {
			Object o = q.removeFirst();
			if (o == newLevelBound) {
				boolean ok = levelCount == (int) Math.pow(2, level);
				boolean lastLevel = q.isEmpty();
				assert lastLevel || ok;
				sb.append(" (#");sb.append(levelCount);sb.append(")\n");
				if(!lastLevel) {
					q.addLast(o);
				}
				level++;
				levelCount = 0;
			} else {
				levelCount++;
				TreeHeapNode<E> n = (TreeHeapNode<E>) o;
				sb.append(n);sb.append(" \t");
				TreeHeapNode<E> l = n.getLeft();
				TreeHeapNode<E> r = n.getRight();
				if (l != null)
					q.addLast(l);
				if (r != null)
					q.addLast(r);
			}
		}
		sb.append("\n");
		return sb.toString();
	}

}
