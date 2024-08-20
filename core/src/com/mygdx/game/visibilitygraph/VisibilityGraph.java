package com.mygdx.game.visibilitygraph;

import com.badlogic.gdx.math.Vector2;
import com.sun.source.tree.BinaryTree;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;


/**
 * Class defining a visibility graph to be used for path finding in a 2D plane
 * full of circular obstacles.
 */
public class VisibilityGraph {

    private Vector2 startPoint;
    private Vector2 endPoint;
    private ArrayList<VGEdge> obstacleEdges;
    private ArrayList<VGEdge> visibilityEdges;
    private ArrayList<VGVertex> vertices;
    private Set<VGObstacle> obstacles;

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

        obstacleEdges = new ArrayList<>();
        vertices = new ArrayList<>();
        visibilityEdges = new ArrayList<>();
        this.obstacles = new HashSet<>();

        this.startPoint = startPoint;
        this.endPoint = endPoint;

        vertices.add(new VGVertex(startPoint, 0));
        vertices.add(new VGVertex(endPoint, 0));

        // Setup Obstacles
        for (ArrayList<Vector2> vertices : obstacles) {
            VGObstacle obstacle = new VGObstacle(vertices, agentRadius);
            this.obstacles.add(obstacle);

            obstacleEdges.addAll(obstacle.getEdges());
            this.vertices.addAll(obstacle.getVertices());
        }

        constructWithNaive();
    }

    /**
     * Private construction algorithm for visibility graph using the naive
     * solution O(n^3).
     */
    private void constructWithNaive() {

        int numVertices = vertices.size();

        for (int i = 0; i < numVertices; i++) {
            VGVertex center = vertices.get(i);
            for (int j = i; j < numVertices; j++) {
                if (i == j) continue;
                VGVertex vertex = vertices.get(j);

                // Skip pairs of belonging to same non-zero group id
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
                }

            }
        }
    }

    /**
     * Private construction algorithm for visibility graph using Lee's
     * solution O(n^2 log n). TO BE IMPLEMENTED!!!
     */
    private void constructWithLees() {
        for (int i = 0; i < vertices.size(); i++) {
            VGVertex center = vertices.get(i);

            // Order vertices by angle from X-Axis
            ConcurrentSkipListMap<Float, VGVertex> orderedVertices
                    = new ConcurrentSkipListMap<>();

            for (int j = 0; j < vertices.size(); j++) {
                if (i == j) continue;
                VGVertex vertex = vertices.get(j);
                orderedVertices.put(vertex.getAngleTo(center.pos), vertex);

                // TODO: Finish Lee's Algorithm
            }
        }
    }

    /**
     * Get start point of navigating agent.
     *
     * @return {@link Vector2} representing the agent start point
     */
    public Vector2 getStartPoint() {
        return startPoint;
    }

    /**
     * Get end point of navigating agent.
     *
     * @return {@link Vector2} representing the agent end point
     */
    public Vector2 getEndPoint() {
        return endPoint;
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

    public ArrayList<Vector2> performAStar() {
        return null;
    }

    /**
     * Private inner class to be used for ordering vertices and edges
     * efficiently with shared keys.
     */
    private class CustomOrderedList {
        
    }

}