package org.soft.fuzzy.core;

import java.io.Serializable;
import java.util.*;

public class FuzzyVariable implements Serializable {

    private final String name;
    private final double minRange;
    private final double maxRange;
    private final Map<String, FuzzySet> fuzzySets;

    public FuzzyVariable(String name, double minRange, double maxRange) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (minRange >= maxRange) {
            throw new IllegalArgumentException("Invalid range: min must be less than max");
        }

        this.name = name;
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.fuzzySets = new LinkedHashMap<>();
    }

    public String getName() {
        return name;
    }

    public double getMinRange() {
        return minRange;
    }

    public double getMaxRange() {
        return maxRange;
    }

    public void addFuzzySet(String setName, FuzzySet set) {
        if (fuzzySets.containsKey(setName)) {
            throw new IllegalArgumentException("Duplicate fuzzy set name: " + setName);
        }
        fuzzySets.put(setName, set);
    }

    public FuzzySet getFuzzySet(String setName) {
        return fuzzySets.get(setName);
    }

    public Collection<FuzzySet> getFuzzySets() {
        return fuzzySets.values();
    }

    public Map<String, Double> fuzzify(double x) {
        Map<String, Double> result = new LinkedHashMap<>();

        // Optionally clamp x to the variable range
        double clampedX = Math.min(Math.max(x, minRange), maxRange);

        for (var entry : fuzzySets.entrySet()) {
            String setName = entry.getKey();
            FuzzySet set = entry.getValue();
            result.put(setName, set.fuzzify(clampedX));
        }

        return result;
    }

    @Override
    public String toString() {
        return "FuzzyVariable{name='" + name + "', range=[" + minRange + ", " + maxRange + "], sets=" + fuzzySets.keySet() + "}";
    }
}
