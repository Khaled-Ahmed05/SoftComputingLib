package org.soft.nn.data;

public class SplitResult {

    private final Dataset train;
    private final Dataset test;

    public SplitResult(Dataset train, Dataset test) {
        this.train = train;
        this.test = test;
    }

    public Dataset getTrain() {
        return train;
    }

    public Dataset getTest() {
        return test;
    }
}
