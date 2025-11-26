package org.soft.fuzzy.core.operators;

import java.io.Serializable;

public class Max implements LogicalOperator, Serializable {
    @Override
    public double apply(double a, double b) {
        return Math.max(a, b);
    }
}
