package yio.tro.evolution.behaviors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.evolution.Button;

/**
 * Created by ivan on 06.10.2014.
 */
public class RbReset extends ReactBehavior {

    @Override
    public void reactAction(Button button) {
        Preferences preferences = Gdx.app.getPreferences("main");
        preferences.putInteger("progress", 0);
        preferences.flush();
        button.menuController.yioGdxGame.setSelectedLevelIndex(0);
    }
}
