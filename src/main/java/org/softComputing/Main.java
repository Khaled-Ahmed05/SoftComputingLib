package org.softComputing;

import org.softComputing.geneticAlgorithm.GeneticAlgorithm;
import org.softComputing.geneticAlgorithm.Termination;
import org.softComputing.geneticAlgorithm.operators.crossover.UniformCO;
import org.softComputing.geneticAlgorithm.operators.selection.*;
import org.softComputing.geneticAlgorithm.operators.crossover.*;
import org.softComputing.geneticAlgorithm.operators.mutation.*;
import org.softComputing.geneticAlgorithm.operators.replacement.*;
import org.softComputing.geneticAlgorithm.initialization.*;
import org.softComputing.geneticAlgorithm.fitnessEvaluation.*;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // ------------------ Problem setup ------------------
        List<Character> target = new ArrayList<>(Arrays.asList('H', 'E', 'L', 'L', 'O', ' ', 'W', 'O', 'R', 'L', 'D'));

        int populationSize = 200;
        int maxGenerations = 500;
        int numOfParents = 4;
        double crossoverRate = 0.7;
        double mutationRate = 0.15;
        int tournamentSize = 5;
        double swapProbability = 0.5;
        int nPoints = 2;
        double lowerBound = 32;
        double upperBound = 122;
        int eliteCount = 2;
        int numOfReplacedGenes = 5;
        List<Character> alphabet = new ArrayList<>(Arrays.asList(
                'A','B','C','D','E','F','G','H','I','J','K','L','M',
                'N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
                'a','b','c','d','e','f','g','h','i','j','k','l','m',
                'n','o','p','q','r','s','t','u','v','w','x','y','z',
                ' '
        ));

        // ------------------ User input ------------------
        populationSize = getInt(scanner, "\nPopulation size", populationSize);
        maxGenerations = getInt(scanner, "Max generations", maxGenerations);
        numOfParents = getInt(scanner, "Number of parents", numOfParents);
        crossoverRate = getDouble(scanner, "Crossover rate", crossoverRate);
        mutationRate = getDouble(scanner, "Mutation rate", mutationRate);

        // ------------------ GA Operator Setup ------------------
        IInitialization<List<Character>> initialization =
                new Initialization(target.size(), alphabet); // random chars of same length

        IFitnessEvaluation<List<Character>> fitnessEvaluation =
                new FitnessEvaluation(target); // fitness = match count / length

        // ---------- User input for operators ----------
        System.out.println("\nSelect Selection method:");
        System.out.println("1: Tournament");
        System.out.println("2: RouletteWheel");
        int selectionChoice = getInt(scanner, ">> Choice", 2);

        ISelection<List<Character>> selection;
        if(selectionChoice == 1) {
            tournamentSize = getInt(scanner, "Tournament size", tournamentSize);
            selection = new Tournament<>(tournamentSize);
        }
        else selection = new RouletteWheel<>();

        System.out.println("\nSelect Crossover method:");
        System.out.println("1: UniformCO");
        System.out.println("2: NPoint");
        System.out.println("3: Multipoint");
        int crossoverChoice = getInt(scanner, ">> Choice", 3);

        ICrossover<Character> crossover;
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
        int mutationChoice = getInt(scanner, "\n>> Choice", 2);

        IMutation<Character> mutation;
        if(mutationChoice == 1) {
            lowerBound = getDouble(scanner, "Lower bound (ASCII)", lowerBound);
            upperBound = getDouble(scanner, "Upper bound (ASCII)", upperBound);
            mutation = new UniformM(mutationRate, lowerBound, upperBound);
        }
        else if (mutationChoice == 2) mutation = new Swap<>(mutationRate);
        else mutation = new Inversion<>(mutationRate);
//        else mutation = new BitFlip(Multipoint)

        System.out.println("\nSelect Replacement method:");
        System.out.println("1: ElitistReplacement");
        System.out.println("2: SteadyStateReplacement");
        System.out.println("3: GenerationalReplacement");
        int replacementChoice = getInt(scanner, ">> Choice", 3);

        IReplacement<Character> replacement;
        if(replacementChoice == 1) {
            eliteCount = getInt(scanner, "Elite count", eliteCount);
            replacement = new ElitistReplacement<>(eliteCount);
        }
        else if(replacementChoice == 2) {
            numOfReplacedGenes = getInt(scanner, "Number of replaced genes", numOfReplacedGenes);
            replacement = new SteadyStateReplacement<>(numOfReplacedGenes);
        }
        else replacement = new GenerationalReplacement<>();

        Termination<List<Character>> termination = new Termination<>(target, maxGenerations);

        // ------------------ GA Configuration ------------------
        GeneticAlgorithm<Character> ga = new GeneticAlgorithm<>(
                populationSize,
                maxGenerations,
                numOfParents,
                crossoverRate,
                initialization,
                fitnessEvaluation,
                selection,
                crossover,
                mutation,
                lowerBound,
                upperBound,
                replacement,
                termination
        );

        // ------------------ Run the GA ------------------
        System.out.println("\nStarting Genetic Algorithm — Password Guessing");
        System.out.println("Target: \"" + target + "\"\n");

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

        List<Character> bestIndividual = ga.run();

        // ------------------ Display result ------------------
        StringBuilder result = new StringBuilder();
        for (Character c : bestIndividual) result.append(c);

        double finalFitness = fitnessEvaluation.evaluate(bestIndividual);
        System.out.println("\nBest evolved string: " + result);
        System.out.printf("Final fitness: %.3f%n", finalFitness);
        System.out.println("Total generations: " + ga.getGeneration());

        // ------------------ Display methods used ------------------
        System.out.print("\nMethods: ");
        System.out.print(selection.getClass().getSimpleName().replace("<>", "") + ", ");
        System.out.print(crossover.getClass().getSimpleName().replace("<>", "") + ", ");
        System.out.print(mutation.getClass().getSimpleName().replace("<>", "") + ", ");
        System.out.println(replacement.getClass().getSimpleName().replace("<>", ""));
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
