package org.soft.nn.core.activations;

public class Linear implements ActivationFunction {

    
    @Override
    public double[] forward(double[] z) {
        return z;
    }
    
    @Override
    public double[] backward(double[] dA) {
        return dA;
    }

    @Override
    public String getName() {
        return "Linear";
    }
}
