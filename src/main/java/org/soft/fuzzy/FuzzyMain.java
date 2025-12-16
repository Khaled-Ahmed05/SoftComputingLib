package org.soft.fuzzy;

import org.soft.fuzzy.core.*;
import org.soft.fuzzy.core.operators.*;
import org.soft.fuzzy.defuzzification.*;
import org.soft.fuzzy.membership.*;

import java.util.*;

public class FuzzyMain {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        FuzzyLogicSystem fls = new FuzzyLogicSystem();

        System.out.println("=== Fuzzy Logic Engine Setup ===");

        // 1. CHOOSE INFERENCE ENGINE
        System.out.println("Choose inference engine:");
        System.out.println("1. Mamdani (default)");
        System.out.println("2. Sugeno");
        System.out.print("Selection: ");
        String engineChoice = sc.nextLine().trim();
        boolean useMamdani = !engineChoice.equals("2");

        System.out.println("\nUsing engine: " + (useMamdani ? "Mamdani" : "Sugeno"));

        // 2. SELECT AND/OR OPERATORS
        System.out.println("\nSelect AND operator (default: MIN):");
        System.out.println("1. MIN");
        System.out.println("2. PRODUCT");
        String andChoice = sc.nextLine().trim();

        LogicalOperator AND = andChoice.equals("2") ? new Product() : new Min();

        System.out.println("\nSelect OR operator (default: MAX):");
        System.out.println("1. MAX");
        System.out.println("2. SUM");
        String orChoice = sc.nextLine().trim();

        LogicalOperator OR = orChoice.equals("2") ? new Sum() : new Max();

        fls.setAndOperator(AND);
        fls.setOrOperator(OR);

        // 3. DEFUZZIFICATION
        Defuzzifier mamdaniDefuzzifier = null;
        WeightedAverage sugenoDefuzzifier = null;

        if (useMamdani) {
            System.out.println("\nChoose defuzzification method:");
            System.out.println("1. Centroid (default)");
            System.out.println("2. MOM (Mean of Maximum)");
            System.out.print("Selection: ");
            String defChoice = sc.nextLine().trim();

            mamdaniDefuzzifier = switch (defChoice) {
                case "2" -> new MeanOfMaximum();
                default -> new Centroid();
            };

            fls.setDefuzzifier(mamdaniDefuzzifier);
        } else {
            System.out.println("\nSugeno defuzzification is always: Weighted Average");
            sugenoDefuzzifier = new WeightedAverage();
        }

        // 4. CHOOSE VARIABLES (DEFAULT OR CUSTOM)
        System.out.println("\nConfigure variables:");
        System.out.println("1. Use default predefined variables");
        System.out.println("2. Create my own variables");
        System.out.print("Selection: ");
        String varChoice = sc.nextLine().trim();

        fls.clearAll();

        if (varChoice.equals("1")) {
            fls.loadDefaultVariables();
            System.out.println("Default variables loaded.");
        } else {
            System.out.println("\nHow many variables do you want to define?");
            int count = Integer.parseInt(sc.nextLine().trim());

            List<String> variableNames = new ArrayList<>();

            for (int i = 0; i < count; i++) {
                System.out.println("\nVariable #" + (i + 1));

                System.out.print("Name: ");
                String name = sc.nextLine().trim();

                System.out.print("Min range: ");
                double min = Double.parseDouble(sc.nextLine().trim());

                System.out.print("Max range: ");
                double max = Double.parseDouble(sc.nextLine().trim());

                FuzzyVariable var = new FuzzyVariable(name, min, max);

                System.out.print("How many fuzzy sets? ");
                int sets = Integer.parseInt(sc.nextLine().trim());

                for (int j = 0; j < sets; j++) {
                    System.out.println("\n--- Creating Set " + (j + 1) + " ---");

                    System.out.print("Enter set name: ");
                    String setName = sc.nextLine().trim();

                    System.out.println("Choose membership type:");
                    System.out.println("1. Triangular (a, b, c)");
                    System.out.println("2. Trapezoidal (a, b, c, d)");
                    System.out.println("3. Gaussian (mean, std)");
                    System.out.print("Selection: ");
                    String choice = sc.nextLine().trim();

                    MembershipFunction mf = switch (choice) {

                        // TRIANGULAR
                        case "1" -> {
                            System.out.println("Enter parameters for TRIANGULAR (a b c): ");
                            System.out.print("a = ");
                            double a = sc.nextDouble();
                            System.out.print("b = ");
                            double b = sc.nextDouble();
                            System.out.print("c = ");
                            double c = sc.nextDouble();
                            sc.nextLine();
                            yield new Triangular(a, b, c);
                        }

                        // TRAPEZOIDAL
                        case "2" -> {
                            System.out.println("Enter parameters for TRAPEZOIDAL (a b c d): ");
                            System.out.print("a = ");
                            double a = sc.nextDouble();
                            System.out.print("b = ");
                            double b = sc.nextDouble();
                            System.out.print("c = ");
                            double c = sc.nextDouble();
                            System.out.print("d = ");
                            double d = sc.nextDouble();
                            sc.nextLine();
                            yield new Trapezoidal(a, b, c, d);
                        }

                        // GAUSSIAN
                        case "3" -> {
                            System.out.println("Enter parameters for GAUSSIAN (mean, std): ");
                            System.out.print("mean = ");
                            double mean = sc.nextDouble();
                            System.out.print("std = ");
                            double std = sc.nextDouble();
                            sc.nextLine();
                            yield new Gaussian(mean, std);
                        }

                        // DEFAULT → TRIANGULAR
                        default -> {
                            System.out.println("Invalid option. Using TRIANGULAR by default.");
                            System.out.println("Enter parameters for TRIANGULAR (a b c): ");
                            System.out.print("a = ");
                            double a = sc.nextDouble();
                            System.out.print("b = ");
                            double b = sc.nextDouble();
                            System.out.print("c = ");
                            double c = sc.nextDouble();
                            sc.nextLine();
                            yield new Triangular(a, b, c);
                        }
                    };

                    var.addFuzzySet(setName, new FuzzySet(setName, mf));
                    System.out.println("Added set: " + setName + ", with membership function: " + mf.toString());
                }

                fls.addVariable(var);
                variableNames.add(name);
                System.out.println("Added variable: " + var);
            }

            // Choose output variable
            System.out.println("\nWhich variable is the OUTPUT variable?");
            for (int i = 0; i < variableNames.size(); i++) {
                System.out.println((i + 1) + ". " + variableNames.get(i));
            }
            int out = Integer.parseInt(sc.nextLine().trim());
            fls.setOutputVariable(variableNames.get(out - 1));

            System.out.println("Custom variables created.");
        }

        // 5. EDIT RULE BASE
        System.out.println("\nOpening Rule Editor...");
        RuleEditorCLI.run(fls, useMamdani);

        // 6. INPUT VALUES
        System.out.println("\nEnter Altitude error (double): ");
        double AE = sc.nextDouble();
        System.out.println("Enter Vertical velocity (double): ");
        double VV = sc.nextDouble();

        sc.close();

        Map<String, Double> inputs = Map.of(
                "Altitude error", AE,
                "Vertical velocity", VV
        );

        // 7. RUN
        if (useMamdani) {

            Map<String, Map<String, Double>> debug = new LinkedHashMap<>();
            double result = fls.getMamdaniEngine().evaluateCrisp(
                    inputs, fls.getOutputVariable(), mamdaniDefuzzifier, debug
            );

            System.out.println("\n=== Mamdani Debug Info ===");
            printDebug(debug);
            System.out.println("\nOutput = " + result);

        } else {

            Map<String, Map<String, Double>> debug = new LinkedHashMap<>();
            double result = fls.getSugenoEngine().evaluateCrisp(inputs, sugenoDefuzzifier, debug);

            System.out.println("\n=== Sugeno Debug Info ===");
            printDebug(debug);
            System.out.println("\nOutput = " + result);
        }
    }

    private static void printDebug(Map<String, Map<String, Double>> debug) {
        debug.forEach((ruleName, contributions) -> {
            System.out.println(ruleName + ":");
            contributions.forEach((s, v) -> {
                if (v != 0) System.out.println("  " + s + " -> " + v);
            });
        });
    }
}
