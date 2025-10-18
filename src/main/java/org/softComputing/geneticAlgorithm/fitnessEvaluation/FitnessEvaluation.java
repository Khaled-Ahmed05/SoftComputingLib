package org.softComputing.geneticAlgorithm.fitnessEvaluation;

import java.util.List;

public class FitnessEvaluation implements IFitnessEvaluation<List<Character>> {

    private final List<Character> target;

    public FitnessEvaluation(List<Character> target) {
        if (target == null || target.isEmpty())
            throw new IllegalArgumentException("Target string cannot be null or empty.");
        this.target = target;
    }

    @Override
    public double evaluate(List<Character> chromosome) {
        if (chromosome == null || chromosome.size() != target.size())
            throw new IllegalArgumentException("Chromosome length must match target length.");

        int matches = 0;

        for (int i = 0; i < target.size(); i++) {
            if (chromosome.get(i).equals(target.get(i))) {
                matches++;
            }
        }

        return (double) matches / target.size();
    }
}
