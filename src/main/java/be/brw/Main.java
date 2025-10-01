package be.brw;

import be.brw.domain.SimpleGeneticAlgorithm;

public class Main {
    public static void main(String[] args) {
        SimpleGeneticAlgorithm algorithm = new SimpleGeneticAlgorithm();
        if(!algorithm.runAlgorithm(10, "0000000000000000000000000000000000000000000000000000000000000001")){
            System.out.println("No solution found!");
        }
    }
}
