package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
 
public class Main extends Application {
	Controller controller;
 
    @Override
    public void start(Stage stage) throws Exception {

    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/layout.fxml"));
        
        Scene scene = new Scene(loader.load(), 800, 600);
    	controller = loader.getController();
        
        stage.setTitle("CAM");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
    	super.stop();
    	controller.stop();
    }
    
	public static void main(String[] args) {
        launch(args);
	}
}
