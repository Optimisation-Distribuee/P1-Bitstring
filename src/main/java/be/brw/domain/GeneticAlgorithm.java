package be.brw.domain;

import be.brw.config.GAConfig;
import be.brw.domain.strategy.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithm {

    private final GAConfig config;
    private final Random random;
    private Population population;

    public GeneticAlgorithm(GAConfig configuration){
        this.config = configuration;
        this.random = new Random(config.getSeed());

        this.population = new Population(
                config.getSolution(),
                config.getPopulationSize(),
                config.getMinGenomeLength(),
                config.getMaxGenomeLength(),
                config.getSeed()
        );
    }

    public Individual runAlgorithm() {
        int maxGeneration = config.getMaxGeneration();
        MutationTargetStrategy mutTarget = config.getMutationTargetStrategy();
        int eliteCount = (int) Math.round(config.getPopulationSize() * (1.0 - config.getCrossoverRate()));
        for (int i = 0; i <= maxGeneration; i++){
            List<Individual> individuals = this.population.getIndividuals();

            for (Individual individual: individuals){
                if(individual.getGenome().equals(this.config.getSolution())){
                    System.out.println("Solution found in " + i + " generations");
                    return this.population.getFittest();
                }
            }

            List<Individual> survivors = selection(individuals, eliteCount);

            if (mutTarget == MutationTargetStrategy.PARENTS || mutTarget == MutationTargetStrategy.BOTH) {
                for (int j = 0; j < survivors.size(); j++) {
                    if (random.nextDouble() < config.getMutationRate()) {
                        survivors.set(j, mutate(survivors.get(j)));
                    }
                }
            }

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
            survivors.addAll(children);
            this.population = new Population(config.getSolution(), survivors, config.getSeed());
        }
        System.out.println("No solution found in " + maxGeneration + " generations");
        return this.population.getFittest();
    }

    private Individual crossover(Individual individual1, Individual individual2){
        CrossoverStrategy crossoverStrategy = config.getCrossoverStrategy();
        CrossoverLeftoverStrategy crossoverLeftoverStrategy = config.getCrossoverLeftoverStrategy();

        List<Byte> newGenome = new ArrayList<>();
        List<Byte> leftovers;

        int len1 = individual1.getGenomeLength();
        int len2 = individual2.getGenomeLength();

        if(len1 == 1 || len2 == 1){
            if(individual1.getFitness() > individual2.getFitness()){
                return individual1;
            }else{
                return individual2;
            }
        }

        int minLength = Math.min(len1, len2);
        int maxLength = Math.max(len1, len2);

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
                cutPointFirst = this.random.nextInt(minLength - 1);
                cutPointSecond = this.random.nextInt(cutPointFirst + 1, maxLength - 1);

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
                    tournamentWinners.addAll(individuals.subList(0, tournamentSize));
                }
                return tournamentWinners;
        }
        throw new UnsupportedOperationException("selectionStrategy was not ELITISM, ROULETTE or TOURNAMENT");
    }
}
