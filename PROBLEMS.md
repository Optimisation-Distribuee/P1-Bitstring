# Problems/nitpicks encountered in the original code
https://github.com/rey5137/tutorials-1/tree/master/algorithms-genetic/src/main/java/com/baeldung/algorithms/ga/binary

## Individual.java

- String concatenation in the toString() method
- genes variable could be final
- getFitness() is called by SimpleGeneticAlgorithm, but then it passes itself back to SimpleGeneticAlgorithm.getFitness() ???

## Population.java

- Using get(0) instead of getFirst(): less robust
- individuals variable could be final

## SimpleGeneticAlgorithm.java

- Using add(0,...) instead of addFirst(): less robust