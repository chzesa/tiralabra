package app.fortune;

import app.vector.*;
import app.parse.*;
import app.io.*;
import static org.junit.Assert.*;
import org.junit.Test;
import java.util.List;

public class FortuneTest
{
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
