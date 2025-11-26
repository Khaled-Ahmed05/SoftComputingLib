package org.soft.genetic.operators.replacement;

import java.util.List;

public class GenerationalReplacement<T> implements IReplacement<T> {

    @Override
    public List<List<T>> replace(List<List<T>> currentPopulation,
                                 List<List<T>> offspring,
                                 List<Double> currentFitness,
                                 List<Double> offspringFitness) {
        return offspring;
    }
}

