This week I changed the algorithm to use custom data structures instead of the java ones. It still has many cases in which the algorithm breaks down, and one such case is when a new site is right below an intersection point of the two arcs which leads to a degenerate case. Given how it's the ordering of the nodes in the tree which becomes confused, now that I have the custom data structures implemented, I'm hoping at least some edge cases will be eliminated by using pointers between the events and arcs.

Regardless, I should now be able to generate working datasets for performance testing, though the algorithm still falls short of the expected since the custom tree structure isn't balanced yet.