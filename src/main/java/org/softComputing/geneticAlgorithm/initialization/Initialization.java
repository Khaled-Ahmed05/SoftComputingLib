package org.softComputing.geneticAlgorithm.initialization;

import java.util.*;

public class Initialization<T> implements IInitialization<T> {
    private final int chromosomeLength;
    private final List<T> alphabet;
    private final Random random = new Random();

    public Initialization(int chromosomeLength, List<T> alphabet) {
        if (chromosomeLength <= 0)
            throw new IllegalArgumentException("Chromosome length must be positive.");
        if (alphabet == null || alphabet.isEmpty())
            throw new IllegalArgumentException("Alphabet cannot be null or empty.");

        this.chromosomeLength = chromosomeLength;
        this.alphabet = alphabet;
    }

    @Override
    public List<List<T>> initializePopulation(int populationSize) {
        if (populationSize <= 0)
            throw new IllegalArgumentException("Population size must be positive.");

        List<List<T>> population = new ArrayList<>();

        for (int i = 0; i < populationSize; i++) {
            List<T> chromosome = new ArrayList<>();
            for (int j = 0; j < chromosomeLength; j++) {
                chromosome.add(alphabet.get(random.nextInt(alphabet.size())));
            }
            population.add(chromosome);
        }

        return population;
    }
}
