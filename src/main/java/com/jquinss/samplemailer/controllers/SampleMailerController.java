package com.jquinss.samplemailer.controllers;

import com.jquinss.samplemailer.mail.*;
import com.jquinss.samplemailer.managers.SMTPAuthenticationManager;
import com.jquinss.samplemailer.util.AppStyler;
import com.jquinss.samplemailer.util.IntRangeStringConverter;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.image.ImageView;
import com.jquinss.samplemailer.managers.SettingsManager;
import com.jquinss.samplemailer.managers.ListViewManager;
import com.jquinss.samplemailer.util.DialogBuilder;
import com.jquinss.samplemailer.util.Logger;

import java.nio.file.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import jakarta.mail.Message.RecipientType;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

import org.xbill.DNS.TextParseException;

public class SampleMailerController {
	@FXML
	private MenuItem exportTemplatesMenuItem;

	@FXML
	private MenuItem saveTemplatesMenuItem;
	
	@FXML
	private MenuItem saveTemplatesAndExitMenuItem;
	
	@FXML
	private ToggleGroup editorTypeToggleGroup;

	@FXML
	private TextField serverNameTextField;

	@FXML
	private TextField fromTextField;

	@FXML
	private TextField mailFromTextField;
	
	@FXML
	private ImageView serverNameTextFieldTooltip;
	
	@FXML
	private ImageView fromTextFieldTooltip;

	@FXML
	private ImageView delayTextFieldTooltip;

	@FXML
	private CheckBox toggleFromTextFieldCheckBox;
	
	@FXML
	private CheckBox toggleServerTextFieldCheckBox;

	@FXML
	private TextField toTextField;
	
	@FXML
	private TextField ccTextField;

	@FXML
	private TextField bccTextField;

	@FXML
	private ListView<File> attachmentListView;

	@FXML
	private TextField subjectTextField;

	@FXML
	private TextArea bodyTextArea;

	@FXML
	private TextField numEmailsTextField;

	@FXML
	private TextField delayTextField;

	@FXML
	private ToggleButton tlsToggleBtn;

	@FXML
	private TextArea logTextArea;
	
	@FXML
	private HTMLEditor bodyHTMLEditor;
	
	@FXML
	private RadioMenuItem hmtlEditorRadioMenuItem;
	
	@FXML
	private RadioMenuItem plainTextEditorRadioMenuItem;
	
	@FXML
	private CustomHeadersPaneController customHeadersPaneController;
	
	@FXML
	private TemplatesPaneController templatesPaneController;
	
	@FXML
	private SchedulerPaneController schedulerPaneController;
	
	private Stage stage;
	
	private ListViewManager<File> attachmentManager;

	private SMTPAuthenticationManager smtpAuthenticationManager = new SMTPAuthenticationManager();
	
	private final ExecutorService emailTaskExecutor = Executors.newSingleThreadExecutor();
	
	private Logger logger;
	
	private Future<Void> futureSubmittedTask;
	
	private static final String EMAIL_RECIPIENTS_REGEX = "^([a-zA-Z0-9_!#$%&�*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+, ?)*[a-zA-Z0-9_!#$%&�*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
	
	private static final Pattern emailRecipientsPattern = Pattern.compile(EMAIL_RECIPIENTS_REGEX);

	@FXML
	private void addAttachment() {
		FileChooser fileChooser = DialogBuilder.buildFileChooser("Select one or multiple files");
		List<File> files = fileChooser.showOpenMultipleDialog(getStage());
		if (files != null) {
			attachmentManager.addItems(files);
		}
	}
	
	@FXML
	private void switchEditor() {
		String selectedEditor = ((RadioMenuItem) editorTypeToggleGroup.getSelectedToggle()).getText();
		if (selectedEditor.equals("HTML")) {
			bodyHTMLEditor.setVisible(true);
			bodyTextArea.setVisible(false);
		}
		else {
			bodyHTMLEditor.setVisible(false);
			bodyTextArea.setVisible(true);
		}
	}
	
	private String getBodyText() {
		String selectedEditor = ((RadioMenuItem) editorTypeToggleGroup.getSelectedToggle()).getText();
		return selectedEditor.equals("HTML") ? bodyHTMLEditor.getHtmlText() : bodyTextArea.getText();
	}
	
	EmailTemplate createEmailTemplate(String name) {
		EmailTemplate template = new EmailTemplate(name);
		
		template.setServer(serverNameTextField.getText());
		template.setMailFrom(mailFromTextField.getText());
		template.setFrom(fromTextField.getText());
		template.setCustomFrom(toggleFromTextFieldCheckBox.isSelected());
		template.setCustomServer(toggleServerTextFieldCheckBox.isSelected());
		template.setTo(toTextField.getText());
		template.setCC(ccTextField.getText());
		template.setBCC(bccTextField.getText());
		template.setSubject(subjectTextField.getText());
		template.setBody(getBodyText());
		template.setTLSEnabled(tlsToggleBtn.isSelected());
		template.setHTMLText(((RadioMenuItem) editorTypeToggleGroup.getSelectedToggle()).getText().equals("HTML"));
		template.setAttachments(attachmentManager.getItems());
		if (customHeadersPaneController.isAddFromFileSelected() && !customHeadersPaneController.getHeadersFilePath().isEmpty()) {
			template.setAddHeadersFromFile(true);
			template.setHeadersFile(customHeadersPaneController.getHeadersFilePath());
			template.setExcludedHeaderNames(customHeadersPaneController.getExcludedHeaderNames());
		} 
		else {
			template.setHeaders(customHeadersPaneController.getHeaders());
		}
		template.setNumEmails(Integer.parseInt(numEmailsTextField.getText()));
		template.setDelay(Integer.parseInt(delayTextField.getText()));
		
		return template;
	}

	void applyEmailTemplate(EmailTemplate template) {
		toggleServerTextFieldCheckBox.setSelected(template.isCustomServer());
		if (toggleServerTextFieldCheckBox.isSelected()) {
			serverNameTextField.setText(template.getServer());
		}
			
		toggleFromTextFieldCheckBox.setSelected(template.isCustomFrom());
		if (toggleFromTextFieldCheckBox.isSelected()) {
			fromTextField.setText(template.getFrom());
		}
			
		mailFromTextField.setText(template.getMailFrom());
		toTextField.setText(template.getTo());
		ccTextField.setText(template.getCC());
		bccTextField.setText(template.getBCC());
		subjectTextField.setText(template.getSubject());
			
		if (template.isHTMLText()) {
			hmtlEditorRadioMenuItem.setSelected(true);
			bodyHTMLEditor.setHtmlText(template.getBody());
		}
		else {
			plainTextEditorRadioMenuItem.setSelected(true);
			bodyTextArea.setText(template.getBody());
		}
			
		switchEditor();
		bodyTextArea.setText(getBodyText());
		tlsToggleBtn.setSelected(template.isTLSEnabled());
		attachmentManager.setItems(template.getAttachments());
			
		if (template.isAddHeadersFromFile()) {
			customHeadersPaneController.enableAddHeadersPane(CustomHeadersPaneController.ADD_FROM_FILE_TEXT);
			customHeadersPaneController.setHeadersFilePath(template.getHeadersFile());
			customHeadersPaneController.setExcludedHeaderNames(template.getExcludedHeaderNames());
		}
		else {
			customHeadersPaneController.enableAddHeadersPane(CustomHeadersPaneController.ADD_MANUALLY_TEXT);
			customHeadersPaneController.setHeaders(template.getHeaders());
		}
			
		numEmailsTextField.setText(Integer.toString(template.getNumEmails()));
		delayTextField.setText(Integer.toString(template.getDelay()));
	}

	@FXML
	private void cancelEmail() {
		if (futureSubmittedTask != null) {
			if (futureSubmittedTask.cancel(true)) {
				logger.logMessage("Email has been cancelled");
			}
		}
	}

	@FXML
	private void clearEmail() {
		serverNameTextField.clear();;
		toggleFromTextFieldCheckBox.setSelected(false);
		mailFromTextField.clear();
		toTextField.clear();
		ccTextField.clear();
		bccTextField.clear();;
		subjectTextField.clear();
		bodyTextArea.clear();
		bodyHTMLEditor.setHtmlText("");
		tlsToggleBtn.setSelected(false);
		attachmentManager.removeAllItems();
		customHeadersPaneController.clearData();
		numEmailsTextField.clear();
		delayTextField.clear();
	}
	
	private void handleStageClosure(WindowEvent e) {
		if (!templatesPaneController.isDataSaved().get()) {
			Dialog<ButtonType> dialog = DialogBuilder.buildConfirmationDialog("Save Changes", "Some templates have not been saved." , "Do you want to save your changes?");
			dialog.getDialogPane().setPrefSize(420, 160);
			setStyles(dialog.getDialogPane());
			setWindowLogo(dialog.getDialogPane(), SettingsManager.getInstance().getDialogLogoImage());
			
			Optional<ButtonType> result = dialog.showAndWait();
			if (result.isPresent()) {
				ButtonType btn = (ButtonType) result.get();
				if (btn.getButtonData() == ButtonData.OK_DONE) {
					saveTemplatesAndExit();
				}
				else if (btn.getButtonData() == ButtonData.NO) {
					exit();
				}
				else {
					e.consume();
				}
			}
		}
		else {
			exit();
		}
	}

	@FXML
	private void exit() {
		shutdownExecutors();
		try {
			saveSMTPAuthenticationData();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		stage.close();
	}

	@FXML
	private void saveTemplatesAndExit() {
		saveTemplates();
		exit();
	}
	
	@FXML
	private void openAboutInfo() {
		showAlertDialog("About", "", "SampleMailerv1.1\n\nCreated by Joaquin Sampedro", AlertType.INFORMATION);
	}

	@FXML
	private void openSettingsDialog() throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/jquinss/samplemailer/fxml/SettingsPane.fxml"));
		Parent parent = fxmlLoader.load();
		SettingsPaneController settingsPaneController = fxmlLoader.getController();
		
		Scene scene = new Scene(parent, 710, 300);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Settings");

		setStyles(scene);
		setWindowLogo(stage, SettingsManager.getInstance().getSettingsLogoImage());
        
        settingsPaneController.setStage(stage);

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
	}

	@FXML
	private void openSMTPAuthenticationDialog() throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/jquinss/samplemailer/fxml/SMTPAuthenticationPane.fxml"));
		SMTPAuthenticationPaneController smtpAuthenticationPaneController = new SMTPAuthenticationPaneController(smtpAuthenticationManager);
		fxmlLoader.setController(smtpAuthenticationPaneController);

		Parent parent = fxmlLoader.load();
		Scene scene = new Scene(parent, 415, 345);
		Stage stage = new Stage();
		stage.setResizable(false);
		stage.setTitle("SMTP Authentication Manager");

		setStyles(scene);
		setWindowLogo(stage, SettingsManager.getInstance().getMainLogoImage());

		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(scene);
		stage.showAndWait();
	}

	@FXML
	private void removeAttachment() {
		File attachment = attachmentManager.getSelectedItem();
		
		if (attachment != null) {
			attachmentManager.removeItem(attachment);
		}
		else {
			showAlertDialog("Error", "Error removing attachment", "No attachments have been selected", AlertType.WARNING);
		}
	}

	@FXML
	private void saveTemplates() {
		try {
			templatesPaneController.saveEmailTemplates(SettingsManager.getInstance().getTemplatesFilePath());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void importTemplates() {
		FileChooser fileChooser = DialogBuilder.buildFileChooser("Import templates",
				new ExtensionFilter("Template files", "*.dat"));

		File file = fileChooser.showOpenDialog(stage);

		if (file != null) {
			if (!templatesPaneController.isEmptyEmailTemplateList().getValue()) {
				Dialog<ButtonType> dialog = DialogBuilder.buildAlertDialog("Confirmation", "Import templates", """
					There are already existing templates. Select "OK" if
					you want to merge them. Otherwise, if you want to
					overwrite them, select "Cancel".""", AlertType.CONFIRMATION);

				setStyles(dialog.getDialogPane());
				setWindowLogo(dialog.getDialogPane(), SettingsManager.getInstance().getDialogLogoImage());

				Optional<ButtonType> result = dialog.showAndWait();

				if (result.isPresent()) {
					try {
						templatesPaneController.loadEmailTemplates(file.toString(), result.get() == ButtonType.OK, false);
					}
					catch (IOException | ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
			else {
				try {
					templatesPaneController.loadEmailTemplates(file.toString(), false, false);
				}
				catch (IOException | ClassNotFoundException e) {
						e.printStackTrace();
				}
			}
		}
	}

	@FXML
	private void exportTemplates() {
		if (!templatesPaneController.isDataSaved().getValue()) {
			Dialog<ButtonType> dialog = DialogBuilder.buildAlertDialog("Confirmation", "Save templates", """
					The templates have been modified. You need to
					save them first before they can be exported.
					
					Do you want to proceed?""", AlertType.CONFIRMATION);

			setStyles(dialog.getDialogPane());
			setWindowLogo(dialog.getDialogPane(), SettingsManager.getInstance().getDialogLogoImage());

			Optional<ButtonType> result = dialog.showAndWait();

			if (result.isPresent()) {
				if (result.get() == ButtonType.OK) {
					saveTemplates();
				}
			}
		}

		FileChooser fileChooser = DialogBuilder.buildFileChooser("Export templates",
				new ExtensionFilter("Template files", "*.dat"));
		fileChooser.setInitialFileName(new File(SettingsManager.getInstance().getTemplatesFilePath()).getName());

		File file = fileChooser.showSaveDialog(stage);

		if (file != null) {
			Path sourcePath = Paths.get(SettingsManager.getInstance().getTemplatesFilePath());
			Path destinationPath = file.toPath();
			try {
				Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void saveSMTPAuthenticationData() throws IOException {
		smtpAuthenticationManager.saveSMTPAuthenticationData(SettingsManager.getInstance().getSMTPAuthDataFilePath());
	}

	@FXML
	private void sendEmail() {
		try {
			EmailTask emailTask = createEmailTask();
			executeEmailTask(emailTask);
		}
		catch (MessagingException e) {
			showAlertDialog("Error", "Error creating email", "Error: " + e.getMessage(), AlertType.ERROR);
		} catch (TextParseException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void clearLogs() {
		logTextArea.clear();
	}
	
	@FXML
	private void exportLogs() {
		FileChooser fileChooser = DialogBuilder.buildFileChooser("Export Logs", new ExtensionFilter("TXT files", "*.txt"));
    	fileChooser.setInitialFileName("logs.txt");

    	File file = fileChooser.showSaveDialog(stage);

    	if (file != null) {
    		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
    			writer.write(logTextArea.getText(), 0, logTextArea.getText().length());
    		}
    		catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
	}

	@FXML
	public void initialize() throws IOException, ClassNotFoundException {
		logger = new Logger(logTextArea);
		initializeManagers();
		initializeControllers();
		initializeControls();
		loadSettings();
	}
	
    public void setStage(Stage stage) {
    	this.stage = stage;
    	this.stage.setOnCloseRequest(e -> {
    		handleStageClosure(e);
    	});
    }
    
    public Stage getStage() {
    	return this.stage;
    }

	private void loadSettings() throws IOException, ClassNotFoundException {
		SettingsManager.getInstance().loadSettings(getDefaultSettings());
	}

	private Properties getDefaultSettings() {
		Properties defaultSettings = new Properties();
		try {
			defaultSettings.setProperty("mail.smtp.localhost", InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		defaultSettings.setProperty("mail.smtp.port", "25");
		defaultSettings.setProperty("mail.smtp.connectiontimeout", "-1");
		defaultSettings.setProperty("mail.smtp.ssl.protocols", "TLSv1.1 TLSv1.2");
		
		defaultSettings.setProperty("mail.content.contenttype", "text/html");
		defaultSettings.setProperty("mail.content.charset", "utf-8");
		
		defaultSettings.setProperty("mail.debug", "false");
		defaultSettings.setProperty("mail.debugurl", SettingsManager.getInstance().getDataPath() + File.separator + "Debug.log");

		return defaultSettings;
	}

	private void initializeManagers() throws IOException, ClassNotFoundException {
		attachmentManager = new ListViewManager<File>(attachmentListView);
		try {
			smtpAuthenticationManager.loadSMTPAuthenticationData(SettingsManager.getInstance().getSMTPAuthDataFilePath());
		}
		catch (FileNotFoundException e) {
			// ignore exception as the files may not have been created yet
		}
	}
	
	private void initializeControllers() {
		schedulerPaneController.setMainController(this);
		schedulerPaneController.setLogger(logger);
		customHeadersPaneController.setMainController(this);
		templatesPaneController.setMainController(this);

	}

	private void initializeControls() {
		setTextFieldsFormatters();
		setControlsBinding();
		setControlsListeners();
		setTooltips();
	}
	
	private void setTextFieldsFormatters() {
		numEmailsTextField.setTextFormatter(new TextFormatter<>(new IntRangeStringConverter(1, 1000), 1));
		delayTextField.setTextFormatter(new TextFormatter<>(new IntRangeStringConverter(0, 1000000), 0));
	}
	
	private void setControlsListeners() {
		toggleFromTextFieldCheckBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
		    if (isNowSelected) {
		    	fromTextField.setDisable(false);
		    	fromTextField.textProperty().unbind();
		    } else {
		    	fromTextField.setDisable(true);
		    	fromTextField.textProperty().bind(mailFromTextField.textProperty());
		    } 
		});
		
		toggleServerTextFieldCheckBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
			if (isNowSelected) {
		    	serverNameTextField.setDisable(false);
		    } else {
		    	serverNameTextField.setDisable(true);
		    	serverNameTextField.clear();
		    } 
		});
	}
	
	private void setControlsBinding() {
		exportTemplatesMenuItem.disableProperty().bind(templatesPaneController.isEmptyEmailTemplateList());
		saveTemplatesMenuItem.disableProperty().bind(templatesPaneController.isDataSaved());
		saveTemplatesAndExitMenuItem.disableProperty().bind(templatesPaneController.isDataSaved());
		fromTextField.textProperty().bind(mailFromTextField.textProperty());
	}
	
	private void setTooltips() {
		AppStyler.setTooltip(serverNameTextFieldTooltip, this, SettingsManager.getInstance().getDialogLogoImage(), """
														Disable this option, if you want the server
														name to be automatically resolved using
														the MX records of the recipient domain""");
		AppStyler.setTooltip(fromTextFieldTooltip, this, SettingsManager.getInstance().getDialogLogoImage(), """
				By default, the "From" field will get the
				same value as the "Mail From". To be
				able to customize it, select this option""");
		AppStyler.setTooltip(delayTextFieldTooltip, this, SettingsManager.getInstance().getDialogLogoImage(), """
				When sending several emails, this is the
				delay in seconds between each email""");
	}
	
	private void setWindowLogo(DialogPane dialogPane, String logo) {
		AppStyler.setWindowLogo(dialogPane, this, logo);
	}

	private void setWindowLogo(Stage stage, String logo) {
		AppStyler.setWindowLogo(stage, this, logo);
	}

	private void setStyles(DialogPane dialogPane) {
		AppStyler.setStyles(dialogPane, this, SettingsManager.getInstance().getCSS());
	}

	private void setStyles(Scene scene) {
		AppStyler.setStyles(scene, this, SettingsManager.getInstance().getCSS());
	}
	
	private List<MimeMessage> createMimeMessages() throws MessagingException, TextParseException {
		List<MimeMessage> messages = new ArrayList<MimeMessage>();
		
		Properties settings = getSettings();
		String debugURL = settings.getProperty("mail.debugurl");
		
		String sender = fromTextField.getText().trim();
		String toRecipients = toTextField.getText().trim();
		String ccRecipients = ccTextField.getText().trim();
		String bccRecipients = bccTextField.getText().trim();

		SMTPAuthenticationProfile smtpAuthenticationProfile = smtpAuthenticationManager.getSMTPAuthenticationProfile(settings.getProperty("mail.smtp.from"));

		if (smtpAuthenticationProfile != null && smtpAuthenticationProfile.isEnabled()) {
			String emailAddress = smtpAuthenticationProfile.getEmailAddress();
			logger.logMessage("Found an authentication profile that matches the sender email address (" + emailAddress + ")");
			logger.logMessage("Requesting for authentication");
			Dialog<String> dialog = DialogBuilder.buildPasswordFieldInputDialog("Authenticate", "Enter password",
									"Enter the authentication password for \nthe email account " + emailAddress);

			setStyles(dialog.getDialogPane());
			setWindowLogo(dialog.getDialogPane(), SettingsManager.getInstance().getDialogLogoImage());

			Optional<String> input = dialog.showAndWait();
			if (input.isPresent()) {
				String password = input.get();

				String server = smtpAuthenticationProfile.getServerProfile().getServerHostName();
				String port = smtpAuthenticationProfile.getServerProfile().getPort();
				boolean tlsEnabled = smtpAuthenticationProfile.getServerProfile().isTLSEnabled();

				Authenticator authenticator = new Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(emailAddress, password);
					}
				};

				Properties settingsCopy = copySettings(settings);
				settingsCopy.setProperty("mail.smtp.host", server);
				settingsCopy.setProperty("mail.smtp.starttls.enable", Boolean.toString(tlsEnabled));
				settingsCopy.setProperty("mail.smtp.auth", "true");
				settingsCopy.setProperty("mail.smtp.port", port);

				Session session = Session.getInstance(settingsCopy, authenticator);

				MimeMessageBuilder mimeMessageBuilder = new MimeMessageBuilder(session);

				mimeMessageBuilder.setFrom(sender);
				mimeMessageBuilder.setRecipients(RecipientType.TO, toRecipients);
				mimeMessageBuilder.setRecipients(RecipientType.CC, ccRecipients);
				mimeMessageBuilder.setRecipients(RecipientType.BCC, bccRecipients);

				addCustomHeadersAndBodyContents(mimeMessageBuilder, settings);

				messages.add(mimeMessageBuilder.buildMessage());
			}
			else {
				logger.logMessage("Authentication dialog has been cancelled");
			}
		}
		else {
			HashMap<String, HashMap<RecipientType, List<String>>> serverToRecipientsMap = getServerToRecipientsMap(toRecipients, ccRecipients, bccRecipients);

			Set<String> servers = serverToRecipientsMap.keySet();

			for (String server : servers) {
				Properties settingsCopy = copySettings(settings);
				settingsCopy.setProperty("mail.smtp.host", server);

				Session session = Session.getInstance(settingsCopy);

				try {
					session.setDebugOut(new PrintStream(debugURL));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				HashMap<RecipientType, List<String>> rcptTypeToRcptsMap = serverToRecipientsMap.get(server);

				MimeMessageBuilder mimeMessageBuilder = new MimeMessageBuilder(session);

				mimeMessageBuilder.setFrom(sender);

				if (rcptTypeToRcptsMap.containsKey(RecipientType.TO)) {
					mimeMessageBuilder.setRecipients(RecipientType.TO, rcptTypeToRcptsMap.get(RecipientType.TO));
				}

				if (rcptTypeToRcptsMap.containsKey(RecipientType.CC)) {
					mimeMessageBuilder.setRecipients(RecipientType.CC, rcptTypeToRcptsMap.get(RecipientType.CC));
				}

				if (rcptTypeToRcptsMap.containsKey(RecipientType.BCC)) {
					mimeMessageBuilder.setRecipients(RecipientType.BCC, rcptTypeToRcptsMap.get(RecipientType.BCC));
				}

				addCustomHeadersAndBodyContents(mimeMessageBuilder, settings);

				messages.add(mimeMessageBuilder.buildMessage());
			}
		}

		return messages;
	}

	private void addCustomHeadersAndBodyContents(MimeMessageBuilder mimeMessageBuilder, Properties settings) throws MessagingException {
		if (!customHeadersPaneController.getHeaders().isEmpty()) {
			mimeMessageBuilder.setHeaders(customHeadersPaneController.getHeaders());
		}

		if (!subjectTextField.getText().isEmpty()) {
			mimeMessageBuilder.setSubject(subjectTextField.getText());
		}

		if (!attachmentManager.getItems().isEmpty()) {
			mimeMessageBuilder.setMultiPartBody(attachmentManager.getItems(), getBodyText(),
					settings.getProperty("mail.content.contenttype"), settings.getProperty("mail.content.charset"));
		}
		else {
			mimeMessageBuilder.setBody(getBodyText(), settings.getProperty("mail.content.contenttype"),
					settings.getProperty("mail.content.charset"));
		}
	}

	private Properties copySettings(Properties settings) {
		Properties settingsCopy = new Properties();
		settings.forEach((key, value) -> {
			settingsCopy.setProperty((String) key, (String) value);
		});
		return  settingsCopy;
	}
	
	private Properties getSettings() {
		Properties settings = SettingsManager.getInstance().getSettings();
		
		if (!toggleFromTextFieldCheckBox.isDisabled()) {
			settings.setProperty("mail.smtp.from", mailFromTextField.getText().trim());
		}
		else {
			settings.setProperty("mail.smtp.from", fromTextField.getText().trim());
		}
		
		if (tlsToggleBtn.isSelected()) {
			settings.setProperty("mail.smtp.starttls.enable", "true");
		}
		else {
			settings.setProperty("mail.smtp.starttls.enable", "false");
		}
		
		return settings;
	}
	
	private HashMap<String, HashMap<RecipientType, List<String>>> getServerToRecipientsMap(String toRecipients, String ccRecipients, String bccRecipients) throws TextParseException {
		HashMap<String, HashMap<RecipientType, List<String>>> serverToRecipientsMap = new HashMap<String, HashMap<RecipientType, List<String>>>();
		
		HashMap<RecipientType, List<String>> rcptTypeToRcptMap = new HashMap<RecipientType, List<String>>();

		addRecipientsToRecipientTypeToRecipientMap(rcptTypeToRcptMap, toRecipients, RecipientType.TO);
		addRecipientsToRecipientTypeToRecipientMap(rcptTypeToRcptMap, ccRecipients, RecipientType.CC);
		addRecipientsToRecipientTypeToRecipientMap(rcptTypeToRcptMap, bccRecipients, RecipientType.BCC);
		
		if (toggleServerTextFieldCheckBox.isSelected()) {
			String server = serverNameTextField.getText().trim();
			serverToRecipientsMap.put(server, rcptTypeToRcptMap);
		}
		else {
			serverToRecipientsMap = ServerToRecipientsMapper.getServerToRecipientsMap(rcptTypeToRcptMap);
		}

		return serverToRecipientsMap;
	}
	
	private void addRecipientsToRecipientTypeToRecipientMap(HashMap<RecipientType, List<String>> recipientTypeToRecipientMap, 
															String recipients, RecipientType recipientType) {
		Matcher rcptsMatcher = emailRecipientsPattern.matcher(recipients);
		if (rcptsMatcher.matches()) {
			List<String> rcpts = Arrays.asList(recipients.split(", ?"));
			recipientTypeToRecipientMap.put(recipientType, rcpts);
		}
		
	}
	
	EmailTask createEmailTask() throws MessagingException, TextParseException {
		List<MimeMessage> mimeMessages = createMimeMessages();
		int numEmails = Integer.parseInt(numEmailsTextField.getText());
		int delay = Integer.parseInt(delayTextField.getText());

		return new EmailTask(mimeMessages, numEmails, delay);
	}
	
	private void executeEmailTask(EmailTask emailTask) {
		emailTask.setLogger(logger);
		futureSubmittedTask = emailTaskExecutor.submit(emailTask);
	}
	
	void showAlertDialog(String title, String headerText, String contentText, AlertType alertType) {
		Alert alert = DialogBuilder.buildAlertDialog(title, headerText, contentText, alertType);

		setStyles(alert.getDialogPane());
		setWindowLogo(alert.getDialogPane(), SettingsManager.getInstance().getDialogLogoImage());

		alert.show();
	}
	
	private void shutdownExecutors() {
		if (!emailTaskExecutor.isShutdown()) {
			emailTaskExecutor.shutdownNow();
		}
		
		schedulerPaneController.shutdownExecutors();
	}
}
