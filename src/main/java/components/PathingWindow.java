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
import components.entities.Player;
import components.pathfinding.Graph;
import org.omg.PortableServer.POA;
import shapes.*;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;

//https://www.david-gouveia.com/pathfinding-on-a-2d-polygonal-map
public class PathingWindow implements ApplicationListener {

    /** The camera. */
    private OrthographicCamera cam;

    /** The shape renderer. */
    private ShapeRenderer sr;

    /** The font for debug text. */
    private BitmapFont font;

    /** The current mouse position. */
    private Point mp;

    private LinkedList<Point> currentPath = new LinkedList<>();

    private Player snowman;
    private SpriteBatch sb;

    private Graph graph;
    private Point startPoint, endPoint;
    private int pathCount;
    private Point target;
    private Point pathMP;


    @Override
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

        addShapes();

        snowman = new Player(10,10,new Texture("snowman.png"));

        graph = new Graph(World.getShapes());
    }

    private void addShapes() {

        World.addShape(new Rectangle(new Point(250,250), 200,100));


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
                        new Point(20, 500),
                        new Point(40, 100),
                        new Point(200, 100)
                ));


        World.addShape(
                new Circle(
                        new Point(800,800),
                        25,
                        150
                ));

    }

    public void render() {
        Gdx.gl.glClearColor( 0, 0, 0, 1 );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );

        //update mouse position
        Vector3 mp3 = cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        mp = new Point(mp3.x, mp3.y);


        sr.begin();

        for (Shape shape : World.getShapes()) {
            sr.setColor(Color.PURPLE);
            for (Line line : shape.getLines())
                sr.line(line.getP1().toVector2(), line.getP2().toVector2());

            sr.setColor(Color.WHITE);
            for (Point vertex : shape.getVertices())
                sr.circle(vertex.x, vertex.y, 5);
        }

        sr.end();



        sb.begin();
        sr.begin();

        snowman.draw(sb);
        snowman.drawDebug(sr, sb, font);

        sr.end();
        sb.end();

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            currentPath = new LinkedList<>(graph.aStar(snowman.getCenter(), mp));
            if (currentPath.size() > 2)
                currentPath.removeLast();
        }

        if (!currentPath.isEmpty()) {
            Point pathingTo = currentPath.peekLast();
            if (snowman.getCenter().distanceTo(pathingTo) <= 10) {
                snowman.setCenter(pathingTo.x, pathingTo.y);
                currentPath.remove(pathingTo);
            }

            snowman.moveTowards(pathingTo, 0.03f);
        }

        sr.begin();
        sr.set(ShapeRenderer.ShapeType.Filled);

        //neighbour
        /*
        sr.setColor(Color.BLUE);
        for (Point vertex : graph.getVertices()) {
            for (Point neighbour : vertex.getNeighbours()) {
                if (neighbour.getShape() instanceof Circle && vertex.getShape() instanceof Circle) {
                    sr.setColor(Color.GREEN);
                } else {
                    sr.setColor(Color.BLUE);
                }
                sr.rectLine(vertex.x, vertex.y, neighbour.x, neighbour.y, 2);
            }
        }
        */

        try {
            sr.setColor(Color.CYAN);
            if (startPoint != null)
                sr.circle(startPoint.x, startPoint.y, 5);

            sr.setColor(Color.YELLOW);
            if (endPoint != null)
                sr.circle(endPoint.x, endPoint.y, 5);

            sr.setColor(Color.GREEN);
            for (Point vertex : graph.getOpenList())
                sr.circle(vertex.x, vertex.y, 5);

            sr.setColor(Color.RED);
            for (Point vertex : graph.getClosedList())
                sr.circle(vertex.x, vertex.y, 5);

            sr.setColor(Color.PURPLE);
            for (Point vertex : graph.getPath())
                sr.circle(vertex.x, vertex.y, 5);

            for (Point vertex : graph.getPath())
                if (vertex.getParent() != null)
                    sr.rectLine(vertex.x, vertex.y, vertex.getParent().x, vertex.getParent().y, 2);

            sr.setColor(Color.RED);
            for (Point vertex : currentPath)
                if (vertex.getParent() != null)
                    sr.rectLine(vertex.x, vertex.y, vertex.getParent().x, vertex.getParent().y, 2);

            for (Point vertex : currentPath)
                sr.circle(vertex.x, vertex.y, 5);

        } catch (ConcurrentModificationException e) {

        }



        sr.end();

    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {}
}
