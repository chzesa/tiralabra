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

		if (intersections[0].equals(ray.origin) || intersections[1].equals(ray.origin))
			return ray.origin;

		// Determine which intersection the boundary ray intersects
		double delta0 = Vector.distance(ray.direction, intersections[0].sub(ray.origin).normalize());
		double delta1 = Vector.distance(ray.direction, intersections[1].sub(ray.origin).normalize());

		if (delta0 < delta1)
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
