package com.jquinss.samplemailer.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;
import com.jquinss.samplemailer.mail.EmailTemplate;
import com.jquinss.samplemailer.util.DialogBuilder;
import com.jquinss.samplemailer.util.OSChecker;
import com.jquinss.samplemailer.util.ObjectSerializer;

public class TemplatesPaneController {
	@FXML
	private ListView<EmailTemplate> templateListView;

	private static final String TEMPLATES_FILE_NAME = "Templates.dat";
	private static final String TEMPLATES_DIR_NAME = "SampleMailer";
	private static final String TEMPLATES_ROOT_DIR = OSChecker.getOSDataDirectory() + File.separator + TEMPLATES_DIR_NAME;
	private static final String TEMPLATES_PATH = TEMPLATES_ROOT_DIR + File.separator + TEMPLATES_FILE_NAME;
	private final ObservableList<EmailTemplate> emailTemplateObsList = FXCollections.observableArrayList();
	private SimpleBooleanProperty templateListSaved = new SimpleBooleanProperty(true);
	private ObjectSerializer objectSerializer;
	private SampleMailerController sampleMailerController;
	
	@FXML
	void addTemplate(ActionEvent event) {
		Alert invalidInputAlert = DialogBuilder.getAlertDialog("Informational alert", "Invalid input",
				"The field cannot be empty", AlertType.ERROR);
		TextInputDialog textInputDialog = DialogBuilder.getSingleTextFieldInputDialog("Template Creator",
				"Create a new template", "Template name:", invalidInputAlert);
		setDialogPaneStyles(invalidInputAlert.getDialogPane());
		setDialogPaneStyles(textInputDialog.getDialogPane());
		
		Optional<String> templateName = textInputDialog.showAndWait();

		if (templateName.isPresent()) {
			System.out.println("Creating template");
			EmailTemplate template = sampleMailerController.createEmailTemplate(templateName.get());
			emailTemplateObsList.add(template);
			templateListSaved.set(false);
		}
	}
	
	@FXML
	void applyTemplate(ActionEvent event) {
		EmailTemplate template = templateListView.getSelectionModel().getSelectedItem();

		if (template != null) {
			sampleMailerController.applyTemplate(template);
		}
		else {
			sampleMailerController.showAlertDialog("Error", "Error applying template", "No templates have been selected", AlertType.WARNING);
		}
	}
	
	@FXML
	void removeTemplate(ActionEvent event) {
		EmailTemplate template = templateListView.getSelectionModel().getSelectedItem();

		if (template != null) {
			emailTemplateObsList.remove(template);
			templateListSaved.set(false);
		}
		else {
			sampleMailerController.showAlertDialog("Error", "Error removing template", "No templates have been selected", AlertType.WARNING);
		}
	}
	
	void setMainController(SampleMailerController sampleMailerController) {
		this.sampleMailerController = sampleMailerController;
	}
	
	void loadTemplates() {
		objectSerializer = new ObjectSerializer(TEMPLATES_PATH);
		
		if (objectSerializer.fileExists()) {
			try {
				objectSerializer.openFileForRead();
				@SuppressWarnings("unchecked")
				List<EmailTemplate> templateList = (List<EmailTemplate>) objectSerializer.readObject();
				emailTemplateObsList.setAll(templateList);
			}
			catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			finally {
				try {
					objectSerializer.closeInput();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	void saveTemplates() {
		objectSerializer = new ObjectSerializer(TEMPLATES_PATH);
		
		try {
			Files.createDirectories(Paths.get(TEMPLATES_ROOT_DIR));
			objectSerializer.openFileForWrite();
			List<EmailTemplate> emailTemplateList = emailTemplateObsList.stream().collect(Collectors.toList());
			objectSerializer.writeObject(emailTemplateList);
			templateListSaved.set(true);
		}
		catch (IOException e) {
			templateListSaved.set(false);
			e.printStackTrace();
		}
		finally {
			try {
				objectSerializer.closeOutput();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	SimpleBooleanProperty isTemplateListSaved() {
		return templateListSaved;
	}
	
	private void setDialogPaneStyles(DialogPane dialogPane) {
		dialogPane.getStylesheets().add(getClass().getResource("/com/jquinss/samplemailer/styles/application.css").toString());
		sampleMailerController.setDialogPaneWindowLogo(dialogPane);
	}
	
	@FXML
	private void initialize() {
		templateListView.setItems(emailTemplateObsList);
	}
}
