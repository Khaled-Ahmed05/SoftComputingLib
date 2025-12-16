package org.soft.nn.core.loss;

public class MeanSquaredError implements LossFunction {
    
    @Override
    public double compute(double[] yTrue, double[] yPred) {
        double sum = 0.0;
        for (int i = 0; i < yTrue.length; i++) {
            double diff = yTrue[i] - yPred[i];
            sum += diff * diff;
        }
        return sum / yTrue.length;
    }

    @Override
    public double[] derivative(double[] yTrue, double[] yPred) {
        double[] dLoss = new double[yTrue.length];
        for (int i = 0; i < yTrue.length; i++) {
            dLoss[i] = 2 * (yPred[i] - yTrue[i]) / yTrue.length;        
        }
        return dLoss;
    }

    @Override
    public String getName() {
        return "MeanSquaredError";
    }
}
