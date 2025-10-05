package be.brw;

import be.brw.config.ConfigLoader;
import be.brw.config.GAConfig;
import be.brw.domain.GeneticAlgorithm;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        try {
            GAConfig config = ConfigLoader.fromYaml(Path.of("src/main/resources/config.yaml"));
            System.out.println(config);

            GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(config);
            boolean status = geneticAlgorithm.runAlgorithm();
            System.out.println(status);

        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
