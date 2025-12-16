package org.soft.nn.data;

public class Dataset {

    private final double[][] features;
    private final double[][] labels;

    public Dataset(double[][] features, double[][] labels) {
        if (features == null || labels == null) {
            throw new IllegalArgumentException("Features and labels cannot be null.");
        }
        if (features.length != labels.length) {
            throw new IllegalArgumentException("Features and labels must have same number of samples.");
        }
        this.features = features;
        this.labels = labels;
    }

    public int size() {
        return features.length;
    }

    public double[][] getFeatures() {
        return features;
    }

    public double[][] getLabels() {
        return labels;
    }
}
