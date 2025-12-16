package org.soft.nn.core.initializers;

import java.util.Random;

public class RandomUniform implements Initializer {

    private double min;
    private double max;
    private final Random random = new Random();

    public void RandomUniformInitializer(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public void RandomUniformInitializer() {
        this.min = -0.5;
        this.max = 0.5;
    }

    @Override
    public double[][] initializeWeights(int outputSize, int inputSize) {
        double[][] weights = new double[outputSize][inputSize];
        for (int i = 0; i < outputSize; i++) {
            for (int j = 0; j < inputSize; j++) {
                weights[i][j] = min + (max - min) * random.nextDouble();
            }
        }
        return weights;
    }

    @Override
    public double[] initializeBiases(int size) {
        double[] biases = new double[size];
        for (int i = 0; i < size; i++) {
            biases[i] = 0.0;
        }
        return biases;
    }

    @Override
    public String getName() {
        return "RandomUniform";
    }
}
