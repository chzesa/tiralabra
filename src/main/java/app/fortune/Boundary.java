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
			return new Vector(intersections[0].x, Utils.parabolaY(siteA, sweepline, intersections[0].x));

		Vector end;

		// Determine which intersection the boundary ray intersects
		Vector delta = intersections[0].sub(ray.origin).normalize();

		if (delta.equals(ray.direction.normalize()))
			return new Vector(intersections[0].x, Utils.parabolaY(siteA, sweepline, intersections[0].x));
		else
			return new Vector(intersections[1].x, Utils.parabolaY(siteA, sweepline, intersections[1].x));
	}

	@Override
	public String toString()
	{
		return "(Boundary " + ray + " bounded by " + siteA + " & " + siteB + ")";
	}
}
