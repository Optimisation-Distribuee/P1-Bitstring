package be.brw.domain;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Population {

    private final Random random;

    private List<Individual> individuals;

    public Population(int size, int minGenomeLength, int maxGenomeLength, int seed){
        this.initPopulation(size, minGenomeLength, maxGenomeLength);
        this.random = new Random(seed);
    }

    public Population(int size, int defaultFatness, int seed){
        this.initPopulation(size, defaultFatness);
        this.random = new Random(seed);
    }

    public int getGenomeLength(int index) {
        return this.individuals.get(index).getGenomeLength();
    }

    public List<Integer> getGenomeLengths(){
        List<Integer> genomesLengths = new ArrayList<Integer>();
        for (Individual individual : this.individuals) {
            genomesLengths.add(individual.getGenomeLength());
        }
        return genomesLengths;
    }


    public int getFitness(int index, byte[] solution) {
        return this.individuals.get(index).getFitness(solution);
    }

    public List<Integer> getAllFitness(){
        List<Integer> allFitness = new ArrayList<Integer>();
        for (Individual individual : this.individuals) {
            allFitness.add(individual.getGenomeLength());
        }
        return allFitness;
    }

    public void initPopulation(int size, int minGenomeLength, int maxGenomeLength) {
        this.individuals = new ArrayList<Individual>();
        for (int i = 0; i < size; i++) {
            int targetLength = random.nextInt(maxGenomeLength - minGenomeLength + 1) + minGenomeLength;
            byte[] genome = new byte[targetLength];
            for (int j = 0; j < targetLength; j++) {
                genome[j] = (byte) random.nextInt(2);
            }

            Individual individual = new Individual(genome);
            this.individuals.add(individual);
        }
    }

    public void initPopulation(int size, int defaultGenomeLength){
        this.individuals = new ArrayList<Individual>();
        for (int i = 0; i < size; i++) {
            byte[] genome = new byte[defaultGenomeLength];
            for (int j = 0; j < defaultGenomeLength; j++) {
                genome[j] = (byte) random.nextInt(2);
            }

            Individual individual = new Individual(genome);
            this.individuals.add(individual);
        }
    }
}
