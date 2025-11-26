package org.soft.genetic.operators.replacement;

import java.util.List;

public interface IReplacement<T> {
    List<List<T>> replace(List<List<T>> currentPopulation,
                          List<List<T>> offspring,
                          List<Double> currentFitness,
                          List<Double> offspringFitness);
}
