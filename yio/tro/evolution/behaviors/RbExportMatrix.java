package yio.tro.evolution.behaviors;

import yio.tro.evolution.Button;
import yio.tro.evolution.EvolutionModel;
import yio.tro.evolution.models.BasicModel;
import yio.tro.evolution.models.MatrixModel;

/**
 * Created by ivan on 07.02.2016.
 */
public class RbExportMatrix extends ReactBehavior{

    @Override
    public void reactAction(Button button) {
        EvolutionModel evolutionModel = button.menuController.yioGdxGame.gameController.evolutionModel;
        if (evolutionModel.isModelMatrix()) {
            MatrixModel matrixModel = (MatrixModel) evolutionModel;
            matrixModel.exportMatrixToClipboard();
        } else if (evolutionModel.isModelBasic()) {
            BasicModel basicModel = (BasicModel) evolutionModel;
            basicModel.showDetailedInfo();
        }
    }
}
