package org.soft.fuzzy.defuzzification;

import org.soft.fuzzy.core.*;

import java.util.Map;

public class Centroid implements Defuzzifier {

    private final int resolution; // number of points to sample

    public Centroid() {
        this(100); // default 100 samples
    }

    public Centroid(int resolution) {
        this.resolution = resolution;
    }

    @Override
    public double defuzzify(Map<String, Double> aggregatedOutputs, FuzzyVariable outputVariable) {
        double sumWeighted = 0.0;
        double sumMembership = 0.0;

        double min = outputVariable.getMinRange();
        double max = outputVariable.getMaxRange();
        double step = (max - min) / resolution;

        for (int i = 0; i <= resolution; i++) {
            double x = min + i * step;
            double mu = 0.0;

            // compute aggregated membership at this x
            for (var entry : aggregatedOutputs.entrySet()) {
                String setName = entry.getKey();
                double firingDegree = entry.getValue();
                FuzzySet set = outputVariable.getFuzzySet(setName);

                if (set != null) {
                    double value = set.fuzzify(x);
                    mu = Math.max(mu, Math.min(firingDegree, value)); // Mamdani clipping
                }
            }

            sumWeighted += x * mu;
            sumMembership += mu;
        }

        return (sumMembership == 0.0) ? 0.0 : sumWeighted / sumMembership;
    }
}
