package shapes;

public class Rectangle extends Shape {

    private float width,height;
    private Point origin;

    public Rectangle(Point p1, float width, float height) {
        super(p1,
                new Point(p1,0, height),
                new Point(p1, width, height),
                new Point(p1, width, 0));

        this.width = width;
        this.height = height;
        origin = p1;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Point getOrigin() {
        return origin;
    }
}
