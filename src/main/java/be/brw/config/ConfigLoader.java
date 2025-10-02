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

            byte[] solution = obj.get("solution").toString().getBytes();

            int minGenomeLength = (Integer) obj.get("minGenomeLength");
            int maxGenomeLength = (Integer) obj.get("maxGenomeLength");
            int maxGeneration = (Integer) obj.get("maxGeneration");

            SelectionStrategy selectionStrategy = SelectionStrategy.valueOf(obj.get("selectionStrategy").toString());
            MutationTargetStrategy mutationTargetStrategy = MutationTargetStrategy.valueOf(obj.get("mutationTargetStrategy").toString());

            double mutationRate = Double.parseDouble(obj.get("mutationRate").toString());
            double bitFlipRate = Double.parseDouble(obj.get("bitFlipRate").toString());
            double bitAddRate = Double.parseDouble(obj.get("bitAddRate").toString());
            double bitRemoveRate = Double.parseDouble(obj.get("bitRemoveRate").toString());

            CrossoverStrategy crossoverStrategy = CrossoverStrategy.valueOf(obj.get("crossoverStrategy").toString());
            double crossoverRate = Double.parseDouble(obj.get("crossoverRate").toString());
            CrossoverLeftoverStrategy crossoverLeftoverStrategy = CrossoverLeftoverStrategy.valueOf(obj.get("crossoverLeftoverStrategy").toString());

            return new GAConfig(
                    solution,
                    minGenomeLength,
                    maxGenomeLength,
                    maxGeneration,
                    selectionStrategy,
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
