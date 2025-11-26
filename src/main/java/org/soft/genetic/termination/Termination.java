package org.soft.genetic.termination;

import java.util.Collections;
import java.util.List;

public class Termination<T> implements ITermination<T>{

    private final int maxGenerations;

    public Termination(List<T> target, int maxGenerations) {
        if (target == null || target.isEmpty())
            throw new IllegalArgumentException("Target string cannot be null or empty.");
        if (maxGenerations <= 0)
            throw new IllegalArgumentException("Maximum generations must be positive.");
        this.maxGenerations = maxGenerations;
    }

    @Override
    public boolean shouldTerminate(List<T> population, List<Double> fitnessValues, int currentGeneration) {
        if (population == null || population.isEmpty() || fitnessValues == null || fitnessValues.isEmpty())
            throw new IllegalArgumentException("Population and fitness values cannot be null or empty.");

        // Get the best fitness in the current generation
        double bestFitness = Collections.max(fitnessValues);

        // Stop if perfect fitness is achieved
        if (bestFitness >= 1.0) {
            System.out.println("\n✅ Target string perfectly matched!");
            return true;
        }

        // Stop if we reached the generation limit
        if (currentGeneration >= maxGenerations) {
            System.out.println("\n❌ Reached maximum number of generations (" + maxGenerations + ").");
            return true;
        }

        return false;
    }
}
