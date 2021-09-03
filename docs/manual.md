# Manual

## Compiling

Run `gradle build` from the root directory. The resulting jar files are found in `./build/libs`.

## Usage

When launching the program the following parameters can be used:

* `-b` will perform benchmarking instead of running the graphical user interface
* `-p <INT>` controls which power of 10 is the benchmarked tested input size (Default 6)
* `-r <INT>` controls how many times each input size is tested (default 5)
* `-c` sets the initial number of sites to be generated when running the GUI
* `-s <INT>` sets a seed to be used for randomly generating the input. If no seed is provided and `-b` is given a predetermined seed i used
* Any other input will be parsed as decimal numbers for site coordinates in `(x, y), (x, y), ...` order. `java -jar jar.java 0.5 0.4 0.3 0.2` and `java -jar jar.java "0.5 0.4 0.3 0.2"` are equivalent.

When running the program's GUI:
* `ESC` closes the program
* `SPACE` generates a new dataset provided user didn't input any
* `A` Toggle automatic dataset generation and movement of the sweep line
* `N` Immediately proceses the next event point in the queue
* `S` Saves the current dataset to disk
* `P` Pauses the sweepline
* `C` Centers the view at 0.5, 0,5
* `V` Runs a validation on the current dataset
* `1-0` Sets the current number of sites to 1-10
* `- +` Sets the number of sites to `current / 10` or `current * 10` respectively
* `M2` (scroll click) allows panning the camera
* `Scrollwheel` Zoom in and out

## Input

Input to the program is provided as a series of decimal numbers, i.e. 1.0 (instead of 1), with every two numbers forming a site's coordinate.
