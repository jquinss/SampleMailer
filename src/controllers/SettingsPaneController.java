package controllers;

import java.util.HashSet;
import java.util.Properties;

import enums.Charset;
import enums.ContentType;
import enums.SSLProtocol;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;
import managers.SettingsManager;

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
    private ComboBox<Charset> charEncodingComboBox;

    @FXML
    private ComboBox<ContentType> contentTypeComboBox;

    @FXML
    private CheckBox enableDebugCheckBox;

    @FXML
    private TextField debugLogDirTextField;

    @FXML
    private Button resetSettingsBtn;

    @FXML
    private Button cancelSettingsBtn;

    @FXML
    private Button applySettingsBtn;
    
    private Stage stage;
    
    private Properties settings;
    
    private final ObservableList<Charset> charsetObsList = FXCollections.observableArrayList();
    
    private final ObservableList<ContentType> contentTypeObsList = FXCollections.observableArrayList();

    @FXML
    void applySettings(ActionEvent event) {
    	
    }

    @FXML
    void cancelSettings(ActionEvent event) {
    	stage.close();
    }

    @FXML
    void resetSettings(ActionEvent event) {

    }
    
    public void setStage(Stage stage) {
    	this.stage = stage;
    }
    
    @FXML
    public void initialize() {
    	settings = SettingsManager.getInstance().getSettings();
    	initializeSMTPSettings();
    	initializeEmailSettings();
    	initializeLogSettings();
    }
    
    private void initializeSMTPSettings() {
    	heloHostnameTextField.setText(settings.getProperty("mail.smtp.localhost"));
        smtpPortTextField.setText(settings.getProperty("mail.smtp.port"));
        connTimeoutTextField.setText(settings.getProperty("mail.smtp.connectiontimeout"));
    	initializeTLSCheckBoxes();
    }
    
    private void initializeEmailSettings() {
    	intializeCharsetComboBox();
    	
    	for (Charset charset : Charset.values()) {
    		if (charset.toString().equals(settings.getProperty("mail.content.charset"))) {
    			charEncodingComboBox.getSelectionModel().select(charset);
    		}

    	}
    	initializeContentTypeComboBox();
    	
    	for (ContentType contentType : ContentType.values()) {
    		if (contentType.toString().equals(settings.getProperty("mail.content.contenttype"))) {
    			contentTypeComboBox.getSelectionModel().select(contentType);
    		}

    	}
    }
    
    private void initializeLogSettings() {
    	enableDebugCheckBox.setSelected(Boolean.getBoolean(settings.getProperty("mail.debug")));
    	debugLogDirTextField.setText(settings.getProperty("mail.debugurl"));
    }
    
    private void initializeTLSCheckBoxes() {
    	HashSet<String> enumValues = new HashSet<String>();
    	
    	for (SSLProtocol sslEnumProtocol : SSLProtocol.values()) {
    		enumValues.add(sslEnumProtocol.toString());
    	}
    	
    	String[] sslProtocols = settings.getProperty("mail.smtp.ssl.protocols").split(" ");
    	
    	for (String sslProtocol : sslProtocols) {
    		if (enumValues.contains(sslProtocol)) {
    			switch (sslProtocol) {
    			case "SSLv3":
    				sslv3CheckBox.setSelected(true);
    				break;
    			case "TLSv1":
    				tlsv1CheckBox.setSelected(true);
    				break;
    			case "TLSv1.1":
    				tlsv1_1CheckBox.setSelected(true);
    				break;
    			case "TLSv1.2":
    				tlsv1_2CheckBox.setSelected(true);
    				break;
    			}
    		}
    	}
    }
    
    private void intializeCharsetComboBox() {
    	initializeCharsetComboBoxCell();
    	
    	for (Charset charset : Charset.values()) {
    		charsetObsList.add(charset);
    	}
    	charEncodingComboBox.setItems(charsetObsList);
    	
    }
    
    private void initializeCharsetComboBoxCell() {
    	charEncodingComboBox.setCellFactory(new Callback<ListView<Charset>, ListCell<Charset>>() {
    		 
            @Override
            public ListCell<Charset> call(ListView<Charset> param) {
                final ListCell<Charset> cell = new ListCell<Charset>() {
 
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
                return cell;
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
    	for (ContentType contentType : ContentType.values()) {
    		contentTypeObsList.add(contentType);
    	}
    	
    	contentTypeComboBox.setItems(contentTypeObsList);
    }
}