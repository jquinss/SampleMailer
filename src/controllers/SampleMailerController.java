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
import mail.EmailTask;
import mail.EmailTemplate;
import mail.MimeMessageBuilder;
import mail.ScheduledEmailTask;
import util.DialogBuilder;
import util.OSChecker;
import control.DateTimePicker;

import java.util.function.UnaryOperator;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.mail.Header;
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
	private TableView<Header> headerTableView;

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
	
	private Stage stage;

	private TemplateManager templateManager;
	
	private ListViewManager<File> attachmentManager;
	
	private TableViewManager<Header> headerManager;
	
	private EmailScheduler emailScheduler;
	
	private final ExecutorService emailTaskExecutor = Executors.newSingleThreadExecutor();
	
	private Future<Void> futureSubmittedTask;

	@FXML
	void addAttachment(ActionEvent event) {
		FileChooser fileChooser = DialogBuilder.getFileChooser("Select one or multiple files");
		List<File> files = fileChooser.showOpenMultipleDialog(getStage());
		System.out.println(files);
		if (files != null) {
			attachmentManager.addItems(files);
		}
	}

	@FXML
	void addHeader(ActionEvent event) {
		String headerName = headerNameField.getText().trim();
		String headerValue = headerValueField.getText().trim();
		
		
		if (!headerName.isEmpty() && !headerValue.isEmpty()) {
			headerManager.addItem(new Header(headerName, headerValue));
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
			EmailTemplate template = new EmailTemplate(templateName.get());
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
			
			templateManager.addItem(template);
		}
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
		Header header = headerManager.getSelectedItem();
		
		if (header != null) {
			String headerName = headerNameField.getText().trim();
			String headerValue = headerValueField.getText().trim();
			
			if (!headerName.isEmpty() && !headerValue.isEmpty()) {
				headerManager.replaceItem(header, new Header(headerName, headerValue));
			}
		}
	}

	@FXML
	void openAboutInfo(ActionEvent event) {

	}

	@FXML
	void openSettingsDialog(ActionEvent event) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("..\\view\\SettingsPane.fxml"));
		Parent parent = fxmlLoader.load();
		
		SettingsPaneController settingsPaneController = fxmlLoader.getController();
		
		Scene scene = new Scene(parent, 800, 350);
        Stage stage = new Stage();
        stage.setResizable(false);
        
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
	}

	@FXML
	void removeHeader(ActionEvent event) {
		Header header = headerManager.getSelectedItem();
		
		if (header != null) {
			headerManager.removeItem(header);
		}
	}

	@FXML
	void removeTemplate(ActionEvent event) {
		EmailTemplate template = templateManager.getSelectedItem();

		if (template != null) {
			templateManager.removeItem(template);
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
		if (templateManager.isTemplateListModified()) {
			templateManager.saveItems();
		}
	}

	@FXML
	void scheduleEmail(ActionEvent event) {
		if (dateTimePicker.getDateTimeValue().isAfter(LocalDateTime.now())) {
			EmailTask emailTask = createEmailTask();
			ScheduledEmailTask scheduledEmailTask = new ScheduledEmailTask(emailTask, dateTimePicker.getDateTimeValue());
			emailScheduler.scheduleEmailTask(scheduledEmailTask);
		}	
	}

	@FXML
	void sendEmail(ActionEvent event) {
		EmailTask emailTask = createEmailTask();
		futureSubmittedTask = emailTaskExecutor.submit(emailTask);
	}

	@FXML
	public void initialize() {
		initializeFields();
		initializeManagers();
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
		defaultSettings.setProperty("mail.debugurl", OSChecker.getOSDataDirectory() + File.separator + "Debug.log");

		return defaultSettings;
	}

	private void saveSettings() {
		SettingsManager.getInstance().saveSettings();
	}

	private void loadTemplates() {
		templateManager.loadItems();
	}
	
	private void initializeManagers() {
		templateManager = new TemplateManager(templateListView);
		attachmentManager = new ListViewManager<File>(attachmentListView);
		headerManager = new TableViewManager<Header>(headerTableView);
		emailScheduler = new EmailScheduler(schedulerTableView);
	}

	private void initializeFields() {
		setTextFormatters();
		setListeners();
	}
	
	private void setTextFormatters() {
		UnaryOperator<TextFormatter.Change> numEmailsFilter = createInputFilter("[1-9][0-9]{0,2}", "1");
		UnaryOperator<TextFormatter.Change> delayFilter = createInputFilter("[0-9]|[1-9][0-9]{0,4}", "0");

		numEmailsField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 1, numEmailsFilter));
		delayField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, delayFilter));
	}
	
	private void setListeners() {
		fromField.textProperty().bind(mailFromField.textProperty());
		
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
	
	private MimeMessage createMimeMessage() {
		Properties settings = SettingsManager.getInstance().getSettings();
		
		settings.setProperty("mail.smtp.host", serverNameField.getText().trim());
		
		if (!toggleFromField.isDisabled()) {
			settings.setProperty("mail.smtp.from", mailFromField.getText().trim());
		}
		
		if (!toggleTLSBtn.isDisable()) {
			settings.setProperty("mail.smtp.starttls.enable", "true");
		}
		
		Session session = Session.getInstance(settings);
		
		MimeMessageBuilder mimeMessageBuilder = new MimeMessageBuilder(session);
		MimeMessage mimeMessage = null;
		
		try {
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
			
			mimeMessage = mimeMessageBuilder.getMessage();
		}
		catch (MessagingException e) {
			e.printStackTrace();
		}
		
		return mimeMessage;
	}
	
	private EmailTask createEmailTask() {
		MimeMessage mimeMessage = createMimeMessage();
		int numEmails = Integer.parseInt(numEmailsField.getText());
		int delay = Integer.parseInt(numEmailsField.getText());
		
		EmailTask emailTask = new EmailTask(mimeMessage, numEmails, delay);
		
		return emailTask;
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
