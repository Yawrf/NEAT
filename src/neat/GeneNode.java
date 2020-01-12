
package neat;

import java.util.ArrayList;
import javafx.scene.paint.Color;

/**
 *
 * @author rewil
 */
public class GeneNode {

    private final int num;
    private final int stage; // 0 - Input, 1 - Hidden, 2 - Output
    
    
    public GeneNode(int num, int stage) {
        this(num, stage, 0, 0);
    }
    public GeneNode(int num, int stage, double x, double y) {
        this.num = num;
        this.stage = stage;
        this.x = x;
        this.y = y;
    }
    
    public int getNum() {
        return num;
    }

    public int getStage() {
        return stage;
    }
    
  //----------------------------------------------------------------------------
    
    private double value = 1;
    private boolean calculated = false;
    private final ArrayList<GeneConnection> inputs = new ArrayList<>();
    
    /**
     * Calculates the value of the GeneNode, then returns it
     * @return 
     */
    public double getValue() {
        if(stage != 0 && !calculated) calculate();
        return value;
    }
    
    /**
     * Sets value of GeneNode to given value
     * If GeneNode is not of Input Stage (0), value will be overridden when getValue() is called
     * @param value 
     */
    public void setValue(double value) {
        this.value = value;
    }
    
    /**
     * Gets the values of enabled GeneConnections inputting into this GeneNode, then stores the total
     */
    private void calculate() {
        value = 0;
        for(GeneConnection gc : inputs) {
            if(gc.isEnabled()) value += gc.getValue();
        }
        calculated = true;
    }
    
    /**
     * Adds a GeneConnection to the collection of inputs for this GeneNode
     * @param connection 
     */
    public void putInput(GeneConnection connection) {
        inputs.add(connection);
    }
    
    /**
     * Marks this GeneNode as needing to recalculate its value
     * Recalculation will occur next time getValue() is called
     */
    public void markUncalculated() {
        calculated = false;
    }
    
  //----------------------------------------------------------------------------
    
    // Visualization
    //<editor-fold>
    
    // Visualization Vars
    private double x = 0;
    private double y = 0;
    private Color color = Color.BLACK;
    
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
    
    public Color getCenterColor(GeneNode gn) {
        Color c = gn.getColor();
        
        double red = ((c.getRed() - color.getRed()) / 2 ) + color.getRed();
        double blue = ((c.getBlue() - color.getBlue()) / 2 ) + color.getBlue();
        double green = ((c.getGreen() - color.getGreen()) / 2 ) + color.getGreen();
        
        red = Math.abs(red);
        green = Math.abs(green);
        blue = Math.abs(blue);
        
        return Color.color(red, green, blue);
    }
    //</editor-fold>
    
    @Override
    public String toString() {
        String out = "";
        
        out += "(" + num + "-" + stage + " (" + x + ", " + y + ") )";
        
        return out;
    }
    
    @Override
    public boolean equals(Object o) { // Checks if passedd in GeneNode has same Number and Stage
        if(!getClass().isInstance(o)) return false;
        GeneNode test = (GeneNode) o;
        boolean check = true;
            check = check && test.getNum() == num;
            check = check && test.getStage() == stage;
        return check;
    }
    
    public GeneNode clone() {
        return new GeneNode(num, stage, x, y);
    }
    
}
