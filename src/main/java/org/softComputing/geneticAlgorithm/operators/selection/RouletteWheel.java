package org.softComputing.geneticAlgorithm.operators.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RouletteWheel<T> implements ISelection<T> {

    @Override
    public List<T> select(List<T> population, List<Double> fitnessValues, int numParents) {
        List<T> selectedParents = new ArrayList<>();
        List<T> candidates = new ArrayList<>(population);
        List<Double> candidateFitness = new ArrayList<>(fitnessValues);
        Random random = new Random();

        for (int p = 0; p < numParents && !candidates.isEmpty(); p++) {

            // get fitness for current candidates
            double totalFitness = 0.0;
            for (double fitness : candidateFitness) {
                totalFitness += fitness;
            }

            // build cumulative fitness array
            double[] cumulativeFitness = new double[candidates.size()];
            double cumulativeSum = 0.0;
            for (int i = 0; i < candidates.size(); i++) {
                cumulativeSum += candidateFitness.get(i);
                cumulativeFitness[i] = cumulativeSum;
            }

            // "spin the wheel"
            double r = random.nextDouble() * totalFitness;
            int chosenIndex = 0;
            for (int i = 0; i < candidates.size(); i++) {
                if (r <= cumulativeFitness[i]) {
                    chosenIndex = i;
                    break;
                }
            }

            // add chosen parent and remove it from the candidate pool
            T chosen = candidates.remove(chosenIndex);
            selectedParents.add(chosen);
        }

        return selectedParents;
    }
}
