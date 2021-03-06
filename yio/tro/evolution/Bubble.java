package yio.tro.evolution;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by ivan on 10.08.2014.
 */
public class Bubble {

    float x, y, dx, dy, r, dr, diam;
    TextureRegion textureRegion;

    public Bubble() {
        setPos(0, 0);
        setSpeed(0, 0);
        setRadius(0, 0);
    }

    void move() {
        x += dx;
        y += dy;
        dx *= 0.99;
        dy *= 0.99;
        r += dr;
        if (r < 0) r = 0;
        diam = 2 * r;
    }

    void setPos(float x, float y) {
        this.x = x;
        this.y = y;
    }

    void setSpeed(float dx, float dy) {
        this.dx = dx;
        this.dy = dy;
    }

    void setRadius(float r, float dr) {
        this.r = r;
        this.dr = dr;
        diam = 2 * r;
    }

    void limitByWalls(float rightLim) {
        if (x < 0) {
            x = 0;
            dx = -dx;
        }
        if (x > rightLim) {
            x = rightLim;
            dx = -dx;
        }
    }

    void gravity(double gravity) {
        dy -= gravity;
    }

    boolean isVisible() {
        return r > 1;
    }

    public void setTextureRegion(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }
}
