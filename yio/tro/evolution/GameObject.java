package yio.tro.evolution;

import yio.tro.evolution.models.Animal;
import yio.tro.evolution.models.EvolutionSubject;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by ivan on 20.09.2015.
 */
public abstract class GameObject extends PosMapObjectYio {

    public boolean valid;
    public double mass;


    public void setValid(boolean valid) {
        this.valid = valid;
    }


    public boolean isValid() {
        return valid;
    }


    public void decreaseMass() {
        mass--;
        if (mass < 0) mass = 0;
    }
}
