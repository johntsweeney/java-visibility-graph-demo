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

}
