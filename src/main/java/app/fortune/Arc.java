package app.fortune;

import app.vector.*;
import app.pq.*;

/**
 * Arcs are sorted primarily by the projection of its leftmost endpoint onto the sweepline.
 * If two adjacent arcs have the same primary sort value, at least one of them is vanishing
 * from the beachline, and multiple sites are on the voronoi circle around the event point.
 */
public class Arc implements ISortable
{
	public final Boundary left;
	public final Boundary right;
	public final Vector site;

	PriorityQueue<Event>.Node event = null;

	public Arc(Boundary left, Vector site, Boundary right)
	{
		this.left = left;
		this.right = right;
		this.site = site;
	}

	public Vector left(double y)
	{
		if (left == null)
			return new Vector(Double.NEGATIVE_INFINITY, y);

		return left.end(y);
	}

	public Vector right(double y)
	{
		if (right == null)
			return new Vector(Double.POSITIVE_INFINITY, y);

		return right.end(y);
	}

	public Vector site()
	{
		return site;
	}

	Ray reconstruct(Boundary bound)
	{
		// Pick the lowest of the sites and the ray origin point as the initial sweepline height
		// Lower sweepline to ensure non-degeneracy
		Ray ray = bound.ray;
		double sweepline = Math.min(Math.min(bound.siteA.y, bound.siteB.y), ray.origin.y);
		sweepline -= Vector.distance(bound.siteA, bound.siteB);

		return new Ray(
			ray.origin,
			bound.end(sweepline).sub(ray.origin).normalize()
		);
	}

	/**
	 * Determines the circle event location below the arc's boundary ray intersection point if any.
	 */
	public Vector circleEvent()
	{
		if (left == null || right == null)
			return null;

		Ray lRay = reconstruct(left);
		Ray rRay = reconstruct(right);
		Vector point = Ray.intersect(lRay, rRay);

		if (point == null)
			return null;

		Vector offset = new Vector(0.0, -point.sub(site).norm());
		Vector ret = point.add(offset);

		if (ret.equals(site))
			return null;

		return ret;
	}

	@Override
	public String toString()
	{
		return "Arc of " + site + "{ " + left + " ; " + right + " }";
	}
}
