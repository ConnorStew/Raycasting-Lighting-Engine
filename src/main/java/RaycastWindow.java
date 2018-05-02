import Lighting.Vision;
import Shapes.*;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

    private Texture background;

    private Sprite snowman;

    private Texture backgroundColour;

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

        background = new Texture(Gdx.files.internal("backgroundbw.png"));
        backgroundColour = new Texture(Gdx.files.internal("background.png"));
        snowman = new Sprite(new Texture(Gdx.files.internal("snowman.png")));

        addShapes();
    }

    private void addShapes() {
        /*
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
        */

        shapes.addAll(
                //wall
                new Rectangle(
                        new Vector2(0, 0),
                        1000,
                        1000
                ),
                new Triangle(
                        new Vector2(115, 230),
                        new Vector2(345, 230),
                        new Vector2(230, 420)
                ),
                new Rectangle(
                        new Vector2(488,160),
                        412,
                        272
                ),
                new Polygon(
                        new Vector2(312,620),
                        new Vector2(260, 776),
                        new Vector2(395, 868),
                        new Vector2(530, 776),
                        new Vector2(480, 620)
                )
        );
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
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //update mouse position
        Vector3 mp3 = cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        mp = new Vector2(mp3.x, mp3.y);

        sb.begin();

        sb.draw(background, 0,0);

        sb.end();

        drawMask();
        drawMaskedSprites();
        //drawShapes();

        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    }

    private void drawShapes() {
        sr.begin();

        for (Shape shape : shapes) {
            for (Line line : shape.getLines()) {
                sr.setColor(Color.PURPLE);
                sr.line(line.getP1(), line.getP2());
            }
        }

        sr.end();
    }

    /**
     * Draw sprites that can only be seen in light.
     */
    private void drawMaskedSprites() {
        sb.begin();

        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        //re-enable the depth test
        Gdx.gl.glDepthFunc(GL20.GL_EQUAL);

        //sb.draw(background, 0,0);
        sb.draw(backgroundColour, 0,0);

        sb.draw(snowman,10,10);

        sb.end();
    }

    /**
     * Draw lights to mask sprites.
     */
    private void drawMask() {
        Gdx.gl.glClearDepthf(1f);
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT); //clear depth buffer
        Gdx.gl.glDepthFunc(GL20.GL_LESS); //set the function to LESS
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST); //enable depth writing
        Gdx.gl.glDepthMask(true);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(mouseLight.getColour());

        //the triangles that make up the light
        Array<Triangle> triangles = mouseLight.raycastForTriangles(shapes,mp);

        for (Triangle triangle : triangles) {
            Vector2 p1 = triangle.getVertices().get(0);
            Vector2 p2 = triangle.getVertices().get(1);
            Vector2 p3 = triangle.getVertices().get(2);

            sr.triangle(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
        }

        sr.end();
    }

    public void resize(int i, int i1) {}
    public void pause() {}
    public void resume() {}
    public void dispose() {}
}

