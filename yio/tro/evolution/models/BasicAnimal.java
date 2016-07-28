package yio.tro.evolution.models;

import yio.tro.evolution.*;

/**
 * Created by ivan on 10.01.2015.
 */
public class BasicAnimal extends Animal {
    double speed, maxSpeed, turnRate;
    public double foodPref, fightPref, movePref;
    double hp; // [0, 1]
    public double dx, dy, attack, defense;
    int currentLegChangeDelay;
    long lastTimeLegChanged, timeOfBirth;
    public boolean legChangeDirection, disease;
    BasicModel basicModel;
    public static final int DEFAULT_MIN_LEG_CHANGE_DELAY = 1;
    public static final int DEFAULT_MAX_LEG_CHANGE_DELAY = 4;
    public static final int DEFAULT_CELL_CHECK_DELAY = 12;
    Storage3xTexture bodyTexture, noseTexture, spotTexture;
    double satiety, randomRotationSpeed, immunity, diseasePower;
    public BasicAiAnimal artificialIntelligence;
    int randomRotateCountDown, checkCellCountDown, legChangeCountDown;
    int massToReproduce, biteEatCountDown, diseaseBiteCountDown;
    boolean readyToReproduce, isRotatingRandomly, visualCode[];
    float viewRadius;


    public BasicAnimal(double x, double y, BasicModel basicModel) {
        super();
        this.x = x;
        this.y = y;
        this.basicModel = basicModel;
        defaultValues();
        setBaseParams();
        updateTextures();
    }


    private void setBaseParams() {
        angle = basicModel.random.nextDouble() * 2d * Math.PI;
        birthMass = 100;
        mass = birthMass;
        name = NameGenerator.generateAnimalName();

        visualCode = new boolean[10];
        for (int i = 0; i < visualCode.length; i++) {
            visualCode[i] = basicModel.random.nextBoolean();
        }

        foodPref = basicModel.random.nextDouble();
        fightPref = basicModel.random.nextDouble();
        movePref = basicModel.random.nextDouble();
        applyPrefs();
    }


    public BasicAnimal(BasicAnimal parent) {
        x = parent.x;
        y = parent.y;
        basicModel = parent.basicModel;
        defaultValues();
        setParamsByParent(parent);
        mutate();
        applyPrefs();
        updateTextures();
    }


    private void setParamsByParent(BasicAnimal parent) {
        name = parent.name;
        angle = parent.angle;
        birthMass = parent.mass;
        mass = birthMass;
        setBodySize(parent.bodySize);
        setDxDy(parent.dx, parent.dy);

        visualCode = new boolean[parent.visualCode.length];
        for (int i = 0; i < parent.visualCode.length; i++) {
            visualCode[i] = parent.visualCode[i];
        }

        foodPref = parent.foodPref;
        fightPref = parent.fightPref;
        movePref = parent.movePref;
    }


    private void applyPrefs() {
        maxSpeed = movePref * BasicModel.MAX_SPEED;
        turnRate = (1d - movePref) * BasicModel.MAX_TURN_RATE;
        defense = 1d - fightPref;
        attack = fightPref;
        immunity = fightPref;
//        if (defense < 0.1) defense = 0.1;
//        if (defense > 0.95) defense = 0.95;
    }


    void defaultValues() {
        hp = 1;
        setSatiety(0.5 + 0.5 * YioGdxGame.random.nextDouble());
        timeOfBirth = GameController.currentTime;
        currentLegChangeDelay = DEFAULT_MAX_LEG_CHANGE_DELAY;
        massToReproduce = 200;
        artificialIntelligence = new BasicAiAnimal(basicModel, this);
    }


    public void move() {
        artificialIntelligence.decide();
        x += dx;
        y += dy;
        limitByBounds();
        satiety -= 0.0015;
        checkForDiseaseBite();
        checkToUpdatePosMap();
        checkToChangeLeg();
        if (readyToReproduce) reproduce();
        if (isRotatingRandomly) randomRotate();
    }


    private void checkForDiseaseBite() {
        if (!disease) return;
        if (diseaseBiteCountDown == 0) {
            decreaseHealth(0.1);
            diseaseBiteCountDown = 10;
        } else diseaseBiteCountDown--;
    }


    private void checkToUpdatePosMap() {
        if (checkCellCountDown < 2) {
            updatePosMap();
            checkCellCountDown = DEFAULT_CELL_CHECK_DELAY;
        } else checkCellCountDown--;
    }


    private void checkToChangeLeg() {
        if (legChangeCountDown < 2) {
            changeLeg();
            legChangeCountDown = currentLegChangeDelay;
        } else legChangeCountDown--;
    }


    public void reproduce() {
        readyToReproduce = false;
        mass /= 2;
        spawnChild();
    }


    public void spawnChild() {
        basicModel.addAnimal(new BasicAnimal(this));
    }


    void updatePosMap() {
        basicModel.posMapAnimals.updateObjectPos(this);
    }


    void updateAngle() {
        angle = YioGdxGame.angle(0, 0, dx, dy);
    }


    void normalizeAngle() {
        while (angle < 0) angle += 2 * Math.PI;
        while (angle >= 2 * Math.PI) angle -= 2 * Math.PI;
    }


    private void updateCurrentLegChangeDelay() {
        if (speed < 0.1 * maxSpeed) {
            currentLegChangeDelay = 9001;
            return;
        }
        double sp = 1d - speed / maxSpeed;
        currentLegChangeDelay = (int) (DEFAULT_MIN_LEG_CHANGE_DELAY + sp * (DEFAULT_MAX_LEG_CHANGE_DELAY - DEFAULT_MIN_LEG_CHANGE_DELAY));
    }


    public void accelerate() {
        if (speed == maxSpeed) return;
        speed += 0.02 * maxSpeed;
        if (speed > maxSpeed) speed = maxSpeed;
        updateDxDy();
        updateCurrentLegChangeDelay();
    }


    public void decelerate() {
        if (speed == 0) return;
        speed -= 0.02 * maxSpeed;
        if (speed < 0) speed = 0;
        updateDxDy();
        updateCurrentLegChangeDelay();
    }


    public void updateDxDy() {
        dx = speed * Math.cos(angle);
        dy = speed * Math.sin(angle);
    }


    public double setDirectionToPoint(double px, double py) {
        double pAngle = YioGdxGame.angle(x, y, px, py);
        if (Math.abs(pAngle - angle) < 0.01) return 0;
        normalizeAngle();
        double dAngle = YioGdxGame.differenceBetweenAngles(angle, pAngle);
        rotate(dAngle);
        return dAngle;
    }


    public double setDirectionToObject(GameObject gameObject) {
        return setDirectionToPoint(gameObject.x, gameObject.y);
    }


    public void updateViewRadius() {
        float mf = (float) (Math.sqrt((float) mass / (float) birthMass));
        viewRadius = (float) (0.5 * bodySize * mf);
    }


    @Override
    public float getViewRadius() {
        return viewRadius;
    }


    void increaseMass(double value) {
        mass += value;
        if (mass >= massToReproduce) readyToReproduce = true;
        updateViewRadius();
    }


    void consumeFood(double value) {
        increaseSatiety(0.01 * value);
        increaseHealth(0.01 * value);
        increaseMass(Math.max(1, value));
    }


    boolean canAttack(BasicAnimal animal) {
//        return Math.abs(0.7 - fightPref) < Math.abs(0.3 - animal.fightPref);
//        return fightPref > animal.defense;
        return true;
    }


    public void infectWithDisease(double power) {
        if (immunity > power) return;
        disease = true;
        diseasePower = power;
    }


    @Override
    public double getDiseasePower() {
        return diseasePower;
    }


    private void mutate() {
        for (int i = 0; i < visualCode.length; i++) {
            if (basicModel.random.nextDouble() < basicModel.mutationRate) {
                visualCode[i] = !visualCode[i];
            }
        }

        foodPref += getRandomMutationDelta();
        foodPref = getLimitedPref(foodPref);

        fightPref += getRandomMutationDelta();
        fightPref = getLimitedPref(fightPref);

        movePref += getRandomMutationDelta();
        movePref = getLimitedPref(movePref);
    }


    private double getLimitedPref(double pref) {
        if (pref > 1) pref = 1;
        if (pref < 0) pref = 0;
        return pref;
    }


    private double getRandomMutationDelta() {
        return basicModel.mutationRate * (-1d + 2d * basicModel.random.nextDouble());
    }


    @Override
    public boolean hasTail() {
        return disease;
    }


    @Override
    public boolean hasNose() {
        return false;
    }


    @Override
    public Storage3xTexture getBodyTexture() {
        return bodyTexture;
    }


    @Override
    public Storage3xTexture getSpotTexture() {
        return spotTexture;
    }


    @Override
    public Storage3xTexture getNoseTexture() {
        return noseTexture;
    }


    GameView getGameView() {
        return basicModel.yioGdxGame.gameView;
    }


    @Override
    public String getGeneticCode() {
        return  "" +
                YioGdxGame.roundUp(foodPref, 2) + " " +
                YioGdxGame.roundUp(fightPref, 2) + " " +
                YioGdxGame.roundUp(movePref, 2);
    }


    public void updateTextures() {
        int bodyType = 1;
        if (visualCode[0]) bodyType = 0;

        int bodyColor = 0;
        if (visualCode[1]) {
            if (visualCode[2]) {
                bodyColor = 3;
            } else {
                bodyColor = 1;
            }
        } else {
            if (visualCode[2]) {
                bodyColor = 2;
            } else {
                bodyColor = 0;
            }
        }

        bodyTexture = getGameView().bodies[bodyType][bodyColor];

        noseTexture = null;
        if (visualCode[4]) noseTexture = getGameView().cheliceraTexture;
        if (visualCode[9]) noseTexture = getGameView().mustacheTexture;

        int spotColor = 0;
        if (visualCode[5]) spotColor = 1;

        int spotType = 0;
        for (int i = 6; i <= 8; i++) {
            if (visualCode[i]) spotType++;
        }

        spotTexture = null;
        if (spotType > 0) spotTexture = getGameView().spots[spotType - 1][spotColor];
    }


    boolean tryToBiteAnimal(BasicAnimal animal) {
        if (!BasicModel.canInteract(this, animal)) return false;
        if (!canAttack(animal)) return false;

        biteAnimal(animal);
        animal.rotate(0.5 * (1 - 2 * basicModel.random.nextDouble()));

        // consume meat in fight
//        if (biteEatCountDown == 0) {
//            consumeFood(0.01 * basicModel.meatFoodValue);
//            animal.consumeFood(0.01 * basicModel.meatFoodValue);
//            biteEatCountDown = 20;
//        } else biteEatCountDown--;

        return true;
    }


    @Override
    public boolean getDisease() {
        return disease;
    }


    private void biteAnimal(BasicAnimal animal) {
        if (attack < animal.defense) return;
        animal.decreaseHealth(0.01 * (attack - animal.defense));
//        animal.decreaseHealth(0.01);
    }


    void decreaseHealth(double value) {
        hp -= value;
    }


    void increaseHealth(double value) {
        hp += value;
        if (hp > 1) hp = 1;
    }


    @Override
    public String getOtherInfo() {
        return "";
    }


    boolean tryToEatCorpse(Corpse corpse) {
        if (!BasicModel.canInteract(this, corpse)) return false;
        if (!corpse.isEatable()) return false;

        if (corpse.disease) infectWithDisease(corpse.diseasePower);
        corpse.decreaseMass();
        consumeFood(basicModel.meatFoodValue);
        return true;
    }


    boolean tryToEatHerb(Herb herb) {
        if (!BasicModel.canInteract(this, herb)) return false;
        if (!herb.isEatable()) return false;
        herb.decreaseMass();
        consumeFood(basicModel.herbFoodValue);
        return true;
    }


    @Override
    public boolean isValid() {
        return satiety > 0 && hp > 0;
    }


    void limitByBounds() {
        if (x > basicModel.boundWidth - bodySize) {
            x = basicModel.boundWidth - bodySize;
            flipSpeedVectorHorizontally();
        }
        if (x < bodySize) {
            x = bodySize;
            flipSpeedVectorHorizontally();
        }
        if (y > basicModel.boundHeight - bodySize) {
            y = basicModel.boundHeight - bodySize;
            flipSpeedVectorVertically();
        }
        if (y < bodySize) {
            y = bodySize;
            flipSpeedVectorVertically();
        }
    }


    public void relocate(double deltaX, double deltaY) {
        x += deltaX;
        y += deltaY;
    }


    public double getCollisionRadius() {
        return 0.5 * bodySize;
    }


    void flipSpeedVectorVertically() {
//        double tempSpeedX = Math.cos(angle);
//        double tempSpeedY = Math.sin(angle);
//        tempSpeedY = -tempSpeedY;
//        angle = YioGdxGame.angle(0, 0, tempSpeedX, tempSpeedY);
        dy = -dy;
        updateAngle();
    }


    void flipSpeedVectorHorizontally() {
//        double tempSpeedX = Math.cos(angle);
//        double tempSpeedY = Math.sin(angle);
//        tempSpeedX = -tempSpeedX;
//        angle = YioGdxGame.angle(0, 0, tempSpeedX, tempSpeedY);
        dx = -dx;
        updateAngle();
    }


    void changeLeg() {
        lastTimeLegChanged = GameController.currentTime;
        if (legChangeDirection) {
            legState++;
            if (legState > 2) {
                legState = 2;
                legChangeDirection = false;
            }
        } else {
            legState--;
            if (legState < 0) {
                legState = 0;
                legChangeDirection = true;
            }
        }
    }


    public void increaseSatiety(double delta) {
        satiety += delta;
        if (satiety > 1) satiety = 1;
    }


    public void randomRotate() {
        if (randomRotateCountDown < 2) {
            randomRotationSpeed += 0.2 * (1 - 2 * basicModel.random.nextDouble());
            rotate(randomRotationSpeed);
            randomRotateCountDown = 12;
        } else randomRotateCountDown--;
    }


    public void setDxDy(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
        speed = YioGdxGame.distance(0, 0, dx, dy);
        updateAngle();
    }


    void rotate(double deltaAngle) {
        if (deltaAngle > turnRate) deltaAngle = turnRate;
        if (deltaAngle < -turnRate) deltaAngle = -turnRate;
        angle += deltaAngle;
        dx = speed * Math.cos(angle);
        dy = speed * Math.sin(angle);
    }


    public void setBodySize(double bodySize) {
        this.bodySize = bodySize;
        updateViewRadius();
    }


    public void setSatiety(double satiety) {
        this.satiety = satiety;
    }
}
