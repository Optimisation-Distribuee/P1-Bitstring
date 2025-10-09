package be.brw.domain;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Population {

    private final Random random;

    private List<Individual> individuals;

    public Population(List<Byte> solution, int size, int minGenomeLength, int maxGenomeLength, int seed){
        this.random = new Random(seed);
        this.initPopulation(size, minGenomeLength, maxGenomeLength);
        this.updateFitness(solution);
    }

    public Population(List<Byte> solution, int size, int defaultGenomeLength, int seed){
        this.random = new Random(seed);
        this.initPopulation(size, defaultGenomeLength);
        this.updateFitness(solution);
    }

    public Population(List<Byte> solution, List<Individual> individuals, int seed){
        this.random = new Random(seed);
        this.individuals = individuals;
        this.updateFitness(solution);
    }

    public void updateFitness(List<Byte> solution) {
        for (Individual individual : this.individuals) {
            List<Byte> genome = individual.getGenome();

            int fitness = 0;
            for (int i = 0; i < solution.size(); i++) {
                if (i >= genome.size()){
                    break;
                }

                if (solution.get(i).equals(genome.get(i))) {
                    fitness++;
                }
            }
            individual.setFitness(fitness);
        }
    }

    public List<Individual> getIndividuals() {
        return individuals;
    }

    public List<Integer> getAllFitness(){
        List<Integer> allFitness = new ArrayList<>();
        for (Individual individual : this.individuals) {
            allFitness.add(individual.getFitness());
        }
        return allFitness;
    }

    private void initPopulation(int size, int minGenomeLength, int maxGenomeLength) {
        this.individuals = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int targetLength = random.nextInt(maxGenomeLength - minGenomeLength + 1) + minGenomeLength;

            List<Byte> genome = this.generateRandomGenome(targetLength);
            Individual individual = new Individual(genome);
            this.individuals.add(individual);
        }
    }

    private void initPopulation(int size, int defaultGenomeLength){
        this.individuals = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            List<Byte> genome = this.generateRandomGenome(defaultGenomeLength);
            Individual individual = new Individual(genome);
            this.individuals.add(individual);
        }
    }

    private List<Byte> generateRandomGenome(int genomeLength) {
        List<Byte> genome = new ArrayList<>();
        for (int j = 0; j < genomeLength; j++) {
            genome.add((byte) random.nextInt(2));
        }
        return genome;
    }
}
