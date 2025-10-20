package org.softComputing.geneticAlgorithm.initialization;

import java.util.List;

public interface IInitialization<T> {
    List<List<T>> initializePopulation(int populationSize);
}
