package app.tree;

import java.util.Comparator;

public class Tree<T>
{
	public class Node
	{
		final T val;

		Node(T value)
		{
			this.val = value;
		}

		Node ancestor = null;
		Node left = null;
		Node right = null;

		public T value()
		{
			return val;
		}

		public String toString()
		{
			return "[ "+ val.toString() +" ] [A]: "
				+ (ancestor == null ? "null" : ancestor.val.toString())
				+ "; [L]: "
				+ (left == null ? "null" : left.val.toString())
				+ "; [R]: "
				+ (right == null ? "null" : right.val.toString());
		}
	}

	final Comparator<T> cmp;
	Node root = null;
	int count = 0;

	public Tree(Comparator<T> cmp)
	{
		this.cmp = cmp;
	}

	public T delete(T item)
	{
		return delete(find(item));
	}

	public T delete(Node node)
	{
		if (node == null)
			return null;

		count--;

		if (node.left != null && node.right != null)
		{
			Node n = next(node);
			swap(n, node);
		}

		if (node.left != null)
		{
			swap(node, node.left);
			node.ancestor.left = null;
			node.ancestor = null;
			return node.value();
		}

		if (node.right != null)
		{
			swap(node, node.right);
			node.ancestor.right = null;
			node.ancestor = null;
			return node.value();
		}

		if (node.ancestor == null)
		{
			root = null;
			return node.value();
		}

		if (node == node.ancestor.left)
			node.ancestor.left = null;
		else
			node.ancestor.right = null;

		node.ancestor = null;
		return node.value();
	}

	public Node next(Node node)
	{
		return next(node.value());
	}

	public Node previous(Node node)
	{
		return previous(node.value());
	}

	public Node add(T item)
	{
		Node node = new Node(item);
		count++;

		if (root == null)
		{
			root = node;
			return node;
		}

		Node previous = null;
		Node current = root;
		int comparison = 0;

		while (current != null)
		{
			previous = current;
			comparison = cmp.compare(item, current.value());

			if (comparison <= 0)
				current = current.left;
			else
				current = current.right;
		}

		if (comparison <= 0)
			previous.left = node;
		else
			previous.right = node;

		node.ancestor = previous;

		return node;
	}

	public Node next(T item)
	{
		Node result = null;
		Node current = root;

		while (current != null)
		{
			int comparison = cmp.compare(item, current.value());

			if (comparison == 1)
				result = current;

			if (comparison == -1)
				current = current.left;
			else
				current = current.right;
		}

		return result;
	}

	public Node previous(T item)
	{
		Node result = null;
		Node current = root;

		while (current != null)
		{
			int comparison = cmp.compare(item, current.value());

			if (comparison == -1)
				result = current;

			if (comparison == 1)
				current = current.left;
			else
				current = current.right;
		}

		return result;
	}

	public Node find(T item)
	{
		Node current = root;

		while (current != null)
		{
			int comparison = cmp.compare(item, current.value());

			if (comparison == 0)
				return current;

			if (comparison == -1)
				current = current.left;
			else
				current = current.right;
		}

		return null;
	}

	void swap(Node a, Node b)
	{
		Node tLeft = a.left;
		Node tRight = a.right;
		Node tAncestor = a.ancestor;

		a.left = b.left;
		a.right = b.right;
		a.ancestor = b.ancestor;

		b.left = tLeft;
		b.right = tRight;
		b.ancestor = tAncestor;

		if (b.left != null)
			b.left.ancestor = b;
		if (b.right != null)
			b.right.ancestor = b;

		if (b.ancestor != null)
		{
			if (b.ancestor.left == a)
				b.ancestor.left = b;
			else if (b.ancestor.right == a)
				b.ancestor.right = b;
		}

		if (a.left != null)
			a.left.ancestor = a;
		if (a.right != null)
			a.right.ancestor = a;

		if (a.ancestor != null)
		{
			if (a.ancestor.left == b)
				a.ancestor.left = a;
			else if (a.ancestor.right == b)
				a.ancestor.right = a;
		}

		if (root == a)
			root = b;
		else if (root == b)
			root = a;
	}
}
