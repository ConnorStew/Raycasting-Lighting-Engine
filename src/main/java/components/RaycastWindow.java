package components;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import components.entities.Entity;
import components.lighting.Light;
import shapes.*;

import java.util.ArrayList;

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
    private Point mp;

    private Texture backgroundColour;

    private Entity snowman;

    private Light light;

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
        font.getData().setScale(0.8f);

        backgroundColour = new Texture(Gdx.files.internal("background.jpg"));

        light = new Light(5000, Color.WHITE);

        addShapes();
    }

    private void addShapes() {
        World.addShape(
                //wall
                new Rectangle(
                        new Point(0, 0),
                        1000,
                        1000
                ));

        World.addShape(
                new Polygon(
                        new Point(20, 620),
                        new Point(25, 630),
                        new Point(69, 654),
                        new Point(90, 698),
                        new Point(150, 698),
                        new Point(200, 665)
                ));

        World.addShape(
                new Triangle(
                        new Point(700, 500),
                        new Point(500, 100),
                        new Point(700, 100)
                ));

        World.addShape(
                new Triangle(
                        new Point(600, 700),
                        new Point(250, 500),
                        new Point(400, 900)
                ));

        World.addShape(
                new Circle(
                        new Point(800,800),
                        25,
                        150
                ));

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
            World.addShape(new Rectangle(new Point(x, y),width,height));
            y += yChange;
            x += xChange;
        }
    }

    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //update mouse position
        Vector3 mp3 = cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        mp = new Point(mp3.x, mp3.y);

        light.setOrigin(mp);
        drawMask(light.raycastForTriangles(World.getShapes()));
        drawMaskedSprites();

        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            World.raycastForPoints(World.getVertices().get(30));
            World.drawDebug(sr);
        }

        //light.drawDebug(sr);
        World.drawShapes(sr);

    }

    /**
     * Draw sprites that can only be seen in light.
     */
    private void drawMaskedSprites() {
        sb.begin();
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        //re-enable the depth test
        Gdx.gl.glDepthFunc(GL20.GL_EQUAL);

        sb.draw(backgroundColour, 0,0, 1000, 1000);
        sb.end();
    }

    /**
     * Draw lights to mask sprites.
     * @param triangles
     */
    private void drawMask(ArrayList<Triangle> triangles) {
        sr.begin();
        Gdx.gl.glClearDepthf(1f);
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT); //clear depth buffer
        Gdx.gl.glDepthFunc(GL20.GL_LESS); //set the function to LESS
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST); //enable depth writing
        Gdx.gl.glDepthMask(true);

        sr.setColor(Color.RED);
        sr.set(ShapeRenderer.ShapeType.Filled);
        for (Triangle triangle : triangles) {
            Point p1 = triangle.getVertices().get(0);
            Point p2 = triangle.getVertices().get(1);
            Point p3 = triangle.getVertices().get(2);

            sr.triangle(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(), p3.getY());
        }
        sr.end();
    }

    public void resize(int i, int i1) {}
    public void pause() {}
    public void resume() {}
    public void dispose() {}
}

