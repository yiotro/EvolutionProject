package yio.tro.evolution.behaviors;

import yio.tro.evolution.Button;
import yio.tro.evolution.models.BasicModel;

/**
 * Created by ivan on 05.04.2016.
 */
public class RbApplyParams extends ReactBehavior{

    @Override
    public void reactAction(Button button) {
        BasicModel basicModel = (BasicModel) button.menuController.yioGdxGame.gameController.evolutionModel;
        basicModel.readSliderParams();
        button.menuController.forceActionForButtonById(31);
    }
}
