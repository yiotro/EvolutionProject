package yio.tro.evolution;

import android.util.Log;
import com.badlogic.gdx.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import yio.tro.evolution.factor_yio.FactorYio;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;
import java.util.StringTokenizer;

public class YioGdxGame extends ApplicationAdapter implements InputProcessor {
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    int w, h;
    public MenuController menuController;
    MenuView menuView;
    public static BitmapFont font;
    public static BitmapFont debugFont;
    public static BitmapFont listFont;
    public static BitmapFont plotFont;
    public static final String FONT_CHARACTERS = "йцукенгшщзхъёфывапролджэячсмитьбюЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<>";
    public static int FONT_SIZE;
    public static final int INDEX_OF_LAST_LEVEL = 50; // with tutorial
    public static final int MAX_HERBS_IN_ONE_PLACE = 2;
    public static final int HERB_DEFAULT_REPRODUCE_POTENTIAL = 3;
    public static boolean MEASURE = false;
    private static GlyphLayout glyphLayout = new GlyphLayout();
    TextureRegion mainBackground, infoBackground, settingsBackground, pauseBackground;
    TextureRegion currentBackground, lastBackground;
    public static float screenRatio;
    public GameSettings gameSettings;
    public GameController gameController;
    public GameView gameView;
    public boolean gamePaused, readyToUnPause, canDecreaseSpeed;
    long timeToUnPause;
    int frameSkipCount;
    FrameBuffer frameBuffer;
    FactorYio blackoutFactor;
    ArrayList<Splat> splats;
    long timeToSpawnNextSplat;
    float splatSize;
    int currentSplatIndex;
    public static final Random random = new Random();
    long lastTimeButtonPressed;
    boolean alreadyShownErrorMessageOnce, showFpsInfo = true;
    int fps, currentFrameCount;
    long timeToUpdateFpsInfo;
    int currentBackgroundIndex;
    boolean backAnimation;
    public static boolean debug, saveDebugResults;
    public int selectedLevelIndex, speedMultiplier;
    boolean loadedResources, showedSplash, ignoreDrag;
    ArrayList<Integer> debugList;
    TextureRegion splash;


    @Override
    public void create() {
        debug = false;
        if (debug) Log.d("yiotro", "Application starting...");
        loadedResources = false;
        showedSplash = false;
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        splash = GameView.loadTextureRegionByName("files/splash.png", true);
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        screenRatio = (float) w / (float) h;
        speedMultiplier = 1;
        canDecreaseSpeed = false;
        debugList = new ArrayList<>();
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            canDecreaseSpeed = true;
        }
        frameBuffer = new FrameBuffer(Pixmap.Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }


    void loadResourcesAndInitEverything() {
        loadedResources = true;
        gameSettings = new GameSettings(this);
        gameSettings.speed = GameSettings.SPEED_NORMAL;
        gameSettings.difficulty = GameSettings.DIFFICULTY_NORMAL;
        FileHandle fontFile = Gdx.files.internal("files/font.otf");
        mainBackground = GameView.loadTextureRegionByName("files/main_menu_background.png", true);
        infoBackground = GameView.loadTextureRegionByName("files/info_background.png", true);
        settingsBackground = GameView.loadTextureRegionByName("files/settings_background.png", true);
        pauseBackground = GameView.loadTextureRegionByName("files/pause_background.png", true);
        blackoutFactor = new FactorYio();
        splats = new ArrayList<Splat>();
        splatSize = 0.15f * Gdx.graphics.getWidth();
        ListIterator iterator = splats.listIterator();
        for (int i = 0; i < 50; i++) {
            iterator.add(new Splat(null, 0, 0));
        }
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        FONT_SIZE = (int) (0.031 * Gdx.graphics.getHeight());
        parameter.characters = FONT_CHARACTERS;

        parameter.size = FONT_SIZE;
        parameter.flip = true;
        font = generator.generateFont(parameter);

        parameter.size = 2 * FONT_SIZE;
        listFont = generator.generateFont(parameter);
        listFont.setColor(Color.BLACK);

        parameter.size = FONT_SIZE;
        parameter.flip = false;
        debugFont = generator.generateFont(parameter);
        debugFont.setColor(1, 0.5f, 0, 1);

        parameter.flip = false;
        parameter.size = (int) (1.5 * FONT_SIZE);
        plotFont = generator.generateFont(parameter);
        plotFont.setColor(Color.BLACK);

        generator.dispose();
        gamePaused = true;
        alreadyShownErrorMessageOnce = false;
        fps = 0;
        selectedLevelIndex = 0;
        timeToUpdateFpsInfo = System.currentTimeMillis() + 1000;
//        decorations = new ArrayList<BackgroundMenuDecoration>();
//        initDecorations();

        Preferences preferences = Gdx.app.getPreferences("main");
        selectedLevelIndex = preferences.getInteger("progress", 0); // 0 - default value
        menuController = new MenuController(this);
        menuView = new MenuView(this);
        gameController = new GameController(this); // must be called after menu controller is created. because of languages manager and other stuff
        gameView = new GameView(this);
        gameView.factorModel.beginDestroying(1, 1);
        currentBackgroundIndex = -1;
        currentBackground = gameView.blackPixel; // call this after game view is created
        beginBackgroundChange(0);

        Gdx.input.setInputProcessor(this);
        Gdx.gl.glClearColor(0, 0, 0, 1);
    }


    public void setBackAnimation(boolean backAnimation) {
        this.backAnimation = backAnimation;
    }


    public void setGamePaused(boolean gamePaused) {
        if (gamePaused) {
            this.gamePaused = true;
        } else {
            unPauseAfterSomeTime();
            beginBackgroundChange(4);
        }
    }


    public void toggleGamePause() {
        gamePaused = !gamePaused;
        menuController.togglePlayPauseButton();
    }


    public void beginBackgroundChange(int index) {
        if (currentBackgroundIndex == index) return;
        currentBackgroundIndex = index;
        lastBackground = currentBackground;
        switch (index) {
            case 0:
                currentBackground = mainBackground;
                break;
            case 1:
                currentBackground = infoBackground;
                break;
            case 2:
                currentBackground = settingsBackground;
                break;
            case 3:
                currentBackground = pauseBackground;
                break;
            case 4:
                currentBackground = gameView.blackPixel;
                break;
        }
        blackoutFactor.setValues(0, 0);
        blackoutFactor.beginSpawning(1, 1.5);
    }


    public void increaseSpeedMultiplier() {
        speedMultiplier *= 2;
        if (speedMultiplier > 64) speedMultiplier = 64;
        else { // speed really changed
            // nothing here for now
        }
    }


    public void decreaseSpeedMultiplier() {
        speedMultiplier /= 2;
        if (speedMultiplier < 1) speedMultiplier = 1;
        else { // speed really changed
            // nothing here for now
        }
    }


    public void move() {
        if (!loadedResources) return;
        blackoutFactor.move();
        checkToUnPause();
        gameView.factorModel.move();
        menuController.move();
        if (gameView.factorModel.get() > 0.99) gameController.cameraMovement();
        if (!gamePaused) {
            gameView.move();
            moveGameController();
        }
        if (!gameView.coversAllScreen()) {
            moveSplats();
        }
        checkToMeasure();
    }


    private void checkToMeasure() {
        if (!MEASURE) return;

        // configure
        if (gamePaused) {
            Button b = menuController.getButtonById(21);
            if (b != null && b.factorModel.get() == 1) {
//                menuController.sliders.get(6).setRunnerValue(0); // temperature = 0
            }
        }

        // start game
        menuController.forceActionForButtonById(25);

        // open tool panel at start
        if (!gamePaused && gameController.dataStorage.getDataList(0).size() < 10) {
            menuController.forceActionForButtonById(31);
            saveDebugResults = true;
        }

        // open graph menu
        menuController.forceActionForButtonById(59);

        // play on graph menu
        if (gamePaused)
            menuController.forceActionForButtonById(52);

//        speedMultiplier = 64;

        if (!gamePaused && gameController.dataStorage.getDataList(0).size() > 1500) {
            if (saveDebugResults) {
                // configure
                float mr = menuController.sliders.get(1).getCurrentRunnerIndex() / 100f;
                mr += 0.02;
                menuController.sliders.get(1).setRunnerValue(mr);

                // save and show results
                debugList.add(getDebugResults());
                say(roundUp(mr, 2) + " | " + debugList);

                saveDebugResults = false;
            }

            // close graph menu
            menuController.forceActionForButtonById(120);

            // open in game menu
            menuController.forceActionForButtonById(30);
        }

        // open game setup menu
        menuController.forceActionForButtonById(43);
    }


    private int getDebugResults() {
        ArrayList<Integer> dataList = gameController.dataStorage.getDataList(0);
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            list.add(dataList.get(dataList.size() - 1 - i));
        }
        return Analyzer.getMedium(list);
    }


    private void checkToUnPause() {
        if (readyToUnPause && System.currentTimeMillis() > timeToUnPause) {
            gamePaused = false;
            readyToUnPause = false;
            gameController.currentTouchCount = 0;
        }
    }


    private void moveGameController() {
        if (speedMultiplier == 1) {
            gameController.move();
        } else {
            for (int k = 0; k < speedMultiplier; k++)
                gameController.move();
        }
    }


    private void moveSplats() {
        if (System.currentTimeMillis() > timeToSpawnNextSplat) {
            timeToSpawnNextSplat = System.currentTimeMillis() + 1000 + random.nextInt(200);
            float sx, sy;
            sx = random.nextFloat() * Gdx.graphics.getWidth();
            sy = random.nextFloat() * Gdx.graphics.getHeight();
            for (int i = 0; i < 3; i++) {
                int c = 0, size = splats.size();
                Splat splat = null;
                while (c < size) {
                    c++;
                    splat = splats.get(currentSplatIndex);
                    currentSplatIndex++;
                    if (currentSplatIndex >= size) currentSplatIndex = 0;
                    if (!splat.isVisible()) break;
                }
                if (splat != null) {
                    float a, p;
                    a = 2f * (float) Math.PI * random.nextFloat();
                    p = 0.01f * splatSize * random.nextFloat();
                    splat.set(sx, sy, 2500);
                    splat.setSpeed(p * (float) Math.cos(a), p * (float) Math.sin(a));
                }
            }
        }
        for (Splat splat : splats) {
            splat.move();
        }
    }


    void renderInternals() {
        currentFrameCount++;
        if (showFpsInfo && System.currentTimeMillis() > timeToUpdateFpsInfo) {
            timeToUpdateFpsInfo = System.currentTimeMillis() + 500;
            fps = currentFrameCount * 2;
            currentFrameCount = 0;
        }
        if (!gameView.coversAllScreen()) {
            Color c = batch.getColor();
            batch.begin();
            if (blackoutFactor.get() < 0.99) {
                batch.setColor(c.r, c.g, c.b, 1);
//                if (backAnimation) {
//                    float f = (float)(1 - 0.2 * blackoutFactor.get());
//                    batch.draw(lastBackground, 0.5f * w - 0.5f * w * f, 0.5f * h - 0.5f * h * f, w * f, h * f);
//                }
//                else
                batch.draw(lastBackground, 0, 0, w, h);
            }
            batch.setColor(c.r, c.g, c.b, blackoutFactor.get());
//            if (backAnimation)
            batch.draw(currentBackground, 0, 0, w, h);
//            else {
//                float f = (float)(0.8 + 0.2 * blackoutFactor.get());
//                batch.draw(currentBackground, 0.5f * w - 0.5f * w * f, 0.5f * h - 0.5f * h * f, w * f, h * f);
//            }
            batch.setColor(c.r, c.g, c.b, 1);
            batch.end();
        }
        gameView.render();
        menuView.render();
        if (showFpsInfo) {
            batch.begin();
            debugFont.draw(batch, "" + fps + ", " + speedMultiplier + "x", 10, Gdx.graphics.getHeight() - 10);
            batch.end();
        }
    }


    public static final void maskingBegin() {
        Gdx.gl.glClearDepthf(1f);
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glDepthFunc(GL20.GL_LESS);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthMask(true);
        Gdx.gl.glColorMask(false, false, false, false);
    }


    public static final void maskingContinue() {
        Gdx.gl.glColorMask(true, true, true, true);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthFunc(GL20.GL_EQUAL);
    }


    public static final void maskingEnd() {
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    }


    @Override
    public void render() {
        try {
            move();
        } catch (Exception exception) {
            if (!alreadyShownErrorMessageOnce) {
                if (debug) Log.d("yiotro", exception.toString());
                exception.printStackTrace();
                alreadyShownErrorMessageOnce = true;
                menuController.createExceptionReport(exception);
            }
        }

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!loadedResources) {
            batch.begin();
            batch.draw(splash, 0, 0, w, h);
            batch.end();
            if (showedSplash) loadResourcesAndInitEverything();
            showedSplash = true;
            return;
        }

        if (gamePaused) {
            renderInternals();
        } else {
            if (Gdx.graphics.getDeltaTime() < 0.025 || frameSkipCount >= 2) {
                frameSkipCount = 0;
                frameBuffer.begin();
                renderInternals();
                frameBuffer.end();
            } else {
                frameSkipCount++;
            }
            batch.begin();
            batch.draw(frameBuffer.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, true);
            batch.end();
        }
    }


    public void setTouchMode(int touchMode) {
        gameController.setTouchMode(touchMode);
    }


    public void unPauseAfterSomeTime() {
        readyToUnPause = true;
        timeToUnPause = System.currentTimeMillis() + 450; // время анимации - около 420мс
    }


    public void startGame() {
        gameController.prepareForNewGame(selectedLevelIndex);
        menuController.createGameOverlay();
        checkToShowInfoAboutClosedSystems();
        gameView.beginSpawnProcess();
        unPauseAfterSomeTime();
    }


    private void checkToShowInfoAboutClosedSystems() {
        if (!gameController.evolutionModel.isModelMatrix()) return;
        Button b = menuController.getButtonById(87132);
        b.factorModel.beginSpawning(3, 1);
        b.setTouchable(true);
    }


    private static double getNormalizedAngle(double a) {
        while (a < 0) a += 2 * Math.PI;
        while (a >= 2 * Math.PI) a -= 2 * Math.PI;
        return a;
    }


    static public float getTextWidth(BitmapFont font, String text) {
        glyphLayout.setText(font, text);
        return glyphLayout.width;
    }


    public static double differenceBetweenAngles(double a, double b) {
        a = getNormalizedAngle(a);
        b = getNormalizedAngle(b);
        double c = b - a;
        if (c < -Math.PI) return 2 * Math.PI - c;
        if (c > Math.PI) return c - 2 * Math.PI;
        return c;
    }


    public static double angle(double x1, double y1, double x2, double y2) {
        if (x1 == x2) {
            if (y2 > y1) return 0.5 * Math.PI;
            if (y2 < y1) return 1.5 * Math.PI;
            return 0;
        }
        if (x2 >= x1) return Math.atan((y2 - y1) / (x2 - x1));
        else return Math.PI + Math.atan((y2 - y1) / (x2 - x1));
    }


    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }


    public static float randomAngle() {
        return 2f * (float) Math.PI * random.nextFloat();
    }


    public static void say(String text) {
        System.out.println(text);
    }


    public static ArrayList<String> decodeStringToArrayList(String string, String delimiters) {
        ArrayList<String> res = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(string, delimiters);
        while (tokenizer.hasMoreTokens()) {
            res.add(tokenizer.nextToken());
        }
        return res;
    }


    public void setSelectedLevelIndex(int selectedLevelIndex) {
        if (selectedLevelIndex >= 0 && selectedLevelIndex <= INDEX_OF_LAST_LEVEL)
            this.selectedLevelIndex = selectedLevelIndex;
    }


    public static double roundUp(double value, int length) {
        double d = Math.pow(10, length);
        value = value * d;
        int i = (int) (value + 0.45);
        return (double) i / d;
    }


    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.BACK) {
            if (!gamePaused) {
                menuController.createInGameMenu();
                setGamePaused(true);
            }
        }
        if (keycode == Input.Keys.ESCAPE) {
            menuController.forceActionForButtonById(1);
            menuController.forceActionForButtonById(10);
            menuController.forceActionForButtonById(30);
            menuController.forceActionForButtonById(41);
            menuController.forceActionForButtonById(120);
            menuController.forceActionForButtonById(24);
        }
        if (keycode == Input.Keys.ENTER) {
            menuController.forceActionForButtonById(42);
            menuController.forceActionForButtonById(3);
            menuController.forceActionForButtonById(25);
            menuController.forceActionForButtonById(31);
            menuController.forceActionForButtonById(51);
            menuController.forceActionForButtonById(121);
        }
        if (keycode == Input.Keys.GRAVE) {
            menuController.forceActionForButtonById(31);
            menuController.forceActionForButtonById(51);
        }
        if (keycode == Input.Keys.SPACE) {
            menuController.forceActionForButtonById(52);
            menuController.forceActionForButtonById(81);
            menuController.forceActionForButtonById(161);
        }
        if (keycode == Input.Keys.TAB) {
            menuController.pressToSwitchModel();
        }
        if (!gamePaused && gameView.factorModel.get() > 0.99) gameController.keyDown(keycode);
        return false;
    }


    public boolean isGamePaused() {
        return gamePaused;
    }


    @Override
    public boolean keyUp(int keycode) {
        return false;
    }


    @Override
    public boolean keyTyped(char character) {
        return false;
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        ignoreDrag = true;
        try {
            if (!gameView.isInMotion() && menuController.touchDown(screenX, Gdx.graphics.getHeight() - screenY, pointer, button)) {
                lastTimeButtonPressed = System.currentTimeMillis();
                return false;
            } else {
                ignoreDrag = false;
            }
            if (gameView.coversAllScreen())
                gameController.touchDown(screenX, Gdx.graphics.getHeight() - screenY, pointer, button);
        } catch (Exception exception) {
            if (!alreadyShownErrorMessageOnce) {
                Log.d("yiotro", exception.toString());
                exception.printStackTrace();
                alreadyShownErrorMessageOnce = true;
                menuController.createExceptionReport(exception);
            }
        }
        return false;
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        try {
            menuController.touchUp(screenX, Gdx.graphics.getHeight() - screenY, pointer, button);
            if (gameView.coversAllScreen() && System.currentTimeMillis() > lastTimeButtonPressed + 300)
                gameController.touchUp(screenX, Gdx.graphics.getHeight() - screenY, pointer, button);
        } catch (Exception exception) {
            if (!alreadyShownErrorMessageOnce) {
                exception.printStackTrace();
                alreadyShownErrorMessageOnce = true;
                menuController.createExceptionReport(exception);
            }
        }
        return false;
    }


    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        menuController.touchDragged(screenX, Gdx.graphics.getHeight() - screenY, pointer);
        if (!ignoreDrag && gameView.coversAllScreen())
            gameController.touchDragged(screenX, Gdx.graphics.getHeight() - screenY, pointer);
        return false;
    }


    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }


    @Override
    public boolean scrolled(int amount) {
        if (gameView.factorModel.get() > 0.1) gameController.scrolled(amount);
        return true;
    }


}
