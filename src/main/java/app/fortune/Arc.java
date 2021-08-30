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
		Vector ret = point.add(offset);

		if (ret.equals(site))
			return null;

		return ret;
	}

	/**
	 * Alyways use the same site for calculating distance to circle event
	 */
	Vector min(Vector a, Vector b)
	{
		if (a.x == b.x)
			return a.y <= b.y ? a : b;
		return a.x <= b.x ? a : b;
	}

	Vector max(Vector a, Vector b)
	{
		return a.exact(min(a, b)) ? b : a;
	}

	public Vector circleEvent2()
	{
		if (left == null || right == null)
			return null;

		Vector a, b, c, i, j, k;
		a = site;
		b = left.siteA.exact(site) ? left.siteB : left.siteA;
		c = right.siteA.exact(site) ? right.siteB : right.siteA;

		if (a.exact(b) || b.exact(c) || a.exact(c))
			return null;

		i = min(a, min(b, c));
		j = null;
		k = max(a, max(b, c));

		if (!a.exact(i) && !a.exact(k))
			j = a;

		if (!b.exact(i) && !b.exact(k))
			j = b;

		if (!c.exact(i) && !c.exact(k))
			j = c;

		Vector point = Utils.intersection(i, j, k);

		if (point == null)
			return null;

		Vector offset = new Vector(0.0, -point.sub(i).norm());
		Vector ret = point.add(offset);

		return ret;
	}

	@Override
	public String toString()
	{
		return "Arc of " + site + "{ " + left + " ; " + right + " }";
	}
}
