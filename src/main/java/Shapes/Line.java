package Shapes;

import com.badlogic.gdx.math.Vector2;

/**
 * This class is used to handling storing and calculating information about lines.
 */
public class Line {

    /** The points that make up this line. */
    private Vector2 p1, p2;

    /**
     * Creates a line using two points.
     * @param p1 the first point
     * @param p2 the second point
     */
    public Line(Vector2 p1, Vector2 p2){
        this.p1 = p1;
        this.p2 = p2;
    }

    /**
     * Creates a line using coordinates.
     * @param x1 the first points x coordinate
     * @param y1 the first points y coordinate
     * @param x2 the second points x coordinate
     * @param y2 the second points y coordinate
     */
    public Line(int x1, int y1, int x2, int y2) {
        p1 = new Vector2(x1, y1);
        p2 = new Vector2(x2, y2);
    }

    /**
     * Creates a line with the first point p1 and a second point at the angle and distance specified.
     * @param p1 the first point
     * @param angle the angle between the points in degrees
     * @param length the distance between the first and second point
     */
    public Line(Vector2 p1, double angle, double length){
        rotateLine(p1, angle, 0, length);
    }

    /**
     * Creates a line with the first point p1 and a second point at the angle and distance specified.
     * @param p1 the first point
     * @param angle the angle between the points in degrees
     * @param offset the offset of the angle in radians
     * @param length the distance between the first and second point
     */
    public Line(Vector2 p1, double angle, double offset, double length) {
        rotateLine(p1, angle, offset, length);
    }

    /**
     * Asumming lines are horizontal not vertical so no need for - y sin stuff
     * Rotates this line by a angle in degrees
     * @param origin the origin of this line
     * @param angle the angles in degrees to rotate this line by
     * @param offset the offset of the line in radians
     * @param length the distance between the origin and the end of the line
     */
    private void rotateLine(Vector2 origin, double angle, double offset, double length) {
        double theta = Math.toRadians(angle+ offset);
        float lineX = (float)(origin.x + Math.cos(theta) * length);
        float lineY = (float)(origin.y + Math.sin(theta) * length);
        this.p1 = origin;
        p2 = new Vector2(lineX, lineY);
    }

    /**
     * Checks if this line and another line intersect. <br>
     * Using the method detailed here: https://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect
     * @param toCheck the line to check against this
     * @return null if the lines do not intersect, a vector2 instance of the intersection point if they do
     */
    public Vector2 intersects(Line toCheck) {
        Vector2 intersection = null;

        double r_x = p2.x - p1.x; //the x increase from this lines first point to its second
        double r_y = p2.y - p1.y; //the y increase from this lines first point to its second

        double s_x = toCheck.p2.x - toCheck.p1.x; //the x increase from the toCheck line's first point to its second
        double s_y = toCheck.p2.y - toCheck.p1.y; //the y increase from the toCheck line's first point to its second

        double denominator = r_x*s_y - r_y*s_x;

        double u = ((toCheck.p1.x - p1.x)*r_y - (toCheck.p1.y - p1.y)*r_x) / denominator;
        double t = ((toCheck.p1.x -  p1.x)*s_y - (toCheck.p1.y - p1.y)*s_x) / denominator;

        if(t >= 0 && t <= 1 && u >= 0 && u <= 1){
            intersection = new Vector2((float)(p1.x + t*r_x), (float) (toCheck.p1.y + u*s_y));
        }

        return intersection;
    }


    /**
     * Get the angle between two points in degrees.
     * @param p1 the first point
     * @param p2 the second point
     * @return the angle between the points in degrees
     */
    public static double getAngleBetween(Vector2 p1, Vector2 p2) {
        double xDistance = p1.x - p2.x;
        double yDistance = p1.y - p2.y;
        double tanc = yDistance / xDistance;
        double angle = Math.toDegrees(Math.atan(tanc));
        double rotation; //the angle to cast towards

        //tan only goes to 180 so reverse the angle when its on the left hand side
        if (xDistance > 0)
            rotation = angle - 180;
        else
            rotation = angle;

        //if the angle is equal to 90 it must be inverted, this is to prevent a bug where the lines were drawn the wrong way
        if (Math.abs(angle) == 90)
            return -angle;

        return rotation;
    }

    /**
     * @return the first point
     */
    public Vector2 getP1() {
        return p1;
    }

    /**
     * @return the second point
     */
    public Vector2 getP2() {
        return p2;
    }

    public double getAngle() {
        return getAngleBetween(p1, p2);
    }
}
