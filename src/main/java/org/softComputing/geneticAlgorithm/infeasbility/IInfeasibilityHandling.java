package org.softComputing.geneticAlgorithm.infeasbility;

import java.util.List;

public interface IInfeasibilityHandling<T> {
    List<T> specialChars(List<T> mutated, double lowerBound, double upperBound);
}
