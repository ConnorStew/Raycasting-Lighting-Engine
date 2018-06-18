package components.pathfinding;

import com.badlogic.gdx.graphics.Color;
import components.World;
import components.lighting.Light;
import org.omg.PortableServer.POA;
import shapes.Circle;
import shapes.Line;
import shapes.Point;
import shapes.Shape;
import sun.security.provider.certpath.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Look here https://github.com/RavenTheorist/A-Star-Project/blob/master/src/a/star/project/GraphImplementation/Graph.java.
 * https://www.raywenderlich.com/4946/introduction-to-a-pathfinding
 */
public class Graph {

    /** The nodes of the {@link Graph}. */
    private ArrayList<Point> vertices = new ArrayList<Point>();

    /** List of {@link #vertices} which are still being considered for the {@link #path}. */
    private LinkedList<Point> openList = new LinkedList<Point>();

    /** List of {@link #vertices} which are not being considered for the {@link #path}. */
    private ArrayList<Point> closedList = new ArrayList<Point>();

    /** List of {@link #vertices} which create the shortest chat. */
    private ArrayList<Point> path = new ArrayList<Point>();

    public Graph(ArrayList<Shape> shapes) {
        for (Point vertex : vertices) {
            vertex.getNeighbours().clear();
            vertex.setParent(null);
        }

        vertices.clear();

        for (Shape shape : shapes)
            vertices.addAll(shape.getVertices());

        for (Point vertex : getVertices())
            addVertex(vertex);

        for (Line line : World.getLines()) {
            line.getP1().addNeighbour(line.getP2());
            line.getP2().addNeighbour(line.getP1());
        }

        openList.clear();
        closedList.clear();
        path.clear();
    }

    /**
     * Adds a vertex to the graph.
     * @param vertex the vertex to add
     */
    public void addVertex(Point vertex) {
        ArrayList<Point> neighbours = World.raycastForPoints(vertex);
        vertex.addNeighbours(neighbours);
        for (Point neighbour : neighbours)
            neighbour.addNeighbour(vertex);
    }

    public void removeVertex(Point vertex) {
        for (Point neighbour : vertex.getNeighbours())
            neighbour.getNeighbours().remove(vertex);
        vertex.getNeighbours().clear();
        vertices.remove(vertex);
    }


    /**
     * Uses the A* algorithm to find the best path between the supplied vertices.
     */
    public ArrayList<Point> aStar(Point start, Point end) {
        openList.clear();
        closedList.clear();
        path.clear();

        addVertex(start);
        addVertex(end);

        if (World.hasLOS(start, end)) {
            path.add(start);
            start.setParent(end);
            path.add(end);
            removeVertex(start);
            removeVertex(end);
            return path;
        }

        final long wait = 0;
        openList.add(start);

        while (!openList.isEmpty()) {
            if (openList.contains(end)) {
                path.add(end);

                Point current = end;
                while (current.getParent() != null) {
                    path.add(current.getParent());
                    current = current.getParent();
                    try {
                        Thread.sleep(wait);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                removeVertex(start);
                removeVertex(end);
                return path;
            }

            //get lowest vertices
            LinkedList<Point> lowestVertices = new LinkedList<Point>();
            float lowestScore = Float.MAX_VALUE;

            for (Point vertex : openList)
                if (vertex.getScore() < lowestScore)
                    lowestScore = vertex.getScore();

            for (Point vertex : openList)
                if (vertex.getScore() == lowestScore)
                    lowestVertices.add(vertex);

            for (Point vertex : lowestVertices) {
                closedList.add(vertex);
                openList.remove(vertex);

                //add neighbours that aren't in the list
                for (Point neighbour : vertex.getNeighbours()) {
                    if (!openList.contains(neighbour) && !closedList.contains(neighbour)) {
                        if (closedList.contains(neighbour))
                            continue;

                        float tentativeG = vertex.getG() + Math.abs(vertex.getX() - neighbour.getX()) + Math.abs(vertex.getY() - neighbour.getY());
                        float tentativeH = Math.abs(end.getX() - neighbour.getX()) + Math.abs(end.getY() - neighbour.getY());
                        float tentativeScore = (tentativeG + tentativeH) * 1; //should times by weight

                        if (!openList.contains(neighbour)) {
                            openList.add(neighbour);
                            setScore(neighbour, vertex, tentativeScore, tentativeG, tentativeH);
                        }

                        //update the score of the neighbouring vertex
                        if (tentativeScore < neighbour.getScore())
                            setScore(neighbour, vertex, tentativeScore, tentativeG, tentativeH);
                    }
                }

            }

            try {
                Thread.sleep(wait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        removeVertex(start);
        removeVertex(end);
        System.out.println("Path not found.");
        return null;
    }

    /**
     * https://en.wikipedia.org/wiki/Theta*
     *
    public void thetaStar() {
        startVertex.setParent(startVertex);
        startVertex.setScore(0);

        openList.push(startVertex);

        while (!openList.isEmpty()) {
            Point point = openList.pop();
            if (point.equals(endVertex)) {
                reconstructPath(point, startVertex);
                return;
            }

            closedList.add(point);

            for (Point neighbour : point.getNeighbours()) {
                if (!closedList.contains(neighbour)) {
                    if (!openList.contains(neighbour)) {
                        neighbour.setScore(Float.MAX_VALUE);
                        neighbour.setParent(null);
                    }
                    updateVertex(point, neighbour);
                }
            }
        }
    }

    private void updateVertex(Point point, Point neighbour) {
        Point parent = point.getParent();
        if (World.hasLOS(point.getParent(), neighbour)) {
            //use the path from the parent to the neighbour if it has los
            if ((parent.getScore()) < neighbour.getScore()) {
                float tentativeG = parent.getG() + Math.abs(parent.getX() - neighbour.getX()) + Math.abs(parent.getY() - neighbour.getY());
                float tentativeH = Math.abs(parent.getX() - neighbour.getX()) + Math.abs(endVertex.getY() - neighbour.getY());
                float tentativeScore = (tentativeG + tentativeH) * 1; //should times by weight

                neighbour.setScore(tentativeScore);
                neighbour.setParent(parent);
                openList.add(neighbour);
            }
        } else {
            if (point.getScore() < neighbour.getScore()) {
                float tentativeG = point.getG() + Math.abs(point.getX() - neighbour.getX()) + Math.abs(point.getY() - neighbour.getY());
                float tentativeH = Math.abs(point.getX() - neighbour.getX()) + Math.abs(endVertex.getY() - neighbour.getY());
                float tentativeScore = (tentativeG + tentativeH) * 1; //should times by weight

                neighbour.setScore(tentativeScore);
                neighbour.setParent(point);
                openList.add(neighbour);
            }
        }
    }

    private void reconstructPath(Point point, Point start) {
        path.add(point);

        if (!point.getParent().equals(start))
            reconstructPath(point.getParent(), start);
    }

     */

    private void setScore(Point vertex, Point parent, float score, float g, float h) {
        vertex.setScore(score);
        vertex.setG(g);
        vertex.setH(h);
        vertex.setParent(parent);
    }

    /**
     * Gets the vertex at the desired x,y coordinate.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param distance given
     * @return the vertex at the position or null if none is found
     */
    public Point getVertexAt(float x, float y, float distance) {
        for (Point vertex : vertices)
            if (vertex.x > x - distance && vertex.x < x + distance && vertex.y > y - distance && vertex.y < y + distance)
                return vertex;

        return null;
    }

    public List<Point> getOpenList() {
        return Collections.unmodifiableList(openList);
    }

    public List<Point> getClosedList() {
        return Collections.unmodifiableList(closedList);
    }

    public List<Point> getPath() {
        return Collections.unmodifiableList(path);
    }

    public List<Point> getVertices() {
        return Collections.unmodifiableList(vertices);
    }



}
