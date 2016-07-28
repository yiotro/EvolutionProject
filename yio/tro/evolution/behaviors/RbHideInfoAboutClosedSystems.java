package yio.tro.evolution.behaviors;

import yio.tro.evolution.Button;

public class RbHideInfoAboutClosedSystems extends ReactBehavior{


    @Override
    public void reactAction(Button button) {
        button.menuController.hideInfoAboutClosedSystems();
    }
}
