package com.jquinss.samplemailer.util;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;

public class DialogBuilder {
	private DialogBuilder() {}
	
	public static TextInputDialog getSingleTextFieldInputDialog(String title, String headerText, 
																String contentText, Alert alertDialog) {
		
		TextInputDialog dialog = new TextInputDialog();
		
		dialog.setTitle(title);
		dialog.setHeaderText(headerText);
		dialog.setContentText(contentText);
		
		final Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
		
		okBtn.addEventFilter(ActionEvent.ACTION, e -> {
			if (dialog.getEditor().getText().trim().isEmpty()) {
				alertDialog.showAndWait();
				e.consume();
			}
		});
		
		return dialog;
	}
	
	public static Alert getAlertDialog(String title, String headerText, 
									String contentText, AlertType alertType) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(headerText);
		alert.setContentText(contentText);
		
		return alert;
	}
	
	public static FileChooser getFileChooser(String title, ExtensionFilter... extFilters) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(title);
		fileChooser.getExtensionFilters().addAll(extFilters);
		
		return fileChooser;
	}
	
	public static Dialog<ButtonType> buildConfirmationDialog(String title, String headerText, String contentText) {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle(title);
		dialog.setHeaderText(headerText);
		dialog.setContentText(contentText);
		
		ButtonType saveButton = new ButtonType("Save", ButtonData.OK_DONE);
		ButtonType doNotSaveButton = new ButtonType("Don't Save And Exit", ButtonData.NO);
		ButtonType cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		
		dialog.getDialogPane().getButtonTypes().addAll(saveButton, doNotSaveButton, cancelButton);
		
		return dialog;
	}
}