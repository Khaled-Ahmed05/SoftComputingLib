package org.softComputing.geneticAlgorithm.operators.replacement;

import java.util.*;
import java.util.stream.IntStream;

public class ElitistReplacement<T> implements IReplacement<T> {

    private final int eliteCount;

    public ElitistReplacement(int eliteCount) {
        if (eliteCount < 0)
            throw new IllegalArgumentException("Elite count must be non-negative.");
        this.eliteCount = eliteCount;
    }

    @Override
    public List<List<T>> replace(List<List<T>> currentPopulation,
                                 List<List<T>> offspring,
                                 List<Double> currentFitness,
                                 List<Double> offspringFitness) {

        int popSize = currentPopulation.size();
        List<List<T>> nextGeneration = new ArrayList<>(popSize);

        // Sort current population indices by fitness descending
        List<Integer> eliteIndices = IntStream.range(0, currentFitness.size())
                .boxed()
                .sorted((i, j) -> Double.compare(currentFitness.get(j), currentFitness.get(i)))
                .limit(eliteCount)
                .toList();

        // Sort offspring indices by fitness descending
        List<Integer> bestOffspringIndices = IntStream.range(0, offspringFitness.size())
                .boxed()
                .sorted((i, j) -> Double.compare(offspringFitness.get(j), offspringFitness.get(i)))
                .toList();

        // Add elites
        for (int i : eliteIndices) {
            nextGeneration.add(new ArrayList<>(currentPopulation.get(i)));
        }

        // Fill remaining slots with top offspring
        int remaining = popSize - eliteCount;
        for (int i = 0; i < remaining && i < bestOffspringIndices.size(); i++) {
            nextGeneration.add(new ArrayList<>(offspring.get(bestOffspringIndices.get(i))));
        }

        return nextGeneration;
    }
}
