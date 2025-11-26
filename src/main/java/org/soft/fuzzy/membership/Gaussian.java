package org.soft.fuzzy.membership;

import java.io.Serializable;

public class Gaussian implements MembershipFunction, Serializable {

    private final double mean;
    private final double std;

    public Gaussian(double mean, double std) {
        if (std <= 0) {
            throw new IllegalArgumentException("Standard deviation must be positive");
        }
        this.mean = mean;
        this.std = std;
    }

    @Override
    public double evaluate(double x) {
        return Math.exp(-0.5 * Math.pow((x - mean) / std, 2));
    }

    @Override
    public String toString() {
        return "Gaussian(" + mean + ", " + std + ")";
    }
}
