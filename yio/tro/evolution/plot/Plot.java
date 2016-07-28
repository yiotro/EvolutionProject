package yio.tro.evolution.plot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.evolution.MenuController;
import yio.tro.evolution.Point;
import yio.tro.evolution.SimpleRectangle;
import yio.tro.evolution.YioGdxGame;
import yio.tro.evolution.factor_yio.FactorYio;

/**
 * Created by ivan on 16.10.2015.
 */
public class Plot {
    MenuController menuController;
    TextureRegion textureRegion;
    FactorYio spawnFactor, moveFactor;
    SimpleRectangle pos;
    double dx, startX, endX;
    int w, h, index;
    float lastTouchX, touchDownX;
    boolean currentlyTouched;
    public static final float PLOT_WIDTH_PLUS_OFFSET = 0.85f * Gdx.graphics.getWidth();

    public Plot(MenuController menuController, int index) {
        this.menuController = menuController;
        this.index = index;
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        pos = new SimpleRectangle(0, 0, 0.8 * w, 0.8 * h);
        spawnFactor = new FactorYio();
        moveFactor = new FactorYio();
    }

    public void move() {
        spawnFactor.move();
        if (spawnFactor.get() == 1) {
            pos.y = 0.05f * h;
        } else {
            pos.y = - 0.95f * h + spawnFactor.get() * h;
        }
        if (currentlyTouched) {
            pos.x += dx;
            dx *= 0.9;
        } else {
            moveFactor.move();
            pos.x = startX + moveFactor.get() * (endX - startX);
        }
    }

    public void setX(double x) {
        pos.x = x;
        startX = pos.x;
    }

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public boolean isVisible() {
        if (pos.x > w) return false;
        if (pos.x + pos.width < 0) return false;
        return true;
    }

    public boolean isValid() {
        return spawnFactor.get() > 0 || spawnFactor.getDy() > 0;
    }

    public FactorYio getSpawnFactor() {
        return spawnFactor;
    }

    public SimpleRectangle getPos() {
        return pos;
    }

    public void hide() {
        spawnFactor.setValues(1, 0);
        spawnFactor.beginDestroying(1, 3);
    }

    public void touchDown(int touchX, int touchY) {
        lastTouchX = touchX;
        touchDownX = touchX;
        currentlyTouched = true;
    }

    public void touchDragged(int touchX, int touchY) {
        if (!currentlyTouched) return;
        dx = 1 * (touchX - lastTouchX);

        lastTouchX = touchX;
    }

    public void touchUp(int touchX, int touchY) {
        if (!currentlyTouched) return;
        moveFactor.setValues(0, 0);
        moveFactor.beginSpawning(3, 1);
        lastTouchX = touchX;
        startX = pos.x;
        Plot nearestPlot = menuController.getNearestToCenterPlot(touchX - touchDownX);
        double delta = 0.1 * w - nearestPlot.pos.x;
        endX = startX + delta;
        currentlyTouched = false;
        dx = 0;
    }
}
