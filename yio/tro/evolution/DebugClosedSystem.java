package yio.tro.evolution;

import yio.tro.evolution.models.MatrixAnimal;

public class DebugClosedSystem implements Comparable{

    public MatrixAnimal one, two;


    public DebugClosedSystem(MatrixAnimal one, MatrixAnimal two) {
        this.one = one;
        this.two = two;
    }


    @Override
    public int compareTo(Object another) {
        DebugClosedSystem a = (DebugClosedSystem) another;
        return a.getDebugCount() - getDebugCount();
    }


    public int getDebugCount() {
        return one.debugCount + two.debugCount;
    }
}
