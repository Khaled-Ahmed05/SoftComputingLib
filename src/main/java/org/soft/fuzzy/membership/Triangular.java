package org.soft.fuzzy.membership;

import java.io.Serializable;

public class Triangular implements MembershipFunction, Serializable {

    private final double a, b, c;

    public Triangular(double a, double b, double c) {
        if (!(a <= b && b <= c)) {
            throw new IllegalArgumentException("Parameters must satisfy a <= b <= c");
        }
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public double evaluate(double x) {
        if (x <= a || x >= c) return 0.0;
        else if (x == b) return 1.0;
        else if (x > a && x < b) return (x - a) / (b - a);
        else return (c - x) / (c - b);
    }

    @Override
    public String toString() {
        return "Triangular" + "(" + a + ", " + b + ", " + c + ")";
    }
}
