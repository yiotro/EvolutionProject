package yio.tro.evolution.models;

import yio.tro.evolution.*;

import java.util.ArrayList;

/**
 * Created by ivan on 10.01.2015.
 */
public class MatrixAnimal extends Animal{
    double speed, maxSpeed;
    double aggression;
    double fearfulness;
    double turnRate;
    double stealth;
    double social;
    double hp; // [0, 1]
    public double dx, dy;
    public ArrayList<Integer> genList;
    public boolean genActive[];
    int currentLegChangeDelay;
    long lastTimeLegChanged, timeOfBirth;
    boolean legChangeDirection;
    MatrixModel matrixModel;
    public static final int DEFAULT_MIN_LEG_CHANGE_DELAY = 1;
    public static final int DEFAULT_MAX_LEG_CHANGE_DELAY = 4;
    public static final int DEFAULT_CELL_CHECK_DELAY = 12;
    Storage3xTexture bodyTexture, noseTexture, spotTexture;
    double satiety, randomRotationSpeed;
    public MatrixAiAnimal artificialIntelligence;
    int randomRotateCountDown, checkCellCountDown, legChangeCountDown;
    public int massToReproduce, debugCount;
    boolean readyToReproduce, isRotatingRandomly;
    float viewRadius;


    public MatrixAnimal(double x, double y, MatrixModel matrixModel) {
        super();
        this.x = x;
        this.y = y;
        this.matrixModel = matrixModel;
        genList = new ArrayList<>();
        genActive = new boolean[MatrixModel.MAX_GEN_NUMBER];
        defaultValues();
        setTurnRate(0.3);
        angle = matrixModel.random.nextDouble() * 2d * Math.PI;
        maxSpeed = 0.0010 * matrixModel.w;
        birthMass = 100;
        mass = birthMass;
        maxSpeed *= 2;
        for (int i = 0; i < matrixModel.numberOfGenes; i++) {
            if (matrixModel.random.nextDouble() < 0.5) {
                genList.add(i);
            }
        }
        updateGenActiveArray();
        name = NameGenerator.generateAnimalNameByGenes(genActive);
        updateTextures();
    }


    public MatrixAnimal(MatrixAnimal parent) {
        genList = new ArrayList<>();
        genActive = new boolean[MatrixModel.MAX_GEN_NUMBER];
        x = parent.x;
        y = parent.y;
        matrixModel = parent.matrixModel;
        defaultValues();
        name = parent.name;
        setTurnRate(parent.turnRate);
        angle = parent.angle;
        maxSpeed = parent.maxSpeed;
        birthMass = parent.mass;
        mass = birthMass;
        setBodySize(parent.bodySize);
        setDxDy(parent.dx, parent.dy);
        for (int i = 0; i < parent.genList.size(); i++) {
            genList.add(parent.genList.get(i));
        }
        if (matrixModel.random.nextDouble() < matrixModel.mutationRate) {
            mutate();
        }
        updateGenActiveArray();
        name = NameGenerator.generateAnimalNameByGenes(genActive);
        updateTextures();
    }


    void defaultValues() {
        hp = 1;
        setSatiety(0.5 + 0.5 * YioGdxGame.random.nextDouble());
        timeOfBirth = GameController.currentTime;
        currentLegChangeDelay = DEFAULT_MAX_LEG_CHANGE_DELAY;
        massToReproduce = 200;
        artificialIntelligence = new MatrixAiAnimal(matrixModel, this);
    }


    public void move() {
        artificialIntelligence.decide();
        x += dx;
        y += dy;
        limitByBounds();
        satiety -= 0.0015;
        if (checkCellCountDown < 2) {
            checkCell();
            checkCellCountDown = DEFAULT_CELL_CHECK_DELAY;
        } else checkCellCountDown--;
        if (legChangeCountDown < 2) {
            changeLeg();
            legChangeCountDown = currentLegChangeDelay;
        } else legChangeCountDown--;
        if (readyToReproduce) reproduce();
        if (isRotatingRandomly) randomRotate();
    }


    public void reproduce() {
        readyToReproduce = false;
        mass /= 2;
        spawnChild();
    }


    public void spawnChild() {
        matrixModel.addAnimal(new MatrixAnimal(this));
    }


    void checkCell() {
        matrixModel.posMapAnimals.updateObjectPos(this);
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


    void increaseMass(int value) {
        mass += value;
        if (mass >= massToReproduce) readyToReproduce = true;
        updateViewRadius();
    }


    @Override
    public String getGeneticCode() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[ ");
        for (Integer gen : genList) {
            stringBuffer.append(gen.intValue() + " ");
        }
        stringBuffer.append("]");
        return stringBuffer.toString();
    }


    @Override
    public String getOtherInfo() {
        return "< - >";
    }


    boolean hasGen(int index) {
        for (int i = 0; i < genList.size(); i++) {
            if (genList.get(i) == index) return true;
        }
        return false;
    }


    public void updateGenActiveArray() {
        for (int i = 0; i < genActive.length; i++) {
            genActive[i] = hasGen(i);
        }
    }


    void consumeFood(double value) {
        increaseSatiety(0.01 * value);
        increaseMass(Math.max(1, (int) (value)));
    }


    private void mutate() {
        if (matrixModel.random.nextDouble() < 0.5) { // add gen
            ArrayList<Integer> absentGenes = new ArrayList<>();
            for (int i = 0; i < matrixModel.numberOfGenes; i++) {
                if (!hasGen(i)) absentGenes.add(i);
            }
            if (absentGenes.size() == 0) return;
            int newGen = absentGenes.get(matrixModel.random.nextInt(absentGenes.size()));
            genList.add(newGen);
        } else { // remove gen
            if (genList.size() <= 1) return;
            int removeIndex = matrixModel.random.nextInt(genList.size());
            genList.remove(removeIndex);
        }
    }


    @Override
    public boolean hasTail() {
        return genActive[3];
    }


    @Override
    public boolean hasNose() {
        return genActive[4];
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
        return matrixModel.yioGdxGame.gameView;
    }


    public void updateTextures() {
        int bodyType = 1;
        if (genActive[0]) bodyType = 0;

        int bodyColor = 0;
        if (genActive[1]) {
            if (genActive[2]) {
                bodyColor = 3;
            } else {
                bodyColor = 1;
            }
        } else {
            if (genActive[2]) {
                bodyColor = 2;
            } else {
                bodyColor = 0;
            }
        }

        bodyTexture = getGameView().bodies[bodyType][bodyColor];

        noseTexture = null;
        if (genActive[4]) noseTexture = getGameView().cheliceraTexture;
        if (genActive[9]) noseTexture = getGameView().mustacheTexture;

        int spotColor = 0;
        if (genActive[5]) spotColor = 1;

        int spotType = 0;
        for (int i = 6; i <= 8; i++) {
            if (genActive[i]) spotType++;
        }

        spotTexture = null;
        if (spotType > 0) spotTexture = getGameView().spots[spotType - 1][spotColor];
    }


    boolean tryToBiteAnimal(MatrixAnimal animal) {
        if (!MatrixModel.canInteract(this, animal)) return false;
        if (matrixModel.compareAnimalsGenetically(this, animal) <= 0)
            return false; // can't bite animal that is stronger genetically
        animal.hp -= 0.01;
        animal.rotate(0.5 * (1 - 2 * matrixModel.random.nextDouble()));
        return true;
    }


    boolean tryToEatCorpse(Corpse corpse) {
        if (!MatrixModel.canInteract(this, corpse)) return false;
        if (!corpse.isEatable()) return false;
        corpse.decreaseMass();
        consumeFood(2);
        return true;
    }


    boolean tryToEatHerb(Herb herb) {
        if (!MatrixModel.canInteract(this, herb)) return false;
        if (!herb.isEatable()) return false;
        herb.decreaseMass();
        consumeFood(1);
        return true;
    }


    @Override
    public boolean getDisease() {
        return false;
    }


    @Override
    public double getDiseasePower() {
        return 0;
    }


    @Override
    public boolean isValid() {
        return satiety > 0 && hp > 0;
    }


    void limitByBounds() {
        if (x > matrixModel.boundWidth - bodySize) {
            x = matrixModel.boundWidth - bodySize;
            flipSpeedVectorHorizontally();
        }
        if (x < bodySize) {
            x = bodySize;
            flipSpeedVectorHorizontally();
        }
        if (y > matrixModel.boundHeight - bodySize) {
            y = matrixModel.boundHeight - bodySize;
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
            randomRotationSpeed += 0.2 * (1 - 2 * matrixModel.random.nextDouble());
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


    public void setAggression(double aggression) {
        this.aggression = aggression;
    }


    public void setFearfulness(double fearfulness) {
        this.fearfulness = fearfulness;
    }


    public void setTurnRate(double turnRate) {
        this.turnRate = turnRate;
    }


    public void setBodySize(double bodySize) {
        this.bodySize = bodySize;
        updateViewRadius();
    }


    public void setSatiety(double satiety) {
        this.satiety = satiety;
    }


    public void setStealth(double stealth) {
        this.stealth = stealth;
    }


    public void setSocial(double social) {
        this.social = social;
    }
}
