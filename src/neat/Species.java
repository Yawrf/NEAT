
package neat;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author rewil
 */
public class Species {
    
    private Genome mascot = null;
    private ArrayList<Genome> members = new ArrayList<>();
    private final double compatVariance = 3.0d;
    private double totalFitness = 0d;
    
    public Species(Genome mascot) {
        this.mascot = mascot;
        members.add(mascot);
    }
    
    /**
     * Adds a Genome to the list of members of the Species
     * @param member 
     */
    public void addMember(Genome member) {
        if(members.contains(member)) return;
        members.add(member);
    }
    
    /**
     * Empties the list of members of the species, but maintains mascot
     */
    public void resetList() {
        members = new ArrayList<>();
        members.add(mascot);
    }
    
    /**
     * Returns an array of all members of the species
     * @return 
     */
    public Genome[] getMembers() {
        Genome[] out = new Genome[members.size()];
        return members.toArray(out);
    }
    
    /**
     * Returns if the species already has a given Genome as a member
     * @param check
     * @return 
     */
    public boolean isMember(Genome check) {
        return members.contains(check);
    }
    
    /**
     * Returns the number of members in the species
     * @return 
     */
    public int size() {
        return members.size();
    }
    
    /**
     * Returns the mascot of this species
     * @return 
     */
    public Genome getMascot() {
        return mascot;
    }
    /**
     * Returns the member of this species with the highest score
     * @return 
     */
    public Genome getBestMember() {
        int index = 0;
        double max = Double.NEGATIVE_INFINITY;
        
        for(int i = 0; i < members.size(); ++i) {
            if(members.get(i).getScore() > max) {
                index = i;
                max = members.get(i).getScore();
            }
        }
        
        return members.get(index);
    }
    
    /**
     * Returns the complexity of the Mascot of this Species
     * Complexity is determined by adding the number of nodes with the number of connections
     * @return 
     */
    public int mascotComplexity() {
        return mascot.getComplexity();
    }
    
    /**
     * Returns if the given Genome is similar enough to the mascot to fit in the species
     * Uses the variance class compatVariable as threshold
     * @param check
     * @return 
     */
    public boolean isCompatible(Genome check) {
        final double value1 = 1d; // Sets weight of Excess Connections
        final double value2 = 1d; // Sets weight of Disjoint Connections
        final double value3 = 1d; // Sets weight of Average Weight Differential
        
        GeneConnection[] masCons = mascot.getConnections();
        GeneConnection[] checkCons = check.getConnections();
        int excess = 0;
        int disjoint = 0;
        int matching = 0;
        double weightDiff = 0;
        int size = Math.max(masCons.length, checkCons.length);
            size = size < 20 ? 20 : size;
        
        int indexM = 0;
        int indexC = 0;
        while(indexM < masCons.length || indexC < checkCons.length) {
            boolean excessM = indexC == checkCons.length;
            boolean excessC = indexM == masCons.length;
            if(!excessM && !excessC) { // In progress through both lists of Connections
                GeneConnection mCon = masCons[indexM];
                GeneConnection cCon = checkCons[indexC];
                switch(mCon.compareTo(cCon)) {
                    case -1:
                        ++indexM;
                        ++disjoint;
                        break;
                    case 0:
                        ++indexM; ++indexC;
                        ++matching;
                        weightDiff += Math.abs(mCon.getWeight() - cCon.getWeight());
                        break;
                    case 1:
                        ++indexC;
                        ++disjoint;
                        break;
                }
            } else {
                if(excessM) { // No more Connections in Check
                    ++indexM;
                    ++excess;
                } else { // No more Connections in Mascot
                    ++indexC;
                    ++excess;
                }
            }
        }
        
        double compatDistance = 0;
            compatDistance += ((value1 * excess) / size); // Add weighted value of Excess connections
            compatDistance += ((value2 * disjoint) / size); // Add weighted value of Disjoint cnnections
            compatDistance += (value3 * (weightDiff / matching)); // Add weighted value of Average Weight Differential

//        System.out.println(compatDistance);
        return compatDistance < compatVariance;
    }
    
    /**
     * Runs the calculate method of each stored Genome
     */
    public void calculateGenomes() {
        for(Genome g : members) g.calculate();
    }
    /**
     * Calculates the Adjusted Fitness of the members of the species, then totals it for the species
     */
    public void calculateFitness() {
        for(Genome g : members) {
            g.setScore(g.getScore() / members.size());
            totalFitness += g.getScore();
        }
    }
    /**
     * Returns the Total Fitness of all members divided by the number of members
     * @return 
     */
    public double getAverageFitness() {
        return totalFitness / members.size();
    }
    /**
     * Returns the Total Fitness of all members
     * @return 
     */
    public double getFitness() {
        return totalFitness;
    }
    
    /**
     * Creates a new Genome by breeding two random Genomes together and adds it to the species
     */
    public void breedRandom() {
        Genome g1 , g2;
        Random rand = new Random();
        g1 = members.get(rand.nextInt(members.size()));
        g2 = members.get(rand.nextInt(members.size()));
        addMember(Genome.crossover(g1, g2));
    }
    
    /**
     * Mutates each genome in the Species
     */
    public void mutate() {
        for(Genome g : members) g.mutate();
    }
    
    /**
     * Returns the average complexity of the stored Genomes in this Species
     * Average Complexity is calculated by counting the Nodes and Connections of each genome and dividing them by the total number of Genomes stored
     * @return 
     */
    public double getAverageComplexity() {
        double weight = 0;
        for(Genome g : members) weight += g.getComplexity();
        weight /= members.size();
        return weight;
    }
    
    /**
     * Set input values of All Genomes in species to given int array
     * If given array is larger than number of inputs, then trailing values will be ignored
     * If given array is smaller than number of inputs, then remaining inputs will be set to 0
     * @param inputs 
     */
    public void setInputs(int... inputs) {
        for(Genome g : members) {
            g.setInputs(inputs);
        }
    }
    
}
