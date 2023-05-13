package com.jquinss.samplemailer.controllers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

import com.jquinss.samplemailer.managers.SettingsManager;
import com.jquinss.samplemailer.managers.TemplatesManager;
import com.jquinss.samplemailer.util.AppStyler;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;
import com.jquinss.samplemailer.mail.EmailTemplate;
import com.jquinss.samplemailer.util.DialogBuilder;

public class TemplatesPaneController {
	@FXML
	private ListView<EmailTemplate> templateListView;
	private final TemplatesManager templatesManager = new TemplatesManager();
	private SampleMailerController sampleMailerController;
	
	@FXML
	void addEmailTemplate() {
		Alert invalidInputAlert = DialogBuilder.buildAlertDialog("Informational alert", "Invalid input",
				"The field cannot be empty", AlertType.ERROR);
		TextInputDialog textInputDialog = DialogBuilder.buildSingleTextFieldInputDialog("Template Creator",
				"Create a new template", "Template name:", invalidInputAlert);

		setStyles(invalidInputAlert.getDialogPane());
		setStyles(textInputDialog.getDialogPane());
		setWindowLogo(invalidInputAlert.getDialogPane(), SettingsManager.getInstance().getDialogLogoImage());
		setWindowLogo(textInputDialog.getDialogPane(), SettingsManager.getInstance().getMainLogoImage());

		Optional<String> templateName = textInputDialog.showAndWait();

		if (templateName.isPresent()) {
			EmailTemplate emailTemplate = sampleMailerController.createEmailTemplate(templateName.get());
			templatesManager.addEmailTemplate(emailTemplate);
		}
	}
	
	@FXML
	void applyEmailTemplate() {
		EmailTemplate emailTemplate = templateListView.getSelectionModel().getSelectedItem();

		if (emailTemplate != null) {
			sampleMailerController.applyEmailTemplate(emailTemplate);
		}
		else {
			sampleMailerController.showAlertDialog("Error", "Error applying template", "No templates have been selected", AlertType.WARNING);
		}
	}
	
	@FXML
	void removeEmailTemplate() {
		EmailTemplate emailTemplate = templateListView.getSelectionModel().getSelectedItem();

		if (emailTemplate != null) {
			templatesManager.removeEmailTemplate(emailTemplate);
		}
		else {
			sampleMailerController.showAlertDialog("Error", "Error removing template", "No templates have been selected", AlertType.WARNING);
		}
	}
	
	void setMainController(SampleMailerController sampleMailerController) {
		this.sampleMailerController = sampleMailerController;
	}
	
	void loadEmailTemplates(String fileName) throws IOException, ClassNotFoundException {
			templatesManager.loadEmailTemplates(fileName, false, true);
	}
	
	void saveEmailTemplates(String fileName) throws IOException {
		templatesManager.saveEmailTemplates(fileName);
	}
	
	SimpleBooleanProperty isDataSaved() {
		return templatesManager.IsDataSaved();
	}

	private void setStyles(DialogPane dialogPane) {
		AppStyler.setStyles(dialogPane, this, SettingsManager.getInstance().getCSS());
	}

	private void setWindowLogo(DialogPane dialogPane, String logo) {
		AppStyler.setWindowLogo(dialogPane, this, logo);
	}
	
	@FXML
	private void initialize() {
		try {
			loadEmailTemplates(SettingsManager.getInstance().getTemplatesFilePath());
		} catch (FileNotFoundException e) {
			// ignore exception if there is no file, the first time templates are loaded
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		templateListView.setItems(templatesManager.getEmailTemplateObservableList());
	}
}
