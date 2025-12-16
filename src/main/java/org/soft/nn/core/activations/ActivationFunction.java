package org.soft.nn.core.activations;

public interface ActivationFunction {

    // z is Pre-activation input
    // A is Activation output
    // dA is the gradient of the loss with respect to the activation output
    // dZ is the gradient of the loss with respect to the pre-activation input

    double[] forward(double[] z);

    double[] backward(double[] dA);

    String getName();
}
