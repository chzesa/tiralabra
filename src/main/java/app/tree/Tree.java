package app.tree;

import java.util.Comparator;
import app.util.*;

/**
 * Unbalanced binary tree.
 */
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
			return "[ " + val.toString() + " ] [A]: "
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

	/**
	 * Constructs a new tree.
	 * @param cmp A comparator determining the ordering of the tree.
	 */
	public Tree(Comparator<T> cmp)
	{
		this.cmp = cmp;
	}

	public boolean isEmpty()
	{
		return size() == 0;
	}

	public int size()
	{
		return this.count;
	}

	/**
	 * Delete a single node containing an equal item from the tree.
	 */
	public T delete(T item)
	{
		return delete(find(item));
	}

	/**
	 * Delete a single node from the tree.
	 */
	public T delete(Node node)
	{
		if (node == null)
			return null;

		count--;

		if (node.left != null && node.right != null)
			swap(next(node), node);

		Node child = node.left == null ? node.right : node.left;
		if (root == node)
			root = child;

		if (node.ancestor != null)
		{
			if (node.ancestor.left == node)
				node.ancestor.left = child;
			else
				node.ancestor.right = child;
		}

		if (child != null)
			child.ancestor = node.ancestor;

		node.ancestor = null;
		node.left = null;
		node.right = null;
		return node.value();
	}

	/**
	 * Add an item to the tree.
	 * @return An object representing the node which can be used for deletion.
	 */
	public Node add(T item)
	{
		if (item == null)
			return null;

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

	/**
	 * Returns the smallest contained item that is larger than the parameter.
	 */
	public Node next(Node node)
	{
		return next(node.value());
	}

	/**
	 * Returns the smallest contained item that is larger than the parameter.
	 */
	public Node next(T item)
	{
		Node result = null;
		Node current = root;

		while (current != null)
		{
			int comparison = cmp.compare(item, current.value());
			if (comparison == -1)
			{
				result = current;
				current = current.left;
			}
			else
			{
				current = current.right;
			}
		}

		return result;
	}

	/**
	 * Returns the largest contained item that is smaller than the parameter.
	 */
	public Node previous(Node node)
	{
		return previous(node.value());
	}

	/**
	 * Returns the largest contained item that is smaller than the parameter.
	 */
	public Node previous(T item)
	{
		Node result = null;
		Node current = root;

		while (current != null)
		{
			int comparison = cmp.compare(item, current.value());
			if (comparison == 1)
			{
				result = current;
				current = current.right;
			}
			else
			{
				current = current.left;
			}
		}

		return result;
	}

	/**
	 * Returns the greatest item smaller than the parameter, or an item which equals it.
	 */
	public Node floor(T item)
	{
		Node result = null;
		Node current = root;

		while (current != null)
		{
			int comparison = cmp.compare(item, current.value());
			if (comparison == 0)
				return current;

			if (comparison == 1)
			{
				result = current;
				current = current.right;
			}
			else
			{
				current = current.left;
			}
		}

		return result;
	}

	void forEachNode(Node n, ICallback<T> fn)
	{
		if (n == null)
			return;

		fn.operation(n.value());

		forEachNode(n.left, fn);
		forEachNode(n.right, fn);
	}

	public void forEach(ICallback<T> fn)
	{
		forEachNode(root, fn);
	}

	void checkAncestry(Node n)
	{
		if (n == null)
			return;

		if (n.left != null)
		{
			if (n.left.ancestor != n)
				throw new RuntimeException("Node " + n.left.value() + " has incorrect ancestor " + n.left.ancestor.value());
			checkAncestry(n.left);
		}
		if (n.right != null)
		{
			if (n.right.ancestor != n)
				throw new RuntimeException("Node " + n.right.value() + " has incorrect ancestor " + n.right.ancestor.value());
			checkAncestry(n.right);
		}
	}

	void checkLeft(Node n, T value)
	{
		if (n == null)
			return;

		if (cmp.compare(n.value(), value) == 1)
			throw new RuntimeException("Left subtree of " + value.toString() + " has a larger item.");

		checkLeft(n.left, value);
		checkLeft(n.right, value);
	}

	void checkRight(Node n, T value)
	{
		if (n == null)
			return;

		if (cmp.compare(value, n.value()) != -1)
			throw new RuntimeException("Right subtree of " + value.toString() + " has an equal or smaller item.");

		checkRight(n.left, value);
		checkRight(n.right, value);
	}

	void checkBalanced(Node n)
	{
		if (n == null)
			return;

		checkLeft(n.left, n.value());
		checkRight(n.right, n.value());

		checkBalanced(n.left);
		checkBalanced(n.right);
	}

	public void validate()
	{
		checkBalanced(root);
		checkAncestry(root);
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
		if (a == null || b == null)
			throw new IllegalArgumentException();

		Node aLeft = a.left;
		Node aRight = a.right;
		Node aAnc = a.ancestor;

		Node bLeft = b.left;
		Node bRight = b.right;
		Node bAnc = b.ancestor;

		if (aAnc != null)
		{
			if (aAnc.left == a)
				aAnc.left = b;
			else if (aAnc.right == a)
				aAnc.right = b;
		}

		if (bAnc != null)
		{
			if (bAnc.left == b)
				bAnc.left = a;
			else if (bAnc.right == b)
				bAnc.right = a;
		}

		if (aLeft != null)
			aLeft.ancestor = b;
		if (aRight != null)
			aRight.ancestor = b;

		if (bLeft != null)
			bLeft.ancestor = a;
		if (bRight != null)
			bRight.ancestor = a;

		if (root == a)
		{
			root = b;
		}
		else if (root == b)
		{
			root = a;
		}

		a.left = bLeft == a ? b : bLeft;
		a.right = bRight == a ? b : bRight;
		a.ancestor = bAnc == a ? b : bAnc;

		b.left = aLeft == b ? a : aLeft;
		b.right = aRight == b ? a : aRight;
		b.ancestor = aAnc == b ? a : aAnc;
	}
}
