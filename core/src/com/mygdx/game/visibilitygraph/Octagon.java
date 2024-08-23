package com.mygdx.game.visibilitygraph;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Octagon {
    public float radius;
    public Vector2 position;
    private ArrayList<Vector2> vertices;

    /**
     * Construct an Octagon with the specified inner radius for its edges.
     *
     * @param position the position of the octagon
     * @param innerRadius the inner radius
     */
    public Octagon(Vector2 position, float innerRadius) {

        this.radius = innerRadius;
        this.position = position;

        set();
    }

    /**
     * Set the position of this Octagon.
     *
     * @param position the position to be set
     */
    public void setPosition(Vector2 position) {
        this.position = position;
    }

    /**
     * Set the radius of this Octagon.
     *
     * @param radius the radius to be set
     */
    public void setRadius(float radius) {
        this.radius = radius;
    }

    /**
     * Set the Octagon vertices based on the octagons position and radius.
     */
    public void set() {

        vertices = new ArrayList<>();
        ArrayList<Vector2> midpoints = new ArrayList<>();
        ArrayList<VectorFormLine> edgeLines = new ArrayList<>();


        // Find midpoints of octagon edges
        for (int i = 0; i <= 315; i += 45) {
            double theta = Math.toRadians(i);
            midpoints.add(
                    new Vector2(
                            (float) (position.x + Math.cos(theta) * radius),
                            (float) (position.y + Math.sin(theta) * radius)
                    )
            );
        }


        // Create edge lines
        for (int i = 0; i < 8; i++) {
            Vector2 a = new Vector2(midpoints.get(i));

            Vector2 temp = new Vector2(a);
            temp.sub(position);
            Vector2 v = new Vector2(temp.y, -temp.x);

            edgeLines.add(new VectorFormLine(a, v));
        }

        // Find intersections between adjacent edge lines (yielding obstacle
        // vertices)
        for (int i = 0; i < 8; i++) {
            VectorFormLine a = edgeLines.get(i);
            VectorFormLine b = edgeLines.get((i + 1) % 8);

            try {
                vertices.add(a.intersect(b));
            } catch (VectorFormLine.ParallelLineException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * Get vertices of this Octagon
     *
     * @return {@link ArrayList} of {@link Vector2} objects
     */
    public ArrayList<Vector2> getVertices() {
        return vertices;
    }
}
