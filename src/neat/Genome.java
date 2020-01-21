
package neat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javafx.scene.paint.Color;
import visualizer.GenomeVisualizerFXMLController;
import visualizer.GenomeVisualizerInterface;

/**
 *
 * @author rewil
 */
public class Genome implements Comparable{
    
    //Vars
    //<editor-fold>
    private double score = 0;
    
    private final double xLeft = 5;
    private final double xRight = 95;
    
    private final double yScale = 10;
    private final double yVariance = 2;
    
    private final Color colorLeft = Color.ORANGE;
    private final Color colorRight = Color.BLUE;
    
    private final ArrayList<Integer> nodeNums = new ArrayList<>();
    private final Map<Integer,GeneNode> nodes = new HashMap<>();
    private final ConnectionHolder connections = new ConnectionHolder();
    private final int[] nodeCounts = new int[3];
    private final Random rand = new Random();
    
    private final double chanceAddConnection = 0.05d; // Chance of addConnection happening in mutate()
    private final double chanceAddNode = 0.025d; // Chance of addNode happening in mutate()
    private final double chanceToggleConnection = 0.015d; // Chance of toggleConnection happening in mutate()
    private final double chanceShiftWeight = 0.01d; // Chance of shiftWeight happening in mutate()
    private final double chanceRandomizeWeight = 0.005d; // Chance of randomizeWeight happening in mutate()
    
    private  double mutability = 1.0d; // Weight of mutation chances, does not affect chanceChangeMutability. .Default: 1.0d
    private final double chanceChangeMutability = 0.01d; // Chance of mutability shifting in mutate()
    private final double maxMutabilityShift = 0.1; // Largest amount in either direction that mutability can shift in mutate()
    //</editor-fold>
    
    /**
     * Calculates the value of each node progressively to avoid recursion going too deep, saving outputs for last
     */
    public void calculate() {
        for(GeneNode n : nodes.values()) {
            n.markUncalculated();
        }
        for(int i = 0; i <= nodeNums.get(nodeNums.size() - 1); ++i) {
            GeneNode n = nodes.get(i);
            if(n == null || n.getStage() == 2) continue;
//            System.out.println(" > " + n + ": " + n.getValue());
            n.getValue();
        }
//        for(GeneNode n : getOutputs()) { // These are generally used for testing purposes only
//            score = n.getValue();
//        } 
//        score = 100d/Math.abs(getComplexity() - 100);
    }
    
    /**
     * Attempts to put a given GeneNode into the Genome
     * Fails and returns false if a GeneNode already in the Genome has the same num
     * Calculates X and Y positions, as well as color, of given input and output nodes if they do not already have an X position
     * @param node
     * @return 
     */
    public boolean putNode(GeneNode node) {
        if(nodeNums.contains(node.getNum())) return false;
        nodes.put(node.getNum(), node);
        nodeNums.add(node.getNum());
            nodeNums.sort(null);
        nodeCounts[node.getStage()]++;
        if(node.getX() == 0) {
            switch(node.getStage()) {
                case 0:
                    node.setX(xLeft);
                    node.setY((yScale * nodeCounts[node.getStage()]) - 5);
                    node.setColor(colorLeft);
                    break;
                case 2:
                    node.setX(xRight);
                    node.setY((yScale * nodeCounts[node.getStage()]) - 5);
                    node.setColor(colorRight);
                    break;
                default:
                    System.out.println("Hidden Node Stored with no X coord");
            }
        }
        return true;
    }
    public void putConnection(GeneConnection connection) {
        connections.putConnection(connection);
    }
    
    /**
     * Attempts to put all nodes into the Genome
     * Fails if a GeneNode is already present with a given num
     * @param nodes 
     */
    public void putNodes(GeneNode... nodes) {
        for(GeneNode n : nodes) {
            putNode(n);
        }
    }
    
    public GeneNode getNode(int num) {
        return nodes.get(num);
    }
    public GeneNode[] getNodes() {
        GeneNode[] out = new GeneNode[nodes.size()];
        return nodes.values().toArray(out);
    }
    public GeneConnection[] getConnections() {
        return connections.getConnections();
    }
    
    public GeneNode[] getInputs() {
        ArrayList<GeneNode> inputs = new ArrayList<>();
        for(GeneNode n : nodes.values()) if(n.getStage() == 0) inputs.add(n);
        GeneNode[] out = new GeneNode[inputs.size()];
        return inputs.toArray(out);
    }
    public GeneNode[] getOutputs() {
        ArrayList<GeneNode> outputs = new ArrayList<>();
        for(GeneNode n : nodes.values()) if(n.getStage() == 2) outputs.add(n);
        GeneNode[] out = new GeneNode[outputs.size()];
        return outputs.toArray(out);
    }
    /**
     * Set input values of Genome to given int array
     * If given array is larger than number of inputs, then trailing values will be ignored
     * If given array is smaller than number of inputs, then remaining inputs will be set to 0
     * @param inputs
     */
    public void setInputs(int... inputs) {
        double[] doubles = new double[inputs.length];
        for(int i = 0; i < inputs.length; ++i) doubles[i] = inputs[i];
        setInputs(doubles);
    }
    /**
     * Set input values of Genome to given double array
     * If given array is larger than number of inputs, then trailing values will be ignored
     * If given array is smaller than number of inputs, then remaining inputs will be set to 0
     * @param inputs 
     */
    public void setInputs(double... inputs) {
        GeneNode[] ins = getInputs();
        int i;
        for(i = 0; i < ins.length && i < inputs.length; ++i) {
            ins[i].setValue(inputs[i]);
        } while(i < ins.length) ins[i++].setValue(0);
    }
    /**
     * Returns an array of the values of the Output Nodes
     * Does not run Calculate, should be run before calling this
     * @return 
     */
    public double[] getOutputValues() {
        GeneNode[] outputs = getOutputs();
        double[] output = new double[outputs.length];
        
        for(int i = 0; i < outputs.length; ++i) output[i] = outputs[i].getValue();
        
        return output;
    }
    
    public GeneConnection getConnection(int in, int out) {
        return connections.getConnection(in, out);
    }
    public GeneConnection getRandomConnection() {
        return connections.getRandomConnection();
    }
    
    public double getScore() {
        return score;
    }
    public void addScore(double add) {
        score += add;
    }
    public void setScore(double score) {
        this.score = score;
    }
    
    /**
     * Returns the Complexity of this Genome
     * Complexity is determined by adding the number of GeneNodes and GeneConnections
     * @return 
     */
    public int getComplexity() {
        return getNodes().length + getConnections().length;
    }
    
    public void mutate() {
        if(rand.nextDouble() < chanceChangeMutability) mutability += (rand.nextDouble() * (2 * maxMutabilityShift) - maxMutabilityShift);
        
        // Change Connection Weights and Network Structures
            if(rand.nextDouble() < chanceAddConnection * mutability) mutateAddConnection(); // Add connections
            if(rand.nextDouble() < chanceAddNode * mutability) mutateAddNode(); // Add nodes
            if(rand.nextDouble() < chanceToggleConnection * mutability) mutateToggleConnection(); // Toggle connections
            if(rand.nextDouble() < chanceShiftWeight * mutability) mutateShiftWeight(); // Shift Weights
            if(rand.nextDouble() < chanceRandomizeWeight * mutability) mutateRandomizeWeight(); // Randomize Weights
    }
    
    //Mutations
    //<editor-fold>
    /**
     * Returns false if connection is already in genome
     * @param in
     * @param out
     * @return 
     */
    public boolean mutateAddConnection(Integer in, Integer out) {
        // New Connection Gene with random Weight is added
        if (connections.getConnection(in, out) != null) return false;
        GeneConnection c = new GeneConnection(nodes.get(in), nodes.get(out), GeneConnection.getRandomWeight(), NEAT.getConnectInno(nodes.get(in).getNum(), nodes.get(out).getNum()));
        putConnection(c);
        return true;
    }
    /**
     * Returns false if connection is already in genome
     * @param in
     * @param out
     * @param weight
     * @return 
     */
    public boolean mutateAddConnection(GeneNode in, GeneNode out, double weight) {
        // New Connection Gene with random Weight is added
        if (connections.getConnection(in.getNum(), out.getNum()) != null) return false;
        GeneConnection c = new GeneConnection(in, out, weight, NEAT.getConnectInno(in.getNum(), out.getNum()));
        putConnection(c);
        return true;
    }
    /**
     * Internal call to randomly mutate a connection
     */
    public void mutateAddConnection() {
        if(nodes.size() < 2 || (nodeCounts[0] == nodes.size())) return; // Ensure enough nodes to function, and that not all nodes are inputs
        int tries = 5;
        while(tries > 0) {
            int a = rand.nextInt(nodes.size()), b = rand.nextInt(nodes.size()); // Random node selection
            while(nodes.get(a).getX() == nodes.get(b).getX()) { // Gaurantees nodes not on same x-position
                a = rand.nextInt(nodes.size());
                b = rand.nextInt(nodes.size());
//                System.out.println(nodes.get(a).getX() + " v " + nodes.get(b).getX());
            } 
            if(nodes.get(a).getX() > nodes.get(b).getX()) { // Ensures nodes are in x-position order
                int temp = a;
                a = b;
                b = temp;
            }
            if(mutateAddConnection(a, b)) tries = 0; // Ensures connection wasn't already present, tries up to 5 times to find one
            else --tries;
        }
    }
    
    /**
     * Splits the given connection into two and a new Hidden Node
     * @param connection 
     */
    public void mutateAddNode(GeneConnection connection) {
        // Existing connection gets split and replaced with a node
        // Old connection is disabled, and two new ones are added
        // New leading in has weight 1, new leading out has old weight
        
        GeneNode node = new GeneNode(nodes.size(), 1);
            node.setX(connection.getCenterX());
            node.setY(connection.getCenterY() + ((rand.nextDouble() * (2*yVariance)) - yVariance));
            node.setColor(connection.getIn().getCenterColor(connection.getOut()));
            putNode(node);
        
        connection.setEnabled(false);
        mutateAddConnection(connection.getIn(), node, 1);
        mutateAddConnection(node, connection.getOut(), GeneConnection.getRandomWeight());
        
    }
    /**
     * Internal call to randomly mutate a new Node
     */
    public void mutateAddNode() {
        GeneConnection g = connections.getRandomEnabledConnection();
            if(g == null) return;
        mutateAddNode(connections.getRandomConnection());
    }
    
    /**
     * Sets the Enabled boolean of the given Connection to the opposite of its current state
     * @param connection 
     */
    public void mutateToggleConnection(GeneConnection connection) {
        connection.setEnabled(!connection.isEnabled());
    }
    /**
     * Internal call to toggle a random connection
     */
    public void mutateToggleConnection() {
        GeneConnection connection = connections.getRandomConnection();
            if(connection == null) return;
        mutateToggleConnection(connection);
    }
    
    /**
     * Shifts the weight of a given GeneConnection by the given amount
     * If the given amount would bring the weight above or below allowed extremes, it will default to the extreme
     * @param connection
     * @param shift 
     */
    public void mutateShiftWeight(GeneConnection connection, double shift) {
        connection.shiftWeight(shift);
    }
    /**
     * Shifts the weight of a given GeneConnection by a random amount
     * Shift amoutn will be within range indicated by GeneConection class
     * @param connection 
     */
    public void mutateShiftWeight(GeneConnection connection) {
        double shift = (rand.nextDouble() * (2*GeneConnection.getMaxWeightShift())) - GeneConnection.getMaxWeightShift();
        mutateShiftWeight(connection, shift);
    }
    /**
     * Internal call to shift the weight of a random GeneConnection by a random amount
     * Shift amount will be within range indicated by GeneConnection class
     */
    public void mutateShiftWeight() {
        GeneConnection connection = connections.getRandomEnabledConnection();
            if(connection == null) return;
        mutateShiftWeight(connection);
    }
    
    /**
     * Randomizes the weight of a given GeneConnection
     * @param connection 
     */
    public void mutateRandomizeWeight(GeneConnection connection) {
        double weight = (rand.nextDouble() * (GeneConnection.getMaxWeight() - GeneConnection.getMinWeight())) + GeneConnection.getMinWeight();
        connection.setWeight(weight);
    }
    /**
     * Internal call to randomize the weight of a random GeneConnection
     */
    public void mutateRandomizeWeight() {
        GeneConnection connection = connections.getRandomEnabledConnection();
            if (connection == null) return;
        mutateRandomizeWeight(connection);
    }
    //</editor-fold>
    
    /**
     * Returns a new Genome formed by breeding the two passed in Genomes
     * GeneConnections which exist in both genomes are randomly selected to be passed on
     * GeneConnections which only exist in one genome are only passed on if their parent has the higher score
     * @param g1
     * @param g2
     * @return 
     */
    public static Genome crossover(Genome g1, Genome g2) {
        if(g1.getScore() < g2.getScore()) {
            Genome temp = g2;
            g2 = g1;
            g1 = temp;
        }
        
        Random rand = new Random();
        Genome out = new Genome();
        for(GeneNode n : g1.getNodes()) {
            if(n.getStage() == 0 || n.getStage() == 2) out.putNode(n.clone());
        }
        
        List<GeneConnection> gc1 = Arrays.asList(g1.getConnections());
        List<GeneConnection> gc2 = Arrays.asList(g2.getConnections());
        for(GeneConnection c : gc1) {
            GeneNode cIn = c.getIn().clone();
            GeneNode cOut = c.getOut().clone();
                if(!out.putNode(cIn)) cIn = out.getNode(cIn.getNum());
                if(!out.putNode(cOut)) cOut = out.getNode(cOut.getNum());
            if(gc2.contains(c)) {
                GeneConnection put = null;
                switch(rand.nextInt(2)) {
                    case 0: 
                        put = new GeneConnection(cIn, cOut, c.getWeight(), c.getInnovation());
                            put.setEnabled(c.isEnabled());
                        out.putConnection(put);
                        break;
                    case 1: 
                        GeneConnection temp = gc2.get(gc2.indexOf(c));
                        put = new GeneConnection(cIn, cOut, temp.getWeight(), temp.getInnovation());
                        out.putConnection(put);
                        break;
                }
            } else {
                GeneConnection put = new GeneConnection(cIn, cOut, c.getWeight(), c.getInnovation());
                    put.setEnabled(c.isEnabled());
                out.putConnection(put);
            }
        }
        
        return out;
    }
    
  //----------------------------------------------------------------------------
    
    @Override
    public String toString() {
        String out = "";
        
        out += "Nodes: ";
            for(Integer i : nodes.keySet()) {
                out += i + "-" + nodes.get(i);
                out += "  |  ";
            } out += '\n';
        out += "Connections: " + connections;
        
        return out;
    }
    
    /**
     * Returns a simplified toString containing less information
     * @return 
     */
    public String toStringSimplified() {
        String out = "";
        
        out += "Nodes: ";
        for(GeneNode n : nodes.values()) {
            out += "(" + n.getNum() + " - " + n.getStage() + ")";
            out += " | ";
        } out += '\n';
        for(GeneConnection c : getConnections()) {
            out += "(" + c.getIn().getNum() + ", " + c.getOut().getNum() + ")";
            out += " | ";
        }
        return out;
    }
    
    public void visualize(GenomeVisualizerInterface controller) {
//        System.out.println("Begin Visualization");
        
        for(GeneConnection c : connections.getConnections()) {
            if(c.isEnabled()) controller.drawConnection(c);
        }
        for(GeneNode n : nodes.values()) {
//            System.out.println(n);
            controller.drawNode(n);
        }
                
//        System.out.println("Finish Visualization");
        
    }
    
    private static Genome storedGenome = null;
    public static void storeGenome(Genome g) {
        storedGenome = g;
    }
    public static Genome getStoredGenome() {
        return storedGenome;
    }

    @Override
    public int compareTo(Object o) {
        if(!getClass().isInstance(o)) throw new InputMismatchException("Cannot compare Genome to non-Genome Instance");
        Genome check = (Genome) o;
        return Double.compare(score, check.getScore());
    }
    
  //----------------------------------------------------------------------------
  //----------------------------------------------------------------------------
    
    private class ConnectionHolder {
        
        private final Map<Integer,Map<Integer,GeneConnection>> connections = new HashMap<>();
        
        /**
         * Puts a connection in the holder, returns false if already existing
         * @param gc
         * @return 
         */
        public boolean putConnection(GeneConnection gc) {
            int in = gc.getIn().getNum(); // Index Code of In node
            int out = gc.getOut().getNum(); // Index Code of Out node
            Map<Integer,GeneConnection> inner = connections.get(in); // Grab inner map to store GeneConnection
                if(inner != null) { // Check if inner map exists yet
                    if(inner.get(out) != null) return false; // If inner map isn't null, check to make sure GeneConnection isn't already stored
                } else { // If inner map is null, generate and store a new map for it
                    inner = new HashMap<>();
                    connections.put(in, inner);
                } 
                
            inner.put(out, gc); 
            return true;
        }
        
        /**
         * Returns a GeneConnection with the indicated In and Out nodes, or null if none exists
         * @param in
         * @param out
         * @return 
         */
        public GeneConnection getConnection(int in, int out) {
            Map<Integer,GeneConnection> inner = connections.get(in);
                if(inner == null) return null;
            return inner.get(out);
        }
        
        /**
         * Returns a random GeneConnection that has been stored
         * @return 
         */
        public GeneConnection getRandomConnection() {
            GeneConnection[] gcs = getConnections();
                if(gcs.length == 0) return null;
            GeneConnection out = gcs[rand.nextInt(gcs.length)];
            return out;
        }
        
        /**
         * Returns all GeneConnections as an Array
         * @return 
         */
        public GeneConnection[] getConnections() {
            ArrayList<GeneConnection> out = new ArrayList<>();
            
            for(int i : connections.keySet()) {
                for(GeneConnection g : connections.get(i).values()) {
                    out.add(g);
                }
            }
            
            GeneConnection[] output = new GeneConnection[out.size()];
            output = out.toArray(output);
            return output;
        }
        
        /**
         * Returns an Enabled GeneConnection, or null if none are stored
         * @return 
         */
        public GeneConnection getRandomEnabledConnection() {
            GeneConnection[] gcs = getConnections();
            ArrayList<GeneConnection> enabled = new ArrayList<>();
            for(GeneConnection g : gcs) {
                if(g.isEnabled()) enabled.add(g);
            }
            if(enabled.isEmpty()) return null;
            return enabled.get(rand.nextInt(enabled.size()));
        }
        
        @Override
        public String toString() {
            String out = "";
            
            for(GeneConnection g : getConnections()) {
                out += g + " | ";
            }
            
            if(out.length() >= 3) out = out.substring(0, out.length() - 3);
            return out;
        }
        
    }
    
}
