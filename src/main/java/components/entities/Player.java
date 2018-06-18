package components.entities;

import com.badlogic.gdx.graphics.Texture;
import shapes.Point;
import shapes.Polygon;

public class Player extends Entity {

    public Player(float x, float y, Texture tex) {
        super(x, y, tex);

        setCollisionMask(new Polygon(
                new Point(x + 25, y),
                new Point(x + 20, y),
                new Point(x + 15, y + 5),
                new Point(x + 15, y + 10),
                new Point(x + 19, y + 20),
                new Point(x + 18, y + 25),
                new Point(x + 20, y + 30),
                new Point(x + 21, y + 35),
                new Point(x + 22, y + 37),
                new Point(x + 22, y + 45),
                new Point(x + 25, y + 48),
                new Point(x + 30, y + 48),
                new Point(x + 35, y + 48),
                new Point(x + 39, y + 45),
                new Point(x + 40, y + 35),
                new Point(x + 42, y + 33),
                new Point(x + 42, y + 28),
                new Point(x + 45, y + 25),
                new Point(x + 45, y + 20),
                new Point(x + 48, y + 12),
                new Point(x + 48, y + 6),
                new Point(x + 40, y)
        ));
    }

    /**
     * Moves towards an entity at this entities speed.
     * @param target the entity to move towards
     * @param delta the time since the last frame was rendered
     */
    public void moveTowards(Point target, float delta) {
        rotateTowards(target.x, target.y);
        moveForward(100 * delta);
    }

    /**
     * Rotates this entity towards a set of coordinates.
     * @param targetX the x coordinate to face towards
     * @param targetY the y coordinate to face towards
     */
    public void rotateTowards(float targetX, float targetY) {
        double xDistance = getCenter().x - targetX;
        double yDistance = getCenter().y - targetY;
        double tanc = yDistance / xDistance;
        double angle = Math.toDegrees(Math.atan(tanc));

        //tan only goes to 180 so reverse the angle when its on the left hand side
        if (xDistance > 0)
            setRotation((float) angle - 180);
        else
            setRotation((float) angle);

        //set the entity back to their position for rotation
        setOriginCenter();
    }

    /**
     * Moves the entity forward
     * @param pixels the amount of pixels to move the entity by
     */
    protected void moveForward(double pixels) {
        x = x + (float) (Math.cos(Math.toRadians(getRotation())) * pixels);
        y = y + (float) (Math.sin(Math.toRadians(getRotation())) * pixels);
    }

}
