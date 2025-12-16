package org.soft.nn.core.initializers;

import java.util.Random;

public class Xavier implements Initializer {

    private final Random random = new Random();

    @Override
    public double[][] initializeWeights(int outputSize, int inputSize) {
        double limit = Math.sqrt(6.0 / (inputSize + outputSize));
        double[][] weights = new double[outputSize][inputSize];

        for (int i = 0; i < outputSize; i++) {
            for (int j = 0; j < inputSize; j++) {
                weights[i][j] = -limit + 2 * limit * random.nextDouble();
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
        return "Xavier";
    }
}
