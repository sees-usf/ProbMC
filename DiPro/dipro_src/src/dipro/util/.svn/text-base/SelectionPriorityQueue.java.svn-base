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

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class SelectionPriorityQueue<E> extends PriorityQueue<E> {

	private static final long serialVersionUID = 1L;

	protected LinkedList<E> selection;

	public SelectionPriorityQueue(Comparator<E> comparator) {
		super(100, comparator);
		selection = new LinkedList<E>();
	}

	public E peek() {
		if (selection.isEmpty())
			return super.peek();
		return selection.get(0);
	}

	public synchronized E poll() {
		if (selection.isEmpty())
			return super.poll();
		E e = selection.remove(0);
		this.remove(e);
		return e;
	}

	@Override
	public synchronized boolean add(E e) {
		return super.add(e);
	}

	@Override
	public synchronized boolean offer(E e) {
		return super.offer(e);
	}

	public synchronized boolean remove(Object o) {
		boolean a = selection.remove(o);
		boolean b = super.remove(o);
		assert !a || b;
		assert !contains(o);
		return b;
	}

	public synchronized void select(Collection<E> el) {
		assert containsAll(el);
		selection.clear();
		selection.addAll(el);
	}

	public synchronized void select(E e) {
		assert contains(e);
		selection.clear();
		selection.add(e);
	}
}
