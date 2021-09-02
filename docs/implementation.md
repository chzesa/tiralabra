# Implementation

The core of the algorithm is the fortune package:

* `Arc`: represents the segments of the parabola currently defining the beachline above the sweepline.
* `Boundary`: are the left and right edges which the Arc's parabola is tracing by intersecting the arcs to its left and right.
* `Fortune`: is the object implementing the algorith itself. For demo and debugging purposes, the algorithm can be computed incrementally point by point.

The other classes are mostly utility, either math related or serialization/io, etc.

A notable feature of the `PriorityQueue` and `Tree` classes is the ability to hold direct references to the values stored within. These references are not invalidated until they are actually removed from the structure, and this allows deletions from the queue which isn't normally possible.

## Analysis

The main loop of the algorithm consists of handling either site events (i.e. the input) and circle events.

### Pseudocode

```
beach = new Tree;
queue = new PriorityQueue

for point in input:
	queue.add(new Event(point))

// primary loop 
while queue is not empty:
	event = queue.pop
	if event is siteEvent:
		handleSiteEvent(event)
	else
		handleCircleEvent(event)

handleSiteEvent(event):
	arc = beach.find(event.point)
	queue.remove(arc.event)

	[leftArc, newArc, rightArc] = generateNewArcs(arc.site, event.site)
	beach.replace(arc, newArc)
	beach.insertPrevious(newArc, leftArc)
	beach.insertNext(newArc, rightArc)

	detectEvent(leftArc)
	detectEvent(rightArc)

detectEvent(arc)
	if boundaries of arc intersect below sweepline:
		queue.add(new Event(arc))

handleCircleEvent(event):
	arc = event.arc
	leftArc = arc.previous()
	rightArc = arc.next()

	queue.remove(leftArc.event)
	queue.remove(rightArc.event)

	beach.delete(arc)

	[left, right] = generateMergedArcs(leftArc, arc, rightArc)
	beach.replace(leftArc, left)
	beach.replace(rightArc, right)

	detectEvent(left)
	detectEvent(right)

	addEdgesToResult(arc)
```

### PriorityQueue

The priority queue is implemented as a heap with an array, with the successors of node at index `i` being located at `i * 2` and `i * 2 + 1`. The values are stored as references, and the object's index is stored inside the reference. When nodes are swapped in the table their current indices are updated, so the data structure supports deletions via references. Each item in the structure contains the associated data as well as an index so the storage of a single item is constant.

When deleting a node, it is first swapped with the last element in the table and then popped off. The swapped element is then either raised or lowered to maintain the heap's invariant. This correction is bounded by the maximum number of swaps between the node's predecessors or successors and therefore runs in `O(log n)` time.

The operations on the queue have` O(log n)` runtime and it uses `O(n)` storage.

### Tree

The tree is a variant of an AVL tree, and is balanced after each insertion and deletion operation. Instead of supporting generic addition and removal, addition is done exclusively by inserting new nodes as either directly next or previous to an existing node. The nodes further maintain references to their ordered next and previous nodes. Correcting these references on addition and removal takes constant time.

Each node in the tree stores references to its ancestor; left and right child nodes; next and previous nodes in order; its depth for rebalancing and the stored value, so the storage requirement of a single node is constant.

Addition is done by traversing to the leftmost descendant of the referenced node's right subtree, and the new node is added as the left child of that descendant for inserting a node next to an existing one (and vice versa for inserting previous to). Given that the tree is balanced, the traversal down to a leaf node, and traversal up to a root to rebalance it, are bound by the tree's height which is `log n` as per the invariant of an AVL tree.

Deletion works as normal, swapping the node with its next one if it has two children, and the node's child being raised in its place before the tree is rebalanced.

Therefore, the operations on the tree have `O(log n)` runtime and using `O(n)` storage.

### Runtime

The algorithm first inserts each point into the priority queue taking `O(n log n)` time.

Given that the graph of a voronoi diagram is connected, and each point in the input generates a single face, the number of vertices has an upper bound linear to the number of faces as per Euler's theorem. Since vertices are handled as circle events the primary loop is bounded by `O(n)`.

The handleSiteEvent function first searches the tree for an arc directly above the new site. This is implemented by traversing the tree down from its root until a node without children is found so the operation takes `O(log n)` time.

In the handleCircleEvent function, generating the new arcs and detecting the intersection in detectEvent take constant time, while the function performs a constant number of operations on `O(log n)` data structures.

Generating the merged arcs in the handleCircleEvent function takes constant time, while the function otherwise performs a constant number of operations on `O(log n)` data structures, so the function's runtime is likewise `O(log n)`.

In total, the above analysis yields an overall runtime of `O(n log n)`.

### Storage

As a reminder, each element in the tree and priority queue structures requires a constant amount of storage.

Since out of the two event types only site events increase the amount of storage used - with each site event generating two new arcs on the beachline and two potential events - the maximal storage requirement is reached when all site events are handled but no circle events have occurred. Taking into account that each arc in the beachline has a potential event associated with it, the maximum size of the beachline and the queue is `2n - 1`. Additionally, each arc stores two boundaries, and each boundary is shared by two arcs, the maximum number of boundaries is `2n - 1`.

Therefore, the algorithm has a `O(n)` storage bound.

## Deficiencies and potential improvements

The algorithm outputs a list of rays and line segments. Typically the output is a doubly connected edge list structure which has logical structure and supports traversal. Operations on such a stucture take constant time so building one during the algorithm's runtime would not (significantly) affect its runtime.

Additionally, the output contains zero-length edges which are generated when multiple arcs vanish at a single event point. These could be ignored but they are not checked for currently.

Instances involving two or more intial points being horizontally parallel, which causes all parabola on the beachline at that point to be degenerate, is not handled. Instead, the input is checked for this case and the first point is shifted upwards a very small amount.

## Sources

https://en.wikipedia.org/wiki/Planar_graph#Euler's_formula

https://www.cs.helsinki.fi/u/ahslaaks/tirakirja/
