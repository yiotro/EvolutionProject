package yio.tro.evolution.behaviors;

import com.badlogic.gdx.Gdx;
import yio.tro.evolution.Button;

/**
 * Created by ivan on 05.08.14.
 */
public class RbExit extends ReactBehavior{

    @Override
    public void reactAction(Button button) {
        Gdx.app.exit();
    }
}
