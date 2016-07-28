package yio.tro.evolution;

/**
 * Created by ivan on 22.07.14.
 */
public class RectangleYio {

    public float x;
    public float y;
    public float width;
    public float height;


    public RectangleYio(double x, double y, double width, double height) {
        set(x, y, width, height);
    }


    public RectangleYio(RectangleYio src) {
        set(src.x, src.y, src.width, src.height);
    }


    public void set(double x, double y, double width, double height) {
        this.x = (float) x;
        this.y = (float) y;
        this.width = (float) width;
        this.height = (float) height;
    }
}
