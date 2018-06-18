package shapes;

import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

public class Circle extends Shape {

    public Circle(Point pos, int sides, int radius) {
        super(getVertices(pos, sides, radius));
    }

    private static ArrayList<Point> getVertices(Point pos, int sides, int radius) {
        ArrayList<Point> vertices = new ArrayList<Point>();

        // Create the circle in the coordinates origin
        for (int i = 0; i < 360; i += 360 / sides) {
            double heading = Math.toRadians(i); //get radians
            vertices.add(new Point((float)(pos.x +  Math.cos(heading) * radius), (float)(pos.y + Math.sin(heading) * radius)));
        }

        return vertices;
    }

}
