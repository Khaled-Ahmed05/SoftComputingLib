package org.softComputing.geneticAlgorithm;

import org.softComputing.geneticAlgorithm.infeasbility.IInfeasibilityHandling;
import org.softComputing.geneticAlgorithm.infeasbility.InfeasibilityHandling;
import org.softComputing.geneticAlgorithm.operators.selection.ISelection;
import org.softComputing.geneticAlgorithm.operators.crossover.ICrossover;
import org.softComputing.geneticAlgorithm.operators.mutation.IMutation;
import org.softComputing.geneticAlgorithm.operators.replacement.IReplacement;
import org.softComputing.geneticAlgorithm.initialization.IInitialization;
import org.softComputing.geneticAlgorithm.fitnessEvaluation.IFitnessEvaluation;

import java.util.*;
import java.util.stream.IntStream;

public class GeneticAlgorithm<T> {

    // Configuration
    private final int populationSize;
    private final int maxGenerations;
    private final int numOfParents;
    private final double crossoverRate;
    private final double lowerBound;
    private final double upperBound;

    // Ops
    private final IInitialization<List<T>> initialization;
    private final IFitnessEvaluation<List<T>> fitnessEvaluator;
    private final ISelection<List<T>> selectionOperator;
    private final ICrossover<T> crossoverOperator;
    private final IMutation<T> mutationOperator;
    private final IReplacement<T> replacementOperator;
    private final Termination<List<T>> terminationCondition;

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
            IInitialization<List<T>> initialization,
            IFitnessEvaluation<List<T>> fitnessEvaluator,
            ISelection<List<T>> selectionOperator,
            ICrossover<T> crossoverOperator,
            IMutation<T> mutationOperator,
            double lowerBound,
            double upperBound,
            IReplacement<T> replacementOperator,
            Termination<List<T>> terminationCondition
    ) {
        if (populationSize <= 0) throw new IllegalArgumentException("populationSize must be > 0");
        if (maxGenerations <= 0) throw new IllegalArgumentException("maxGenerations must be > 0");
        if (crossoverRate < 0 || crossoverRate > 1) throw new IllegalArgumentException("crossoverRate must be in [0,1]");

        this.populationSize = populationSize;
        this.maxGenerations = maxGenerations;
        this.numOfParents = numOfParents;
        this.crossoverRate = crossoverRate;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;

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

            if (terminationCondition.shouldTerminate(population, fitnessValues, generation)) {
                break;
            }

            // 1) Selection
            List<List<T>> parents = selectionOperator.select(population, fitnessValues, numOfParents);

            if (parents == null || parents.size() < 2) {
                throw new IllegalStateException("Selection must return at least 2 parents.");
            }

            // 2) Crossover
            List<List<T>> offspring = new ArrayList<>(populationSize);

            int pairs = parents.size() / 2;
            for (int i = 0; i < pairs; i++) {
                List<T> p1 = parents.get(i*2);
                List<T> p2 = parents.get(i*2 + 1);

                List<List<T>> children;
                if (random.nextDouble() < crossoverRate) {
                    children = crossoverOperator.crossover(p1, p2);
                } else {
                    // No crossover = copy parents
                    children = Arrays.asList(deepCopy(p1), deepCopy(p2));
                }

                for (List<T> child : children) {
                    offspring.add(deepCopy(child));
                    if (offspring.size() >= populationSize) break;
                }

                if (offspring.size() >= populationSize) break;
            }

            int parentIndex = 0;
            while (offspring.size() < populationSize) {
                offspring.add(new ArrayList<>(parents.get(parentIndex % parents.size())));
                parentIndex++;
            }

            // 3) Mutation
            List<List<T>> mutatedOffspring = new ArrayList<>(offspring.size());
            for (List<T> child : offspring) {
                List<T> mutated = mutationOperator.mutate(child);

                // values in boundary that would cause solution infeasibility
                IInfeasibilityHandling<T> handling = new InfeasibilityHandling<>();
                mutated = handling.specialChars(mutated, lowerBound, upperBound);

                mutatedOffspring.add(mutated);
            }

            // 4) Replacement
            List<Double> offspringFitness = evaluatePopulation(mutatedOffspring);

            List<List<T>> nextPopulation = replacementOperator.replace(population, mutatedOffspring, fitnessValues, offspringFitness);

            if (nextPopulation == null || nextPopulation.size() != populationSize) {
                nextPopulation = new ArrayList<>(mutatedOffspring);

                if (nextPopulation.size() > populationSize) nextPopulation = nextPopulation.subList(0, populationSize);
                while (nextPopulation.size() < populationSize) nextPopulation.add(deepCopy(nextPopulation.getFirst()));
            }

            population = nextPopulation;
            fitnessValues = evaluatePopulation(population);

            // Log / statistics
            double bestFitness = Collections.max(offspringFitness);
            double worstFitness = Collections.min(offspringFitness);

            System.out.printf(
                    "\nGeneration %d — best: %.6f | worst: %.6f",
                    generation, bestFitness, worstFitness
            );


            System.out.println("\nTop " + 3 + " individuals:");
            List<Integer> sortedIndices = IntStream.range(0, fitnessValues.size())
                    .boxed()
                    .sorted((i, j) -> Double.compare(fitnessValues.get(j), fitnessValues.get(i)))
                    .toList();


//            System.out.println("Selected parents:");
//            for (List<T> parent : parents) {
//                System.out.println("  " + parent);
//            }
//
//            System.out.println("Offspring after crossover:");
//            for (List<T> child : offspring) {
//                System.out.println("  " + child);
//            }
//
//            System.out.println("Offspring after mutation:");
//            for (List<T> child : mutatedOffspring) {
//                System.out.println("  " + child);
//            }


            for (int i = 0; i < Math.min(3, sortedIndices.size()); i++) {
                int idx = sortedIndices.get(i);
                System.out.printf("  #%d fitness: %.6f | individual: %s%n", i+1, fitnessValues.get(idx), population.get(idx));
            }


            // Early termination check (in case replacement produced a perfect solution)
            if (terminationCondition.shouldTerminate(population, fitnessValues, generation)) {
                break;
            }
        }

        // return best individual
        return getBestIndividual();
    }

    private void initializePopulation() {
        population = initialization.initializePopulation(populationSize);
        if (population == null || population.size() != populationSize) {
            throw new IllegalStateException("Initialization must produce exactly populationSize individuals.");
        }
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

    // clone object not inner element (reference not values)
    private List<T> deepCopy(List<T> original) {
        return new ArrayList<>(original);
    }

    public int getGeneration() {
        return generation;
    }
}
