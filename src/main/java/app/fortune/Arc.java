package app.fortune;

import app.vector.*;
import app.pq.*;

/**
 * Arcs are sorted primarily by the projection of its leftmost endpoint onto the sweepline.
 * If two adjacent arcs have the same primary sort value, at least one of them is vanishing
 * from the beachline, and multiple sites are on the voronoi circle around the event point.
 * In this case, the sites are ordered clockwise, which is equivalent to ordering them from
 * left to right, given each arc is currently above the sweepline.
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
		return point.add(offset);
	}

	@Override
	public String toString()
	{
		return "Arc of " + site + "{ " + left + " ; " + right + " }";
	}
}
