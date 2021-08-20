# Testing

The software is mostly tested with randomly generated data, initially with large datasets of known data which produces correct output when the algorithm relied on default data structures, as well as a set of problematic inputs which did not produce correct output despite the default implementations.

The aforementioned datasets are used for regression testing. Further unit tests for the algorithm itself are focused around edge cases, such as horizontally and vertically parallel sites, as well as overlapping circle events and new sites appearing at beachline intersection points.

Currently, with the two tests which aren't passed (testing a case in which only degenerate parabola exist at the time of adding a new site), the test coverage is around 80% or higher for the components relevant to the algorithm implementation, but most of the coverage is purely from testing the algorithm itself instead of unit testing of the supplementary data structures.

A coverage report can be found in /docs/coverage

# Performance testing

Testing the algorithm's performance will be done using randomly generated data, either with seeded random number generation or by reading the input from a pregenerated file.