package be.brw;

import be.brw.config.ConfigLoader;
import be.brw.config.GAConfig;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        try {
            GAConfig config = ConfigLoader.fromYaml(Path.of("src/main/resources/config.yaml"));
            System.out.println(config);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
