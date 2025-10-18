package org.softComputing.geneticAlgorithm.operators.mutation;

import java.util.List;

public interface IMutation<T> {
    List<T> mutate(List<T> chromosome);
}
