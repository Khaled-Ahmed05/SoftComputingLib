package org.soft;

import org.soft.genetic.GeneticAlgorithm;
import org.soft.genetic.termination.ITermination;
import org.soft.genetic.termination.Termination;
import org.soft.genetic.operators.crossover.UniformCO;
import org.soft.genetic.operators.selection.*;
import org.soft.genetic.operators.crossover.*;
import org.soft.genetic.operators.mutation.*;
import org.soft.genetic.operators.replacement.*;
import org.soft.genetic.initialization.*;
import org.soft.genetic.fitnessEvaluation.*;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // ------------------ Problem setup ------------------
        System.out.print("Enter your Password (Separate floats with spaces, e.g., Khaled12 3.3 !): ");
        String targetInput = scanner.nextLine();

        List<Object> target = parseInput(targetInput);

        int populationSize = 200;
        int maxGenerations = 500;
        int numOfParents = 8;
        double crossoverRate = 0.7;
        double mutationRate = 0.05;
        int tournamentSize = 2;
        double swapProbability = 0.5;
        int nPoints = 2;
        double lowerBound = 32;
        double upperBound = 122;
        int eliteCount = 2;
        int numOfReplacedGenes = 5;

        List<Object> alphabet = new ArrayList<>(Arrays.asList(
                'A','B','C','D','E','F','G','H','I','J','K','L','M',
                'N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
                'a','b','c','d','e','f','g','h','i','j','k','l','m',
                'n','o','p','q','r','s','t','u','v','w','x','y','z',
                1, 2, 3, 4, 5, 6, 7, 8, 9, 0, '!', '-', '@', '?', '_'
        ));
                for (int i = 0; i < 10; i++) {
            double value = i + Math.random();
            value = Math.round(value * 10.0) / 10.0;
            alphabet.add(value);
        }

        // ------------------ User input ------------------
        populationSize = getInt(scanner, "\nPopulation size", populationSize);
        maxGenerations = getInt(scanner, "Max generations", maxGenerations);
        numOfParents = getInt(scanner, "Number of parents", numOfParents);
        crossoverRate = getDouble(scanner, "Crossover rate", crossoverRate);
        mutationRate = getDouble(scanner, "Mutation rate", mutationRate);

        // ------------------ GA Operator Setup ------------------
        IInitialization<Object> initialization =
                new Initialization<>(target.size(), alphabet);

        IFitnessEvaluation<List<Object>> fitnessEvaluation =
                new FitnessEvaluation<>(target);

        // ---------- User input for operators ----------
        System.out.println("\nSelect Selection method:");
        System.out.println("1: Tournament");
        System.out.println("2: RouletteWheel");
        int selectionChoice = getInt(scanner, ">> Choice", 1);

        ISelection<List<Object>> selection;
        if(selectionChoice == 1) {
            tournamentSize = getInt(scanner, "Tournament size", tournamentSize);
            selection = new Tournament<>(tournamentSize);
        }
        else selection = new RouletteWheel<>();

        System.out.println("\nSelect Crossover method:");
        System.out.println("1: UniformCO");
        System.out.println("2: NPoint");
        System.out.println("3: Multipoint");
        int crossoverChoice = getInt(scanner, ">> Choice", 1);

        ICrossover<Object> crossover;
        if(crossoverChoice == 1) {
            swapProbability = getDouble(scanner, "Swap probability", swapProbability);
            crossover = new UniformCO<>(swapProbability);
        }
        else if(crossoverChoice == 2) {
            nPoints = getInt(scanner, "Number of crossover points", nPoints);
            crossover = new NPoint<>(nPoints);
        }
        else crossover = new Multipoint<>();

        System.out.println("\nSelect Mutation method:");
        System.out.println("1: UniformM");
        System.out.println("2: Swap");
        System.out.print("3: Inversion");
//        System.out.println("4: BitFlip");
        int mutationChoice = getInt(scanner, "\n>> Choice", 1);

        IMutation<Object> mutation;
        if(mutationChoice == 1) {
            lowerBound = getDouble(scanner, "Lower bound (ASCII)", lowerBound);
            upperBound = getDouble(scanner, "Upper bound (ASCII)", upperBound);
            mutation = new UniformM<>(mutationRate, lowerBound, upperBound);
        }
        else if (mutationChoice == 2) mutation = new Swap<>(mutationRate);
        else mutation = new Inversion<>(mutationRate);
//        else mutation = new BitFlip(mutationRate);

        System.out.println("\nSelect Replacement method:");
        System.out.println("1: ElitistReplacement");
        System.out.println("2: SteadyStateReplacement");
        System.out.println("3: GenerationalReplacement");
        int replacementChoice = getInt(scanner, ">> Choice", 2);

        IReplacement<Object> replacement;
        if(replacementChoice == 1) {
            eliteCount = getInt(scanner, "Elite count", eliteCount);
            replacement = new ElitistReplacement<>(eliteCount);
        }
        else if(replacementChoice == 2) {
            numOfReplacedGenes = getInt(scanner, "Number of replaced genes", numOfReplacedGenes);
            replacement = new SteadyStateReplacement<>(numOfReplacedGenes);
        }
        else replacement = new GenerationalReplacement<>();

        ITermination<List<Object>> termination = new Termination<>(Collections.singletonList(target), maxGenerations);

        // ------------------ GA Configuration ------------------
        GeneticAlgorithm<Object> ga = new GeneticAlgorithm<>(
                populationSize,
                maxGenerations,
                numOfParents,
                crossoverRate,
                initialization,
                fitnessEvaluation,
                selection,
                crossover,
                mutation,
                replacement,
                termination
        );

        // ------------------ Run the GA ------------------
        List<Object> bestIndividual = ga.run();

        // ------------------ Display config ------------------
        System.out.println("\nConfiguration:");
        System.out.println("Population Size: " + populationSize);
        System.out.println("Max Generations: " + maxGenerations);
        System.out.println("Crossover Rate: " + crossoverRate);
        System.out.println("Tournament Size: " + tournamentSize);
        System.out.println("Swap Probability: " + swapProbability);
        System.out.println("Crossover Points: " + nPoints);
        System.out.println("Mutation Rate: " + mutationRate);
        System.out.println("Lower Bound: " + lowerBound);
        System.out.println("Upper Bound: " + upperBound);
        System.out.println("Elite Count: " + eliteCount);
        System.out.println("Num of Replaced Genes: " + numOfReplacedGenes);

        // ------------------ Display result ------------------
        StringBuilder result = new StringBuilder();
        result.append('[');
        for (int i = 0; i < bestIndividual.size(); i++) {
            Object c = bestIndividual.get(i);
            result.append(c);
            if (i < bestIndividual.size() - 1) {
                result.append(", ");
            }
        }
        result.append(']');

        double finalFitness = fitnessEvaluation.evaluate(bestIndividual);
        System.out.println("\nTarget individual: " + target);
        System.out.println("Best individual  : " + result);
        System.out.printf("Final fitness    : %.3f%n", finalFitness);
        System.out.println("Total generations: " + ga.getGeneration());

        // ------------------ Display methods used ------------------
        System.out.print("\nMethods: ");
        System.out.print(selection.getClass().getSimpleName().replace("<>", "") + ", ");
        System.out.print(crossover.getClass().getSimpleName().replace("<>", "") + ", ");
        System.out.print(mutation.getClass().getSimpleName().replace("<>", "") + ", ");
        System.out.println(replacement.getClass().getSimpleName().replace("<>", ""));
    }

    private static List<Object> parseInput(String targetInput) {
        List<Object> target = new ArrayList<>();

        String[] tokens = targetInput.split(" ");

        for (String token : tokens) {
            try {
                // Try to parse as double first (for floats or ints)
                double num = Double.parseDouble(token);
                // round to 1 decimal place (same as alphabet)
                num = Math.round(num * 10.0) / 10.0;
                target.add(num);
            } catch (NumberFormatException e) {
                // Not a number, so treat as individual characters
                for (char c : token.toCharArray()) {
                    target.add(c);
                }
            }
        }
        return target;
    }

    private static int getInt(Scanner scanner, String prompt, int defaultValue) {
        System.out.print(prompt + " (default: " + defaultValue + "): ");
        String input = scanner.nextLine();
        return input.isEmpty() ? defaultValue : Integer.parseInt(input);
    }

    private static double getDouble(Scanner scanner, String prompt, double defaultValue) {
        System.out.print(prompt + " (default: " + defaultValue + "): ");
        String input = scanner.nextLine();
        return input.isEmpty() ? defaultValue : Double.parseDouble(input);
    }
}
