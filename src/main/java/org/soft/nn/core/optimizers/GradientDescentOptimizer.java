package org.soft.nn.core.optimizers;

public class GradientDescentOptimizer implements Optimizer {

    private final double learningRate;

    public GradientDescentOptimizer(double learningRate) {
        this.learningRate = learningRate;
    }

    @Override
    public void update(double[][] weights, double[][] dWeights) {
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                weights[i][j] -= learningRate * dWeights[i][j];
            }
        }
    }

    @Override
    public void update(double[] biases, double[] dBiases) {
        for (int i = 0; i < biases.length; i++) {
            biases[i] -= learningRate * dBiases[i];
        }
    }

    @Override
    public double getLearningRate() {
        return learningRate;
    }

    @Override
    public String getName() {
        return "GradientDescent";
    }
}
