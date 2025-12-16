package org.soft.fuzzy;

import org.soft.fuzzy.defuzzification.*;
import org.soft.fuzzy.inference.*;
import org.soft.fuzzy.core.operators.*;
import org.soft.fuzzy.core.*;
import org.soft.fuzzy.membership.Triangular;

import java.util.*;

public class FuzzyLogicSystem {

    private final Map<String, FuzzyVariable> inputVariables = new LinkedHashMap<>();
    private FuzzyVariable outputVariable;

    private final FuzzyRuleBase mamdaniRuleBase = new FuzzyRuleBase();
    private final FuzzyRuleBase sugenoRuleBase = new FuzzyRuleBase();

    private LogicalOperator AND = new Min();
    private LogicalOperator OR = new Max();

    // Engines
    private final Mamdani mamdaniEngine;
    private final Sugeno sugenoEngine;

    public FuzzyLogicSystem() {
        defineDefaultVariables();

        defineDefaultRules();

        mamdaniEngine = new Mamdani(mamdaniRuleBase, AND, OR);
        sugenoEngine = new Sugeno(sugenoRuleBase, AND);

        new Centroid();
    }

    public void setAndOperator(LogicalOperator AND) {
        this.AND = AND;
    }

    public void setOrOperator(LogicalOperator OR) {
        this.OR = OR;
    }

    public void setDefuzzifier(Defuzzifier defuzzifier) {
    }

    public FuzzyVariable getInputVariable(String name) {
        return inputVariables.get(name);
    }

    public Map<String, FuzzyVariable> getInputVariablesMap() { return inputVariables; }

    public FuzzyVariable getOutputVariable() {
        return outputVariable;
    }

    public FuzzyRuleBase getMamdaniRuleBase() {
        return mamdaniRuleBase;
    }

    public FuzzyRuleBase getSugenoRuleBase() {
        return sugenoRuleBase;
    }

    public Mamdani getMamdaniEngine() {
        return mamdaniEngine;
    }

    public Sugeno getSugenoEngine() {
        return sugenoEngine;
    }

    public void clearAll() {
        inputVariables.clear();
        if (outputVariable != null) outputVariable.getFuzzySets().clear();
        mamdaniRuleBase.clear();
        sugenoRuleBase.clear();
    }

    public void addVariable(FuzzyVariable var) {
        inputVariables.put(var.getName(), var);
    }

    public void setOutputVariable(String name) {
        outputVariable = inputVariables.get(name);
    }

    public void loadDefaultVariables() {
        inputVariables.clear();
        mamdaniRuleBase.clear();
        sugenoRuleBase.clear();
        defineDefaultVariables();
        defineDefaultRules();
    }


    // ---------------- Default Variables ----------------
    private void defineDefaultVariables() {
        FuzzyVariable altitudeError = new FuzzyVariable("Altitude error", -5, 5); // meters

        altitudeError.addFuzzySet("Major Negative", new FuzzySet("Major Negative", new Triangular(-5, -5, -3)));
        altitudeError.addFuzzySet("Minor Negative", new FuzzySet("Minor Negative", new Triangular(-5, -3, -1)));
        altitudeError.addFuzzySet("Zero", new FuzzySet("Zero", new Triangular(-2, 0, 2)));
        altitudeError.addFuzzySet("Minor Positive", new FuzzySet("Minor Positive", new Triangular(1, 3, 5)));
        altitudeError.addFuzzySet("Major Positive", new FuzzySet("Major Positive", new Triangular(3, 5, 5)));

        FuzzyVariable verticalVelocity = new FuzzyVariable("Vertical velocity", -3, 3); // km/h

        verticalVelocity.addFuzzySet("Fast Fall", new FuzzySet("Fast Fall", new Triangular(-3, -3, -1.5)));
        verticalVelocity.addFuzzySet("Slow Fall", new FuzzySet("Slow Fall", new Triangular(-3, -1.5, 0)));
        verticalVelocity.addFuzzySet("Stable", new FuzzySet("Stable", new Triangular(-1, 0, 1)));
        verticalVelocity.addFuzzySet("Slow Rise", new FuzzySet("Slow Rise", new Triangular(0, 1.5, 3)));
        verticalVelocity.addFuzzySet("Fast Rise", new FuzzySet("Fast Rise", new Triangular(1.5, 3, 3)));

        outputVariable = new FuzzyVariable("Thrust adjustment", -30, 30); // Newtons

        outputVariable.addFuzzySet("Major Decrease", new FuzzySet("Major Decrease", new Triangular(-30, -30, -15)));
        outputVariable.addFuzzySet("Minor Decrease", new FuzzySet("Minor Decrease", new Triangular(-25, -15, -5)));
        outputVariable.addFuzzySet("No Change", new FuzzySet("No Change", new Triangular(-10, 0, 10)));
        outputVariable.addFuzzySet("Minor Increase", new FuzzySet("Minor Increase", new Triangular(5, 15, 25)));
        outputVariable.addFuzzySet("Major Increase", new FuzzySet("Major Increase", new Triangular(15, 30, 30)));

        inputVariables.put(altitudeError.getName(), altitudeError);
        inputVariables.put(verticalVelocity.getName(), verticalVelocity);
    }

    // ---------------- Default Rule Bases ----------------
    private void defineDefaultRules() {
        FuzzyVariable altitudeError = inputVariables.get("Altitude error");
        FuzzyVariable verticalVelocity = inputVariables.get("Vertical velocity");

    // ---------- Mamdani Rule Base ----------
    // Major Positive
        mamdaniRuleBase.addRule(new FuzzyRule(
                List.of(new FuzzyCondition(altitudeError, "Major Positive"),
                        new FuzzyCondition(verticalVelocity, "Fast Fall")),
                new FuzzyCondition(outputVariable, "Major Increase"),
                AND));

        mamdaniRuleBase.addRule(new FuzzyRule(
                List.of(new FuzzyCondition(altitudeError, "Major Positive"),
                        new FuzzyCondition(verticalVelocity, "Slow Fall")),
                new FuzzyCondition(outputVariable, "Minor Increase"),
                AND));

        mamdaniRuleBase.addRule(new FuzzyRule(
                List.of(new FuzzyCondition(altitudeError, "Major Positive"),
                        new FuzzyCondition(verticalVelocity, "Stable")),
                new FuzzyCondition(outputVariable, "Major Increase"),
                AND));

    // Minor Positive
        mamdaniRuleBase.addRule(new FuzzyRule(
                List.of(new FuzzyCondition(altitudeError, "Minor Positive"),
                        new FuzzyCondition(verticalVelocity, "Fast Fall")),
                new FuzzyCondition(outputVariable, "Minor Increase"),
                AND));

        mamdaniRuleBase.addRule(new FuzzyRule(
                List.of(new FuzzyCondition(altitudeError, "Minor Positive"),
                        new FuzzyCondition(verticalVelocity, "Stable")),
                new FuzzyCondition(outputVariable, "Minor Increase"),
                AND));

        mamdaniRuleBase.addRule(new FuzzyRule(
                List.of(new FuzzyCondition(altitudeError, "Minor Positive"),
                        new FuzzyCondition(verticalVelocity, "Slow Rise")),
                new FuzzyCondition(outputVariable, "No Change"),
                AND));

    // Zero
        mamdaniRuleBase.addRule(new FuzzyRule(
                List.of(new FuzzyCondition(altitudeError, "Zero"),
                        new FuzzyCondition(verticalVelocity, "Stable")),
                new FuzzyCondition(outputVariable, "No Change"),
                AND));

        mamdaniRuleBase.addRule(new FuzzyRule(
                List.of(new FuzzyCondition(altitudeError, "Zero"),
                        new FuzzyCondition(verticalVelocity, "Fast Fall")),
                new FuzzyCondition(outputVariable, "Minor Increase"),
                AND));

        mamdaniRuleBase.addRule(new FuzzyRule(
                List.of(new FuzzyCondition(altitudeError, "Zero"),
                        new FuzzyCondition(verticalVelocity, "Slow Rise")),
                new FuzzyCondition(outputVariable, "Minor Decrease"),
                AND));

    // Minor Negative
        mamdaniRuleBase.addRule(new FuzzyRule(
                List.of(new FuzzyCondition(altitudeError, "Minor Negative"),
                        new FuzzyCondition(verticalVelocity, "Slow Rise")),
                new FuzzyCondition(outputVariable, "Major Decrease"),
                AND));

        mamdaniRuleBase.addRule(new FuzzyRule(
                List.of(new FuzzyCondition(altitudeError, "Minor Negative"),
                        new FuzzyCondition(verticalVelocity, "Stable")),
                new FuzzyCondition(outputVariable, "Minor Decrease"),
                AND));

    // Major Negative
        mamdaniRuleBase.addRule(new FuzzyRule(
                List.of(new FuzzyCondition(altitudeError, "Major Negative"),
                        new FuzzyCondition(verticalVelocity, "Slow Rise")),
                new FuzzyCondition(outputVariable, "Major Decrease"),
                AND));

        mamdaniRuleBase.addRule(new FuzzyRule(
                List.of(new FuzzyCondition(altitudeError, "Major Negative"),
                        new FuzzyCondition(verticalVelocity, "Fast Rise")),
                new FuzzyCondition(outputVariable, "Minor Decrease"),
                AND));

        mamdaniRuleBase.addRule(new FuzzyRule(
                List.of(new FuzzyCondition(altitudeError, "Major Negative"),
                        new FuzzyCondition(verticalVelocity, "Stable")),
                new FuzzyCondition(outputVariable, "Major Decrease"),
                AND));


    // ---------------- Sugeno Rule Base (Numeric Outputs) ----------------
    // Major Positive
        sugenoRuleBase.addRule(new SugenoRule(
                List.of(new FuzzyCondition(altitudeError, "Major Positive"),
                        new FuzzyCondition(verticalVelocity, "Fast Fall")),
                25.0,
                AND));

        sugenoRuleBase.addRule(new SugenoRule(
                List.of(new FuzzyCondition(altitudeError, "Major Positive"),
                        new FuzzyCondition(verticalVelocity, "Slow Fall")),
                10.0,
                AND));

        sugenoRuleBase.addRule(new SugenoRule(
                List.of(new FuzzyCondition(altitudeError, "Major Positive"),
                        new FuzzyCondition(verticalVelocity, "Stable")),
                25.0,
                AND));

    // Minor Positive
        sugenoRuleBase.addRule(new SugenoRule(
                List.of(new FuzzyCondition(altitudeError, "Minor Positive"),
                        new FuzzyCondition(verticalVelocity, "Fast Fall")),
                10.0,
                AND));

        sugenoRuleBase.addRule(new SugenoRule(
                List.of(new FuzzyCondition(altitudeError, "Minor Positive"),
                        new FuzzyCondition(verticalVelocity, "Stable")),
                10.0,
                AND));

        sugenoRuleBase.addRule(new SugenoRule(
                List.of(new FuzzyCondition(altitudeError, "Minor Positive"),
                        new FuzzyCondition(verticalVelocity, "Slow Rise")),
                0.0,
                AND));

    // Zero
        sugenoRuleBase.addRule(new SugenoRule(
                List.of(new FuzzyCondition(altitudeError, "Zero"),
                        new FuzzyCondition(verticalVelocity, "Stable")),
                0.0,
                AND));

        sugenoRuleBase.addRule(new SugenoRule(
                List.of(new FuzzyCondition(altitudeError, "Zero"),
                        new FuzzyCondition(verticalVelocity, "Fast Fall")),
                10.0,
                AND));

        sugenoRuleBase.addRule(new SugenoRule(
                List.of(new FuzzyCondition(altitudeError, "Zero"),
                        new FuzzyCondition(verticalVelocity, "Slow Rise")),
                -10.0,
                AND));

    // Minor Negative
        sugenoRuleBase.addRule(new SugenoRule(
                List.of(new FuzzyCondition(altitudeError, "Minor Negative"),
                        new FuzzyCondition(verticalVelocity, "Slow Rise")),
                -25.0,
                AND));

        sugenoRuleBase.addRule(new SugenoRule(
                List.of(new FuzzyCondition(altitudeError, "Minor Negative"),
                        new FuzzyCondition(verticalVelocity, "Stable")),
                -10.0,
                AND));

    // Major Negative
        sugenoRuleBase.addRule(new SugenoRule(
                List.of(new FuzzyCondition(altitudeError, "Major Negative"),
                        new FuzzyCondition(verticalVelocity, "Slow Rise")),
                -25.0,
                AND));

        sugenoRuleBase.addRule(new SugenoRule(
                List.of(new FuzzyCondition(altitudeError, "Major Negative"),
                        new FuzzyCondition(verticalVelocity, "Fast Rise")),
                -10.0,
                AND));

        sugenoRuleBase.addRule(new SugenoRule(
                List.of(new FuzzyCondition(altitudeError, "Major Negative"),
                        new FuzzyCondition(verticalVelocity, "Stable")),
                -25.0,
                AND));
    }
}