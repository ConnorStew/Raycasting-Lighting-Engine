package Lighting;

import Shapes.Line;
import Shapes.Shape;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.text.DecimalFormat;

/**
 * This class is used to define a raycasting light.
 */
public abstract class Light {

    /** How far a ray from this light is cast. */
    private final double LENGTH;

    /** This lights colour. */
    private final Color COLOUR;

    /** The last set of rays casted by this light that have hit. */
    private final Array<Line> collidedRays;

    private final Array<Line> rays;

    /**
     * Creates a new light.
     * @param length the length of this light
     * @param colour the lights colour
     */
    Light(double length, Color colour) {
        LENGTH = length;
        COLOUR = colour;
        collidedRays = new Array<Line>();
        rays = new Array<Line>();
    }

    /**
     * Casts rays from this light to every shape vertex in the shapes array from the origin.
     * @param shapes the shapes to cast towards
     * @param origin the origin point of this light
     * @return a pixmap of triangles created from the rays
     */
    public Pixmap raycast(Array<Shape> shapes, Vector2 origin) {
        collidedRays.clear();
        rays.clear();

        Array<Line> shapeLines = new Array<Line>(); //the lines that create the shapes

        for (Shape shape : shapes) {
            //cast towards the shape vertices
            for (Vector2 vertex : shape.getVertices()) {
                Line firstLine = new Line(origin, vertex);
                rays.add(firstLine);
                rays.add(new Line(origin, firstLine.getAngle(), 0.001, LENGTH));
                rays.add(new Line(origin, firstLine.getAngle(), -0.001, LENGTH));
            }

            //add this shapes lines to be checked for intersections with rays
            shapeLines.addAll(shape.getLines());
        }

        Array<Vector2> hits = new Array<Vector2>(); //the location of any rays that intersect with a line

        for (Line ray : rays) {
            for (Line line : shapeLines) {
                Vector2 intersection = ray.intersects(line);
                if (intersection != null) {
                    hits.add(intersection);
                }
            }

            //add the lowest hit if a hit occurred
            if (hits.size > 0) {
                Vector2 lowest = hits.get(0);
                for (Vector2 hit : hits)
                    if (origin.dst(hit) < origin.dst(lowest))
                        lowest = hit;

                collidedRays.add(new Line(origin, lowest));
                hits.clear();
            }
        }

        if (collidedRays.size > 0) {
            //sort valid rays by angle using bubble sort
            int n = collidedRays.size;
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

            Pixmap map = new Pixmap(1000,1000, Pixmap.Format.RGBA8888);
            map.setColor(COLOUR);

            //create the triangles using the sorted rays
            for (int i = 0; i < collidedRays.size - 1; i++) {
                map.fillTriangle(
                        (int)collidedRays.get(i).getP2().x,
                        (int)collidedRays.get(i).getP2().y,
                        (int)origin.x,
                        (int)origin.y,
                        (int)collidedRays.get(i + 1).getP2().x,
                        (int)collidedRays.get(i + 1).getP2().y
                );
            }

            //connect the last hit to the first
            map.fillTriangle(
                    (int)collidedRays.get(0).getP2().x,
                    (int)collidedRays.get(0).getP2().y,
                    (int)origin.x,
                    (int)origin.y,
                    (int)collidedRays.get(collidedRays.size - 1).getP2().x,
                    (int)collidedRays.get(collidedRays.size - 1).getP2().y
            );

            return map;
        }


        return new Pixmap(1000,1000, Pixmap.Format.RGBA8888);
    }

    public void drawDebug(ShapeRenderer sr, SpriteBatch sb, BitmapFont font) {

        DecimalFormat format = new DecimalFormat();
        format.applyPattern("#.##");
        for (Line ray : collidedRays) {
            sr.setColor(Color.RED);
            sr.line(ray.getP1(), ray.getP2());
            sr.setColor(Color.GREEN);
            sr.circle(ray.getP2().x, ray.getP2().y, 5);



            double xDistance = ray.getP1().x - ray.getP2().x;
            double yDistance = ray.getP1().y - ray.getP2().y;
            double tanc = yDistance / xDistance;
            double angle = Math.toDegrees(Math.atan(tanc));

            font.draw(sb, format.format(angle), ray.getP2().x, ray.getP2().y);
        }


    }

}
