package com.mygdx.game.visibilitygraph;

import com.badlogic.gdx.math.Vector2;

import java.util.*;


/**
 * Class defining a visibility graph to be used for path finding in a 2D plane
 * full of circular obstacles.
 */
public class VisibilityGraph {

    private final int STARTPOINT = 0;
    private final int ENDPOINT = 1;
    private ArrayList<VGEdge> obstacleEdges;
    private ArrayList<VGEdge> visibilityEdges;
    private ArrayList<VGVertex> vertices;
    private HashMap<Integer, VGObstacle> obstacles;

    /**
     * Construct a Visibility Graph.
     *
     * @param startPoint starting point of navigating agent
     * @param endPoint destination point of navigating agent
     * @param obstacles a set of obstacles defined as vertex arrays (vertices
     *                  should be inserted in counter-clockwise order)
     * @param agentRadius radius of navigating agent
     */
    public VisibilityGraph(
            Vector2 startPoint,
            Vector2 endPoint,
            Set<ArrayList<Vector2>> obstacles,
            float agentRadius)
    {

        vertices = new ArrayList<>();
        vertices.add(new VGVertex(startPoint, 0)); // Index POINT.START
        vertices.add(new VGVertex(endPoint, 0));   // Index POINT.END

        // Setup Obstacles
        this.obstacles = new HashMap<>();
        obstacleEdges = new ArrayList<>();
        for (ArrayList<Vector2> vertices : obstacles) {
            VGObstacle obstacle = new VGObstacle(vertices, agentRadius);
            this.obstacles.put(obstacle.getId(), obstacle);

            obstacleEdges.addAll(obstacle.getEdges());
            this.vertices.addAll(obstacle.getVertices());
        }

        visibilityEdges = new ArrayList<>();
        constructWithNaive();
    }

    /**
     * Construction algorithm for visibility graph using the naive
     * solution O(n^3).
     */
    private void constructWithNaive() {

        int numVertices = vertices.size();

        for (int i = 0; i < numVertices; i++) {
            VGVertex center = vertices.get(i);
            for (int j = i; j < numVertices; j++) {
                if (i == j) continue;
                VGVertex vertex = vertices.get(j);

                // Skip pairs belonging to same non-zero group id
                if (vertex.groupID == center.groupID && vertex.groupID != 0) {
                    continue;
                }

                boolean intersects = false;
                VGEdge visEdge = new VGEdge(center, vertex, false);
                for (VGEdge obstacleEdge : obstacleEdges) {
                    if (visEdge.incidentTo(obstacleEdge)) continue;
                    if (visEdge.intersects(obstacleEdge)) {
                        intersects = true;
                        break;
                    }
                }

                if (!intersects) {
                    visibilityEdges.add(visEdge);
                    visEdge.a.addNeighbor(visEdge.b, visEdge);
                    visEdge.b.addNeighbor(visEdge.a, visEdge);
                }

            }
        }
    }

//    /**
//     * Private construction algorithm for visibility graph using Lee's
//     * solution O(n^2 log n). TO BE IMPLEMENTED!!!
//     */
//    private void constructWithLees() {
//        for (int i = 0; i < vertices.size(); i++) {
//            VGVertex center = vertices.get(i);
//
//            // Order vertices by angle from X-Axis
//            ConcurrentSkipListMap<Float, VGVertex> orderedVertices
//                    = new ConcurrentSkipListMap<>();
//
//            for (int j = 0; j < vertices.size(); j++) {
//                if (i == j) continue;
//                VGVertex vertex = vertices.get(j);
//                orderedVertices.put(vertex.getAngleTo(center.pos), vertex);
//
//                // TODO: Finish Lee's Algorithm
//            }
//        }
//    }

    /**
     * Get start point of navigating agent.
     *
     * @return {@link Vector2} representing the agent start point
     */
    public Vector2 getStartPoint() {
        return vertices.get(STARTPOINT).pos;
    }

    /**
     * Get end point of navigating agent.
     *
     * @return {@link Vector2} representing the agent end point
     */
    public Vector2 getEndPoint() {
        return vertices.get(ENDPOINT).pos;
    }

    /**
     * Get vertices of Visibility graph.
     *
     * @return {@link ArrayList} of {@link VGVertex} objects
     */
    public ArrayList<VGVertex> getVertices() {
        return vertices;
    }

    /**
     * Get a list of all edges in the Visibility Graph.
     *
     * @return {@link ArrayList} of {@link VGEdge} objects
     */
    public ArrayList<VGEdge> getAllEdges() {
        ArrayList<VGEdge> edges = new ArrayList<>(obstacleEdges);
        edges.addAll(visibilityEdges);
        return edges;
    }

    /**
     * Get list of obstacle edges in Visibility Graph.
     *
     * @return {@link ArrayList} of {@link VGEdge} objects
     */
    public ArrayList<VGEdge> getObstacleEdges() {
        return obstacleEdges;
    }

    /**
     * Get list of visibility edges in Visibility Graph.
     *
     * @return {@link ArrayList} of {@link VGEdge} objects
     */
    public ArrayList<VGEdge> getVisibilityEdges() {
        return visibilityEdges;
    }

    /**
     * Perform an A* search algorithm to find the shortest path between the
     * start and end point of this visibility graph. This returns a list of
     * destination points to get to the end goal from the start point.
     *
     * @return {@link ArrayList} of {@link Vector2} objects
     */
    public ArrayList<Vector2> aStar() {

        VGVertex startPoint = vertices.get(STARTPOINT);
        VGVertex endPoint = vertices.get(ENDPOINT);

        HashMap<VGVertex, Float> hCost = new HashMap<>();
        HashMap<VGVertex, Float> gCost = new HashMap<>();
        HashMap<VGVertex, Float> fCost = new HashMap<>();
        HashMap<VGVertex, VGVertex> parent = new HashMap<>();
        HashSet<VGVertex> closed = new HashSet<>();

        Comparator<VGVertex> costComparator
                = (o1, o2) -> Float.compare(fCost.get(o1), fCost.get(o2));

        PriorityQueue<VGVertex> priorityQueue
                = new PriorityQueue<>(costComparator);


        // Initialize Start Point Costs
        hCost.put(startPoint, startPoint.pos.dst(endPoint.pos));
        gCost.put(startPoint, 0f);
        fCost.put(startPoint, hCost.get(startPoint) + gCost.get(startPoint));
        priorityQueue.add(startPoint);

        // Initialize Costs of all other vertices
        for (int i = 1; i < vertices.size(); i++) {
            VGVertex vertex = vertices.get(i);
            hCost.put(vertex, vertex.pos.dst(endPoint.pos));
            gCost.put(vertex, Float.POSITIVE_INFINITY);
            fCost.put(vertex, hCost.get(vertex) + gCost.get(vertex));
            priorityQueue.add(vertex);
        }

        // Visit vertices in queue
        while (!priorityQueue.isEmpty()) {
            VGVertex current = priorityQueue.poll();
            closed.add(current);
            ArrayList<VGVertex> adjacentVertices = current.getAdjacentVertices();
            ArrayList<VGEdge> incidentEdges = current.getIncidentEdges();

            for (int j = 0; j < adjacentVertices.size(); j++) {
                VGVertex adjVertex = adjacentVertices.get(j);

                if (closed.contains(adjVertex)) continue;

                VGEdge incEdge = incidentEdges.get(j);

                // Calculate new neighbor g and f cost
                float g = gCost.get(current) + incEdge.weight;
                float f = g + hCost.get(adjVertex);

                if (f < fCost.get(adjVertex)) {
                    gCost.put(adjVertex, g);
                    fCost.put(adjVertex, f);
                    parent.put(adjVertex, current);
                    priorityQueue.remove(adjVertex);
                    priorityQueue.add(adjVertex);
                }
            }
        }

        if (parent.get(endPoint) == null) {
            return new ArrayList<>();
        }

        // Trace path from end point and return list of destinations
        Stack<Vector2> stack = new Stack<>();
        VGVertex current = endPoint;
        while (current != startPoint) {
            stack.push(current.pos);
            current = parent.get(current);
        }

        ArrayList<Vector2> destPoints = new ArrayList<>();
        while (!stack.isEmpty()) {
            destPoints.add(stack.pop());
        }

        return destPoints;
    }

}