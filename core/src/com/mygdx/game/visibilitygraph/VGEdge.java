package com.mygdx.game.visibilitygraph;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector4;

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
        weight = a.pos.dst(b.pos);
    }

    /**
     * Get whether this edge is incident to the edge passed in.
     *
     * @param edge the other edge to consider
     *
     * @return whether these edges are incident
     */
    public boolean incidentTo(VGEdge edge) {
        return a.equals(edge.a)
                || a.equals(edge.b)
                || b.equals(edge.a)
                || b.equals(edge.b);
    }

    /**
     * Get whether this Edge intersects with the edge passed in.
     *
     * @return whether the edges intersect
     */
    public boolean intersects(VGEdge edge) {
        VectorFormLine line1 = getLine();
        VectorFormLine line2 = edge.getLine();

        // Get edge boundary lists
        Bounds b1 = getBoundaries();
        Bounds b2 = edge.getBoundaries();

        if (line1.isEquivalentTo(line2)) {
            // Check to see if edge segments overlap
            return (
                b1.includes(edge.a.pos)
                || b1.includes(edge.b.pos)
                || b2.includes(a.pos)
                || b2.includes(b.pos)
            );
        }

        try {
            // Get point of intersection
            Vector2 poi = line1.intersect(line2);

            // Check to see if intersection is within boundaries of each edge
            return b1.includes(poi) && b2.includes(poi);

        } catch (VectorFormLine.ParallelLineException e) {
            // Lines are non-equivalent parallel. No intersection.
            return false;
        }
    }

    /**
     * Private helper method to get the boundaries in the bounding box of
     * this edge.
     *
     * @return {@link Vector4} containing {x=X_MIN, y=X_MAX, z=Y_MIN, w=Y_MAX}
     */
    private Bounds getBoundaries() {
        return new Bounds(
                Math.min(a.pos.x, b.pos.x),
                Math.max(a.pos.x, b.pos.x),
                Math.min(a.pos.y, b.pos.y),
                Math.max(a.pos.y, b.pos.y)
        );
    }

    /**
     * Private helper method to get the line on which this edge exists.
     *
     * @return {@link VectorFormLine} the corresponding line
     */
    private VectorFormLine getLine() {
        Vector2 startPoint = a.pos;
        Vector2 direction =
                new Vector2(
                        b.pos.x - a.pos.x,
                        b.pos.y - a.pos.y
                );

        return new VectorFormLine(startPoint, direction);
    }

    /**
     * Private helper inner class used to represent the boundaries of this edge.
     */
    private static class Bounds {

        private final float xMin;
        private final float xMax;
        private final float yMin;
        private final float yMax;

        /**
         * Construct a Bounds object.
         *
         * @param xMin minimum x value
         * @param xMax maximum x value
         * @param yMin minimum y value
         * @param yMax maximum y value
         */
        public Bounds(float xMin, float xMax, float yMin, float yMax) {
            this.xMin = xMin;
            this.xMax = xMax;
            this.yMin = yMin;
            this.yMax = yMax;
        }

        /**
         * Get whether the point passed in can be found within these bounds.
         *
         * @param point the point to consider
         *
         * @return whether the point is present within these bounds.
         */
        public boolean includes(Vector2 point) {
            return (
                point.x >= xMin && point.x <= xMax    // Satisfies x bounds
                && point.y >= yMin && point.y <= yMax // Satisfies y bounds
            );
        }
    }

}
