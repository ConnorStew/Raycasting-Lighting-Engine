import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Line {

    private static final double DEFAULT_RAY_LENGTH = 2000;

    private Array<Vector2> vertices = new Array<Vector2>();

    private Vector2 p1, p2;

    public Line (float x1, float y1, float x2, float y2) {
        p1 = new Vector2(x1, y1);
        p2 = new Vector2(x2, y2);
        vertices.addAll(p1, p2);
    }

    public Line(Vector2 p1, Vector2 p2){
        this.p1 = p1;
        this.p2 = p2;
    }

    public Line(Vector2 p1, double angle){
        this.p1 = p1;
        p2 = new Vector2((float) (p1.x + Math.cos(Math.toRadians(angle)) * DEFAULT_RAY_LENGTH), (float) (p1.y + Math.sin(Math.toRadians(angle)) * DEFAULT_RAY_LENGTH));
    }

    public Line(Vector2 mp, double angleBetween, double offset) {
        this.p1 = mp;
        p2 = new Vector2((float) (p1.x + Math.cos(Math.toRadians(angleBetween) + offset) * DEFAULT_RAY_LENGTH), (float) (p1.y + Math.sin(Math.toRadians(angleBetween) + offset) * DEFAULT_RAY_LENGTH));
    }

    public Vector2 getP1() {
        return p1;
    }

    public void setP1(Vector2 p1) {
        this.p1 = p1;
    }

    public Vector2 getP2() {
        return p2;
    }

    public void setP2(Vector2 p2) {
        this.p2 = p2;
    }

    public Array<Vector2> getVertices() {
        return vertices;
    }

    public double getAngle() {
        double xDistance = p1.x - p2.x;
        double yDistance = p1.y - p2.y;
        double tanc = yDistance / xDistance;
        double angle = Math.toDegrees(Math.atan(tanc));
        double rotation; //the angle to cast towards

        //tan only goes to 180 so reverse the angle when its on the left hand side
        if (xDistance > 0)
            rotation = angle - 180;
        else
            rotation = angle;

        return rotation;
    }
}
