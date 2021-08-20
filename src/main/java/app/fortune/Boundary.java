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

		double do0 = Vector.distance(ray.origin, intersections[0]);
		double do1 = Vector.distance(ray.origin, intersections[1]);

		Vector closest = do0 < do1 ? intersections[0] : intersections[1];

		// Determine which intersection the boundary ray intersects
		double dd0 = Vector.distance(ray.direction, intersections[0].sub(ray.origin).normalize());
		double dd1 = Vector.distance(ray.direction, intersections[1].sub(ray.origin).normalize());

		if (dd0 < dd1)
		{
			if (dd0 < Math.min(do0, do1))
				closest = intersections[0];
		}
		else
		{
			if (dd1 < Math.min(do0, do1))
				closest = intersections[1];
		}

		return closest;
	}

	@Override
	public String toString()
	{
		return "(Boundary " + ray + " bounded by " + siteA + " & " + siteB + ")";
	}
}
