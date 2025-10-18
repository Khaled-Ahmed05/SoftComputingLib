package org.softComputing.geneticAlgorithm.operators.mutation;

import java.util.*;

public class UniformM extends AbstractIMutation<Character> {
    private final double lowerBound;
    private final double upperBound;

    public UniformM(double mutationRate, double lowerBound, double upperBound) {
        super(mutationRate);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public List<Character> mutate(List<Character> chromosome) {
        validateChromosome(chromosome);

        List<Character> mutated = new ArrayList<>(chromosome);

        for (int i = 0; i < mutated.size(); i++) {
            if (shouldMutate()) {
                // Map char to numeric value
                double Xi = mutated.get(i);

                double deltaLxi = Xi - lowerBound;
                double deltaUxi = upperBound - Xi;

                double r1 = random.nextDouble();
                double delta = (r1 <= 0.5) ? deltaLxi : deltaUxi;
                double r2 = random.nextDouble() * delta;

                double newXi = (r1 <= 0.5) ? (Xi - r2) : (Xi + r2);
                newXi = Math.max(lowerBound, Math.min(upperBound, newXi)); // clamp

                // Map back to char
                mutated.set(i, (char) Math.round(newXi));
            }
        }

        // values in boundary that would cause solution infeasibility
        for (int i = 0; i < mutated.size(); i++) {
            char gene = mutated.get(i);

            if ((int) gene > 90 && (int) gene < 97 || (int) gene < 65 && (int) gene > 32) {
                char newGene;
                do {
                    newGene = (char) (lowerBound + (int) (Math.random() * (upperBound - lowerBound + 1)));
                } while (newGene >= 91 && newGene <= 96);

                mutated.set(i, newGene);
            }
        }

        return mutated;
    }
}
