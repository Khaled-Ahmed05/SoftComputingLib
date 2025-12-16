package org.soft.nn.core.loss;

public interface LossFunction {
    
    double compute(double[] yTrue, double[] yPred);

    double[] derivative(double[] yTrue, double[] yPred);

    String getName();
}
