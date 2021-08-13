# Implementation

The core of the algorithm is the fortune package:

* `Arc`: represents the segments of the parabola currently defining the beachline above the sweepline.
* `Boundary`: are the left and right edges which the Arc's parabola is tracing by intersecting the arcs to its left and right.
* `Fortune`: is the object implementing the algorith itself. For demo and debugging purposes, the algorithm can be computed incrementally point by point.

The other classes are mostly utility, either math related or serialization/io, etc.

A notable feature of the `PriorityQueue` and `Tree` classes is the ability to hold direct references to the values stored within. These references are not invalidated until they are actually removed from the structure, and this allows deletions from the queue which isn't normally possible.