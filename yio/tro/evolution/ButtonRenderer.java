package yio.tro.evolution;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;

/**
 * Created by ivan on 22.07.14.
 */
public class ButtonRenderer {
    FrameBuffer frameBuffer;
    ShapeRenderer shapeRenderer;
    SpriteBatch batch;
    SimpleRectangle pos;
    BitmapFont font;
    TextureRegion buttonBackground, bigButtonBackground;

    protected ButtonRenderer() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        buttonBackground = GameView.loadTextureRegionByName("files/button_background.png", true);
        buttonBackground.flip(false, true);
        bigButtonBackground = GameView.loadTextureRegionByName("files/big_button_background.png", true);
        bigButtonBackground.flip(false, true);
    }

    void beginRender(Button button) {
        frameBuffer = new FrameBuffer(Pixmap.Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        frameBuffer.begin();
        Gdx.gl.glClearColor(button.backColor.r, button.backColor.g, button.backColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        Matrix4 matrix4 = new Matrix4();
//        matrix4.setToOrtho2D(0, 0, Gdx.graphics.getWidth() * 2, Gdx.graphics.getHeight() * 2);
//        batch.setProjectionMatrix(matrix4);
        batch.begin();
        if (button.position.height < 0.2 * Gdx.graphics.getHeight())
            batch.draw(buttonBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        else
            batch.draw(bigButtonBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
        pos = new SimpleRectangle(button.position);
        font = YioGdxGame.font;
    }

    void endRender(Button button) {
        Texture texture = frameBuffer.getColorBufferTexture();
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        button.textureRegion = new TextureRegion(texture, (int)pos.width, (int)pos.height);
        frameBuffer.end();
    }

    public void renderButton(Button button) {
        beginRender(button);
        BitmapFont font = YioGdxGame.font;
        float ratio = (float)(pos.width / pos.height);
        int lineHeight = (int)(1.2f * YioGdxGame.FONT_SIZE);
        int horizontalOffset = (int)(0.3f * YioGdxGame.FONT_SIZE);
        if (button.text.size() == 1) {
            //if button has single line of text then center it
            float textWidth = getTextWidth(button.text.get(0), font);
            horizontalOffset = (int)(0.5 * (1.35f * YioGdxGame.FONT_SIZE * ratio - textWidth));
            if (horizontalOffset < 0) {
                horizontalOffset = (int)(0.3f * YioGdxGame.FONT_SIZE);
            }
        }
        int verticalOffset = (int)(0.3f * YioGdxGame.FONT_SIZE);
        int lineNumber = 0;
        float longestLineLength = 0, currentLineLength;
        batch.begin();
        font.setColor(0, 0, 0, 1);
        for (String line : button.text) {
            font.draw(batch, line, horizontalOffset, verticalOffset + lineNumber * lineHeight);
            currentLineLength = getTextWidth(line, font);
            if (currentLineLength > longestLineLength) longestLineLength = currentLineLength;
            lineNumber++;
        }
        batch.end();
        pos.height = button.text.size() * lineHeight + verticalOffset / 2;
        pos.width = pos.height * ratio;
        if (longestLineLength > pos.width - 0.3f * (float)lineHeight) {
            pos.width = longestLineLength + 2 * horizontalOffset;
        }
        endRender(button);
    }


    private int getTextWidth(String text, BitmapFont font) {
        return (int) YioGdxGame.getTextWidth(font, text);
    }
}
