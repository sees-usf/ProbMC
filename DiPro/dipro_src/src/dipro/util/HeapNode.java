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


public class HeapNode<E> {
	
	private E element;
	private int index;

	protected HeapNode(int index, E element) {
		this.index = index;
		this.element = element;
	}

	public E getElement() {
		return element;
	}

	public int getIndex() {
		return index;
	}
	
	
//	public int getParentIndex() {
//		if (index == 0)	return -1;
//		return (index - 1) >>> 1;
//	}
//
//		public int getLeftIndex() {
//			// The two children of queue[n] are queue[2*n+1] and queue[2*(n+1)]
//			int leftIndex = 2 * index + 1;
//			return leftIndex;
//		}
//
//		public int getRight() {
//			// The two children of queue[n] are queue[2*n+1] and queue[2*(n+1)]
//			int rightIndex = 2 * (index + 1);
//			return rightIndex;
//		}
//
//		boolean checkIndexValidity() {
//			boolean ok = indexOfEq(element) == index;
//			return ok;
//		}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final HeapNode<E> other = (HeapNode<E>) obj;
		if (element == null) {
			if (other.element != null)
				return false;
		} else if (!element.equals(other.element))
			return false;
		return true;
	}

	public String toString() {
		return element.toString();
	}
}
