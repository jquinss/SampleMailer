package com.jquinss.samplemailer.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
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
import javafx.scene.control.Tooltip;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.converter.IntegerStringConverter;
import javafx.scene.image.ImageView;
import com.jquinss.samplemailer.managers.SettingsManager;
import com.jquinss.samplemailer.managers.ListViewManager;
import com.jquinss.samplemailer.mail.EmailTask;
import com.jquinss.samplemailer.mail.EmailTemplate;
import com.jquinss.samplemailer.mail.MimeMessageBuilder;
import com.jquinss.samplemailer.util.DialogBuilder;
import com.jquinss.samplemailer.util.Logger;
import com.jquinss.samplemailer.util.OSChecker;
import com.jquinss.samplemailer.mail.ServerToRecipientsMapper;

import java.util.function.UnaryOperator;
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
	private MenuItem saveTemplatesBtn;
	
	@FXML
	private MenuItem saveTemplatesAndExitBtn;
	
	@FXML
	private ToggleGroup editorTypeToggleGroup;

	@FXML
	private TextField serverNameField;

	@FXML
	private TextField fromField;

	@FXML
	private TextField mailFromField;
	
	@FXML
	private ImageView serverNameFieldQuestionMark;
	
	@FXML
	private ImageView fromFieldQuestionMark;

	@FXML
	private CheckBox toggleFromField;
	
	@FXML
	private CheckBox toggleServerField;

	@FXML
	private TextField toField;
	
	@FXML
	private TextField ccField;

	@FXML
	private TextField bccField;

	@FXML
	private ListView<File> attachmentListView;

	@FXML
	private TextField subjectField;

	@FXML
	private TextArea bodyTextArea;

	@FXML
	private TextField numEmailsField;

	@FXML
	private TextField delayField;

	@FXML
	private ToggleButton toggleTLSBtn;

	@FXML
	private TextArea logArea;
	
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
	
	private final ExecutorService emailTaskExecutor = Executors.newSingleThreadExecutor();
	
	private Logger logger;
	
	private Future<Void> futureSubmittedTask;
	
	private static final String EMAIL_RECIPIENTS_REGEX = "^([a-zA-Z0-9_!#$%&�*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+, ?)*[a-zA-Z0-9_!#$%&�*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
	
	private static final Pattern emailRecipientsPattern = Pattern.compile(EMAIL_RECIPIENTS_REGEX);

	@FXML
	void addAttachment(ActionEvent event) {
		FileChooser fileChooser = DialogBuilder.getFileChooser("Select one or multiple files");
		List<File> files = fileChooser.showOpenMultipleDialog(getStage());
		if (files != null) {
			attachmentManager.addItems(files);
		}
	}
	
	@FXML
	void switchEditor(ActionEvent event) {
		switchEditor();
	}
	
	void switchEditor() {
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
		if (selectedEditor.equals("HTML")) {
			return bodyHTMLEditor.getHtmlText();
		}
		else {
			return bodyTextArea.getText();
		}
	}
	
	EmailTemplate createEmailTemplate(String name) {
		EmailTemplate template = new EmailTemplate(name);
		
		template.setServer(serverNameField.getText());
		template.setMailFrom(mailFromField.getText());
		template.setFrom(fromField.getText());
		template.setCustomFrom(toggleFromField.isSelected());
		template.setCustomServer(toggleServerField.isSelected());
		template.setTo(toField.getText());
		template.setCC(ccField.getText());
		template.setBCC(bccField.getText());
		template.setSubject(subjectField.getText());
		template.setBody(getBodyText());
		template.setTLSEnabled(toggleTLSBtn.isSelected());
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
		template.setNumEmails(Integer.parseInt(numEmailsField.getText()));
		template.setDelay(Integer.parseInt(delayField.getText()));
		
		return template;
	}

	void applyTemplate(EmailTemplate template) {
		toggleServerField.setSelected(template.isCustomServer());
		if (toggleServerField.isSelected()) {
			serverNameField.setText(template.getServer());
		}
			
		toggleFromField.setSelected(template.isCustomFrom());
		if (toggleFromField.isSelected()) {
			fromField.setText(template.getFrom());
		}
			
		mailFromField.setText(template.getMailFrom());
		toField.setText(template.getTo());
		ccField.setText(template.getCC());
		bccField.setText(template.getBCC());
		subjectField.setText(template.getSubject());
			
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
		toggleTLSBtn.setSelected(template.isTLSEnabled());
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
			
		numEmailsField.setText(Integer.toString(template.getNumEmails()));
		delayField.setText(Integer.toString(template.getDelay()));
	}

	@FXML
	void cancelEmail(ActionEvent event) {
		if (futureSubmittedTask != null) {
			if (futureSubmittedTask.cancel(true)) {
				System.out.println("Email has been cancelled");
			}
		}
	}

	@FXML
	void clearEmail(ActionEvent event) {
		serverNameField.clear();;
		toggleFromField.setSelected(false);
		mailFromField.clear();
		toField.clear();
		ccField.clear();
		bccField.clear();;
		subjectField.clear();
		bodyTextArea.clear();
		bodyHTMLEditor.setHtmlText("");
		toggleTLSBtn.setSelected(false);
		attachmentManager.removeAllItems();
		customHeadersPaneController.clearData();
		numEmailsField.clear();
		delayField.clear();
	}
	
	private void handleStageClosure(WindowEvent e) {
		if (!templatesPaneController.isTemplateListSaved().get()) {
			Dialog<ButtonType> dialog = DialogBuilder.buildConfirmationDialog("Save Changes", "Some templates have not been saved." , "Do you want to save your changes?");
			dialog.getDialogPane().setPrefSize(420, 160);
			dialog.getDialogPane().getStylesheets().add(getClass().getResource("/com/jquinss/samplemailer/styles/application.css").toString());
			setDialogPaneWindowLogo(dialog.getDialogPane());
			
			Optional<ButtonType> result = dialog.showAndWait();
			if (result.isPresent()) {
				ButtonType btn = (ButtonType) result.get();
				if (btn.getButtonData() == ButtonData.OK_DONE) {
					saveAndExit();
				}
				else if (btn.getButtonData() == ButtonData.NO) {
					exit();
				}
				else {
					e.consume();
				}
			}
		}
	}

	@FXML
	void exitApp(ActionEvent event) {
		exit();
	}
	
	private void exit() {
		shutdownExecutors();
		stage.close();
	}
	
	private void saveAndExit() {
		saveTemplatesIfModified();;
		exit();
	}
	
	@FXML
	void openAboutInfo(ActionEvent event) {
		showAlertDialog("About", "", "SampleMailerv1.0\n\nCreated by Joaquin Sampedro", AlertType.INFORMATION);
	}

	@FXML
	void openSettingsDialog(ActionEvent event) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/jquinss/samplemailer/fxml/SettingsPane.fxml"));
		Parent parent = fxmlLoader.load();
		
		SettingsPaneController settingsPaneController = fxmlLoader.getController();
		
		Scene scene = new Scene(parent, 710, 300);
		scene.getStylesheets().add(getClass().getResource("/com/jquinss/samplemailer/styles/application.css").toString());
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Settings");
        stage.getIcons().add(new Image(getClass().getResource("/com/jquinss/samplemailer/images/settings.png").toString()));
        
        settingsPaneController.setStage(stage);
        
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
	}

	@FXML
	void removeAttachment(ActionEvent event) {
		File attachment = attachmentManager.getSelectedItem();
		
		if (attachment != null) {
			attachmentManager.removeItem(attachment);
		}
		else {
			showAlertDialog("Error", "Error removing attachment", "No attachments have been selected", AlertType.WARNING);
		}
	}

	@FXML
	void saveTemplates(ActionEvent event) {
		saveTemplatesIfModified();
	}
	
	@FXML
	void saveTemplatesAndExit(ActionEvent event) {
		saveTemplatesIfModified();;
		shutdownExecutors();
		stage.close();
	}
	
	private void saveTemplatesIfModified() {
		if (!templatesPaneController.isTemplateListSaved().get()) {
			templatesPaneController.saveTemplates();
		}
	}

	@FXML
	void sendEmail(ActionEvent event) {
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
	void clearLogs(ActionEvent event) {
		logArea.clear();
	}
	
	@FXML
	void exportLogs(ActionEvent event) {
		FileChooser fileChooser = DialogBuilder.getFileChooser("Export Logs", new ExtensionFilter("TXT files", "*.txt"));
    	fileChooser.setInitialFileName("logs.txt");

    	File file = fileChooser.showSaveDialog(stage);

    	if (file != null) {
    		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
    			writer.write(logArea.getText(), 0, logArea.getText().length());
    		}
    		catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
	}

	@FXML
	public void initialize() {
		logger = new Logger(logArea);
		initializeManagers();
		initializeControls();
		loadSettings();
		loadTemplates();
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

	private void loadSettings() {
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
		defaultSettings.setProperty("mail.debugurl", OSChecker.getOSDataDirectory() + File.separator + "SampleMailer" + File.separator + "Debug.log");

		return defaultSettings;
	}

	private void loadTemplates() {
		templatesPaneController.loadTemplates();
	}
	
	private void initializeManagers() {
		attachmentManager = new ListViewManager<File>(attachmentListView);
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
		UnaryOperator<TextFormatter.Change> numEmailsFilter = createInputFilter("[1-9][0-9]{0,2}", "1");
		UnaryOperator<TextFormatter.Change> delayFilter = createInputFilter("[0-9]|[1-9][0-9]{0,4}", "0");

		numEmailsField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 1, numEmailsFilter));
		delayField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, delayFilter));
	}
	
	private void setControlsListeners() {
		toggleFromField.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
		    if (isNowSelected) {
		    	fromField.setDisable(false);
		    	fromField.textProperty().unbind();
		    } else {
		    	fromField.setDisable(true);
		    	fromField.textProperty().bind(mailFromField.textProperty());
		    } 
		});
		
		toggleServerField.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
			if (isNowSelected) {
		    	serverNameField.setDisable(false);
		    } else {
		    	serverNameField.setDisable(true);
		    	serverNameField.clear();
		    } 
		});
	}
	
	private void setControlsBinding() {
		saveTemplatesBtn.disableProperty().bind(templatesPaneController.isTemplateListSaved());
		saveTemplatesAndExitBtn.disableProperty().bind(templatesPaneController.isTemplateListSaved());
		fromField.textProperty().bind(mailFromField.textProperty());
	}

	private UnaryOperator<TextFormatter.Change> createInputFilter(String validInputRegEx, String defaultText) {
		UnaryOperator<TextFormatter.Change> filter = change -> {
			String newText = change.getControlNewText();
			if (newText.matches(validInputRegEx)) {
				return change;
			} else if (newText.matches("")) {
				change.setText(defaultText);
				change.setCaretPosition(change.getCaretPosition() + 1);

				return change;
			}

			return null;
		};

		return filter;
	}
	
	private void setTooltips() {
		serverNameFieldQuestionMark.setImage(new Image(getClass().getResource("/com/jquinss/samplemailer/images/question_mark.png").toString()));
		fromFieldQuestionMark.setImage(new Image(getClass().getResource("/com/jquinss/samplemailer/images/question_mark.png").toString()));
		Tooltip.install(serverNameFieldQuestionMark, new Tooltip("Disable this option, if you want the server\nname to be automatically resolved using\n the MX records of the recipient domain"));
		Tooltip.install(fromFieldQuestionMark, new Tooltip("By default, the \"From\" field will get the\nsame value as the \"Mail From\". To be\nable to customize it, select this option"));
	}
	
	void setDialogPaneWindowLogo(DialogPane dialogPane) {
		Stage stage = (Stage) dialogPane.getScene().getWindow();
		stage.getIcons().add(new Image(getClass().getResource("/com/jquinss/samplemailer/images/question_mark.png").toString()));
	}
	
	private List<MimeMessage> createMimeMessages() throws MessagingException, TextParseException {
		List<MimeMessage> messages = new ArrayList<MimeMessage>();
		
		Properties settings = getSettings();
		String debugURL = settings.getProperty("mail.debugurl");
		
		String sender = fromField.getText().trim();
		String toRecipients = toField.getText().trim();
		String ccRecipients = ccField.getText().trim();
		String bccRecipients = bccField.getText().trim();
		
		HashMap<String, HashMap<RecipientType, List<String>>> serverToRecipientsMap = getServerToRecipientsMap(toRecipients, ccRecipients, bccRecipients);
		
		Set<String> servers = serverToRecipientsMap.keySet();
		
		for (String server : servers) {
			Properties settingsCopy = new Properties();
			settings.forEach((key, value) -> {
				settingsCopy.setProperty((String) key, (String) value);
			});
			
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
				
			if (!customHeadersPaneController.getHeaders().isEmpty()) {
				mimeMessageBuilder.setHeaders(customHeadersPaneController.getHeaders());
			}
				
			if (!subjectField.getText().isEmpty()) {
				mimeMessageBuilder.setSubject(subjectField.getText());
			}
				
			if (!attachmentManager.getItems().isEmpty()) {
				mimeMessageBuilder.setMultiPartBody(attachmentManager.getItems(), getBodyText(), 
				settings.getProperty("mail.content.contenttype"), settings.getProperty("mail.content.charset"));
			}
			else {
				mimeMessageBuilder.setBody(getBodyText(), settings.getProperty("mail.content.contenttype"), 
				settings.getProperty("mail.content.charset"));
			}
			
			messages.add(mimeMessageBuilder.buildMessage());
		}
		
		return messages;
	}
	
	private Properties getSettings() {
		Properties settings = SettingsManager.getInstance().getSettings();
		
		if (!toggleFromField.isDisabled()) {
			settings.setProperty("mail.smtp.from", mailFromField.getText().trim());
		}
		else {
			settings.setProperty("mail.smtp.from", fromField.getText().trim());
		}
		
		if (toggleTLSBtn.isSelected()) {
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
		
		if (toggleServerField.isSelected()) {
			String server = serverNameField.getText().trim();
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
		int numEmails = Integer.parseInt(numEmailsField.getText());
		int delay = Integer.parseInt(numEmailsField.getText());
		EmailTask emailTask = new EmailTask(mimeMessages, numEmails, delay);
		
		return emailTask;
	}
	
	private void executeEmailTask(EmailTask emailTask) {
		emailTask.setLogger(logger);
		futureSubmittedTask = emailTaskExecutor.submit(emailTask);
	}
	
	void showAlertDialog(String title, String headerText, String contentText, AlertType alertType) {
		Alert alert = DialogBuilder.getAlertDialog(title, headerText, contentText, alertType);
		alert.getDialogPane().getStylesheets().add(getClass().getResource("/com/jquinss/samplemailer/styles/application.css").toString());
		setDialogPaneWindowLogo(alert.getDialogPane());
		alert.show();
	}
	
	private void shutdownExecutors() {
		if (!emailTaskExecutor.isShutdown()) {
			emailTaskExecutor.shutdownNow();
		}
		
		schedulerPaneController.shutdownExecutors();
	}
}
