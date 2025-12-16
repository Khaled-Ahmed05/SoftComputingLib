package org.soft.nn;

import org.soft.nn.core.*;
import org.soft.nn.core.activations.*;
import org.soft.nn.core.initializers.*;
import org.soft.nn.core.loss.*;
import org.soft.nn.core.optimizers.*;
import org.soft.nn.training.*;

import java.io.*;
import java.util.*;

public class NNMain {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String csvFile = "customer_data.csv";

        // 1. Load Data from CSV
        
        Dataset dataset = loadCSV(csvFile);
        if (dataset.X.length == 0) {
            System.out.println("Dataset is empty or invalid!");
            sc.close();
            return;
        }

        // testing. Shuffle dataset

        shuffleDataset(dataset.X, dataset.y);

        // 2. Normalize Features
        
        normalizeFeatures(dataset.X);

        // 3. Train/Test Split
        
        Map<String, double[][]> split = trainTestSplit(dataset.X, dataset.y, 0.75);
        double[][] X_train = split.get("X_train");
        double[][] y_train = split.get("y_train");
        double[][] X_test = split.get("X_test");
        double[][] y_test = split.get("y_test");

        // 4. Ask user for hyperparameters (with defaults)
        
        int inputNeurons = X_train[0].length;

        // NUM OF HIDDEN LAYERS
        System.out.println("Enter number of hidden layers (default 2):");
        String layersInput = sc.nextLine();
        int hiddenLayers = layersInput.isEmpty() ? 2 : Integer.parseInt(layersInput);

        // NEURONS PER HIDDEN LAYER
        int[] layersNeurons = new int[hiddenLayers + 2];
        layersNeurons[0] = inputNeurons;
        for (int i = 1; i <= hiddenLayers; i++) {
            System.out.printf("Neurons in hidden layer %d (default 16): ", i);
            String n = sc.nextLine();
            layersNeurons[i] = n.isEmpty() ? 16 : Integer.parseInt(n);
        }
        layersNeurons[hiddenLayers + 1] = 1;

        // ACTIVATIONS
        ActivationFunction[] activations = new ActivationFunction[hiddenLayers + 2];
        activations[0] = null;
        for (int i = 1; i <= hiddenLayers; i++) {
            System.out.printf("Activation for hidden layer %d [ReLU/Sigmoid/Tanh/Linear] (default ReLU): ", i);
            String act = sc.nextLine().toLowerCase();
            if (act.equals("sigmoid")) activations[i] = new Sigmoid();
            else if (act.equals("tanh")) activations[i] = new Tanh();
            else if (act.equals("linear")) activations[i] = new Linear();
            else activations[i] = new ReLU();
        }
        activations[hiddenLayers + 1] = new Sigmoid();

        // INITIALIZERS
        Initializer[] initializers = new Initializer[hiddenLayers + 2];
        initializers[0] = null;
        for (int i = 1; i <= hiddenLayers; i++) {
            System.out.printf("Initializer for hidden layer %d [He/Xavier/RandomUnifrom] (default He): ", i);
            String init = sc.nextLine().toLowerCase();
            if (init.equals("xavier")) initializers[i] = new Xavier();
            else if (init.equals("randomuniform")) initializers[i] = new RandomUniform();
            else initializers[i] = new He();
        }
        initializers[hiddenLayers + 1] = new Xavier();

        // LEARNING RATE
        System.out.println("Learning rate (default 0.01):");
        String lrInput = sc.nextLine();
        double lr = lrInput.isEmpty() ? 0.01 : Double.parseDouble(lrInput);
        Optimizer optimizer = new GradientDescentOptimizer(lr);

        // BATCH SIZE
        System.out.println("Batch size (default 32):");
        String batchInput = sc.nextLine();
        int batchSize = batchInput.isEmpty() ? 32 : Integer.parseInt(batchInput);

        // EPOCHS
        System.out.println("Epochs (default 1000):");
        String epochsInput = sc.nextLine();
        int epochs = epochsInput.isEmpty() ? 1000 : Integer.parseInt(epochsInput);
    
        // LOSS FUNCTION
        System.out.println("Loss function [mse / crossentropy] (default crossentropy):");
        String lossInput = sc.nextLine().trim().toLowerCase();
        LossFunction lossFunction;
        if (lossInput.equals("mse") || lossInput.equals("meansquared")) {
            lossFunction = new MeanSquaredError();
        } else {
            lossFunction = new CrossEntropy();
        }


        sc.close();

        // 5. Build Neural Network
        
        NeuralNetwork nn = new NeuralNetwork();
        for (int i = 1; i < layersNeurons.length; i++) {
            nn.addLayer(new DenseLayer(
                    layersNeurons[i - 1],
                    layersNeurons[i],
                    activations[i],
                    initializers[i]
            ));
        }
        nn.compile(lossFunction, optimizer);

        // 6. Train

        Trainer trainer = new Trainer(nn, epochs, batchSize);
        trainer.train(X_train, y_train);
        System.out.println("\nTraining Loss Trend:");
        printLossTrend(trainer.getTrainingHistory().getLossHistory());

        // 7. Evaluate
        
        System.out.println("\n--- Test Predictions ---");
        System.out.println("ID | Predicted | Actual | Error");
        System.out.println("--------------------------------");
        for (int i = 0; i < X_test.length; i++) {
            double[] output = nn.forward(X_test[i]);
            double predicted = output[0];
            double actual = y_test[i][0];
            double error = Math.abs(predicted - actual);
            String mark = error > 0.5 ? "false" : "true";
            System.out.printf("%-2d | %-9.3f | %-6.1f | %.3f %s\n",
                    i + 1, predicted, actual, error, mark);
        }

        // 8. Show Hyperparameters used

        System.out.println("\n=== Neural Network Configuration ===");
        System.out.println("Hidden Layers: " + hiddenLayers);
        for (int i = 1; i <= hiddenLayers; i++) {
            System.out.printf("Layer %d: %d neurons, Activation = %s, Initializer = %s\n",
                    i,
                    layersNeurons[i],
                    activations[i].getClass().getSimpleName(),
                    initializers[i].getClass().getSimpleName()
            );
        }
        System.out.printf("Learning Rate: %s | Batch Size: %d | Epochs: %d\n", lrInput, batchSize, epochs);
        System.out.println("Loss Function: " + lossFunction.getClass().getSimpleName());

        // --- Test Summary ---
        int correct = 0;
        double maxError = 0;
        for (int i = 0; i < X_test.length; i++) {
            double predicted = nn.forward(X_test[i])[0];
            double actual = y_test[i][0];
            double error = Math.abs(predicted - actual);
            if (error < 0.5) correct++;
            if (error > maxError) maxError = error;
        }

        System.out.println("\nTest Summary:");
        System.out.printf("Accuracy: %d%%\n", (int)(100.0 * correct / X_test.length));
        System.out.printf("Max Error: %.3f\n", maxError);
        System.out.printf("Misclassified Samples: %d/%d\n", X_test.length - correct, X_test.length);

}

    // Utility Classes & Methods
    
    public static class Dataset {
        double[][] X;
        double[][] y;
        public Dataset(double[][] X, double[][] y) {
            this.X = X;
            this.y = y;
        }
    }

    public static Dataset loadCSV(String filePath) {
        List<double[]> features = new ArrayList<>();
        List<double[]> labels = new ArrayList<>();

        Set<String> genderSet = new LinkedHashSet<>();
        Set<String> occupationSet = new LinkedHashSet<>();
        Set<String> regionSet = new LinkedHashSet<>();

        List<String[]> rawData = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean headerSkipped = false;
            while ((line = br.readLine()) != null) {
                if (!headerSkipped) { headerSkipped = true; continue; }
                String[] values = line.split(",");
                rawData.add(values);

                genderSet.add(values[1]);
                occupationSet.add(values[2]);
                regionSet.add(values[3]);
            }
        } catch (IOException e) {
            System.out.println("Failed to read CSV: " + e.getMessage());
            return new Dataset(new double[0][0], new double[0][0]);
        }

        // Maps for one-hot encoding
        Map<String, Integer> genderMap = createIndexMap(genderSet);
        Map<String, Integer> occupationMap = createIndexMap(occupationSet);
        Map<String, Integer> regionMap = createIndexMap(regionSet);

        for (String[] row : rawData) {
            List<Double> featureRow = new ArrayList<>();

            try { featureRow.add(Double.parseDouble(row[0])); }
            catch (NumberFormatException e) { featureRow.add(0.0); }

            double[] genderOH = oneHotVector(row[1], genderMap);
            for (double d : genderOH) featureRow.add(d);

            double[] occupationOH = oneHotVector(row[2], occupationMap);
            for (double d : occupationOH) featureRow.add(d);

            double[] regionOH = oneHotVector(row[3], regionMap);
            for (double d : regionOH) featureRow.add(d);

            double[] labelRow = new double[1];
            try { labelRow[0] = Double.parseDouble(row[4]); }
            catch (NumberFormatException e) { labelRow[0] = 0.0; }

            features.add(featureRow.stream().mapToDouble(d -> d).toArray());
            labels.add(labelRow);
        }

        return new Dataset(features.toArray(new double[0][]), labels.toArray(new double[0][]));
    }

    // Helper to create index map for one-hot encoding
    private static Map<String, Integer> createIndexMap(Set<String> set) {
        Map<String, Integer> map = new HashMap<>();
        int idx = 0;
        for (String val : set) {
            map.put(val, idx++);
        }
        return map;
    }

    // Helper to create one-hot vector
    private static double[] oneHotVector(String value, Map<String, Integer> map) {
        double[] vector = new double[map.size()];
        if (map.containsKey(value)) vector[map.get(value)] = 1.0;
        return vector;
    }

    public static void normalizeFeatures(double[][] X) {
        for (int j = 0; j < X[0].length; j++) {
            double min = Double.MAX_VALUE;
            double max = -Double.MAX_VALUE;
            for (int i = 0; i < X.length; i++) {
                min = Math.min(min, X[i][j]);
                max = Math.max(max, X[i][j]);
            }
            double range = max - min;
            if (range == 0) range = 1;
            for (int i = 0; i < X.length; i++) X[i][j] = (X[i][j] - min) / range;
        }
    }

    public static Map<String, double[][]> trainTestSplit(double[][] X, double[][] y, double trainRatio) {
        int total = X.length;
        int trainSize = (int) (total * trainRatio);
        double[][] X_train = new double[trainSize][X[0].length];
        double[][] y_train = new double[trainSize][y[0].length];
        double[][] X_test = new double[total - trainSize][X[0].length];
        double[][] y_test = new double[total - trainSize][y[0].length];
        for (int i = 0; i < trainSize; i++) {
            X_train[i] = Arrays.copyOf(X[i], X[i].length);
            y_train[i] = Arrays.copyOf(y[i], y[i].length);
        }
        for (int i = trainSize; i < total; i++) {
            X_test[i - trainSize] = Arrays.copyOf(X[i], X[i].length);
            y_test[i - trainSize] = Arrays.copyOf(y[i], y[i].length);
        }
        Map<String, double[][]> map = new HashMap<>();
        map.put("X_train", X_train);
        map.put("y_train", y_train);
        map.put("X_test", X_test);
        map.put("y_test", y_test);
        return map;
    }

    private static void printLossTrend(List<Double> losses) {
        int step = Math.max(1, losses.size() / 50);
        for (int i = 0; i < losses.size(); i += step) {
            System.out.printf("Epoch %4d: %-50s\n", i + 1, losses.get(i));
        }
    }

    public static void shuffleDataset(double[][] X, double[][] y) {
        Random rand = new Random(42);
        for (int i = X.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);

            // swap X
            double[] tempX = X[i];
            X[i] = X[j];
            X[j] = tempX;

            // swap y
            double[] tempY = y[i];
            y[i] = y[j];
            y[j] = tempY;
        }
    }
}
