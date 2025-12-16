package org.soft.nn.core;

import org.soft.nn.core.optimizers.Optimizer;

public abstract class Layer {

    protected double[] input;
    protected double[] output;

    public abstract double[] forward(double[] input);

    public abstract double[] backward(double[] dOutput);

    public abstract void updateParameters(Optimizer optimizer);

    public double[] getOutput() {
        return output;
    }
}
