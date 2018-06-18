package components.lighting;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import components.World;
import shapes.Line;
import shapes.Point;
import shapes.Shape;
import shapes.Triangle;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * This class is used to define a raycasting light.
 */
public class Light {

    /** How far a ray from this light is cast. */
    private static final double LENGTH = 50000;

    /** This lights colour. */
    private final Color COLOUR;

    /** The last set of rays casted by this light that have hit. */
    private final ArrayList<Line> collidedRays;

    /** The rays that are sent out. */
    private final ArrayList<Line> rays;

    private final ArrayList<Point> visibleVertice = new ArrayList<Point>();

    private static final boolean DRAW_TEXT = false;
    private static final boolean DRAW_LINES = true;
    private Point origin;

    /**
     * Creates a new light.
     * @param length the length of this lights
     * @param colour the lights colour
     */
    public Light(double length, Color colour) {
        //LENGTH = length;
        COLOUR = colour;
        collidedRays = new ArrayList<Line>();
        rays = new ArrayList<Line>();
    }

    public ArrayList<Triangle> raycastForTriangles(ArrayList<Shape> shapes) {
        collidedRays.clear();
        rays.clear();
        visibleVertice.clear();

        ArrayList<Triangle> triangles = new ArrayList<Triangle>();
        ArrayList<Line> shapeLines = new ArrayList<Line>(); //the lines that create the shapes

        for (Shape shape : shapes) {
            //cast towards the shape vertices
            for (Point vertex : shape.getVertices()) {
                Line firstLine = new Line(origin, vertex);
                rays.add(firstLine);
                rays.add(new Line(origin, firstLine.getAngle(), 0.001, LENGTH));
                rays.add(new Line(origin, firstLine.getAngle(), -0.001, LENGTH));
            }

            //add this shapes lines to be checked for intersections with rays
            shapeLines.addAll(shape.getLines());
        }

        Array<Point> hits = new Array<Point>(); //the location of any rays that intersect with a line

        for (Line ray : rays) {
            for (Line line : shapeLines) {
                Point intersection = ray.intersects(line);
                if (intersection != null) {
                    hits.add(intersection);
                }
            }

            //add the lowest hit if a hit occurred
            if (hits.size > 0) {
                Point lowest = hits.get(0);
                for (Point hit : hits)
                    if (origin.distanceTo(hit) < origin.distanceTo(lowest))
                        lowest = hit;

                visibleVertice.add(lowest);
                collidedRays.add(new Line(origin, lowest));
                hits.clear();
            }
        }

        if (collidedRays.size() > 0) {
            //sort valid rays by angle using bubble sort
            int n = collidedRays.size();
            int k;
            for (int m = n; m >= 0; m--) {
                for (int i = 0; i < n - 1; i++) {
                    k = i + 1;
                    if (Line.getAngleBetween(collidedRays.get(i).getP1(), collidedRays.get(i).getP2()) > Line.getAngleBetween(collidedRays.get(k).getP1(), collidedRays.get(k).getP2())) {
                        Line temp;
                        temp = collidedRays.get(i);
                        collidedRays.set(i, collidedRays.get(k));
                        collidedRays.set(k, temp);
                    }
                }
            }

            //create the triangles using the sorted rays
            for (int i = 0; i < collidedRays.size() - 1; i++) {
                triangles.add(new Triangle(
                        (int)collidedRays.get(i).getP2().x,
                        (int)collidedRays.get(i).getP2().y,
                        (int)origin.x,
                        (int)origin.y,
                        (int)collidedRays.get(i + 1).getP2().x,
                        (int)collidedRays.get(i + 1).getP2().y
                ));
            }

            //connect the last hit to the first
            triangles.add(new Triangle(
                    (int)collidedRays.get(0).getP2().x,
                    (int)collidedRays.get(0).getP2().y,
                    (int)origin.x,
                    (int)origin.y,
                    (int)collidedRays.get(collidedRays.size() - 1).getP2().x,
                    (int)collidedRays.get(collidedRays.size() - 1).getP2().y
            ));

        }


        return triangles;
    }

    public Color getColour() {
        return COLOUR;
    }

    public void drawDebug(ShapeRenderer sr) {
        sr.begin();
        DecimalFormat format;
        if (DRAW_TEXT) {
            format = new DecimalFormat();
            format.applyPattern("#.##");
        }

        sr.set(ShapeRenderer.ShapeType.Filled);
        sr.setColor(Color.FOREST);
        sr.circle(origin.x, origin.y, 5);

        if (DRAW_LINES) {
            for (Line ray : collidedRays) {
                sr.setColor(Color.RED);
                sr.line(ray.getP1().toVector2(), ray.getP2().toVector2());
                sr.setColor(Color.GREEN);
                sr.circle(ray.getP2().x, ray.getP2().y, 5);
            }
        }
        sr.end();
    }

    public void update(float interpolation) {

    }

    public void setOrigin(Point point) {
        origin = point;
    }

    public Point getOrigin() {
        return origin;
    }

    public ArrayList<Point> getVisibleVertices() {
        return visibleVertice;
    }

    public static ArrayList<Point> raycastForVisiblePoints(ArrayList<Shape> shapes, Point origin) {
        ArrayList<Line> rays = new ArrayList<>();
        ArrayList<Point> visibleVertices = new ArrayList<>();
        ArrayList<Line> shapeLines = new ArrayList<Line>(); //the lines that create the shapes
        ArrayList<Point> hits = new ArrayList<Point>(); //the location of any rays that intersect with a line

        for (Shape castFrom : shapes) {
            for (Point endVertex : castFrom.getVertices()) {
                if (endVertex.equals(origin) || origin.getShape() != null && origin.getShape().equals(castFrom))
                    continue;

                //the amount of times the start vertex intersects before reaching the end vertex
                int intersectionCount = 0;
                Line ray = new Line(origin, endVertex);
                for (Line line : World.getLines()) {
                    if (ray.intersects(line) != null) {
                        intersectionCount++;
                    }
                }

                if (intersectionCount < 5) {
                    visibleVertices.add(endVertex);
                }
            }
        }

        return visibleVertices;
    }
}
