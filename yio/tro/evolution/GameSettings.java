package yio.tro.evolution;

/**
 * Created by ivan on 05.08.14.
 */
public class GameSettings {

    YioGdxGame yioGdxGame;
    int speed;
    public static final int SPEED_SLOW = 0;
    public static final int SPEED_NORMAL = 1;
    public static final int SPEED_FAST = 2;
    int difficulty;
    public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_NORMAL = 1;
    public static final int DIFFICULTY_HARD = 2;

    public GameSettings(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
    }

    public void switchSpeed() {
        speed++;
        if (speed > SPEED_FAST) speed = 0;
    }

    public void switchDifficulty() {
        difficulty++;
        if (difficulty > DIFFICULTY_HARD) difficulty = 0;
    }
}
