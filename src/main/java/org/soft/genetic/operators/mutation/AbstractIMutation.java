package org.soft.genetic.operators.mutation;

import java.util.*;

public abstract class AbstractIMutation<T> implements IMutation<T> {
    protected final Random random = new Random();
    protected final double mutationRate;

    protected AbstractIMutation(double mutationRate) {
        if (mutationRate < 0 || mutationRate > 1)
            throw new IllegalArgumentException("Mutation rate must be between 0 and 1.");
        this.mutationRate = mutationRate;
    }

    protected boolean shouldMutate() {
        return random.nextDouble() < mutationRate;
    }

    protected void validateChromosome(List<T> chromosome) {
        if (chromosome == null || chromosome.isEmpty())
            throw new IllegalArgumentException("Chromosome cannot be null or empty.");
    }
}
