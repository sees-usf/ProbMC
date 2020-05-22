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

import dipro.graph.DirectedEdge;
import dipro.util.TreeHeapNode;

public class HeapTNode extends TreeHeapNode<TreeHeapNode<DirectedEdge>> {
//	protected Vertex vertex;
	
	HeapTNode(TreeHeapNode<DirectedEdge> heapInRoot) {
		super(heapInRoot);
//		this.vertex = v; 
	}
	
	public String toString() {
		return "HeapTNode {"+getElement().getElement()+"}";
	}

//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		final HeapTNode other = (HeapTNode) obj;
//		assert vertex!=null && other.vertex!=null;
//		if (!vertex.equals(other.vertex))
//			return false;
//		assert getElement()!= null && other.getElement()!=null;
//		DirectedEdge e1 = getElement().getElement();
//		DirectedEdge e2 = other.getElement().getElement();
//		assert e1!=null && e2!=null;
//		if(!e1.equals(e2)) {
//			return false;
//		}
//		return true;
//	}
//	
//	
//	@Override
//	public int hashCode() {
//		final int PRIME = 31;
//		int result = 1;
//		result = PRIME * result + ((vertex == null) ? 0 : vertex.hashCode());
//		DirectedEdge e = getElement().getElement();
//		result = PRIME * result + ((e == null) ? 0 : e.hashCode());
//		return result;
//	}
}
