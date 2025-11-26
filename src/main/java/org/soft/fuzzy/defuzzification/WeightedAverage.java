package org.soft.fuzzy.defuzzification;

import org.soft.fuzzy.inference.SugenoRule;

import java.util.Map;

public class WeightedAverage {
    public double defuzzify(Map<SugenoRule, Double> aggregatedOutputs) {
        double numerator = 0.0;
        double denominator = 0.0;

        for (Map.Entry<SugenoRule, Double> entry : aggregatedOutputs.entrySet()) {
            double firingStrength = entry.getValue();
            double ruleOutput = entry.getKey().getOutputValue();

            numerator += firingStrength * ruleOutput;
            denominator += firingStrength;
        }

        return (denominator == 0.0) ? 0.0 : numerator / denominator;
    }
}
