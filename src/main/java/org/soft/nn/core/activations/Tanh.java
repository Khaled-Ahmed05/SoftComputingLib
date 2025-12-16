package org.soft.nn.core.activations;

public class Tanh implements ActivationFunction {

    private double[] output;

    @Override
    public double[] forward(double[] z) {
        output = new double[z.length];
        for (int i = 0; i < z.length; i++) {
            output[i] = Math.tanh(z[i]);
        }
        return output;
    }

    @Override
    public double[] backward(double[] dA) {
        double[] dZ = new double[dA.length];
        for (int i = 0; i < dA.length; i++) {
            dZ[i] = dA[i] * (1 - output[i] * output[i]);
        }
        return dZ;
    }

    @Override
    public String getName() {
        return "Tanh";
    }
}
