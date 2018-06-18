package shapes;

public class Triangle extends Shape {

    public Triangle(Point p1, Point p2, Point p3) {
       super(p1, p2, p3);
    }

    public Triangle(int x1, int y1, int x2, int y2, int x3, int y3) {
        super(new Point(x1, y1), new Point(x2,y2), new Point(x3, y3));
    }
}
