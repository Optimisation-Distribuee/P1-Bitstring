package be.brw;

import be.brw.domain.SimpleGeneticAlgorithm;

public class Main {
    public static void main(String[] args) {
        SimpleGeneticAlgorithm algorithm = new SimpleGeneticAlgorithm();
        algorithm.runAlgorithm(10, "0000000000000000000000000000000000000000000000000000000000000001");
    }
}
