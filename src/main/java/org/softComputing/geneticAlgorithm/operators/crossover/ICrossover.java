package org.softComputing.geneticAlgorithm.operators.crossover;

import java.util.List;

public interface ICrossover<T> {
    List<List<T>> crossover(List<T> parent1, List<T> parent2);
}
