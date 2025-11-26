package org.soft.fuzzy.core;

import org.soft.fuzzy.membership.MembershipFunction;

import java.io.Serializable;

public record FuzzySet(String name, MembershipFunction membershipFunction) implements Serializable {

    public FuzzySet {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Fuzzy set name cannot be null or empty");
        }
        if (membershipFunction == null) {
            throw new IllegalArgumentException("Membership function cannot be null");
        }
    }

    public double fuzzify(double x) {
        return membershipFunction.evaluate(x);
    }

    @Override
    public String toString() {
        return "FuzzySet{name='" + name + "'}";
    }

    public String getName() {
        return name;
    }
}
