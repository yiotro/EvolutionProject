package yio.tro.evolution.behaviors;

import yio.tro.evolution.Button;

/**
 * Created by ivan on 31.01.2015.
 */
public class RbHideToolPanel extends ReactBehavior{

    @Override
    public void reactAction(Button button) {
        button.menuController.hideToolPanel();
    }
}
