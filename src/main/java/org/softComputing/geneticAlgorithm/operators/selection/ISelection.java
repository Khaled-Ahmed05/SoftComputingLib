package org.softComputing.geneticAlgorithm.operators.selection;

import org.softComputing.geneticAlgorithm.fitnessEvaluation.IFitnessEvaluation;

import java.util.List;

public interface ISelection<T> {
    List<T> select(List<T> population, List<Double> fitnessValues, int numParents);
}
