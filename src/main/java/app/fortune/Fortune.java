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

public class Fortune
{
	Vector eventPoint = new Vector(0, 0);
	public PriorityQueue<Event> queue = new PriorityQueue<>(new QueueCompare());
	public Tree<Arc> beach = null;
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
		sites.forEach(site -> queue.push(new Event(site)));
		checkInput();
	}

	/**
	 * @param sites Array of seeds of the voronoi diagram.
	 */
	public Fortune(Vector[] sites)
	{
		for (int i = 0; i < sites.length; i++)
			queue.push(new Event(sites[i]));
		checkInput();
	}

	void checkInput()
	{
		if (queue.size() <= 1)
			return;
		Event e = queue.pop();
		if (Math.abs(queue.peek().point().y - e.point().y) < Vector.PRECISION)
		{
			System.out.println("Corrected input.");
			queue.push(new Event(new Vector(e.point().x, e.point().y + 4.0 * Vector.PRECISION)));
		}
		else
			queue.push(e);
	}

	void detectEvent(Tree<Arc>.Node node)
	{
		Arc arc = node.value();
		Vector point = arc.circleEvent();
		if (point != null)
			arc.event = queue.push(new Event(point, arc.site, node));
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

	Boundary generateMergedBoundary(Arc left, Arc mid, Arc right, Vector origin)
	{
		Vector direction = mid.left.ray.direction
			.add(mid.right.ray.direction)
			.normalize();

		Vector bisector = Utils.bisector(left.site, right.site);

		if (Vector.dot(direction, bisector) < 0)
			bisector = bisector.neg();

		return new Boundary(
			new Ray(origin, bisector),
			left.site,
			right.site
		);
	}

	Tree<Arc>.Node findArc(Vector point)
	{
		Tree<Arc>.Node result = null;
		Tree<Arc>.Node current = beach.root();

		while (current != null)
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
			beach = new Tree(new Arc(null, site, null));
			return;
		}

		// find arc above the event point

		// Set the site x-coordinate at positive infinity in case the event point is at the
		// exact point of the left edge of the arc it's on.
		Tree<Arc>.Node node = findArc(site);
		Arc arc = node.value();

		// if the arc defines a circle event it's a false alarm. Remove event from qeueue
		removeEvent(arc);

		// split the arc into new sections
		Boundary[] bounds = generateBoundaries(arc, site);
		Boundary left = bounds[0];
		Boundary right = bounds[1];

		Arc leftArc = new Arc(arc.left, arc.site, left);
		Arc rightArc = new Arc(right, arc.site, arc.right);
		arc = new Arc(left, site, right);

		node = beach.replace(node, arc);
		detectEvent(beach.addPrevious(node, leftArc));
		detectEvent(beach.addNext(node, rightArc));
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

		beach.delete(node);
		// Remove all events involving the arc including any caused by its boundaries
		removeEvent(larc);
		removeEvent(rarc);

		// rebuild the left and right arcs surrounding the removed arc
		Boundary middle = generateMergedBoundary(larc, arc, rarc, circlePoint);
		Arc left = new Arc(larc.left, larc.site, middle);
		Arc right = new Arc(middle, rarc.site, rarc.right);

		lNode = beach.replace(lNode, left);
		rNode = beach.replace(rNode, right);

		detectEvent(lNode);
		detectEvent(rNode);

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
			Event e = queue.pop();
			eventPoint = e.point();
			if (e.isSiteEvent())
				siteEvent(e);
			else
				circleEvent(e);
		}

		return !queue.isEmpty();
	}

	/**
	 * Processes all event points before the y coordinate.
	 */
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
		while (process());
		return result();
	}

	/**
	 * Returns the next event in the queue if any.
	 */
	public Vector peek()
	{
		if (!queue.isEmpty())
			return queue.peek().point();
		return null;
	}

	/**
	 * Returns the height of the sweep line
	 */
	public double sweepLine()
	{
		return eventPoint.y;
	}

	/**
	 * For visualization purposes.
	 * Set the sweepline height for the algorithm if it is between the current height and the
	 * y-coordinate of the next event point.
	 */
	public void setSweepline(double y)
	{
		if (queue.isEmpty() ||
			(y < sweepLine() && y > queue.peek().point().y))
			eventPoint = new Vector(sweepLine(), y);
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
}
