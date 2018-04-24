import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;

import java.nio.IntBuffer;
import java.util.HashSet;

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

    /** The vertices of the shapes. */
    private Array<Vector2> vertices = new Array<Vector2>();

    /** Whether to draw debug information. */
    private final boolean drawDebug = true;

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
        font.getData().setScale(2);

        shapes.addAll(
                //wall
                new Rectangle(
                        new Vector2(0, 0),
                        new Vector2(0, 1000),
                        new Vector2(1000, 1000),
                        new Vector2(1000, 0)
                ),
                new Rectangle(
                        new Vector2(300, 300),
                        new Vector2(300, 350),
                        new Vector2(320, 370),
                        new Vector2(320, 300)
                ),
                new Rectangle(
                        new Vector2(40, 60),
                        new Vector2(100, 100),
                        new Vector2(250, 250),
                        new Vector2(100, 50)
                ),
                new Rectangle(
                        new Vector2(300, -100),
                        new Vector2(100, -100),
                        new Vector2(100, -200),
                        new Vector2(300, -200)
                ),
                new Triangle(
                        new Vector2(500, 100),
                        new Vector2(700, 100),
                        new Vector2(500, 400)
                ),
                new Triangle(
                        new Vector2(200, 700),
                        new Vector2(400, 900),
                        new Vector2(600, 700)
                )
        );

        int x1 = 500, x2 = 505;
        int y1 = 505, y2 = 500;
        int amount = 8;

        for (int i = 0; i < amount; i++) {
            shapes.add(
                 new Rectangle(
                     new Vector2(x1, y1),
                     new Vector2(x1, y2),
                     new Vector2(x2, y2),
                     new Vector2(x2, y1)
                 )
            );

            y1 = y1 + 10;
            y2 = y2 + 10;
        }

        //get points to cast towards
        for (Shape shape : shapes) {
            for (Line line : shape.getLines()) {
                for (Vector2 vertex : line.getVertices()) {
                    Math.round(vertex.y);
                    Math.round(vertex.x);
                    if (!vertices.contains(vertex, false))
                        vertices.add(vertex);
                }
            }
        }
    }

    public void render() {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //update mouse position
        Vector3 mp3 = cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        mp = new Vector2(mp3.x, mp3.y);

        Texture map = raycast();

        sb.begin();

        sb.draw(map, 0,0,  map.getWidth(), map.getHeight(), 0,0,
                map.getWidth(), map.getHeight(), false, true);

        sb.end();

        sr.begin();

        for (Shape shape : shapes) {
            for (Line line : shape.getLines()) {
                sr.setColor(Color.PURPLE);
                sr.line(line.getP1(), line.getP2());
            }
        }

        sr.end();

        /*
        https://stackoverflow.com/questions/24922539/drawing-shapes-as-textures-in-libgdx
        https://stackoverflow.com/questions/24820240/libgdx-open-gles-2-0-stencil-alpha-masking
        */
    }

    private Texture raycast() {
        Array<Line> rays = new Array<Line>();
        Array<Line> collidedRays = new Array<Line>();
        Array<Triangle> triangles = new Array<Triangle>();

        for (Vector2 vertex : vertices) {
            rays.add(new Line(mp, vertex));
            rays.add(new Line(mp, getAngleBetween(mp, vertex), 0.001));
            rays.add(new Line(mp, getAngleBetween(mp, vertex), -0.001));
        }

        Array<Vector2> hits = new Array<Vector2>();

        for (Line ray : rays) {
            for (Shape shape : shapes) {
                for (Line line : shape.getLines()) {
                    Vector2 intersection = getIntersectionBetweenLines(ray.getP1(), ray.getP2(), line.getP1(), line.getP2());
                    if (intersection != null){
                        hits.add(intersection);
                    }
                }
            }

            //add the lowest hit if a hit occurred
            if (hits.size > 0) {
                Vector2 lowest = hits.get(0);
                for (Vector2 hit : hits)
                    if (mp.dst(hit) < mp.dst(lowest))
                        lowest = hit;

                collidedRays.add(new Line(mp, lowest));
            }

            hits.clear();
        }


        //sort valid rays by angle using bubble sort
        int n = collidedRays.size;
        int k;
        for (int m = n; m >= 0; m--) {
            for (int i = 0; i < n - 1; i++) {
                k = i + 1;
                if (collidedRays.get(i).getAngle() > collidedRays.get(k).getAngle()) {
                    Line temp;
                    temp = collidedRays.get(i);
                    collidedRays.set(i, collidedRays.get(k));
                    collidedRays.set(k, temp);
                }
            }
        }

        //create the triangles using the sorted rays
        for (int i = 0; i < collidedRays.size - 1; i++) {
            triangles.add(new Triangle(
                    collidedRays.get(i).getP2(),
                    mp,
                    collidedRays.get(i + 1).getP2()
            ));
        }

        //connect the last hit to the first
        triangles.add(new Triangle(
                collidedRays.get(0).getP2(),
                mp,
                collidedRays.get(collidedRays.size - 1).getP2()
        ));

        Pixmap map = new Pixmap(1000,1000, Pixmap.Format.RGBA8888);
        map.setBlending(Pixmap.Blending.None);
        map.setColor(Color.BLACK);
        map.fillRectangle(0,0, map.getWidth(), map.getHeight());
        map.setColor(Color.WHITE);
        for (Shape triangle : triangles) {
            map.fillTriangle(
                    (int)triangle.getLines().get(0).getP1().x,
                    (int)triangle.getLines().get(0).getP1().y,
                    (int)triangle.getLines().get(1).getP1().x,
                    (int)triangle.getLines().get(1).getP1().y,
                    (int)triangle.getLines().get(2).getP1().x,
                    (int)triangle.getLines().get(2).getP1().y
            );
        }

        if (drawDebug) {
            sr.begin();
            //draw all rays that hit
            for (Line ray : collidedRays) {
                sr.setColor(Color.RED);
                sr.circle(ray.getP2().x, ray.getP2().y, 5);
                sr.setColor(Color.WHITE);
                sr.line(mp, ray.getP2());
            }

            //draw vertices of the shapes
            for (Vector2 vertex : vertices) {
                sr.setColor(Color.ORANGE);
                sr.circle(vertex.x, vertex.y, 5);
            }

            //draw triangles
            for (Shape triangle : triangles) {
                sr.setColor(Color.RED);
                for (Line line : triangle.getLines()) {
                    sr.line(line.getP1(), line.getP2());
                }
            }
            sr.end();
        }

        return new Texture(map);
    }

    /**
     * Checks if the point toCheck is on the line (p1, p2).
     * @param p1 the first point of the line
     * @param p2 the second point of the line
     * @param toCheck the point to check
     * @return if the toCheck point is on the line
     */
    private boolean isPointOnLine(Vector2 p1, Vector2 p2, Vector2 toCheck) {
        if (p1.dst(toCheck) + p2.dst(toCheck) == p1.dst(p2))
            return true; // C is on the line.
        return false;    // C is not on the line.
    }

    private double getAngleBetween(Vector2 p1, Vector2 p2) {
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

    private double getAngleBetween(Vector2 p1, Vector2 p2, double radiansOffset) {
        double xDistance = p1.x - p2.x;
        double yDistance = p1.y - p2.y;
        double tanc = yDistance / xDistance;
        double angle = Math.toDegrees(Math.atan(tanc) + radiansOffset);
        double rotation; //the angle to cast towards

        //tan only goes to 180 so reverse the angle when its on the left hand side
        if (xDistance > 0)
            rotation = angle - 180;
        else
            rotation = angle;

        return rotation;
    }

    public Vector2 getIntersectionBetweenLines(Vector2 p1, Vector2 p2, Vector2 p3, Vector2 p4){
        Vector2 intersection = null;

        double r_x = p2.x - p1.x;
        double r_y = p2.y - p1.y;

        double s_x = p4.x - p3.x;
        double s_y = p4.y - p3.y;

        double denom = r_x*s_y - r_y*s_x;

        double u = ((p3.x - p1.x)*r_y - (p3.y - p1.y)*r_x) / denom;
        double t = ((p3.x -  p1.x)*s_y - (p3.y - p1.y)*s_x) / denom;

        if(t >= 0 && t <= 1 && u >= 0 && u <= 1){
            intersection = new Vector2((float)(p1.x + t*r_x), (float) (p3.y + u*s_y));
        }

        return intersection;
    }

    public void resize(int i, int i1) {}
    public void pause() {}
    public void resume() {}
    public void dispose() {}
}

