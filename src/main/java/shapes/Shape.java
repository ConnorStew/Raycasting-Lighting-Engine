package shapes;

import java.util.ArrayList;

/**
 * A shape is defined by a collection of lines.
 */
public class Shape {

    /** The lines that make up the shape. */
    protected ArrayList<Line> lines = new ArrayList<Line>();

    /** The vertices of the shape. */
    protected ArrayList<Point> vertices = new ArrayList<Point>();

    private Rectangle mask;

    Shape(ArrayList<Point> points) {
        vertices = points;

        for (int i = 0; i < points.size() - 1; i++)
            lines.add(new Line(points.get(i), points.get(i + 1)));

        //connect the last line to the first
        lines.add(new Line(points.get(points.size() - 1), points.get(0)));

        generateMask();
    }


    private void generateMask() {
        if (!(this instanceof Rectangle)) {
            //get lowest x and lowest y highest x and highest y
            float lowX = vertices.get(0).x;
            float lowY = vertices.get(0).y;
            float highX = -1000;
            float highY = -1000;
            for (Point vertex : vertices) {
                if (vertex.x < lowX)
                    lowX = vertex.x;

                if (vertex.y < lowY)
                    lowY = vertex.y;

                if (vertex.y > highY)
                    highY = vertex.y;

                if (vertex.x > highX)
                    highX = vertex.x;
            }

            float width = highX - lowX;
            float height = highY - lowY;

            mask = new Rectangle(new Point(lowX, lowY), width, height);
        }
    }

    Shape(Point... points) {
        if (points.length >= 3) {
            for (Point vertex : points)
                vertices.add(vertex);

            for (int i = 0; i < vertices.size() - 1; i++)
                lines.add(new Line(vertices.get(i), vertices.get(i + 1)));

            //connect the last line to the first
            lines.add(new Line(vertices.get(vertices.size() - 1), vertices.get(0)));

            generateMask();
        }
    }

    public Shape(Shape toCopy) {
        for (Point vertex : toCopy.getVertices())
            vertices.add(new Point(vertex.getX(), vertex.getY()));

        for (Line line : toCopy.getLines())
            lines.add(new Line((int)line.getP1().x, (int)line.getP1().y, (int)line.getP2().x, (int)line.getP2().y));

        generateMask();
    }

    public ArrayList<Point> getVertices() {
        return vertices;
    }

    public ArrayList<Line> getLines() {
        return lines;
    }

    public void moveShape(float xIncrease, float yIncrease) {
        for (Point vertex : vertices) {
            vertex.x += xIncrease;
            vertex.y += yIncrease;
        }

        lines.clear();
        for (int i = 0; i < vertices.size() - 1; i++)
            lines.add(new Line(vertices.get(i), vertices.get(i + 1)));

        //connect the last line to the first
        lines.add(new Line(vertices.get(vertices.size() - 1), vertices.get(0)));
    }

    public void setPosition(Point newPos) {
        Point currentPos = getVertices().get(0);

        //get the x and y difference between the current position and the new one
        float xDif = newPos.x - currentPos.x;
        float yDif = newPos.y - currentPos.y;

        moveShape(xDif, yDif);
    }

    public void setPosition(float x, float y) {
        setPosition(new Point(x,y));
    }

    public Rectangle getMask() {
        return mask;
    }

    public Point centroid()  {
        double centroidX = 0, centroidY = 0;

        for(Point vertex : vertices) {
            centroidX += vertex.getX();
            centroidY += vertex.getY();
        }
        return new Point((float) centroidX / vertices.size(), (float) centroidY / vertices.size());
    }

    public float[] getVerticesAsArray() {
        float[] toReturn = new float[vertices.size()];

        for (int i = 0; i < vertices.size() * 2; i =+ 2) {
            toReturn[i] = vertices.get(i).x;
            toReturn[i + 1] = vertices.get(i).y;
        }

        return toReturn;
    }
}

