# Visibility Graph Demo Program
This project contains my personal exploration of using visibility graphs as a 
tool for navigating obstacle fields in a simulation. You can find most of the
relevant files in the folder linked below:

- [VisibilityGraph](core/src/com/mygdx/game/visibilitygraph)

## How to Run
This project was built using the libgdx library on Java 19.
You can either clone the repo and build it yourself with Gradle, or download the
compiled jar file [vis-graph-demo-1.0.jar](standalone/vis-graph-demo-1.0.jar).
To execute the jar file, run: 
`java -jar vis-graph-demo-1.0.jar`

## Known Issues / WIP
- Currently, the program does not properly handle overlapping obstacles. In the 
future I plan to do this using the GJK algorithm. For now, when obstacles 
overlap, it is possible that a shortest path will run through the interior of
the objects.