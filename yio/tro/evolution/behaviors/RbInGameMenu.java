package yio.tro.evolution.behaviors;

import yio.tro.evolution.Button;

/**
 * Created by ivan on 06.08.14.
 */
public class RbInGameMenu extends ReactBehavior{

    @Override
    public void reactAction(Button button) {
        button.menuController.createInGameMenu();
        button.menuController.yioGdxGame.setGamePaused(true);
    }
}
