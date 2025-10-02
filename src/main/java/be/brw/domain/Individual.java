package be.brw.domain;

import java.util.List;

public class Individual {

    private final List<Byte> genome;

    public Individual(List<Byte> genome){
        this.genome = genome;
    }

    public int getFitness(byte[] solution) {
        int fitness = 0;
        for(byte b : solution){
            if(b !=  genome.get(0)){
                fitness++;
            }
        }
        return fitness;
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
    public String toString() {
        int length = this.getGenomeLength();
        return String.format("Individual fitness: %d", length);
    }
}
