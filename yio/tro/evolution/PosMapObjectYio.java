package yio.tro.evolution;

/**
 * Created by ivan on 12.03.2016.
 */
public abstract class PosMapObjectYio {

    public double x, y;
    PMCoor writtenIndexPoint, indexPoint;


    public PosMapObjectYio() {
        writtenIndexPoint = new PMCoor();
        indexPoint = new PMCoor();
    }
}
