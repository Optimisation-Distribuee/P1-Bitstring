package be.brw.config;

import be.brw.domain.strategy.CrossoverLeftoverStrategy;
import be.brw.domain.strategy.CrossoverStrategy;
import be.brw.domain.strategy.MutationTargetStrategy;
import be.brw.domain.strategy.SelectionStrategy;

import java.util.Arrays;

public class GAConfig {

    private final byte[] solution;
    private final int minGenomeLength;
    private final int maxGenomeLength;
    private final int maxGeneration;

    // Selection
    private final SelectionStrategy selectionStrategy;

    // Mutation
    private final MutationTargetStrategy mutationTargetStrategy;
    private final double mutationRate;
    private final double bitFlipRate;
    private final double bitAddRate;
    private final double bitRemoveRate;

    // Crossover
    private final CrossoverStrategy crossoverStrategy;
    private final double crossoverRate;

    // Leftover
    private final CrossoverLeftoverStrategy crossoverLeftoverStrategy;

    public GAConfig(byte[] solution, int minGenomeLength, int maxGenomeLength, int maxGeneration, SelectionStrategy selectionStrategy, MutationTargetStrategy mutationTargetStrategy, double mutationRate, double bitFlipRate, double bitAddRate, double bitRemoveRate, CrossoverStrategy crossoverStrategy, double crossoverRate, CrossoverLeftoverStrategy crossoverLeftoverStrategy) {
        this.solution = solution;
        this.minGenomeLength = minGenomeLength;
        this.maxGenomeLength = maxGenomeLength;
        this.maxGeneration = maxGeneration;
        this.selectionStrategy = selectionStrategy;
        this.mutationTargetStrategy = mutationTargetStrategy;
        this.mutationRate = mutationRate;
        this.bitFlipRate = bitFlipRate;
        this.bitAddRate = bitAddRate;
        this.bitRemoveRate = bitRemoveRate;
        this.crossoverStrategy = crossoverStrategy;
        this.crossoverRate = crossoverRate;
        this.crossoverLeftoverStrategy = crossoverLeftoverStrategy;
    }

    public byte[] getSolution() {
        return solution;
    }

    public int getMinGenomeLength() {
        return minGenomeLength;
    }

    public int getMaxGenomeLength() {
        return maxGenomeLength;
    }

    public int getMaxGeneration() {
        return maxGeneration;
    }

    public SelectionStrategy getSelectionStrategy() {
        return selectionStrategy;
    }

    public MutationTargetStrategy getMutationTargetStrategy() {
        return mutationTargetStrategy;
    }

    public double getMutationRate() {
        return mutationRate;
    }

    public double getBitFlipRate() {
        return bitFlipRate;
    }

    public double getBitAddRate() {
        return bitAddRate;
    }

    public double getBitRemoveRate() {
        return bitRemoveRate;
    }

    public CrossoverStrategy getCrossoverStrategy() {
        return crossoverStrategy;
    }

    public double getCrossoverRate() {
        return crossoverRate;
    }

    public CrossoverLeftoverStrategy getCrossoverLeftoverStrategy() {
        return crossoverLeftoverStrategy;
    }

    @Override
    public String toString() {
        return "GAConfig{" +
                "solution=" + Arrays.toString(solution) +
                ", minGenomeLength=" + minGenomeLength +
                ", maxGenomeLength=" + maxGenomeLength +
                ", maxGeneration=" + maxGeneration +
                ", selectionStrategy=" + selectionStrategy +
                ", mutationTargetStrategy=" + mutationTargetStrategy +
                ", mutationRate=" + mutationRate +
                ", bitFlipRate=" + bitFlipRate +
                ", bitAddRate=" + bitAddRate +
                ", bitRemoveRate=" + bitRemoveRate +
                ", crossoverStrategy=" + crossoverStrategy +
                ", crossoverRate=" + crossoverRate +
                ", crossoverLeftoverStrategy=" + crossoverLeftoverStrategy +
                '}';
    }
}
