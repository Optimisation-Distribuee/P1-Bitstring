package be.brw.domain;

import java.util.List;

public class Individual implements Comparable<Individual>{

    private final List<Byte> genome;
    private int fitness;

    public Individual(List<Byte> genome, int fitness) {
        this.genome = genome;
        this.fitness = fitness;
    }

    public Individual(List<Byte> genome){
        this.genome = genome;
        this.fitness = 0;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    public int getFitness() {
        return this.fitness;
    }

    public List<Byte> getGenome() {
        return this.genome;
    }

    public int getGenomeLength() {
        return this.genome.size();
    }

    public void setGene(int index, Byte gene){
        genome.set(index, gene);
    }

    public void addGene(Byte gene){
        genome.add(gene);
    }

    public void removeGene(int index){
        genome.remove(index);
    }


    @Override
    public int compareTo(Individual other) {
        return other.fitness - this.fitness;
    }

    @Override
    public String toString() {
        return "Individual{" +
                "genome=" + genome +
                ", fitness=" + fitness +
                '}';
    }
}
