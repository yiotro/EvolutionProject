package yio.tro.evolution;

import com.badlogic.gdx.Gdx;
import yio.tro.evolution.behaviors.ReactBehavior;
import yio.tro.evolution.models.MatrixAnimal;
import yio.tro.evolution.models.EvolutionSubject;
import yio.tro.evolution.models.Herb;
import yio.tro.evolution.models.MatrixModel;
import yio.tro.evolution.plot.Plot;
import yio.tro.evolution.plot.PlotFactory;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.StringTokenizer;

/**
 * Created by ivan on 22.07.14.
 */
public class MenuController {
    public YioGdxGame yioGdxGame;
    ArrayList<Button> buttons;
    ButtonFactory buttonFactory;
    SimpleRectangle biggerBlockPosition;
    ButtonRenderer buttonRenderer;
    LanguagesManager languagesManager;
    ArrayList<Plot> plots;
    public ArrayList<SliderYio> sliders;
    PlotFactory plotFactory;
    boolean arePlotsValid;
    public boolean combinationPlot[];
    public static final int NUMBER_OF_PLOT_LINES = 6;
    long timeToUpdatePlot, decreaseSpeedCountDown;


    public MenuController(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
        buttonFactory = new ButtonFactory(this);
        buttons = new ArrayList<Button>();
        biggerBlockPosition = new SimpleRectangle(0.1 * Gdx.graphics.getWidth(), 0.1 * Gdx.graphics.getHeight(), 0.8 * Gdx.graphics.getWidth(), 0.8 * Gdx.graphics.getHeight());
        buttonRenderer = new ButtonRenderer();
        languagesManager = LanguagesManager.getInstance();
        plots = new ArrayList<>();
        plotFactory = new PlotFactory(yioGdxGame);

        initSliders();

        combinationPlot = new boolean[NUMBER_OF_PLOT_LINES];
        combinationPlot[0] = true;

        createMainMenu();
    }


    private void initSliders() {
        sliders = new ArrayList<SliderYio>();
        for (int i = 0; i < 8; i++) {
            sliders.add(new SliderYio(this));
        }
        sliders.get(0).setValues(1, 0, 1010, true, SliderYio.CONFIGURE_NUMBER); // animals number
        sliders.get(1).setValues(0.04, 0, 101, false, SliderYio.CONFIGURE_FACTOR); // mutation rate
        sliders.get(2).setValues(0.1, 0, 101, false, SliderYio.CONFIGURE_FACTOR); // disease rate
        sliders.get(3).setValues(1, MatrixModel.MIN_GEN_NUMBER, MatrixModel.MAX_GEN_NUMBER, false, SliderYio.CONFIGURE_NUMBER); // gen number
        sliders.get(4).setValues(1, 0, 1, false, SliderYio.CONFIGURE_TUMBLER); // random matrix
        sliders.get(5).setValues(0.51, 0, 301, true, SliderYio.CONFIGURE_FOOD_VALUE); // meat food value
        sliders.get(6).setValues(1, 0, 101, true, SliderYio.CONFIGURE_FACTOR); // temperature
        sliders.get(7).setValues(0.36, 0, 301, false, SliderYio.CONFIGURE_FOOD_VALUE); // herb food value
    }


    public void move() {
        for (SliderYio sliderYio : sliders) sliderYio.move();
        for (Button button : buttons) {
            button.move();
        }
        for (int i = buttons.size() - 1; i >= 0; i--) {
            if (buttons.get(i).checkToPerformAction()) break;
        }
        movePlots();
    }


    private void movePlots() {
        if (!arePlotsValid) return;

        arePlotsValid = false;
        for (Plot plot : plots) {
            plot.move();
            if (plot.isValid()) arePlotsValid = true;
        }

        if (    arePlotsValid &&
                !yioGdxGame.gamePaused &&
                System.currentTimeMillis() > timeToUpdatePlot &&
                plots.get(0).getSpawnFactor().get() == 1) {

            updateCombinationPlot();
            timeToUpdatePlot = System.currentTimeMillis() + 50;

            checkToDecreaseSpeed();
        }
    }


    private void checkToDecreaseSpeed() {
        if (!yioGdxGame.canDecreaseSpeed) return;

        if (yioGdxGame.fps < 20) {
            if (decreaseSpeedCountDown == 0) {
                yioGdxGame.decreaseSpeedMultiplier();
                decreaseSpeedCountDown = 40;
            } else decreaseSpeedCountDown--;
        }
    }


    public void addButtonToArray(Button button) {
        // considered that menu block is not in array at this moment
        ListIterator iterator = buttons.listIterator();
        while (iterator.hasNext()) {
            iterator.next();
        }
        iterator.add(button);
    }


    public void removeButtonFromArray(Button button) {
        ListIterator iterator = buttons.listIterator();
        Button currentBlock;
        while (iterator.hasNext()) {
            currentBlock = (Button) iterator.next();
            if (currentBlock == button) {
                iterator.remove();
                return;
            }
        }
    }


    public Button getButtonById(int id) { // can return null
        for (Button button : buttons) {
            if (button.id == id) return button;
        }
        return null;
    }


    void loadButtonOnce(Button button, String fileName) {
        if (button.notRendered()) {
            button.loadTexture(fileName);
        }
    }


    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        for (SliderYio sliderYio : sliders)
            if (sliderYio.touchDown(screenX, screenY)) return true;

        for (Button buttonLighty : buttons) {
            if (buttonLighty.isTouchable()) {
                if (buttonLighty.checkTouch(screenX, screenY, pointer, button)) return true;
            }
        }

        if (arePlotsValid) {
            for (Plot plot : plots) {
                plot.touchDown(screenX, screenY);
            }
        }

        return false;
    }


    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        for (SliderYio sliderYio : sliders)
            if (sliderYio.touchUp(screenX, screenY)) return true;

        if (arePlotsValid) {
            for (Plot plot : plots) {
                plot.touchUp(screenX, screenY);
            }
        }

        return false;
    }


    public void touchDragged(int screenX, int screenY, int pointer) {
        for (SliderYio slider : sliders) {
            slider.touchDrag(screenX, screenY);
        }
        if (arePlotsValid) {
            for (Plot plot : plots) {
                plot.touchDragged(screenX, screenY);
            }
        }
    }


    void beginMenuCreation() {
        for (SliderYio sliderYio : sliders) sliderYio.destroy();
        for (Button button : buttons) {
            button.destroy();
        }
        if (yioGdxGame.gameView != null) yioGdxGame.gameView.beginDestroyProcess();
        yioGdxGame.backAnimation = false;
    }


    void endMenuCreation() {

    }


    ArrayList<String> getArrayListFromString(String src) {
        ArrayList<String> list = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(src, "#");
        while (tokenizer.hasMoreTokens()) {
            list.add(tokenizer.nextToken());
        }
        return list;
    }


    public void forceActionForButtonById(int id) {
        Button b = getButtonById(id);
        if (b == null) return;
        if (b.factorModel.get() != 1) return;
        if (b.isCurrentlyTouched()) return;
        b.forcePerformAction(true);
    }


    SimpleRectangle generateRectangle(double x, double y, double width, double height) {
        return new SimpleRectangle(x * Gdx.graphics.getWidth(), y * Gdx.graphics.getHeight(), width * Gdx.graphics.getWidth(), height * Gdx.graphics.getHeight());
    }


    SimpleRectangle generateSquare(double x, double y, double size) {
        return generateRectangle(x, y, size, size * YioGdxGame.screenRatio);
    }


    public void createMainMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(0);

        Button exitButton = buttonFactory.getButton(generateSquare(0.91, 0.83, 0.07), 1, null);
        loadButtonOnce(exitButton, "files/interface/shut_down.png");
        exitButton.setShadow(true);
        exitButton.setAnimType(Button.ANIM_UP);
        exitButton.setReactBehavior(ReactBehavior.rbExit);
        exitButton.disableTouchAnimation();

        Button infoButton = buttonFactory.getButton(generateSquare(0.02, 0.83, 0.07), 2, null);
        loadButtonOnce(infoButton, "files/interface/info_icon.png");
        infoButton.setShadow(true);
        infoButton.setAnimType(Button.ANIM_UP);
        infoButton.setReactBehavior(ReactBehavior.rbInfo);
        infoButton.disableTouchAnimation();

        Button playButton = buttonFactory.getButton(generateSquare(0.4, 0.32, 0.2), 3, null);
        loadButtonOnce(playButton, "files/interface/play_button.png");
        playButton.setReactBehavior(ReactBehavior.rbGameSetupMenu);
        playButton.disableTouchAnimation();

        endMenuCreation();
    }


    public void createInfoMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(1);

        spawnBackButton(10, ReactBehavior.rbMainMenu);

//        Button resetButton = buttonFactory.getButton(generateRectangle(0.73, 0.9, 0.25, 0.07), 12, languagesManager.getString("menu_reset"));
//        resetButton.setReactBehavior(ReactBehavior.rbReset);
//        resetButton.setAnimType(Button.ANIM_UP);

        Button infoPanel = buttonFactory.getButton(generateRectangle(0.02, 0.05, 0.96, 0.8), 11, null);
        if (infoPanel.notRendered()) {
            infoPanel.addManyLines(getArrayListFromString(languagesManager.getString("info_array")));
            buttonRenderer.renderButton(infoPanel);
        }
        infoPanel.setTouchable(false);
        infoPanel.setAnimType(Button.ANIM_FROM_CENTER);

        endMenuCreation();
    }


    private void destroyAllModelSpecificSliders() {
        int[] ids = new int[]{3, 4, 5, 7, 2};
        for (int i = 0; i < ids.length; i++) {
            sliders.get(ids[i]).destroy();
        }
//        sliders.get(3).destroy();
//        sliders.get(4).destroy();
//        sliders.get(5).destroy();
//        sliders.get(7).destroy();
//        sliders.get(2).destroy();
    }


    private void makeModelLabel(int id, String label) {
        Button labelButton = getButtonById(id);
        labelButton.cleatText();
        labelButton.setTextLine(label);
        labelButton.addTextLine(" ");
        labelButton.addTextLine(" ");
        buttonRenderer.renderButton(labelButton);
        labelButton.factorModel.beginSpawning(3, 2);
    }


    private void makeGlobalLabel(Button labelButton, String label) {
        labelButton.setTextLine(label);
        labelButton.addTextLine(" ");
        labelButton.addTextLine(" ");
        buttonRenderer.renderButton(labelButton);
    }


    public void chooseMatrixModel() {
        destroyAllModelSpecificSliders();
        sliders.get(3).appear(); // gen number
        sliders.get(4).appear(); // random matrix

        makeModelLabel(26, "Matrix size");
        makeModelLabel(27, "Random matrix");
        getButtonById(23).destroy();

        getButtonById(28).setKeepSelection(true);
        getButtonById(29).setKeepSelection(false);
    }


    public void chooseBasicModel() {
        destroyAllModelSpecificSliders();
        sliders.get(5).appear(); // meat food
        sliders.get(7).appear(); // herb food
        sliders.get(2).appear(); // disease rate

        makeModelLabel(26, "Meat food value");
        makeModelLabel(27, "Herb food value");
        makeModelLabel(23, "Disease rate");

        getButtonById(28).setKeepSelection(false);
        getButtonById(29).setKeepSelection(true);
    }


    public void createGameSetupMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(2);

        sliders.get(0).appear();
        sliders.get(0).setPos(0.05, 0.6, 0.4, 0);

        sliders.get(1).appear();
        sliders.get(1).setPos(0.05, 0.35, 0.4, 0);

        sliders.get(2).appear();
        sliders.get(2).setPos(0.55, 0.1, 0.4, 0);

        sliders.get(6).appear();
        sliders.get(6).setPos(0.05, 0.1, 0.4, 0); // temperature

        sliders.get(3).setPos(0.55, 0.6, 0.4, 0); // gen number
        sliders.get(4).setPos(0.55, 0.35, 0.4, 0); // random matrix
        sliders.get(5).setPos(0.55, 0.6, 0.4, 0); // meat food
        sliders.get(7).setPos(0.55, 0.35, 0.4, 0); // herb food

        Button animalNumberLabel = buttonFactory.getButton(generateRectangle(0.01, 0.55, 0.48, 0.2), 21, null);
        makeGlobalLabel(animalNumberLabel, "All animals");
        animalNumberLabel.setTouchable(false);
        animalNumberLabel.setAnimType(Button.ANIM_DEFAULT);
        animalNumberLabel.factorModel.beginSpawning(3, 2);

        Button mutationRateLabel = buttonFactory.getButton(generateRectangle(0.01, 0.3, 0.48, 0.2), 22, null);
        makeGlobalLabel(mutationRateLabel, "Mutation rate");
        mutationRateLabel.setTouchable(false);
        mutationRateLabel.setAnimType(Button.ANIM_DEFAULT);
        mutationRateLabel.factorModel.beginSpawning(3, 2);

        Button diseaseRateLabel = buttonFactory.getButton(generateRectangle(0.51, 0.05, 0.48, 0.2), 23, null);
        diseaseRateLabel.setTouchable(false);
        diseaseRateLabel.setAnimType(Button.ANIM_DEFAULT);
        diseaseRateLabel.factorModel.beginSpawning(3, 2);

        Button modelLabelOne = buttonFactory.getButton(generateRectangle(0.51, 0.55, 0.48, 0.2), 26, null);
        modelLabelOne.setTouchable(false);
        modelLabelOne.setAnimType(Button.ANIM_DEFAULT);
        modelLabelOne.factorModel.beginSpawning(3, 2);

        Button modelLabelTwo = buttonFactory.getButton(generateRectangle(0.51, 0.3, 0.48, 0.2), 27, null);
        modelLabelTwo.setTouchable(false);
        modelLabelTwo.setAnimType(Button.ANIM_DEFAULT);
        modelLabelTwo.factorModel.beginSpawning(3, 2);

        Button temperatureLabel = buttonFactory.getButton(generateRectangle(0.01, 0.05, 0.48, 0.2), 310, null);
        makeGlobalLabel(temperatureLabel, "Temperature amplitude");
        temperatureLabel.setTouchable(false);
        temperatureLabel.setAnimType(Button.ANIM_DEFAULT);
        temperatureLabel.factorModel.beginSpawning(3, 2);

        Button chooseBase = buttonFactory.getButton(generateRectangle(0.5 - 0.3, 0.78, 0.3 * 2, 0.07), 20, " ");
        chooseBase.setTouchable(false);
        chooseBase.setAnimType(Button.ANIM_UP);

        Button chooseModelMatrix = buttonFactory.getButton(generateRectangle(0.5 - 0.3, 0.78, 0.3, 0.07), 28, "Matrix model");
        chooseModelMatrix.setReactBehavior(ReactBehavior.rbChooseMatrixModel);
        chooseModelMatrix.setShadow(false);
        chooseModelMatrix.setAnimType(Button.ANIM_UP);

        Button chooseModelBasic = buttonFactory.getButton(generateRectangle(0.5, 0.78, 0.3, 0.07), 29, "Basic model");
        chooseModelBasic.setReactBehavior(ReactBehavior.rbChooseBasicModel);
        chooseModelBasic.setShadow(false);
        chooseModelBasic.setAnimType(Button.ANIM_UP);

        spawnBackButton(24, ReactBehavior.rbMainMenu);

        Button startButton = buttonFactory.getButton(generateRectangle(0.73, 0.9, 0.25, 0.07), 25, languagesManager.getString("game_settings_start"));
        startButton.setReactBehavior(ReactBehavior.rbStartGame);
        startButton.setAnimType(Button.ANIM_UP);

        switch (yioGdxGame.gameController.chosenModel) {
            case GameController.MODEL_BASIC:
                chooseBasicModel();
                break;
            case GameController.MODEL_MATRIX:
                chooseMatrixModel();
                break;
        }

        endMenuCreation();
    }


    public void pressToSwitchModel() {
        switch (yioGdxGame.gameController.chosenModel) {
            case GameController.MODEL_BASIC:
                forceActionForButtonById(28);
                break;
            case GameController.MODEL_MATRIX:
                forceActionForButtonById(29);
                break;
        }
    }


    public void createGameOverlay() {
        beginMenuCreation();

        Button inGameMenuButton = buttonFactory.getButton(generateSquare(1 - 0.1 / YioGdxGame.screenRatio, 0.9, 0.1 / YioGdxGame.screenRatio), 30, null);
        loadButtonOnce(inGameMenuButton, "files/interface/menu_icon.png");
        inGameMenuButton.setReactBehavior(ReactBehavior.rbInGameMenu);
        inGameMenuButton.setAnimType(Button.ANIM_UP);
        inGameMenuButton.rectangularMask = true;

        Button showToolPanelButton = buttonFactory.getButton(generateSquare(0, 0.9, 0.1 / YioGdxGame.screenRatio), 31, null);
        loadButtonOnce(showToolPanelButton, "files/interface/icon_show_tool_panel.png");
        showToolPanelButton.setReactBehavior(ReactBehavior.rbShowToolPanel);
        showToolPanelButton.setAnimType(Button.ANIM_UP);
        showToolPanelButton.rectangularMask = true;

        endMenuCreation();
    }


    public void togglePlayPauseButton() {
        if (yioGdxGame.gamePaused) getButtonById(52).loadTexture("files/interface/black_play_button.png");
        else getButtonById(52).loadTexture("files/interface/black_pause_button.png");

        if (arePlotsValid && !yioGdxGame.gamePaused) yioGdxGame.speedMultiplier = 64;
    }


    public void showToolPanel() {
        getButtonById(31).destroy();
        hideParamsPanel();

        Button panel = buttonFactory.getButton(generateRectangle(0, 0.9, 1, 0.1), 50, " ");
        panel.setTouchable(false);
        panel.setAnimType(Button.ANIM_UP);

        Button hideToolPanelButton = buttonFactory.getButton(generateSquare(0, 0.9, 0.1 / YioGdxGame.screenRatio), 51, null);
        loadButtonOnce(hideToolPanelButton, "files/interface/icon_hide_tool_panel.png");
        hideToolPanelButton.setReactBehavior(ReactBehavior.rbHideToolPanel);
        hideToolPanelButton.setAnimType(Button.ANIM_UP);
        hideToolPanelButton.rectangularMask = true;

        int count = 1;

        Button slowDownButton = buttonFactory.getButton(generateSquare(count * 0.1 / YioGdxGame.screenRatio, 0.9, 0.1 / YioGdxGame.screenRatio), 57, null);
        loadButtonOnce(slowDownButton, "files/interface/slow_down.png");
        slowDownButton.setReactBehavior(ReactBehavior.rbSlowDown);
        slowDownButton.setAnimType(Button.ANIM_UP);
        slowDownButton.rectangularMask = true;
        count++;

        Button playPauseButton = buttonFactory.getButton(null, 52, null);
        playPauseButton.setPosition(generateSquare(count * 0.1 / YioGdxGame.screenRatio, 0.9, 0.1 / YioGdxGame.screenRatio));
        togglePlayPauseButton();
        playPauseButton.setReactBehavior(ReactBehavior.rbTogglePausePlay);
        playPauseButton.setAnimType(Button.ANIM_UP);
        playPauseButton.rectangularMask = true;
        count++;

        Button speedUpButton = buttonFactory.getButton(generateSquare(count * 0.1 / YioGdxGame.screenRatio, 0.9, 0.1 / YioGdxGame.screenRatio), 58, null);
        loadButtonOnce(speedUpButton, "files/interface/speed_up.png");
        speedUpButton.setReactBehavior(ReactBehavior.rbSpeedUp);
        speedUpButton.setAnimType(Button.ANIM_UP);
        speedUpButton.rectangularMask = true;
        count++;

        Button moveModeButton = buttonFactory.getButton(generateSquare(count * 0.1 / YioGdxGame.screenRatio, 0.9, 0.1 / YioGdxGame.screenRatio), 56, null);
        loadButtonOnce(moveModeButton, "files/interface/icon_move.png");
        moveModeButton.setReactBehavior(ReactBehavior.rbSetTouchModeMove);
        moveModeButton.setAnimType(Button.ANIM_UP);
        moveModeButton.rectangularMask = true;
        count++;

        Button addCarnivoresButton = buttonFactory.getButton(generateSquare(count * 0.1 / YioGdxGame.screenRatio, 0.9, 0.1 / YioGdxGame.screenRatio), 53, null);
        loadButtonOnce(addCarnivoresButton, "files/interface/add_animal.png");
        addCarnivoresButton.setReactBehavior(ReactBehavior.rbSetTouchModeAddAnimal);
        addCarnivoresButton.setAnimType(Button.ANIM_UP);
        addCarnivoresButton.enableTouchAnimation();
        addCarnivoresButton.rectangularMask = true;
        count++;

        Button paramsButton = buttonFactory.getButton(generateSquare(count * 0.1 / YioGdxGame.screenRatio, 0.9, 0.1 / YioGdxGame.screenRatio), 54, null);
        loadButtonOnce(paramsButton, "files/interface/icon_params.png");
        paramsButton.setReactBehavior(ReactBehavior.rbShowParams);
        paramsButton.setAnimType(Button.ANIM_UP);
        paramsButton.rectangularMask = true;
        count++;

        Button graphButton = buttonFactory.getButton(generateSquare(count * 0.1 / YioGdxGame.screenRatio, 0.9, 0.1 / YioGdxGame.screenRatio), 59, null);
        loadButtonOnce(graphButton, "files/interface/graph.png");
        graphButton.setReactBehavior(ReactBehavior.rbCreatePlotMenu);
        graphButton.setAnimType(Button.ANIM_UP);
        graphButton.rectangularMask = true;
        count++;

        Button diagramButton = buttonFactory.getButton(generateSquare(count * 0.1 / YioGdxGame.screenRatio, 0.9, 0.1 / YioGdxGame.screenRatio), 55, null);
        loadButtonOnce(diagramButton, "files/interface/diagram.png");
        diagramButton.setReactBehavior(ReactBehavior.rbSwitchDiagram);
        diagramButton.setAnimType(Button.ANIM_UP);
        diagramButton.rectangularMask = true;
        count++;

        if (yioGdxGame.speedMultiplier >= 8) {
            for (int i = 50; i <= 59; i++) {
                Button b = getButtonById(i);
                if (b != null) b.factorModel.setValues(1, 0);
            }
        }

        // inGameMenuButton must be rendered on top of this panel
        Button igm = getButtonById(30);
        removeButtonFromArray(igm);
        addButtonToArray(igm);
    }


    public void showParamsPanel() {
        if (!yioGdxGame.gameController.evolutionModel.isModelBasic()) return;
        hideToolPanel();

        Button base = buttonFactory.getButton(generateRectangle(0.25, 0.15, 0.5, 0.7), 160, null);
        if (base.notRendered()) {
            base.addTextLine("Meat food value");
            base.addTextLine(" "); base.addTextLine(" ");
            base.addTextLine("Herb food value");
            base.addTextLine(" "); base.addTextLine(" ");
            base.addTextLine("Temperature amplitude");
            base.addTextLine(" "); base.addTextLine(" ");
            base.addTextLine("Disease rate");
            base.addTextLine(" "); base.addTextLine(" ");
            base.addTextLine(" ");
            buttonRenderer.renderButton(base);
        }
        base.setTouchable(false);
        base.setAnimType(Button.ANIM_FROM_CENTER);
        base.factorModel.beginSpawning(3, 2);

        sliders.get(5).setPos(0.3, 0.77, 0.4, 0);
        sliders.get(5).setVerticalTouchOffset(0.06f * yioGdxGame.h);
        sliders.get(5).appear();

        sliders.get(6).setPos(0.3, 0.45, 0.4, 0);
        sliders.get(6).setVerticalTouchOffset(0.06f * yioGdxGame.h);
        sliders.get(6).appear();

        sliders.get(7).setPos(0.3, 0.61, 0.4, 0);
        sliders.get(7).setVerticalTouchOffset(0.06f * yioGdxGame.h);
        sliders.get(7).appear();

        sliders.get(2).setPos(0.3, 0.29, 0.4, 0);
        sliders.get(2).setVerticalTouchOffset(0.06f * yioGdxGame.h);
        sliders.get(2).appear();

        Button applyParamsButton = buttonFactory.getButton(generateRectangle(0.6, 0.15, 0.15, 0.05), 161, "Apply");
        applyParamsButton.setReactBehavior(ReactBehavior.rbApplyParams);
        applyParamsButton.setAnimType(Button.ANIM_FROM_CENTER);
        applyParamsButton.factorModel.beginSpawning(3, 2);
    }


    public void hideParamsPanel() {
        for (int i = 160; i <= 161; i++) {
            Button b = getButtonById(i);
            if (b == null) return;
            b.destroy();
        }

        int[] ids = new int[]{2, 5, 6, 7};
        for (int i = 0; i < ids.length; i++) {
            sliders.get(ids[i]).setVerticalTouchOffset(0.1f * yioGdxGame.h);
            sliders.get(ids[i]).destroy();
        }
    }


    public void hideToolPanel() {
        for (int i = 50; i <= 59; i++) {
            Button b = getButtonById(i);
            if (b != null) b.destroy();
        }

        Button showToolPanelButton = buttonFactory.getButton(generateSquare(0, 0.9, 0.1 / YioGdxGame.screenRatio), 31, null);
        loadButtonOnce(showToolPanelButton, "files/interface/icon_show_tool_panel.png");
        showToolPanelButton.setReactBehavior(ReactBehavior.rbShowToolPanel);
        showToolPanelButton.setAnimType(Button.ANIM_UP);
        showToolPanelButton.rectangularMask = true;
    }


    public void showPlots() {
        arePlotsValid = true;
        plots.clear();
        double tx = 0.1 * Gdx.graphics.getWidth();
        DataStorage dataStorage = yioGdxGame.gameController.dataStorage;
        for (int i = 0; i < NUMBER_OF_PLOT_LINES + 1; i++) {
            Plot plot = plotFactory.getPlot(this, dataStorage, i);
            plot.setX(tx);
            tx += Plot.PLOT_WIDTH_PLUS_OFFSET;
            plots.add(plot);
        }

        updateCombinationPlot();
    }


    public void updateCombinationPlot() {
        plotFactory.renderPlot(plots.get(0), yioGdxGame.gameController.dataStorage, combinationPlot);
    }


    public Plot getNearestToCenterPlot(double shift) {
        if (plots.size() == 0) return null;
        Plot nearestPlot = plots.get(0);
        double seekPos = 0.5 * Gdx.graphics.getWidth();
        if (Math.abs(shift) < 0.45 * Gdx.graphics.getWidth()) {
            if (shift < -0.07 * Gdx.graphics.getWidth()) seekPos += Plot.PLOT_WIDTH_PLUS_OFFSET;
            if (shift > 0.07 * Gdx.graphics.getWidth()) seekPos -= Plot.PLOT_WIDTH_PLUS_OFFSET;
        }
        double minDistance = Math.abs(nearestPlot.getPos().getCenterX() - seekPos);
        double tempDistance;
        for (Plot plot : plots) {
            tempDistance = Math.abs(plot.getPos().getCenterX() - seekPos);
            if (tempDistance < minDistance) {
                minDistance = tempDistance;
                nearestPlot = plot;
            }
        }
        return nearestPlot;
    }


    public void hidePlots() {
        if (!arePlotsValid) return;

        yioGdxGame.speedMultiplier = 1;
        for (Plot plot : plots) {
            plot.hide();
        }
    }


    public void showInfoAboutClosedSystems(ArrayList<DebugClosedSystem> closedSystems) {
        Button infoPanel;
        infoPanel = buttonFactory.getButton(generateRectangle(0, 0, 0.25, 0.2), 87132, null);
        infoPanel.cleatText();
        infoPanel.addManyLines(getStringsOfClosedSystems(closedSystems));

        buttonRenderer.renderButton(infoPanel);
        infoPanel.setReactBehavior(ReactBehavior.rbHideInfoAboutClosedSystems);
        infoPanel.setAnimType(Button.ANIM_DOWN);
    }


    private ArrayList<String> getStringsOfClosedSystems(ArrayList<DebugClosedSystem> closedSystems) {
        ArrayList<String> result = new ArrayList<>();

        for (int i = 0; i < closedSystems.size() && i < 6; i++) {
            DebugClosedSystem closedSystem = closedSystems.get(i);
            result.add(closedSystem.getDebugCount() + ": " + closedSystem.one.genList + " - " + closedSystem.two.genList);
        }

        for (int i = result.size(); i < 6; i++) {
            result.add(" ");
        }

        return result;
    }


    public void hideInfoAboutClosedSystems() {
        Button b = getButtonById(87132);
        if (b != null) b.destroy();
    }


    public void showInfoAboutSelectedSubject() {
        EvolutionSubject subject = yioGdxGame.gameController.evolutionModel.getSelectedSubject();
        if (subject == null) return;
        Button infoPanel;
        infoPanel = buttonFactory.getButton(generateRectangle(0, 0, 0.15, 0.15), 37126, null);
        infoPanel.cleatText();
        infoPanel.addTextLine("< " + subject.name + " >");
        infoPanel.addTextLine("Code: " + subject.getGeneticCode());
        infoPanel.addTextLine("Mass: " + YioGdxGame.roundUp(subject.mass, 2));
        infoPanel.addTextLine("Other: " + subject.getOtherInfo());
//        if (subject instanceof MatrixAnimal) {
//            for (int i = 0; i < 1; i++) infoPanel.addTextLine(" ");
//        }
        buttonRenderer.renderButton(infoPanel);
        infoPanel.setReactBehavior(ReactBehavior.rbHideAnimalInfo);
        infoPanel.setAnimType(Button.ANIM_DOWN);
    }


    public void hideInfoAboutSelectedSubject() {
        Button b = getButtonById(37126);
        if (b != null) b.destroy();
    }


    private void spawnBackButton(int id, ReactBehavior reactBehavior) {
        Button backButton = buttonFactory.getButton(generateRectangle(0.02, 0.9, 0.15, 0.07), id, null);
        loadButtonOnce(backButton, "files/interface/back_icon.png");
        backButton.setShadow(true);
        backButton.setAnimType(Button.ANIM_UP);
        backButton.setReactBehavior(reactBehavior);
    }


    public void createPlotMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(1);

        spawnBackButton(120, ReactBehavior.rbResumeGame);

        Button ppButton = getButtonById(52);
        if (ppButton != null) {
            ppButton.setPosition(generateSquare(0.63, 0.9, 0.07 / YioGdxGame.screenRatio));
            ppButton.factorModel.setValues(0, 0.001);
            ppButton.factorModel.beginSpawning(1, 1.5);
            ppButton.setTouchable(true);
        }

        Button combinationButton = buttonFactory.getButton(generateRectangle(0.73, 0.9, 0.25, 0.07), 121, languagesManager.getString("combination"));
        combinationButton.setReactBehavior(ReactBehavior.rbShowCombinationPanel);
        combinationButton.setAnimType(Button.ANIM_UP);

        Button matrixButton = buttonFactory.getButton(generateSquare(0.68, 0.9, 0.07 / YioGdxGame.screenRatio), 122, null);
        loadButtonOnce(matrixButton, "files/interface/matrix.png");
        matrixButton.setReactBehavior(ReactBehavior.rbExportMatrix);
        matrixButton.setAnimType(Button.ANIM_UP);
        matrixButton.setShadow(true);

        endMenuCreation();
    }


    private String getCombinationButtonString(int id) {
        return yioGdxGame.gameController.dataStorage.getPlotNameByIndex(id - 82);
    }


    private String getWrappedCombinationString(int id) {
        String str = getCombinationButtonString(id);
        boolean isOn = combinationPlot[id - 82];
        if (isOn) return "[" + str + "]";
        else return str;
    }


    public void switchCombination(Button button) {
        int combIndex = button.id - 82;
        combinationPlot[combIndex] = !combinationPlot[combIndex];
        button.setTextLine(getWrappedCombinationString(button.id));
        buttonRenderer.renderButton(button);
        updateCombinationPlot();
    }


    public void showCombinationPanel() {
        Button basePanel = buttonFactory.getButton(generateRectangle(0, 0, 1, 0.22), 80, " ");
        basePanel.setTouchable(false);
        basePanel.rectangularMask = true;

        Button closeButton = buttonFactory.getButton(generateSquare(0.95, 0.22 - 0.05 * YioGdxGame.screenRatio, 0.05), 81, null);
        loadButtonOnce(closeButton, "files/interface/cancel_icon.png");
        closeButton.setReactBehavior(ReactBehavior.rbHideCombinationPanel);

        double xPos = 0;
        double yPos = 0;
        for (int i = 82; i <= 87; i++) {
            if (i == 85) {
                xPos = 0.5;
                yPos = 0;
            }
            Button combButton = buttonFactory.getButton(generateRectangle(xPos, yPos, 0.4, 0.07), i, null);
            yPos += 0.07;
            combButton.setTextLine(getWrappedCombinationString(i));
            buttonRenderer.renderButton(combButton);
            combButton.setReactBehavior(ReactBehavior.rbCombinationButton);
        }

        for (int i = 80; i <= 87; i++) {
            Button button = getButtonById(i);
            button.setAnimType(Button.ANIM_DOWN);
        }
    }


    public void hideCombinationPanel() {
        for (int i = 80; i <= 87; i++) {
            Button button = getButtonById(i);
            button.destroy();
        }
    }


    public void createInGameMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(3);

        Button basePanel = buttonFactory.getButton(generateRectangle(0.3, 0.3, 0.4, 0.4), 40, null);
        basePanel.setTouchable(false);
        basePanel.onlyShadow = true;
        basePanel.setAnimType(Button.ANIM_DEFAULT);

        Button mainMenuButton = buttonFactory.getButton(generateRectangle(0.3, 0.3, 0.4, 0.1), 41, languagesManager.getString("in_game_menu_main_menu"));
        mainMenuButton.setReactBehavior(ReactBehavior.rbMainMenu);
        mainMenuButton.setShadow(false);
        mainMenuButton.setAnimType(Button.ANIM_FROM_CENTER);

        Button resumeButton = buttonFactory.getButton(generateRectangle(0.3, 0.6, 0.4, 0.1), 42, languagesManager.getString("in_game_menu_resume"));
        resumeButton.setReactBehavior(ReactBehavior.rbResumeGame);
        resumeButton.setShadow(false);
        resumeButton.setAnimType(Button.ANIM_FROM_CENTER);

        Button restartButton = buttonFactory.getButton(generateRectangle(0.3, 0.5, 0.4, 0.1), 44, languagesManager.getString("in_game_menu_restart"));
        restartButton.setReactBehavior(ReactBehavior.rbStartGame);
        restartButton.setShadow(false);
        restartButton.setAnimType(Button.ANIM_FROM_CENTER);

        Button chooseLevelButton = buttonFactory.getButton(generateRectangle(0.3, 0.4, 0.4, 0.1), 43, languagesManager.getString("in_game_menu_new_game"));
        chooseLevelButton.setReactBehavior(ReactBehavior.rbGameSetupMenu);
        chooseLevelButton.setShadow(false);
        chooseLevelButton.setAnimType(Button.ANIM_FROM_CENTER);

        endMenuCreation();
    }


    public void createExceptionReport(Exception exception) {
        beginMenuCreation();
        yioGdxGame.setGamePaused(true);

        ArrayList<String> text = new ArrayList<String>();
        text.add("Error : " + exception.toString());
        String temp;
        int start, end;
        boolean go;
        for (int i = 0; i < exception.getStackTrace().length; i++) {
            temp = exception.getStackTrace()[i].toString();
            start = 0;
            go = true;
            while (go) {
                end = start + 40;
                if (end > temp.length() - 1) {
                    go = false;
                    end = temp.length() - 1;
                }
                text.add(temp.substring(start, end));
                start = end + 1;
            }
        }
        Button textPanel = buttonFactory.getButton(generateRectangle(0.1, 0.2, 0.8, 0.7), 6731267, null);
        if (textPanel.notRendered()) {
            textPanel.addManyLines(text);
            for (int i = 0; i < 10; i++) textPanel.addTextLine(" ");
            buttonRenderer.renderButton(textPanel);
        }
        textPanel.setTouchable(false);

        Button okButton = buttonFactory.getButton(generateRectangle(0.1, 0.1, 0.8, 0.1), 73612321, "Ok");
        okButton.setReactBehavior(ReactBehavior.rbMainMenu);

        endMenuCreation();
    }
}
