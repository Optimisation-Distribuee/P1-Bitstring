package be.brw.domain;

import be.brw.config.GAConfig;
import be.brw.domain.strategy.CrossoverLeftoverStrategy;
import be.brw.domain.strategy.CrossoverStrategy;
import be.brw.domain.strategy.MutationStrategy;

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
                config.getPopulationSize(),
                config.getMinGenomeLength(),
                config.getMaxGenomeLength(),
                config.getSeed()
        );
    }

    public boolean runAlgorithm() {
        int maxGeneration = config.getMaxGeneration();
        for (int i = 0; i <= maxGeneration; i++){
            List<Integer> allFitness = population.getAllFitness();
            if (allFitness.contains(config.getSolution().length)) {
                System.out.println("Solution found in " + i + " generations");
                return true;
            }

        }
        System.out.println("No solution found in " + maxGeneration + " generations");
        return false;
    }

    private Individual crossover(Individual individual1, Individual individual2){
        CrossoverStrategy crossoverStrategy = config.getCrossoverStrategy();
        CrossoverLeftoverStrategy crossoverLeftoverStrategy = config.getCrossoverLeftoverStrategy();

        throw new UnsupportedOperationException("crossover not implemented yet");
    }

    private Individual mutate(Individual individual){
        MutationStrategy randomMutationStrategy = MutationStrategy.values()[random.nextInt(MutationStrategy.values().length)];

        int randomGeneIndex = random.nextInt(individual.getGenomeLength());
        Byte randomGene = (byte) random.nextInt(2);

        switch(randomMutationStrategy){
            case ADD:
                individual.addGene(randomGene);
                break;
            case REMOVE:
                individual.removeGene(randomGeneIndex);
                break;
            case FLIP:
                individual.setGene(randomGeneIndex, randomGene);
                break;
        }

        return individual;
    }

    public List<Individual> selection(Population population) {
        throw new UnsupportedOperationException("selection not implemented yet");
    }

    public Population evolve(Population population){
        throw new UnsupportedOperationException("evolve not implemented yet");
    }
}
