package org.softComputing.geneticAlgorithm.operators.crossover;

import java.util.*;

public abstract class AbstractICrossover<T> implements ICrossover<T> {

    protected void validateParents(List<T> parent1, List<T> parent2) {
        if (parent1 == null || parent2 == null)
            throw new IllegalArgumentException("Parents cannot be null.");
        if (parent1.size() != parent2.size())
            throw new IllegalArgumentException("Parents must be of the same length.");
    }
}
