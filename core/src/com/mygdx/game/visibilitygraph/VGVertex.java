package com.mygdx.game.visibilitygraph;

import com.badlogic.gdx.math.Vector2;

/**
 * Defines a Visibility Graph Vertex.
 */
public class VGVertex {

    public Vector2 position;

    /**
     * Construct a VGVertex.
     *
     * @param pos the positional info of this vertex
     */
    public VGVertex(Vector2 pos) {
        position = pos;
    }

    /**
     * Get the angle of this vertex to the specified point from the X-axis.
     * This is used for sorting vertices in the construction of a Visibility
     * Graph.
     *
     * @param point the point from which to get the angle
     *
     * @return the angle (in degrees)
     */
    public float getAngleTo(Vector2 point) {
        Vector2 vectorTo =  new Vector2(
                position.x - point.x,
                position.y - point.y
        );

        return vectorTo.angleDeg();
    }

}
