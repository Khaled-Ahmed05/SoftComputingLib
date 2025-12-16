package org.soft.nn.core;

import org.soft.nn.core.loss.LossFunction;
import org.soft.nn.core.optimizers.Optimizer;

import java.util.ArrayList;
import java.util.List;

public class NeuralNetwork {

    private final List<Layer> layers;
    private LossFunction lossFunction;
    private Optimizer optimizer;
    private boolean compiled;

    private double[] lastOutput;

    public NeuralNetwork() {
        this.layers = new ArrayList<>();
        this.compiled = false;
    }

    public void addLayer(Layer layer) {
        if (compiled) {
            throw new IllegalStateException("Cannot add layers after compilation.");
        }
        layers.add(layer);
    }

    public void compile(LossFunction lossFunction, Optimizer optimizer) {
        if (layers.isEmpty()) {
            throw new IllegalStateException("Network must have at least one layer.");
        }
        this.lossFunction = lossFunction;
        this.optimizer = optimizer;
        this.compiled = true;
    }

    public double[] forward(double[] input) {
        ensureCompiled();
        double[] output = input;
        for (Layer layer : layers) {
            output = layer.forward(output);
        }
        lastOutput = output;
        return output;
    }

    public void backward(double[] yTrue) {
        ensureCompiled();
        double[] dA = lossFunction.derivative(yTrue, lastOutput);

        for (int i = layers.size() - 1; i >= 0; i--) {
            dA = layers.get(i).backward(dA);
        }
    }

    public void updateParameters() {
        ensureCompiled();
        for (Layer layer : layers) {
            layer.updateParameters(optimizer);
        }
    }

    public double computeLoss(double[] yTrue) {
        ensureCompiled();
        return lossFunction.compute(yTrue, lastOutput);
    }

    public double[] predict(double[] input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null.");
        }
     
        return forward(input);
    }

    public double[][] predictBatch(double[][] inputs) {
        if (inputs == null || inputs.length == 0) {
            throw new IllegalArgumentException("Input batch cannot be null or empty.");
        }
        
        double[][] outputs = new double[inputs.length][];
        for (int i = 0; i < inputs.length; i++) {
            outputs[i] = predict(inputs[i]);
        }
        return outputs;
    }

    public double evaluate(double[][] X, double[][] y) {
        double totalLoss = 0.0;
        for (int i = 0; i < X.length; i++) {
            forward(X[i]);
            totalLoss += computeLoss(y[i]);
        }
        return totalLoss / X.length;
    }

    private void ensureCompiled() {
        if (!compiled) {
            throw new IllegalStateException("NeuralNetwork must be compiled before use.");
        }
    }

        public List<Layer> getLayers() {
        return layers;
    }

    public boolean isCompiled() {
        return compiled;
    }
}
