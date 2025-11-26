package org.soft.fuzzy.inference;

import org.soft.fuzzy.core.*;
import org.soft.fuzzy.core.operators.LogicalOperator;
import org.soft.fuzzy.defuzzification.WeightedAverage;

import java.util.*;

public class Sugeno {

    private final FuzzyRuleBase ruleBase;

    public Sugeno(FuzzyRuleBase ruleBase, LogicalOperator operator) {
        this.ruleBase = ruleBase;
    }

    public double evaluateCrisp(Map<String, Double> inputs, WeightedAverage defuzzifier,
                                Map<String, Map<String, Double>> debugInfo) {

        Map<FuzzyRule, Double> ruleStrengths = ruleBase.evaluateAll(inputs);

        Map<String, Double> output = new LinkedHashMap<>();
        for (var entry : ruleStrengths.entrySet()) {
            output.put(entry.getKey().toString(), entry.getValue());
        }
        debugInfo.put("Output", output);

        Map<SugenoRule, Double> sugenoAggregated = new LinkedHashMap<>();
        for (var entry : ruleStrengths.entrySet()) {
            FuzzyRule rule = entry.getKey();
            double firingStrength = entry.getValue();

            if (rule instanceof SugenoRule sugenoRule) {
                sugenoAggregated.put(sugenoRule, firingStrength);
            }
        }

        Map<String, Double> aggregatedDebug = new LinkedHashMap<>();
        sugenoAggregated.forEach((r, v) -> aggregatedDebug.put(r.toString(), v));
        debugInfo.put("AggregatedOutputs", aggregatedDebug);

        double crispOutput = defuzzifier.defuzzify(sugenoAggregated);
        debugInfo.put("CrispOutput", Map.of("Value", crispOutput));

        return crispOutput;
    }
}
