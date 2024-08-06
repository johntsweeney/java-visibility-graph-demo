package com.mygdx.game.visibilitygraph;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Class defining a visibility graph to be used for path finding in a 2D plane
 * full of circular obstacles.
 */
public class VisibilityGraph {

    private ArrayList<VGEdge> edgeList;

    /**
     * Construct a Visibility Graph
     */
    public VisibilityGraph(ArrayList<VGObstacle> obstacles) {
        edgeList = new ArrayList<>();
    }


    public ArrayList<Vector2> performAStar() {
        return null;
    }

}