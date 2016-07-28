package yio.tro.evolution.behaviors;

import yio.tro.evolution.Button;

/**
 * Created by ivan on 05.04.2016.
 */
public class RbShowParams extends ReactBehavior{

    @Override
    public void reactAction(Button button) {
        button.menuController.showParamsPanel();
    }
}
