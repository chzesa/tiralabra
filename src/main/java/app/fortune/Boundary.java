package app.fortune;

import app.vector.*;

public class Boundary
{
	public final Ray ray;
	public final Vector siteA;
	public final Vector siteB;

	Boundary(Ray ray, Vector siteA, Vector siteB)
	{
		this.ray = ray;
		this.siteA = siteA;
		this.siteB = siteB;
	}

	public Vector begin()
	{
		return ray.origin;
	}

	public Vector end(double sweepline)
	{
		Vector[] intersections = Utils.parabolaIntersection(siteA, siteB, sweepline);

		if (intersections.length == 0)
			return ray.origin;

		if (intersections.length == 1)
			return intersections[0];

		if (intersections[1].x < intersections[0].x)
		{
			Vector temp = intersections[0];
			intersections[0] = intersections[1];
			intersections[1] = temp;
		}

		return Utils.vectorCompare(siteA, siteB) <= 0 ? intersections[0] : intersections[1];
	}

	@Override
	public String toString()
	{
		return "(Boundary " + ray + " bounded by " + siteA + " & " + siteB + ")";
	}
}
