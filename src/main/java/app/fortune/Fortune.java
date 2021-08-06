package app.fortune;

import app.vector.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.TreeSet;

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

			if (a.site().equals(b.site()))
			{
				return 0;
			}

			if (a.site().x < b.site().x)
				return -1;

			return 1;
		}

		if (a.point().y > b.point().y)
			return -1;

		return 1;
	}
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

		double aP = a.leftX(sweepline);
		double bP = b.leftX(sweepline);

		if (Math.abs(aP - bP) < Vector.PRECISION)
		// if (aP == bP)
		{
			double aS = a.rightX(sweepline);
			double bS = b.rightX(sweepline);
			if (Math.abs(aS - bS) < Vector.PRECISION)
			// if (aS == bS)
			{
				double aT = a.siteX();
				double bT = b.siteX();

				if (Math.abs(aT - bT) < Vector.PRECISION)
				// if (aT == bT)
					return 0;

				if (aT < bT)
					return -1;
				return 1;
			}

			if (aS < bS)
				return -1;
			return 1;
		}

		if (aP < bP)
			return -1;

		return 1;
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

	public double leftX(double y)
	{
		return x1;
	}

	public double rightX(double y)
	{
		return x1;
	}

	public double siteX()
	{
		return x2;
	}
}

public class Fortune
{
	BeachlineCompare beachCmp = new BeachlineCompare();
	public TreeSet<Event> queue = new TreeSet<>(new QueueCompare());
	public TreeSet<ISortable> beach = new TreeSet<>(beachCmp);
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
		public final Vector a;
		public final Vector b;

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
		return new Boundary[] { new Boundary(left, arc.site, site), new Boundary(right, site, arc.site) };
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
		Arc arc = (Arc) beach.floor(new PointQuery(site.x, Double.POSITIVE_INFINITY));

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

	String border(Arc a, double y)
	{
		return String.format("%.3f", a.leftX(y)) + " ; " + String.format("%.3f", a.rightX(y));
	}

	void circleEvent(Event e)
	{
		Vector point = e.point();
		Vector site = e.site();
		Vector circlePoint = new Vector(point.x, Utils.parabolaY(site, point.y, point.x));

		// Find and remove the arc being removed and its adjacent arcs
		Arc arc = (Arc) beach.floor(new PointQuery(point.x, site.x));
		Arc larc = (Arc) beach.lower(arc);
		Arc rarc = (Arc) beach.higher(arc);

		System.out.println("Removing:\n\t"
			+ "left  " + larc + " | " + border(larc, point.y)
			+ "\n\t"
			+ "mid   " + arc + " | " + border(arc, point.y)
			+ "\n\t"
			+ "right " + rarc + " | " + border(rarc, point.y)
		);

		beach.remove(arc);
		beach.remove(larc);
		beach.remove(rarc);

		// Remove all events involving the arc including any caused by its boundaries
		removeEvent(larc);
		removeEvent(rarc);

		// rebuild the left and right arcs surrounding the removed arc
		Vector direction = arc.left.ray.direction.normalize()
			.add(arc.right.ray.direction.normalize())
			.normalize();

		Vector bisector = Utils.bisector(larc.site, rarc.site);

		if (Vector.dot(direction, bisector) < 0)
			bisector = bisector.neg();

		Boundary middle = new Boundary(
			new Ray(circlePoint, bisector),
			larc.site,
			rarc.site
		);

		Arc left = new Arc(larc.left, larc.site, middle);
		Arc right = new Arc(middle, rarc.site, rarc.right);

		System.out.println("Adding:\n\t"
			+ "left  " + left + " | " + border(left, point.y)
			+ "\n\tright " + right + " | " + border(right, point.y));

		beach.add(left);
		beach.add(right);

		// detect events
		detectEvent(left);
		detectEvent(right);

		// add edges of removed arc to result
		edges.add(new Edge(arc.left.ray.origin, circlePoint));
		edges.add(new Edge(arc.right.ray.origin, circlePoint));
	}

	/**
	 * Processes a single event point per call.
	 * @return true, if not all events have been processed.
	 */
	public boolean process()
	{
		if (!queue.isEmpty())
		{
			Event e = queue.pollFirst();
			beachCmp.sweepline = e.point().y;
			if (e.isSiteEvent())
			{
				System.out.println("Site event " + e.point());
				siteEvent(e);
			}
			else
			{
				System.out.println("Circle event " + e.point());
				circleEvent(e);
			}
		}

		return !queue.isEmpty();
	}

	public Result processTo(double y)
	{
		while (!queue.isEmpty())
		{
			if (queue.first().point().y < y)
				break;

			process();
		}

		setSweepline(y);
		return result();
	}

	/**
	 * Immediately generates the voronoi diagram.
	 */
	public Result processAll()
	{
		while (process());
		return result();
	}

	public Vector peek()
	{
		if (!queue.isEmpty())
			return queue.first().point();
		return null;
	}

	public double sweepLine()
	{
		return beachCmp.sweepline;
	}

	public void setSweepline(double y)
	{
		if (queue.isEmpty() ||
			(y < beachCmp.sweepline && y > queue.first().point().y))
			beachCmp.sweepline = y;
	}

	/**
	 * Query the result of the algorithm.
	 * @return the current state of the generated diagram.
	 */
	public Result result()
	{
		ArrayList<Edge> resEdges = new ArrayList<>();
		for (Edge edge : edges)
			resEdges.add(edge);

		ArrayList<Edge> infEdges = new ArrayList<>();

		for (ISortable is : beach)
		{
			Arc arc = (Arc) is;
			if (arc.left == null)
				continue;

			infEdges.add(new Edge(arc.left.ray.origin, arc.left.ray.direction));
		}

		return new Result(resEdges, infEdges);
	}
}
