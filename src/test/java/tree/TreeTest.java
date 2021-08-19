package app.tree;

import java.util.Comparator;
import static org.junit.Assert.*;
import org.junit.*;

public class TreeTest
{
	class Cmp implements Comparator<Integer>
	{
		public int compare(Integer a, Integer b)
		{
			return Integer.compare(a, b);
		}
	}

	void printTree(Tree<Integer>.Node n)
	{
		if (n == null)
			return;
		printTree(n.left);
		System.out.println(n);
		printTree(n.right);
	}

	Tree<Integer> tree;

	@Before
	public void init()
	{
		tree = new Tree(new Cmp());
	}

	void balanced()
	{
		tree.add(3);

		tree.add(1);
		tree.add(5);

		tree.add(0);
		tree.add(2);
		tree.add(4);
		tree.add(6);
	}

	void leftHeavy()
	{
		for (int i = 0; i < 10; i++)
			tree.add(9 - i);
	}

	void rightHeavy()
	{
		for (int i = 0; i < 10; i++)
			tree.add(i);
	}

	void exists(int i)
	{
		Tree<Integer>.Node res = tree.find(i);
		assertNotNull(res);
		assertTrue(res.value().equals(i));
	}

	@Test
	public void testAdd()
	{
		leftHeavy();
		rightHeavy();
		balanced();
	}

	@Test
	public void testFind1()
	{
		rightHeavy();

		for (int i = 0; i < 10; i++)
			exists(i);
	}

	@Test
	public void testFind2()
	{
		leftHeavy();

		for (int i = 0; i < 10; i++)
			exists(i);
	}

	@Test
	public void testFind3()
	{
		balanced();

		for (int i = 0; i < 7; i++)
			exists(i);
	}

	@Test
	public void testPrevious1()
	{
		rightHeavy();

		for (int i = 0; i < 10; i++)
		{
			Tree<Integer>.Node res = tree.previous(i + 1);
			assertNotNull(res);
			assertTrue(res.value().equals(i));
		}

		assertNull(tree.previous(0));
	}

	@Test
	public void testPrevious2()
	{
		leftHeavy();

		for (int i = 0; i < 10; i++)
		{
			Tree<Integer>.Node res = tree.previous(i + 1);
			assertNotNull(res);
			assertTrue(res.value().equals(i));
		}

		assertNull(tree.previous(0));
	}

	@Test
	public void testPrevious3()
	{
		balanced();

		for (int i = 0; i < 7; i++)
		{
			Tree<Integer>.Node res = tree.previous(i + 1);
			assertNotNull(res);
			assertTrue(res.value().equals(i));
		}

		assertNull(tree.previous(0));
	}

	@Test
	public void testNext1()
	{
		rightHeavy();

		for (int i = 0; i < 10; i++)
		{
			Tree<Integer>.Node res = tree.next(i - 1);
			assertNotNull(res);
			assertTrue(res.value().equals(i));
		}

		assertNull(tree.next(9));
	}

	@Test
	public void testNext2()
	{
		leftHeavy();

		for (int i = 0; i < 10; i++)
		{
			Tree<Integer>.Node res = tree.next(i - 1);
			assertNotNull(res);
			assertTrue(res.value().equals(i));
		}
		assertNull(tree.next(9));
	}

	@Test
	public void testNext3()
	{
		balanced();

		for (int i = 0; i < 7; i++)
		{
			Tree<Integer>.Node res = tree.next(i - 1);
			assertNotNull(res);
			assertTrue(res.value().equals(i));
		}

		assertNull(tree.next(6));
	}

	@Test
	public void testDelete()
	{
		balanced();
		tree.validate();
		assertTrue(tree.delete(1).equals(1));
		tree.validate();
		assertTrue(tree.delete(5).equals(5));
		tree.validate();
		assertTrue(tree.delete(3).equals(3));
		tree.validate();
	}
}
