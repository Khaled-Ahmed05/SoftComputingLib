package org.soft.fuzzy.core.operators;

public class Product implements LogicalOperator {
    @Override
    public double apply(double a, double b) {
        return a * b;
    }
}
