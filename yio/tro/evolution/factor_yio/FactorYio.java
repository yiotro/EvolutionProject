package yio.tro.evolution.factor_yio;

/**
 * Created by ivan on 21.04.2015.
 */
public class FactorYio {
    double f, gravity, dy, speedMultiplier;
    MoveBehavior moveBehavior;
    boolean itsTimeToStop;

    public FactorYio() {
        // empty constructor
        moveBehavior = MoveBehavior.moveBehaviorLighty;
    }

    public void move() {
        moveBehavior.move(this);
    }

    public void beginSpawning(int moveMode, double speed) {
        // speed == 1 is default
        setMoveBehaviorByMoveMode(moveMode);
        gravity = 0.01;
        speedMultiplier = 0.3 * speed;
        moveBehavior.alertAboutSpawning(this);
    }

    public void beginDestroying(int moveMode, double speed) {
        // speed == 1 is default
        setMoveBehaviorByMoveMode(moveMode);
        gravity = -0.01;
        speedMultiplier = 0.3 * speed;
        moveBehavior.alertAboutDestroying(this);
    }

    private void setMoveBehaviorByMoveMode(int moveMode) {
        switch (moveMode) {
            case 0: moveBehavior = MoveBehavior.moveBehaviorSimple; break;
            case 1: moveBehavior = MoveBehavior.moveBehaviorLighty; break;
            case 2: moveBehavior = MoveBehavior.moveBehaviorSpringy; break;
            case 3: moveBehavior = MoveBehavior.moveBehaviorApproach; break;
            case 4: moveBehavior = MoveBehavior.moveBehaviorPlayful; break;
            default: moveBehavior = MoveBehavior.moveBehaviorLighty; break;
        }
    }

    public void setValues(double f, double dy) {
        this.f = f;
        this.dy = dy;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }

    public double getDy() {
        return dy;
    }

    public double getGravity() {
        return gravity;
    }

    public void stopMoving() {
        moveBehavior.stopMoving(this);
    }

    public boolean needsToMove() {
        return moveBehavior.needsToMove(this);
    }

    public float get() {
        return (float)f;
    }
}