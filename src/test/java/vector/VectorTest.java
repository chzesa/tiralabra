package app.vector;

import static org.junit.Assert.*;
import org.junit.Test;

public class VectorTest
{
	@Test
	public void testConstructor()
	{
		Vector a = new Vector(5, 2);
		assertEquals(5, a.x, Vector.PRECISION);
		assertEquals(2, a.y, Vector.PRECISION);
	}

	@Test
	public void equalsExact()
	{
		Vector a = new Vector(1, 3);
		Vector b = new Vector(1, 3);

		assertTrue(a.equals(a));
		assertTrue(a.equals(b));
	}

	@Test
	public void equalsAlmostExact()
	{
		Vector a = new Vector(1, 3);
		Vector b = new Vector(1, 3 + Vector.PRECISION / 2);

		assertTrue(a.equals(b));
	}

	@Test
	public void notEquals()
	{
		Vector a = new Vector(1, 0);
		Vector b = new Vector(2, 1);

		assertFalse(a.equals(b));
	}

	@Test
	public void testSum()
	{
		Vector a = new Vector(1, 0);
		assertTrue(a.add(a).equals(new Vector(2, 0)));
	}

	@Test
	public void testSub()
	{
		Vector a = new Vector(1, 0);
		assertTrue(a.sub(a).equals(new Vector(0, 0)));
	}

	@Test
	public void testDot()
	{
		Vector a = new Vector(3, 5);
		assertEquals(34, Vector.dot(a, a), Vector.PRECISION);
	}

	@Test
	public void testNorm()
	{
		Vector a = new Vector(1, 0);
		Vector b = new Vector(0, -3);

		assertEquals(1, a.norm(), Vector.PRECISION);
		assertEquals(3, b.norm(), Vector.PRECISION);
	}

	@Test
	public void testScale()
	{
		Vector a = new Vector(2, 3);
		Vector b = a.scale(3);

		assertEquals(6, b.x, Vector.PRECISION);
		assertEquals(9, b.y, Vector.PRECISION);
	}

	@Test
	public void testNeg()
	{
		Vector a = new Vector(2, 3);
		a = a.neg();
		assertEquals(-2, a.x, Vector.PRECISION);
		assertEquals(-3, a.y, Vector.PRECISION);
	}

	@Test
	public void testNormalize()
	{
		Vector a = new Vector(3, 0);
		a = a.normalize();
		Vector b = new Vector(1.0 / 2, 0);
		b = b.normalize();

		Vector c = new Vector(-5, 0);
		c = c.normalize();

		assertEquals(1.0, a.x, Vector.PRECISION);
		assertEquals(1.0, b.x, Vector.PRECISION);
		assertEquals(-1.0, c.x, Vector.PRECISION);
	}
}
