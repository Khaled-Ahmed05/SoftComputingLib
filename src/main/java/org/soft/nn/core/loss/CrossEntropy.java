package org.soft.nn.core.loss;

public class CrossEntropy implements LossFunction {
    
    private static final double EPSILON = 1e-15;

    @Override
    public double compute(double[] yTrue, double[] yPred) {
        double loss = 0.0;
        for (int i = 0; i < yTrue.length; i++) {
            double yHat = Math.min(Math.max(yPred[i], EPSILON), 1 - EPSILON);
            loss += -(yTrue[i] * Math.log(yHat)
                    + (1 - yTrue[i]) * Math.log(1 - yHat));
        }
        return loss / yTrue.length;
    }

    @Override
    public double[] derivative(double[] yTrue, double[] yPred) {
        double[] dLoss = new double[yTrue.length];
        for (int i = 0; i < yTrue.length; i++) {
            double yHat = Math.min(Math.max(yPred[i], EPSILON), 1 - EPSILON);
            dLoss[i] = (yHat - yTrue[i]) / (yHat * (1 - yHat));
        }
        return dLoss;
    }

    @Override
    public String getName() {
        return "CrossEntropy";
    }   
}
