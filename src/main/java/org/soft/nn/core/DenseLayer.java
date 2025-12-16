package org.soft.nn.core;

import org.soft.nn.core.activations.ActivationFunction;
import org.soft.nn.core.initializers.Initializer;
import org.soft.nn.core.optimizers.Optimizer;
import org.soft.nn.utils.ShapeMismatchException;

public class DenseLayer extends Layer {

    private final int inputSize;
    private final int outputSize;

    private double[][] weights;
    private double[] biases;

    private double[][] dWeights;
    private double[] dBiases;

    private double[] z; // pre-activation

    private final ActivationFunction activation;
    private final Initializer initializer;

    public DenseLayer(int inputSize, int outputSize,
                      ActivationFunction activation,
                      Initializer initializer) {
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        this.activation = activation;
        this.initializer = initializer;
        initializeParameters();
    }
     
    private void initializeParameters() {
        this.weights = initializer.initializeWeights(outputSize, inputSize);
        this.biases = initializer.initializeBiases(outputSize);
    }
    
    @Override
    public double[] forward(double[] input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null.");
        }

        if (input.length != inputSize) {
            throw new ShapeMismatchException(
                "Expected input size " + inputSize + ", got " + input.length
            );
        }

        if (weights.length != outputSize || weights[0].length != inputSize) {
            throw new IllegalStateException("Weights shape mismatch: expected [" +
                outputSize + "][" + inputSize + "] but got [" +
                weights.length + "][" + weights[0].length + "]");
        }

        this.input = input;
        z = new double[outputSize];
        output = new double[outputSize];
        
        for (int i = 0; i < outputSize; i++) {
            double sum = biases[i];
            for (int j = 0; j < inputSize; j++) {
                sum += weights[i][j] * input[j];
            }
            z[i] = sum;
        }
        
        output = activation.forward(z);
        return output;
    }
    
    @Override
    public double[] backward(double[] dOutput) {
        if (dOutput == null) {
            throw new IllegalArgumentException("Gradient input cannot be null.");
        }

        if (dOutput.length != outputSize) {
            throw new ShapeMismatchException(
                "Expected gradient size " + outputSize + ", got " + dOutput.length
            );
        }

        if (weights.length != outputSize || weights[0].length != inputSize) {
            throw new IllegalStateException("Weights shape mismatch: expected [" +
                outputSize + "][" + inputSize + "] but got [" +
                weights.length + "][" + weights[0].length + "]");
        }

        double[] dZ = activation.backward(dOutput);
        
        dWeights = new double[outputSize][inputSize];
        dBiases = new double[outputSize];
        double[] dInput = new double[inputSize];
        
        // dW and db
        for (int i = 0; i < outputSize; i++) {
            dBiases[i] = dZ[i];
            for (int j = 0; j < inputSize; j++) {
                dWeights[i][j] = dZ[i] * input[j];
            }
        }
        
        // dInput
        for (int j = 0; j < inputSize; j++) {
            double sum = 0.0;
            for (int i = 0; i < outputSize; i++) {
                sum += weights[i][j] * dZ[i];
            }
            dInput[j] = sum;
        }
        
        return dInput;
    }
    
    @Override
    public void updateParameters(Optimizer optimizer) {
        optimizer.update(weights, dWeights);
        optimizer.update(biases, dBiases);
    }

    public double[][] getWeights() {
        return weights;
    }
    
    public double[][] getWeightGradients() {
        return dWeights;
    }
}
