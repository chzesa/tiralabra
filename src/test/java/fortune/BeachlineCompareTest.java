package app.fortune;

import app.vector.*;
import static org.junit.Assert.*;
import org.junit.Test;

public class BeachlineCompareTest
{
	@Test
	public void testClockwiseOrdering()
	{
		Vector[] vecs = new Vector[] {
			new Vector(-0.01, -1),
			new Vector(-1, -1),
			new Vector(-1, 1),
			new Vector(-0.01, 1),
			new Vector(0.01, 1),
			new Vector(1, 1),
			new Vector(1, -1),
			new Vector(0.01, -1)
		};

		for (int i = 1; i < vecs.length; i++)
			assertTrue(Double.compare(
				BeachlineCompare.angle(vecs[i - 1]),
				BeachlineCompare.angle(vecs[i])) == -1);
	}
}
