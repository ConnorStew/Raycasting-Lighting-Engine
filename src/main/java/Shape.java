import com.badlogic.gdx.utils.Array;

/**
 * A shape is defined by a collection of lines.
 */
public class Shape {

    protected Array<Line> lines = new Array<Line>();

    public Array<Line> getLines() {
        return lines;
    }

}

