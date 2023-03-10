package application;

import controllers.SampleMailerController;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/SampleMailer.fxml"));
			primaryStage.setResizable(false);
			primaryStage.setTitle("SampleMailer");
			VBox root = (VBox)fxmlLoader.load();
			final SampleMailerController controller = fxmlLoader.getController();
			controller.setStage(primaryStage);
			Scene scene = new Scene(root,1150,768);
			scene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
