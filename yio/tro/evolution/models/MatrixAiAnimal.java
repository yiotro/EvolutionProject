package yio.tro.evolution.models;

import yio.tro.evolution.Corpse;
import yio.tro.evolution.GameObject;
import yio.tro.evolution.PosMapObjectYio;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ivan on 30.09.2015.
 */
public class MatrixAiAnimal {

    MatrixModel matrixModel;
    Random random;
    public static final int DEFAULT_PICK_DELAY = 15;
    int pickTargetCountDown;
    double lastRotation;
    ArrayList<Corpse> nearbyCorpses;
    public Corpse targetCorpse;
    MatrixAnimal targetAnimal;
    MatrixAnimal animal;
    int pickTargetAnimalCountDown, dropTargetCountDown;
    public static final int DROP_TARGET_DELAY = 300; // 10 sec



    public MatrixAiAnimal(MatrixModel matrixModel, MatrixAnimal animal) {
        this.matrixModel = matrixModel;
        this.animal = animal;
        nearbyCorpses = new ArrayList<>();
        random = new Random();
    }

    public void decide() {
        checkForEatableHerbs();
        checkForCorpsesNearby();
        animal.isRotatingRandomly = false;
        if (targetCorpse == null) { // no corpses nearby
            checkForAnimalsNearby();
            if (targetAnimal == null) {
                animal.accelerate();
                animal.isRotatingRandomly = true;
            } else { // target animal not null
                decideAboutTargetAnimal();
            }
        } else { // target corpse not null
            decideAboutTargetCorpse();
        }
    }

    private void checkForAnimalsNearby() {
        if (targetAnimal == null || !targetAnimal.isValid()) {
            if (pickTargetAnimalCountDown < 2) {
                pickTargetAnimal();
                pickTargetAnimalCountDown = DEFAULT_PICK_DELAY;
            } else pickTargetAnimalCountDown--;
        }
    }

    private void checkForEatableHerbs() {
        ArrayList<PosMapObjectYio> eatableHerbs = matrixModel.posMapHerbs.getSectorByPos(animal.x, animal.y);
        if (eatableHerbs.size() == 0) return;
        animal.tryToEatHerb((Herb) eatableHerbs.get(0));
    }

    private void checkForCorpsesNearby() {
        if (targetCorpse == null || !targetCorpse.isValid()) {
            if (pickTargetCountDown < 2) {
                pickTargetCorpse();
                pickTargetCountDown = DEFAULT_PICK_DELAY;
            } else pickTargetCountDown--;
        }
    }

    private void decideAboutTargetAnimal() {
        if (dropTargetCountDown < 2) {
            targetAnimal = null;
            return;
        } else dropTargetCountDown--;
        animal.setDirectionToObject(targetAnimal); // target animal changes position every turn
        animal.tryToBiteAnimal(targetAnimal);
        if (MatrixModel.canInteract(animal, targetAnimal) && targetAnimal.speed < animal.speed) animal.decelerate();
        else animal.accelerate();
    }

    private void decideAboutTargetCorpse() {
        setDirectionToObjectIfNecessary(animal, targetCorpse);
        boolean eaten = animal.tryToEatCorpse(targetCorpse);
        if (    !eaten &&
                MatrixModel.distanceBetweenObjects(animal, targetCorpse) < 2 * MatrixModel.INTERACT_DISTANCE &&
                animal.speed > 0.7 * animal.maxSpeed) animal.decelerate();
        else if (eaten) animal.decelerate();
        else animal.accelerate();
    }

    void pickTargetAnimal() {
        ArrayList<PosMapObjectYio> nearbyAnimals;
        MatrixAnimal a;
        int xIndex, yIndex;
        double tempDistance, minDistance = matrixModel.w * 2;
        xIndex = (int)(animal.x / matrixModel.cacheCellSize);
        yIndex = (int)(animal.y / matrixModel.cacheCellSize);
        for (int i = xIndex- MatrixModel.INTERACT_CELL_OFFSET; i<=xIndex+ MatrixModel.INTERACT_CELL_OFFSET; i++) {
            for (int j = yIndex- MatrixModel.INTERACT_CELL_OFFSET; j<=yIndex+ MatrixModel.INTERACT_CELL_OFFSET; j++) {
                nearbyAnimals = matrixModel.posMapAnimals.getSector(i, j);
                if (nearbyAnimals == null) continue;
                int c = 0;
                for (int z = nearbyAnimals.size() - 1; z >= 0 && c < 3; z--) {
                    a = (MatrixAnimal) nearbyAnimals.get(z);
                    if (a == animal) continue;
                    if (matrixModel.compareAnimalsGenetically(animal, a) <= 0) continue;
                    tempDistance = fastDistance(animal, a);
                    if (a.isValid() && tempDistance < minDistance) {
                        targetAnimal = a;
                        minDistance = tempDistance;
                    }
                    c++;
                }
            }
        }
        if (targetAnimal != null && !targetAnimal.isValid()) {
            targetAnimal = null;
            return;
        }
        if (targetAnimal instanceof MatrixAnimal && random.nextDouble() > 0.5) {
            targetAnimal = null;
            return;
        }
        dropTargetCountDown = DROP_TARGET_DELAY;
    }

    void pickTargetCorpse() {
        nearbyCorpses.clear();
        matrixModel.interactWithNearbyCorpses(animal, this);
        if (nearbyCorpses.size() == 0) {
            targetCorpse = null;
            return;
        }
        double minDistance = MatrixModel.distanceBetweenObjects(animal, nearbyCorpses.get(0));
        double currentDistance;
        Corpse nearestCorpse = nearbyCorpses.get(0);
        for (Corpse nearbyCorpse : nearbyCorpses) {
            currentDistance = MatrixModel.distanceBetweenObjects(animal, nearbyCorpse);
            if (currentDistance < minDistance) {
                minDistance = currentDistance;
                nearestCorpse = nearbyCorpse;
            }
        }
        targetCorpse = nearestCorpse;
        if (targetCorpse.mass <= 0) {
            targetCorpse = null;
        }
    }

    public void addToNearbyCorpses(Corpse corpse) {
        nearbyCorpses.add(corpse);
    }

    public GameObject getTarget() {
        if (targetCorpse != null) return targetCorpse;
        else return targetAnimal;
    }

    void setDirectionToObjectIfNecessary(MatrixAnimal animal, GameObject gameObject) {
        lastRotation = animal.setDirectionToObject(gameObject);
    }

    public static double fastDistance(GameObject one, GameObject two) {
        return Math.abs(one.x - two.x) + Math.abs(one.y - two.y);
    }
}
