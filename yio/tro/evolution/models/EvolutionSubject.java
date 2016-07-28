package yio.tro.evolution.models;

import yio.tro.evolution.GameObject;

import java.util.ArrayList;

/**
 * Created by ivan on 10.01.2015.
 */
public abstract class EvolutionSubject extends GameObject {
    double age;
    public String name;
    double birthMass;


    public EvolutionSubject() {

    }


    public abstract String getOtherInfo();


    public abstract String getGeneticCode();
}
