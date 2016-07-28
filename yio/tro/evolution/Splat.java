package yio.tro.evolution;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.evolution.factor_yio.FactorYio;

/**
 * Created by ivan on 13.08.2014.
 */
public class Splat {

    TextureRegion textureRegion;
    float x, y, dx, dy;
    FactorYio factorModel;
    long timeToDestroy;

    public Splat(TextureRegion textureRegion, float x, float y) {
        this.textureRegion = textureRegion;
        this.x = x;
        this.y = y;
        factorModel = new FactorYio();
    }

    void move() {
        factorModel.move();
        if (System.currentTimeMillis() > timeToDestroy) {
            factorModel.beginDestroying(1, 0.5);
        }
        x += dx;
        y += dy;
    }

    void set(float x, float y, int lifeTime) {
        this.x = x;
        this.y = y;
        timeToDestroy = System.currentTimeMillis() + lifeTime + YioGdxGame.random.nextInt(200);
        factorModel.beginSpawning(1, 0.5);
    }

    void setSpeed(float sdx, float sdy) {
        dx = sdx;
        dy = sdy;
    }

    boolean isVisible() {
        return factorModel.get() > 0.1;
    }
}
