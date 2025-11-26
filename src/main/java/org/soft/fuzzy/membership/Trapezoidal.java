package org.soft.fuzzy.membership;

import java.io.Serializable;

public class Trapezoidal implements MembershipFunction, Serializable {

    private final double a, b, c, d;

    public Trapezoidal(double a, double b, double c, double d) {
        if (!(a <= b && b <= c && c <= d)) {
            throw new IllegalArgumentException("Parameters must satisfy a <= b <= c <= d");
        }
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    @Override
    public double evaluate(double x) {
        if (x <= a || x >= d) return 0.0;
        else if (x >= b && x <= c) return 1.0;
        else if (x > a && x < b) return (x - a) / (b - a);
        else return (d - x) / (d - c);
    }

    @Override
    public String toString() {
        return "Trapezoidal" + "(" + a + ", " + b + ", " + c + ", " + d + ")";
    }
}
