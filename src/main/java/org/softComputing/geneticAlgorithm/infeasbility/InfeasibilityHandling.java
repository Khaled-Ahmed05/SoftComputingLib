package org.softComputing.geneticAlgorithm.infeasbility;

import java.util.List;
import java.util.Random;

public class InfeasibilityHandling<T> implements IInfeasibilityHandling<T> {
    private final Random random = new Random();

    @Override
    public List<T> specialChars(List<T> mutated, double lowerBound, double upperBound) {
        for (int i = 0; i < mutated.size(); i++) {
            // Cast T to Character safely (only works if T = Character)
            char gene = (Character) mutated.get(i);

            // If gene is outside the allowed printable character ranges
            if ((gene > 90 && gene < 97) || (gene < 65 && gene > 32)) {
                char newGene;
                do {
                    newGene = (char) (lowerBound + (int) (random.nextDouble() * (upperBound - lowerBound + 1)));
                } while (newGene >= 91 && newGene <= 96); // skip forbidden range

                // Cast back to T
                mutated.set(i, (T) Character.valueOf(newGene));
            }
        }
        return mutated;
    }
}
