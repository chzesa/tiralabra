package app.fortune;

import app.fortune.*;
import java.util.ArrayList;
import java.util.List;
import app.vector.*;

/**
 * A basic validator class for checking the output of Fortune class.
 * Does not guarantee that the tested voronoi diagram is correct, only
 * that it does not contain anything incorrect.
 */
public class Validator
{

	List<Vector> sites;
	Fortune.Result result;

	public Validator(List<Vector> sites, Fortune.Result result)
	{
		this.sites = sites;
		this.result = result;
	}

	public Validator(Vector[] sites, Fortune.Result result)
	{
		this.sites = new ArrayList<>();
		for (int i = 0; i < sites.length; i++)
			this.sites.add(sites[i]);

		this.result = result;
	}

	static List<Vector> closestSites(Vector point, List<Vector> sites)
	{
		ArrayList<Vector> res = new ArrayList<>();
		double dist = Double.POSITIVE_INFINITY;

		for (Vector site : sites)
		{
			double d = Vector.distance(site, point);
			if (Math.abs(d - dist) < Vector.PRECISION)
			{
				res.add(site);
			}
			else if (d < dist)
			{
				res.clear();
				res.add(site);
				dist = d;
			}
		}

		return res;
	}

	static boolean validateRay(Fortune.Edge ray, List<Vector> sites)
	{
		Vector half = ray.a.add(ray.b.normalize().scale(Math.max(ray.a.norm(), 1)));
		List<Vector> sitesA = closestSites(ray.a, sites);
		List<Vector> sitesH = closestSites(half, sites);

		if (sitesH.size() > 2)
		{
			System.out.println("Direction of ray " + new Ray(ray.a, ray.b) + " may be incorrect, adjacent sites:");
			for (Vector v : sitesH)
				System.out.println("\t" + v);
		}

		if (sitesH.size() < 2)
		{
			throw new Error("Direction of ray " + new Ray(ray.a, ray.b) + " is incorrect.");
		}

		if (!sitesA.containsAll(sitesH))
		{
			throw new Error("Origin of ray " + new Ray(ray.a, ray.b) + " doesn't contain all incident sites.");
		}

		return sitesA.size() > 1;
	}

	static boolean validateEdge(Fortune.Edge edge, List<Vector> sites)
	{
		Vector half = edge.b.sub(edge.a).scale(0.5).add(edge.a);
		List<Vector> sitesA = closestSites(edge.a, sites);
		List<Vector> sitesB = closestSites(edge.b, sites);
		List<Vector> sitesH = closestSites(half, sites);

		if (Vector.distance(edge.a, edge.b) > Vector.PRECISION )
		{
			if (sitesH.size() > 2)
			{
				System.out.println("Direction of edge " + edge + " may be incorrect, adjacent sites to halfway point " + half + ":");
				System.out.println(edge.a.x + ", " + edge.a.y + "; " + edge.b.x + ", " + edge.b.y);
				for (Vector v : sitesH)
					System.out.println("\t" + v);
			}

			if (sitesH.size() < 2)
				throw new Error("Direction of edge " + edge + " is incorrect.");

			if (!sitesA.containsAll(sitesH))
				throw new Error("Origin of edge " + edge + " is not adjacent to its incident sites.");

			if (!sitesB.containsAll(sitesH))
				throw new Error("Destination of edge " + edge + " is not adjacent to its incident sites.");
		}

		/**
		 * TODO: The algorithm currently inserts two parallel rays with the same origin
		 * but with opposite directions when a new site is added. This can be detected
		 * whenever the beachline is updated, or converted to lines (not supported) if
		 * any such split edges remain at the end.
		 * The usual solution to handling rays and lines is to enclose the voronoi diagram
		 * in a box, which is also a possibility.
		 */

		if (sitesA.size() > 2 && sitesB.size() > 2)
			return true;

		if (sitesA.size() == 2)
			return true;

		return false;
	}

	public boolean result()
	{
		boolean res = true;
		for (Fortune.Edge e : result.edges)
			res = res && validateEdge(e, sites);

		for (Fortune.Edge r : result.rays)
			res = res && validateRay(r, sites);

		return res;
	}
}
