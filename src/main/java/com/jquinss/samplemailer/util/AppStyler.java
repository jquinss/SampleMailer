package com.jquinss.samplemailer.util;

import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class AppStyler {
    private AppStyler() {}

    public static void setStyles(Pane pane, Object context, String cssFile) {
        Scene scene = pane.getScene();
        setStyles(scene, context, cssFile);
    }

    public static void setStyles(Scene scene, Object context, String cssFile) {
        scene.getStylesheets().add(context.getClass().getResource(cssFile).toString());
    }

    public static void setWindowLogo(Pane pane, Object context, String imageFile) {
        Stage stage = (Stage) pane.getScene().getWindow();
        setWindowLogo(stage, context, imageFile);
    }

    public static void setWindowLogo(Stage stage, Object context, String imageFile) {
        stage.getIcons().add(new Image(context.getClass().getResource(imageFile).toString()));
    }

    public static void setTooltip(ImageView imageView, Object context, String imageFile, String tooltipText) {
        imageView.setImage(new Image(context.getClass().getResource(imageFile).toString()));
        Tooltip.install(imageView, new Tooltip(tooltipText));
    }
}
