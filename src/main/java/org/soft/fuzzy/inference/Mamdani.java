package org.soft.fuzzy.inference;

import org.soft.fuzzy.core.*;
import org.soft.fuzzy.core.operators.LogicalOperator;
import org.soft.fuzzy.defuzzification.Defuzzifier;

import java.util.*;

public class Mamdani {

    private final FuzzyRuleBase ruleBase;
    private final LogicalOperator implication;
    private final LogicalOperator aggregation;

    public Mamdani(FuzzyRuleBase ruleBase,
                   LogicalOperator implication,
                   LogicalOperator aggregation) {
        this.ruleBase = ruleBase;
        this.implication = implication;
        this.aggregation = aggregation;
    }

    public double evaluateCrisp(Map<String, Double> inputs,
                                FuzzyVariable outputVariable,
                                Defuzzifier defuzzifier,
                                Map<String, Map<String, Double>> debugInfo) {

        Map<FuzzyRule, Double> ruleStrengths = ruleBase.evaluateAll(inputs);

        Map<String, Double> outputDebug = new LinkedHashMap<>();
        for (var entry : ruleStrengths.entrySet()) {
            outputDebug.put(entry.getKey().toString(), entry.getValue());
        }
        debugInfo.put("Output", outputDebug);

        Map<String, Double> aggregatedOutputs = new LinkedHashMap<>();
        for (var entry : ruleStrengths.entrySet()) {
            FuzzyRule rule = entry.getKey();
            double firingStrength = entry.getValue();
            FuzzyCondition output = rule.getConsequent();
            String outputSetName = output.getSetName();

            double impliedDegree = implication.apply(firingStrength, 1.0);

            aggregatedOutputs.merge(outputSetName, impliedDegree, aggregation::apply);
        }

        debugInfo.put("AggregatedOutputs", new LinkedHashMap<>(aggregatedOutputs));

        double crispOutput = defuzzifier.defuzzify(aggregatedOutputs, outputVariable);
        debugInfo.put("CrispOutput", Map.of("Value", crispOutput));

        return crispOutput;
    }
}
