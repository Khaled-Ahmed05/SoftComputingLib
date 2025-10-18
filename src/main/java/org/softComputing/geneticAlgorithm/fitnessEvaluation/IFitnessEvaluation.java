package org.softComputing.geneticAlgorithm.fitnessEvaluation;

public interface IFitnessEvaluation<T> {
    double evaluate(T chromosome);
}
