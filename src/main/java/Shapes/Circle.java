package Shapes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Circle extends Shape {

    public Circle(Vector2 pos, int sides, int radius) {
        super(getVertices(pos, sides, radius));
    }

    private static Array<Vector2> getVertices(Vector2 pos, int sides, int radius) {
        Array<Vector2> vertices = new Array<Vector2>();

        // Create the circle in the coordinates origin
        for (int i = 0; i < 360; i += 360 / sides) {
            double heading = Math.toRadians(i); //get radians
            vertices.add(new Vector2((float)(pos.x +  Math.cos(heading) * radius), (float)(pos.y + Math.sin(heading) * radius)));
        }

        return vertices;
    }

}
