
package neat;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author rewil
 */
public class SpeciesManipulator {
    
    private final int countGenomes;
    private final double percentTrim; // From 0 to 1
    
    private ArrayList<Species> species = new ArrayList<>();
    
    /**
     * Generates a new Species Manipulator with the given number of inputs and outputs
     * Uses default values of 1000 Genomes and 90% trim rate
     * @param inputs
     * @param outputs 
     */
    public SpeciesManipulator(int inputs, int outputs) {this(inputs, outputs, 1000, 0.9d);}
    /**
     * Generates a new Species Manipulator with the given number of inputs and outputs
     * Uses passed in countGenomes for number of genomes and percentTrim in range 0-1 to determine trim percentage
     * @param inputs
     * @param outputs
     * @param countGenomes
     * @param percentTrim 
     */
    public SpeciesManipulator(int inputs, int outputs, int countGenomes, double percentTrim) {
        this.countGenomes = countGenomes;
        this.percentTrim = percentTrim % 1;
        GeneNode[] nodes = new GeneNode[inputs + outputs];
            for(int i = 0; i < inputs + outputs; ++i) {
                nodes[i] = new GeneNode(i, i < inputs ? 0 : 2);
            }
        Genome[] genomes = new Genome[countGenomes];
        for(int i = 0; i < countGenomes; ++i) {
            genomes[i] = new Genome();
            GeneNode[] clones = new GeneNode[nodes.length];
                for(int j = 0; j < nodes.length; ++j) {
                    clones[j] = nodes[j].clone();
                }
            genomes[i].putNodes(clones);
        }
        
        for(Genome g : genomes) storeGenome(g);
    }
    
    public void putSpecies(Species s) {
        species.add(s);
    }
    
    public Species[] getSpecies() {
        Species[] out = new Species[species.size()];
        return species.toArray(out);
    }
    /**
     * Returns the number of Species stored
     * @return 
     */
    public int getCount() {
        return species.size();
    }
    /**
     * Returns the average number of Genomes stored in each Species
     * @return 
     */
    public double getAverageSize() {
        double averageSize = 0;
        for (Species s : species) {
            averageSize += s.size();
        } averageSize /= species.size();
        return averageSize;
    }
    /**
     * Gets number of Genomes contained in all Species stored
     * Primarily used for debugging
     * @return 
     */
    public int getGenomeCount() {
        int out = 0;
        
        for(Species s : species) out += s.size();
        
        return out;
    }
    
    /**
     * Returns an array of the Average Complexities of each Species stored
     * @return 
     */
    public double[] getAverageComplexities() {
        double[] acs = new double[species.size()];
        
        for(int i = 0; i < species.size(); ++i) acs[i] = species.get(i).getAverageComplexity();
        
        return acs;
    }
    /**
     * Returns the average of the Average Complexities of each species stored
     * @return 
     */
    public double getAverageComplexity() {
        double out = 0;
        for(double d : getAverageComplexities()) out += d;
        out /= species.size();
        return out;
    }
    
    /**
     * Stores a given Genome into the first Species it fits in, or creates a new one if it doesn't fit into any
     * @param g 
     */
    public void storeGenome(Genome g) {
        boolean speciated = false;
        for(Species s : species) {
            if(s.isCompatible(g)) {
                s.addMember(g);
                speciated = true;
                break;
            }
        }
        if(!speciated) species.add(new Species(g));
    }
    
    /**
     * Performs the mutate command on each Species stored
     */
    public void mutate() {
        for(Species s : species) s.mutate();
    }
    
    /**
     * Trims a percent of Genomes from each species equal to the class-variable percentTrim
     * A value of 1 will remove everything except the Mascot
     */
    public void trim() {
        calculate();
        for(Species s : species) {
            Genome[] g = s.getMembers(); 
                Arrays.sort(g);
            s.resetList();
            int cap = (int) (g.length * percentTrim); 
            for(int i = g.length - 1; i >= cap; --i) {
                s.addMember(g[i]);
            }
        }
    }
    
    /**
     * Calculates the number of Genomes needed to restore count to class variable countGenomes
     * Breeds randomly within species to refill 
     */
    public void refill() {
        int space = countGenomes - getGenomeCount();
        double totalFitness = getTotalFitness(); 
        double[] fitnesses = getFitnesses();
        int[] allotment = tuneAllotments(getAllotments(fitnesses, totalFitness, space), space);
        
        for(int i = 0; i < allotment.length; ++i) {
            for(int j = 0; j < allotment[i]; ++j) species.get(i).breedRandom();
        }
    }   private double getTotalFitness() {
            double out = 0;
            
            for(Species s : species) out += s.getFitness();
            
            return out;
        }
        private double[] getFitnesses() {
            double[] out = new double[species.size()];
            for(int i = 0; i < out.length; ++i) out[i] = species.get(i).getFitness();
            return out;
        }
        private int[] getAllotments(double[] fitnesses, double totalFitness, int space) {
            int[] allotment = new int[fitnesses.length];
            boolean swing = true;
            for(int i = 0; i < fitnesses.length; ++i) {
                double percent = fitnesses[i] / totalFitness;
                double lot = space * percent;
                allotment[i] = swing ? (int) Math.floor(lot) : (int) Math.ceil(lot);
                swing = !swing;
            } 
            return allotment;
        }
        private int[] tuneAllotments(int[] allotments, int space) {
            int total = 0;
            for(int i : allotments) total += i;
            if(total < space) {
                int min = Integer.MAX_VALUE;
                int mindex = 0;
                for(int i = 0; i < allotments.length; ++i) {
                    if(allotments[i] < min) {
                        min = allotments[i];
                        mindex = i;
                    }
                } allotments[mindex] += (space - total);
            }
            if(total > space) {
                int max = Integer.MIN_VALUE;
                int maxdex = 0;
                for(int i = 0; i < allotments.length; ++i) {
                    if(allotments[i] > max) {
                        max = allotments[i];
                        maxdex = i;
                    }
                } allotments[maxdex] -= (total - space);
            }
            
            return allotments;
        }
        
    /**
     * Runs the calculateGenomes method of each species stored
     */
    public void calculate() {
        for(Species s : species) {s.calculateGenomes(); s.calculateFitness();}
    }
        
    /**
     * Reorganizes Genomes into Species following the compatVariance variable in the Species class
     */
    public void respeciate() {
        ArrayList<Genome> genomes = new ArrayList<>();
        ArrayList<Genome> mascots = new ArrayList<>();
        for(Species s : species) {
            for(Genome g : s.getMembers()) {
                genomes.add(g);
            }
            genomes.remove(s.getMascot());
            mascots.add(s.getMascot());
        } species = new ArrayList<>();
        for(Genome g : mascots) {
            storeGenome(g);
        }
        for(Genome g : genomes) {
            storeGenome(g);
        }
    }
    
    /**
     * Performs trim, fill, mutate, and respeciate in order
     * Mutate is performed the specified number of times
     * @param countMutations 
     */
    public void process(int countMutations) {
        trim(); 
        refill();
        for(int i = 0; i < countMutations; ++i) mutate();
        respeciate();
    }
    
    /**
     * Set input values of All Genomes in All Species to given int array
     * If given array is larger than number of inputs, then trailing values will be ignored
     * If given array is smaller than number of inputs, then remaining inputs will be set to 0
     * @param inputs 
     */
    public void setInputs(int... inputs) {
        for(Species s : species) {
            s.setInputs(inputs);
        }
    }
    
    
    
    @Override
    public String toString() {
        String out = "";
        out += "Species Found: " + getCount() + '\n';
        out += "Average size of Species: " + getAverageSize() + '\n';
//        out += "Number of Genomes contained: " + getGenomeCount() + '\n';
        out += "Average Complexity of Species: " + getAverageComplexity() + '\n';
        out += Arrays.toString(getAverageComplexities());
        return out;
    }
    
}
