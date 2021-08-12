package app.pq;

import java.util.*;

public class PriorityQueue<T>
{
	int capacity = 2;
	int used = 0;
	Object[] items = new Object[capacity];
	final Comparator<T> cmp;

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
			return (Node) pq.items[index * 2];
		}

		Node right()
		{
			return (Node) pq.items[index * 2 + 1];
		}

		Node ancestor()
		{
			return (Node) pq.items[(index - index % 2) / 2];
		}

		public T value()
		{
			return val;
		}
	}

	public PriorityQueue(Comparator<T> cmp)
	{
		this.cmp = cmp;
	}

	public Node push(T item)
	{
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
		used++;

		fix(node);

		return node;
	}

	public T pop()
	{
		if (used == 0)
			return null;

		return delete((Node) items[1]);
	}

	public T delete(Node node)
	{
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
