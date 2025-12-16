package org.soft.nn.data;

import java.util.Random;

public class DataSplitter {

    private final Random random;

    public DataSplitter() {
        this.random = new Random();
    }

    public DataSplitter(long seed) {
        this.random = new Random(seed);
    }

    public SplitResult split(Dataset dataset, double trainRatio) {

        if (trainRatio <= 0 || trainRatio >= 1) {
            throw new IllegalArgumentException("Train ratio must be between 0 and 1.");
        }

        int size = dataset.size();
        int trainSize = (int) (size * trainRatio);

        double[][] X = dataset.getFeatures();
        double[][] y = dataset.getLabels();

        int[] indices = shuffleIndices(size);

        double[][] XTrain = new double[trainSize][];
        double[][] yTrain = new double[trainSize][];
        double[][] XTest = new double[size - trainSize][];
        double[][] yTest = new double[size - trainSize][];

        for (int i = 0; i < trainSize; i++) {
            XTrain[i] = X[indices[i]];
            yTrain[i] = y[indices[i]];
        }

        for (int i = trainSize; i < size; i++) {
            XTest[i - trainSize] = X[indices[i]];
            yTest[i - trainSize] = y[indices[i]];
        }

        return new SplitResult(
                new Dataset(XTrain, yTrain),
                new Dataset(XTest, yTest)
        );
    }

    private int[] shuffleIndices(int size) {
        int[] indices = new int[size];
        for (int i = 0; i < size; i++) {
            indices[i] = i;
        }

        for (int i = size - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = indices[i];
            indices[i] = indices[j];
            indices[j] = temp;
        }
        return indices;
    }
}
