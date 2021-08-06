package app.fortune;

import app.vector.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.TreeSet;

class Event
{
	private final Vector pt;
	private final Vector s;

	Event(Vector site)
	{
		this.pt = null;
		this.s = site;
	}

	Event(Vector point, Vector site)
	{
		this.pt = point;
		this.s = site;
	}

	boolean isSiteEvent()
	{
		return pt == null;
	}

	Vector site()
	{
		return s;
	}

	Vector point()
	{
		if (isSiteEvent())
			return s;
		return pt;
	}
}

/**
 * Site events are sorted top to bottom, left to right.
 * If two events have an equal point, if only one of them is a site event, the site event is sorted
 * first because it will cancel any circle events at that location.
 */
class QueueCompare implements Comparator<Event>
{
	@Override
	public int compare(Event a, Event b)
	{
		if (a.point().equals(b.point()))
		{
			if (a.isSiteEvent() && !b.isSiteEvent())
				return -1;

			if (!a.isSiteEvent() && b.isSiteEvent())
				return 1;

			if(a.site().equals(b.site()))
			{
				return 0;
			}

			if (a.site().x < b.site().x)
				return -1;

			return 1;
		}

		if(a.point().y > b.point().y)
			return -1;

		return 1;
	}
}

class Boundary
{
	final Ray ray;
	final Vector siteA;
	final Vector siteB;

	Boundary(Ray ray, Vector siteA, Vector siteB)
	{
		this.ray = ray;
		this.siteA = siteA;
		this.siteB = siteB;
	}
}

interface ISortable
{
	public double primary(double y);
	public double secondary();
}

class BeachlineCompare implements Comparator<ISortable>
{
	public double sweepline = 0;

	@Override
	public int compare(ISortable a, ISortable b)
	{
		// Test in case the value is at negative infinity.
		if (a == b)
			return 0;

		double aP = a.primary(sweepline);
		double bP = b.primary(sweepline);

		if (Math.abs(aP - bP) < Vector.PRECISION)
		{
			double aS = a.secondary();
			double bS = b.secondary();
			if (Math.abs(aS - bS) < Vector.PRECISION)
				return 0;

			if (aS < bS)
				return -1;
			return 1;
		}

		if (aP < bP)
			return -1;

		return 1;
	}
}

/**
 * Arcs are sorted primarily by the projection of its leftmost endpoint onto the sweepline.
 * If two adjacent arcs have the same primary sort value, at least one of them is vanishing
 * from the beachline, and multiple sites are on the voronoi circle around the event point.
 * In this case, the sites are ordered clockwise, which is equivalent to ordering them from
 * left to right, given each arc is currently above the sweepline.
 */
class Arc implements ISortable
{
	final Boundary left;
	final Boundary right;
	final Vector site;

	Arc(Boundary left, Vector site, Boundary right)
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
	Vector circleEvent()
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

class PointQuery implements ISortable
{
	final double x1;
	final double x2;

	PointQuery(double x1, double x2)
	{
		this.x1 = x1;
		this.x2 = x2;
	}

	public double primary(double y)
	{
		return x1;
	}

	public double secondary()
	{
		return x2;
	}
}

class Parabola
{
	final double x2;
	final double x;
	final double c;
	final boolean degenerate;

	Parabola(Vector focus, double directrix)
	{
		degenerate = Math.abs(focus.y - directrix) < Vector.PRECISION;

		double d = (2.0f * (focus.y - directrix));
		x2 = 1.0f / d;
		x = (-2.0f * focus.x ) / d;
		c = focus.x * focus.x / d + (1.0f / 2.0f) * (focus.y + directrix);
	}
}

class Utils
{
	/**
	 * Given a vector defined by origin and destination, returns a perpendicular vector
	 * pointing to the right from the viewpoint of the vector.
	 * Used to generate the bisector direction vector of two adjacent sites.
	 */
	static Vector bisector(Vector orig, Vector dst)
	{
		Vector d = dst.sub(orig);

		double a1 = d.x;
		double a2 = d.y;
		double a3 = 0;

		double b1 = 0;
		double b2 = 0;
		double b3 = 1;

		return new Vector(
			a2 * b3 - a3 * b2,
			a3 * b1 - a1 * b3
		).normalize();
	}

	/**
	 * Given a parabola defined by a focus and directrix, returns the y coordinate of the
	 * parabola at x.
	 */
	static double parabolaY(Vector focus, double directrix, double x)
	{
		double a = focus.x;
		double b = focus.y;

		return (x - a) * (x - a) / (2.0f * (b - directrix)) + (b + directrix) / 2.0f;
	}

	/**
	 * Given two focuses and a directrix, returns the intersection points of the parabolas
	 * provided neither parabola is degenerate.
	 * Both parabolas are expected to be above the directrix.
	 */
	static Vector[] parabolaIntersection(Vector focusA, Vector focusB, double directrix)
	{
		Parabola p1 = new Parabola(focusA, directrix);
		Parabola p2 = new Parabola(focusB, directrix);

		if (p1.degenerate && p2.degenerate)
		{
			return new Vector[] {};
		} else if (p1.degenerate)
		{
			return new Vector[]
			{
				new Vector(focusA.x, parabolaY(focusB, directrix, focusA.x))
			};
		} else if (p2.degenerate)
		{
			return new Vector[]
			{
				new Vector(focusB.x, parabolaY(focusA, directrix, focusB.x))
			};
		}

		double[] xCoords = solveQuadraticFn(p1.x2 - p2.x2, p1.x - p2.x, p1.c - p2.c);

		return new Vector[]
		{
			new Vector(xCoords[0], parabolaY(focusA, directrix, xCoords[0])),
			new Vector(xCoords[1], parabolaY(focusA, directrix, xCoords[1])),
		};
	}

	static double[] solveQuadraticFn(double a, double b, double c)
	{
		double sqrt = Math.sqrt(b * b - 4.0f * a * c);

		return new double[]
		{
			(-b + sqrt) / (2.0f * a),
			(-b - sqrt) / (2.0f * a)
		};
	}
}

public class Fortune
{
	BeachlineCompare beachCmp = new BeachlineCompare();
	TreeSet<Event> queue = new TreeSet<>(new QueueCompare());
	TreeSet<ISortable> beach = new TreeSet<>(beachCmp);
	ArrayList<Edge> edges = new ArrayList<>();

	public class Result
	{
		/**
		 * List of line segments equaling the closed edges of the voronoi diagram.
		 */
		public List<Edge> edges;

		/**
		 * List of rays extending to infinity from origin Edge.a in the direction of Edge.b.
		 */
		public List<Edge> rays;

		Result(List<Edge> edges, List<Edge> rays)
		{
			this.edges = edges;
			this.rays = rays;
		}
	}

	public class Edge
	{
		final Vector a;
		final Vector b;

		public Edge(Vector a, Vector b)
		{
			this.a = a;
			this.b = b;
		}
	}

	/**
	 * @param sites List of seeds of the voronoi diagram.
	 */
	public Fortune(List<Vector> sites)
	{
		sites.forEach(site -> queue.add(new Event(site)));
	}

	void detectEvent(Arc arc)
	{
		Vector point = arc.circleEvent();
		if (point != null)
			queue.add(new Event(point, arc.site));
	}

	void removeEvent(Arc arc)
	{
		Vector point = arc.circleEvent();
		if (point != null)
			queue.remove(new Event(point, arc.site));
	}

	Boundary[] generateBoundaries(Arc arc, Vector site)
	{
		Vector isect = new Vector(site.x, Utils.parabolaY(arc.site, site.y, site.x));
		Ray left = new Ray(isect, Utils.bisector(arc.site, site));
		Ray right = new Ray(isect, Utils.bisector(site, arc.site));
		return new Boundary[] { new Boundary(left, arc.site, site), new Boundary(right, arc.site, site) };
	}
	
	void siteEvent(Event event)
	{
		Vector site = event.site();
		if (beach.isEmpty())
		{
			beach.add(new Arc(null, site, null));
			return;
		}

		// find arc above the event point

		// Set the site x-coordinate at positive infinity in case the event point is at the
		// exact point of the left edge of the arc it's on.
		Arc arc = (Arc)beach.floor(new PointQuery(site.x, Double.POSITIVE_INFINITY));

		// if the arc defines a circle event it's a false alarm. Remove event from qeueue
		removeEvent(arc);

		// split the arc into new sections
		beach.remove(arc);

		Boundary[] bounds = generateBoundaries(arc, site);
		Boundary left = bounds[0];
		Boundary right = bounds[1];

		Arc leftArc = new Arc(arc.left, arc.site, left);
		Arc newArc = new Arc(left, site, right);
		Arc rightArc = new Arc(right, arc.site, arc.right);

		beach.add(leftArc);
		beach.add(newArc);
		beach.add(rightArc);

		// Detect potential circle events
		detectEvent(leftArc);
		detectEvent(rightArc);
	}

	void circleEvent(Event e)
	{
		Vector point = e.point();
		Vector site = e.site();

		// Find and remove the arc being removed and its adjacent arcs
		Arc arc = (Arc)beach.floor(new PointQuery(point.x, site.x));
		Arc larc = (Arc)beach.lower(arc);
		Arc rarc = (Arc)beach.higher(arc);

		beach.remove(arc);
		beach.remove(larc);
		beach.remove(rarc);

		// Remove all events involving the arc including any caused by its boundaries
		removeEvent(larc);
		removeEvent(rarc);

		// rebuild the left and right arcs surrounding the removed arc
		Boundary middle = new Boundary(
			new Ray(point, Utils.bisector(larc.site, rarc.site)),
			larc.site,
			rarc.site
		);

		Arc left = new Arc(larc.left, larc.site, middle);
		Arc right = new Arc(middle, rarc.site, rarc.right);

		beach.add(left);
		beach.add(right);

		// detect events
		detectEvent(left);
		detectEvent(right);

		// add edges of removed arc to result
		edges.add(new Edge(arc.left.ray.origin, point));
		edges.add(new Edge(arc.right.ray.origin, point));
	}

	/**
	 * Processes a single event point per call.
	 * @return true, if not all events have been processed.
	 */
	public boolean process()
	{
		if(!queue.isEmpty())
		{
			Event e = queue.first();
			beachCmp.sweepline = e.point().y;
			if(e.isSiteEvent())
				siteEvent(e);
			else
				circleEvent(e);
		}

		return !queue.isEmpty();
	}

	/**
	 * Immediately generates the voronoi diagram.
	 */
	public Result processAll()
	{
		while(process());
		return result();
	}

	/**
	 * Query the result of the algorithm.
	 * @return the current state of the generated diagram.
	 */
	public Result result()
	{
		ArrayList<Edge> infEdges = new ArrayList<>();

		for (ISortable is : beach)
		{
			Arc arc = (Arc)is;
			if (arc.left == null)
				continue;

			infEdges.add(new Edge(arc.left.ray.origin, arc.left.ray.direction));
		}

		return new Result(edges, infEdges);
	}
}