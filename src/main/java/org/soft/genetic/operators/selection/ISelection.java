package org.soft.genetic.operators.selection;

import java.util.List;

public interface ISelection<T> {
    List<T> select(List<T> population, List<Double> fitnessValues, int numParents);
}
