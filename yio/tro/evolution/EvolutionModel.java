package yio.tro.evolution;

import yio.tro.evolution.factor_yio.FactorYio;
import yio.tro.evolution.models.Animal;
import yio.tro.evolution.models.EvolutionSubject;
import yio.tro.evolution.models.Grass;
import yio.tro.evolution.models.Herb;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ivan on 30.03.2016.
 */
public abstract class EvolutionModel {

    public YioGdxGame yioGdxGame;
    public GameController gameController;
    public int w, h;
    public Random random;
    public double cacheHerbsCellSize;
    public float boundWidth, boundHeight;
    public double cacheCellSize, mutationRate, temperature;
    public PosMapYio posMapHerbs;
    public ArrayList<Grass> grass;


    public EvolutionModel(YioGdxGame yioGdxGame, GameController gameController) {
        this.yioGdxGame = yioGdxGame;
        this.gameController = gameController;
        random = new Random();
    }


    public abstract void moveAlways();


    public abstract void move();


    public abstract void prepare();


    public abstract void notifyAboutMovement();


    public abstract void addAnimal(float x, float y);


    public abstract void addHerb(float x, float y);


    public abstract EvolutionSubject getSelectedSubject();


    public abstract boolean isModelMatrix();


    public abstract boolean isModelBasic();


    public abstract FactorYio getSelectionFactor();


    public abstract boolean isGenDiagramVisible();


    public abstract void switchDiagram();


    public abstract ArrayList<? extends Animal> getAnimals();


    public abstract ArrayList<Herb> getHerbs();


    public abstract ArrayList<Corpse> getCorpses();


    public abstract int[] getGeneticSituation();


    public abstract void createField();


    public abstract Herb spawnHerb(double posX, double posY);


    public abstract void spawnGrass(Herb herb1, Herb herb2);


    public abstract void spawnManyAnimals(double posX, double posY);


    public abstract void touchDown(float tx, float ty);


    public abstract void touchDrag(float tx, float ty);


    public abstract void touchUp(float tx, float ty);
}
