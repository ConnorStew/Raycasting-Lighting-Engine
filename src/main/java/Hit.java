import com.badlogic.gdx.math.Vector2;

public class Hit {

    private final Vector2 hit;

    Hit(Vector2 hit) {
        this.hit = hit;
    }
    public Vector2 getPoint() {
        return hit;
    }
}
