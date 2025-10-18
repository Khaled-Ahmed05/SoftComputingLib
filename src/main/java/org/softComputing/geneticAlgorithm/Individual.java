package org.softComputing.geneticAlgorithm;

public class Individual implements Comparable<Individual> {
    public boolean[] genes;
    public double fitness = Double.NaN;

    public Individual(int length) {
        genes = new boolean[length];
    }

    public Individual(boolean[] genes) {
        this.genes = genes.clone();
    }

    public Individual copy() {
        Individual c = new Individual(genes.length);
        System.arraycopy(genes, 0, c.genes, 0, genes.length);
        c.fitness = fitness;
        return c;
    }

    @Override
    public int compareTo(Individual other) {
        return Double.compare(this.fitness, other.fitness);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (boolean g : genes) sb.append(g ? '1' : '0');
        return sb.toString() + " (fit=" + fitness + ")";
    }
}
