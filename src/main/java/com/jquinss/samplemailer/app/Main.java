package com.jquinss.samplemailer.app;

import com.jquinss.samplemailer.controllers.SampleMailerController;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/jquinss/samplemailer/fxml/SampleMailer.fxml"));
			primaryStage.setResizable(false);
			primaryStage.setTitle("SampleMailer");
			primaryStage.getIcons().add(new Image(getClass().getResource("/com/jquinss/samplemailer/images/logo.png").toString()));
			VBox root = (VBox)fxmlLoader.load();
			final SampleMailerController controller = fxmlLoader.getController();
			controller.setStage(primaryStage);
			Scene scene = new Scene(root,960,840);
			scene.getStylesheets().add(getClass().getResource("/com/jquinss/samplemailer/styles/application.css").toString());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
