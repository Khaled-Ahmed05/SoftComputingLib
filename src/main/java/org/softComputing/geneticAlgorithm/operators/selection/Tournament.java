package org.softComputing.geneticAlgorithm.operators.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Tournament<T> implements ISelection<T> {
    private final int tournamentSize;

    public Tournament(int tournamentSize) {
        this.tournamentSize = tournamentSize;
    }

    @Override
    public List<T> select(List<T> population, List<Double> fitnessValues, int numParents) {

        List<T> selectedParents = new ArrayList<>();
        List<T> candidates = new ArrayList<>(population);
        List<Double> candidateFitness = new ArrayList<>(fitnessValues);
        Random random = new Random();

        for (int p = 0; p < numParents && !candidates.isEmpty(); p++) {
            // randomly pick {tournamentSize} individuals for the tournament
            List<Integer> tournamentIndices = new ArrayList<>();
            for (int i = 0; i < tournamentSize; i++) {
                tournamentIndices.add(random.nextInt(candidates.size()));
            }

            int bestIndex = tournamentIndices.getFirst();
            double bestFitness = candidateFitness.get(bestIndex);

            // find the best (highest fitness) individual
            for (int i = 1; i < tournamentIndices.size(); i++) {
                int idx = tournamentIndices.get(i);
                double fitness = candidateFitness.get(idx);
                if (fitness > bestFitness) {
                    bestFitness = fitness;
                    bestIndex = idx;
                }
            }

            selectedParents.add(candidates.remove(bestIndex));
            candidateFitness.remove(bestIndex);
        }

        return selectedParents;
    }
}
