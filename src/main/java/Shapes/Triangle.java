package Shapes;

import com.badlogic.gdx.math.Vector2;

public class Triangle extends Shape {

    public Triangle(Vector2 p1, Vector2 p2, Vector2 p3) {
       super(p1, p2, p3);
    }

    public Triangle(int x1, int y1, int x2, int y2, int x3, int y3) {
        super(new Vector2(x1, y1), new Vector2(x2,y2), new Vector2(x3, y3));
    }
}
