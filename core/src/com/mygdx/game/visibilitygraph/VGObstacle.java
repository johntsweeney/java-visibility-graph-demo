package com.mygdx.game.visibilitygraph;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Defines a Visibility Graph Obstacle. A visibility graph is constructed
 * from a set of these along with a start and end point.
 */
public class VGObstacle {
    ArrayList<VGVertex> vertices;
    ArrayList<VGEdge> edges;

    /**
     * Construct a Visibility Graph Obstacle
     *
     * @param vertices list of vectors defining the shape and position
     *                 of the obstacle
     * @param growthValue amount by which to expand obstacle shape
     *                    (this is used to account for agents which have
     *                    volume)
     */
    public VGObstacle(ArrayList<Vector2> vertices, float growthValue) {
        this.vertices = new ArrayList<>();
        for (Vector2 curVec : vertices) {
            this.vertices.add(new VGVertex(curVec));
        }

        this.edges = new ArrayList<>();
        VGVertex prevVertex = this.vertices.get(vertices.size() - 1);
        for (VGVertex curVertex : this.vertices) {
            edges.add(new VGEdge(prevVertex, curVertex, true));
        }

        grow(growthValue);
    }

    /**
     * Grow obstacle by specified value. This is used when the visibility
     * graph needs to account for an agent with non-negligible volume.
     *
     * @param value amount by which the object should grow
     */
    private void grow(float value) {
        if (value <= 0) return;

        int n = edges.size();

        ArrayList<Vector2> normals = new ArrayList<>();
        ArrayList<Vector2> directions = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            VGEdge e = edges.get(i);

            // Calculate a normal vector for edge e
            Vector2 normal = new Vector2();
            normal.x = e.b.position.y - e.a.position.y;
            normal.y = -(e.b.position.x - e.a.position.x);

            // Calculate orthogonal projection of point vector u onto normal
            // vector (the resulting projection will be a normal vector
            // pointing to the line parallel to the edge)
            Vector2 u = e.a.position; // starting point of edge
            normal = normal.scl(
                    (u.dot(normal)) / (normal.dot(normal))
            );

            normals.add(normal);

            // Calculate direction vector of edge
            Vector2 direction = e.b.position.sub(e.a.position);
            directions.add(direction);
        }

        // Calculate new vertices
        for (int i = 0; i < n; i++) {

            /*
             * Each line corresponding to an edge can be represented in
             *   vector form by adding a multiple of the edges direction
             *   vector to its normal vector.
             * We can therefore extend each edge out by the growth value
             *   by finding the intersection of each pair of lines created
             *   by adding a multiple of the direction vector to the normal
             *   plus the growth value times the normalized normal.
             * */

            int i0 = i;
            int i1 = (i + 1) % n;

            Vector2 sp0, sp1; // Starting points
            Vector2 n0, n1; // Normal vectors
            Vector2 d0, d1; // Direction vectors

            n0 = normals.get(i0);
            n1 = normals.get(i1);
            sp0 = n0.add(n0.nor().scl(value));
            sp1 = n1.add(n1.nor().scl(value));

            d0 = directions.get(i0);
            d1 = directions.get(i1);

            VectorFormLine l0 = new VectorFormLine(sp0, d0);
            VectorFormLine l1 = new VectorFormLine(sp1, d1);

            try {
                Vector2 vertex = l0.intersect(l1);;
                vertices.set(i, new VGVertex(vertex));
            } catch (VectorFormLine.ParallelLineException e) {
                System.err.println("Parallel Line Exception occurred during " +
                        "Obstacle Growth");
            }
        }

        // Setup new edges
        for (int i = 0; i < n; i++) {
            VGEdge edge = new VGEdge(
                    vertices.get((i + 1) % n),
                    vertices.get(i),
                    true
            );
            edges.set(i, edge);
        }

    }
}