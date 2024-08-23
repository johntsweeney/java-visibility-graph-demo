package com.mygdx.game.visibilitygraph;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Defines a Visibility Graph Vertex.
 */
public class VGVertex {

    // FIXME: DEBUG ID
    private static int numInstances = 0;
    public int ID;

    public Vector2 pos;      // Vertex Position

    public final int groupID;

    private ArrayList<VGVertex> adjacentVertices;
    private ArrayList<VGEdge> incidentEdges;

    /**
     * Construct a VGVertex.
     *
     * @param pos the positional info of this vertex
     * @param groupID the group ID of this vertex
     */
    public VGVertex(Vector2 pos, int groupID) {
        adjacentVertices = new ArrayList<>();
        incidentEdges = new ArrayList<>();
        this.pos = pos;
        this.groupID = groupID;

        // FIXME: DEBUG ID
        ID = numInstances;
        numInstances++;
    }

    /**
     * Add a neighbor to this vertex.
     *
     * @param vertex the neighboring vertex
     * @param edge the corresponding edge
     */
    public void addNeighbor(VGVertex vertex, VGEdge edge) {
        adjacentVertices.add(vertex);
        incidentEdges.add(edge);
    }

    /**
     * Get list of vertices adjacent to this vertex.
     *
     * @return {@link ArrayList} of {@link VGVertex} objects
     */
    public ArrayList<VGVertex> getAdjacentVertices() {
        return adjacentVertices;
    }

    /**
     * Get list of edges incident to this vertex.
     *
     * @return {@link ArrayList} of {@link VGEdge} objects
     */
    public ArrayList<VGEdge> getIncidentEdges() {
        return incidentEdges;
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
                pos.x - point.x,
                pos.y - point.y
        );

        return vectorTo.angleDeg();
    }

}
