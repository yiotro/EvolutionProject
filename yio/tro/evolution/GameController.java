package yio.tro.evolution;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import yio.tro.evolution.models.*;

import java.util.Random;

/**
 * Created by ivan on 05.08.14.
 */
public class GameController {

    public static final int TOUCH_MODE_MOVE = 0;
    public static final int TOUCH_MODE_ADD_HERB = 1;
    public static final int TOUCH_MODE_ADD_ANIMAL = 2;
    public static final int MODEL_MATRIX = 1;
    public static final int MODEL_BASIC = 2;

    public YioGdxGame yioGdxGame;

    boolean blockMultiInput, blockDragToLeft, blockDragToRight, blockDragToDown, blockDragToUp;
    boolean multiTouchDetected;

    public int w, h, screenX, screenY, touchDownX, touchDownY;
    int maxTouchCount, currentTouchCount, lastTouchCount;
    int touchMode, chosenModel;

    public static long currentTime;
    long timeToUnblockMultiInput;
    long timeOne, timeTwo;

    float camDx, camDy, lastMultiTouchDistance, camDZoom, trackerZoom;
    float fieldX1, fieldY1, fieldX2, fieldY2; // bounds of field
    float frameX1, frameY1, frameX2, frameY2; // what is visible
    public float cos60, sin60, selectX, selectY, deltaMovementFactor;
    public float boundWidth, boundHeight;

    public Random random, predictableRandom;
    LanguagesManager languagesManager;
    OrthographicCamera orthoCam;
    public EvolutionModel evolutionModel;

    public DataStorage dataStorage;


    public GameController(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        cos60 = (float) Math.cos(Math.PI / 3d);
        sin60 = (float) Math.sin(Math.PI / 3d);
        random = new Random();
        predictableRandom = new Random(0);
        languagesManager = yioGdxGame.menuController.languagesManager;
        deltaMovementFactor = 48;
        boundWidth = 2 * w;
        boundHeight = 2 * h;
        chosenModel = MODEL_BASIC;
    }


    void createField() {
        evolutionModel.createField();
    }


    public static double angleBetweenObjects(GameObject one, GameObject two) {
        return YioGdxGame.angle(one.x, one.y, two.x, two.y);
    }


    public void updateMeasureTime() {
        timeOne = timeTwo;
        timeTwo = System.currentTimeMillis();
        YioGdxGame.say("" + (timeTwo - timeOne));
    }


    public void move() {
        currentTime = System.currentTimeMillis();
        if (Gdx.input.isKeyPressed(Input.Keys.K) && currentTime % 2 == 0) {
            evolutionModel.spawnManyAnimals(selectX, selectY);
        }
        evolutionModel.move();
    }


    public void cameraMovement() {
        evolutionModel.moveAlways();
        checkForKeys();
        float k = deltaMovementFactor * 0.017f;
        yioGdxGame.gameView.orthoCam.translate(k * camDx, k * camDy);
        yioGdxGame.gameView.updateCam();
        camDx *= 0.9;
        camDy *= 0.9;
        if (Math.abs(camDZoom) > 0.01) {
            if (trackerZoom > 2.1) {
                camDZoom = -0.1f;
                blockMultiInputForSomeTime(50);
            }
            if (trackerZoom < 0.4) {
                camDZoom = 0.015f;
                blockMultiInputForSomeTime(50);
            }
            yioGdxGame.gameView.orthoCam.zoom += 0.2 * camDZoom;
            trackerZoom += 0.2 * camDZoom;
            yioGdxGame.gameView.updateCam();
            camDZoom *= 0.9;
        }
        fieldX1 = 0.5f * w - orthoCam.position.x / orthoCam.zoom;
        fieldX2 = 0.5f * w + boundWidth / orthoCam.zoom - orthoCam.position.x / orthoCam.zoom;
        fieldY1 = 0.5f * h - orthoCam.position.y / orthoCam.zoom;
        fieldY2 = 0.5f * h + boundHeight / orthoCam.zoom - orthoCam.position.y / orthoCam.zoom;
        updateFrame();
        if (blockDragToLeft) blockDragToLeft = false;
        if (blockDragToRight) blockDragToRight = false;
        if (blockDragToUp) blockDragToUp = false;
        if (blockDragToDown) blockDragToDown = false;
        if (fieldX2 - fieldX1 < 1.1f * w) { //center
            float deltaX = 0.2f * (1f * w / orthoCam.zoom - orthoCam.position.x / orthoCam.zoom);
            yioGdxGame.gameView.orthoCam.translate(deltaX, 0);
        } else {
            if (fieldX1 > 0) {
                camDx = boundPower();
            }
            if (fieldX1 > -0.1 * w) blockDragToLeft = true;
            if (fieldX2 < w) {
                camDx = -boundPower();
            }
            if (fieldX2 < 1.1 * w) blockDragToRight = true;
        }
        if (fieldY2 - fieldY1 < 1.1f * h) {
            float deltaY = 0.2f * (1f * h / orthoCam.zoom - orthoCam.position.y / orthoCam.zoom);
            yioGdxGame.gameView.orthoCam.translate(0, deltaY);
        } else {
            if (fieldY1 > 0) {
                camDy = boundPower();
            }
            if (fieldY1 > -0.1 * w) blockDragToDown = true;
            if (fieldY2 < h) {
                camDy = -boundPower();
            }
            if (fieldY2 < 1.1 * h) blockDragToUp = true;
        }
    }


    float boundPower() {
        return 0.005f * w * trackerZoom;
    }


    public void setTouchMode(int touchMode) {
        this.touchMode = touchMode;
    }


    void updateFrame() {
        frameX1 = (0 - 0.5f * w) * orthoCam.zoom + orthoCam.position.x;
        frameX2 = (w - 0.5f * w) * orthoCam.zoom + orthoCam.position.x;
        frameY1 = (0 - 0.5f * h) * orthoCam.zoom + orthoCam.position.y;
        frameY2 = (h - 0.5f * h) * orthoCam.zoom + orthoCam.position.y;
    }


    void blockMultiInputForSomeTime(int time) {
        blockMultiInput = true;
        timeToUnblockMultiInput = System.currentTimeMillis() + time;
    }


    void prepareForNewGame(int index) {
        createEvolutionModel();
        trackerZoom = 1;
        touchMode = TOUCH_MODE_MOVE;
        yioGdxGame.speedMultiplier = 1;
        yioGdxGame.gameView.createOrthoCam();
        orthoCam = yioGdxGame.gameView.orthoCam;
        predictableRandom = new Random(index);
        createField();
        yioGdxGame.beginBackgroundChange(4);
        maxTouchCount = 0;
        currentTouchCount = 0;
        updateFrame();
        dataStorage = new DataStorage(this);
        dataStorage.takeSnapshot();
    }


    private void createEvolutionModel() {
        switch (chosenModel) {
            default:
            case MODEL_MATRIX:
                evolutionModel = new MatrixModel(yioGdxGame, this);
                break;
            case MODEL_BASIC:
                evolutionModel = new BasicModel(yioGdxGame, this);
                break;
        }
        evolutionModel.prepare();
    }


    public static long getSpedUpTime(long time) {
        return currentTime + (time - currentTime) / 2;
    }


    public static long getSlowedDownTime(long time) {
        return currentTime + (time - currentTime) * 2;
    }


    void touchDown(int screenX, int screenY, int pointer, int button) {
        currentTouchCount++;
        this.screenX = screenX;
        this.screenY = screenY;
        touchDownX = screenX;
        touchDownY = screenY;
        if (blockMultiInput) blockMultiInput = false;
        if (blockMultiInput) return;
        if (currentTouchCount == 1) {
            maxTouchCount = 1;
            multiTouchDetected = false;
            // touched down
        } else {
            multiTouchDetected = true;
            lastMultiTouchDistance = (float) YioGdxGame.distance(Gdx.input.getX(0), Gdx.input.getY(0), Gdx.input.getX(1), Gdx.input.getY(1));
        }

        if (currentTouchCount > maxTouchCount) maxTouchCount = currentTouchCount;
        lastTouchCount = currentTouchCount;
    }


    public static int maxNumberFromArray(int array[]) {
        int max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) max = array[i];
        }
        return max;
    }


    void touchUp(int screenX, int screenY, int pointer, int button) {
        currentTouchCount--;
        if (blockMultiInput) return;
        if (currentTouchCount == maxTouchCount - 1) {

        }
        if (currentTouchCount == 0) {
            if (!multiTouchDetected && YioGdxGame.distance(screenX, screenY, touchDownX, touchDownY) < 0.02 * w && Math.abs(camDx) < 0.005 * w && Math.abs(camDy) < 0.005 * w) {
                selectX = (screenX - 0.5f * w) * orthoCam.zoom + orthoCam.position.x;
                selectY = (screenY - 0.5f * h) * orthoCam.zoom + orthoCam.position.y;
                evolutionModel.touchUp(selectX, selectY);
            }
            multiTouchDetected = false;
        }
        lastTouchCount = currentTouchCount;
        // some stuff here
    }


    void touchDragged(int screenX, int screenY, int pointer) {
        if (yioGdxGame.gamePaused && touchMode != TOUCH_MODE_MOVE) return;
        selectX = (screenX - 0.5f * w) * orthoCam.zoom + orthoCam.position.x;
        selectY = (screenY - 0.5f * h) * orthoCam.zoom + orthoCam.position.y;
        switch (touchMode) {
            case TOUCH_MODE_MOVE:
                evolutionModel.notifyAboutMovement();
                if (multiTouchDetected) {
                    if (blockMultiInput) return;
                    float currentMultiTouchDistance = (float) YioGdxGame.distance(Gdx.input.getX(0), Gdx.input.getY(0), Gdx.input.getX(1), Gdx.input.getY(1));
                    camDZoom = lastMultiTouchDistance / currentMultiTouchDistance - 1;
                    if (camDZoom < 0) camDZoom *= 0.3;
                } else {
                    float currX, currY;
                    currX = (this.screenX - screenX) * trackerZoom;
                    currY = (this.screenY - screenY) * trackerZoom;
                    this.screenX = screenX;
                    this.screenY = screenY;
                    if (blockDragToLeft && currX < 0) currX = 0;
                    if (blockDragToRight && currX > 0) currX = 0;
                    if (blockDragToUp && currY > 0) currY = 0;
                    if (blockDragToDown && currY < 0) currY = 0;
                    if (Math.abs(currX) > 0.5 * Math.abs(camDx)) camDx = currX;
                    if (Math.abs(currY) > 0.5 * Math.abs(camDy)) camDy = currY;
                }
                break;
            case TOUCH_MODE_ADD_ANIMAL:
                evolutionModel.addAnimal(selectX, selectY);
                break;
            case TOUCH_MODE_ADD_HERB:
                evolutionModel.addHerb(selectX, selectY);
                break;
        }
    }


    boolean isPressed(int keycode) {
        return Gdx.input.isKeyPressed(keycode);
    }


    void checkForKeys() {
        if (isPressed(Input.Keys.LEFT) || isPressed(Input.Keys.A)) {
            camDx -= 0.003 * w;
        }
        if (isPressed(Input.Keys.RIGHT) || isPressed(Input.Keys.D)) {
            camDx += 0.003 * w;
        }
        if (isPressed(Input.Keys.UP) || isPressed(Input.Keys.W)) {
            camDy += 0.003 * w;
        }
        if (isPressed(Input.Keys.DOWN) || isPressed(Input.Keys.S)) {
            camDy -= 0.003 * w;
        }
        if (isPressed(Input.Keys.I)) { // zoom In
            camDZoom -= 0.03;
        }
        if (isPressed(Input.Keys.O)) { // zoom Out
            camDZoom += 0.05;
        }
    }


    void keyDown(int keycode) {
        switch (keycode) {

        }
    }


    public void setChosenModel(int chosenModel) {
        this.chosenModel = chosenModel;
    }


    void scrolled(int amount) {
        if (amount == 1) {
            camDZoom += 0.1f;
        } else if (amount == -1) {
            camDZoom -= 0.1f;
        }
    }
}
