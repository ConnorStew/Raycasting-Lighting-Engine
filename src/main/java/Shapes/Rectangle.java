package Shapes;

import com.badlogic.gdx.math.Vector2;

public class Rectangle extends Shape {

    public Rectangle(Vector2 p1, float width, float height) {
        super(p1,
                p1.cpy().add(0, height),
                p1.cpy().add(width, height),
                p1.cpy().add(width, 0));
    }

    /**
     * Checks if this rectangles contains the given vector.
     * @param toCheck the vector to check.
     * @return whether the given vector is within this rectangle
     */
    public boolean contains(Vector2 toCheck) {
        //TODO: This method.
        return false;
    }
}
