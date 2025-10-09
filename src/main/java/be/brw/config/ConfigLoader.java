package be.brw.config;

import be.brw.domain.strategy.CrossoverLeftoverStrategy;
import be.brw.domain.strategy.CrossoverStrategy;
import be.brw.domain.strategy.MutationTargetStrategy;
import be.brw.domain.strategy.SelectionStrategy;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class ConfigLoader {
    public static GAConfig fromYaml(Path path) throws IOException {
        Yaml yaml = new Yaml();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            Map<String, Object> obj = yaml.load(reader);

            int seed = Integer.parseInt(obj.get("seed").toString());
            byte[] solution = obj.get("solution").toString().getBytes();

            int minGenomeLength = Integer.parseInt(obj.get("minGenomeLength").toString());
            int maxGenomeLength = Integer.parseInt(obj.get("maxGenomeLength").toString());
            int maxGeneration = Integer.parseInt(obj.get("maxGeneration").toString());
            int populationSize = Integer.parseInt(obj.get("populationSize").toString());

            SelectionStrategy selectionStrategy = SelectionStrategy.valueOf(obj.get("selectionStrategy").toString());
            int tournamentSize = -1;
            if (selectionStrategy == SelectionStrategy.TOURNAMENT) {
                tournamentSize = Integer.parseInt(obj.get("tournamentSize").toString());
            }
            MutationTargetStrategy mutationTargetStrategy = MutationTargetStrategy.valueOf(obj.get("mutationTargetStrategy").toString());

            double mutationRate = Double.parseDouble(obj.get("mutationRate").toString());
            double bitFlipRate = Double.parseDouble(obj.get("bitFlipRate").toString());
            double bitAddRate = Double.parseDouble(obj.get("bitAddRate").toString());
            double bitRemoveRate = Double.parseDouble(obj.get("bitRemoveRate").toString());

            CrossoverStrategy crossoverStrategy = CrossoverStrategy.valueOf(obj.get("crossoverStrategy").toString());
            double crossoverRate = Double.parseDouble(obj.get("crossoverRate").toString());
            CrossoverLeftoverStrategy crossoverLeftoverStrategy = CrossoverLeftoverStrategy.valueOf(obj.get("crossoverLeftoverStrategy").toString());

            return new GAConfig(
                    seed,
                    solution,
                    minGenomeLength,
                    maxGenomeLength,
                    maxGeneration,
                    populationSize,
                    selectionStrategy,
                    tournamentSize,
                    mutationTargetStrategy,
                    mutationRate,
                    bitFlipRate,
                    bitAddRate,
                    bitRemoveRate,
                    crossoverStrategy,
                    crossoverRate,
                    crossoverLeftoverStrategy
            );
        }
    }
}
