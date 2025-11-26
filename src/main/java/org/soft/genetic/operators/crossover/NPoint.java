package org.soft.genetic.operators.crossover;

import java.util.*;

public class NPoint<T> extends AbstractICrossover<T> {
    private final int nPoints;

    public NPoint(int nPoints) {
        this.nPoints = nPoints;
    }

    @Override
    public List<List<T>> crossover(List<T> parent1, List<T> parent2) {
        validateParents(parent1, parent2);

        int length = parent1.size();
        List<T> child1 = new ArrayList<>(parent1);
        List<T> child2 = new ArrayList<>(parent2);
        Random random = new Random();

        Set<Integer> points = new TreeSet<>();
        while (points.size() < nPoints) {
            int p = random.nextInt(length - 1) + 1;
            points.add(p);
        }

        boolean swap = false;
        List<Integer> crossoverPoints = new ArrayList<>(points);
        crossoverPoints.add(length);

        int start = 0;
        for (int point : crossoverPoints) {
            if (swap) {
                for (int i = start; i < point; i++) {
                    child1.set(i, parent2.get(i));
                    child2.set(i, parent1.get(i));
                }
            }
            swap = !swap;
            start = point;
        }

        return Arrays.asList(child1, child2);
    }
}
