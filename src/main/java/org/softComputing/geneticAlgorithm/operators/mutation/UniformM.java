package org.softComputing.geneticAlgorithm.operators.mutation;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class UniformM<T> extends AbstractIMutation<T> {
    private final double charLowerBound;
    private final double charUpperBound;
    private final Double numericLowerBound;
    private final Double numericUpperBound;

    public UniformM(double mutationRate, double lowerBound, double upperBound) {
        super(mutationRate);
        this.charLowerBound = lowerBound;
        this.charUpperBound = upperBound;
        this.numericLowerBound = null;
        this.numericUpperBound = null;
    }

    @Override
    public List<T> mutate(List<T> chromosome) {
        validateChromosome(chromosome);

        List<T> mutated = new ArrayList<>(chromosome);

        for (int i = 0; i < mutated.size(); i++) {
            if (!shouldMutate()) continue;

            T gene = mutated.get(i);
            switch (gene) {
                case Character Xi -> {
                    double newXi = uniformLogic(charLowerBound, charUpperBound, Xi);

                    char newChar = (char) Math.round(newXi);

                    mutated.set(i, (T) Character.valueOf(newChar));
                }
                case Number number -> {
                    double lower = numericLowerBound != null ? numericLowerBound : 0.0;
                    double upper = numericUpperBound != null ? numericUpperBound : 10.0;

                    double Xi = number.doubleValue();
                    double newXi = uniformLogic(lower, upper, Xi);

                    newXi = Math.round(newXi * 10.0) / 10.0;

                    Number boxed = boxNumberForType(gene.getClass(), newXi);
                    mutated.set(i, (T) boxed);
                }
                default -> throw new UnsupportedOperationException(
                        "UniformM does not support mutation for type: " + gene.getClass());
            }
        }

        return mutated;
    }

    private double uniformLogic(double lower, double upper, double xi) {
        double deltaLxi = xi - lower;
        double deltaUxi = upper - xi;

        double r1 = random.nextDouble();
        double delta = (r1 <= 0.5) ? deltaLxi : deltaUxi;
        double r2 = random.nextDouble() * Math.abs(delta);

        double newXi = (r1 <= 0.5) ? (xi - r2) : (xi + r2);
        newXi = Math.max(lower, Math.min(upper, newXi));

        return newXi;
    }

    private Number boxNumberForType(Class<?> type, double value) {
        if (type == Integer.class) return (int) Math.round(value);
        if (type == Long.class) return Math.round(value);
        if (type == Short.class) return (short) Math.round(value);
        if (type == Byte.class) return (byte) Math.round(value);
        if (type == Float.class) return (float) value;
        if (type == Double.class) return value;
        throw new UnsupportedOperationException("Numeric type not supported: " + type);
    }
}
