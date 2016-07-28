package yio.tro.evolution.behaviors;

import yio.tro.evolution.Button;

/**
 * Created by ivan on 05.08.14.
 */
public class RbInfo extends ReactBehavior{

    @Override
    public void reactAction(Button button) {
        button.menuController.createInfoMenu();
    }
}
