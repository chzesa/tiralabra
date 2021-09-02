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

		Node ancestor = null;
		Node left = null;
		Node right = null;

		Node next = null;
		Node previous = null;

		int depth = 0;

		Node(T value)
		{
			this.val = value;
		}

		public T value()
		{
			return val;
		}

		public Node next()
		{
			return next;
		}

		public Node previous()
		{
			return previous;
		}

		public Node left()
		{
			return left;
		}

		public Node right()
		{
			return right;
		}

		void reset()
		{
			ancestor = null;
			left = null;
			right = null;
			next = null;
			previous = null;
			depth = 0;
		}
	}

	private Node root = null;
	private int count = 0;

	/**
	 * Constructs a new tree.
	 * @param item The root node of the tree
	 */
	public Tree(T item)
	{
		this.root = new Node(item);
		this.count = 1;
	}

	public Node root()
	{
		return root;
	}

	public int size()
	{
		return this.count;
	}

	public boolean isEmpty()
	{
		return size() == 0;
	}

	public void forEach(ICallback<T> fn)
	{
		forEachNode(root, fn);
	}

	/**
	 * Replace the item stored in a node with the new item.
	 * @param node node whose position the item is inserted at.
	 * @param item the replacing item
	 */
	public Node replace(Node node, T item)
	{
		Node replacement = new Node(item);
		swap(node, replacement);
		setLinks(node.previous(), replacement, node.next());
		node.reset();
		return replacement;
	}

	/**
	 * Creates a new node containing the item in the tree directly previous to an existing node.
	 * @param node the node previous to which the new node is added
	 * @param item value of the newly added node.
	 */
	public Node addPrevious(Node node, T item)
	{
		Node added = new Node(item);
		setLinks(node.previous(), added, node);

		Node parent = node.left;
		if (parent == null)
		{
			parent = node;
			parent.left = added;
		}
		else
		{
			while (parent.right != null)
				parent = parent.right;

			parent.right = added;
		}

		added.ancestor = parent;

		count++;
		rebalance(added);
		return added;
	}

	/**
	 * Creates a new node containing the item in the tree directly next to an existing node.
	 * @param node the node next to which the new node is added
	 * @param item value of the newly added node.
	 */
	public Node addNext(Node node, T item)
	{
		Node added = new Node(item);
		setLinks(node, added, node.next());

		Node parent = node.right;
		if (parent == null)
		{
			parent = node;
			parent.right = added;
		}
		else
		{
			while (parent.left != null)
				parent = parent.left;

			parent.left = added;
		}

		added.ancestor = parent;

		count++;
		rebalance(added);
		return added;
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
			swap(node, node.next());

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
		{
			child.ancestor = node.ancestor;
			rebalance(child);
		}
		else
		{
			rebalance(node.ancestor);
		}

		setLinks(node.previous, null, node.next);

		node.reset();
		return node.value();
	}

	private void rebalance(Node n)
	{
		while (n != null)
		{
			updateDepth(n);

			if (Math.abs(depth(n.right) - depth(n.left)) < 2)
			{
				n = n.ancestor;
				continue;
			}

			if (depth(n.right) > depth(n.left))
			{
				if (depth(n.right.right) < depth(n.right.left))
					cwRotate(n.right);

				ccwRotate(n);
			}
			else
			{
				if (depth(n.left.right) > depth(n.left.left))
					ccwRotate(n.left);

				cwRotate(n);
			}

			n = n.ancestor;
		}
	}

	private int depth(Node n)
	{
		if (n == null)
			return 0;

		return n.depth;
	}

	private void updateDepth(Node n)
	{
		n.depth = Math.max(depth(n.left), depth(n.right)) + 1;
	}

	private void cwRotate(Node node)
	{
		Node child = node.left;
		Node mover = child.right;
		Node parent = node.ancestor;

		child.right = node;
		node.left = mover;

		if (root == node)
		{
			root = child;
		}
		else
		{
			if (parent.left == node)
				parent.left = child;
			else
				parent.right = child;
		}

		child.ancestor = parent;
		node.ancestor = child;
		if (mover != null)
			mover.ancestor = node;

		updateDepth(node);
		updateDepth(child);
	}

	private void ccwRotate(Node node)
	{
		Node child = node.right;
		Node mover = child.left;
		Node parent = node.ancestor;

		child.left = node;
		node.right = mover;

		if (root == node)
		{
			root = child;
		}
		else
		{
			if (parent.left == node)
				parent.left = child;
			else
				parent.right = child;
		}

		child.ancestor = parent;
		node.ancestor = child;
		if (mover != null)
			mover.ancestor = node;

		updateDepth(node);
		updateDepth(child);
	}

	private void setLinks(Node left, Node mid, Node right)
	{
		if (left != null)
			left.next = mid != null ? mid : right;

		if (mid != null)
		{
			mid.previous = left;
			mid.next = right;
		}

		if (right != null)
			right.previous = mid != null ? mid : left;
	}

	private void swap(Node a, Node b)
	{
		int depth = a.depth;
		a.depth = b.depth;
		b.depth = depth;

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

	private void forEachNode(Node n, ICallback<T> fn)
	{
		if (n == null)
			return;

		forEachNode(n.left, fn);
		fn.operation(n.value());
		forEachNode(n.right, fn);
	}
}
