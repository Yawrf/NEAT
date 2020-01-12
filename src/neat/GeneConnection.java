
package neat;

import java.util.InputMismatchException;
import java.util.Random;

/**
 *
 * @author rewil
 */
public class GeneConnection implements Comparable{
    
    private final GeneNode in, out;
    private final int innovation;
    
    private double weight;
    private boolean enabled;
    
    private static final double maxWeight = 2d;
    private static final double minWeight = -2d;
    private static final double maxWeightShift = 0.3d;
    
    public GeneConnection(GeneNode in, GeneNode out, double weight, int innovation) {
        this.in = in;
        this.out = out;
        this.innovation = innovation;
        this.weight = weight;
        
        out.putInput(this);
        
        enabled = true;
    }
    
    /**
     * Returns the value of the In node multiplied by the weight of the GeneConnection
     * @return 
     */
    public double getValue() {
        if(!enabled) return 0;
        double out = in.getValue() * weight;
        return out;
    }
    
  //----------------------------------------------------------------------------
    
    public double getWeight() {
        return weight;
    }
    public void setWeight(double weight) {
        this.weight = weight;
    }
    /**
     * Shifts weight by given double, or sets it to Max or Min weight if it would surpass one
     * @param shift 
     */
    public void shiftWeight(double shift) {
        weight += shift;
        if (weight > maxWeight) weight = maxWeight;
        if (weight < minWeight) weight = minWeight;
    }

    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public GeneNode getIn() {
        return in;
    }
    public GeneNode getOut() {
        return out;
    }

    public int getInnovation() {
        return innovation;
    }
    
    public double getCenterX() {
        return Math.abs(((out.getX() - in.getX()) / 2) + in.getX());
    }
    public double getCenterY() {
        return Math.abs(((out.getY() - in.getY()) / 2) + in.getY());
    }
    
    public static double getMaxWeight() {
        return maxWeight;
    }
    public static double getMinWeight() {
        return minWeight;
    }
    public static double getMaxWeightShift() {
        return maxWeightShift;
    }
    public static double getRandomWeight() {
        Random rand = new Random();
        return ((rand.nextDouble() * (maxWeight - minWeight)) + minWeight);
    }
    
    public double getWeightOnScale() {
        return ((weight - minWeight) / (maxWeight - minWeight));
    }
    
    @Override
    public String toString() {
        String out = "";
        
        out += "(" + in.getNum() + ", " + this.out.getNum() + " - " + weight + ")";
        
        return out;
    }
    
    @Override
    public boolean equals(Object o) { // Confirms that a passed in GeneConnection has the same Innovation Number
        if(!getClass().isInstance(o)) return false;
        GeneConnection test = (GeneConnection) o;
        return test.getInnovation() == innovation;
    }

    @Override
    public int compareTo(Object o) {
        if(!getClass().isInstance(o)) throw new InputMismatchException("Cannot compare GeneConnection to non-GeneConnection instance");
        GeneConnection check = (GeneConnection) o;
        
        if(in.getNum() == check.getIn().getNum()) {
            if(out.getNum() == check.getOut().getNum()) return 0; // In and Out match, so they're equal
            else if(out.getNum() < check.getOut().getNum()) return -1; // In matches, but out is less, so this is less
            else if(out.getNum() > check.getOut().getNum()) return 1; // In matches, but out is greature, so this is greater
        } else if(in.getNum() < check.getIn().getNum()) return -1; // In is less, so this is less
          else if(in.getNum() > check.getIn().getNum()) return 1; // In is greater, so this is greater
        
        throw new InputMismatchException("Reached end of compareTo without satisfying a condition. This should be impossible");
    }
    
}
