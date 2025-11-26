package org.soft.fuzzy.core.operators;

public class Sum implements LogicalOperator {
    @Override
    public double apply(double a, double b) {
        return Math.min(1.0, a + b);
    }
}
