package yio.tro.evolution.models;

import yio.tro.evolution.EvolutionModel;
import yio.tro.evolution.GameObject;
import yio.tro.evolution.NameGenerator;
import yio.tro.evolution.YioGdxGame;
import yio.tro.evolution.factor_yio.FactorYio;

/**
 * Created by ivan on 11.11.2014.
 */
public class Herb extends GameObject {

    public EvolutionModel evolutionModel;
    public float radius, diameter, size;
    public int reproductionPotential;
    int reproduceCountDown, getRidOfCocoonCountDown, dropMassCountDown;
    FactorYio sizeFactor, cocoonFactor;
    int minReproduceDelay, maxReproduceDelay;
    boolean isCocoon;


    public Herb(double x, double y, float size, EvolutionModel evolutionModel) {
        super();
        this.x = (float) x;
        this.y = (float) y;
        mass = 30;
        this.size = size / (float) mass;
        this.evolutionModel = evolutionModel;
        radius = 0;
        diameter = 0;
        reproductionPotential = evolutionModel.random.nextInt(YioGdxGame.HERB_DEFAULT_REPRODUCE_POTENTIAL + 1);
        if (reproductionPotential == 0 && evolutionModel.random.nextDouble() < 0.3) reproductionPotential = 1;
        sizeFactor = new FactorYio();
        cocoonFactor = new FactorYio();
        cocoonFactor.setValues(0, 0);
        cocoonFactor.beginSpawning(1, 1);
        isCocoon = true;
        setValid(true);
        minReproduceDelay = 30;
        maxReproduceDelay = 40;
        getRidOfCocoonCountDown = 150 + (int) ((1 - evolutionModel.temperature) * 100);
    }


    public void move() {
        if (sizeFactor.get() < 1) {
            sizeFactor.move();
            updateRadius();
        }

        if (isCocoon) {
            if (cocoonFactor.get() < 1) cocoonFactor.move();
            if (getRidOfCocoonCountDown < 2) {
                getRidOfCocoon();
            } else getRidOfCocoonCountDown--;
        }

        checkToDropMass();
    }


    private void getRidOfCocoon() {
        isCocoon = false;
        sizeFactor.setValues(0.5, 0);
        sizeFactor.beginSpawning(1, 3);
        updateRadius();
        reproduceCountDown = 0;
    }


    private void checkToDropMass() {
        if (dropMassCountDown == 0) {
            decreaseMass();
            updateRadius();
            dropMassCountDown = 1000; // lower values can cause big influence
        } else dropMassCountDown--;
    }


    private void updateRadius() {
        radius = sizeFactor.get() * size * (float) mass;
        diameter = 2 * radius;
    }


    public float getCocoonRadius() {
        return 0.5f * cocoonFactor.get() * size * (float) mass;
    }


    public boolean isCocoon() {
        return isCocoon;
    }


    public void checkToSpawnHerb() {
        if (!isCocoon() && reproductionPotential > 0) {
            if (reproduceCountDown < 2) {
                reproduce();
            }
            reproduceCountDown--;
        }
    }


    public boolean isEatable() {
        return !isCocoon() && mass > 0;
    }


    @Override
    public boolean isValid() {
        return mass > 0;
    }


    void reproduce() {
        float rAngle, rRadius;
        rAngle = YioGdxGame.randomAngle();
        rRadius = 0.08f * evolutionModel.w;
        Herb spawnedHerb = evolutionModel.spawnHerb(x + rRadius * (float) Math.cos(rAngle), y + rRadius * (float) Math.sin(rAngle));
        if (spawnedHerb != null) {
            evolutionModel.spawnGrass(this, spawnedHerb);
            reproductionPotential--;
        }
        reproduceCountDown = minReproduceDelay + evolutionModel.random.nextInt(maxReproduceDelay - minReproduceDelay);
    }
}
