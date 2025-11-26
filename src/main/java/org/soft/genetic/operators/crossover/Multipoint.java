package org.soft.genetic.operators.crossover;

import java.util.*;

public class Multipoint<T> extends AbstractICrossover<T> {

    @Override
    public List<List<T>> crossover(List<T> parent1, List<T> parent2) {
        validateParents(parent1, parent2);

        int length = parent1.size();
        List<T> child1 = new ArrayList<>(parent1);
        List<T> child2 = new ArrayList<>(parent2);

        Random random = new Random();

        // Determine number of crossover points (at least 1, at most length-1)
        int maxPoints = Math.min(3, length - 1); // optional: limit to 3 points
        int numPoints = 1 + random.nextInt(maxPoints);

        // Pick distinct crossover points in (1 to length-1)
        Set<Integer> points = new TreeSet<>();
        while (points.size() < numPoints) {
            int p = 1 + random.nextInt(length - 1);
            points.add(p);
        }

        // Add the end of the sequence to simplify segment swapping
        List<Integer> crossoverPoints = new ArrayList<>(points);
        crossoverPoints.add(length);

        boolean swap = false;
        int prev = 0;

        // Swap segments between parents at each crossover point
        for (int point : crossoverPoints) {
            if (swap) {
                for (int i = prev; i < point; i++) {
                    child1.set(i, parent2.get(i));
                    child2.set(i, parent1.get(i));
                }
            }
            swap = !swap;
            prev = point;
        }

        return Arrays.asList(child1, child2);
    }
}
