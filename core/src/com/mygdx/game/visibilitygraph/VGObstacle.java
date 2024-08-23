package com.mygdx.game.visibilitygraph;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Defines a Visibility Graph Obstacle. A visibility graph is constructed
 * from a set of these along with a start and end point.
 */
public class VGObstacle {


    private static int numInstances = 0;

    private int id;
    private ArrayList<VGVertex> vertices;
    private ArrayList<VGEdge> edges;

    /**
     * Construct a Visibility Graph Obstacle
     *
     * @param vertices list of vectors defining the shape and position
     *                 of the obstacle
     * @param growthValue amount by which to expand obstacle shape
     *                    (this is used to account for agents which have
     *                    area)
     */
    public VGObstacle(ArrayList<Vector2> vertices, float growthValue) {
        numInstances++;
        id = numInstances;
        this.vertices = new ArrayList<>();
        for (Vector2 curVec : vertices) {
            VGVertex vertex = new VGVertex(curVec, id);
            this.vertices.add(vertex);
        }

        this.edges = new ArrayList<>();
        VGVertex prevVertex = this.vertices.get(vertices.size() - 1);
        for (VGVertex curVertex : this.vertices) {

            // Create new edge
            VGEdge newEdge = new VGEdge(prevVertex, curVertex, true);

            // Add edge to incident vertices
            prevVertex.addNeighbor(curVertex, newEdge);
            curVertex.addNeighbor(prevVertex, newEdge);

            // Add edge to list of edges
            edges.add(newEdge);

            prevVertex = curVertex;
        }

        grow(growthValue);
    }

    /**
     * Get ID of this Obstacle.
     *
     * @return the ID of this obstacle
     */
    public int getId() {
        return id;
    }

    /**
     * Get the edges of this obstacle.
     *
     * @return {@link ArrayList} of {@link VGEdge} objects
     */
    public ArrayList<VGEdge> getEdges() {
        return edges;
    }

    /**
     * Get the vertices of this obstacle.
     *
     * @return {@link ArrayList} of {@link VGVertex} objects
     */
    public ArrayList<VGVertex> getVertices() {
        return vertices;
    }

    /**
     * Grow obstacle by specified value. This is used when the visibility
     * graph needs to account for an agent with non-negligible area.
     *
     * @param value amount by which the object should grow
     */
    private void grow(float value) {
        if (value <= 0) return;

        int n = edges.size();

        ArrayList<Vector2> normals = new ArrayList<>();
        ArrayList<Vector2> unitNormals = new ArrayList<>();
        ArrayList<Vector2> directions = new ArrayList<>();
        for (VGEdge e : edges) {
            // Calculate a normal vector for edge e
            Vector2 normal = new Vector2();
            Vector2 a = new Vector2(e.a.pos);
            Vector2 b = new Vector2(e.b.pos);
            normal.x = b.y - a.y;
            normal.y = -(b.x - a.x);

            Vector2 uNorm = new Vector2(normal);
            unitNormals.add(uNorm.nor());

            // Calculate orthogonal projection of point vector u onto normal
            // vector (the resulting projection will be a normal vector
            // pointing to the line parallel to the edge)
            Vector2 u = new Vector2(a); // starting point of edge
            normal = normal.scl(
                    (u.dot(normal)) / (normal.dot(normal))
            );

            normals.add(normal);

            // Calculate direction vector of edge
            Vector2 direction = b.sub(a);
//            Vector2 direction = e.a.position.sub(e.b.position);

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
            Vector2 u0, u1; // Unit normals
            Vector2 d0, d1; // Direction vectors

            n0 = new Vector2(normals.get(i0));
            n1 = new Vector2(normals.get(i1));
            u0 = new Vector2(unitNormals.get(i0));
            u1 = new Vector2(unitNormals.get(i1));
            sp0 = n0.add(u0.scl(value));
            sp1 = n1.add(u1.scl(value));

            d0 = new Vector2(directions.get(i0));
            d1 = new Vector2(directions.get(i1));

            VectorFormLine l0 = new VectorFormLine(sp0, d0);
            VectorFormLine l1 = new VectorFormLine(sp1, d1);

            try {
                Vector2 vertex = l0.intersect(l1);
                vertices.set(i, new VGVertex(vertex, id));
            } catch (VectorFormLine.ParallelLineException e) {
                System.err.println("Parallel Line Exception occurred during " +
                        "Obstacle Growth");
            }
        }

        // Setup new edges
        for (int i = 0; i < n; i++) {

            VGVertex curVertex = vertices.get(i);
            VGVertex nextVertex = vertices.get((i + 1) % n);

            VGEdge edge = new VGEdge(
                    curVertex,
                    nextVertex,
                    true
            );

            curVertex.addNeighbor(nextVertex, edge);
            nextVertex.addNeighbor(curVertex, edge);

            edges.set(i, edge);
        }

    }
}