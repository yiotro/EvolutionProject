package yio.tro.evolution;

/**
 * Created by ivan on 28.08.2014.
 */
public class PMCoor {

    public int x, y;


    public PMCoor() {
        set(0, 0);
    }


    public void set(double x, double y) {
        this.x = (int) x;
        this.y = (int) y;
    }


    void setBy(PMCoor p) {
        this.x = p.x;
        this.y = p.y;
    }
}
