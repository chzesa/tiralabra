package app.pq;

import java.util.*;
import app.util.*;

public class PriorityQueue<T>
{
	int capacity = 32;
	int used = 0;
	Object[] items = new Object[capacity];
	final Comparator<T> cmp;

	/**
	 * A class representing items in the queue.
	 */
	public class Node
	{
		int index;
		final T val;
		final PriorityQueue<T> pq;

		Node(int index, T value, PriorityQueue<T> pq)
		{
			this.index = index;
			this.val = value;
			this.pq = pq;
		}

		Node left()
		{
			if (index * 2 > pq.used)
				return null;

			return (Node) pq.items[index * 2];
		}

		Node right()
		{
			if (index * 2 + 1 > pq.used)
				return null;

			return (Node) pq.items[index * 2 + 1];
		}

		Node ancestor()
		{
			return (Node) pq.items[(index - index % 2) / 2];
		}

		/**
		 * Get the item this node represents.
		 */
		public T value()
		{
			return val;
		}
	}

	/**
	 * Constructs a priority queue.
	 * @param cmp A comparator object determining the queue ordering.
	 */
	public PriorityQueue(Comparator<T> cmp)
	{
		this.cmp = cmp;
	}

	/**
	 * Add an item into the priority queue.
	 * @return An object which can be used to delete the added value.
	 */
	public Node push(T item)
	{
		if (item == null)
			throw new IllegalArgumentException("Attempted to add null item.");

		used++;

		if (used == capacity - 1)
		{
			capacity *= 2;
			Object[] arr = new Object[capacity];
			for (int i = 0; i < used + 1; i++)
				arr[i] = items[i];

			items = arr;
		}

		Node node = new Node(used, item, this);
		items[used] = node;

		fix(node);

		return node;
	}

	/**
	 * Remove the first item from the queue.
	 */
	public T pop()
	{
		if (used == 0)
			return null;

		return delete((Node) items[1]);
	}

	/**
	 * Returns the first item in the queue without removing it.
	 */
	public T peek()
	{
		return ((Node) items[1]).value();
	}

	/**
	 * Deletes the item represented by the node from the queue.
	 */
	public T delete(Node node)
	{
		if (node == null)
			throw new IllegalArgumentException("Attempted to delete null node.");

		if (used == 0)
			return null;

		Node last = (Node) items[used];

		swap(node, last);
		items[used] = null;
		used--;

		fix(last);

		return node.value();
	}

	public int size()
	{
		return used;
	}

	public boolean isEmpty()
	{
		return size() == 0;
	}

	public void forEach(ICallback<T> fn)
	{
		for (int i = 1; i <= used; i++)
			fn.operation(((Node) items[i]).value());
	}

	/**
	 * While a node compares smaller than its immediate ancestor, the two are swapped.
	 * 
	 * Then, if a node compares greater than either one of its descendants, it's swapped
	 * with the one ordered first.
	 * 
	 * Due to the invariant of the data structure, only one of the loops does anything.
	 * Since a node can be deleted from any position in the tree, and this is done by swapping
	 * it with the very last node in the array, it's unknown whether the swapped-in node should
	 * be raised or lowered, so both are tried.
	 */
	void fix(Node node)
	{
		while (node.ancestor() != null)
		{
			if (cmp.compare(node.ancestor().value(), node.value()) <= 0)
				break;

			swap(node, node.ancestor());
		}

		while (node.left() != null)
		{
			Node compare;
			if (node.right() != null)
				compare = cmp.compare(node.left().value(), node.right().value()) <= 0 ? node.left() : node.right();
			else
				compare = node.left();

			if (cmp.compare(compare.value(), node.value()) >= 0)
				break;

			swap(node, compare);
		}
	}

	void swap(Node a, Node b)
	{
		int ixA, ixB;
		ixA = a.index;
		ixB = b.index;

		a.index = ixB;
		b.index = ixA;

		items[ixA] = b;
		items[ixB] = a;
	}
}
