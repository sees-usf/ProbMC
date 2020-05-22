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

import java.util.Arrays;
import java.util.Comparator;
import java.util.Queue;


public class Heap<E> {

	private static final int DEFAULT_INITIAL_CAPACITY = 11;

	/**
	 * The Heap is a complete heap-ordered binary tree realised here using an
	 * array: the two children of queue[n] are queue[2*n+1] and queue[2*(n+1)].
	 * The priority queue is ordered by comparator, or by the elements' natural
	 * ordering, if comparator is null: For each node n in the heap and each
	 * descendant d of n, n <= d. The element with the lowest value is in
	 * queue[0], assuming the queue is nonempty.
	 */
	private transient Object[] queue;

	/**
	 * The number of elements in the priority queue.
	 */
	private int size = 0;

	/**
	 * The comparator, or null if priority queue uses elements' natural
	 * ordering.
	 */
	private final Comparator<? super E> comparator;

	/**
	 * The number of times this priority queue has been <i>structurally modified</i>.
	 * See AbstractList for gory details.
	 */
	private transient int modCount = 0;

	/**
	 * Creates a {@code PriorityQueue} with the default initial capacity (11)
	 * that orders its elements according to their
	 * {@linkplain Comparable natural ordering}.
	 */
	public Heap() {
		this(DEFAULT_INITIAL_CAPACITY, null);
	}

	/**
	 * Creates a {@code Heap} with the specified initial capacity that orders
	 * its elements according to their {@linkplain Comparable natural ordering}.
	 * 
	 * @param initialCapacity
	 *            the initial capacity for this priority queue
	 * @throws IllegalArgumentException
	 *             if {@code initialCapacity} is less than 1
	 */
	public Heap(int initialCapacity) {
		this(initialCapacity, null);
	}

	/**
	 * Creates a {@code Heap} with the specified initial capacity that orders
	 * its elements according to the specified comparator.
	 * 
	 * @param initialCapacity
	 *            the initial capacity for this priority queue
	 * @param comparator
	 *            the comparator that will be used to order this priority queue.
	 *            If {@code null}, the {@linkplain Comparable natural ordering}
	 *            of the elements will be used.
	 * @throws IllegalArgumentException
	 *             if {@code initialCapacity} is less than 1
	 */
	public Heap(int initialCapacity, Comparator<? super E> comparator) {
		this.queue = new Object[initialCapacity];
		this.comparator = comparator;
	}

	// /**
	// * Creates a {@code PriorityQueue} containing the elements in the
	// * specified collection. If the specified collection is an instance of
	// * a {@link SortedSet} or is another {@code PriorityQueue}, this
	// * priority queue will be ordered according to the same ordering.
	// * Otherwise, this priority queue will be ordered according to the
	// * {@linkplain Comparable natural ordering} of its elements.
	// *
	// * @param c the collection whose elements are to be placed
	// * into this priority queue
	// * @throws ClassCastException if elements of the specified collection
	// * cannot be compared to one another according to the priority
	// * queue's ordering
	// * @throws NullPointerException if the specified collection or any
	// * of its elements are null
	// */
	// public PriorityQueue(Collection<? extends E> c) {
	// initFromCollection(c);
	// if (c instanceof SortedSet)
	// comparator = (Comparator<? super E>)
	// ((SortedSet<? extends E>)c).comparator();
	// else if (c instanceof PriorityQueue)
	// comparator = (Comparator<? super E>)
	// ((PriorityQueue<? extends E>)c).comparator();
	// else {
	// comparator = null;
	// heapify();
	// }
	// }
	//
	// /**
	// * Creates a {@code PriorityQueue} containing the elements in the
	// * specified priority queue. This priority queue will be
	// * ordered according to the same ordering as the given priority
	// * queue.
	// *
	// * @param c the priority queue whose elements are to be placed
	// * into this priority queue
	// * @throws ClassCastException if elements of {@code c} cannot be
	// * compared to one another according to {@code c}'s
	// * ordering
	// * @throws NullPointerException if the specified priority queue or any
	// * of its elements are null
	// */
	// public PriorityQueue(PriorityQueue<? extends E> c) {
	// comparator = (Comparator<? super E>)c.comparator();
	// initFromCollection(c);
	// }
	//
	// /**
	// * Creates a {@code PriorityQueue} containing the elements in the
	// * specified sorted set. This priority queue will be ordered
	// * according to the same ordering as the given sorted set.
	// *
	// * @param c the sorted set whose elements are to be placed
	// * into this priority queue
	// * @throws ClassCastException if elements of the specified sorted
	// * set cannot be compared to one another according to the
	// * sorted set's ordering
	// * @throws NullPointerException if the specified sorted set or any
	// * of its elements are null
	// */
	// public PriorityQueue(SortedSet<? extends E> c) {
	// comparator = (Comparator<? super E>)c.comparator();
	// initFromCollection(c);
	// }

	// /**
	// * Initializes queue array with elements from the given Collection.
	// *
	// * @param c the collection
	// */
	// private void initFromCollection(Collection<? extends E> c) {
	// Object[] a = c.toArray();
	// // If c.toArray incorrectly doesn't return Object[], copy it.
	// if (a.getClass() != Object[].class)
	// a = Arrays.copyOf(a, a.length, Object[].class);
	// queue = a;
	// size = a.length;
	// }

	/**
	 * Increases the capacity of the array.
	 * 
	 * @param minCapacity
	 *            the desired minimum capacity
	 */
	private void grow(int minCapacity) {
		if (minCapacity < 0) // overflow
			throw new OutOfMemoryError();
		int oldCapacity = queue.length;
		// Double size if small; else grow by 50%
		int newCapacity = ((oldCapacity < 64) ? ((oldCapacity + 1) * 2)
				: ((oldCapacity / 2) * 3));
		if (newCapacity < 0) // overflow
			newCapacity = Integer.MAX_VALUE;
		if (newCapacity < minCapacity)
			newCapacity = minCapacity;
		queue = Arrays.copyOf(queue, newCapacity);
	}

	/**
	 * Inserts the specified element into this priority queue.
	 * 
	 * @return {@code true} (as specified by {@link Queue#offer})
	 * @throws ClassCastException
	 *             if the specified element cannot be compared with elements
	 *             currently in this priority queue according to the priority
	 *             queue's ordering
	 * @throws NullPointerException
	 *             if the specified element is null
	 */
	public boolean offer(E e) {
		if (e == null)
			throw new NullPointerException();
		modCount++;
		int i = size;
		if (i >= queue.length)
			grow(i + 1);
		size = i + 1;
		if (i == 0)
			queue[0] = e;
		else
			siftUp(i, e);
		return true;
	}

	@SuppressWarnings("unchecked")
	public E peek() {
		if (size == 0)
			return null;
		return (E) queue[0];
	}

	@SuppressWarnings("unchecked")
	public HeapNode<E> nodeOf(E e) {
		int i = indexOf(e);
		if (i < 0)
			return null;
		return new HeapNode<E>(i, (E)queue[i]);
	}

	protected int indexOf(Object o) {
		if (o != null) {
			for (int i = 0; i < size; i++)
				if (o.equals(queue[i]))
					return i;
		}
		return -1;
	}

	protected int indexOfEq(Object o) {
		if (o != null) {
			for (int i = 0; i < size; i++)
				if (o == queue[i])
					return i;
		}
		return -1;
	}

	@SuppressWarnings("unchecked")
	protected E elementAt(int index) {
		assert index>=0 && index< size;
		return (E)queue[index];
	}
	/**
	 * Removes a single instance of the specified element from this queue, if it
	 * is present. More formally, removes an element {@code e} such that
	 * {@code o.equals(e)}, if this queue contains one or more such elements.
	 * Returns {@code true} if and only if this queue contained the specified
	 * element (or equivalently, if this queue changed as a result of the call).
	 * 
	 * @param o
	 *            element to be removed from this queue, if present
	 * @return {@code true} if this queue changed as a result of the call
	 */
	public boolean remove(Object o) {
		int i = indexOf(o);
		if (i == -1)
			return false;
		else {
			removeAt(i);
			return true;
		}
	}

	/**
	 * Version of remove using reference equality, not equals. Needed by
	 * iterator.remove.
	 * 
	 * @param o
	 *            element to be removed from this queue, if present
	 * @return {@code true} if removed
	 */
	boolean removeEq(Object o) {
		for (int i = 0; i < size; i++) {
			if (o == queue[i]) {
				removeAt(i);
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns {@code true} if this queue contains the specified element. More
	 * formally, returns {@code true} if and only if this queue contains at
	 * least one element {@code e} such that {@code o.equals(e)}.
	 * 
	 * @param o
	 *            object to be checked for containment in this queue
	 * @return {@code true} if this queue contains the specified element
	 */
	public boolean contains(Object o) {
		return indexOf(o) != -1;
	}
	
	@SuppressWarnings("unchecked")
	public HeapNode<E> get(Object o) {
		int index = indexOf(o); 
		HeapNode<E> node = null;
		if(index >= 0 && index < size) {
			node = new HeapNode<E>(index, (E)queue[index]);
		}
		return node;
	}

	/**
	 * Returns an array containing all of the elements in this queue. The
	 * elements are in no particular order.
	 * 
	 * <p>
	 * The returned array will be "safe" in that no references to it are
	 * maintained by this queue. (In other words, this method must allocate a
	 * new array). The caller is thus free to modify the returned array.
	 * 
	 * <p>
	 * This method acts as bridge between array-based and collection-based APIs.
	 * 
	 * @return an array containing all of the elements in this queue
	 */
	public Object[] toArray() {
		return Arrays.copyOf(queue, size);
	}

	/**
	 * Returns an array containing all of the elements in this queue; the
	 * runtime type of the returned array is that of the specified array. The
	 * returned array elements are in no particular order. If the queue fits in
	 * the specified array, it is returned therein. Otherwise, a new array is
	 * allocated with the runtime type of the specified array and the size of
	 * this queue.
	 * 
	 * <p>
	 * If the queue fits in the specified array with room to spare (i.e., the
	 * array has more elements than the queue), the element in the array
	 * immediately following the end of the collection is set to {@code null}.
	 * 
	 * <p>
	 * Like the {@link #toArray()} method, this method acts as bridge between
	 * array-based and collection-based APIs. Further, this method allows
	 * precise control over the runtime type of the output array, and may, under
	 * certain circumstances, be used to save allocation costs.
	 * 
	 * <p>
	 * Suppose <tt>x</tt> is a queue known to contain only strings. The
	 * following code can be used to dump the queue into a newly allocated array
	 * of <tt>String</tt>:
	 * 
	 * <pre>
	 * String[] y = x.toArray(new String[0]);
	 * </pre>
	 * 
	 * Note that <tt>toArray(new Object[0])</tt> is identical in function to
	 * <tt>toArray()</tt>.
	 * 
	 * @param a
	 *            the array into which the elements of the queue are to be
	 *            stored, if it is big enough; otherwise, a new array of the
	 *            same runtime type is allocated for this purpose.
	 * @return an array containing all of the elements in this queue
	 * @throws ArrayStoreException
	 *             if the runtime type of the specified array is not a supertype
	 *             of the runtime type of every element in this queue
	 * @throws NullPointerException
	 *             if the specified array is null
	 */
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		if (a.length < size)
			// Make a new array of a's runtime type, but my contents:
			return (T[]) Arrays.copyOf(queue, size, a.getClass());
		System.arraycopy(queue, 0, a, 0, size);
		if (a.length > size)
			a[size] = null;
		return a;
	}

	/**
	 * Creates a shallow of the Heap
	 * 
	 * @return a shallow copy of the Heap
	 */
	public Heap<E> copy() {
		Heap<E> cpy = new Heap<E>(1, this.comparator);
		fillCopy(cpy);
		return cpy;
	}

	protected void fillCopy(Heap<E> cpyHeap) {
		cpyHeap.queue = Arrays.copyOf(this.queue, this.queue.length);
		cpyHeap.modCount = this.modCount;
		cpyHeap.size = this.size;
	}
	
	// /**
	// * Returns an iterator over the elements in this queue. The iterator
	// * does not return the elements in any particular order.
	// *
	// * @return an iterator over the elements in this queue
	// */
	// public Iterator<E> iterator() {
	// return new Itr();
	// }
	//
	// private final class Itr implements Iterator<E> {
	// /**
	// * Index (into queue array) of element to be returned by
	// * subsequent call to next.
	// */
	// private int cursor = 0;
	//
	// /**
	// * Index of element returned by most recent call to next,
	// * unless that element came from the forgetMeNot list.
	// * Set to -1 if element is deleted by a call to remove.
	// */
	// private int lastRet = -1;
	//
	// /**
	// * A queue of elements that were moved from the unvisited portion of
	// * the heap into the visited portion as a result of "unlucky" element
	// * removals during the iteration. (Unlucky element removals are those
	// * that require a siftup instead of a siftdown.) We must visit all of
	// * the elements in this list to complete the iteration. We do this
	// * after we've completed the "normal" iteration.
	// *
	// * We expect that most iterations, even those involving removals,
	// * will not need to store elements in this field.
	// */
	// private ArrayDeque<E> forgetMeNot = null;
	//
	// /**
	// * Element returned by the most recent call to next iff that
	// * element was drawn from the forgetMeNot list.
	// */
	// private E lastRetElt = null;
	//
	// /**
	// * The modCount value that the iterator believes that the backing
	// * Queue should have. If this expectation is violated, the iterator
	// * has detected concurrent modification.
	// */
	// private int expectedModCount = modCount;
	//
	// public boolean hasNext() {
	// return cursor < size ||
	// (forgetMeNot != null && !forgetMeNot.isEmpty());
	// }
	//
	// public E next() {
	// if (expectedModCount != modCount)
	// throw new ConcurrentModificationException();
	// if (cursor < size)
	// return (E) queue[lastRet = cursor++];
	// if (forgetMeNot != null) {
	// lastRet = -1;
	// lastRetElt = forgetMeNot.poll();
	// if (lastRetElt != null)
	// return lastRetElt;
	// }
	// throw new NoSuchElementException();
	// }
	//
	// public void remove() {
	// if (expectedModCount != modCount)
	// throw new ConcurrentModificationException();
	// if (lastRet != -1) {
	// E moved = PriorityQueue.this.removeAt(lastRet);
	// lastRet = -1;
	// if (moved == null)
	// cursor--;
	// else {
	// if (forgetMeNot == null)
	// forgetMeNot = new ArrayDeque<E>();
	// forgetMeNot.add(moved);
	// }
	// } else if (lastRetElt != null) {
	// PriorityQueue.this.removeEq(lastRetElt);
	// lastRetElt = null;
	// } else {
	// throw new IllegalStateException();
	// }
	// expectedModCount = modCount;
	// }
	// }

	public int size() {
		return size;
	}

	/**
	 * Removes all of the elements from this priority queue. The queue will be
	 * empty after this call returns.
	 */
	public void clear() {
		modCount++;
		for (int i = 0; i < size; i++)
			queue[i] = null;
		size = 0;
	}

	@SuppressWarnings("unchecked")
	public E poll() {
		if (size == 0)
			return null;
		int s = --size;
		modCount++;
		E result = (E) queue[0];
		E x = (E) queue[s];
		queue[s] = null;
		if (s != 0)
			siftDown(0, x);
		return result;
	}

	/**
	 * Removes the ith element from queue.
	 * 
	 * Normally this method leaves the elements at up to i-1, inclusive,
	 * untouched. Under these circumstances, it returns null. Occasionally, in
	 * order to maintain the heap invariant, it must swap a later element of the
	 * list with one earlier than i. Under these circumstances, this method
	 * returns the element that was previously at the end of the list and is now
	 * at some position before i. This fact is used by iterator.remove so as to
	 * avoid missing traversing elements.
	 */
	@SuppressWarnings("unchecked")
	private E removeAt(int i) {
		assert i >= 0 && i < size;
		modCount++;
		int s = --size;
		if (s == i) // removed last element
			queue[i] = null;
		else {
			E moved = (E) queue[s];
			queue[s] = null;
			siftDown(i, moved);
			if (queue[i] == moved) {
				siftUp(i, moved);
				if (queue[i] != moved)
					return moved;
			}
		}
		return null;
	}

	/**
	 * Inserts item x at position k, maintaining heap invariant by promoting x
	 * up the tree until it is greater than or equal to its parent, or is the
	 * root.
	 * 
	 * To simplify and speed up coercions and comparisons. the Comparable and
	 * Comparator versions are separated into different methods that are
	 * otherwise identical. (Similarly for siftDown.)
	 * 
	 * @param k
	 *            the position to fill
	 * @param x
	 *            the item to insert
	 */
	private void siftUp(int k, E x) {
		if (comparator != null)
			siftUpUsingComparator(k, x);
		else
			siftUpComparable(k, x);
	}

	@SuppressWarnings("unchecked")
	private void siftUpComparable(int k, E x) {
		Comparable<? super E> key = (Comparable<? super E>) x;
		while (k > 0) {
			int parent = (k - 1) >>> 1;
			Object e = queue[parent];
			if (key.compareTo((E) e) >= 0)
				break;
			queue[k] = e;
			k = parent;
		}
		queue[k] = key;
	}

	@SuppressWarnings("unchecked")
	private void siftUpUsingComparator(int k, E x) {
		while (k > 0) {
			int parent = (k - 1) >>> 1;
			Object e = queue[parent];
			if (comparator.compare(x, (E) e) >= 0)
				break;
			queue[k] = e;
			k = parent;
		}
		queue[k] = x;
	}

	/**
	 * Inserts item x at position k, maintaining heap invariant by demoting x
	 * down the tree repeatedly until it is less than or equal to its children
	 * or is a leaf.
	 * 
	 * @param k
	 *            the position to fill
	 * @param x
	 *            the item to insert
	 */
	private void siftDown(int k, E x) {
		if (comparator != null)
			siftDownUsingComparator(k, x);
		else
			siftDownComparable(k, x);
	}

	@SuppressWarnings("unchecked")
	private void siftDownComparable(int k, E x) {
		Comparable<? super E> key = (Comparable<? super E>) x;
		int half = size >>> 1; // loop while a non-leaf
		while (k < half) {
			int child = (k << 1) + 1; // assume left child is least
			Object c = queue[child];
			int right = child + 1;
			if (right < size
					&& ((Comparable<? super E>) c).compareTo((E) queue[right]) > 0)
				c = queue[child = right];
			if (key.compareTo((E) c) <= 0)
				break;
			queue[k] = c;
			k = child;
		}
		queue[k] = key;
	}

	@SuppressWarnings("unchecked")
	private void siftDownUsingComparator(int k, E x) {
		int half = size >>> 1;
		while (k < half) {
			int child = (k << 1) + 1;
			Object c = queue[child];
			int right = child + 1;
			if (right < size && comparator.compare((E) c, (E) queue[right]) > 0)
				c = queue[child = right];
			if (comparator.compare(x, (E) c) <= 0)
				break;
			queue[k] = c;
			k = child;
		}
		queue[k] = x;
	}

	// /**
	// * Establishes the heap invariant (described above) in the entire tree,
	// * assuming nothing about the order of the elements prior to the call.
	// */
	// @SuppressWarnings("unchecked")
	// private void heapify() {
	// for (int i = (size >>> 1) - 1; i >= 0; i--)
	// siftDown(i, (E) queue[i]);
	// }

	/**
	 * Returns the comparator used to order the elements in this queue, or
	 * {@code null} if this queue is sorted according to the
	 * {@linkplain Comparable natural ordering} of its elements.
	 * 
	 * @return the comparator used to order this queue, or {@code null} if this
	 *         queue is sorted according to the natural ordering of its elements
	 */
	public Comparator<? super E> comparator() {
		return comparator;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("Heap[");
		for (int i = 0; i < size; i++) {
			if (i != 0)
				sb.append(", ");
			sb.append(queue[i]);
		}
		sb.append("]");
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	public HeapNode<E> getRootNode() {
		if (size == 0)
			return null;
		return new HeapNode<E>(0, (E)queue[0]);
	}
	
	@SuppressWarnings("unchecked")
	public HeapNode<E> getParent(HeapNode<E> node) {
		assert checkIndexValidity(node);
		int index = node.getIndex();
		assert index>=0 && index<size;
		if (index == 0)
			return null;
		int parentIndex = computeParentIndex(index);
		return new HeapNode<E>(parentIndex, (E)queue[parentIndex]);
	}

	@SuppressWarnings("unchecked")
	public HeapNode<E> getLeft(HeapNode<E> node) {
		assert checkIndexValidity(node);
		// The two children of queue[n] are queue[2*n+1] and queue[2*(n+1)]
		int index = node.getIndex();
		assert index>=0 && index<size;
		int leftIndex = computeLeftIndex(index);
		if (leftIndex >= size)
			return null;
		return new HeapNode<E>(leftIndex, (E)queue[leftIndex]);
	}

	@SuppressWarnings("unchecked")
	public HeapNode<E> getRight(HeapNode<E> node) {
		assert checkIndexValidity(node);
		// The two children of queue[n] are queue[2*n+1] and queue[2*(n+1)]
		int index = node.getIndex();
		assert index>=0 && index<size;
		int rightIndex = computeRightIndex(index);
		if (rightIndex >= size)
			return null;
		return new HeapNode<E>(rightIndex, (E)queue[rightIndex]);
	}
	
	public boolean isALeafElement(E element) {
		int index = indexOf(element);
		int leftIndex = computeLeftIndex(index);
		int rightIndex = computeRightIndex(index);
		if(leftIndex>= size && rightIndex>=size) return true;
		return false;
	}
	
	protected int computeParentIndex(int index) {
		assert index >0 && index<size; 
		return (index - 1) >>> 1;
	}
	
	protected int computeLeftIndex(int index) {
		assert index >=0 && index<size; 
		return 2 * index + 1;
	}
	
	protected int computeRightIndex(int index) {
		assert index >=0 && index<size; 
		return 2 * (index + 1);
	}

	public boolean checkIndexValidity(HeapNode<E> node) {
		int index = node.getIndex();
		assert index>=0 && index<size;
		E element1 = node.getElement();
		E element2 = elementAt(index);
		boolean ok = element1.equals(element2);
		return ok;
	}

}
