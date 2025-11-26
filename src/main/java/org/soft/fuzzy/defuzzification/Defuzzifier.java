package org.soft.fuzzy.defuzzification;

import org.soft.fuzzy.core.FuzzyVariable;

import java.util.Map;

public interface Defuzzifier {
    double defuzzify(Map<String, Double> aggregatedOutputs, FuzzyVariable outputVariable);
}
