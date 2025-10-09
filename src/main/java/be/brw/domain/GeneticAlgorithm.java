package be.brw.domain;

import be.brw.config.GAConfig;
import be.brw.domain.strategy.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Implements the core logic of a genetic algorithm to solve a bitstring-matching problem.
 * <p>
 * This class orchestrates the evolutionary process, including population initialization,
 * selection, crossover, and mutation, over a series of generations. It is configured
 * via a {@link GAConfig} object, which specifies all parameters for the simulation.
 * </p>
 */
public class GeneticAlgorithm {

    /**
     * Configuration object containing all parameters for the genetic algorithm.
     */
    private final GAConfig config;
    /**
     * Random number generator used for all stochastic operations (selection, crossover, mutation).
     */
    private final Random random;
    /**
     * The current population of individuals. This object is replaced with a new population each generation.
     */
    private Population population;

    /**
     * Constructs a new GeneticAlgorithm instance and initializes the first population.
     *
     * @param configuration The {@link GAConfig} object that defines the parameters of the algorithm.
     */
    public GeneticAlgorithm(GAConfig configuration){
        this.config = configuration;
        this.random = new Random(config.getSeed());

        // Initialize the starting population based on the configuration.
        this.population = new Population(
                config.getSolution(),
                config.getPopulationSize(),
                config.getMinGenomeLength(),
                config.getMaxGenomeLength(),
                config.getSeed()
        );
    }

    /**
     * Executes the genetic algorithm for a configured number of generations.
     * <p>
     * The algorithm proceeds generation by generation, applying selection, crossover, and mutation
     * to evolve the population toward the target solution. The process terminates if a perfect
     * solution is found or the maximum number of generations is reached.
     * </p>
     * @return The fittest individual found after the algorithm completes or finds a solution.
     */
    public Individual runAlgorithm() {
        int maxGeneration = config.getMaxGeneration();
        MutationTargetStrategy mutTarget = config.getMutationTargetStrategy();
        int eliteCount = (int) Math.round(config.getPopulationSize() * (1.0 - config.getCrossoverRate()));
        for (int i = 0; i <= maxGeneration; i++){
            List<Individual> individuals = this.population.getIndividuals();

            // Check for a perfect solution in the current population.
            for (Individual individual: individuals){
                if(individual.getGenome().equals(this.config.getSolution())){
                    System.out.println("Solution found in " + i + " generations");
                    return this.population.getFittest();
                }
            }

            // 1. Selection: Select the "elite" individuals to survive to the next generation.
            List<Individual> survivors = selection(individuals, eliteCount);

            // 2. Mutation (on parents): Optionally mutate the selected survivors.
            if (mutTarget == MutationTargetStrategy.PARENTS || mutTarget == MutationTargetStrategy.BOTH) {
                for (int j = 0; j < survivors.size(); j++) {
                    if (random.nextDouble() < config.getMutationRate()) {
                        survivors.set(j, mutate(survivors.get(j)));
                    }
                }
            }

            // 3. Crossover and Mutation (on children): Create new children to fill the rest of the population.
            List<Individual> children = new ArrayList<>(config.getPopulationSize() - eliteCount);
            while (eliteCount + children.size() < config.getPopulationSize()) {
                List<Individual> parents = selection(survivors, 2);
                Individual child = crossover(parents.getFirst(), parents.getLast());
                if (mutTarget == MutationTargetStrategy.PARENTS || mutTarget == MutationTargetStrategy.BOTH) {
                    if (random.nextDouble() < config.getMutationRate()) {
                        mutate(child);
                    }
                }

                children.add(child);
            }

            // Create the next generation's population from survivors and new children.
            survivors.addAll(children);
            this.population = new Population(config.getSolution(), survivors, config.getSeed());
        }

        System.out.println("No solution found in " + maxGeneration + " generations");
        return this.population.getFittest();
    }

    /**
     * Performs crossover between two parent individuals to create a new child.
     * <p>
     * The method of crossover (e.g., one-point, uniform) and the handling of leftover genes
     * from parents of different lengths are determined by the {@link GAConfig}.
     * </p>
     * @param individual1 The first parent.
     * @param individual2 The second parent.
     * @return A new {@link Individual} (child) resulting from the crossover.
     */
    private Individual crossover(Individual individual1, Individual individual2){
        CrossoverStrategy crossoverStrategy = config.getCrossoverStrategy();
        CrossoverLeftoverStrategy crossoverLeftoverStrategy = config.getCrossoverLeftoverStrategy();

        List<Byte> newGenome = new ArrayList<>();
        List<Byte> leftovers;

        int len1 = individual1.getGenomeLength();
        int len2 = individual2.getGenomeLength();

        // Guard against crossover with very short genomes, returning the fitter parent instead.
        if(len1 == 1 || len2 == 1){
            if(individual1.getFitness() > individual2.getFitness()){
                return individual1;
            }else{
                return individual2;
            }
        }

        int minLength = Math.min(len1, len2);

        List<Byte> firstGenome = individual1.getGenome();
        List<Byte> secondGenome = individual2.getGenome();

        int cutPointFirst, cutPointSecond;
        List<Byte> firstPart, secondPart, thirdPart, childGenome;
        switch(crossoverStrategy){
            case ONE_POINT:
                cutPointFirst = this.random.nextInt(minLength - 1);

                firstPart  = firstGenome.subList(0, cutPointFirst);
                secondPart = secondGenome.subList(cutPointFirst, len2);

                childGenome = new ArrayList<>();
                childGenome.addAll(firstPart);
                childGenome.addAll(secondPart);

                return new Individual(childGenome);
            case TWO_POINT:
                // Ensure there are at least two points to choose from for a valid crossover.
                if (minLength < 2) {
                    // Fallback to a simpler crossover or return a parent if genomes are too short.
                    return individual1.getFitness() > individual2.getFitness() ? individual1 : individual2;
                }
                cutPointFirst = this.random.nextInt(minLength - 1) + 1; // Range [1, minLength - 1]
                cutPointSecond = this.random.nextInt(minLength - cutPointFirst) + cutPointFirst; // Range [cutPointFirst, minLength - 1]

                firstPart  = individual1.getGenome().subList(0, cutPointFirst);
                secondPart = individual2.getGenome().subList(cutPointFirst, cutPointSecond);
                thirdPart = individual1.getGenome().subList(cutPointSecond, len2);

                childGenome = new ArrayList<>();
                childGenome.addAll(firstPart);
                childGenome.addAll(secondPart);
                childGenome.addAll(thirdPart);

                return new Individual(childGenome);
            case UNIFORM:
                int pickA = minLength / 2;
                int pickB = minLength / 2;

                boolean choice = random.nextBoolean();
                if(choice){
                    pickA += minLength % 2;
                }else{
                    pickB += minLength % 2;
                }

                int cursor = 0;
                while(pickA + pickB > 0){
                    choice = random.nextBoolean();
                    if(choice && pickA > 0){
                        newGenome.add(firstGenome.get(cursor));
                        pickA--;
                        cursor++;
                        continue;
                    }

                    if(!choice && pickB > 0){
                        newGenome.add(secondGenome.get(cursor));
                        pickB--;
                        cursor++;
                    }
                }

                leftovers = (firstGenome.size() > secondGenome.size())
                        ? firstGenome.subList(minLength, firstGenome.size())
                        : secondGenome.subList(minLength, secondGenome.size());
                break;
            case ARITHMETIC:
                for(int i = 0; i < minLength; i++){
                    byte geneA = firstGenome.get(i);
                    byte geneB = secondGenome.get(i);

                    newGenome.add((byte) ((int)geneA ^ (int)geneB));
                }

                leftovers = (firstGenome.size() > secondGenome.size())
                        ? firstGenome.subList(minLength, firstGenome.size())
                        : secondGenome.subList(minLength, secondGenome.size());

                break;
            default:
                throw new UnsupportedOperationException("crossoverStrategy was not ONE_POINT, TWO_POINT, UNIFORM, ARITHMETIC");
        }

        if(!leftovers.isEmpty()) {
            switch (crossoverLeftoverStrategy) {
                case KEEP_ALL_OR_NOTHING_RANDOMLY:
                    if(random.nextBoolean()){
                        newGenome.addAll(leftovers);
                    }
                    break;
                case KEEP_ONE_OR_NOT_RANDOMLY:
                    for(byte leftover: leftovers){
                        if(random.nextBoolean()){
                            newGenome.add(leftover);
                        }
                    }
                    break;
                case KEEP_ONLY_FROM_FITTEST_PARENT:
                    if(individual1.getFitness() >= individual2.getFitness()){
                        newGenome.addAll(firstGenome.subList(minLength, firstGenome.size()));
                    }else{
                        newGenome.addAll(secondGenome.subList(minLength, secondGenome.size()));
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("crossoverLeftoverStrategy was not KEEP_ALL_OR_NOTHING_RANDOMLY, KEEP_ONE_OR_NOT_RANDOMLY or KEEP_ONLY_FROM_FITTEST_PARENT");
            }
        }
        return new Individual(newGenome);
    }

    /**
     * Applies a mutation to an individual's genome.
     * <p>
     * A random mutation type (ADD, REMOVE, FLIP) is chosen. The mutation is then applied
     * based on the corresponding rate defined in {@link GAConfig}.
     * </p>
     * @param individual The individual to mutate.
     * @return The same individual, now potentially mutated.
     */
    private Individual mutate(Individual individual){
        MutationStrategy randomMutationStrategy = MutationStrategy.values()[random.nextInt(MutationStrategy.values().length)];

        int randomGeneIndex = random.nextInt(individual.getGenomeLength());
        Byte randomGene = (byte) random.nextInt(2);

        boolean shouldMutate;
        switch(randomMutationStrategy){
            case ADD:
                shouldMutate = random.nextDouble() <= this.config.getBitAddRate();
                if(!shouldMutate){
                    break;
                }

                individual.addGene(randomGene);
                break;
            case REMOVE:
                shouldMutate = random.nextDouble() <= this.config.getBitRemoveRate();
                if(!shouldMutate){
                    break;
                }

                if(individual.getGenomeLength() == 1){
                    break;
                }
                individual.removeGene(randomGeneIndex);
                break;
            case FLIP:
                shouldMutate = random.nextDouble() <= this.config.getBitFlipRate();
                if(!shouldMutate){
                    break;
                }

                individual.setGene(randomGeneIndex, randomGene);
                break;
            default:
                throw new UnsupportedOperationException("MutationStrategy was not ADD, REMOVE or FLIP");
        }

        return individual;
    }

    /**
     * Selects a subset of individuals from a given list based on the configured selection strategy.
     *
     * @param individuals The pool of individuals to select from.
     * @param selectionSize The number of individuals to select.
     * @return A new list containing the selected individuals.
     * @throws UnsupportedOperationException if the configured selection strategy is not recognized.
     * @see SelectionStrategy
     */
    public List<Individual> selection(List<Individual> individuals, int selectionSize) {
        SelectionStrategy selectionStrategy = config.getSelectionStrategy();
        switch (selectionStrategy) {
            case ELITISM:
                // Select the fittest individuals
                Collections.sort(individuals);
                return individuals.subList(0,selectionSize);
            case ROULETTE:
                // Fitness-proportionate selection
                List<Individual> rouletteWinners = new ArrayList<>(selectionSize);

                int totalFitness = 0;
                for (Individual i: individuals) {
                    totalFitness += i.getFitness();
                }

                // If total fitness is zero, all individuals are equally bad.
                // Fallback to random selection.
                if (totalFitness == 0) {
                    Collections.shuffle(individuals, random);
                    return individuals.subList(0, selectionSize);
                }

                for (int i = 0; i < selectionSize; i++) {
                    int pick = this.random.nextInt(totalFitness);

                    int rouletteSum = 0;
                    for (Individual individual : individuals) {
                        rouletteSum += individual.getFitness();
                        if (rouletteSum >= pick) {
                            rouletteWinners.add(individual);
                            break;
                        }
                    }
                }
                return rouletteWinners;
            case TOURNAMENT:
                // Select the fittest individuals from a random sample
                int tournamentSize = config.getTournamentSize();
                List<Individual> tournamentWinners = new ArrayList<>(selectionSize);
                for (int i = 0; i < selectionSize; i++) {
                    Collections.shuffle(individuals, random);
                    List<Individual> tournamentContestants = individuals.subList(0, tournamentSize);
                    tournamentWinners.add(Collections.max(tournamentContestants));
                }
                return tournamentWinners;
        }
        throw new UnsupportedOperationException("selectionStrategy was not ELITISM, ROULETTE or TOURNAMENT");
    }
}
