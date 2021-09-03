# Testing

## Unit tests

The software is mostly tested with randomly generated data, initially with large datasets of known data which produces correct output when the algorithm relied on default data structures, as well as a set of problematic inputs which did not produce correct output despite the default implementations.

The aforementioned datasets are used for regression testing. Further unit tests for the algorithm itself are focused around edge cases such as new events overlapping with existing ones or appearing exactly along the beachline boundaries. 

Tree and Priority queue do not have tests, but their incorrectness would cause the algorithm to fail as well and the algorithm's test output is checked.

A coverage report can be found in /docs/coverage

# Performance testing

The algorithm is benchmarked with randomly generated data by running it with the `-b` switch. If no seed is given the algorithm uses a predetermined seed for repeatability. See the [manual](https://github.com/chzesa/tiralabra/blob/master/docs/manual.md) for more details on customizing the test parameters.

Below is the result of a benchmark which was run as `java -jar build/libs/tira-all.jar -b -p 7 -r 10`:

Measured averages
```
     input size   average time
             10           73us
            100          370us
           1000         3061us
          10000        37650us
         100000       397164us
        1000000      5978397us
       10000000     88527941us

```

![Bar graph of results](https://github.com/chzesa/tiralabra/blob/master/docs/time_absolute.png)

![Bar graph of results relative to input size](https://github.com/chzesa/tiralabra/blob/master/docs/time_relative.png)