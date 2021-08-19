package app.fortune;

import app.vector.*;
import app.parse.*;
import app.io.*;
import static org.junit.Assert.*;
import org.junit.Test;
import java.util.List;

public class FortuneTest
{
	void test(Vector[] sites)
	{
		Fortune f = new Fortune(sites);
		Validator valid = new Validator(sites, f.processAll());
		assertTrue(valid.result());
	}

	@Test
	public void testNone()
	{
		test(new Vector[] {});
	}

	@Test
	public void testOne()
	{
		test(new Vector[] {
			new Vector(1, 1)
		});
	}

	@Test
	public void testFew()
	{
		test(new Vector[] {
			new Vector(1, 1),
			new Vector(2, 3),
			new Vector(1, 2)
		});
	}

	@Test
	public void testVertical()
	{
		test(new Vector[] {
			new Vector(0, 5),
			new Vector(0, 4),
			new Vector(0, 3),
			new Vector(0, 2),
			new Vector(0, 1),
		});
	}

	@Test
	public void testHorizontal()
	{
		test(new Vector[] {
			new Vector(0, 0),
			new Vector(1, 0),
			new Vector(2, 0),
			new Vector(3, 0),
			new Vector(4, 0),
		});
	}

	@Test
	public void testHorizontalWithInitialSiteAbove()
	{
		test(new Vector[] {
			new Vector(1, 1),
			new Vector(0, 0),
			new Vector(1, 0),
			new Vector(2, 0),
			new Vector(3, 0),
			new Vector(4, 0),
		});
	}

	@Test
	public void testGrid()
	{
		test(new Vector[] {
			new Vector(0, 0),
			new Vector(0, 1),
			new Vector(1, 0),
			new Vector(1, 2),
			new Vector(2, 0),
			new Vector(2, 2),
			new Vector(3, 0),
			new Vector(3, 1),
		});
	}

	@Test
	public void testGridWithInitialSiteAbove()
	{
		test(new Vector[] {
			new Vector(1.5f, 3),
			new Vector(0, 0),
			new Vector(0, 1),
			new Vector(1, 0),
			new Vector(1, 2),
			new Vector(2, 0),
			new Vector(2, 2),
			new Vector(3, 0),
			new Vector(3, 1),
		});
	}

	@Test
	public void testConverging()
	{
		double x = 1.0f/Math.sqrt(2);

		test(new Vector[] {
			new Vector(-x, x),
			new Vector(0, 1),
			new Vector(x, x)
		});
	}

	@Test
	public void testConvergingWithParallels()
	{
		double x = 1.0f/Math.sqrt(2);

		test(new Vector[] {
			new Vector(-1, 0),
			new Vector(-x, x),
			new Vector(0, 1),
			new Vector(x, x),
			new Vector(1, 0)
		});
	}

	@Test
	public void testConvergingWithPointBelow()
	{
		double x = 1.0f/Math.sqrt(2);

		test(new Vector[] {
			new Vector(-x, x),
			new Vector(0, 1),
			new Vector(x, x),
			new Vector(0, -2)
		});
	}

	@Test
	public void testConvergingWithPointBelowOnCircle()
	{
		double x = 1.0f/Math.sqrt(2);

		test(new Vector[] {
			new Vector(-x, x),
			new Vector(0, 1),
			new Vector(x, x),
			new Vector(0, -1)
		});
	}

	@Test
	public void testConvergingWithParallelsAndPointBelow()
	{
		double x = 1.0f/Math.sqrt(2);

		test(new Vector[] {
			new Vector(-1, 0),
			new Vector(-x, x),
			new Vector(0, 1),
			new Vector(x, x),
			new Vector(1, 0),
			new Vector(0, -2)
		});
	}

	@Test
	public void testConvergingWithParallelsAndPointBelowOnCircle()
	{
		double x = 1.0f/Math.sqrt(2);

		test(new Vector[] {
			new Vector(-1, 0),
			new Vector(-x, x),
			new Vector(0, 1),
			new Vector(x, x),
			new Vector(1, 0),
			new Vector(0, -1)
		});
	}

	@Test
	public void testConvergingWithSiteOverlappingPoint()
	{
		double x = 1.0f/Math.sqrt(2);

		test(new Vector[] {
			new Vector(-x, x),
			new Vector(0, 1),
			new Vector(x, x),
			new Vector(0, 0)
		});
	}

	List<List<Vector>> loadDataset(String file)
	{
		String s = FileHandler.readToString(file);
		if (s.equals(""))
			throw new Error("Failed to locate test data.");

		return Parse.fromStringll(s);
	}

	@Test
	public void testOkDataset1()
	{
		List<List<Vector>> data = loadDataset("dataset/ok_data_1.txt");
		for (List<Vector> sites : data)
			assertTrue(new Validator(sites, new Fortune(sites).processAll()).result());
	}

	@Test
	public void testOkDataset2()
	{
		for (List<Vector> sites : loadDataset("dataset/ok_data_2.txt"))
			assertTrue(new Validator(sites, new Fortune(sites).processAll()).result());
	}

	@Test
	public void testOkDataset3()
	{
		for (List<Vector> sites : loadDataset("dataset/ok_data_3.txt"))
			assertTrue(new Validator(sites, new Fortune(sites).processAll()).result());
	}

	void testBadData(String file)
	{
		int count = 0;
		List<List<Vector>> ll = loadDataset("dataset/" + file);
		for (List<Vector> sites : ll)
			try
			{
				if (new Validator(sites, new Fortune(sites).processAll()).result())
					count++;
			}
			catch (Exception e)
			{ }
		System.out.println(file + " successes: " + count + "/" + ll.size());
	}

	@Test
	public void testBadDataset1()
	{
		String file = "bad_data_1.txt";
		testBadData(file);
	}

	@Test
	public void testBadDataset2()
	{
		String file = "bad_data_2.txt";
		testBadData(file);
	}

	@Test
	public void testBadDataset3()
	{
		String file = "bad_data_3.txt";
		testBadData(file);
	}
}
