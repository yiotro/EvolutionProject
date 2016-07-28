package yio.tro.evolution;

import yio.tro.evolution.factor_yio.FactorYio;
import yio.tro.evolution.models.Animal;
import yio.tro.evolution.models.BasicAiAnimal;
import yio.tro.evolution.models.BasicAnimal;
import yio.tro.evolution.models.MatrixAnimal;

import java.util.Random;

/**
 * Created by ivan on 20.09.2015.
 */
public class Corpse extends GameObject{

    public boolean disease;
    public double r, rotateAngle, diseasePower;
    public FactorYio factorYio;

    public Corpse(Animal animal, double r, double massMultiplier) {
        x = animal.x;
        y = animal.y;
        this.r = r * 5;
        mass = massMultiplier * animal.mass;
        factorYio = new FactorYio();
        rotateAngle = YioGdxGame.random.nextDouble() * 2d * Math.PI;

        disease = animal.getDisease();
        if (disease) {
//            diseasePower = YioGdxGame.random.nextDouble();
            diseasePower = animal.getDiseasePower() + 0.01 * (2 * YioGdxGame.random.nextDouble() - 1);
            if (diseasePower > 1) diseasePower = 1;
            if (diseasePower < 0) diseasePower = 0;
        }
    }

    public boolean isEatable() {
        return mass > 0;
    }

    public void move() {
        factorYio.move();
    }

    public double getViewRadius() {
        return factorYio.get() * r * mass;
    }

    @Override
    public boolean isValid() {
        return mass > 0;
    }
}
