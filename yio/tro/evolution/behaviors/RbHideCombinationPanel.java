package yio.tro.evolution.behaviors;

import yio.tro.evolution.Button;

/**
 * Created by ivan on 30.11.2015.
 */
public class RbHideCombinationPanel extends ReactBehavior{

    @Override
    public void reactAction(Button button) {
        button.menuController.hideCombinationPanel();
    }
}