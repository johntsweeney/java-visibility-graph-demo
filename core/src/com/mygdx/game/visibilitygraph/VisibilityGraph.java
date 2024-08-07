package com.mygdx.game.visibilitygraph;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Set;

/**
 * Class defining a visibility graph to be used for path finding in a 2D plane
 * full of circular obstacles.
 */
public class VisibilityGraph {

    private ArrayList<VGEdge> edgeList;

    /**
     * Construct a Visibility Graph.
     *
     * @param obstacles a set of obstacles defined as vertex arrays (vertices
     *                  should be inserted in counter-clockwise order)
     * @param agentRadius radius of navigating agent
     */
    public VisibilityGraph(
            Set<ArrayList<Vector2>> obstacles,
            float agentRadius)
    {
        edgeList = new ArrayList<>();

    }


    public ArrayList<Vector2> performAStar() {
        return null;
    }

}