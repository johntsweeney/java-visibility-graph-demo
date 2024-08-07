package com.mygdx.game.visibilitygraph;

import com.badlogic.gdx.math.Vector2;

/**
 * Class defining a line represented in vector form, where a point on the line
 * can be calculated by the formula <i>r(t) = b + tv</i> for a starting vector
 * <i>b</i> and directional vector <i>v</i>.
 */
public class VectorFormLine {

    public final Vector2 b; // Starting point
    public final Vector2 v; // Direction Vector

    /**
     * Parallel Line Exception to be thrown when the presence of parallel lines
     * would result in an error.
     */
    public static class ParallelLineException extends Exception {

        /**
         * Construct a Parallel Line Exception with a message.
         *
         * @param message the message
         */
        public ParallelLineException(String message) {
            super(message);
        }
    }

    /**
     * Construct a Vector Form Line.
     *
     * @param b starting point
     * @param v direction vector
     */
    public VectorFormLine(Vector2 b, Vector2 v) {
        this.b = b;
        this.v = v;
    }

    /**
     * Function r(t) = b + tv.
     *
     * @param t scalar by which to multiply v
     * @return {@link Vector2} the point on the line given by b + tv
     */
    public Vector2 r(float t) {
        return b.add(v.scl(t));
    }

    /**
     * Solve for the point at which this line intersects the line passed in.
     *
     * @param line line to intersect with
     * @return {@link Vector2} the point of intersection
     */
    public Vector2 intersect(VectorFormLine line) throws ParallelLineException {
        /*
         * Solving for the point of intersection.
         *
         * For some constants v0x, v0y, v1x, v1y
         *                    b0x, b0y, b1x, b1y
         *
         * Let v0, v1 be direction vectors
         *   corresponding to each line.
         * Let b0, b1 be the starting points for each
         *   line.
         *
         * To find the intersection point of these lines, we assert that for
         * some scalars s and t the following is true:
         *
         *   b0 + s * v0 = b1 + t * v1
         *
         * With some work, this equation can be written as the following system
         * of equations:
         *
         *   s * v0.x - t * v1.x = b1.x - b0.x
         *   s * v0.y - t * v1.y = b1.y - b0.y
         *
         * From which we can build the following augmented matrix:
         *
         *      s      t      Constants
         *   [ v0.x   -v1.x  |  b1.x - b0.x ]
         *   [ v0.y   -v1.y  |  b1.y - b0.y ]
         *
         * From here we can solve the system by reducing the matrix to reduced
         * row echelon form, giving us the following formulas for s and t:
         *
         *   s = [v1.y (b1.x - b0.x) + v1.x (b0.y - b1.y)]
         *          / (v0.x * v1.y - v0.y * v1.x)
         *
         *   t = [v0.y (b1.x - b0.x) + v0.x (b0.y - b1.y)]
         *          / (v0.x * v1.y - v0.y * v1.x)
         *
         * Since both b0 + s * v0 and b1 + t * v1 give us the same point, we
         * only need to calculate one of the two.
         */

        // If the product of the direction vectors magnitudes is the same as
        // their dot product then the corresponding lines are parallel
        if (v.len() * line.v.len() == v.dot(line.v)) {
            throw new ParallelLineException(
                    "There are either none or infinitely many points of"
                            + " intersection for a pair of parallel lines."
            );
        }

        Vector2 b0, b1;
        Vector2 v0, v1;

        b0 = b;
        b1 = line.b;

        v0 = v;
        v1 = line.v;

        float s = (v1.y * (b1.x - b0.x) + v1.x * (b0.y - b1.y))
                / (v0.x * v1.y - v0.y * v1.x);

        return r(s);
    }

}
