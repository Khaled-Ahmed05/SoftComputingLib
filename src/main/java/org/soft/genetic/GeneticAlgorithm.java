package org.soft.genetic;

import org.soft.genetic.operators.selection.ISelection;
import org.soft.genetic.operators.crossover.ICrossover;
import org.soft.genetic.operators.mutation.IMutation;
import org.soft.genetic.operators.replacement.IReplacement;
import org.soft.genetic.initialization.IInitialization;
import org.soft.genetic.fitnessEvaluation.IFitnessEvaluation;
import org.soft.genetic.termination.ITermination;

import java.util.*;
import java.util.stream.IntStream;

public class GeneticAlgorithm<T> {

    // Configuration
    private final int populationSize;
    private final int maxGenerations;
    private final int numOfParents;
    private final double crossoverRate;

    // Operators
    private final IInitialization<T> initialization;
    private final IFitnessEvaluation<List<T>> fitnessEvaluator;
    private final ISelection<List<T>> selectionOperator;
    private final ICrossover<T> crossoverOperator;
    private final IMutation<T> mutationOperator;
    private final IReplacement<T> replacementOperator;
    private final ITermination<List<T>> terminationCondition;

    // State
    private List<List<T>> population;
    private List<Double> fitnessValues;
    private int generation = 0;
    private final Random random = new Random();

    public GeneticAlgorithm(
            int populationSize,
            int maxGenerations,
            int numOfParents,
            double crossoverRate,
            IInitialization<T> initialization,
            IFitnessEvaluation<List<T>> fitnessEvaluator,
            ISelection<List<T>> selectionOperator,
            ICrossover<T> crossoverOperator,
            IMutation<T> mutationOperator,
            IReplacement<T> replacementOperator,
            ITermination<List<T>> terminationCondition
    ) {
        if (populationSize <= 0) throw new IllegalArgumentException("populationSize must be > 0");
        if (maxGenerations <= 0) throw new IllegalArgumentException("maxGenerations must be > 0");
        if (crossoverRate < 0 || crossoverRate > 1) throw new IllegalArgumentException("crossoverRate must be in [0,1]");

        this.populationSize = populationSize;
        this.maxGenerations = maxGenerations;
        this.numOfParents = numOfParents;
        this.crossoverRate = crossoverRate;
        this.initialization = Objects.requireNonNull(initialization);
        this.fitnessEvaluator = Objects.requireNonNull(fitnessEvaluator);
        this.selectionOperator = Objects.requireNonNull(selectionOperator);
        this.crossoverOperator = Objects.requireNonNull(crossoverOperator);
        this.mutationOperator = Objects.requireNonNull(mutationOperator);
        this.replacementOperator = Objects.requireNonNull(replacementOperator);
        this.terminationCondition = Objects.requireNonNull(terminationCondition);
    }

    public List<T> run() {
        initializePopulation();
        evaluatePopulation();

        for (generation = 1; generation <= maxGenerations; generation++) {

            if (terminationCondition.shouldTerminate(population, fitnessValues, generation)) break;

            // 1) Selection
            List<List<T>> parents = selectionOperator.select(population, fitnessValues, numOfParents);
            if (parents == null || parents.size() < 2)
                throw new IllegalStateException("Selection must return at least 2 parents.");

            // 2) Crossover
            List<List<T>> offspring = new ArrayList<>(populationSize);
            int pairs = parents.size() / 2;

            for (int i = 0; i < pairs; i++) {
                List<T> p1 = parents.get(i * 2);
                List<T> p2 = parents.get(i * 2 + 1);

                List<List<T>> children = (random.nextDouble() < crossoverRate)
                        ? crossoverOperator.crossover(p1, p2)
                        : Arrays.asList(deepCopy(p1), deepCopy(p2));

                for (List<T> child : children) {
                    offspring.add(deepCopy(child));
                    if (offspring.size() >= populationSize) break;
                }

                if (offspring.size() >= populationSize) break;
            }

            // fill if less than population size
            int parentIndex = 0;
            while (offspring.size() < populationSize) {
                offspring.add(deepCopy(parents.get(parentIndex % parents.size())));
                parentIndex++;
            }

            // 3) Mutation
            List<List<T>> mutatedOffspring = new ArrayList<>(offspring.size());
            for (List<T> child : offspring) {
                List<T> mutated = mutationOperator.mutate(child);

                mutated = repairChromosome(mutated);

                mutatedOffspring.add(mutated);
            }

            // 4) Replacement
            List<Double> offspringFitness = evaluatePopulation(mutatedOffspring);
            List<List<T>> nextPopulation = replacementOperator.replace(population, mutatedOffspring, fitnessValues, offspringFitness);

            if (nextPopulation == null || nextPopulation.size() != populationSize) {
                nextPopulation = new ArrayList<>(mutatedOffspring);
                while (nextPopulation.size() < populationSize)
                    nextPopulation.add(deepCopy(nextPopulation.getFirst()));
                if (nextPopulation.size() > populationSize)
                    nextPopulation = nextPopulation.subList(0, populationSize);
            }

            population = nextPopulation;
            fitnessValues = evaluatePopulation(population);

            // --- Logging ---
            double bestFitness = Collections.max(fitnessValues);
            double worstFitness = Collections.min(fitnessValues);
            System.out.printf("\nGeneration %d — best: %.6f | worst: %.6f%n", generation, bestFitness, worstFitness);

            List<Integer> sortedIndices = IntStream.range(0, fitnessValues.size())
                    .boxed()
                    .sorted((i, j) -> Double.compare(fitnessValues.get(j), fitnessValues.get(i)))
                    .toList();

            System.out.println("Top 3 individuals:");
            for (int i = 0; i < Math.min(3, sortedIndices.size()); i++) {
                int idx = sortedIndices.get(i);
                System.out.printf("  #%d fitness: %.6f | individual: %s%n", i + 1, fitnessValues.get(idx), population.get(idx));
            }

            if (terminationCondition.shouldTerminate(population, fitnessValues, generation)) break;
        }

        return getBestIndividual();
    }

    // Initialize population
    private void initializePopulation() {
        population = initialization.initializePopulation(populationSize);
        if (population == null || population.size() != populationSize)
            throw new IllegalStateException("Initialization must produce exactly populationSize individuals.");
    }

    private void evaluatePopulation() {
        fitnessValues = evaluatePopulation(population);
    }

    private List<Double> evaluatePopulation(List<List<T>> pop) {
        List<Double> fitness = new ArrayList<>(pop.size());
        for (List<T> individual : pop) {
            fitness.add(fitnessEvaluator.evaluate(individual));
        }
        return fitness;
    }

    private List<T> getBestIndividual() {
        int bestIndex = IntStream.range(0, fitnessValues.size())
                .boxed()
                .max(Comparator.comparingDouble(fitnessValues::get))
                .orElse(0);
        return population.get(bestIndex);
    }

    private List<T> deepCopy(List<T> original) {
        return new ArrayList<>(original);
    }

    public int getGeneration() {
        return generation;
    }

    @SuppressWarnings("unchecked")
    private List<T> repairChromosome(List<T> chromosome) {
        if (chromosome == null || chromosome.isEmpty()) return chromosome;

        List<T> repaired = new ArrayList<>(chromosome.size());
        for (T gene : chromosome) {
            if (gene instanceof Integer) {
                int value = (Integer) gene;
                // Clamp between 0 and 9
                if (value < 0) value = 0;
                if (value > 9) value = 9;
                repaired.add((T) Integer.valueOf(value));
            } else {
                repaired.add(gene);
            }
        }
        return repaired;
    }
}
