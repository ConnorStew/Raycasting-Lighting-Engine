package components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import shapes.Line;
import shapes.Point;
import shapes.Shape;

import java.util.ArrayList;

public class World {

    private static World INSTANCE = new World();

    private ArrayList<Shape> shapes;

    private ArrayList<Line> lines;

    private ArrayList<Point> vertices;

    private static ArrayList<Line> rays = new ArrayList<>();
    private static ArrayList<Point> visibleVertices = new ArrayList<>();

    private World() {
        World.INSTANCE = this;
        this.shapes = new ArrayList<Shape>();
        this.lines = new ArrayList<Line>();
        this.vertices = new ArrayList<Point>();
    }

    public static void addShape(Shape toAdd) {
        INSTANCE.shapes.add(toAdd);
        INSTANCE.lines.addAll(toAdd.getLines());
        INSTANCE.vertices.addAll(toAdd.getVertices());

        for (Line line : toAdd.getLines())
            line.setShape(toAdd);

        for (Point point : toAdd.getVertices())
            point.setShape(toAdd);
    }

    public static ArrayList<Shape> getShapes() {
        return INSTANCE.shapes;
    }

    public static ArrayList<Line> getLines() {
        return INSTANCE.lines;
    }

    public static ArrayList<Point> getVertices() {
        return INSTANCE.vertices;
    }

    /**
     * Draws the shapes stored in the world to the given shape renderer.
     * @param sr the shape renderer to draw to
     */
    public static void drawShapes(ShapeRenderer sr) {
        sr.begin();
        sr.setColor(Color.PURPLE);
        for (Line line : World.getLines())
            sr.line(line.getP1().toVector2(), line.getP2().toVector2());

        sr.setColor(Color.WHITE);
        for (Point vertex : World.getVertices())
            sr.circle(vertex.x, vertex.y, 5);

        sr.end();
    }

    public static void drawDebug(ShapeRenderer sr) {
        sr.begin();
        sr.setColor(Color.TEAL);
        for (Line line : rays)
            sr.line(line.getP1().toVector2(), line.getP2().toVector2());

        sr.setColor(Color.PINK);
        sr.set(ShapeRenderer.ShapeType.Filled);
        for (Point vertex : visibleVertices)
            sr.circle(vertex.x, vertex.y, 5);

        sr.end();
    }

    public static boolean hasLOS(Point origin, Point target) {
        rays.clear();

        int intersectionCount = 0;

        rays.add(new Line(origin, target));

        for (Line ray : rays) {
            for (Line line : getLines()) {
                if (line.getP2().equals(origin))
                    continue;

                Point intersection = ray.intersects(line);
                if (intersection != null) {
                    intersectionCount++;
                }
            }
        }

        return intersectionCount == 0;
    }

    /**
     * Raycasts for visible vertices within the world.
     * @param origin the origin point to raycast from
     * @return a list of visible points
     */
    public static ArrayList<Point> raycastForPoints(Point origin) {
        rays.clear();
        visibleVertices.clear();

        for (Shape shape : getShapes())
            for (Point vertex : shape.getVertices())
                rays.add(new Line(origin, vertex));

        Array<Point> hits = new Array<Point>(); //the location of any rays that intersect with a line

        for (Line ray : rays) {
            for (Line line : getLines()) {
                if (line.getP2().equals(origin))
                    continue;

                Point intersection = ray.intersects(line);
                if (intersection != null) {
                    hits.add(intersection);
                }
            }

            hits.removeValue(origin, false);

            //add the lowest hit if a hit occurred
            if (hits.size > 0) {
                Point lowest = null;
                for (Point hit : hits)
                    if (lowest == null || origin.distanceTo(hit) < origin.distanceTo(lowest))
                        lowest = hit;

                for (Point vertex : getVertices())
                    if (vertex.equals(lowest) && !vertex.getShape().equals(origin.getShape()))
                        visibleVertices.add(vertex);

                hits.clear();
            }
            hits.clear();
        }

        return visibleVertices;
    }
}
