package org.soft.fuzzy.core;

import java.util.*;

public class FuzzyRuleBase {

    private final List<FuzzyRule> rules;

    public FuzzyRuleBase() {
        this.rules = new ArrayList<>();
    }

    public void addRule(FuzzyRule rule) {
        rules.add(rule);
    }

    public void removeRule(FuzzyRule rule) {
        rules.remove(rule);
    }

    public List<FuzzyRule> getRules() {
        return rules;
    }

    public void clear() {
        rules.clear();
    }

    public Map<FuzzyRule, Double> evaluateAll(Map<String, Double> inputs) {
        Map<FuzzyRule, Double> activations = new LinkedHashMap<>();
        for (FuzzyRule rule : rules) {
            if (!rule.isEnabled()) continue;
            double strength = rule.evaluate(inputs) * rule.getWeight();
            activations.put(rule, strength);
        }
        return activations;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Rule Base:\n");
        for (FuzzyRule r : rules) sb.append("  ").append(r).append("\n");
        return sb.toString();
    }
}
