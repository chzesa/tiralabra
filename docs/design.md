# Design Document

A voronoi diagram is a division of a plane to cells based on the sites such that the boundaries of each cell contain all points in the plane which are closest to that site. The program implements Fortune's algorithm to generate a voronoi diagram for a given set of sites on a plane.¹²

Fortune's algorithm is a sweep line algorithm which processes events in order, and maintains information about how the voronoi diagram under construction intersects the sweep line as new sites appear and disappear. The events, comprised of the voronoi cell sites and intersections between their boundaries, are stored in a priority queue for quick insertions and deletions (using pointers). The state of the sweep line is maintained in a balanced binary search tree for efficient insertion and removal, as well as quick access to neighbouring boundaries for detecting future events whenever boundaries appear and disappear on the sweep line.²³

## Storage and runtime analysis

The algorithm processes a number of events linear to the complexity of the diagram: each site and vertex of the voronoi diagram is processed once. The vertices are generated and added to the queue during the algorithm, and any incorrect events (such as a boundary intersection which occurs inside a newly added site) can be removed from the queue while the correct events are processed. Processing each vertex and site event consists of a constant number of operations on the data structures used²³. It follows that the runtime is O(n log n) and storage O(n) given the properties of a priority queue and binary search tree.⁴

## Input and output

The input for the program is a set of points (sites) on a plane. The output is a visualization of the resulting voronoi diagram.

## Sources

1: https://en.wikipedia.org/wiki/Voronoi_diagram

2: https://link.springer.com/article/10.1007/BF01840357

3: https://link.springer.com/book/10.1007%2F978-3-540-77974-2

4: https://www.cs.helsinki.fi/u/ahslaaks/tirakirja/

## Misc

This project is for a bachelor's in science (bSc) level course at the University of Helsinki.

The project is implemented in Java, with documentation etc. in English.