package com.jquinss.samplemailer.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jquinss.samplemailer.managers.SettingsManager;
import com.jquinss.samplemailer.util.AppStyler;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.apache.poi.hsmf.MAPIMessage;
import org.apache.poi.hsmf.exceptions.ChunkNotFoundException;

import com.jquinss.samplemailer.mail.MailConverter;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import com.jquinss.samplemailer.mail.EmailHeader;
import com.jquinss.samplemailer.mail.HeaderExtractor;
import com.jquinss.samplemailer.util.DialogBuilder;

public class CustomHeadersPaneController {
	
	@FXML
	private ToggleGroup headersImportTypeToggleGroup;

	@FXML
	private TableView<EmailHeader> headerTableView;

	@FXML
	private TextField headerNameTextField;

	@FXML
	private TextField headerValueTextField;
	
	@FXML
	private TextField excludedHeaderNameTextField;
	
	@FXML
	private TableView<String> excludedHeaderNamesTableView;
	
	@FXML
	private TableColumn<String, String> excludedHeaderNameTableColumn;
	
	@FXML
	private ImageView addHeadersFromFileTooltip;

	@FXML
	private VBox addHeadersManuallyPane;
	
	@FXML
	private VBox addHeadersFromFilePane;
	
	@FXML
	private TextField headersFilePathTextField;
	
	private Stage stage;
	
	private SampleMailerController sampleMailerController;
	
	private final ObservableList<EmailHeader> headersObsList = FXCollections.observableArrayList();
	private final ObservableList<String> excludedHeaderNamesObsList = FXCollections.observableArrayList();
	
	static final String ADD_FROM_FILE_TEXT = "Add from File";
	static final String ADD_MANUALLY_TEXT = "Add manually";
	
	@FXML
	private void switchHeadersImportType() {
		toggleAddHeadersPanes();
	}
	
	@FXML
	private void selectHeadersFile() {
		FileChooser fileChooser = DialogBuilder.buildFileChooser("Select the file containing headers");
		File file = fileChooser.showOpenDialog(stage);
		if (file != null) {
			headersFilePathTextField.setText(file.getPath());
		}
	}
	
	@FXML
	private void clearHeadersFileSelection() {
		headersFilePathTextField.clear();
	}

	@FXML
	private void addHeader() {
		String headerName = headerNameTextField.getText().trim();
		String headerValue = headerValueTextField.getText().trim();
		
		
		if (!headerName.isEmpty() && !headerValue.isEmpty()) {
			headersObsList.add(new EmailHeader(headerName, headerValue));
			headerNameTextField.clear();
			headerValueTextField.clear();
		}
		else {
			sampleMailerController.showAlertDialog("Error", "Error creating header", "You must enter a name and value for the header", AlertType.WARNING);
		}
	}
	
	@FXML
	private void modifyHeader() {
		EmailHeader selectedHeader = headerTableView.getSelectionModel().getSelectedItem();
		
		if (selectedHeader != null) {
			String headerName = headerNameTextField.getText().trim();
			String headerValue = headerValueTextField.getText().trim();
			replaceHeader(selectedHeader, new EmailHeader(headerName, headerValue));
		}
		else {
			sampleMailerController.showAlertDialog("Error", "Error modifying header", "No headers have been selected", AlertType.WARNING);
		}
	}
	
	@FXML
	private void removeHeader() {
		EmailHeader header = headerTableView.getSelectionModel().getSelectedItem();
		
		if (header != null) {
			headersObsList.remove(header);
		}
		else {
			sampleMailerController.showAlertDialog("Error", "Error removing header", "No headers have been selected", AlertType.WARNING);
		}
	}
	
	@FXML
	private void addHeaderToExcludedHeadersList() {
		String excludedHeaderName = excludedHeaderNameTextField.getText().trim();
		
		if (!excludedHeaderName.isEmpty()) {
			excludedHeaderNamesObsList.add(excludedHeaderName);
			excludedHeaderNameTextField.clear();
		}
	}
	
	@FXML
	private void removeHeaderFromExcludedHeadersList() {
		String excludedHeaderName = excludedHeaderNamesTableView.getSelectionModel().getSelectedItem();
		
		if (!excludedHeaderName.isEmpty()) {
			excludedHeaderNamesObsList.remove(excludedHeaderName);
		}
	}
	
	private void replaceHeader(EmailHeader currentHeader, EmailHeader newHeader) {
		if (!newHeader.getName().isEmpty() && !newHeader.getName().isEmpty()) {
			int oldItemIndex = headersObsList.indexOf(currentHeader);
			headersObsList.remove(oldItemIndex);
			headersObsList.add(oldItemIndex, newHeader);
		}
		else {
			sampleMailerController.showAlertDialog("Error", "Error modifying header", "You must enter a name and value for the header", AlertType.WARNING);
		}
	}
	
	@FXML
	private void initialize() {
		headerTableView.setItems(headersObsList);
		excludedHeaderNamesTableView.setItems(excludedHeaderNamesObsList);
		excludedHeaderNameTableColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue()));
		setTooltips();
	}
	
	private void setTooltips() {
		AppStyler.setTooltip(addHeadersFromFileTooltip, this,
				SettingsManager.getInstance().getDialogLogoImage(), """
				Select a file to import the headers from. This can be an .eml
				or an Outlook .msg file or a text file containing key-value
				entities separated by a colon (e.g. test: 1234)""");
	}
	
	void setMainController(SampleMailerController sampleMailerController) {
		this.sampleMailerController = sampleMailerController;
	}
	
	List<EmailHeader> getHeaders() {
		List<EmailHeader> emailHeaders = new ArrayList<EmailHeader>();
		String selectedHeadersImportType = ((RadioButton) headersImportTypeToggleGroup.getSelectedToggle()).getText();
		if (selectedHeadersImportType.equals("Add manually")) {
			emailHeaders = new ArrayList<>(headersObsList);
		}
		else {
			String file = headersFilePathTextField.getText();
			if (!file.isEmpty()) {
				try {
					emailHeaders = importHeadersFromFile(new File(file));
				}
				catch (IOException|ChunkNotFoundException|MessagingException e) {
					sampleMailerController.showAlertDialog("Error", "Error importing headers", "An error has occurred while importing the headers from the file " +
															file + ". Only .msg, .eml and text files are supported. Check if this is a valid file", AlertType.ERROR);
				}
			}
		}
		
		return emailHeaders;
	}
	
	List<String> getExcludedHeaderNames() {
		return new ArrayList<>(excludedHeaderNamesObsList);
	}
	
	String getHeadersFilePath() {
		return headersFilePathTextField.getText();
	}
	
	void setHeaders(List<EmailHeader> items) {
		headersObsList.setAll(items);
	}
	
	void setExcludedHeaderNames(List<String> excludedHeaderNames) {
		excludedHeaderNamesObsList.setAll(excludedHeaderNames);		
	}
	
	void setHeadersFilePath(String path) {
		headersFilePathTextField.setText(path);
	}
	
	void clearData() {
		headersObsList.clear();
		headersFilePathTextField.clear();
		excludedHeaderNamesObsList.clear();
	}
	
	void enableAddHeadersPane(String radioButtonName) {
		for (Toggle toggle : headersImportTypeToggleGroup.getToggles()) {
			RadioButton radioButton = (RadioButton) toggle;
			if (radioButton.getText().equals(radioButtonName)) {
				headersImportTypeToggleGroup.selectToggle(radioButton);
				toggleAddHeadersPanes();
			}
		}
	}
	
	private List<EmailHeader> importHeadersFromFile(File file) throws IOException, ChunkNotFoundException, MessagingException {
		List<EmailHeader> emailHeaders = new ArrayList<EmailHeader>();
		
		if (file.getName().matches(".*\\.msg")) {
			MAPIMessage msg = new MAPIMessage(file);
			emailHeaders = HeaderExtractor.extractHeaders(msg);
		}
		else if (file.getName().matches(".*\\.eml")) {
			MimeMessage msg = MailConverter.emlToMime(file);
			emailHeaders = HeaderExtractor.extractHeaders(msg);
		}
		else {
			emailHeaders = HeaderExtractor.extractHeaders(file.toString());
		}
		
		emailHeaders = HeaderExtractor.getNotMatchingHeaders(emailHeaders, new ArrayList<>(excludedHeaderNamesObsList));
		
		return emailHeaders;
	}
	
	private void toggleAddHeadersPanes() {
		if (isAddFromFileSelected()) {
			showAddHeadersFromFilePane();
		}
		else {
			showAddHeadersManuallyPane();
		}
	}
	
	boolean isAddFromFileSelected() {
		return ((RadioButton) headersImportTypeToggleGroup.getSelectedToggle()).getText().equals(ADD_FROM_FILE_TEXT);
	}
	
	private void showAddHeadersManuallyPane() {
		addHeadersManuallyPane.setVisible(true);
		addHeadersFromFilePane.setVisible(false);
	}
	
	private void showAddHeadersFromFilePane() {
		addHeadersManuallyPane.setVisible(false);
		addHeadersFromFilePane.setVisible(true);
	}
}
