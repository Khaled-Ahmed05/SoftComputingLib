package org.soft.fuzzy.core;

import org.soft.fuzzy.FuzzyLogicSystem;
import org.soft.fuzzy.core.operators.*;
import org.soft.fuzzy.inference.SugenoRule;

import java.util.*;

public class RuleEditorCLI {

    public static void run(FuzzyLogicSystem fls, Boolean useMamdani) {
        FuzzyRuleBaseEditor editor = (useMamdani) ? new FuzzyRuleBaseEditor(fls.getMamdaniRuleBase()) : new FuzzyRuleBaseEditor(fls.getSugenoRuleBase());

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== RULE EDITOR ===");
            System.out.println("1. Show all rules");
            System.out.println("2. Add rule");
            System.out.println("3. Edit rule");
            System.out.println("4. Delete rule");
            System.out.println("5. Enable/Disable rule");
            System.out.println("6. Set rule weight");
            System.out.println("7. Save to JSON");
            System.out.println("8. Load from JSON");
            System.out.println("0. Run Task");
            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> showAll(editor);
                    case "2" -> addRuleInteractive(editor, fls, sc);
                    case "3" -> editRuleInteractive(editor, fls, sc);
                    case "4" -> deleteRuleInteractive(editor, sc);
                    case "5" -> toggleRuleInteractive(editor, sc);
                    case "6" -> setWeightInteractive(editor, sc);
                    case "7" -> saveInteractive(editor, sc);
                    case "8" -> loadInteractive(editor, fls, sc);
                    case "0" -> { return; }
                    default -> System.out.println("Unknown option");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
                ex.printStackTrace(System.out);
            }
        }
    }

    private static void showAll(FuzzyRuleBaseEditor editor) {
        System.out.println("\nCurrent Rules:");
        editor.getAllRules().forEach((id, rule) -> {
            System.out.println("ID=" + id + " | enabled=" + rule.isEnabled() + " | weight=" + rule.getWeight());
            System.out.println("  " + rule);
        });
    }

    private static void addRuleInteractive(FuzzyRuleBaseEditor editor, FuzzyLogicSystem fls, Scanner sc) {
        System.out.println("\nChoose rule type:");
        System.out.println("1 - Mamdani rule (fuzzy set output)");
        System.out.println("2 - Sugeno rule (numeric output)");
        System.out.print("Enter choice: ");
        String type = sc.nextLine().trim();

        FuzzyRule newRule;

        if (type.equals("2")) {
            newRule = addSugenoRuleFromUser(fls, sc);
        } else {
            newRule = addMamdaniRuleFromUser(fls, sc);
        }

        if (newRule == null) {
            System.out.println("Rule creation cancelled.");
            return;
        }

        int id = editor.addRule(newRule);
        System.out.println("Added rule with ID = " + id);
    }

    private static void editRuleInteractive(FuzzyRuleBaseEditor editor, FuzzyLogicSystem fls, Scanner sc) {
        System.out.print("Enter rule ID to edit: ");
        int id = Integer.parseInt(sc.nextLine().trim());

        Map<Integer, FuzzyRule> rules = editor.getAllRules();
        if (!rules.containsKey(id)) {
            System.out.println("Rule not found.");
            return;
        }

        System.out.println("\nChoose rule type:");
        System.out.println("1 - Mamdani rule (fuzzy set output)");
        System.out.println("2 - Sugeno rule (numeric output)");
        System.out.print("Enter choice: ");
        String type = sc.nextLine().trim();

        FuzzyRule newRule;

        if (type.equals("2")) {
            newRule = addSugenoRuleFromUser(fls, sc);
        } else {
            newRule = addMamdaniRuleFromUser(fls, sc);
        }

        if (newRule == null) {
            System.out.println("Rule creation cancelled.");
            return;
        }

        editor.editRule(id, newRule);

        System.out.println("\nRule " + id + " updated successfully.");
    }

    private static void deleteRuleInteractive(FuzzyRuleBaseEditor editor, Scanner sc) {
        System.out.print("Enter rule ID to delete: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        editor.removeRule(id);
        System.out.println("Deleted rule " + id);
    }

    private static void toggleRuleInteractive(FuzzyRuleBaseEditor editor, Scanner sc) {
        System.out.print("Enter rule ID to toggle enable/disable: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        FuzzyRule r = editor.getAllRules().get(id);
        if (r == null) { System.out.println("Not found"); return; }
        r.setEnabled(!r.isEnabled());
        System.out.println("Rule " + id + " enabled=" + r.isEnabled());
    }

    private static void setWeightInteractive(FuzzyRuleBaseEditor editor, Scanner sc) {
        System.out.print("Enter rule ID: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Enter weight (0.0 - 1.0): ");
        double w = Double.parseDouble(sc.nextLine().trim());
        editor.setRuleWeight(id, w);
        System.out.println("Weight updated");
    }

    private static void saveInteractive(FuzzyRuleBaseEditor editor, Scanner sc) throws Exception {
        System.out.print("Enter file path (e.g. rules.json): ");
        String path = sc.nextLine().trim();
        editor.saveToJson(path);
        System.out.println("Saved to " + path);
    }

    private static void loadInteractive(FuzzyRuleBaseEditor editor, FuzzyLogicSystem fls, Scanner sc) throws Exception {
        System.out.print("Enter file path to load: ");
        String path = sc.nextLine().trim();
        Map<String, FuzzyVariable> vars = new LinkedHashMap<>(fls.getInputVariablesMap());
        vars.put(fls.getOutputVariable().getName(), fls.getOutputVariable());
        editor.loadFromJson(path, vars);
        System.out.println("Loaded rules from " + path);
    }

    private static FuzzyRule addMamdaniRuleFromUser(FuzzyLogicSystem fls, Scanner sc) {

        System.out.println("\n--- Mamdani Rule Builder ---");

        System.out.print("Number of conditions: ");
        int n = Integer.parseInt(sc.nextLine().trim());

        List<FuzzyCondition> conditions = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            System.out.print("Variable for condition " + (i+1) + ": ");
            String var = sc.nextLine().trim();

            FuzzyVariable fv = fls.getInputVariable(var);
            if (fv == null) fv = fls.getOutputVariable();
            if (fv == null) {
                System.out.println("Unknown variable: " + var);
                return null;
            }

            System.out.println("Available sets:");
            fv.getFuzzySets().forEach(fs -> System.out.println(" - " + fs.getName()));

            System.out.print("Choose set: ");
            String set = sc.nextLine().trim();

            conditions.add(new FuzzyCondition(fv, set));
        }

        // Consequent
        System.out.print("Consequent variable (output): ");
        String consVar = sc.nextLine().trim();

        FuzzyVariable consFV = fls.getOutputVariable();
        FuzzyVariable tryVar = fls.getInputVariable(consVar);
        if (tryVar != null) consFV = tryVar;

        System.out.println("Available sets for consequent:");
        consFV.getFuzzySets().forEach(fs -> System.out.println(" - " + fs.getName()));

        System.out.print("Choose consequent set: ");
        String consSet = sc.nextLine().trim();

        LogicalOperator op = chooseOperator(sc);

        return new FuzzyRule(conditions, new FuzzyCondition(consFV, consSet), op);
    }

    private static FuzzyRule addSugenoRuleFromUser(FuzzyLogicSystem fls, Scanner sc) {

        System.out.println("\n--- Sugeno Rule Builder ---");

        System.out.print("Number of conditions: ");
        int n = Integer.parseInt(sc.nextLine().trim());

        List<FuzzyCondition> conds = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            System.out.print("Variable for condition " + (i+1) + ": ");
            String var = sc.nextLine().trim();

            FuzzyVariable fv = fls.getInputVariable(var);
            if (fv == null) {
                System.out.println("Unknown variable: " + var);
                return null;
            }

            System.out.println("Available sets:");
            fv.getFuzzySets().forEach(fs -> System.out.println(" - " + fs.getName()));

            System.out.print("Choose set: ");
            String setName = sc.nextLine().trim();

            conds.add(new FuzzyCondition(fv, setName));
        }

        // Sugeno constant output
        System.out.print("Enter Sugeno output value (e.g., 100.0): ");
        double outVal = Double.parseDouble(sc.nextLine().trim());

        LogicalOperator op = chooseOperator(sc);

        return new SugenoRule(conds, outVal, op);
    }

    private static LogicalOperator chooseOperator(Scanner sc) {
        System.out.println("\nChoose operator:");
        System.out.println("1 - AND (Min)");
        System.out.println("2 - OR  (Max)");
        System.out.print("Enter choice: ");
        String op = sc.nextLine().trim();

        return switch (op) {
            case "2" -> new Max();
            default -> new Min();
        };
    }
}
