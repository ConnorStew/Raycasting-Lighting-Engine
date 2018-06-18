package shapes;

import com.badlogic.gdx.math.Vector2;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;

public class Point {

    public float x, y;

    /** Vertices connected to this vertex. */
    private ArrayList<Point> neighbours = new ArrayList<>();

    /** This vertex's distance from the starting vertex. */
    private float g;

    /** This vertex's heuristic distance from the starting vertex. */
    private float h;

    /** The vertex's distance score based on {@link #g} and {@link #h}. */
    private float f;

    /** The node that connected this vertex to the path. */
    private Point parent;
    private Shape shape;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point p, float xIncrease, float yIncrease) {
        this.x = p.x + xIncrease;
        this.y = p.y + yIncrease;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Vector2 toVector2() {
        return new Vector2(x,y);
    }

    /**
     * Gets the distance between two points.
     * @param otherPoint the point to check the distanceTo against
     * @return the distanceTo between the points
     */
    public float distanceTo(Point otherPoint) {
        final float x_d = x - otherPoint.x;
        final float y_d = y - otherPoint.y;
        return (float)Math.sqrt(x_d * x_d + y_d * y_d);
    }

    @Override
    public String toString() {
        return "[x: " + x + ", y:" + y + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Point) {
            float x1 = Math.round(x);
            float y1 = Math.round(y);

            float x2 = Math.round(((Point) obj).x);
            float y2 = Math.round(((Point) obj).y);

            if (x1 == x2 && y1 == y2)
                return true;
        }

        return false;
    }

    public void addNeighbour(Point toAdd) {
        neighbours.add(toAdd);
    }

    public List<Point> getNeighbours() {
        return neighbours;
    }

    public float getScore() {
        return f;
    }

    public float getG() {
        return g;
    }

    public void setG(float g) {
        this.g = g;
    }

    public void setH(float h) {
        this.h = h;
    }

    public float getH() {
        return h;
    }

    public void setScore(float score) {
        this.f = score;
    }

    public Point getParent() {
        return parent;
    }

    public void setParent(Point parent) {
        this.parent = parent;
    }

    public void addNeighbours(ArrayList<Point> points) {
        neighbours.addAll(points);
    }

    public Shape getShape() {
        return shape;
    }

    public boolean distanceEquals(Point point, int distance) {
        float minX = x - distance;
        float maxX = x + distance;
        float minY = y - distance;
        float maxY = x + distance;

        return (point.x > minX && point.x < maxX && point.y > minY &&  point.y < maxY);
    }
}
