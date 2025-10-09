package be.brw.domain;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Represents a collection of {@link Individual} objects in a genetic algorithm.
 * <p>
 * This class manages the creation, initialization, and fitness evaluation of a population.
 * It is designed to solve a bitstring matching problem, where the fitness of an
 * individual is determined by how closely its genome matches a target solution.
 * </p>
 */
public class Population {

    /**
     * A random number generator for creating random genomes.
     */
    private final Random random;

    /**
     * The list of individuals that constitute the population.
     */
    private List<Individual> individuals;

    /**
     * Constructs a new population with randomly generated individuals of variable genome length.
     * After initialization, the fitness of each individual is calculated against the provided solution.
     *
     * @param solution The target bitstring solution used for fitness calculation.
     * @param size The number of individuals to create in the population.
     * @param minGenomeLength The minimum possible length for a randomly generated genome.
     * @param maxGenomeLength The maximum possible length for a randomly generated genome.
     * @param seed The seed for the random number generator to ensure reproducibility.
     */
    public Population(List<Byte> solution, int size, int minGenomeLength, int maxGenomeLength, int seed){
        this.random = new Random(seed);
        this.initPopulation(size, minGenomeLength, maxGenomeLength);
        this.updateFitness(solution);
    }

    /**
     * Constructs a new population with randomly generated individuals of a fixed genome length.
     * After initialization, the fitness of each individual is calculated against the provided solution.
     *
     * @param solution The target bitstring solution used for fitness calculation.
     * @param size The number of individuals to create in the population.
     * @param defaultGenomeLength The fixed length for all randomly generated genomes.
     * @param seed The seed for the random number generator to ensure reproducibility.
     */
    public Population(List<Byte> solution, int size, int defaultGenomeLength, int seed){
        this.random = new Random(seed);
        this.initPopulation(size, defaultGenomeLength);
        this.updateFitness(solution);
    }

    /**
     * Constructs a population from an existing list of individuals.
     * The fitness of each individual in the provided list is immediately calculated
     * against the given solution.
     *
     * @param solution The target bitstring solution used for fitness calculation.
     * @param individuals The pre-existing list of individuals to form the population.
     * @param seed The seed for the random number generator.
     */
    public Population(List<Byte> solution, List<Individual> individuals, int seed){
        this.random = new Random(seed);
        this.individuals = individuals;
        this.updateFitness(solution);
    }

    /**
     * Calculates and updates the fitness for every individual in the population.
     * <p>
     * Fitness is calculated by counting the number of matching genes at the same position
     * and then penalizing the score based on the difference in length between the
     * individual's genome and the target solution.
     * </p>
     *
     * @param solution The target bitstring to compare against.
     */
    public void updateFitness(List<Byte> solution) {
        for (Individual individual : this.individuals) {
            List<Byte> genome = individual.getGenome();

            int matchingGenes = 0;
            // Determine the comparison length to avoid IndexOutOfBoundsException
            int comparisonLength = Math.min(solution.size(), genome.size());

            for (int i = 0; i < comparisonLength; i++) {
                if (solution.get(i).equals(genome.get(i))) {
                    matchingGenes++;
                }
            }

            // Calculate the penalty for length difference.
            // TODO: determine how we want to do this
            int lengthDifference = Math.abs(genome.size() - solution.size());
            int finalFitness = matchingGenes - lengthDifference;

            individual.setFitness(finalFitness);
        }
    }

    /**
     * Returns the list of individuals in the population.
     *
     * @return The list of {@link Individual} objects.
     */
    public List<Individual> getIndividuals() {
        return individuals;
    }

    /**
     * Initializes the population with a specified number of individuals, each having a
     * randomly determined genome length within the given bounds.
     */
    private void initPopulation(int size, int minGenomeLength, int maxGenomeLength) {
        this.individuals = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int targetLength = random.nextInt(maxGenomeLength - minGenomeLength + 1) + minGenomeLength;

            List<Byte> genome = this.generateRandomGenome(targetLength);
            Individual individual = new Individual(genome);
            this.individuals.add(individual);
        }
    }

    /**
     * Initializes the population with a specified number of individuals, each having
     * a genome of a fixed, default length.
     */
    private void initPopulation(int size, int defaultGenomeLength){
        this.individuals = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            List<Byte> genome = this.generateRandomGenome(defaultGenomeLength);
            Individual individual = new Individual(genome);
            this.individuals.add(individual);
        }
    }

    /**
     * Generates a random genome (list of bytes) of a specified length.
     */
    private List<Byte> generateRandomGenome(int genomeLength) {
        List<Byte> genome = new ArrayList<>();
        for (int j = 0; j < genomeLength; j++) {
            genome.add((byte) random.nextInt(2));
        }
        return genome;
    }

    /**
     * Returns a string representation of the population.
     *
     * @return A string containing the list of individuals.
     */
    @Override
    public String toString() {
        return "Population{" +
                "individuals=" + individuals +
                '}';
    }

    /**
     * Finds and returns the individual with the highest fitness score in the population.
     *
     * @return The fittest {@link Individual}.
     * @throws java.util.NoSuchElementException if the population is empty.
     */
    public Individual getFittest() {
        if (individuals == null || individuals.isEmpty()) {
            throw new java.util.NoSuchElementException("Cannot find fittest individual in an empty population.");
        }
        return java.util.Collections.max(individuals, Comparator.comparingInt(Individual::getFitness));
    }

    public int size() {
        if (this.individuals == null) {
            return 0;
        }
        return this.individuals.size();
    }
}
