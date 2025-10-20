package org.softComputing.geneticAlgorithm.termination;

import java.util.List;

public interface ITermination<T> {
    boolean shouldTerminate(List<T> population, List<Double> fitnessValues, int currentGeneration);
}
