package org.softComputing.geneticAlgorithm;

import java.util.Arrays;
import java.util.Comparator;

public class Population {
    public Individual[] individuals;

    public Population(int size, int genomeLength) {
        individuals = new Individual[size];
        for (int i = 0; i < size; i++) individuals[i] = new Individual(genomeLength);
    }

    public int size() { return individuals.length; }

    public Individual getBest() {
        return Arrays.stream(individuals).max(Comparator.naturalOrder()).orElse(null);
    }

    public Individual getWorst() {
        return Arrays.stream(individuals).min(Comparator.naturalOrder()).orElse(null);
    }

    public void sortByFitnessDescending() {
        Arrays.sort(individuals, (a,b) -> Double.compare(b.fitness, a.fitness));
    }
}

