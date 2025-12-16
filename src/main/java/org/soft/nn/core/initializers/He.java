package org.soft.nn.core.initializers;

import java.util.Random;

public class He implements Initializer {

    private final Random random = new Random();

    @Override
    public double[][] initializeWeights(int outputSize, int inputSize) {
        double stdDev = Math.sqrt(2.0 / inputSize);
        double[][] weights = new double[outputSize][inputSize];

        for (int i = 0; i < outputSize; i++) {
            for (int j = 0; j < inputSize; j++) {
                weights[i][j] = random.nextGaussian() * stdDev;
            }
        }
        return weights;
    }

    @Override
    public double[] initializeBiases(int size) {
        return new double[size];
    }

    @Override
    public String getName() {
        return "He";
    }
}
