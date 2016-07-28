package yio.tro.evolution;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import yio.tro.evolution.plot.Plot;

import java.util.ArrayList;

/**
 * Created by ivan on 22.07.14.
 */
public class MenuView {
    YioGdxGame yioGdxGame;
    MenuController menuController;
    TextureRegion buttonPixel, shadowCorner, shadowSide, blackCircle;
    ShapeRenderer shapeRenderer;
    SpriteBatch batch;
    int cornerSize, w, h;
    float x1, y1, x2, y2; // local variables for rendering
    Color c; // local variable for rendering

    public MenuView(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        menuController = yioGdxGame.menuController;
        shapeRenderer = new ShapeRenderer();
        cornerSize = (int)(0.02 * Gdx.graphics.getHeight());
        buttonPixel = GameView.loadTextureRegionByName("files/button_pixel.png", false);
        shadowCorner = GameView.loadTextureRegionByName("files/corner_shadow.png", true);
        shadowSide = GameView.loadTextureRegionByName("files/side_shadow.png", true);
        blackCircle = GameView.loadTextureRegionByName("files/anim_circle_high_res.png", false);
    }

    private void drawRoundRect(SimpleRectangle pos) {
        shapeRenderer.rect((float)pos.x + cornerSize, (float)pos.y, (float)pos.width - 2 * cornerSize, (float)pos.height);
        shapeRenderer.rect((float)pos.x, (float)pos.y + cornerSize, (float)pos.width, (float)pos.height - 2 * cornerSize);
        shapeRenderer.circle((float)pos.x + cornerSize, (float)pos.y + cornerSize, cornerSize);
        shapeRenderer.circle((float)pos.x + (float)pos.width - cornerSize, (float)pos.y + cornerSize, cornerSize);
        shapeRenderer.circle((float)pos.x + cornerSize, (float)pos.y + (float)pos.height - cornerSize, cornerSize);
        shapeRenderer.circle((float)pos.x + (float)pos.width - cornerSize, (float)pos.y + (float)pos.height - cornerSize, cornerSize);
    }

    private void drawRect(SimpleRectangle pos) {
        shapeRenderer.rect((float)pos.x, (float)pos.y, (float)pos.width, (float)pos.height);
    }

    private void renderShadow(Button button, SpriteBatch batch) {
        x1 = button.x1;
        x2 = button.x2;
        y1 = button.y1;
        y2 = button.y2;
        if (button.factorModel.get() <= 1)
            batch.setColor(c.r, c.g, c.b, (float) button.factorModel.get());
        else batch.setColor(c.r, c.g, c.b, 1);
        batch.draw(shadowSide, x1 + cornerSize, y2 - cornerSize, 2 * (button.hor - cornerSize), 2 * cornerSize);
        batch.draw(shadowSide, x1 + cornerSize, y1 + cornerSize, 0, 0, 2 * (button.ver - cornerSize), 2 * cornerSize, 1, 1, 90);
        batch.draw(shadowSide, x2 - cornerSize, y1 + cornerSize, 0, 0, 2 * (button.hor - cornerSize), 2 * cornerSize, 1, 1, 180);
        batch.draw(shadowSide, x2 - cornerSize, y2 - cornerSize, 0, 0, 2 * (button.ver - cornerSize), 2 * cornerSize, 1, 1, 270);
        batch.draw(shadowCorner, x1 - cornerSize, y2 - cornerSize, 2 * cornerSize, 2 * cornerSize);
        batch.draw(shadowCorner, x1 + cornerSize, y1 - cornerSize, 0, 0, 2 * cornerSize, 2 * cornerSize, 1, 1, 90);
        batch.draw(shadowCorner, x2 + cornerSize, y1 + cornerSize, 0, 0, 2 * cornerSize, 2 * cornerSize, 1, 1, 180);
        batch.draw(shadowCorner, x2 - cornerSize, y2 + cornerSize, 0, 0, 2 * cornerSize, 2 * cornerSize, 1, 1, 270);
    }

    private void renderShadow(SimpleRectangle rectangle, float factor, SpriteBatch batch) {
        float hor = 0.5f * factor * (float)rectangle.width;
        float ver = 0.5f * factor * (float)rectangle.height;
        float cx = (float)rectangle.x + 0.5f * (float)rectangle.width;
        float cy = (float)rectangle.y + 0.5f * (float)rectangle.height;
        x1 = cx - hor;
        x2 = cx + hor;
        y1 = cy - ver;
        y2 = cy + ver;
        if (factor <= 1)
            batch.setColor(c.r, c.g, c.b, factor);
        else batch.setColor(c.r, c.g, c.b, 1);
        batch.draw(shadowSide, x1 + cornerSize, y2 - cornerSize, 2 * (hor - cornerSize), 2 * cornerSize);
        batch.draw(shadowSide, x1 + cornerSize, y1 + cornerSize, 0, 0, 2 * (ver - cornerSize), 2 * cornerSize, 1, 1, 90);
        batch.draw(shadowSide, x2 - cornerSize, y1 + cornerSize, 0, 0, 2 * (hor - cornerSize), 2 * cornerSize, 1, 1, 180);
        batch.draw(shadowSide, x2 - cornerSize, y2 - cornerSize, 0, 0, 2 * (ver - cornerSize), 2 * cornerSize, 1, 1, 270);
        batch.draw(shadowCorner, x1 - cornerSize, y2 - cornerSize, 2 * cornerSize, 2 * cornerSize);
        batch.draw(shadowCorner, x1 + cornerSize, y1 - cornerSize, 0, 0, 2 * cornerSize, 2 * cornerSize, 1, 1, 90);
        batch.draw(shadowCorner, x2 + cornerSize, y1 + cornerSize, 0, 0, 2 * cornerSize, 2 * cornerSize, 1, 1, 180);
        batch.draw(shadowCorner, x2 - cornerSize, y2 + cornerSize, 0, 0, 2 * cornerSize, 2 * cornerSize, 1, 1, 270);
    }

    boolean checkForSpecialMask(Button button) {
        switch (button.id) {
            case 3:
                shapeRenderer.circle(button.cx, button.cy, (float)(0.9 + 0.1 * button.selectionFactor.get()) * button.hor);
                return true;
        }
        return false;
    }

    boolean checkForSpecialAnimationMask(Button button) { // mask when circle fill animation on press
        SimpleRectangle pos = button.animPos;
        switch (button.id) {
            case 41: // main menu button
                shapeRenderer.rect((float)pos.x, (float)(pos.y + 0.5 * pos.height), (float)pos.width, 0.5f * (float)pos.height);
                return true;
            case 42: // resume button
                shapeRenderer.rect((float)pos.x, (float)pos.y, (float)pos.width, 0.5f * (float)pos.height);
                return true;
            case 43: // new game button
                shapeRenderer.rect((float)pos.x, (float)pos.y, (float)pos.width, (float)pos.height);
                return true;
            case 44: // restart button
                shapeRenderer.rect((float)pos.x, (float)pos.y, (float)pos.width, (float)pos.height);
                return true;
        }
        return false;
    }

    void renderSlider(SliderYio slider) {
        if (slider.appearFactor.get() == 0) return;
        batch.begin();
        Color c = batch.getColor();
        batch.setColor(c.r, c.g, c.b, slider.appearFactor.get());
        batch.draw(yioGdxGame.gameView.blackPixel, slider.getX(), slider.currentVerticalPos - 0.0025f * h, slider.getWidth(), 0.005f * h);
        for (int i = 0; i < slider.getNumberOfSegmentsForView() + 1; i++) {
            GameView.drawFromCenter(batch, yioGdxGame.gameView.blackCircleTexture, slider.getSegmentLeftSidePos(i), slider.currentVerticalPos, slider.getSegmentCenterSize(i));
        }
        GameView.drawFromCenter(batch, yioGdxGame.gameView.blackCircleTexture, slider.getX() + slider.runnerValue * slider.getWidth(), slider.currentVerticalPos, slider.circleSize);
        yioGdxGame.plotFont.setColor(0, 0, 0, slider.appearFactor.get());
        yioGdxGame.plotFont.draw(batch, slider.getValueString(), slider.getX() + slider.getWidth() - slider.textWidth, slider.currentVerticalPos + 0.05f * h);
        yioGdxGame.plotFont.setColor(0, 0, 0, 1);
        batch.setColor(c.r, c.g, c.b, c.a);
        batch.end();
    }

    public void render() {
        ArrayList<Button> buttons = menuController.buttons;
        batch = yioGdxGame.batch;
        c = batch.getColor();

        if (menuController.arePlotsValid) {
            for (Plot plot : menuController.plots) {
                if (plot.isVisible()) renderPlot(plot);
            }
        }

        //shadows
        batch.begin();
        for (Button button : buttons) {
            if (button.isVisible() && !button.currentlyTouched && button.hasShadow && !button.mandatoryShadow && button.factorModel.get() > 0.1) {
                renderShadow(button, batch);
            }
        }
        batch.end();

        // Drawing masks
        YioGdxGame.maskingBegin();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Button button : buttons) {
            if (button.isVisible()) {
                if (checkForSpecialMask(button)) continue;
                if (button.rectangularMask && !button.currentlyTouched) {
                    drawRect(button.position);
                    continue;
                }
                drawRoundRect(button.animPos);
            }
        }
        shapeRenderer.end();


        // Drawing buttons
        batch.begin();
        YioGdxGame.maskingContinue();
        SimpleRectangle ap;
        for (Button button : buttons) {
            if (button.isVisible() && !button.onlyShadow) {
                if (button.mandatoryShadow) renderShadow(button, batch);
                if (button.factorModel.get() <= 1)
                    batch.setColor(c.r, c.g, c.b, (float) button.factorModel.get());
                else batch.setColor(c.r, c.g, c.b, 1);
                ap = button.animPos;
                batch.draw(button.textureRegion, (float)ap.x, (float)ap.y, (float)ap.width, (float)ap.height);
                if (button.isCurrentlyTouched() && (!button.touchAnimation || button.selectionFactor.get() > 0.99)) {
                    batch.setColor(c.r, c.g, c.b, 0.7f * button.selAlphaFactor.get());
                    batch.draw(buttonPixel, (float)ap.x, (float)ap.y, (float)ap.width, (float)ap.height);
                    if (button.touchAnimation && button.lockAction) button.lockAction = false;
                }
            }
        }
        batch.setColor(c.r, c.g, c.b, 1);
        batch.end();
        YioGdxGame.maskingEnd();

        for (Button button : buttons) {
            if (button.isVisible() && button.isCurrentlyTouched() && button.touchAnimation && button.selectionFactor.get() <= 0.99) {
                YioGdxGame.maskingBegin();
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                checkForSpecialAnimationMask(button);
                drawRoundRect(button.animPos);
                shapeRenderer.end();

                batch.begin();
                YioGdxGame.maskingContinue();
                batch.setColor(c.r, c.g, c.b, 0.7f * button.selAlphaFactor.get());
                float r = (float) button.selectionFactor.get() * button.animR;
                batch.draw(blackCircle, button.touchX - r, button.touchY - r, 2 * r, 2 * r);
                batch.end();
                batch.setColor(c.r, c.g, c.b, 1);
                YioGdxGame.maskingEnd();
            }
        }

        for (SliderYio sliderYio : menuController.sliders) renderSlider(sliderYio);
    }

    void renderPlot(Plot plot) {
        batch = yioGdxGame.batch;

        YioGdxGame.maskingBegin();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawRoundRect(plot.getPos());
        shapeRenderer.end();

        batch.begin();
        YioGdxGame.maskingContinue();
        batch.draw(plot.getTextureRegion(), plot.getPos().getX(), plot.getPos().getY(), plot.getPos().getWidth(), plot.getPos().getHeight());
        batch.end();
        YioGdxGame.maskingEnd();
    }

    void renderScroller(Scroller scroller) {
        batch = yioGdxGame.batch;
        c = batch.getColor();
        batch.begin();
        if (scroller.factorModel.get() > 0.1) renderShadow(scroller.animFrame, (float) scroller.factorModel.get(), batch);
        batch.end();
        Gdx.gl.glClearDepthf(1f);
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glDepthFunc(GL20.GL_LESS);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthMask(true);
        Gdx.gl.glColorMask(false, false, false, false);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawRoundRect(scroller.animFrame);
        shapeRenderer.end();
        batch.begin();
        Gdx.gl.glColorMask(true, true, true, true);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthFunc(GL20.GL_EQUAL);
        batch.draw(scroller.bg1, (float) scroller.animFrame.x, (float) scroller.animFrame.y, (float) scroller.animFrame.width, (float) scroller.animFrame.height);
        float y = (float) scroller.animFrame.y + (float) scroller.animFrame.height + scroller.pos;
        int index = 0;
        for (TextureRegion textureRegion : scroller.cache) {
            if (y <= scroller.animFrame.y + scroller.animFrame.height + scroller.lineHeight && y >= scroller.animFrame.y) {
                batch.draw(textureRegion, (float) scroller.frame.x, y - scroller.lineHeight, (float) scroller.frame.width, scroller.lineHeight);
                if (index == scroller.selectionIndex && scroller.selectionFactor.get() > 0.99) {
                    batch.setColor(c.r, c.g, c.b, 0.5f);
                    batch.draw(buttonPixel, (float) scroller.frame.x, y - scroller.lineHeight, (float) scroller.frame.width, scroller.lineHeight);
                    batch.setColor(c.r, c.g, c.b, 1);
                }
            }
            y -= scroller.lineHeight;
            index++;
        }
        batch.end();
        batch.setColor(c.r, c.g, c.b, 1);
        if (scroller.selectionFactor.get() <= 0.99) {
            y = (float) scroller.animFrame.y + (float) scroller.animFrame.height + scroller.pos - scroller.selectionIndex * scroller.lineHeight;
            if (y > scroller.animFrame.y + scroller.animFrame.height + scroller.lineHeight && y < scroller.animFrame.y) return;
            Gdx.gl.glClearDepthf(1f);
            Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
            Gdx.gl.glDepthFunc(GL20.GL_LESS);
            Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
            Gdx.gl.glDepthMask(true);
            Gdx.gl.glColorMask(false, false, false, false);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            float ay = y - scroller.lineHeight;
            float ah = scroller.lineHeight;
            if (ay < scroller.frame.y) {
                float d = (float) scroller.frame.y - ay;
                ay += d;
                ah -= d;
            } else if (ay + ah > scroller.frame.y + scroller.frame.height) {
                float d = (float)(y + scroller.lineHeight - scroller.frame.y - scroller.frame.height);
                ah -= d - scroller.lineHeight;
            }
            shapeRenderer.rect((float) scroller.frame.x, ay, (float) scroller.frame.width, ah);
            shapeRenderer.end();
            batch.begin();
            batch.setColor(c.r, c.g, c.b, 0.5f);
            Gdx.gl.glColorMask(true, true, true, true);
            Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
            Gdx.gl.glDepthFunc(GL20.GL_EQUAL);
            float cx = scroller.selectX;
            float cy = (float)(y - 0.5 * scroller.lineHeight);
            float dw = 1.1f * (float) scroller.selectionFactor.get() * scroller.animRadius;
            batch.draw(blackCircle, cx - dw, cy - dw, 2 * dw, 2 * dw);
            batch.end();
            batch.setColor(c.r, c.g, c.b, 1);
        }
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    }
}
