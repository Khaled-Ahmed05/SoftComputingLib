package org.soft.nn.core.initializers;

public interface Initializer {

    double[][] initializeWeights(int outputSize, int inputSize);

    double[] initializeBiases(int size);

    String getName();
}
