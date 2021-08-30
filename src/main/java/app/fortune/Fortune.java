package app.fortune;

import app.vector.*;
import app.pq.*;
import app.tree.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

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
		int result = Double.compare(b.point().y, a.point().y);
		if (result == 0)
		{
			result = Double.compare(a.point().x, b.point().x);
			if (result == 0)
			{
				if (a.isSiteEvent() && !b.isSiteEvent())
					result = -1;

				if (!a.isSiteEvent() && b.isSiteEvent())
					result = 1;
			}
			return result;
		}
		return result;
	}
}

class BeachlineCompare implements Comparator<Arc>
{
	public Vector ep = new Vector(0, 0);

	boolean equals(double a, double b)
	{
		return Math.abs(a - b) < Vector.PRECISION;
	}

	static double angle(Vector vec)
	{
		Vector down = new Vector(0, -1);
		double angle = Vector.angle(vec, down);
		if (angle < 0)
			angle += 2 * Math.PI;
		return angle;
	}

	@Override
	public int compare(Arc a, Arc b)
	{
		// Test in case the value is at negative infinity.
		if (a == b)
			return 0;

		double aP = a.left(ep.y).x;
		double bP = b.left(ep.y).x;

		if (equals(aP, bP))
		{
			double aS = a.right(ep.y).x;
			double bS = b.right(ep.y).x;
			if (equals(aS, bS))
			{
				Vector vA = a.site().sub(ep);
				Vector vB = b.site().sub(ep);
				double angleA = angle(vA);
				double angleB = angle(vB);

				if (equals(angleA, angleB))
					return 0;

				return Double.compare(angleA, angleB);
			}

			return Double.compare(aS, bS);
		}

		return Double.compare(aP, bP);
	}
}

public class Fortune
{
	BeachlineCompare beachCmp = new BeachlineCompare();
	public PriorityQueue<Event> queue = new PriorityQueue<>(new QueueCompare());
	public Tree<Arc> beach = null;
	ArrayList<Edge> edges = new ArrayList<>();
	public boolean debug = false;
	int limit;

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
		sites.forEach(site -> queue.push(new Event(site)));
		limit = sites.size() * 1000;
	}

	public Fortune(Vector[] sites)
	{
		for (int i = 0; i < sites.length; i++)
			queue.push(new Event(sites[i]));

		limit = sites.length * 1000;
	}

	void detectEvent(Tree<Arc>.Node node)
	{
		Arc arc = node.value();
		Vector point = arc.circleEvent();
		if (point != null)
		{
			print("Detected circle event at " + point + " for " + arc.site);
			arc.event = queue.push(new Event(point, arc.site, node));
		}
	}

	void removeEvent(Arc arc)
	{
		if (arc != null && arc.event != null)
		{
			queue.delete(arc.event);
			arc.event = null;
		}
	}

	Boundary[] generateBoundaries(Arc arc, Vector site)
	{
		Vector isect = Utils.parabolaPt(arc.site, site.y, site.x);
		Ray left = new Ray(isect, Utils.bisector(arc.site, site));
		Ray right = new Ray(isect, Utils.bisector(site, arc.site));
		return new Boundary[] { new Boundary(left, arc.site, site), new Boundary(right, site, arc.site) };
	}

	Tree<Arc>.Node findArc(Vector point)
	{
		Tree<Arc>.Node result = null;
		Tree<Arc>.Node current = beach.root();

		while(current != null)
		{
			Vector end = current.value().left(point.y);
			if (point.x < end.x)
				current = current.left();
			else
			{
				result = current;
				current = current.right();
			}
		}

		return result;
	}
	
	void siteEvent(Event event)
	{
		Vector site = event.site();
		if (beach == null)
		{
			beach = new Tree(beachCmp, new Arc(null, site, null));
			return;
		}

		// find arc above the event point

		// Set the site x-coordinate at positive infinity in case the event point is at the
		// exact point of the left edge of the arc it's on.
		Tree<Arc>.Node node = findArc(site);
		Arc arc = node.value();

		// if the arc defines a circle event it's a false alarm. Remove event from qeueue
		removeEvent(arc);

		print("Removing:\n\t" + border(arc, sweepLine()) + " | " + arc);

		// split the arc into new sections

		Boundary[] bounds = generateBoundaries(arc, site);
		Boundary left = bounds[0];
		Boundary right = bounds[1];

		if (Math.abs(arc.site.y - site.y) < Vector.PRECISION && arc.right == null)
		{
			Arc newArc = new Arc(left, site, null);
			arc = new Arc(arc.left, arc.site, left);
			node = beach.replace(node, arc);

			detectEvent(node);
			detectEvent(beach.addNext(node, newArc));
			return;
		}

		Arc leftArc = new Arc(arc.left, arc.site, left);
		Arc rightArc = new Arc(right, arc.site, arc.right);
		arc = new Arc(left, site, right);

		print("Adding:\n\t"
			+ "left  " + border(leftArc, sweepLine()) + " | " + leftArc
			+ "\n\t"
			+ "mid   " + border(arc, sweepLine()) + " | " + arc
			+ "\n\t"
			+ "right " + border(rightArc, sweepLine()) + " | " + rightArc
		);

		node = beach.replace(node, arc);
		detectEvent(beach.addPrevious(node, leftArc));
		detectEvent(beach.addNext(node, rightArc));

		check(leftArc, sweepLine());
		check(arc, sweepLine());
		check(rightArc, sweepLine());
	}

	void circleEvent(Event e)
	{
		Vector point = e.point();
		Vector site = e.site();
		Vector circlePoint = Utils.parabolaPt(site, sweepLine(), point.x);

		// Find and remove the arc being removed and its adjacent arcs
		Tree<Arc>.Node lNode, node, rNode;

		node = e.arc;
		lNode = node.previous();
		rNode = node.next();

		Arc arc = node.value();
		Arc larc = lNode.value();
		Arc rarc = rNode.value();

		print("Removing:\n\t"
			+ "left  " + border(larc, sweepLine()) + " | " + larc
			+ "\n\t"
			+ "mid   " + border(arc, sweepLine()) + " | " + arc
			+ "\n\t"
			+ "right " + border(rarc, sweepLine()) + " | " + rarc
		);

		beach.delete(node);
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

		print("Adding:\n\t"
			+ "left  " + border(left, sweepLine()) + " | " + left
			+ "\n\t"
			+ "right " + border(right, sweepLine()) + " | " + right
			+ "\n\t"
			+ "with bisector " + middle.ray
		);

		lNode = beach.replace(lNode, left);
		rNode = beach.replace(rNode, right);

		detectEvent(lNode);
		detectEvent(rNode);

		// add edges of removed arc to result
		edges.add(new Edge(arc.left.ray.origin, circlePoint));
		edges.add(new Edge(arc.right.ray.origin, circlePoint));
		check(left, sweepLine());
		check(right, sweepLine());
	}

	/**
	 * Processes a single event point per call.
	 * @return true, if not all events have been processed.
	 */
	public boolean process()
	{
		if (!queue.isEmpty())
		{
			Event e = queue.pop();
			print("Setting EP to: " + e.point().toString() + " site ? " + e.isSiteEvent());
			beachCmp.ep = e.point();
			if (e.isSiteEvent())
			{
				print("Site event " + e.point());
				siteEvent(e);
			}
			else
			{
				print("Circle event " + e.point() + " of " + e.site());
				circleEvent(e);
			}
		}

		return !queue.isEmpty();
	}

	public Result processTo(double y)
	{
		while (!queue.isEmpty())
		{
			if (queue.peek().point().y < y)
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
		while (process() && limit-- > 0);
		return result();
	}

	public Vector peek()
	{
		if (!queue.isEmpty())
			return queue.peek().point();
		return null;
	}

	public double sweepLine()
	{
		return beachCmp.ep.y;
	}

	public void setSweepline(double y)
	{
		if (queue.isEmpty() ||
			(y < beachCmp.ep.y && y > queue.peek().point().y))
			beachCmp.ep = new Vector(beachCmp.ep.x, y);
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

		if (beach != null)
			beach.forEach(is ->
			{
				Arc arc = (Arc) is;
				if (arc.left != null)
					infEdges.add(new Edge(arc.left.ray.origin, arc.left.ray.direction));
			});

		return new Result(resEdges, infEdges);
	}

	String border(Arc a, double y)
	{
		return String.format("%.3f", a.left(y).x) + " ; " + String.format("%.3f", a.right(y).x);
	}

	void check(Arc a, double y)
	{
		double left = a.left(y).x;
		double right = a.right(y).x;
		if (Math.abs(left - right) > Vector.PRECISION && right < left)
			throw new Error(beachCmp.ep + "\n\t" + border(a, y) + " | " + a.toString());
	}

	void print(String s)
	{
		if (debug)
			System.out.println(s);
	}
}
