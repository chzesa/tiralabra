# Week 2

Figuring out an alternative scheme for storing event points and arrays in default data structures turned out to be more time consuming that I had anticipated. The example implementation given in Computational Geometry describes a method which involves altering the inner nodes when adding and removing nodes, and adding entire subtrees at once since leaves and the guiding nodes are described as conceptually different.

The solution I arrived at is sorting the arcs by projecting the left boundary its parabola is drawing out onto the sweepline, and in the case of a circle event, the converging arcs are also ordered by the x-coordinate of their site.

The default data structures do not support deleting objects by pointers either, which is why I used a tree for the initial implementation instead of a priority queue. While this isn't as optimal, the implementation still has O(n log n) runtime.

A problem I haven't had time to tackle is implementing tests for the actual voronoi diagram generation. It's likely that I must write a class for analysing the resulting diagram, and implement tests for that class, before I can actually test the algorithm's output.

Given that I failed to add any visualization this week, I'm going to have to implement it soon so I can gain some insight into my current implementation of the algorithm. I'm not expecting the implementations of the helper data structures to be all that difficult given that I've created such implementations in the past.