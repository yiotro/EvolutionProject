package yio.tro.evolution.models;

import com.badlogic.gdx.Gdx;
import yio.tro.evolution.*;
import yio.tro.evolution.factor_yio.FactorYio;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by ivan on 30.03.2016.
 */
public class BasicModel extends EvolutionModel {

    public static final int LIMIT = 4;
    public static final double MAX_TURN_RATE = 0.3;
    public static final double MAX_SPEED = 0.005 * Gdx.graphics.getWidth();
    public static final double INTERACT_DISTANCE = 0.02 * Gdx.graphics.getWidth();
    public static final int ANIMAL_QUANTITY_LIMIT = 5000;
    public static final int INTERACT_CELL_OFFSET = 1;

    boolean grassCover[][];
    public boolean cameraFixedOnSelectedAnimal, genDiagramVisible;

    public int cacheWidth, cacheHeight;
    int addingDelay, currentAnimalStep, updateGenSituationCountDown, genSituation[];
    int herbMoveDelay, checkToRemoveDelay, moveGrassDelay, snapshotDelay, diseaseCountDown;
    int checkToRemoveCountDown, snapshotCountDown, moveGrassCountDown, moveHerbsCountDown;
    long lastTimeSomethingBeenAdded, timeStep;
    double meatFoodValue, temperatureAmplitude, herbFoodValue, diseaseRate;

    public PosMapYio posMapAnimals;
    public PosMapYio posMapCorpses;
    public ArrayList<Herb> herbs;
    ArrayList<Herb> emptyHerbList;
    public ArrayList<BasicAnimal> animals, emptyAnimalList;
    public ArrayList<Corpse> corpses, emptyCorpseList;
    EvolutionSubject selectedSubject;
    FactorYio selectionFactor;
    PMCoor indexPoint;


    public BasicModel(YioGdxGame yioGdxGame, GameController gameController) {
        super(yioGdxGame, gameController);
        w = gameController.w;
        h = gameController.h;
        boundWidth = gameController.boundWidth;
        boundHeight = gameController.boundHeight;

        addingDelay = 2;

        herbs = new ArrayList<Herb>();
        animals = new ArrayList<BasicAnimal>();
        grass = new ArrayList<Grass>();
        emptyAnimalList = new ArrayList<BasicAnimal>();
        emptyHerbList = new ArrayList<Herb>();
        emptyCorpseList = new ArrayList<>();
        selectionFactor = new FactorYio();
        indexPoint = new PMCoor();

        cacheCellSize = 0.05 * w;
        cacheHerbsCellSize = 0.07 * w;

        cacheWidth = (int) (boundWidth / cacheCellSize) + 1;
        cacheHeight = (int) (boundHeight / cacheCellSize) + 1;

        grassCover = new boolean[cacheWidth][cacheHeight];

        RectangleYio mapPos = new RectangleYio(0, 0, boundWidth, boundHeight);
        posMapHerbs = new PosMapYio(mapPos, cacheHerbsCellSize);
        posMapAnimals = new PosMapYio(mapPos, cacheCellSize);
        posMapCorpses = new PosMapYio(mapPos, cacheCellSize);

        setDelays();
    }


    @Override
    public void prepare() {
        temperature = 0.5;
        timeStep = 0;
        herbs = new ArrayList<Herb>();
        animals = new ArrayList<BasicAnimal>();
        corpses = new ArrayList<Corpse>();
        clearPosMaps();
        grass.clear();
        cameraFixedOnSelectedAnimal = false;
        readSliderParams();
        prepareDiagram();
    }


    private void prepareDiagram() {
        genDiagramVisible = false;
        genSituation = new int[4];
        yioGdxGame.gameView.setDiagram(genDiagramVisible);
        double diagWidth = 0.04 * gameController.w * (genSituation.length + 1) + 0.006 * gameController.w * (genSituation.length - 1);
        gameController.yioGdxGame.gameView.diagramPos = new Rect(gameController.w - diagWidth, 0, diagWidth, 0.4 * gameController.h);
    }


    public void readSliderParams() {
        double f = yioGdxGame.menuController.sliders.get(5).getCurrentRunnerIndex() / 100f;
        meatFoodValue = f;

        f = yioGdxGame.menuController.sliders.get(7).getCurrentRunnerIndex() / 100f;
        herbFoodValue = f;

        mutationRate = yioGdxGame.menuController.sliders.get(1).getCurrentRunnerIndex();
        mutationRate /= 100d;

        temperatureAmplitude = yioGdxGame.menuController.sliders.get(6).getCurrentRunnerIndex() / 100f;

        diseaseRate = yioGdxGame.menuController.sliders.get(2).getCurrentRunnerIndex() / 100f;
    }


    @Override
    public void touchDown(float tx, float ty) {

    }


    @Override
    public void touchDrag(float tx, float ty) {

    }


    @Override
    public void touchUp(float tx, float ty) {
        if (tryToSelectSubject(tx, ty)) yioGdxGame.menuController.showInfoAboutSelectedSubject();
        else yioGdxGame.menuController.hideInfoAboutSelectedSubject();
    }


    boolean tryToSelectSubject(double selX, double selY) {
        selectedSubject = null;
        selectionFactor.setValues(0, 0);
        selectionFactor.beginSpawning(4, 2);
        cameraFixedOnSelectedAnimal = false;
        int x = (int) (selX / cacheCellSize);
        int y = (int) (selY / cacheCellSize);

        ArrayList<PosMapObjectYio> tempAnimalList;
        BasicAnimal tempAnimal;
        double minDistance, currentDistance;
        minDistance = w * h;
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                tempAnimalList = posMapAnimals.getSector(i, j);
                for (int k = tempAnimalList.size() - 1; k >= 0; k--) {
                    tempAnimal = (BasicAnimal) tempAnimalList.get(k);
                    currentDistance = YioGdxGame.distance(selX, selY, tempAnimal.x, tempAnimal.y);
                    if (currentDistance < 0.02 * w && currentDistance < minDistance) {
                        selectedSubject = tempAnimal;
                        minDistance = currentDistance;
                    }
                }
            }
        }
        if (selectedSubject != null) {
            cameraFixedOnSelectedAnimal = true;
            return true;
        }

//        ArrayList<Herb> tempHerbList;
//        GameObject tempHerb;
//        for (int i=x-1; i<=x+1; i++) {
//            for (int j=y-1; j<=y+1; j++) {
//                tempHerbList = getCacheHerbsByIndex(i, j);
//                for (int k=tempHerbList.size()-1; k>=0; k--) {
//                    tempHerb = tempHerbList.get(k);
//                    if (tempHerb instanceof Herb) {
//                        if (YioGdxGame.distance(selX, selY, tempHerb.x, tempHerb.y) < 0.02 * w) {
//                            selectedSubject = (Herb)tempHerb;
//                            return true;
//                        }
//                    }
//                }
//            }
//        }
        return false;
    }


    void clearPosMaps() {
        posMapAnimals.clear();
        posMapCorpses.clear();
        posMapHerbs.clear();
    }


    @Override
    public void notifyAboutMovement() {
        cameraFixedOnSelectedAnimal = false;
    }


    @Override
    public void addAnimal(float x, float y) {
        if (System.currentTimeMillis() > lastTimeSomethingBeenAdded + addingDelay) {
            spawnRandomAnimal(x, y);
            lastTimeSomethingBeenAdded = System.currentTimeMillis();
        }
    }


    @Override
    public void addHerb(float x, float y) {
        if (System.currentTimeMillis() > lastTimeSomethingBeenAdded + addingDelay) {
            spawnHerb(x, y);
            lastTimeSomethingBeenAdded = System.currentTimeMillis();
        }
    }


    @Override
    public void createField() {
        selectedSubject = null;
        ArrayList<SliderYio> sliders = yioGdxGame.menuController.sliders;

        for (int i = 0; i < sliders.get(0).getCurrentRunnerIndex(); i++) {
            spawnAnimal(boundWidth * random.nextFloat(), boundHeight * random.nextFloat());
        }

        for (int i = 0; i < 100; i++) {
            spawnHerb(random.nextDouble() * boundWidth, random.nextDouble() * boundHeight);
        }
    }


    public static boolean canInteract(GameObject one, GameObject two) {
        return distanceBetweenObjects(one, two) <= INTERACT_DISTANCE;
    }


    public static double distanceBetweenObjects(GameObject one, GameObject two) {
        return YioGdxGame.distance(one.x, one.y, two.x, two.y);
    }


    private double getRandomBodySize() {
        return 0.01f * w * (1 + random.nextDouble());
    }


    public boolean getCacheGrassByIndex(int i, int j) {
        if (i < 0 || j < 0 || i >= cacheWidth || j >= cacheHeight) return false;
        return grassCover[i][j];
    }


    public boolean getCacheGrassByPos(double x, double y) {
        int i = (int) (x / cacheCellSize);
        int j = (int) (y / cacheCellSize);
        return getCacheGrassByIndex(i, j);
    }


    public void setCacheGrassByIndex(boolean value, int i, int j) {
        if (i < 0 || j < 0 || i >= cacheWidth || j >= cacheHeight) return;
        grassCover[i][j] = value;
    }


    public void setCacheGrassByPos(boolean value, double x, double y) {
        int i = (int) (x / cacheCellSize);
        int j = (int) (y / cacheCellSize);
        setCacheGrassByIndex(value, i, j);
    }


    @Override
    public void switchDiagram() {
        genDiagramVisible = !genDiagramVisible;
        yioGdxGame.gameView.setDiagram(genDiagramVisible);
        updateGenSituation();
    }


    public void updateGenSituation() {
        double mediumFoodPref = 0;
        for (BasicAnimal animal : animals) {
            mediumFoodPref += animal.foodPref;
        }
        mediumFoodPref /= animals.size();

        double mediumFightPref = 0;
        for (BasicAnimal animal : animals) {
            mediumFightPref += animal.fightPref;
        }
        mediumFightPref /= animals.size();

        double mediumMovePref = 0;
        for (BasicAnimal animal : animals) {
            mediumMovePref += animal.movePref;
        }
        mediumMovePref /= animals.size();

        int M = 1000;
        genSituation[0] = M - 1;
        genSituation[1] = (int)(M * mediumFoodPref);
        genSituation[2] = (int)(M * mediumFightPref);
        genSituation[3] = (int)(M * mediumMovePref);
    }


    private void setDelays() {
        checkToRemoveDelay = 30;
        snapshotDelay = 60;
        moveGrassDelay = 6;
        herbMoveDelay = 3;
    }


    @Override
    public void move() {
        timeStep++;
        moveObjects();
        moveHerbs();
        moveAnimals();
        moveChecks();
        moveGrass();
        updateTemperature();
    }


    public void gatherNearbyCorpses(BasicAnimal animal) {
        ArrayList<PosMapObjectYio> nearbyCorpses;
        posMapCorpses.transformCoorToIndex(animal.x, animal.y, indexPoint);
        for (int i = indexPoint.x - INTERACT_CELL_OFFSET; i <= indexPoint.x + INTERACT_CELL_OFFSET; i++) {
            for (int j = indexPoint.y - INTERACT_CELL_OFFSET; j <= indexPoint.y + INTERACT_CELL_OFFSET; j++) {
                nearbyCorpses = posMapCorpses.getSector(i, j);
                if (nearbyCorpses == null) continue;
                int c = 0;
                for (int z = nearbyCorpses.size() - 1; z >= 0 && c < LIMIT; z--) {
                    animal.artificialIntelligence.addToNearbyCorpses((Corpse) nearbyCorpses.get(z));
                    c++;
                }
            }
        }
    }


    @Override
    public FactorYio getSelectionFactor() {
        return selectionFactor;
    }


    private double getTemperaturePeriod() {
        return 1;
    }


    public void updateTemperature() {
        double t = timeStep / 10000d;
        t /= getTemperaturePeriod();
        temperature = 0.5 + temperatureAmplitude * 0.4 * Math.sin(t);

//        double sin = Math.sin(timeStep / 20000d);
//        double delta = 0;
//        if (sin > 0) delta = 1;
//        if (sin < 0) delta = -1;
//        delta *= temperatureAmplitude * 0.4;
//        temperature = 0.5 + delta;

//        double step = (int)(timeStep / 200000d);
//        temperature = 0.3 * step;
//        while (temperature > 1) temperature -= 1;
    }


    void moveObjects() {
        for (Corpse corpse : corpses) corpse.move();
    }


    void moveHerbs() {
        if (moveHerbsCountDown < 2) {
            for (Herb herb : herbs) herb.move();
            moveHerbsCountDown = herbMoveDelay;
        } else moveHerbsCountDown--;
    }


    void moveAnimals() {
        for (int i = animals.size() - 1; i >= 0; i--) {
            if (i % 2 == currentAnimalStep) {
                animals.get(i).move();
            }
        }
        currentAnimalStep++;
        if (currentAnimalStep > 1) currentAnimalStep = 0;
    }


    void moveChecks() {
        if (checkToRemoveCountDown < 2) {
            checkToRemoveStuff();
            checkToRemoveCountDown = checkToRemoveDelay;
        } else checkToRemoveCountDown--;

        for (int i = herbs.size() - 1; i >= 0; i--) herbs.get(i).checkToSpawnHerb();

        checkToTakeSnapshot();

        checkToUpdateGenSituation();

        checkToSpawnDisease();
    }


    private void checkToSpawnDisease() {
        if (diseaseCountDown == 0) {
            spawnDisease();
            diseaseCountDown = 60;
        } else diseaseCountDown--;
    }


    private void spawnDisease() {
        if (animals.size() == 0) return;
        int index = random.nextInt(animals.size());
        animals.get(index).infectWithDisease(diseaseRate);
    }


    public void showDetailedInfo() {
        YioGdxGame.say("--- Detailed info ---");

        for (int i = 0; i < MenuController.NUMBER_OF_PLOT_LINES; i++) {
            if (!gameController.yioGdxGame.menuController.combinationPlot[i]) continue;
            ArrayList<Integer> list = gameController.dataStorage.getDataList(i);
            String name = gameController.dataStorage.getPlotNameByIndex(i);
            YioGdxGame.say(name + ": " + list);
        }
    }


    private void detailedInfoOne() {
        YioGdxGame.say("Current population: " + animals.size());

        YioGdxGame.say("amplitude = " + YioGdxGame.roundUp(temperatureAmplitude, 2));

        YioGdxGame.say("period = " + YioGdxGame.roundUp(getTemperaturePeriod(), 2));

        ArrayList<Integer> population = gameController.dataStorage.getDataList(0);
        YioGdxGame.say("population: " + getSievedList(population, 10));

        ArrayList<Integer> temperature = gameController.dataStorage.getDataList(2);
        YioGdxGame.say("temperature: " + getSievedList(temperature, 10));
    }


    private ArrayList<Integer> getSievedList(ArrayList<Integer> src, int sieveStep) {
        ArrayList<Integer> resultList = new ArrayList<>();
        for (int i = 0; i < src.size(); i += sieveStep) {
            resultList.add(src.get(i));
        }
        return resultList;
    }


    private void checkToUpdateGenSituation() {
        if (genDiagramVisible) {
            if (updateGenSituationCountDown == 0) {
                updateGenSituation();
                updateGenSituationCountDown = 10 * gameController.yioGdxGame.speedMultiplier;
            } else updateGenSituationCountDown--;
        }
    }


    private void checkToTakeSnapshot() {
        if (snapshotCountDown < 2) {
            gameController.dataStorage.takeSnapshot();
            snapshotCountDown = snapshotDelay;
        } else snapshotCountDown--;
    }


    @Override
    public boolean isGenDiagramVisible() {
        return genDiagramVisible;
    }


    @Override
    public int[] getGeneticSituation() {
        return genSituation;
    }


    void moveGrass() {
        if (timeStep > 15000) return; // grass is decorative. It's static after some time

        if (moveGrassCountDown < 2) {
            for (int i = grass.size() - 1; i >= 0; i--)
                grass.get(i).move();
            moveGrassCountDown = moveGrassDelay;
        } else moveGrassCountDown--;
    }


    void checkToRemoveStuff() {
        checkToRemoveCountDown = checkToRemoveDelay;
        checkToRemoveAnimals();
        checkToRemoveCorpses();
        checkToRemoveHerbs();
    }


    void checkToRemoveAnimals() {
        for (int k = animals.size() - 1; k >= 0; k--) {
            if (!animals.get(k).isValid()) destroyAnimal(animals.get(k));
        }
    }


    void checkToRemoveHerbs() {
        for (int i = herbs.size() - 1; i >= 0; i--) {
            if (!herbs.get(i).isValid()) {
                destroyHerb(herbs.get(i));
            }
        }
    }


    void checkToRemoveCorpses() {
        for (int i = corpses.size() - 1; i >= 0; i--) {
            if (!corpses.get(i).isValid()) destroyCorpse(corpses.get(i));
        }
    }


    @Override
    public EvolutionSubject getSelectedSubject() {
        return selectedSubject;
    }


    @Override
    public boolean isModelMatrix() {
        return false;
    }


    @Override
    public boolean isModelBasic() {
        return true;
    }


    public void destroyCorpse(Corpse corpse) {
        corpse.setValid(false);
        corpses.remove(corpse);
        posMapCorpses.removeObject(corpse);
    }


    public void destroyHerb(Herb herb) {
        herb.setValid(false);
        herbs.remove(herb);
        posMapHerbs.removeObject(herb);
    }


    void destroyAnimal(BasicAnimal animal) {
        animal.setValid(false);
        animals.remove(animal);
        posMapAnimals.removeObject(animal);
        addCorpse(animal, 0.5 * animal.bodySize / animal.mass);
    }


    private void addCorpse(BasicAnimal animal, double r) {
        ListIterator iterator = corpses.listIterator();
        Corpse corpse = new Corpse(animal, r, 0.4);
        corpse.factorYio.setValues(0.8, 0);
        corpse.factorYio.beginSpawning(4, 1);
        iterator.add(corpse);
        posMapCorpses.addObject(corpse);
    }


    public void spawnGrass(Herb herb1, Herb herb2) {
        if (getCacheGrassByPos(0.5 * (herb1.x + herb2.x), 0.5 * (herb1.y + herb2.y))) return;
        grass.add(new Grass(herb1, herb2, gameController));
        setCacheGrassByPos(true, 0.5 * (herb1.x + herb2.x), 0.5 * (herb1.y + herb2.y));
    }


    public boolean canSpawnHerb(double x, double y) {
        if (posMapHerbs.getSectorByPos(x, y).size() > 1) return false;
        return true;
    }


    public Herb spawnHerb(double posX, double posY) {
        if (posX < 0 || posY < 0 || posX > gameController.boundWidth || posY > gameController.boundHeight) return null;
        if (!canSpawnHerb(posX, posY)) return null;
        ListIterator iterator = herbs.listIterator();
        Herb herb = new Herb(posX, posY, 0.02f * gameController.w, this);
        iterator.add(herb);
        posMapHerbs.addObject(herb);
        return herb;
    }


    public void addAnimal(BasicAnimal animal) {
        if (animals.size() > ANIMAL_QUANTITY_LIMIT) return;
        ListIterator iterator = animals.listIterator();
        iterator.add(animal);
        posMapAnimals.addObject(animal);
    }


    @Override
    public void moveAlways() {
        if (selectedSubject != null) selectionFactor.move();
    }


    public BasicAnimal spawnRandomAnimal(float posX, float posY) {
        return spawnAnimal(posX, posY);
    }


    public BasicAnimal spawnAnimal(float posX, float posY) {
        if (posX < 0 || posY < 0 || posX > gameController.boundWidth || posY > gameController.boundHeight) return null;
        ListIterator iterator = animals.listIterator();
        BasicAnimal animal = new BasicAnimal(posX, posY, this);
        iterator.add(animal);
        double ra = YioGdxGame.randomAngle();
        double rr = 0.001 * gameController.w + 0.001 * gameController.w * gameController.random.nextDouble();
        animal.setDxDy(rr * Math.cos(ra), rr * Math.sin(ra));
        animal.setBodySize(getRandomBodySize());
        posMapAnimals.addObject(animal);
        return animal;
    }


    @Override
    public void spawnManyAnimals(double posX, double posY) {
        for (int i = 0; i < 5; i++) {
            spawnAnimal((float)posX, (float)posY);
        }
    }


    @Override
    public ArrayList<? extends Animal> getAnimals() {
        ArrayList<? extends Animal> bases = animals;
        return bases;
    }


    @Override
    public ArrayList<Herb> getHerbs() {
        return herbs;
    }


    @Override
    public ArrayList<Corpse> getCorpses() {
        return corpses;
    }


}
