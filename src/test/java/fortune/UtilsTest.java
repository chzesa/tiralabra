package app.fortune;

import app.vector.*;
import static org.junit.Assert.*;
import org.junit.Test;

public class UtilsTest
{
	@Test
	public void testBisectorsVertical()
	{
		Vector a = new Vector(0, 0);
		Vector b = new Vector(1, 0);

		assertTrue(Utils.bisector(a, b).equals(new Vector(0, -1)));
		assertTrue(Utils.bisector(b, a).equals(new Vector(0, 1)));
	}

	@Test
	public void testBisectorsHorizontal()
	{
		Vector a = new Vector(0, 0);
		Vector b = new Vector(0, 1);

		assertTrue(Utils.bisector(a, b).equals(new Vector(1, 0)));
		assertTrue(Utils.bisector(b, a).equals(new Vector(-1, 0)));
	}

	@Test
	public void testBisectorsDiagonal()
	{
		Vector a = new Vector(0, 0);
		Vector b = new Vector(0, 1);

		assertTrue(Utils.bisector(a, b).equals(new Vector(1, 0)));
		assertTrue(Utils.bisector(b, a).equals(new Vector(-1, 0)));
	}
}
