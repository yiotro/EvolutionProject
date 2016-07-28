package yio.tro.evolution.behaviors;

import yio.tro.evolution.Button;

/**
 * Created by ivan on 06.08.14.
 */
public class RbResumeGame extends ReactBehavior{

    @Override
    public void reactAction(Button button) {
        button.menuController.createGameOverlay();
        button.menuController.yioGdxGame.gameView.beginSpawnProcess();
        button.menuController.yioGdxGame.unPauseAfterSomeTime();
        button.menuController.hidePlots(); // if they are visible
    }
}
