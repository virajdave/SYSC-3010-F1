package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class Main extends Application {
	Controller controller;
	
	@Override
	public void start(Stage primaryStage) throws Exception {

    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view.fxml"));

        Scene scene = new Scene(loader.load(), 800, 600);
    	controller = new Controller();
    	loader.setController(controller);

    	primaryStage.setTitle("CAM");
    	primaryStage.setScene(scene);
    	primaryStage.show();
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
