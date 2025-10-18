package org.softComputing.geneticAlgorithm.operators.replacement;

import java.util.*;
import java.util.stream.IntStream;

public class SteadyStateReplacement<T> implements IReplacement<T> {

    private final int numOfReplacedGenes; // Number of individuals to replace

    public SteadyStateReplacement(int numOfReplacedGenes) {
        if (numOfReplacedGenes <= 0)
            throw new IllegalArgumentException("k must be positive.");
        this.numOfReplacedGenes = numOfReplacedGenes;
    }

    @Override
    public List<List<T>> replace(List<List<T>> currentPopulation,
                                 List<List<T>> offspring,
                                 List<Double> currentFitness,
                                 List<Double> offspringFitness) {

        List<List<T>> nextGeneration = new ArrayList<>(currentPopulation);

        // Sort current population indices by fitness ascending ( the worst first )
        List<Integer> worstIndices = IntStream.range(0, currentFitness.size())
                .boxed()
                .sorted(Comparator.comparingDouble(currentFitness::get))
                .limit(numOfReplacedGenes)
                .toList();

        // Sort offspring indices by fitness descending ( the best first )
        List<Integer> bestOffspringIndices = IntStream.range(0, offspringFitness.size())
                .boxed()
                .sorted((i, j) -> Double.compare(offspringFitness.get(j), offspringFitness.get(i)))
                .limit(numOfReplacedGenes)
                .toList();

        for (int i = 0; i < Math.min(numOfReplacedGenes, worstIndices.size()); i++) {
            nextGeneration.set(worstIndices.get(i),
                    new ArrayList<>(offspring.get(bestOffspringIndices.get(i))));
        }

        return nextGeneration;
    }
}
