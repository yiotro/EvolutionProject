package yio.tro.evolution.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Clipboard;
import yio.tro.evolution.*;
import yio.tro.evolution.factor_yio.FactorYio;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * Created by ivan on 30.03.2016.
 */
public class MatrixModel extends EvolutionModel {

    public static final int LIMIT = 4;
    public static final double INTERACT_DISTANCE = 0.02 * Gdx.graphics.getWidth();
    public static final int ANIMAL_QUANTITY_LIMIT = 5000;
    public static final int INTERACT_CELL_OFFSET = 1;
    public static final int MIN_GEN_NUMBER = 2;
    public static final int MAX_GEN_NUMBER = 10;

    boolean grassCover[][];
    public boolean cameraFixedOnSelectedAnimal, genDiagramVisible;

    public int numberOfGenes;
    public int cacheWidth, cacheHeight;
    int addingDelay, currentAnimalStep;
    int herbMoveDelay, checkToRemoveDelay, moveGrassDelay, snapshotDelay;
    int checkToRemoveCountDown, snapshotCountDown, moveGrassCountDown, moveHerbsCountDown;
    public int genRelationMatrix[][], genSituation[], updateGenSituationCountDown;
    long lastTimeSomethingBeenAdded, timeStep;

    double temperatureDelta, temperatureAmplitude;

    public PosMapYio posMapAnimals;
    public PosMapYio posMapCorpses;
    public ArrayList<Herb> herbs;
    ArrayList<Herb> emptyHerbList;
    public ArrayList<MatrixAnimal> animals, emptyAnimalList;
    public ArrayList<Corpse> corpses, emptyCorpseList;
    EvolutionSubject selectedSubject;
    FactorYio selectionFactor;


    public MatrixModel(YioGdxGame yioGdxGame, GameController gameController) {
        super(yioGdxGame, gameController);
        w = gameController.w;
        h = gameController.h;
        boundWidth = gameController.boundWidth;
        boundHeight = gameController.boundHeight;

        addingDelay = 2;

        herbs = new ArrayList<Herb>();
        animals = new ArrayList<MatrixAnimal>();
        grass = new ArrayList<Grass>();
        emptyAnimalList = new ArrayList<MatrixAnimal>();
        emptyHerbList = new ArrayList<Herb>();
        emptyCorpseList = new ArrayList<>();
        selectionFactor = new FactorYio();

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
        animals = new ArrayList<MatrixAnimal>();
        corpses = new ArrayList<Corpse>();
        clearPosMaps();
        grass.clear();
        generateGenes(yioGdxGame.menuController.sliders.get(3).getCurrentRunnerIndex() + MIN_GEN_NUMBER);
        temperatureAmplitude = yioGdxGame.menuController.sliders.get(6).getCurrentRunnerIndex() / 100f;
        cameraFixedOnSelectedAnimal = false;
        genDiagramVisible = false;
        yioGdxGame.gameView.setDiagram(genDiagramVisible);
    }


    @Override
    public void switchDiagram() {
        genDiagramVisible = !genDiagramVisible;
        yioGdxGame.gameView.setDiagram(genDiagramVisible);
        updateGenSituation();
    }


    @Override
    public boolean isGenDiagramVisible() {
        return genDiagramVisible;
    }


    @Override
    public int[] getGeneticSituation() {
        return genSituation;
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
        MatrixAnimal tempAnimal;
        double minDistance, currentDistance;
        minDistance = w * h;
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                tempAnimalList = posMapAnimals.getSector(i, j);
                for (int k = tempAnimalList.size() - 1; k >= 0; k--) {
                    tempAnimal = (MatrixAnimal) tempAnimalList.get(k);
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


    public void interactWithNearbyAnimals(MatrixAnimal animal) {
        ArrayList<PosMapObjectYio> nearbyAnimals;
        nearbyAnimals = posMapAnimals.getSectorByPos(animal.x, animal.y);
        int c = 0;
        for (int z = nearbyAnimals.size() - 1; z >= 0 && c < LIMIT; z--) {
            if (animal.x < nearbyAnimals.get(z).x) {
                collideTwoAnimals(animal, (MatrixAnimal) nearbyAnimals.get(z));
                c++;
            }
        }

//        c = 0;
//        if (i < cacheWidth - 1)
//            for (int z = cacheAnimals[i+1][j].size() - 1; z >= 0 && c < LIMIT; z--) {
//                collideTwoAnimals(animal, cacheAnimals[i + 1][j].get(z));
//                c++;
//            }
//
//        c = 0;
//        if (j < locMatrixHeight - 1)
//            for (int z = cacheAnimals[i][j+1].size() - 1; z >= 0 && c < LIMIT; z--) {
//                collideTwoAnimals(animal, cacheAnimals[i][j + 1].get(z));
//                c++;
//            }
//
//        c = 0;
//        if (i < cacheWidth - 1 && j < locMatrixHeight - 1)
//            for (int z = cacheAnimals[i+1][j+1].size() - 1; z >= 0 && c < LIMIT; z--) {
//                collideTwoAnimals(animal, cacheAnimals[i + 1][j + 1].get(z));
//                c++;
//            }
    }


    public void interactWithNearbyCorpses(MatrixAnimal animal, MatrixAiAnimal aiAnimal) {
        ArrayList<PosMapObjectYio> nearbyCorpses;
        int xIndex, yIndex;
        xIndex = (int) (animal.x / cacheCellSize);
        yIndex = (int) (animal.y / cacheCellSize);
        for (int i = xIndex - INTERACT_CELL_OFFSET; i <= xIndex + INTERACT_CELL_OFFSET; i++) {
            for (int j = yIndex - INTERACT_CELL_OFFSET; j <= yIndex + INTERACT_CELL_OFFSET; j++) {
                if (i < 0 || j < 0 || i > cacheWidth - 1 || j > cacheHeight - 1) continue;
                nearbyCorpses = posMapCorpses.getSector(i, j);
                int c = 0;
                for (int z = nearbyCorpses.size() - 1; z >= 0 && c < LIMIT; z--) {
                    aiAnimal.addToNearbyCorpses((Corpse) nearbyCorpses.get(z));
                    c++;
                }
            }
        }
    }


    @Override
    public FactorYio getSelectionFactor() {
        return selectionFactor;
    }


    void collideAnimals() {
//        lastTimeCollidedAnimals = currentTime;
        for (MatrixAnimal animal : animals) {
//            if (random.nextDouble() < 0.25) // optimization
            interactWithNearbyAnimals(animal);
        }
    }


    public void updateGenSituation() {
        for (int i = 0; i < genSituation.length; i++) {
            genSituation[i] = 0;
        }

        for (MatrixAnimal animal : animals) {
            for (Integer integer : animal.genList) {
                genSituation[integer]++;
            }
        }
    }


    void collideTwoAnimals(MatrixAnimal one, MatrixAnimal two) {
        if (Math.abs(one.x - two.x) > one.getCollisionRadius() + two.getCollisionRadius()) return;
        if (Math.abs(one.y - two.y) > one.getCollisionRadius() + two.getCollisionRadius()) return;
        double d = 0.5 * (one.getCollisionRadius() + two.getCollisionRadius()) - distanceBetweenObjects(one, two);
        if (d > 0) {
            double a = YioGdxGame.angle(one.x, one.y, two.x, two.y);
            one.relocate(-0.25 * d * Math.cos(a), -0.25 * d * Math.sin(a));
            two.relocate(0.25 * d * Math.cos(a), 0.25 * d * Math.sin(a));
        }
    }


    public void updateTemperature() {
        temperature = 0.5 + temperatureAmplitude * 0.4 * Math.sin(timeStep / 10000d); // 10000d

//        temperatureDelta *= 0.5;
//        double changeSpeed = 0.0125;
//        temperatureDelta += -changeSpeed / 2 + changeSpeed * gameController.random.nextDouble();
//        temperature += temperatureDelta;
//        if (temperature > 1) {
//            temperature = 1 - 0.05 * gameController.random.nextDouble();
//        }
//        if (temperature < 0) {
//            temperature = 0.05 * gameController.random.nextDouble();
//        }
    }


    void moveObjects() {
        for (Corpse corpse : corpses) corpse.move();
//        if (selectedSubject != null && cameraFixedOnSelectedAnimal) {
//            tempSelY = selectedSubject.y;
//            tempSelX = selectedSubject.x;
//        }
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
//        if (currentTime - lastTimeCollidedAnimals > 200) collideAnimals();
//        if (selectedSubject != null && cameraFixedOnSelectedAnimal) {
//            camDx = (float)(selectedSubject.x - tempSelX);
//            camDy = (float)(selectedSubject.y - tempSelY);
//        }
    }


    void moveChecks() {
        if (checkToRemoveCountDown < 2) {
            checkToRemoveStuff();
            checkToRemoveCountDown = checkToRemoveDelay;
        } else checkToRemoveCountDown--;

        for (int i = herbs.size() - 1; i >= 0; i--) herbs.get(i).checkToSpawnHerb();

        if (snapshotCountDown < 2) {
            gameController.dataStorage.takeSnapshot();
            snapshotCountDown = snapshotDelay;
        } else snapshotCountDown--;

        if (genDiagramVisible) {
            if (updateGenSituationCountDown == 0) {
                updateGenSituation();
                updateGenSituationCountDown = 5 * gameController.yioGdxGame.speedMultiplier;
            } else updateGenSituationCountDown--;
        }
    }


    void moveGrass() {
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
        return true;
    }


    @Override
    public boolean isModelBasic() {
        return false;
    }


    public void destroyCorpse(Corpse corpse) {
        corpse.setValid(false);
        corpses.remove(corpse);
        posMapCorpses.removeObject(corpse);
    }


    public void destroyHerb(Herb herb) {
        herbs.remove(herb);
        posMapHerbs.removeObject(herb);
    }


    void destroyAnimal(MatrixAnimal animal) {
        animals.remove(animal);
        posMapAnimals.removeObject(animal);
        addCorpse(animal, 0.5 * animal.bodySize / animal.mass);
    }


    private void addCorpse(MatrixAnimal animal, double r) {
        ListIterator iterator = corpses.listIterator();
        Corpse corpse = new Corpse(animal, r, 0.3);
        corpse.factorYio.setValues(0.8, 0);
        corpse.factorYio.beginSpawning(4, 1);
        iterator.add(corpse);
        posMapCorpses.addObject(corpse);
    }


    @Override
    public void spawnGrass(Herb herb1, Herb herb2) {
        if (getCacheGrassByPos(0.5 * (herb1.x + herb2.x), 0.5 * (herb1.y + herb2.y))) return;
        grass.add(new Grass(herb1, herb2, gameController));
        setCacheGrassByPos(true, 0.5 * (herb1.x + herb2.x), 0.5 * (herb1.y + herb2.y));
    }


    public boolean canSpawnHerb(double x, double y) {
        if (posMapHerbs.getSectorByPos(x, y).size() > 0) return false;
        return true;
//        int count = 0;
//        for (int i=index1-1; i<=index1+1; i++) {
//            for (int j=index2-1; j<=index2+1; j++) {
//                if (getCacheHerbsByIndex(i, j).size() > 0) count++;
//            }
//        }
//        return count < YioGdxGame.MAX_HERBS_IN_ONE_PLACE;
    }


    @Override
    public Herb spawnHerb(double posX, double posY) {
        if (posX < 0 || posY < 0 || posX > gameController.boundWidth || posY > gameController.boundHeight) return null;
        if (!canSpawnHerb(posX, posY)) return null;
        ListIterator iterator = herbs.listIterator();
        Herb herb = new Herb(posX, posY, 0.02f * gameController.w, this);
        iterator.add(herb);
        posMapHerbs.addObject(herb);
        return herb;
    }


    public void addAnimal(MatrixAnimal animal) {
        if (animals.size() > ANIMAL_QUANTITY_LIMIT) return;
        ListIterator iterator = animals.listIterator();
        iterator.add(animal);
        posMapAnimals.addObject(animal);
    }


    @Override
    public void moveAlways() {
        if (selectedSubject != null) selectionFactor.move();
    }


    public MatrixAnimal spawnRandomAnimal(float posX, float posY) {
        return spawnAnimal(posX, posY);
    }


    public MatrixAnimal spawnAnimal(float posX, float posY) {
        if (posX < 0 || posY < 0 || posX > gameController.boundWidth || posY > gameController.boundHeight) return null;
        ListIterator iterator = animals.listIterator();
        MatrixAnimal animal = new MatrixAnimal(posX, posY, this);
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
        spawnAnimal((float) posX, (float) posY);
    }


    public int getColumnSum(int columnIndex) {
        int sum = 0;
        for (int j = 0; j < numberOfGenes; j++) {
            sum += genRelationMatrix[columnIndex][j];
        }
        return sum;
    }


    public int getIndexOfBiggestColumn() {
        int index = 0;
        int max = -9000;
        for (int i = 0; i < numberOfGenes; i++) {
            if (getColumnSum(i) > max) {
                max = getColumnSum(i);
                index = i;
            }
        }

        return index;
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


    public int getIndexOfSmallestColumn() {
        int index = 0;
        int min = 9000;
        for (int i = 0; i < numberOfGenes; i++) {
            if (getColumnSum(i) < min) {
                min = getColumnSum(i);
                index = i;
            }
        }

        return index;
    }


    public int getRandomIntExceptOne(int upLim, int exceptNumber) {
        int number;
        while (true) {
            number = gameController.random.nextInt(upLim);
            if (number != exceptNumber) return number;
        }
    }


    public void setGenMatrixElement(int value, int i, int j) {
        genRelationMatrix[i][j] = value;
        genRelationMatrix[j][i] = -value;
    }


    private void balanceMatrix() {
        while (true) {
            int minColumnIndex = getIndexOfSmallestColumn();
            int maxColumnIndex = getIndexOfBiggestColumn();
            if (getColumnSum(maxColumnIndex) - getColumnSum(minColumnIndex) < 2) break;
            int minRowIndex = getRandomIntExceptOne(numberOfGenes, minColumnIndex);
            setGenMatrixElement(genRelationMatrix[minColumnIndex][minRowIndex] + 1, minColumnIndex, minRowIndex);
            int maxRowIndex = getRandomIntExceptOne(numberOfGenes, maxColumnIndex);
            setGenMatrixElement(genRelationMatrix[maxColumnIndex][maxRowIndex] - 1, maxColumnIndex, maxRowIndex);
        }

        slightlyChangeMatrix();
    }


    private void slightlyChangeMatrix() {
        int x = random.nextInt(numberOfGenes);
        for (int i = 0; i < 3; i++) {
            int y = getRandomIntExceptOne(numberOfGenes, x);
            setGenMatrixElement(genRelationMatrix[x][y] + 1, x, y);
        }
    }


    public void generateGenes(int numberOfGenes) {
        this.numberOfGenes = numberOfGenes;
        if (gameController.yioGdxGame.menuController.sliders.get(4).getCurrentRunnerIndex() == 1) { // random matrix
            randomizeGenMatrix();
        } else { // read from file
            readGenMatrixFromFile();
        }

        showMatrix("Gen matrix", genRelationMatrix);
        yioGdxGame.menuController.showInfoAboutClosedSystems(getDebugClosedSystems());
//        showPossibleClosedSystems();
//        showMatrixForTex(getNumberOfGenes());

        genSituation = new int[numberOfGenes];
        updateGenSituationCountDown = 0;

        mutationRate = gameController.yioGdxGame.menuController.sliders.get(1).getCurrentRunnerIndex();
        mutationRate /= 100d;

        double diagWidth = 0.04 * gameController.w * (numberOfGenes + 1) + 0.006 * gameController.w * (numberOfGenes - 1);
        gameController.yioGdxGame.gameView.diagramPos = new Rect(gameController.w - diagWidth, 0, diagWidth, 0.4 * gameController.h);
    }


    void mirrorGenMatrix() {
        for (int i = 0; i < numberOfGenes; i++) {
            for (int j = 0; j < numberOfGenes; j++) {
                genRelationMatrix[i][j] = - genRelationMatrix[i][j];
            }
        }
    }


    void showPossibleClosedSystems() {
        YioGdxGame.say("");
        YioGdxGame.say("Possible closed systems:");
        ArrayList<MatrixAnimal> combinations = getAllPossibleCombinations();
        countGeneticPowerOfCombinations(combinations);

        ArrayList<DebugClosedSystem> closedSystems = getDebugClosedSystems(combinations);

        for (DebugClosedSystem closedSystem : closedSystems) {
            YioGdxGame.say(closedSystem.getDebugCount() + ": " + closedSystem.one.genList + " - " + closedSystem.two.genList);
        }
    }


    private ArrayList<DebugClosedSystem> getDebugClosedSystems() {
        ArrayList<MatrixAnimal> combinations = getAllPossibleCombinations();
        countGeneticPowerOfCombinations(combinations);
        return getDebugClosedSystems(combinations);
    }


    private ArrayList<DebugClosedSystem> getDebugClosedSystems(ArrayList<MatrixAnimal> combinations) {
        ArrayList<DebugClosedSystem> closedSystems = new ArrayList<>();

        for (int i = 0; i < combinations.size(); i++) {
            MatrixAnimal one = combinations.get(i);
            for (int j = i + 1; j < combinations.size(); j++) {
                MatrixAnimal two = combinations.get(j);

                if (canBeClosedSystem(one, two)) {
                    DebugClosedSystem closedSystem = new DebugClosedSystem(one, two);
                    closedSystems.add(closedSystem);
                }
            }
        }

        Collections.sort(closedSystems);
        return closedSystems;
    }


    private void countGeneticPowerOfCombinations(ArrayList<MatrixAnimal> combinations) {
        for (MatrixAnimal combination : combinations) {
            combination.debugCount = 0;
            for (MatrixAnimal sub : combinations) {
                if (compareAnimalsGenetically(combination, sub) > 0)
                    combination.debugCount++;
                if (compareAnimalsGenetically(combination, sub) < 0)
                    combination.debugCount--;
            }
        }
    }


    private ArrayList<MatrixAnimal> getAllPossibleCombinations() {
        // Створюємо список наборів атрибутів
        ArrayList<MatrixAnimal> combinations = new ArrayList<>();
        boolean bits[];
        // Циклом перебираємо всі числа від 1 до 2^n
        for (int k = ((int) Math.pow(2, numberOfGenes)) - 1; k > 0; k--) {
            // Переводимо число у двійковий код
            bits = getBits(k, numberOfGenes);

            // Створюємо тварину з набором атрибутів
            MatrixAnimal comb = new MatrixAnimal(0, 0, this);
            comb.genList = new ArrayList<>();
            for (int i = 0; i < bits.length; i++) {
                if (bits[i]) {
                    comb.genList.add(i);
                }
                comb.updateGenActiveArray();
            }

            if (comb.genList.size() < 5)
                combinations.add(comb);
        }

        return combinations;
    }


    private static boolean[] getBits(int input, int digitNumber) {
        boolean[] bits = new boolean[digitNumber];
        for (int i = digitNumber - 1; i >= 0; i--) {
            bits[i] = (input & (1 << i)) != 0;
        }
        return bits;
    }


    String arrayToString(ArrayList<Integer> array) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Integer integer : array) {
            stringBuilder.append(integer + " ");
        }
        return stringBuilder.toString();
    }


    private void readGenMatrixFromFile() {
        ArrayList<String> lines = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(Gdx.files.internal("matrix.txt").reader());
            while (true) {
                String line = br.readLine();
                if (line == null) break;
                lines.add(line);
            }
            br.close();
        } catch (Exception e) {
            YioGdxGame.say("Problem when reading matrix from file:");
            e.printStackTrace();
            yioGdxGame.menuController.createExceptionReport(e);
        }

        numberOfGenes = 0;
        StringTokenizer stringTokenizer = new StringTokenizer(lines.get(0), " ");
        while (stringTokenizer.hasMoreTokens()) {
            stringTokenizer.nextToken();
            numberOfGenes++;
        }
        YioGdxGame.say("number of genes = " + numberOfGenes);

        genRelationMatrix = new int[numberOfGenes][numberOfGenes];

        int i = 0, j = 0;
        for (int k = 0; k < lines.size(); k++) {
            StringTokenizer tokenizer = new StringTokenizer(lines.get(k), " ");
            i = 0;
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                genRelationMatrix[i][j] = Integer.valueOf(token);
                i++;
            }
            j++;
        }
    }


    void showGenCombs() {
        ArrayList<MatrixAnimal> combs = new ArrayList<>();

        boolean foundSame;
        for (MatrixAnimal animal : animals) {
            foundSame = false;
            for (MatrixAnimal comb : combs) {
                if (booleanArraysAreEqual(comb.genActive, animal.genActive)) {
                    comb.debugCount++;
                    foundSame = true;
                    break;
                }
            }

            if (!foundSame) {
                combs.add(animal);
                animal.debugCount = 1;
            }
        }

        YioGdxGame.say("");
        YioGdxGame.say("gen combs:");
        for (MatrixAnimal comb : combs) {
            YioGdxGame.say(comb.debugCount + ": " + comb.genList);
        }
    }


    boolean canBeClosedSystem(MatrixAnimal one, MatrixAnimal two) {
        if (compareAnimalsGenetically(one, two) != 0) return false;

//        if (one.genList.size() > 1 && two.genList.size() > 1) return false;

        for (int i = 0; i < numberOfGenes; i++) {
            if (one.genActive[i] && two.genActive[i]) return false;
        }

        if (isDangerousForClosedSystem(one, two, one)) return false;
        if (isDangerousForClosedSystem(one, two, two)) return false;

        for (MatrixAnimal neutralMuta : getNeutralMutations(one, two)) {
            if (isDangerousForClosedSystem(one, two, neutralMuta)) return false;
        }

        return true;
    }


    ArrayList<MatrixAnimal> getNeutralMutations(MatrixAnimal one, MatrixAnimal two) {
        ArrayList<MatrixAnimal> neutralMutations = new ArrayList<>();

        for (MatrixAnimal muta : getAllPossibleMutations(one)) {
            if (compareAnimalsGenetically(one, muta) == 0 && compareAnimalsGenetically(two, muta) == 0)
                neutralMutations.add(muta);
        }

        for (MatrixAnimal muta : getAllPossibleMutations(two)) {
            if (compareAnimalsGenetically(one, muta) == 0 && compareAnimalsGenetically(two, muta) == 0)
                neutralMutations.add(muta);
        }

        return neutralMutations;
    }


    boolean isDangerousForClosedSystem(MatrixAnimal one, MatrixAnimal two, MatrixAnimal mutator) {
        for (MatrixAnimal muta : getAllPossibleMutations(mutator)) {
            if (compareAnimalsGenetically(muta, one) > 0 && compareAnimalsGenetically(muta, two) >= 0) return true;
            if (compareAnimalsGenetically(muta, one) >= 0 && compareAnimalsGenetically(muta, two) > 0) return true;
        }
        return false;
    }


    ArrayList<MatrixAnimal> getAllPossibleMutations(MatrixAnimal src) {
        ArrayList<MatrixAnimal> allMutations = new ArrayList<>();

        for (int i = 0; i < numberOfGenes; i++) {
            MatrixAnimal muta = new MatrixAnimal(0, 0, this);
            muta.genList = new ArrayList<>();
            for (int j = 0; j < numberOfGenes; j++) {
                if (i == j) continue;
                if (src.genActive[j]) muta.genList.add(j);
            }
            if (!src.genActive[i]) {
                muta.genList.add(i);
            }
            muta.updateGenActiveArray();
            allMutations.add(muta);
        }

        return allMutations;
    }


    boolean booleanArraysAreEqual(boolean a[], boolean b[]) {
        if (a.length != b.length) return false;

        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) return false;
        }

        return true;
    }


    public void exportMatrixToClipboard() {
        showGenCombs();
        StringBuffer stringBuffer = new StringBuffer();

        for (int j = 0; j < numberOfGenes; j++) {
            for (int i = 0; i < numberOfGenes; i++) {
                stringBuffer.append(genRelationMatrix[i][j] + " ");
            }
            stringBuffer.append("\n");
        }

        Clipboard clipboard = Gdx.app.getClipboard();
        clipboard.setContents(stringBuffer.toString());
    }


    private void randomizeGenMatrix() {
        genRelationMatrix = new int[numberOfGenes][numberOfGenes];
        for (int j = 0; j < numberOfGenes; j++) {
            for (int i = 0; i < numberOfGenes; i++) {
                if (i == j) {
                    genRelationMatrix[i][j] = 0;
                } else if (i > j) {
                    genRelationMatrix[i][j] = gameController.random.nextInt(11) - 5;
                } else {
                    genRelationMatrix[i][j] = -genRelationMatrix[j][i];
                }
            }
        }
    }


    private void showMatrixSums(int matrix[][]) {
        int sum[] = new int[matrix.length];

        int currentSum = 0;
        for (int i = 0; i < matrix.length; i++) {
            currentSum = 0;
            for (int j = 0; j < matrix.length; j++) {
                currentSum += matrix[i][j];
            }
            sum[i] = currentSum;
        }

        System.out.print("sum: ");
        for (int i = 0; i < sum.length; i++) {
            System.out.print(sum[i] + " ");
        }
        YioGdxGame.say("");
    }


    private void showMatrix(String name, int matrix[][]) {
        YioGdxGame.say("");
        YioGdxGame.say(name + ":");
        for (int j = 0; j < matrix.length; j++) {
            for (int i = 0; i < matrix[0].length; i++) {
                if (matrix[i][j] >= 0) System.out.print(" ");
                System.out.print(matrix[i][j] + " ");
            }
            YioGdxGame.say("");
        }
    }


    private void showMatrixForTex(int numberOfGenes) {
        YioGdxGame.say("matrix:");
        YioGdxGame.say("\\left(");
        YioGdxGame.say("\\begin{array}{rrrrrrrrrr}");
        for (int j = 0; j < numberOfGenes; j++) {
            for (int i = 0; i < numberOfGenes; i++) {
                System.out.print(" " + genRelationMatrix[i][j] + " ");
                if (i != numberOfGenes - 1) System.out.print("&");
            }
            YioGdxGame.say(" \\\\");
        }
        YioGdxGame.say("\\end{array}");
        YioGdxGame.say("\\right)");
    }


    public int[][] getGenRelationMatrix() {
        return genRelationMatrix;
    }


    public int getNumberOfGenes() {
        return numberOfGenes;
    }


    public int compareTwoGenes(int genIndex1, int genIndex2) {
        return genRelationMatrix[genIndex1][genIndex2];
    }


    public int compareAnimalsGenetically(MatrixAnimal one, MatrixAnimal two) {
        int sum = 0;
        for (Integer gen1 : one.genList) {
            for (Integer gen2 : two.genList) {
                sum += compareTwoGenes(gen1, gen2);
            }
        }
        return sum;
    }
}
