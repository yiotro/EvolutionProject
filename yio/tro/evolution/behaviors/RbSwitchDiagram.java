package yio.tro.evolution.behaviors;

import yio.tro.evolution.Button;
import yio.tro.evolution.EvolutionModel;
import yio.tro.evolution.models.MatrixModel;

/**
 * Created by ivan on 03.01.2016.
 */
public class RbSwitchDiagram extends ReactBehavior{

    @Override
    public void reactAction(Button button) {
        EvolutionModel evolutionModel = button.menuController.yioGdxGame.gameController.evolutionModel;
        evolutionModel.switchDiagram();
    }
}
