package yio.tro.evolution.plot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import yio.tro.evolution.*;
import yio.tro.evolution.models.MatrixModel;

import java.util.ArrayList;

/**
 * Created by ivan on 16.10.2015.
 */
public class PlotFactory {

    YioGdxGame yioGdxGame;
    FrameBuffer frameBuffer;
    SpriteBatch batch;
    BitmapFont font;
    TextureRegion plotBackground, blackPixel, redPixel, greenPixel, blackCircle, magentaPixel;
    int w, h, plotColor;
    public static final int PLOT_COLOR_BLACK = 0;
    public static final int PLOT_COLOR_RED = 1;
    public static final int PLOT_COLOR_GREEN = 2;
    public static final int PLOT_COLOR_MAGENTA = 3;
    float axisThickness, plotThickness;
    Point yAxisPos, xAxisPos;
    ArrayList<Integer> list;
    ArrayList<FrameBuffer> frameBuffers;
    String plotName;


    public PlotFactory(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
        batch = new SpriteBatch();
        plotBackground = GameView.loadTextureRegionByName("files/white_pixel.png", true);
        blackPixel = GameView.loadTextureRegionByName("files/black_pixel.png", true);
        redPixel = GameView.loadTextureRegionByName("files/red_pixel.png", true);
        greenPixel = GameView.loadTextureRegionByName("files/green_pixel.png", true);
        blackCircle = GameView.loadTextureRegionByName("files/black_circle.png", true);
        magentaPixel = GameView.loadTextureRegionByName("files/magenta_pixel.png", true);
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        yAxisPos = new Point(0.1 * w, 0.9 * h);
        xAxisPos = new Point(0.9 * w, 0.1 * h);
        axisThickness = 0.01f * w;
        plotThickness = axisThickness / 2;
        frameBuffers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            frameBuffers.add(new FrameBuffer(Pixmap.Format.RGB565, w, h, false));
        }
    }


    void beginRender(Plot plot) {
        frameBuffer = frameBuffers.get(plot.index);
        frameBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(plotBackground, 0, 0, w, h);
        batch.end();
    }


    void endRender(Plot plot) {
        Texture texture = frameBuffer.getColorBufferTexture();
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        plot.textureRegion = new TextureRegion(texture, w, h);
        plot.textureRegion.flip(false, true);
        frameBuffer.end();
    }


    private void renderPlotBase() {
        batch.begin();

        // axis
        GameView.drawLine(yAxisPos.x - axisThickness / 2, xAxisPos.y, xAxisPos.x - axisThickness / 2, xAxisPos.y, axisThickness, batch, blackPixel);
        GameView.drawLine(yAxisPos.x, xAxisPos.y - axisThickness / 2, yAxisPos.x, yAxisPos.y - axisThickness / 2, axisThickness, batch, blackPixel);

        float arrowOffset = axisThickness / (2f * (float) (Math.sqrt(2)));

        // upper arrow
        GameView.drawLine(yAxisPos.x + arrowOffset, yAxisPos.y, yAxisPos.x - 2 * axisThickness + arrowOffset, yAxisPos.y - 2 * axisThickness, axisThickness, batch, blackPixel);
        GameView.drawLine(yAxisPos.x - arrowOffset, yAxisPos.y, yAxisPos.x + 2 * axisThickness - arrowOffset, yAxisPos.y - 2 * axisThickness, axisThickness, batch, blackPixel);

        // right arrow
        GameView.drawLine(xAxisPos.x, xAxisPos.y + arrowOffset, xAxisPos.x - 2 * axisThickness, xAxisPos.y - 2 * axisThickness + arrowOffset, axisThickness, batch, blackPixel);
        GameView.drawLine(xAxisPos.x, xAxisPos.y - arrowOffset, xAxisPos.x - 2 * axisThickness, xAxisPos.y + 2 * axisThickness - arrowOffset, axisThickness, batch, blackPixel);

        batch.end();
    }


    private TextureRegion getPixelByColor() {
        switch (plotColor) {
            default:
            case PLOT_COLOR_BLACK:
                return blackPixel;
            case PLOT_COLOR_RED:
                return redPixel;
            case PLOT_COLOR_GREEN:
                return greenPixel;
            case PLOT_COLOR_MAGENTA:
                return magentaPixel;
        }
    }


    private int getMaxValueInList(ArrayList<Integer> list) {
        int max = 0;
        for (Integer integer : list) {
            int current = integer.intValue();
            if (current > max) max = current;
        }
        return max;
    }


    private void renderLine(ArrayList<Integer> list, int max, boolean tagX) {
        boolean showDots = false;
        if (list.size() < 30) showDots = true;

        SimpleRectangle plotViewPos = new SimpleRectangle(yAxisPos.x + axisThickness / 2, xAxisPos.y + axisThickness / 2, 0.75 * w, 0.7 * h);
        float delta = (float) plotViewPos.width / ((float) (list.size() - 1));
        float currentX = 0;
        TextureRegion pixel = getPixelByColor();
        batch.begin();

        int step = 1;
        float defDelta = delta;
        while (delta < 0.005 * w) {
            step++;
            delta = defDelta * (float) step;
        }

        for (int i = 0; i < list.size() - step; i += step) {
            float fy1 = (float) list.get(i) / (float) max;
            float fy2 = (float) list.get(i + step) / (float) max;
            GameView.drawLine(plotViewPos.x + currentX, plotViewPos.y + fy1 * plotViewPos.getHeight(), plotViewPos.x + currentX + delta, plotViewPos.y + fy2 * plotViewPos.getHeight(), plotThickness, batch, pixel);
            currentX += delta;
        }

        renderDots(list, max, showDots, plotViewPos, delta);

        // tag max
        renderMaxTagY(max, plotViewPos);
        if (tagX) renderMaxTagX(list, plotViewPos);
        batch.end();
    }


    private void renderMaxTagX(ArrayList<Integer> list, SimpleRectangle plotViewPos) {
        GameView.drawLine(plotViewPos.x + plotViewPos.width, xAxisPos.y - 2 * axisThickness, plotViewPos.x + plotViewPos.width, xAxisPos.y + 2 * axisThickness, plotThickness, batch, blackPixel);
        float textWidth = getTextWidth("" + list.size(), font);
        font.draw(batch, "" + list.size(), plotViewPos.getX() + plotViewPos.getWidth() - textWidth / 2, xAxisPos.y - 0.022f * w);
    }


    private void renderMaxTagY(int max, SimpleRectangle plotViewPos) {
        GameView.drawLine(yAxisPos.x - 2 * axisThickness, plotViewPos.y + plotViewPos.height, yAxisPos.x + 2 * axisThickness, plotViewPos.y + plotViewPos.height, plotThickness, batch, blackPixel);
        font.draw(batch, getProperRepresentationOfNumber(max), yAxisPos.x - 0.08f * w, plotViewPos.getY() + plotViewPos.getHeight() + font.getLineHeight() / 2);
    }


    private void renderDots(ArrayList<Integer> list, float max, boolean showDots, SimpleRectangle plotViewPos, float delta) {
        float currentX;
        if (showDots) {
            currentX = 0;
            for (int i = 0; i < list.size(); i++) {
                float fy1 = (float) list.get(i) / max;
                GameView.drawFromCenter(batch, blackCircle, plotViewPos.x + currentX, plotViewPos.y + fy1 * plotViewPos.getHeight(), 1.5f * plotThickness);
                currentX += delta;
            }
        }
    }


    String getProperRepresentationOfNumber(int value) {
        if (value <= 1000) return "" + value;
        value += 1;
        value = (int) ((float) value / 100f);
        String str = Integer.toString(value);
        return str.charAt(0) + "." + str.charAt(1) + "K";
    }


    void renderSimplePlot(Plot plot, DataStorage dataStorage, int plotIndex) {
        if (isPlotSpecial(plotIndex)) {
            renderSpecialPlot(plot, dataStorage, plotIndex);
            return;
        }

        boolean comb[] = new boolean[MenuController.NUMBER_OF_PLOT_LINES];
        comb[plotIndex] = true;
        renderPlot(plot, dataStorage, comb);
    }


    private int getTextWidth(String text, BitmapFont font) {
        return (int) YioGdxGame.getTextWidth(font, text);
    }


    private void renderGenerationMatrix(Plot plot) {
        beginRender(plot);
        if (yioGdxGame.gameController.evolutionModel.isModelMatrix()) {
            MatrixModel matrixModel = (MatrixModel) yioGdxGame.gameController.evolutionModel;
            int genMatrix[][] = matrixModel.getGenRelationMatrix();
            int numberOfGenes = matrixModel.getNumberOfGenes();
            Color defColor = font.getColor();
            font.setColor(0, 0, 0, 1);
            float currentX;
            float deltaX = 0.9f * w / (numberOfGenes - 1);
            float deltaY = 0.9f * h / (numberOfGenes - 1);
            if (deltaX > 0.1f * w) deltaX = 0.1f * w;
            if (deltaY > 0.1f * h) deltaY = 0.1f * h;
            float currentY = 0.575f * h + 0.5f * (deltaY * numberOfGenes);
            batch.begin();
            for (int j = 0; j < numberOfGenes; j++) {
                currentX = 0.55f * w - 0.5f * deltaX * numberOfGenes;
                currentY -= deltaY;
                for (int i = 0; i < numberOfGenes; i++) {
                    float textWidth = getTextWidth("" + genMatrix[i][j], font);
                    font.draw(batch, "" + genMatrix[i][j], currentX - textWidth, currentY);
                    currentX += deltaX;
                }
            }
            batch.end();
            font.setColor(defColor);
        }
        endRender(plot);
    }


    private void renderSpecialPlot(Plot plot, DataStorage dataStorage, int plotIndex) {
        switch (plotIndex) {
            case MenuController.NUMBER_OF_PLOT_LINES: // generation matrix
                renderGenerationMatrix(plot);
                break;
        }
    }


    private int getMaxValueInPlot(DataStorage dataStorage, boolean combination[]) {
        int max = 0;
        for (int i = 0; i < combination.length; i++) {
            if (!combination[i]) continue;
            int currentValue = getMaxValueInList(dataStorage.getDataList(i));
            if (currentValue > max) max = currentValue;
        }
        return max;
    }


    private Color getTagColorByIndex(int colorIndex) {
        switch (colorIndex) {
            default:
            case PLOT_COLOR_BLACK:
                return Color.BLACK;
            case PLOT_COLOR_GREEN:
                return new Color(0, 0.5f, 0, 1);
            case PLOT_COLOR_RED:
                return Color.RED;
            case PLOT_COLOR_MAGENTA:
                return new Color(0.64f, 0.13f, 0.64f, 1);
        }
    }


    private void renderTags(DataStorage dataStorage, boolean combination[]) {
        batch.begin();
        float currentY = 0.9f * h;
        Color defColor = font.getColor();
        for (int i = 0; i < combination.length; i++) {
            if (!combination[i]) continue;
            float textWidth = YioGdxGame.getTextWidth(font, dataStorage.getPlotNameByIndex(i));
            int colorIndex = dataStorage.getPlotColorByIndex(i);
            font.setColor(getTagColorByIndex(colorIndex));
            font.draw(batch, dataStorage.getPlotNameByIndex(i), 0.95f * w - textWidth, currentY);
            currentY -= 0.05f * w;
        }
        font.setColor(defColor);
        batch.end();
    }


    private boolean isPlotSpecial(int plotIndex) {
        return plotIndex == MenuController.NUMBER_OF_PLOT_LINES; // generation matrix
    }


    public void renderPlot(Plot plot, DataStorage dataStorage, boolean combination[]) {
        beginRender(plot);
        renderPlotBase();

        boolean tagX = true;
        for (int i = 0; i < combination.length; i++) {
            if (!combination[i]) continue;

            plotColor = dataStorage.getPlotColorByIndex(i);
            ArrayList<Integer> list = dataStorage.getDataList(i);
            if (list.size() >= 2) {
                renderLine(list, getMaxValueInPlot(dataStorage, combination), tagX);
                tagX = false;
            }
        }

        renderTags(dataStorage, combination);
        endRender(plot);
    }


    public Plot getPlot(MenuController menuController, DataStorage dataStorage, int plotIndex) {
        list = dataStorage.getDataList(plotIndex);
        plotName = dataStorage.getPlotNameByIndex(plotIndex);
        plotColor = dataStorage.getPlotColorByIndex(plotIndex);
        Plot plot = new Plot(menuController, plotIndex);
        font = menuController.yioGdxGame.plotFont;
        renderSimplePlot(plot, dataStorage, plotIndex);
        plot.spawnFactor.setValues(0, 0);
        plot.spawnFactor.beginSpawning(3, 1);
        return plot;
    }
}
