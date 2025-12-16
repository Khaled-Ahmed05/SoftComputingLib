package org.soft.nn.core.optimizers;

public interface Optimizer {

    void update(double[][] weights, double[][] dWeights);

    void update(double[] biases, double[] dBiases);

    double getLearningRate();

    String getName();
}
