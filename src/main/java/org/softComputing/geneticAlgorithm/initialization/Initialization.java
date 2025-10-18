package org.softComputing.geneticAlgorithm.initialization;

import java.util.*;

public class Initialization implements IInitialization<List<Character>> {
    private final int chromosomeLength;
    private final List<Character> alphabet;
    private final Random random = new Random();

    public Initialization(int chromosomeLength, List<Character> alphabet) {
        if (chromosomeLength <= 0)
            throw new IllegalArgumentException("Chromosome length must be positive.");
        if (alphabet == null || alphabet.isEmpty())
            throw new IllegalArgumentException("Alphabet cannot be null or empty.");

        this.chromosomeLength = chromosomeLength;
        this.alphabet = alphabet;
    }

    @Override
    public List<List<Character>> initializePopulation(int populationSize) {
        if (populationSize <= 0)
            throw new IllegalArgumentException("Population size must be positive.");

        List<List<Character>> population = new ArrayList<>();

        for (int i = 0; i < populationSize; i++) {
            List<Character> chromosome = new ArrayList<>();
            for (int j = 0; j < chromosomeLength; j++) {
                chromosome.add(alphabet.get(random.nextInt(alphabet.size())));
            }
            population.add(chromosome);
        }

        return population;
    }
}
