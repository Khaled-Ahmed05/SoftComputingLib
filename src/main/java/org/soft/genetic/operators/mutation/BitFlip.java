package org.soft.genetic.operators.mutation;

import java.util.*;

public class BitFlip extends AbstractIMutation<Boolean> {

    public BitFlip(double mutationRate) {
        super(mutationRate);
    }

    @Override
    public List<Boolean> mutate(List<Boolean> chromosome) {
        validateChromosome(chromosome);

        List<Boolean> mutated = new ArrayList<>(chromosome);
        for (int i = 0; i < mutated.size(); i++) {
            if (shouldMutate()) {
                mutated.set(i, !mutated.get(i));
            }
        }
        return mutated;
    }
}

