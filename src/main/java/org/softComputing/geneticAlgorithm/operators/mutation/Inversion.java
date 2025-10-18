package org.softComputing.geneticAlgorithm.operators.mutation;

import java.util.*;

public class Inversion<T> extends AbstractIMutation<T> {

    public Inversion(double mutationRate) {
        super(mutationRate);
    }

    @Override
    public List<T> mutate(List<T> chromosome) {
        validateChromosome(chromosome);

        List<T> mutated = new ArrayList<>(chromosome);

        if (shouldMutate() && chromosome.size() > 1) {
            int idx1 = random.nextInt(chromosome.size());
            int idx2 = random.nextInt(chromosome.size());

            int start = Math.min(idx1, idx2);
            int end = Math.max(idx1, idx2);

            // reverse
            while (start < end) {
                T temp = mutated.get(start);
                mutated.set(start, mutated.get(end));
                mutated.set(end, temp);
                start++;
                end--;
            }
        }

        return mutated;
    }
}
