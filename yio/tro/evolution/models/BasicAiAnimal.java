package yio.tro.evolution.models;

import yio.tro.evolution.*;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ivan on 30.09.2015.
 */
public class BasicAiAnimal {

    public static final int DROP_TARGET_DELAY = 300; // 10 sec
    public static final int CHANGE_FOOD_PREF_DELAY = 450;
    public static final int DEFAULT_PICK_DELAY = 15;
    public static final int FOOD_CARNIVORES = 0;
    public static final int FOOD_HERBIVORES = 1;

    boolean readyToChangeFoodPreference;
    int pickCorpseCountDown, pickHerbCountDown, changeFoodPrefCountDown;
    int pickAnimalCountDown, dropTargetCountDown, currentFoodPreference;
    double lastRotation;

    BasicModel basicModel;
    Random random;
    ArrayList<Corpse> nearbyCorpses;
    public Corpse targetCorpse;
    BasicAnimal animal, targetAnimal;
    Herb targetHerb;
    PMCoor indexPoint;


    public BasicAiAnimal(BasicModel basicModel, BasicAnimal animal) {
        this.basicModel = basicModel;
        this.animal = animal;
        nearbyCorpses = new ArrayList<>();
        random = new Random();
        indexPoint = new PMCoor();
        changeFoodPrefCountDown = CHANGE_FOOD_PREF_DELAY;
    }


    public void decide() {
        // Якщо дуже довго не вдається знайти їжу
        // то тварина змінює свою поведінку
        movePrefCountDown(); 

        if (currentFoodPreference == FOOD_HERBIVORES) {
            // поведінка травоїдної тварини
            behaveAsHerbivores();
        } else {
            // поведінка м'ясоїдної тварини
            behaveAsCarnivores();
        }
    }


    private void movePrefCountDown() {
        if (changeFoodPrefCountDown == 0) {
            changeFoodPreference();
        } else changeFoodPrefCountDown--;
    }


    private void behaveAsHerbivores() {
        // Тварина перевіряє чи нема поряд з нею рослин
        checkForEatableHerbs();

        stopAnimalRandomRotation();

        if (targetHerb == null) { // якщо нема поряд рослин
            // спробувати змінити свою поведінку
            if (checkToChangeFoodPref()) return;

            animal.isRotatingRandomly = true;

            // набрати максимальну швидкість
            if (animal.speed < animal.maxSpeed)
                animal.accelerate();

        } else { // якщо поряд є рослина
            decideAboutTargetHerb();
        }
    }


    private void behaveAsCarnivores() {
        // пошук трупів поблизу
        checkForCorpsesNearby();

        stopAnimalRandomRotation();

        if (targetCorpse == null) { // якщо труп знайти не вдалося
            // спробувати змінити свою поведінку
            if (checkToChangeFoodPref()) return;
            // якщо поведінка не змінилась, то
            // спробувати знайти собі жертву
            searchForTargetAnimal();
        } else { // якщо труп знайдено
            decideAboutTargetCorpse();
        }
    }


    private void stopAnimalRandomRotation() {
        if (animal.isRotatingRandomly)
            animal.isRotatingRandomly = false;
    }


    private boolean checkToChangeFoodPref() {
        if (readyToChangeFoodPreference) {
            changeFoodPreference();
            return true;
        }
        return false;
    }


    private void searchForTargetAnimal() {
        checkForAnimalsNearby();

        if (targetAnimal == null) {
            animal.accelerate();
            animal.isRotatingRandomly = true;
        } else { // target animal not null
            decideAboutTargetAnimal();
        }
    }


    public void changeFoodPreference() {
        readyToChangeFoodPreference = false;
        changeFoodPrefCountDown = CHANGE_FOOD_PREF_DELAY;

        if (random.nextDouble() < animal.foodPref) {
            currentFoodPreference = FOOD_CARNIVORES;
        }
         else {
            currentFoodPreference = FOOD_HERBIVORES;
        }
    }


    private void checkForAnimalsNearby() {
        if (targetAnimal == null || !targetAnimal.isValid()) {
            if (pickAnimalCountDown < 2) {
                pickTargetAnimal();
                pickAnimalCountDown = DEFAULT_PICK_DELAY;
            } else pickAnimalCountDown--;
        }
    }


    private void checkForEatableHerbs() {
        if (targetHerb == null || !targetHerb.isValid()) {
            if (pickHerbCountDown < 2) {
                pickTargetHerb();
                pickHerbCountDown = DEFAULT_PICK_DELAY;
            } else pickHerbCountDown--;
        }
    }


    private void checkForCorpsesNearby() {
        if (targetCorpse == null || !targetCorpse.isValid()) {
            if (pickCorpseCountDown < 2) {
                pickTargetCorpse();
                pickCorpseCountDown = DEFAULT_PICK_DELAY;
            } else pickCorpseCountDown--;
        }
    }


    private void decideAboutTargetAnimal() {
        if (dropTargetCountDown < 2) {
            targetAnimal = null;
            return;
        } else dropTargetCountDown--;
        animal.setDirectionToObject(targetAnimal); // target animal changes position every turn
        animal.tryToBiteAnimal(targetAnimal);
        if (BasicModel.canInteract(animal, targetAnimal) && targetAnimal.speed < animal.speed) animal.decelerate();
        else animal.accelerate();
    }


    private void decideAboutTargetHerb() {
        // TargetHerb - це рослина на яку націлилась тварина
        // Якщо рослина виявилась мертвою, то нічого не робити
        if (checkIfTargetHerbIsValid()) return;

        // якщо відстань велика, то націлитись на рослину
        if (fastDistance(animal, targetHerb) > 2 * BasicModel.INTERACT_DISTANCE) {
            setDirectionToObjectIfNecessary(animal, targetHerb);
        }

        // спроба вкусити рослину
        boolean eaten = animal.tryToEatHerb(targetHerb);

        // якщо рослину вдалося вкусити, то тварина сповільнюється
        if (    !eaten &&
                fastDistance(animal, targetHerb) < 2 * BasicModel.INTERACT_DISTANCE &&
                animal.speed > 0.2 * animal.maxSpeed) {
            animal.decelerate();
        } else if (eaten) {
            animal.decelerate();
        } else if (animal.speed < 0.1 * animal.maxSpeed) {
            animal.accelerate();
        }
    }


    private boolean checkIfTargetHerbIsValid() {
        if (!targetHerb.isValid()) {
            targetHerb = null;
            return true;
        }
        return false;
    }


    private void decideAboutTargetCorpse() {
        setDirectionToObjectIfNecessary(animal, targetCorpse);
        boolean eaten = animal.tryToEatCorpse(targetCorpse);
        if (    !eaten &&
                BasicModel.distanceBetweenObjects(animal, targetCorpse) < 2 * BasicModel.INTERACT_DISTANCE &&
                animal.speed > 0.7 * animal.maxSpeed) animal.decelerate();
        else if (eaten) animal.decelerate();
        else animal.accelerate();
    }


    void pickTargetAnimal() {
        ArrayList<PosMapObjectYio> nearbyAnimals;
        BasicAnimal a;
        double tempDistance, minDistance = basicModel.w + basicModel.h;
        basicModel.posMapAnimals.transformCoorToIndex(animal.x, animal.y, indexPoint);

        for (int i = indexPoint.x - BasicModel.INTERACT_CELL_OFFSET; i <= indexPoint.x + BasicModel.INTERACT_CELL_OFFSET; i++) {
            for (int j = indexPoint.y - BasicModel.INTERACT_CELL_OFFSET; j <= indexPoint.y + BasicModel.INTERACT_CELL_OFFSET; j++) {

                nearbyAnimals = basicModel.posMapAnimals.getSector(i, j);
                if (nearbyAnimals == null) continue;
                int c = 0;
                for (int z = nearbyAnimals.size() - 1; z >= 0 && c < 3; z--) {
                    a = (BasicAnimal) nearbyAnimals.get(z);
                    if (a == animal) continue; // do not attack himself
                    if (!animal.canAttack(a)) continue; // check if can attack
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

//        if (targetAnimal instanceof BasicAnimal && random.nextDouble() > 0.5) {
//            targetAnimal = null;
//            return;
//        }

        // target animal successfully picked
        dropTargetCountDown = DROP_TARGET_DELAY;
    }


    void pickTargetHerb() {
        Herb herb;
        for (PosMapObjectYio herbObject : basicModel.posMapHerbs.getSectorByPos(animal.x, animal.y)) {
            herb = (Herb) herbObject;
            if (herb.isValid()) {
                targetHerb = herb;
                readyToChangeFoodPreference = true;
                return;
            }
        }
    }


    void pickTargetCorpse() {
        nearbyCorpses.clear();

        basicModel.gatherNearbyCorpses(animal);

        if (nearbyCorpses.size() == 0) {
            targetCorpse = null;
            return;
        }

        double minDistance = basicModel.w + basicModel.h;
        double currentDistance;

        for (Corpse nearbyCorpse : nearbyCorpses) {
            if (nearbyCorpse.mass <= 0) continue;
            currentDistance = BasicModel.distanceBetweenObjects(animal, nearbyCorpse);
            if (currentDistance < minDistance) {
                minDistance = currentDistance;
                targetCorpse = nearbyCorpse;
            }
        }

        if (targetCorpse != null) {
            readyToChangeFoodPreference = true;
        }
    }


    public void addToNearbyCorpses(Corpse corpse) {
        nearbyCorpses.add(corpse);
    }


    public GameObject getTarget() {
        if (targetCorpse != null) return targetCorpse;
        else return targetAnimal;
    }


    void setDirectionToObjectIfNecessary(BasicAnimal animal, GameObject gameObject) {
        lastRotation = animal.setDirectionToObject(gameObject);
    }


    public static double fastDistance(GameObject one, GameObject two) {
        return Math.abs(one.x - two.x) + Math.abs(one.y - two.y);
    }
}
