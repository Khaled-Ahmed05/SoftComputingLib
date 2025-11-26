package org.soft.genetic.fitnessEvaluation;

public interface IFitnessEvaluation<T> {
    double evaluate(T chromosome);
}
