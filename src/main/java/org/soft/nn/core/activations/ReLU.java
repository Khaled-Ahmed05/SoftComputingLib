package org.soft.nn.core.activations;

public class ReLU implements ActivationFunction {

    private double[] z;

    @Override
    public double[] forward(double[] z) {
        this.z = z.clone();
        double[] output = new double[z.length];
        for (int i = 0; i < z.length; i++) {
            output[i] = Math.max(0, z[i]);
        }
        return output;
    }

    @Override
    public double[] backward(double[] dA) {
        double[] dZ = new double[dA.length];
        for (int i = 0; i < dA.length; i++) {
            dZ[i] = z[i] > 0 ? dA[i] : 0.0;
        }
        return dZ;
    }

    @Override
    public String getName() {
        return "ReLU";
    }
}
