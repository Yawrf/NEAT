/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visualizer;

import java.io.File;
import java.net.URL;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author rewil
 */
public class GenomeVisualizer extends Application {

    private Scene scene = null;
    private GenomeVisualizerFXMLController controller = null;
    
    @Override
    public void start(Stage stage) throws Exception {
        URL path = new File("src/visualizer/GenomeVisualizerFXML.fxml").toURI().toURL();
        FXMLLoader loader = new FXMLLoader(path);
            Parent root = loader.load();
            
        controller = (GenomeVisualizerFXMLController) loader.getController();
        scene = new Scene(root);
        
        stage.setTitle("Genome Visualizer - NEAT");
        stage.setScene(scene);
        stage.show();
    }
    
    public Scene getScene() {
        return scene;
    }
    public GenomeVisualizerFXMLController getController() {
        return controller;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
