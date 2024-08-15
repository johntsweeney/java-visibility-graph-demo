package com.mygdx.game.visibilitygraph;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 * Class defining a visibility graph to be used for path finding in a 2D plane
 * full of circular obstacles.
 */
public class VisibilityGraph {

    private Vector2 startPoint;
    private Vector2 endPoint;
    private ArrayList<VGEdge> edgeList;
    private ArrayList<VGVertex> vertexList;
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
        this.startPoint = startPoint;
        this.endPoint = endPoint;

        edgeList = new ArrayList<>();
        vertexList = new ArrayList<>();
        this.obstacles = new HashSet<>();

        // Setup Obstacles
        for (ArrayList<Vector2> vertices : obstacles) {
            VGObstacle obstacle = new VGObstacle(vertices, agentRadius);
            this.obstacles.add(obstacle);

            edgeList.addAll(obstacle.getEdges());
            vertexList.addAll(obstacle.getVertices());
        }

        // TODO: Construct visibility graph\
        //

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
     * Get list of edges in Visibility Graph.
     *
     * @return {@link ArrayList} of {@link VGEdge} objects
     */
    public ArrayList<VGEdge> getEdgeList() {
        return edgeList;
    }

    public ArrayList<Vector2> performAStar() {
        return null;
    }

}