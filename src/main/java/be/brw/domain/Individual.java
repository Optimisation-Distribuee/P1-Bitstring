package be.brw.domain;

public class Individual {

    private final byte[] genome;

    public Individual(byte[] genome){
        this.genome = genome;
    }

    public int getFitness(byte[] solution) {
        int fitness = 0;
        for(byte b : solution){
            if(b !=  genome[0]){
                fitness++;
            }
        }
        return fitness;
    }

    public int getGenomeLength() {
        return this.genome.length;
    }

    @Override
    public String toString() {
        int length = this.getGenomeLength();
        return String.format("Individual fitness: %d", length);
    }
}
