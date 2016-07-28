package yio.tro.evolution.behaviors;

import yio.tro.evolution.Button;
import yio.tro.evolution.GameController;

/**
 * Created by ivan on 01.02.2015.
 */
public class RbSetTouchModeAddHerb extends ReactBehavior{

    @Override
    public void reactAction(Button button) {
        button.menuController.yioGdxGame.setTouchMode(GameController.TOUCH_MODE_ADD_HERB);
    }
}
