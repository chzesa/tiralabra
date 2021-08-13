package app.parse;

import app.vector.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.*;
import static org.junit.Assert.*;

public class ParseTest
{
	@Test
	public void testListToString()
	{
		List<Vector> list = new ArrayList<>();
		list.add(new Vector(1, 0));
		list.add(new Vector(0, 1));

		String s = Parse.toStringl(list);
		List<Vector> list2 = Parse.fromStringl(s);
		assertTrue(list.equals(list2));
	}

	@Test
	public void testListListToString()
	{
		List<Vector> list1 = new ArrayList<>();
		list1.add(new Vector(1, 0));
		list1.add(new Vector(0, 1));

		List<Vector> list2 = new ArrayList<>();
		list2.add(new Vector(3, 0));
		list2.add(new Vector(2, 1));
		list2.add(new Vector(3, 5));
		list2.add(new Vector(2, 3));

		List<List<Vector>> ll = new ArrayList<>();

		ll.add(list1);
		ll.add(list2);

		String s = Parse.toStringll(ll);
		List<List<Vector>> ll2 = Parse.fromStringll(s);
		assertTrue(ll.equals(ll2));
	}

	@Test
	public void testListToStringMany()
	{
		Random rand = new Random(5508);
		List<List<Vector>> ll = new ArrayList<>();

		for (int a = 0; a < 200; a++)
		{
			List<Vector> list = new ArrayList<>();
			int n = rand.nextInt(100) + 1;
			for (int i = 0; i < n; i++)
			{
				list.add(new Vector(rand.nextDouble(), rand.nextDouble()));
			}

			ll.add(list);
		}

		assertTrue(ll.equals(Parse.fromStringll(Parse.toStringll(ll))));
	}
}
