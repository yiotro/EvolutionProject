package yio.tro.evolution.models;

import yio.tro.evolution.GameController;
import yio.tro.evolution.YioGdxGame;
import yio.tro.evolution.factor_yio.FactorYio;

/**
 * Created by ivan on 31.01.2015.
 */
public class Grass {

    GameController gameController;
    public Herb herb1, herb2;
    public FactorYio factorModel;
    public float length, angle, width;
    public double x, y;

    public Grass(Herb herb1, Herb herb2, GameController gameController) {
        this.herb1 = herb1;
        this.herb2 = herb2;
        this.gameController = gameController;
        factorModel = new FactorYio();
        factorModel.setValues(0, 0);
        factorModel.beginSpawning(1, 10);
        length = (float)YioGdxGame.distance(herb1.x, herb1.y, herb2.x, herb2.y);
        angle = (float)YioGdxGame.angle(herb1.x, herb1.y, herb2.x, herb2.y);
        width = 0.05f * gameController.w;
        x = 0.5 * (herb1.x + herb2.x);
        y = 0.5 * (herb1.y + herb2.y);
    }

    public void move() {
        if (factorModel.get() < 1)
            factorModel.move();
    }
}
