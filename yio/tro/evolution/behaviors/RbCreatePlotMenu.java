package yio.tro.evolution.behaviors;

import yio.tro.evolution.Button;

/**
 * Created by ivan on 15.10.2015.
 */
public class RbCreatePlotMenu extends ReactBehavior{

    @Override
    public void reactAction(Button button) {
        button.menuController.yioGdxGame.setGamePaused(true);
        button.menuController.createPlotMenu();
        button.menuController.showPlots();
    }
}
