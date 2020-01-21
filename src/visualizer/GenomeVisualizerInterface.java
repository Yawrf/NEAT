/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visualizer;

import neat.GeneConnection;
import neat.GeneNode;

/**
 *
 * @author rewil
 */
public interface GenomeVisualizerInterface {
    public void drawNode(GeneNode n);
    public void drawConnection(GeneConnection c);
}
