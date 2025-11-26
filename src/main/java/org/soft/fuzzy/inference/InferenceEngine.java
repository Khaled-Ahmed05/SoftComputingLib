package org.soft.fuzzy.inference;

import java.util.Map;

public interface InferenceEngine {
    double evaluate(Map<String, Double> inputs);
}
