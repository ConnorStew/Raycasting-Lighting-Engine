package Shapes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * A shape is defined by a collection of lines.
 */
public class Shape {

    /** The lines that make up the shape. */
    private Array<Line> lines = new Array<Line>();

    /** The vertices of the shape. */
    private Array<Vector2> vertices;

    Shape(Array<Vector2> points) {
        vertices = points;

        for (int i = 0; i < points.size - 1; i++)
            lines.add(new Line(points.get(i), points.get(i + 1)));

        //connect the last line to the first
        lines.add(new Line(points.get(points.size - 1), points.get(0)));
    }

    Shape(Vector2... points) {
        vertices = new Array<Vector2>(points);

        for (int i = 0; i < vertices.size - 1; i++)
            lines.add(new Line(vertices.get(i), vertices.get(i + 1)));

        //connect the last line to the first
        lines.add(new Line(vertices.get(vertices.size - 1), vertices.get(0)));
    }

    public Array<Vector2> getVertices() {
        return vertices;
    }

    public Array<Line> getLines() {
        return lines;
    }

}

