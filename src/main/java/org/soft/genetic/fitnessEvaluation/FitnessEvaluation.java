package org.soft.genetic.fitnessEvaluation;

import java.util.List;

public class FitnessEvaluation<T> implements IFitnessEvaluation<List<T>> {

    private final List<T> target;

    public FitnessEvaluation(List<T> target) {
        if (target == null || target.isEmpty())
            throw new IllegalArgumentException("Target cannot be null or empty.");
        this.target = target;
    }

    @Override
    public double evaluate(List<T> chromosome) {
        if (chromosome == null || chromosome.size() != target.size())
            throw new IllegalArgumentException("Chromosome length must match target length.");

        double totalScore = 0.0;
        double maxScore = target.size();

        for (int i = 0; i < target.size(); i++) {
            T t = target.get(i);
            T c = chromosome.get(i);

            totalScore += evaluateGene(t, c);
        }

        return totalScore / maxScore;
    }

    private double evaluateGene(T targetGene, T chromosomeGene) {
        if (targetGene == null || chromosomeGene == null)
            return 0.0;

        switch (targetGene) {
            case Number number when chromosomeGene instanceof Number -> {
                double t = number.doubleValue();
                double c = ((Number) chromosomeGene).doubleValue();
                double diff = Math.abs(t - c);

                // Use Gaussian-like fitness curve for better gradient sensitivity
                return Math.exp(-Math.pow(diff, 2) / 4.0);
            }
            case Character ignored1 when chromosomeGene instanceof Character -> {
                return targetGene.equals(chromosomeGene) ? 1.0 : 0.0;
            }
            case String ignored when chromosomeGene instanceof String -> {
                return targetGene.equals(chromosomeGene) ? 1.0 : 0.0;
            }
            default -> {
            }
        }

        //Mixed type (try numeric distance fallback)
        try {
            double t = tryParseDouble(targetGene);
            double c = tryParseDouble(chromosomeGene);
            double diff = Math.abs(t - c);
            return Math.exp(-Math.pow(diff, 2) / 4.0);
        } catch (NumberFormatException e) {
            return targetGene.equals(chromosomeGene) ? 1.0 : 0.0;
        }
    }

    private double tryParseDouble(Object obj) {
        if (obj instanceof Number)
            return ((Number) obj).doubleValue();
        return Double.parseDouble(obj.toString());
    }
}
