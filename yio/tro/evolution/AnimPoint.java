package yio.tro.evolution;

import yio.tro.evolution.factor_yio.FactorYio;

/**
 * Created by ivan on 13.08.2014.
 */
public class AnimPoint {
    float x, y;
    int appurtenance;
    FactorYio factorModel;

    public AnimPoint(float x, float y, int appurtenance) {
        this.x = x;
        this.y = y;
        this.appurtenance = appurtenance;
        factorModel = new FactorYio();
    }
}
