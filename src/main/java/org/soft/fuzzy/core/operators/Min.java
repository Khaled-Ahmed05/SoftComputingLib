package org.soft.fuzzy.core.operators;

import java.io.Serializable;

public class Min implements LogicalOperator, Serializable {
    @Override
    public double apply(double a, double b) {
        return Math.min(a, b);
    }
}
