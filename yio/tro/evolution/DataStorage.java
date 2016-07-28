package yio.tro.evolution;

import yio.tro.evolution.models.Animal;
import yio.tro.evolution.models.BasicAnimal;
import yio.tro.evolution.plot.PlotFactory;

import java.util.ArrayList;

/**
 * Created by ivan on 16.10.2015.
 */
public class DataStorage {

    public static final int DISTRIBUTION_COUNT = 50;
    ArrayList<Integer> animalsListSize, movePrefSmoothed, temperatureHistory;
    ArrayList<Integer> foodPrefDistribution, fightPrefDistribution, movePrefDistribution;
    int foodCount[], fightCount[], moveCount[], moveSmoothCount[];
    ArrayList<int[]> moveSmoothHistory;
    GameController gameController;


    public DataStorage(GameController gameController) {
        this.gameController = gameController;
        animalsListSize = new ArrayList<>();
        movePrefSmoothed = new ArrayList<>();
        temperatureHistory = new ArrayList<>();

        foodPrefDistribution = new ArrayList<>();
        fightPrefDistribution = new ArrayList<>();
        movePrefDistribution = new ArrayList<>();
        moveSmoothHistory = new ArrayList<>();

        foodCount = new int[DISTRIBUTION_COUNT + 1];
        fightCount = new int[DISTRIBUTION_COUNT + 1];
        moveCount = new int[DISTRIBUTION_COUNT + 1];
        moveSmoothCount = new int[DISTRIBUTION_COUNT + 1];
        for (int i = 0; i < DISTRIBUTION_COUNT + 1; i++) {
            foodPrefDistribution.add(0);
            fightPrefDistribution.add(0);
            movePrefDistribution.add(0);
            movePrefSmoothed.add(0);
        }
    }


    public void takeSnapshot() {
        EvolutionModel evolutionModel = gameController.evolutionModel;
        animalsListSize.add(evolutionModel.getAnimals().size());
        temperatureHistory.add((int) (200 + 300 * evolutionModel.temperature));
        if (evolutionModel.isModelBasic()) {
            updateFoodDistribution();
            updateFightDistribution();
            updateMoveDistribution();
            updateMovePrefSmoothed();
        }
    }


    private void updateMovePrefSmoothed() {
        int tempCount[] = new int[moveCount.length];

        for (int i = 0; i < moveCount.length; i++) {
            tempCount[i] = moveCount[i];
            moveSmoothCount[i] = 0;
        }

        moveSmoothHistory.add(tempCount);

        int N = moveSmoothHistory.size() - 1;
        if (N < 5) return;
        if (N > 500) N = 500;
        for (int k = moveSmoothHistory.size() - 1; k >= moveSmoothHistory.size() - N; k--) {
            for (int i = 0; i < moveSmoothCount.length; i++) {
                moveSmoothCount[i] += moveSmoothHistory.get(k)[i];
            }
        }

        for (int i = 0; i < moveSmoothCount.length; i++) {
            moveSmoothCount[i] /= N;
            movePrefSmoothed.set(i, moveSmoothCount[i]);
        }
    }


    private void updateMoveDistribution() {
        for (int i = 0; i < moveCount.length; i++) {
            moveCount[i] = 0;
        }

        for (Animal animal : gameController.evolutionModel.getAnimals()) {
            BasicAnimal basicAnimal = (BasicAnimal) animal;
            int index = (int)(basicAnimal.movePref * DISTRIBUTION_COUNT);
            moveCount[index]++;
        }

        for (int i = 0; i < moveCount.length; i++) {
            movePrefDistribution.set(i, moveCount[i]);
        }
    }


    private void updateFightDistribution() {
        for (int i = 0; i < fightCount.length; i++) {
            fightCount[i] = 0;
        }

        for (Animal animal : gameController.evolutionModel.getAnimals()) {
            BasicAnimal basicAnimal = (BasicAnimal) animal;
            int index = (int)(basicAnimal.fightPref * DISTRIBUTION_COUNT);
            fightCount[index]++;
        }

        for (int i = 0; i < fightCount.length; i++) {
            fightPrefDistribution.set(i, fightCount[i]);
        }
    }


    private void updateFoodDistribution() {
        for (int i = 0; i < foodCount.length; i++) {
            foodCount[i] = 0;
        }

        for (Animal animal : gameController.evolutionModel.getAnimals()) {
            BasicAnimal basicAnimal = (BasicAnimal) animal;
            int index = (int)(basicAnimal.foodPref * DISTRIBUTION_COUNT);
            foodCount[index]++;
        }

        for (int i = 0; i < foodCount.length; i++) {
            foodPrefDistribution.set(i, foodCount[i]);
        }
    }


    public int getPlotColorByIndex(int plotIndex) {
        switch (plotIndex) {
            default:
            case 0:
                return PlotFactory.PLOT_COLOR_BLACK;
            case 1:
                return PlotFactory.PLOT_COLOR_BLACK;
            case 2:
                return PlotFactory.PLOT_COLOR_MAGENTA;
            case 3:
                return PlotFactory.PLOT_COLOR_MAGENTA;
            case 4:
                return PlotFactory.PLOT_COLOR_RED;
            case 5:
                return PlotFactory.PLOT_COLOR_GREEN;
        }
    }


    public String getPlotNameByIndex(int plotIndex) {
        switch (plotIndex) {
            default:
            case 0:
                return "All animals";
            case 1:
                return "MovePrefSmooth";
            case 2:
                return "Temperature";
            case 3:
                return "Food Pref";
            case 4:
                return "Def pref";
            case 5:
                return "Move pref";
        }
    }


    public ArrayList<Integer> getDataList(int plotIndex) {
        switch (plotIndex) {
            default:
            case 0:
                return animalsListSize;
            case 1:
                return movePrefSmoothed;
            case 2:
                return temperatureHistory;
            case 3:
                return foodPrefDistribution;
            case 4:
                return fightPrefDistribution;
            case 5:
                return movePrefDistribution;
        }
    }
}
