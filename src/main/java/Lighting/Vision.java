package Lighting;

import Shapes.Line;
import Shapes.Shape;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * A game entities vision.
 */
public class Vision extends Light {


    public Vision(double length, Color colour) {
        super(length, colour);
    }

}
