package shapes;

import java.util.ArrayList;

public class Polygon extends Shape {

    public Polygon(Point... points) {
        super(points);
    }

    public Polygon(ArrayList<Point> points) {
        super(points);

    }

    public float[] getVerticesAsArray() {
        float[] toReturn = new float[vertices.size() * 2];
        int counter = 0;
        for (Point vertex : vertices) {
            toReturn[counter] = vertex.x;
            toReturn[counter + 1] = vertex.y;
            counter = counter + 2;
        }

        return toReturn;
    }
}
