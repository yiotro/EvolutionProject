package yio.tro.evolution.behaviors;

import yio.tro.evolution.Button;

/**
 * Created by ivan on 29.11.2015.
 */
public class RbHideAnimalInfo extends ReactBehavior{

    @Override
    public void reactAction(Button button) {
        button.menuController.hideInfoAboutSelectedSubject();
    }
}
