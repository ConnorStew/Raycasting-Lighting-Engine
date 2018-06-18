package components.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import shapes.Point;
import shapes.Shape;

/**
 * This class represents an entity in the game.
 */
public abstract class Entity extends Sprite {

    /** The entities x position. */
    protected float x;

    /** The entities y position. */
    protected float y;

    /** The entities texture. */
    private Texture tex;

    protected Shape collisionMask;

    protected Point collisionMaskOrigin;

    public Entity(float x, float y, Texture tex) {
        this.x = x;
        this.y = y;
        this.tex = tex;
    }

    public void draw(SpriteBatch sb) {
        sb.draw(tex, x, y);
    }

    /**
     * Draws debug information about this entity.
     * @param sr the shape renderer to draw with
     * @param sb the sprite batch to draw with
     * @param font the font to draw with
     */
    public void drawDebug(ShapeRenderer sr, SpriteBatch sb, BitmapFont font) {
    }

    public void setCollisionMask(Shape collisionMask) {
        this.collisionMask = collisionMask;
        Point firstPoint = collisionMask.getVertices().get(0);
        collisionMaskOrigin = new Point(x - firstPoint.getX(), y - firstPoint.getY());
    }


    /**
     * This entities point.
     * @return the entities point in the bottom left
     */
    public Point getPoint() {
        return new Point(x,y);
    }

    /**
     * This entities center.
     * @return the center of the entity
     */
    public Point getCenter() {
        return new Point(x + tex.getWidth() / 2, y + tex.getHeight() / 2);
    }
}
