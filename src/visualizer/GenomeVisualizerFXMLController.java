/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visualizer;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import neat.GeneConnection;
import neat.GeneNode;
import neat.Genome;

/**
 * FXML Controller class
 *
 * @author rewil
 */
public class GenomeVisualizerFXMLController implements Initializable, GenomeVisualizerInterface {
    
    private Genome gen;
    
    public void visualize() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gen.calculate();
            double size = Math.max(gen.getInputs().length, gen.getOutputs().length);
        yMod = (canvas.getHeight() / 100d) * (10/size);
        gen.visualize(this);
    }
    
    @FXML public void addConnection() {
        gen.mutateAddConnection();
        visualize();
    }
    @FXML public void addNode() {
        gen.mutateAddNode();
        visualize();
    }
    @FXML public void toggleConnection() {
        gen.mutateToggleConnection();
        visualize();
    }
    @FXML public void shiftWeight() {
        gen.mutateShiftWeight();
        visualize();
    }
    @FXML public void randomizeWeight() {
        gen.mutateRandomizeWeight();
        visualize();
    }
    @FXML public void mutate() {
        gen.mutate();
        visualize();
    }
    
    @FXML Canvas canvas;
        private GraphicsContext gc;
        
        private double xMod;
        private double yMod;
    @Override
    public void drawNode(GeneNode g) {
        double nodeSize = 15;
        
        double nodeX = g.getX() * xMod;
        double nodeY = g.getY() * yMod;
        gc.setGlobalAlpha(0.5);
        gc.setFill(g.getColor());
        gc.fillOval(nodeX - nodeSize/2, nodeY - nodeSize/2, nodeSize, nodeSize);
        gc.setFill(Color.BLACK);
        gc.setGlobalAlpha(1.0);
        gc.fillText("" + g.getValue(), nodeX - nodeSize*1.5, nodeY);
        
    }
    @Override
    public void drawConnection(GeneConnection c) {
        
        double x1 = c.getIn().getX() * xMod;
        double y1 = c.getIn().getY() * yMod;
        double x2 = c.getOut().getX() * xMod;
        double y2 = c.getOut().getY() * yMod;
        
        gc.setLineWidth(c.getWeightOnScale() * 2);
//        System.out.println(c.getWeightOnScale());
        gc.strokeLine(x1, y1, x2, y2);
        gc.setLineWidth(1d);
    }
    
  //----------------------------------------------------------------------------
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        gc = canvas.getGraphicsContext2D();
        gen = Genome.getStoredGenome();
            if(gen == null) gen = new Genome();
        
        xMod = canvas.getWidth() / 100d;
        yMod = canvas.getHeight() / 100d;
        gc.setFont(new Font(gc.getFont().getName(), 10));
        
        visualize();
    }    
    
}
