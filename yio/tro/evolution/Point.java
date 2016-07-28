package yio.tro.evolution;

/**
 * Created by ivan on 28.08.2014.
 */
public class Point {
    public float x, y;

    public Point() {
        set(0, 0);
    }

    public Point(double x, double y) {
        set(x, y);
    }

    public void set(double x, double y) {
        this.x = (float)x;
        this.y = (float)y;
    }
}
