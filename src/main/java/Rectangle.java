import com.badlogic.gdx.math.Vector2;

public class Rectangle extends Shape {

    Rectangle(Vector2 p1, Vector2 p2, Vector2 p3, Vector2 p4) {
        lines.addAll(
                new Line(p1.x,p1.y, p2.x, p2.y),
                new Line(p2.x,p2.y, p3.x, p3.y),
                new Line(p3.x, p3.y, p4.x, p4.y),
                new Line(p4.x, p4.y, p1.x,p1.y)
        );
    }
}
