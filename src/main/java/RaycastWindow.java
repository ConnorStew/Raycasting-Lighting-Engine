import Lighting.Vision;
import Shapes.*;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class RaycastWindow implements ApplicationListener {

    /** The camera. */
    private OrthographicCamera cam;

    /** The shape renderer. */
    private ShapeRenderer sr;

    /** The font for debug text. */
    private BitmapFont font;

    /** The sprite batch. */
    private SpriteBatch sb;

    /** The current mouse position. */
    private Vector2 mp;

    /** The shapes to raycast towards. */
    private Array<Shape> shapes = new Array<Shape>();

    private Vision mouseLight = new Vision(2000, Color.WHITE);

    public void create() {
        cam = new OrthographicCamera(1000, 1000);
        cam.translate(500,500);
        cam.update();

        sr = new ShapeRenderer();
        sr.setAutoShapeType(true);
        sr.setProjectionMatrix(cam.combined);

        sb = new SpriteBatch();
        sb.setProjectionMatrix(cam.combined);

        font = new BitmapFont();
        font.setUseIntegerPositions(false);
        //font.getData().setScale(2);

        addShapes();
    }

    private void addShapes() {
        shapes.addAll(
                //wall
                new Rectangle(
                        new Vector2(0, 0),
                        1000,
                        1000
                ),
                new Polygon(
                        new Vector2(20, 620),
                        new Vector2(25, 630),
                        new Vector2(69, 654),
                        new Vector2(90, 698),
                        new Vector2(150, 698),
                        new Vector2(200, 665)
                ),
                new Triangle(
                        new Vector2(700, 500),
                        new Vector2(500, 100),
                        new Vector2(700, 100)
                ),
                new Triangle(
                        new Vector2(600, 700),
                        new Vector2(250, 500),
                        new Vector2(400, 900)
                ),
                new Circle(
                        new Vector2(800,800),
                        25,
                        150
                ),
                new Circle(
                        new Vector2(800,800),
                        25,
                        75
                ),
                new Circle(
                        new Vector2(800,800),
                        25,
                        30
                )
        );

        generateRectangles(8, 500, 505, -60, -60, 50, 50);
        generateRectangles(8, 900, 100, 0, -20, 10, 10);
    }

    /**
     * Generates a set amount of rectangles.
     * @param amount the amount of rectangles
     * @param x the starting x position
     * @param y the starting y position
     * @param xChange increase in x per rectangle
     * @param yChange increase in y per rectangle
     * @param width width of a rectangle
     * @param height height of a rectangle
     */
    private void generateRectangles(int amount, int x, int y, int xChange, int yChange, int width, int height) {
        for (int i = 0; i < amount; i++) {
            shapes.add( new Rectangle(new Vector2(x, y),width,height));
            y += yChange;
            x += xChange;
        }
    }

    public void render() {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        System.out.println(Gdx.graphics.getFramesPerSecond());

        //update mouse position
        Vector3 mp3 = cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        mp = new Vector2(mp3.x, mp3.y);

        Pixmap pixMap = mouseLight.raycast(shapes, mp);
        Texture map = new Texture(pixMap);
        pixMap.dispose();

        sb.begin();

        sb.draw(map, 0,0,  map.getWidth(), map.getHeight(), 0,0,
                map.getWidth(), map.getHeight(), false, true);

        sr.begin();

        //mouseLight.drawDebug(sr, sb, font);

        sb.end();

        map.dispose();

        for (Shape shape : shapes) {
            for (Line line : shape.getLines()) {
                sr.setColor(Color.PURPLE);
                sr.line(line.getP1(), line.getP2());
            }
        }

        sr.end();
    }

    public void resize(int i, int i1) {}
    public void pause() {}
    public void resume() {}
    public void dispose() {}
}

