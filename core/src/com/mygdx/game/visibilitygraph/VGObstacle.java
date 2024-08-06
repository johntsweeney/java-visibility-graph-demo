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
        VGVertex prevVertex = this.vertices.getLast();
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

            /* Solving for the point of intersection
             *
             * For some constants d0x, d0y, d1x, d1y
             *                    n0x, n0y, n1x, n1y
             *                    u0x, u0y, u1x, u1y:
             *
             * Let d0 = <d0x, d0y>, d1 = <d1x, d1y> be the direction vectors
             *   for edges 0 and 1 respectively.
             * Let n0 = <n0x, n0y>, n1 = <n1x, n1y> be the normal vectors
             *   touching the line corresponding to each edge.
             * Let u0 = <u0x, u0y>, u1 = <u1x, u1y> be the unit normal
             *   vectors for each edge.
             *
             * We can set up the following equation:
             *
             *   d0 + n0 + s * u0 = d1 + n1 + t * u1   for some scalars s, t
             *
             * With some work, this equation can be written as the following
             * system of equations:
             *
             *   s(u0x) - t(u1x) = d0x + n0x - d1x - n1x
             *   s(u0y) - t(u1y) = d0y + n0y - d1y - n1y
             *
             * From which we can build the following augmented matrix:
             *
             *     s      t           Constants
             *  [ u0x   -u1x  |  d0x + n0x - d1x - n1x ]
             *  [ u0y   -u1y  |  d0y + n0y - d1y - n1y ]
             *
             * From here we can solve the system by reducing the matrix to
             * reduced row echelon form, giving us the following formulas
             * for a and b:
             *
             *   s = [ u1x (d0y + n0y - d1y - n1y)
             *          + u1y (d1x + n1x - d0x - n0x) ]
             *       / (u0x * u1y - u0y * u1x)
             *
             *   t = [ u0x (d0y + n0y - d1y - n1y)
             *          + u0y (d1x + n1x - d0x - n0x) ]
             *       / (u0x * u1y - u0y * u1x)
             *
             * And, since both d0 + n0 + s * u0 and d1 + n1 + t * u1 give
             * us the same point, we only need to calculate one of the two.
             *
             * */

            int i0 = i;
            int i1 = (i + 1) % n;
            VGEdge e0 = edges.get(i0);
            VGEdge e1 = edges.get(i1);

            Vector2 d0, d1; // Direction vectors
            Vector2 n0, n1; // Normal vectors
            Vector2 u0, u1; // Unit normal vectors

            float d0x, d0y, d1x, d1y; // Direction vector components
            float n0x, n0y, n1x, n1y; // Normal vector components
            float u0x, u0y, u1x, u1y; // Unit normal components

            d0 = directions.get(i0);
            d1 = directions.get(i1);

            d0x = d0.x;
            d0y = d0.y;
            d1x = d1.x;
            d1y = d1.y;

            n0 = normals.get(i0);
            n1 = normals.get(i1);

            n0x = n0.x;
            n0y = n0.y;
            n1x = n1.x;
            n1y = n1.y;

            u0 = n0.nor();
            u1 = n1.nor();

            u0x = u0.x;
            u0y = u0.y;
            u1x = u1.x;
            u1y = u1.y;

            float s = ( u1x * (d0y + n0y - d1y - n1y)
                    + u1y * (d1x + n1x - d0x - n0x))
                    / (u0x * u1y - u0y * u1x);

            // Calculate d0 + n0 + s * u0 as new vertex
            Vector2 vertex = d0.add(n0).add(u0.scl(s));
            vertices.set(i, new VGVertex(vertex));
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