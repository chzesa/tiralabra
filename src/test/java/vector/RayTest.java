package app.vector;

import static org.junit.Assert.*;
import org.junit.Test;

public class RayTest
{
	@Test
	public void testMisses()
	{
		Vector up = new Vector(0, 1);
		Ray horizontal = new Ray(new Vector(0, 0), new Vector(1, 0));

		Ray left = new Ray(new Vector(-1, -1), up);
		Ray right = new Ray(new Vector(1, 1), up);

		assertNull(Ray.intersect(horizontal, left));
		assertNull(Ray.intersect(right, horizontal));
	}

	@Test
	public void testHits()
	{
		Vector zero = new Vector(0, 0);
		Vector up = new Vector(0, 1);
		Ray horizontal = new Ray(new Vector(0, 0), new Vector(1, 0));

		Ray yAxis = new Ray(new Vector(0, -1), up);
		Ray sameOrigin = new Ray(new Vector(0, 0), up);
		Ray right = new Ray(new Vector(1, -1), up);

		assertEquals(zero, Ray.intersect(yAxis, horizontal));
		assertEquals(zero, Ray.intersect(sameOrigin, horizontal));
		assertEquals(new Vector(1, 0), Ray.intersect(right, horizontal));
	}

	@Test
	public void testDiagonal()
	{
		Ray a = new Ray(new Vector(1, 0), new Vector(-1, -1));
		Ray b = new Ray(new Vector(0, 0), new Vector(1, -1));

		assertEquals(new Vector(0.5, -0.5), Ray.intersect(a, b));
		assertEquals(new Vector(0.5, -0.5), Ray.intersect(b, a));
	}
}