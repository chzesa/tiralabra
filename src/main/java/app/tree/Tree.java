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

		public String toString()
		{
			return "[ " + val.toString() + " ] [A]: "
				+ (ancestor == null ? "null" : ancestor.val.toString())
				+ "; [L]: "
				+ (left == null ? "null" : left.val.toString())
				+ "; [R]: "
				+ (right == null ? "null" : right.val.toString());
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

	final Comparator<T> cmp;
	Node root = null;
	int count = 0;

	public Node root()
	{
		return root;
	}

	/**
	 * Constructs a new tree.
	 * @param cmp A comparator determining the ordering of the tree.
	 */
	public Tree(Comparator<T> cmp, T item)
	{
		this.cmp = cmp;
		this.root = new Node(item);
		this.count = 1;
	}

	public boolean isEmpty()
	{
		return size() == 0;
	}

	public int size()
	{
		return this.count;
	}

	public Node replace(Node n, T item)
	{
		Node replacement = new Node(item);
		swap(n, replacement);
		setLinks(n.previous(), replacement, n.next());
		n.reset();
		return replacement;
	}

	public Node addPrevious(Node n, T item)
	{
		Node added = new Node(item);
		setLinks(n.previous(), added, n);

		Node parent = n.left;
		if (parent == null)
		{
			parent = n;
			parent.left = added;
		}
		else
		{
			while(parent.right != null)
				parent = parent.right;

			parent.right = added;
		}

		added.ancestor = parent;

		count++;
		rebalance(added);
		return added;
	}

	public Node addNext(Node n, T item)
	{
		Node added = new Node(item);
		setLinks(n, added, n.next());

		Node parent = n.right;
		if (parent == null)
		{
			parent = n;
			parent.right = added;
		}
		else
		{
			while(parent.left != null)
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

	void rebalance(Node n)
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

	int depth(Node n)
	{
		if (n == null)
			return 0;

		return n.depth;
	}

	void updateDepth(Node n)
	{
		n.depth = Math.max(depth(n.left), depth(n.right)) + 1;
	}

	void cwRotate(Node node)
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

	void ccwRotate(Node node)
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

	void forEachNode(Node n, ICallback<T> fn)
	{
		if (n == null)
			return;

		forEachNode(n.left, fn);
		fn.operation(n.value());
		forEachNode(n.right, fn);
	}

	void setLinks(Node left, Node mid, Node right)
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
			throw new RuntimeException("Left subtree of " + value.toString() + " has a larger item " + n.value().toString());

		checkLeft(n.left, value);
		checkLeft(n.right, value);
	}

	void checkRight(Node n, T value)
	{
		if (n == null)
			return;

		if (cmp.compare(value, n.value()) != -1)
			throw new RuntimeException("Right subtree of \n\t" + value.toString() + "\nhas an equal or smaller item\n\t" + n.value().toString());

		checkRight(n.left, value);
		checkRight(n.right, value);
	}

	void checkOrdered(Node n)
	{
		if (n == null)
			return;

		checkLeft(n.left, n.value());
		checkRight(n.right, n.value());

		checkOrdered(n.left);
		checkOrdered(n.right);
	}

	public void validate()
	{
		checkOrdered(root);
		checkAncestry(root);
	}

	void swap(Node a, Node b)
	{
		if (a == null || b == null)
			throw new IllegalArgumentException();

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
}
