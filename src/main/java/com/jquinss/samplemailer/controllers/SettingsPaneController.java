package com.jquinss.samplemailer.controllers;

import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;

import com.jquinss.samplemailer.enums.Charset;
import com.jquinss.samplemailer.enums.ContentType;
import com.jquinss.samplemailer.enums.SSLProtocol;
import com.jquinss.samplemailer.util.AppStyler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;
import com.jquinss.samplemailer.managers.SettingsManager;
import com.jquinss.samplemailer.util.DialogBuilder;
import com.jquinss.samplemailer.util.Validator;

public class SettingsPaneController {

    @FXML
    private TextField heloHostnameTextField;

    @FXML
    private TextField smtpPortTextField;

    @FXML
    private TextField connTimeoutTextField;

    @FXML
    private CheckBox sslv3CheckBox;

    @FXML
    private CheckBox tlsv1CheckBox;

    @FXML
    private CheckBox tlsv1_1CheckBox;

    @FXML
    private CheckBox tlsv1_2CheckBox;

	@FXML
	private CheckBox tlsv1_3CheckBox;

    @FXML
    private ComboBox<Charset> charEncodingComboBox;

    @FXML
    private ComboBox<ContentType> contentTypeComboBox;

    @FXML
    private CheckBox enableDebugCheckBox;

    @FXML
    private TextField debugLogDirTextField;
    
    @FXML
    private ImageView connTimeoutTextFieldTooltip;
    
    private Stage stage;
    
    private final ObservableList<Charset> charsetObsList = FXCollections.observableArrayList();
    
    private final ObservableList<ContentType> contentTypeObsList = FXCollections.observableArrayList();

    @FXML
    void applySettings() {
    	String validationResult = validateInput();
    	
    	if (!validationResult.isEmpty()) {
    		Alert alert = DialogBuilder.buildAlertDialog("Error", "Invalid Input", validationResult, AlertType.ERROR);
    		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();

			AppStyler.setStyles(alert.getDialogPane(), this, SettingsManager.getInstance().getCSS());
			AppStyler.setWindowLogo(stage, this, SettingsManager.getInstance().getSettingsLogoImage());

    		alert.showAndWait();
    	}
    	else {
			try {
				saveSettings();
			} catch (IOException e) {
				e.printStackTrace();
			}
			stage.close();
    	}
    }

    @FXML
    void cancelSettings() {
    	stage.close();
    }

    @FXML
    void resetSettings() {
    	clearTLSSetting();
    	Properties defaultSettings = SettingsManager.getInstance().getDefaultSettings();
    	loadSettings(defaultSettings);
    }
    
    public void setStage(Stage stage) {
    	this.stage = stage;
    }
    
    @FXML
    public void initialize() {
    	Properties settings = SettingsManager.getInstance().getSettings();
    	initializeSettingsTabs();
    	loadSettings(settings);
    	setTooltips();
    }
    
    private void initializeSettingsTabs() {
    	initializeCharsetComboBox();
    	initializeContentTypeComboBox();
    }
    
    private void loadSettings(Properties settings) {
    	loadSMTPSettings(settings);
    	loadEmailSettings(settings);
    	loadLogSettings(settings);
    }
    
    private void loadSMTPSettings(Properties settings) {
    	heloHostnameTextField.setText(settings.getProperty("mail.smtp.localhost"));
    	smtpPortTextField.setText(settings.getProperty("mail.smtp.port"));
    	connTimeoutTextField.setText(settings.getProperty("mail.smtp.connectiontimeout"));
    	loadTLSSettings(settings);
    }
    
    private void loadEmailSettings(Properties settings) {
    	for (Charset charset : Charset.values()) {
    		if (charset.toString().equals(settings.getProperty("mail.content.charset"))) {
    			charEncodingComboBox.getSelectionModel().select(charset);
    		}
    	}
    	
    	for (ContentType contentType : ContentType.values()) {
    		if (contentType.toString().equals(settings.getProperty("mail.content.contenttype"))) {
    			contentTypeComboBox.getSelectionModel().select(contentType);
    		}
    	}
    }
    
    private void loadLogSettings(Properties settings) {
    	enableDebugCheckBox.setSelected(Boolean.parseBoolean(settings.getProperty("mail.debug")));
    	debugLogDirTextField.setText(settings.getProperty("mail.debugurl"));
    }
    
    private void setTooltips() {
		AppStyler.setTooltip(connTimeoutTextFieldTooltip, this, SettingsManager.getInstance().getDialogLogoImage(), """
				Socket connection time-out value in milliseconds.
				The default value is -1. If the time-out value
				is 0 or the default value, then it is interpreted
				as an infinite time-out. Range: -1 to 2147483647""");
	}
    
    private void clearTLSSetting() {
    	sslv3CheckBox.setSelected(false);
		tlsv1CheckBox.setSelected(false);
		tlsv1_1CheckBox.setSelected(false);
		tlsv1_2CheckBox.setSelected(false);
		tlsv1_3CheckBox.setSelected(false);
    }
    
    private void loadTLSSettings(Properties settings) {
    	HashSet<String> enumValues = new HashSet<String>();
    	
    	for (SSLProtocol sslEnumProtocol : SSLProtocol.values()) {
    		enumValues.add(sslEnumProtocol.toString());
    	}
    	
    	String[] sslProtocols = settings.getProperty("mail.smtp.ssl.protocols").split(" ");
    	
    	for (String sslProtocol : sslProtocols) {
    		if (enumValues.contains(sslProtocol)) {
				switch (sslProtocol) {
					case "SSLv3" -> sslv3CheckBox.setSelected(true);
					case "TLSv1" -> tlsv1CheckBox.setSelected(true);
					case "TLSv1.1" -> tlsv1_1CheckBox.setSelected(true);
					case "TLSv1.2" -> tlsv1_2CheckBox.setSelected(true);
					case "TLSv1.3" -> tlsv1_3CheckBox.setSelected(true);
				}
    		}
    	}
    }
    
    private void initializeCharsetComboBox() {
    	initializeCharsetComboBoxCell();
		charsetObsList.addAll(Charset.values());
    	charEncodingComboBox.setItems(charsetObsList);
    }
    
    private void initializeCharsetComboBoxCell() {
    	charEncodingComboBox.setCellFactory(new Callback<ListView<Charset>, ListCell<Charset>>() {
            @Override
            public ListCell<Charset> call(ListView<Charset> param) {
                return new ListCell<Charset>() {
                    @Override
                    protected void updateItem(Charset item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item != null) {
                            setText(item.getDescription());
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });;
        
        charEncodingComboBox.setButtonCell(new ListCell<Charset>() {
            @Override
            protected void updateItem(Charset item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getDescription());
                } else {
                    setText(null);
                }
            }
        });
    }
    
    private void initializeContentTypeComboBox() {
		contentTypeObsList.addAll(ContentType.values());
    	contentTypeComboBox.setItems(contentTypeObsList);
    }
    
    private String validateInput() {
    	StringBuilder validationText = new StringBuilder();
    	
    	if (!Validator.validatePattern(heloHostnameTextField.getText().trim(), "[\\p{ASCII}]{1,255}")) {
    		validationText.append("- Invalid hostname. It must contain from 1 to 255 ASCII characters.\n");
    	}
    	
    	if (!Validator.validateIntRange(0, 65535, smtpPortTextField.getText().trim())) {
    		validationText.append("- Invalid port number. Valid port numbers range from 0 to 65535.\n");
    	}
    	
    	if (!Validator.validateIntRange(-1, 2147483647, connTimeoutTextField.getText().trim())) {
    		validationText.append("- Invalid time out value. Valid time out value range from -1 (no timeout) to 2147483647.\n");
    	}
    	
       if (!sslv3CheckBox.isSelected() && !tlsv1CheckBox.isSelected() 
    		   && !tlsv1_1CheckBox.isSelected() && !tlsv1_2CheckBox.isSelected()
	   			&& !tlsv1_3CheckBox.isSelected()) {
    	   validationText.append("- At least one TLS option must be selected.");
       }
    	
    	return validationText.toString();
    }
    
    private void saveSettings() throws IOException {
    	Properties settings = new Properties();
    	
    	settings.setProperty("mail.smtp.localhost", heloHostnameTextField.getText().trim());
    	settings.setProperty("mail.smtp.port", smtpPortTextField.getText().trim());
    	settings.setProperty("mail.smtp.connectiontimeout", connTimeoutTextField.getText().trim());
    	settings.setProperty("mail.smtp.ssl.protocols", getSelectedTLSProtocols());
    	settings.setProperty("mail.content.charset",charEncodingComboBox.getSelectionModel().getSelectedItem().toString());
    	settings.setProperty("mail.content.contenttype", contentTypeComboBox.getSelectionModel().getSelectedItem().toString());
    	settings.setProperty("mail.debug", Boolean.toString(enableDebugCheckBox.isSelected()));
    	if (enableDebugCheckBox.isSelected()) {
    		System.out.println("Debug is selected");
    		settings.setProperty("mail.debug", "true");
    	}

    	settings.setProperty("mail.debugurl", debugLogDirTextField.getText().trim());
    	SettingsManager.getInstance().setSettings(settings);
    	SettingsManager.getInstance().saveSettings();
    }
    
    private String getSelectedTLSProtocols() {
    	StringBuilder protocols = new StringBuilder();
    	
    	if (sslv3CheckBox.isSelected()) {
    		protocols.append(SSLProtocol.SSLv3.toString()).append(" ");
    	}
    	
    	if (tlsv1CheckBox.isSelected()) {
    		protocols.append(SSLProtocol.TLSv1.toString()).append(" ");
    	}
    	
    	if (tlsv1_1CheckBox.isSelected()) {
    		protocols.append(SSLProtocol.TLSv1_1.toString()).append(" ");
    	}
    	
    	if (tlsv1_2CheckBox.isSelected()) {
    		protocols.append(SSLProtocol.TLSv1_2.toString());
    	}

		if (tlsv1_3CheckBox.isSelected()) {
			protocols.append(SSLProtocol.TLSv1_3.toString());
		}

    	return protocols.toString().trim();
    }
}