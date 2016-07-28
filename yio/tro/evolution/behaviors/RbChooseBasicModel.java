package yio.tro.evolution.behaviors;

import yio.tro.evolution.Button;
import yio.tro.evolution.GameController;

/**
 * Created by ivan on 31.03.2016.
 */
public class RbChooseBasicModel extends ReactBehavior{

    @Override
    public void reactAction(Button button) {
        button.menuController.chooseBasicModel();
        button.menuController.yioGdxGame.gameController.setChosenModel(GameController.MODEL_BASIC);
    }
}
