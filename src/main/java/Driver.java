import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import components.PathingWindow;
import components.RaycastWindow;

public class Driver {

    public static void main(String[] args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Physics";
        config.width = 900;
        config.height = 700;
        new LwjglApplication(new PathingWindow(), config);
    }
}
