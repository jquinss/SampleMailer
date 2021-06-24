package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import managers.SettingsManager;
import managers.TableViewManager;
import managers.TemplateManager;
import managers.ListViewManager;
import managers.EmailScheduler;
import mail.EmailHeader;
import mail.EmailTask;
import mail.EmailTemplate;
import mail.MimeMessageBuilder;
import mail.ScheduledEmailTask;
import util.DialogBuilder;
import util.Logger;
import util.OSChecker;
import control.DateTimePicker;
import exceptions.InvalidDateTimeException;

import java.util.function.UnaryOperator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

public class SampleMailerController {

	@FXML
	private MenuItem saveTemplatesBtn;
	
	@FXML
	private MenuItem saveTemplatesAndExitBtn;

	@FXML
	private MenuItem exitAppBtn;

	@FXML
	private MenuItem openSettingsDialogBtn;

	@FXML
	private MenuItem aboutBtn;

	@FXML
	private TextField serverNameField;

	@FXML
	private TextField fromField;

	@FXML
	private TextField mailFromField;

	@FXML
	private CheckBox toggleFromField;

	@FXML
	private TextField toField;
	
	@FXML
	private TextField ccField;

	@FXML
	private TextField bccField;

	@FXML
	private ListView<File> attachmentListView;

	@FXML
	private Button addAttachmentBtn;

	@FXML
	private Button removeAttachmentBtn;

	@FXML
	private TableView<EmailHeader> headerTableView;

	@FXML
	private TextField headerNameField;

	@FXML
	private TextField headerValueField;

	@FXML
	private Button addHeaderBtn;

	@FXML
	private Button modifyHeaderBtn;

	@FXML
	private Button removeHeaderBtn;

	@FXML
	private ListView<EmailTemplate> templateListView;

	@FXML
	private Button addTemplateBtn;

	@FXML
	private Button applyTemplateBtn;

	@FXML
	private Button removeTemplateBtn;

	@FXML
	private TableView<ScheduledEmailTask> schedulerTableView;

	@FXML
	private DateTimePicker dateTimePicker;

	@FXML
	private Button scheduleEmailBtn;

	@FXML
	private Button cancelScheduledEmailBtn;

	@FXML
	private TextField subjectField;

	@FXML
	private TextArea bodyField;

	@FXML
	private TextField numEmailsField;

	@FXML
	private TextField delayField;

	@FXML
	private ToggleButton toggleTLSBtn;

	@FXML
	private Button sendEmailBtn;

	@FXML
	private Button cancelEmailBtn;

	@FXML
	private Button clearEmailBtn;

	@FXML
	private TextArea logArea;
	
	@FXML
	private Button clearLogsBtn;
	
	private Stage stage;

	private TemplateManager templateManager;
	
	private ListViewManager<File> attachmentManager;
	
	private TableViewManager<EmailHeader> headerManager;
	
	private EmailScheduler emailScheduler;
	
	private final ExecutorService emailTaskExecutor = Executors.newSingleThreadExecutor();
	
	private Logger logger;
	
	private Future<Void> futureSubmittedTask;

	@FXML
	void addAttachment(ActionEvent event) {
		FileChooser fileChooser = DialogBuilder.getFileChooser("Select one or multiple files");
		List<File> files = fileChooser.showOpenMultipleDialog(getStage());
		if (files != null) {
			attachmentManager.addItems(files);
		}
	}

	@FXML
	void addHeader(ActionEvent event) {
		String headerName = headerNameField.getText().trim();
		String headerValue = headerValueField.getText().trim();
		
		
		if (!headerName.isEmpty() && !headerValue.isEmpty()) {
			headerManager.addItem(new EmailHeader(headerName, headerValue));
		}
		else {
			DialogBuilder.getAlertDialog("Error", "Error creating header", "You must enter a name and value for the header", AlertType.WARNING).show();
		}
	}

	@FXML
	void addTemplate(ActionEvent event) {

		Alert invalidInputAlert = DialogBuilder.getAlertDialog("Informational alert", "Invalid input",
				"The field cannot be empty", AlertType.ERROR);
		TextInputDialog textInputDialog = DialogBuilder.getSingleTextFieldInputDialog("Template Creator",
				"Create a new template", "Template name:", invalidInputAlert);

		Optional<String> templateName = textInputDialog.showAndWait();

		if (templateName.isPresent()) {
			EmailTemplate template = createEmailTemplate(templateName.get());
			templateManager.addItem(template);
		}
	}
	
	private EmailTemplate createEmailTemplate(String name) {
		EmailTemplate template = new EmailTemplate(name);
		
		template.setServer(serverNameField.getText());
		template.setMailFrom(mailFromField.getText());
		template.setFrom(fromField.getText());
		template.setCustomFrom(toggleFromField.isSelected());
		template.setTo(toField.getText());
		template.setCC(ccField.getText());
		template.setBCC(bccField.getText());
		template.setSubject(subjectField.getText());
		template.setBody(bodyField.getText());
		template.setTLSEnabled(toggleTLSBtn.isSelected());
		template.setAttachmentList(attachmentManager.getItems());
		template.setHeaderList(headerManager.getItems());
		template.setNumEmails(Integer.parseInt(numEmailsField.getText()));
		template.setDelay(Integer.parseInt(delayField.getText()));
		
		return template;
	}

	@FXML
	void applyTemplate(ActionEvent event) {
		EmailTemplate template = templateManager.getSelectedItem();

		if (template != null) {
			serverNameField.setText(template.getServer());
			toggleFromField.setSelected(template.isCustomFrom());
			
			if (toggleFromField.isSelected()) {
				fromField.setText(template.getFrom());
			}
			
			mailFromField.setText(template.getMailFrom());
			toField.setText(template.getTo());
			ccField.setText(template.getCC());
			bccField.setText(template.getBCC());
			subjectField.setText(template.getSubject());
			bodyField.setText(template.getBody());
			toggleTLSBtn.setSelected(template.isTLSEnabled());
			attachmentManager.setItems(template.getAttachmentList());
			headerManager.setItems(template.getHeaderList());
			numEmailsField.setText(Integer.toString(template.getNumEmails()));
			delayField.setText(Integer.toString(template.getDelay()));
		}
		else {
			DialogBuilder.getAlertDialog("Error", "Error applying template", "No templates have been selected", AlertType.WARNING).show();
		}
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
	void cancelScheduledEmail(ActionEvent event) {
		ScheduledEmailTask scheduledEmailTask = emailScheduler.getSelectedItem();
		if (scheduledEmailTask != null) {
			emailScheduler.cancelScheduledEmailTask(scheduledEmailTask);
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
		bodyField.clear();
		toggleTLSBtn.setSelected(false);
		attachmentManager.removeAllItems();
		headerManager.removeAllItems();
		numEmailsField.clear();
		delayField.clear();
	}

	@FXML
	void exitApp(ActionEvent event) {
		shutdownExecutors();
		stage.close();
	}

	@FXML
	void modifyHeader(ActionEvent event) {
		EmailHeader selectedHeader = headerManager.getSelectedItem();
		
		if (selectedHeader != null) {
			String headerName = headerNameField.getText().trim();
			String headerValue = headerValueField.getText().trim();
			replaceHeader(selectedHeader, new EmailHeader(headerName, headerValue));
		}
		else {
			DialogBuilder.getAlertDialog("Error", "Error modifying header", "No headers have been selected", AlertType.WARNING).show();
		}
	}

	@FXML
	void openAboutInfo(ActionEvent event) {
		DialogBuilder.getAlertDialog("About", "", "SampleMailerv1.0\n\nDesigned by Joaquin Sampedro", AlertType.INFORMATION).show();
	}

	@FXML
	void openSettingsDialog(ActionEvent event) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/SettingsPane.fxml"));
		Parent parent = fxmlLoader.load();
		
		SettingsPaneController settingsPaneController = fxmlLoader.getController();
		
		Scene scene = new Scene(parent, 800, 350);
		scene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Settings");
        
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
			DialogBuilder.getAlertDialog("Error", "Error removing attachment", "No attachments have been selected", AlertType.WARNING).show();	
		}
	}

	@FXML
	void removeHeader(ActionEvent event) {
		EmailHeader header = headerManager.getSelectedItem();
		
		if (header != null) {
			headerManager.removeItem(header);
		}
		else {
			DialogBuilder.getAlertDialog("Error", "Error removing header", "No headers have been selected", AlertType.WARNING).show();
		}
	}

	@FXML
	void removeTemplate(ActionEvent event) {
		EmailTemplate template = templateManager.getSelectedItem();

		if (template != null) {
			templateManager.removeItem(template);
		}
		else {
			DialogBuilder.getAlertDialog("Error", "Error removing template", "No templates have been selected", AlertType.WARNING).show();
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
		if (!templateManager.isTemplateListSaved().get()) {
			templateManager.saveItems();
		}
	}

	@FXML
	void scheduleEmail(ActionEvent event) {
		try {
			EmailTask emailTask = createEmailTask();
			scheduleEmailTask(emailTask);
		}
		catch (MessagingException e) {
			DialogBuilder.getAlertDialog("Error", "Error creating email", "Error: " + e.getMessage(), AlertType.ERROR).show();
		}
		catch (InvalidDateTimeException e) {
			DialogBuilder.getAlertDialog("Error", "Error scheduling email", "Error: " + e.getMessage(), AlertType.ERROR).show();
		}
	}

	@FXML
	void sendEmail(ActionEvent event) {
		try {
			EmailTask emailTask = createEmailTask();
			executeEmailTask(emailTask);
		}
		catch (MessagingException e) {
			DialogBuilder.getAlertDialog("Error", "Error creating email", "Error: " + e.getMessage(), AlertType.ERROR).show();
		}
	}
	
	@FXML
	void clearLogs(ActionEvent event) {
		logArea.clear();
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
		templateManager.loadItems();
	}
	
	private void initializeManagers() {
		templateManager = new TemplateManager(templateListView);
		attachmentManager = new ListViewManager<File>(attachmentListView);
		headerManager = new TableViewManager<EmailHeader>(headerTableView);
		emailScheduler = new EmailScheduler(schedulerTableView);
	}

	private void initializeControls() {
		setTextFieldsFormatters();
		setControlsBinding();
		setControlsListeners();
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
	}
	
	private void setControlsBinding() {
		saveTemplatesBtn.disableProperty().bind(templateManager.isTemplateListSaved());
		saveTemplatesAndExitBtn.disableProperty().bind(templateManager.isTemplateListSaved());
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
	
	private MimeMessage createMimeMessage() throws MessagingException {
		Properties settings = getSettings();
		String debugURL = settings.getProperty("mail.debugurl");
		
		Session session = Session.getInstance(settings);
		
		try {
			session.setDebugOut(new PrintStream(debugURL));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		MimeMessageBuilder mimeMessageBuilder = new MimeMessageBuilder(session);

		mimeMessageBuilder.setFrom(fromField.getText().trim());
		mimeMessageBuilder.setRecipients(RecipientType.TO, toField.getText().trim());
			
		if (!ccField.getText().trim().isEmpty()) {
			mimeMessageBuilder.setRecipients(RecipientType.CC, ccField.getText().trim());
		}
			
		if (!bccField.getText().trim().isEmpty()) {
			mimeMessageBuilder.setRecipients(RecipientType.BCC, ccField.getText().trim());
		}
			
		if (!headerManager.getItems().isEmpty()) {
			mimeMessageBuilder.setHeaders(headerManager.getItems());
		}
			
		if (!subjectField.getText().isEmpty()) {
			mimeMessageBuilder.setSubject(subjectField.getText());
		}
			
		if (!attachmentManager.getItems().isEmpty()) {
			mimeMessageBuilder.setMultiPartBody(attachmentManager.getItems(), bodyField.getText(), 
			settings.getProperty("mail.content.contenttype"), settings.getProperty("mail.content.charset"));
		}
		else {
			mimeMessageBuilder.setBody(bodyField.getText(), settings.getProperty("mail.content.contenttype"), 
			settings.getProperty("mail.content.charset"));
		}

		
		return mimeMessageBuilder.buildMessage();
	}
	
	private Properties getSettings() {
		Properties settings = SettingsManager.getInstance().getSettings();
		
		settings.setProperty("mail.smtp.host", serverNameField.getText().trim());
		
		if (!toggleFromField.isDisabled()) {
			settings.setProperty("mail.smtp.from", mailFromField.getText().trim());
		}
		
		if (!toggleTLSBtn.isDisable()) {
			settings.setProperty("mail.smtp.starttls.enable", "true");
		}
		
		return settings;
	}
	
	private EmailTask createEmailTask() throws MessagingException {
		MimeMessage mimeMessage = createMimeMessage();
		int numEmails = Integer.parseInt(numEmailsField.getText());
		int delay = Integer.parseInt(numEmailsField.getText());
		EmailTask emailTask = new EmailTask(mimeMessage, numEmails, delay);
		
		return emailTask;
	}
	
	private void executeEmailTask(EmailTask emailTask) {
		emailTask.setLogger(logger);
		futureSubmittedTask = emailTaskExecutor.submit(emailTask);
	}
	
	private void scheduleEmailTask(EmailTask emailTask) throws InvalidDateTimeException {
		emailTask.setLogger(logger);
		ScheduledEmailTask scheduledEmailTask = new ScheduledEmailTask(emailTask, dateTimePicker.getDateTimeValue());
		emailScheduler.scheduleEmailTask(scheduledEmailTask);
	}
	
	private void replaceHeader(EmailHeader currentHeader, EmailHeader newHeader) {
		if (!newHeader.getName().isEmpty() && !newHeader.getName().isEmpty()) {
			headerManager.replaceItem(currentHeader, newHeader);
		}
		else {
			DialogBuilder.getAlertDialog("Error", "Error modifying header", "You must enter a name and value for the header", AlertType.WARNING).show();
		}
	}
	
	private void shutdownExecutors() {
		if (!emailTaskExecutor.isShutdown()) {
			emailTaskExecutor.shutdownNow();
		}
		
		if (!emailScheduler.getScheduledExecutorService().isShutdown()) {
			emailScheduler.getScheduledExecutorService().shutdownNow();
		}
	}
}
