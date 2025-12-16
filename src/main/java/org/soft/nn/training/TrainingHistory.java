package org.soft.nn.training;

import java.util.ArrayList;
import java.util.List;

public class TrainingHistory {

    private final List<Double> lossPerEpoch = new ArrayList<>();

    public void addLoss(double loss) {
        lossPerEpoch.add(loss);
    }

    public List<Double> getLossHistory() {
        return lossPerEpoch;
    }

    public int getEpochCount() {
        return lossPerEpoch.size();
    }
}
