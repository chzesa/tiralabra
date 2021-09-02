package app.fortune;

import app.vector.*;
import app.pq.*;

/**
 * Arcs are sorted primarily by the projection of its leftmost endpoint onto the sweepline.
 * If two adjacent arcs have the same primary sort value, at least one of them is vanishing
 * from the beachline, and multiple sites are on the voronoi circle around the event point.
 */
public class Arc
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

	/**
	 * Left endpoint of this arc on the beachline
	 */
	public Vector left(double sweepline)
	{
		if (left == null)
			return new Vector(Double.NEGATIVE_INFINITY, sweepline);

		return left.end(sweepline);
	}

	/**
	 * Right endpoint of this arc on the beachline
	 */
	public Vector right(double sweepline)
	{
		if (right == null)
			return new Vector(Double.POSITIVE_INFINITY, sweepline);

		return right.end(sweepline);
	}

	public Vector site()
	{
		return site;
	}

	/**
	 * Determines the circle event location below the arc's boundary ray intersection point if any.
	 */
	public Vector circleEvent()
	{
		if (left == null || right == null)
			return null;

		Vector point = Ray.intersect(left.ray, right.ray);
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
