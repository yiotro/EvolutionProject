package yio.tro.evolution.models;

import yio.tro.evolution.Storage3xTexture;

/**
 * Created by ivan on 30.03.2016.
 */
public abstract class Animal extends EvolutionSubject{

    public double bodySize;
    public int legState;
    public double angle;


    public abstract float getViewRadius();


    public abstract Storage3xTexture getBodyTexture();


    public abstract Storage3xTexture getNoseTexture();


    public abstract Storage3xTexture getSpotTexture();


    public abstract boolean hasTail();


    public abstract boolean hasNose();


    public abstract boolean getDisease();


    public abstract double getDiseasePower();
}
