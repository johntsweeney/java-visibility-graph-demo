package com.mygdx.game.visibilitygraph;

/**
 * Defines a Visibility Graph Edge.
 */
public class VGEdge {

    public VGVertex a, b;
    public boolean isSolid;
    public float weight;

    /**
     * Construct a VGEdge.
     *
     * @param a vertex A of Edge
     * @param b vertex B of Edge
     * @param isSolid whether this edge is solid
     */
    public VGEdge(VGVertex a, VGVertex b, boolean isSolid) {
        this.a = a;
        this.b = b;
        this.isSolid = isSolid;
        weight = a.position.dst(b.position);
    }

}
