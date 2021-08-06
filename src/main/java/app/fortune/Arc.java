package app.fortune;

import app.vector.*;

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

	public Arc(Boundary left, Vector site, Boundary right)
	{
		this.left = left;
		this.right = right;
		this.site = site;
	}

	public double primary(double y)
	{
		if (left == null)
			return Double.NEGATIVE_INFINITY;

		Vector[] intersections = Utils.parabolaIntersection(left.siteA, left.siteB, y);

		if (intersections.length == 0)
			return site.x;

		if (intersections.length == 1)
			return intersections[0].x;

		Vector end;

		// Determine which intersection the boundary ray intersects
		Vector delta = intersections[0].sub(left.ray.origin).normalize();

		if (delta.equals(left.ray.direction.normalize()))
			return intersections[0].x;
		else
			return intersections[1].x;
	}

	public double secondary()
	{
		return site.x;
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
}
