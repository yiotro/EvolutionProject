package yio.tro.evolution;

import com.badlogic.gdx.Gdx;
import yio.tro.evolution.factor_yio.FactorYio;

import java.util.ArrayList;

/**
 * Created by ivan on 15.08.2015.
 */
public class SliderYio {

    MenuController menuControllerLighty;
    float runnerValue, currentVerticalPos, circleSize, segmentSize, textWidth;
    float viewMagnifier, circleDefaultSize, verticalTouchOffset, viewX, viewWidth;
    FactorYio appearFactor;
    FactorYio sizeFactor;
    boolean fromUp, isCurrentlyPressed;
    int numberOfSegments, configureType, minNumber;
    public static final int CONFIGURE_NUMBER = 0;
    public static final int CONFIGURE_FACTOR = 1;
    public static final int CONFIGURE_TUMBLER = 2;
    public static final int CONFIGURE_FOOD_VALUE = 3;
    Rect pos;
    String valueString;
    ArrayList<SliderYio> listeners;


    public SliderYio(MenuController menuControllerLighty) {
        this.menuControllerLighty = menuControllerLighty;
        appearFactor = new FactorYio();
        sizeFactor = new FactorYio();
        pos = new Rect(0, 0, 0, 0);
        fromUp = true;
        circleDefaultSize = 0.015f * Gdx.graphics.getHeight();
        circleSize = circleDefaultSize;
        listeners = new ArrayList<SliderYio>();
        verticalTouchOffset = 0.1f * Gdx.graphics.getHeight();
    }


    void setPos(double kx, double ky, double kw, double kh) {
        pos.x = (int) (kx * Gdx.graphics.getWidth());
        pos.y = (int) (ky * Gdx.graphics.getHeight());
        pos.width = (int) (kw * Gdx.graphics.getWidth());
        pos.height = (int) (kh * Gdx.graphics.getHeight());
    }


    boolean isCoorInsideSlider(float x, float y) {
        return x > pos.x - 0.05f * Gdx.graphics.getWidth() &&
                x < pos.x + pos.width + 0.05f * Gdx.graphics.getWidth() &&
                y > currentVerticalPos - verticalTouchOffset &&
                y < currentVerticalPos + verticalTouchOffset;
    }


    boolean touchDown(float x, float y) {
        if (isCoorInsideSlider(x, y) && appearFactor.get() == 1) {
            sizeFactor.beginSpawning(3, 2);
            isCurrentlyPressed = true;
            setValueByX(x);
            return true;
        }
        return false;
    }


    boolean touchUp(float x, float y) {
        if (isCurrentlyPressed) {
            sizeFactor.beginDestroying(1, 1);
            isCurrentlyPressed = false;
            updateValueString();
            return true;
        }
        return false;
    }


    void touchDrag(float x, float y) {
        if (isCurrentlyPressed) {
            setValueByX(x);
        }
    }


    boolean isVisible() {
        return appearFactor.get() > 0;
    }


    void setValueByX(float x) {
        x -= pos.x;
        runnerValue = x / pos.width;
        if (runnerValue < 0) runnerValue = 0;
        if (runnerValue > 1) runnerValue = 1;
        updateValueString();
    }


    void pullRunnerToCenterOfSegment() {
        double cx = getCurrentRunnerIndex() * segmentSize;
        double delta = cx - runnerValue;
        runnerValue += 0.2 * delta;
    }


    void move() {
        if (appearFactor.needsToMove()) {
            appearFactor.move();
            viewWidth = pos.width * appearFactor.get();
            viewX = pos.x + 0.5f * pos.width - 0.5f * viewWidth;
        }
        if (sizeFactor.needsToMove()) sizeFactor.move();
        circleSize = circleDefaultSize + 0.01f * Gdx.graphics.getHeight() * sizeFactor.get();
//        if (fromUp) {
//            currentVerticalPos = (1 - appearFactor.get()) * (1.1f * Gdx.graphics.getHeight() - pos.y) + pos.y;
//        } else {
//            currentVerticalPos = appearFactor.get() * (pos.y + 0.1f * Gdx.graphics.getHeight()) - 0.1f * Gdx.graphics.getHeight();
//        }
        currentVerticalPos = pos.y;
        if (!isCurrentlyPressed) pullRunnerToCenterOfSegment();
    }


    public void setValues(double runnerValue, int minNumber, int maxNumber, boolean fromUp, int configureType) {
        setRunnerValue((float) runnerValue);
        setNumberOfSegments(maxNumber - minNumber);
        setFromUp(fromUp);
        this.configureType = configureType;
        this.minNumber = minNumber;
        updateValueString();
    }


    public void setRunnerValue(float runnerValue) {
        this.runnerValue = runnerValue;
    }


    public int getCurrentRunnerIndex() {
        return (int) (runnerValue / segmentSize + 0.5);
    }


    public void setNumberOfSegments(int numberOfSegments) {
        this.numberOfSegments = numberOfSegments;
        segmentSize = 1.01f / numberOfSegments;
        viewMagnifier = (numberOfSegments + 1f) / numberOfSegments;
    }


    public void addListener(SliderYio sliderYio) {
        if (listeners.contains(sliderYio)) return;
        listeners.add(sliderYio);
    }


    void notifyListeners() {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).beNotifiedAboutChange(this);
        }
    }


    void beNotifiedAboutChange(SliderYio sliderYio) {
        int s = sliderYio.getCurrentRunnerIndex() + sliderYio.minNumber;
        setNumberOfSegments(s);
        updateValueString();
    }


    public void appear() {
        appearFactor.beginSpawning(3, 2);
    }


    public void destroy() {
        appearFactor.beginDestroying(3, 3);
    }


    public float getX() {
        return viewX;
    }


    public float getWidth() {
        return viewWidth;
    }


    float getSegmentCenterSize(int index) {
//        float cx = index * segmentSize;
//        float dist = Math.abs(runnerValue - cx);
//        if (dist > 0.5f * segmentSize) dist = 0.5f * segmentSize;
//        dist /= segmentSize;
//        dist *= 2;
//        if (!isCurrentlyPressed) dist = 1.0f;
//        float f = 0.5f + (1.0f - dist);
//        return f * circleDefaultSize;
        return 0.4f * circleSize;
    }


    float getSegmentLeftSidePos(int index) {
        return pos.x + index * segmentSize * pos.width;
    }


    void updateValueString() {
        switch (configureType) {
            default:
            case CONFIGURE_NUMBER:
                valueString = "" + (getCurrentRunnerIndex() + minNumber);
                break;
            case CONFIGURE_FACTOR:
                valueString = "" + YioGdxGame.roundUp((getCurrentRunnerIndex()) / 100f, 4);
                break;
            case CONFIGURE_TUMBLER:
                if (getCurrentRunnerIndex() == 1) {
                    valueString = "on";
                } else {
                    valueString = "off";
                }
                break;
            case CONFIGURE_FOOD_VALUE:
                double f = getCurrentRunnerIndex() / 100f;
                valueString = "" + YioGdxGame.roundUp(f, 2);
                break;
        }
        textWidth = YioGdxGame.getTextWidth(menuControllerLighty.yioGdxGame.plotFont, valueString);
        notifyListeners();
    }


    public void setVerticalTouchOffset(float verticalTouchOffset) {
        this.verticalTouchOffset = verticalTouchOffset;
    }


    public int getNumberOfSegmentsForView() {
        if (numberOfSegments > 10) return -1;
        return numberOfSegments;
    }


    String getValueString() {
        return valueString;
    }


    public void setFromUp(boolean fromUp) {
        this.fromUp = fromUp;
    }
}
