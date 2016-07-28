package yio.tro.evolution;

/**
 * Created by ivan on 22.07.14.
 */
public class SimpleRectangle {
    public double x;
    public double y;
    public double width;
    public double height;

    public SimpleRectangle(double x, double y, double width, double height) {
        set(x, y, width, height);
    }

    public SimpleRectangle(SimpleRectangle src) {
        set(src.x, src.y, src.width, src.height);
    }

    public void set(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public float getX() {
        return (float)x;
    }

    public float getY() {
        return (float)y;
    }

    public float getWidth() {
        return (float)width;
    }

    public float getHeight() {
        return (float)height;
    }

    public float getCenterX() {
        return (float)x + 0.5f * (float)width;
    }
}
