package org.soft.nn.training;

import org.soft.nn.core.NeuralNetwork;
import org.soft.nn.utils.ShapeMismatchException;

public class Trainer {

    private final NeuralNetwork network;
    private final int epochs;
    private final int batchSize;

    private final TrainingHistory history;

    public Trainer(NeuralNetwork network, int epochs, int batchSize) {
        this.network = network;
        this.epochs = epochs;
        this.batchSize = batchSize;
        this.history = new TrainingHistory();
    }

    public void train(double[][] X, double[][] y) {
        if (X == null || y == null) {
            throw new IllegalArgumentException("Training data cannot be null.");
        }
        if (X.length != y.length) {
            throw new ShapeMismatchException(
                "Features length " + X.length + " != labels length " + y.length
            );
        }
        if (epochs <= 0) {
            throw new IllegalArgumentException("Epochs must be positive.");
        }
        if (batchSize <= 0) {
            throw new IllegalArgumentException("Batch size must be positive.");
        }

        int numSamples = X.length;

        for (int epoch = 0; epoch < epochs; epoch++) {

            double epochLoss = 0.0;

            for (int start = 0; start < numSamples; start += batchSize) {

                int end = Math.min(start + batchSize, numSamples);

                for (int i = start; i < end; i++) {
                    network.forward(X[i]);
                    epochLoss += network.computeLoss(y[i]);
                    network.backward(y[i]);
                    network.updateParameters();
                }
            }

            epochLoss /= numSamples;
            history.addLoss(epochLoss);
        }
    }
 
    public TrainingHistory getTrainingHistory() {
        return history;
    }
}