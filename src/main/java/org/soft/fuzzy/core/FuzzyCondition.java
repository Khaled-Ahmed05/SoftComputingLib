package org.soft.fuzzy.core;

import java.io.Serializable;

public record FuzzyCondition(FuzzyVariable variable, String setName) implements Serializable {

    public double evaluate(double crispValue) {
        FuzzySet set = variable.getFuzzySet(setName);
        if (set == null) {
            throw new IllegalArgumentException("Fuzzy set not found: " + setName);
        }
        return set.fuzzify(crispValue);
    }

    public String getSetName() {
        return setName;
    }

    @Override
    public String toString() {
        return variable.getName() + " IS " + setName;
    }
}
