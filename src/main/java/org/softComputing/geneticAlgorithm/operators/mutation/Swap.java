package org.softComputing.geneticAlgorithm.operators.mutation;

import java.util.*;

public class Swap<T> extends AbstractIMutation<T> {

    public Swap(double mutationRate) {
        super(mutationRate);
    }

    @Override
    public List<T> mutate(List<T> chromosome) {
        validateChromosome(chromosome);

        List<T> mutated = new ArrayList<>(chromosome);
        if (shouldMutate()) {
            int i = random.nextInt(mutated.size());
            int j = random.nextInt(mutated.size());

            while (i == j) {
                j = random.nextInt(mutated.size());
            }

            Collections.swap(mutated, i, j);
        }
        return mutated;
    }
}

