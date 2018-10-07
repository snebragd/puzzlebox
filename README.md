# puzzlebox
brute force puzzle box solver

Hack to solve a wooden puzzle I got as a gift. Gave up on solving it manually and resorted to this brute force java hack. I honestly don't remember why I wrote this in Java. Wrong tool for the job. Whatever, it works.

Should be straight forward to enter the pieces of any M*N*O sized puzzle and get it solved. Specify puzzle pieces as per the examples under in the puzzles directory. 

## Example usage
```
$ java PBox puzzles/puzzle1.txt
Solving 3x3x3 puzzle with 6 pieces
Solution 1
L0	L1	L2
F D D 	B B B 	E B C
F F D 	F A D 	E C C
A A A 	F A C 	E E C
$
```

## Make CAD files
You can also get it to output OpenSCAD files for each piece of the puzzle if you are desiring to 3D-print it:
```
$ java PBox puzzles/puzzle1.txt /tmp/puzzle1
```

This will give you six .scad files in /tmp named puzzle1_A.scad, puzzle1_B.scad, and so on.

To conveniently turn those .scad into .stl for printing you can do the following:
```
$ ls /tmp/*.scad | sed 's/\(.*\)scad/\1stl \1scad/' | xargs -n 2 OpenSCAD -o
```

**Happy puzzling!**