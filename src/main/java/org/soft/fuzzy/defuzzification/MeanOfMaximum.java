package org.soft.fuzzy.defuzzification;

import org.soft.fuzzy.core.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MeanOfMaximum implements Defuzzifier {

    private final int resolution;

    public MeanOfMaximum() {
        this(100);
    }

    public MeanOfMaximum(int resolution) {
        this.resolution = resolution;
    }

    @Override
    public double defuzzify(Map<String, Double> aggregatedOutputs, FuzzyVariable outputVariable) {
        double min = outputVariable.getMinRange();
        double max = outputVariable.getMaxRange();
        double step = (max - min) / resolution;

        double maxMembership = Double.NEGATIVE_INFINITY;
        List<Double> maxPoints = new ArrayList<>();

        for (int i = 0; i <= resolution; i++) {
            double x = min + i * step;
            double mu = 0.0;

            for (var entry : aggregatedOutputs.entrySet()) {
                String setName = entry.getKey();
                double firingDegree = entry.getValue();
                FuzzySet set = outputVariable.getFuzzySet(setName);

                if (set != null) {
                    double value = set.fuzzify(x);
                    mu = Math.max(mu, Math.min(firingDegree, value));
                }
            }

            if (mu > maxMembership) {
                maxMembership = mu;
                maxPoints.clear();
                maxPoints.add(x);
            } else if (mu == maxMembership) {
                maxPoints.add(x);
            }
        }

        // average all points with max membership
        double sum = 0.0;
        for (double val : maxPoints) sum += val;
        return (maxPoints.isEmpty()) ? 0.0 : sum / maxPoints.size();
    }
}
