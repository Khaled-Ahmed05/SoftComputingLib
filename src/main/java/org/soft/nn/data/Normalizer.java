package org.soft.nn.data;

public class Normalizer {

    private double[] mean;
    private double[] std;
    private boolean fitted = false;

    public void fit(double[][] data) {

        int features = data[0].length;
        mean = new double[features];
        std = new double[features];

        for (double[] row : data) {
            for (int j = 0; j < features; j++) {
                mean[j] += row[j];
            }
        }

        for (int j = 0; j < features; j++) {
            mean[j] /= data.length;
        }

        for (double[] row : data) {
            for (int j = 0; j < features; j++) {
                std[j] += Math.pow(row[j] - mean[j], 2);
            }
        }

        for (int j = 0; j < features; j++) {
            std[j] = Math.sqrt(std[j] / data.length);
            if (std[j] == 0) {
                std[j] = 1;
            }
        }

        fitted = true;
    }

    public double[][] transform(double[][] data) {
        if (!fitted) {
            throw new IllegalStateException("Normalizer must be fitted before transform.");
        }
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Data cannot be null or empty.");
        }

        double[][] normalized = new double[data.length][data[0].length];

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                normalized[i][j] = (data[i][j] - mean[j]) / std[j];
            }
        }
        return normalized;
    }

    public double[][] fitTransform(double[][] data) {
        fit(data);
        return transform(data);
    }
}
