package yio.tro.evolution.behaviors;

import yio.tro.evolution.Button;

/**
 * Created by ivan on 05.08.14.
 */
public abstract class ReactBehavior {

    public abstract void reactAction(Button button);

    public static RbExit rbExit = new RbExit();
    public static RbInfo rbInfo = new RbInfo();
    public static RbMainMenu rbMainMenu = new RbMainMenu();
    public static RbGameSetupMenu rbGameSetupMenu = new RbGameSetupMenu();
    public static RbStartGame rbStartGame = new RbStartGame();
    public static RbInGameMenu rbInGameMenu = new RbInGameMenu();
    public static RbResumeGame rbResumeGame = new RbResumeGame();
    public static RbReset rbReset = new RbReset();
    public static RbShowToolPanel rbShowToolPanel = new RbShowToolPanel();
    public static RbHideToolPanel rbHideToolPanel = new RbHideToolPanel();
    public static RbTogglePausePlay rbTogglePausePlay = new RbTogglePausePlay();
    public static RbNothing rbNothing = new RbNothing();
    public static RbSetTouchModeMove rbSetTouchModeMove = new RbSetTouchModeMove();
    public static RbSetTouchModeAddHerb rbSetTouchModeAddHerb = new RbSetTouchModeAddHerb();
    public static RbSetTouchModeAddAnimal rbSetTouchModeAddAnimal = new RbSetTouchModeAddAnimal();
    public static RbSpeedUp rbSpeedUp = new RbSpeedUp();
    public static RbSlowDown rbSlowDown = new RbSlowDown();
    public static RbCreatePlotMenu rbCreatePlotMenu = new RbCreatePlotMenu();
    public static RbHideAnimalInfo rbHideAnimalInfo = new RbHideAnimalInfo();
    public static RbShowCombinationPanel rbShowCombinationPanel = new RbShowCombinationPanel();
    public static RbHideCombinationPanel rbHideCombinationPanel = new RbHideCombinationPanel();
    public static RbCombinationButton rbCombinationButton = new RbCombinationButton();
    public static RbSwitchDiagram rbSwitchDiagram = new RbSwitchDiagram();
    public static RbExportMatrix rbExportMatrix = new RbExportMatrix();
    public static RbChooseMatrixModel rbChooseMatrixModel = new RbChooseMatrixModel();
    public static RbChooseBasicModel rbChooseBasicModel = new RbChooseBasicModel();
    public static RbShowParams rbShowParams = new RbShowParams();
    public static RbApplyParams rbApplyParams = new RbApplyParams();
    public static RbHideInfoAboutClosedSystems rbHideInfoAboutClosedSystems = new RbHideInfoAboutClosedSystems();
}
