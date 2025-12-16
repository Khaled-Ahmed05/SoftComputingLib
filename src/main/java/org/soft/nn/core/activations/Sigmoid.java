package org.soft.nn.core.activations;

public class Sigmoid implements ActivationFunction {

    private double[] output;

    @Override
    public double[] forward(double[] z) {
        output = new double[z.length];
        for (int i = 0; i < z.length; i++) {
            output[i] = 1.0 / (1.0 + Math.exp(-z[i]));
        }
        return output;
    }

    @Override
    public double[] backward(double[] dA) {
        double[] dZ = new double[dA.length];
        for (int i = 0; i < dA.length; i++) {
            dZ[i] = dA[i] * output[i] * (1 - output[i]);
        }
        return dZ;
    }

    @Override
    public String getName(){
        return "Sigmoid";
    }
}
