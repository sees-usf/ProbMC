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

public class TreeHeapNode<E> {

	private E element;
	private TreeHeapNode<E> left;
	private TreeHeapNode<E> right;

	protected TreeHeapNode(E element) {
		this.element = element;
		left=null;
		right=null;
	}

	public TreeHeapNode<E> getLeft() {
		return left;
	}

	public void setLeft(TreeHeapNode<E> left) {
		assert left!=null;
		this.left = left;
	}

	public void removeLeft() {
		this.left = null;
	}
	
	public TreeHeapNode<E> getRight() {
		return right;
	}

	public void setRight(TreeHeapNode<E> right) {
		assert right!=null;
		this.right = right;
	}
	
	public void removeRight() {
		right = null;
	}

	public E getElement() {
		return element;
	}
	
	protected void setElement(E element) {
		assert element!=null;
		this.element = element;
	}
	
	public String toString() {
		return element.toString();
	}
}
