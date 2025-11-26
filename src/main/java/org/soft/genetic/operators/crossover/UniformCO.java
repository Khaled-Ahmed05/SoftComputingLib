package org.soft.genetic.operators.crossover;

import java.util.*;

public class UniformCO<T> extends AbstractICrossover<T> {
    private final double swapProbability;

    public UniformCO(double swapProbability) {
        if (swapProbability < 0 || swapProbability > 1)
            throw new IllegalArgumentException("Probability must be between 0 and 1.");
        this.swapProbability = swapProbability;
    }

    @Override
    public List<List<T>> crossover(List<T> parent1, List<T> parent2) {
        validateParents(parent1, parent2);

        int length = parent1.size();
        List<T> child1 = new ArrayList<>(parent1);
        List<T> child2 = new ArrayList<>(parent2);
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            if (random.nextDouble() < swapProbability) {
                T temp = child1.get(i);
                child1.set(i, child2.get(i));
                child2.set(i, temp);
            }
        }

        return Arrays.asList(child1, child2);
    }
}
