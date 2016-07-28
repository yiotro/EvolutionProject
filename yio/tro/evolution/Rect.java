package yio.tro.evolution;

/**
 * Created by ivan on 25.08.2014.
 */
public class Rect {
    int x, y, width, height;

    public Rect(double x, double y, double width, double height) {
        set(x, y, width, height);
    }

    public void set(double x, double y, double width, double height) {
        this.x = (int)x;
        this.y = (int)y;
        this.width = (int)width;
        this.height = (int)height;
    }
}
