package com.jquinss.samplemailer.controllers;

import com.jquinss.samplemailer.exceptions.ServerProfileInUseException;
import com.jquinss.samplemailer.mail.SMTPAuthenticationProfile;
import com.jquinss.samplemailer.mail.ServerProfile;
import com.jquinss.samplemailer.managers.SMTPAuthenticationManager;
import com.jquinss.samplemailer.util.DialogBuilder;
import com.jquinss.samplemailer.util.IntRangeStringConverter;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SMTPAuthenticationPaneController implements Initializable {
    @FXML
    private TabPane smtpAuthenticationPaneTabPane;

    @FXML
    private Tab smtpAuthenticationPaneServerProfilesTab;

    @FXML
    private ListView<ServerProfile> serverProfilesListView;

    @FXML
    private TableView<SMTPAuthenticationProfile> authenticationProfilesTableView;

    @FXML
    private TableColumn<SMTPAuthenticationProfile, String> authenticationProfileNameTableColumn;

    @FXML
    private TableColumn<SMTPAuthenticationProfile, String> authenticationProfileStateTableColumn;

    @FXML
    private TextField emailAddressField;

    @FXML
    private ComboBox<ServerProfile> serverProfilesComboBox;

    @FXML
    private CheckBox enableProfileCheckBox;

    @FXML
    private TextField serverProfileNameField;

    @FXML
    private TextField serverNameOrIPField;

    @FXML
    private TextField smtpPortField;

    @FXML
    private CheckBox useTLSCheckBox;

    private Stage serverProfileDialogStage;

    private Stage authenticationProfileDialogStage;

    private boolean isEditServerProfileMode = false;

    private boolean isAddServerProfileMode = false;

    private boolean isEditAuthenticationProfileMode = false;

    private boolean isAddAuthenticationProfileMode = false;

    private ServerProfile editedServerProfile;

    private SMTPAuthenticationProfile editedAuthenticationProfile;

    private final SMTPAuthenticationManager smtpAuthenticationManager;

    public SMTPAuthenticationPaneController(SMTPAuthenticationManager smtpAuthenticationManager) {
        this.smtpAuthenticationManager = smtpAuthenticationManager;
    }

    @FXML
    private void addServerProfile() {
        try {
            isAddServerProfileMode = true;
            openServerProfileDialog();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void removeServerProfile() {
        ServerProfile serverProfile = serverProfilesListView.getSelectionModel().getSelectedItem();
        if (serverProfile != null) {
            try {
                smtpAuthenticationManager.removeServerProfile(serverProfile);
            }
            catch (ServerProfileInUseException e) {
                Alert alert = DialogBuilder.buildAlertDialog("Error", "Error removing server profile",
                        "The selected server profile is already in use",
                        Alert.AlertType.ERROR);
                alert.getDialogPane().getStylesheets().add(getClass().getResource("/com/jquinss/samplemailer/styles/application.css").toString());
                //stage.getIcons().add(new Image(getClass().getResource("/com/jquinss/samplemailer/images/question_mark.png").toString()));
                alert.show();
                smtpAuthenticationPaneTabPane.getSelectionModel().select(smtpAuthenticationPaneServerProfilesTab);
            }
        }
    }

    @FXML
    private void editServerProfile() {
        ServerProfile serverProfile = serverProfilesListView.getSelectionModel().getSelectedItem();
        if (serverProfile != null) {
            try {
                editedServerProfile = serverProfile;
                isEditServerProfileMode = true;
                openServerProfileDialog();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void addAuthenticationProfile() {
        if (smtpAuthenticationManager.getServerProfileObservableList().isEmpty()) {
            Alert alert = DialogBuilder.buildAlertDialog("Information", "", "You must create at least 1 server profile",
                    Alert.AlertType.INFORMATION);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/com/jquinss/samplemailer/styles/application.css").toString());
            //stage.getIcons().add(new Image(getClass().getResource("/com/jquinss/samplemailer/images/question_mark.png").toString()));
            alert.show();
            smtpAuthenticationPaneTabPane.getSelectionModel().select(smtpAuthenticationPaneServerProfilesTab);
        }
        else {
            try {
                isAddAuthenticationProfileMode = true;
                openAuthenticationProfileDialog();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void removeAuthenticationProfile() {
        SMTPAuthenticationProfile smtpAuthenticationProfile = authenticationProfilesTableView.getSelectionModel().getSelectedItem();
        if (smtpAuthenticationProfile != null) {
            smtpAuthenticationManager.removeSMTPAuthenticationProfile(smtpAuthenticationProfile);
        }
    }

    @FXML
    private void editAuthenticationProfile() {
        SMTPAuthenticationProfile authenticationProfile = authenticationProfilesTableView.getSelectionModel().getSelectedItem();
        if (authenticationProfile != null) {
            try {
                editedAuthenticationProfile = authenticationProfile;
                isEditAuthenticationProfileMode = true;
                openAuthenticationProfileDialog();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void saveAuthenticationProfile() {
        String emailAddress = emailAddressField.getText().trim();
        ServerProfile serverProfile = serverProfilesComboBox.getSelectionModel().getSelectedItem();
        boolean enableProfile = enableProfileCheckBox.isSelected();

        boolean validAuthenticationProfile = false;

        if (isEditAuthenticationProfileMode && !emailAddress.isEmpty()) {
            validAuthenticationProfile = true;
            editedAuthenticationProfile.setEmailAddress(emailAddress);
            editedAuthenticationProfile.setServerProfile(serverProfile);
            editedAuthenticationProfile.setEnabled(enableProfile);
            smtpAuthenticationManager.replaceAuthenticationProfile(editedAuthenticationProfile, editedAuthenticationProfile);
            authenticationProfilesTableView.getSelectionModel().select(editedAuthenticationProfile);
            closeAuthenticationProfileDialogStage();
        }
        else if (isAddAuthenticationProfileMode && !emailAddress.isEmpty() &&
                !smtpAuthenticationManager.existsSMTPAuthenticationProfile(emailAddress)) {
            validAuthenticationProfile = true;
            smtpAuthenticationManager.addSMTPAuthenticationProfile(new SMTPAuthenticationProfile(emailAddress, serverProfile, enableProfile));
            closeAuthenticationProfileDialogStage();
        }

        StringBuilder validationMessage = new StringBuilder();

        if (emailAddress.isEmpty()) {
            validationMessage.append("Email address cannot be empty\n");
        }

        if (smtpAuthenticationManager.existsSMTPAuthenticationProfile(emailAddress)) {
            validationMessage.append("The email address already exists in another profile");
        }

        if (!validAuthenticationProfile) {
            Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Invalid authentication profile",
                    validationMessage.toString(), Alert.AlertType.ERROR);
            alertDialog.showAndWait();
        }
    }

    @FXML
    private void cancelAuthenticationProfileDialog() {
        closeAuthenticationProfileDialogStage();
    }

    @FXML
    private void saveServerProfile() {
        String profileName = serverProfileNameField.getText().trim();
        String hostname = serverNameOrIPField.getText().trim();
        String port = smtpPortField.getText().trim();
        boolean isTLSEnabled = useTLSCheckBox.isSelected();

        if (isEditServerProfileMode && isValidServerProfile(profileName, hostname, port)) {
            editedServerProfile.setProfileName(profileName);
            editedServerProfile.setServerHostName(hostname);
            editedServerProfile.setPort(port);
            editedServerProfile.setTLSEnabled(isTLSEnabled);
            smtpAuthenticationManager.replaceServerProfile(editedServerProfile, editedServerProfile);
            serverProfilesListView.getSelectionModel().select(editedServerProfile);
            closeServerProfileDialogStage();
        }
        else {
            if (isValidServerProfile(profileName, hostname, port)) {
                smtpAuthenticationManager.addServerProfile(new ServerProfile(profileName, hostname, port, isTLSEnabled));
                closeServerProfileDialogStage();
            }
            else {
                StringBuilder validationMessage = new StringBuilder();
                validationMessage.append("The following fields are required:\n");

                if (profileName.isEmpty()) {
                    validationMessage.append("- Server profile name\n");
                }

                if (hostname.isEmpty()) {
                    validationMessage.append("- Server name/IP address\n");
                }

                if (port.isEmpty()) {
                    validationMessage.append("- SMTP port");
                }

                Alert alertDialog = DialogBuilder.buildAlertDialog("Error", "Invalid server profile",
                                                            validationMessage.toString(), Alert.AlertType.ERROR);

                alertDialog.showAndWait();
            }
        }
    }

    private boolean isValidServerProfile(String profileName, String hostname, String port) {
        return !(profileName.isEmpty() || hostname.isEmpty() || port.isEmpty());
    }

    @FXML
    private void cancelServerProfileDialog() {
        closeServerProfileDialogStage();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (isEditAuthenticationProfileMode || isAddAuthenticationProfileMode) {
            initializeServerProfilesComboBox();
        }

        if (isEditServerProfileMode || isAddServerProfileMode) {
            initializeServerProfileTextFormatters();
        }

        if (isAddAuthenticationProfileMode) {
            serverProfilesComboBox.getSelectionModel().select(0);
        }

        if (isEditServerProfileMode) {
            loadCurrentlyEditedServerProfileInfo();
        }
        else if (isEditAuthenticationProfileMode) {
            loadCurrentlyEditedAuthenticationProfileInfo();
        }
        else {
            initializeServerProfilesListView();
            initializeAuthenticationProfilesTableView();
        }
    }

    private void closeServerProfileDialogStage() {
        isAddServerProfileMode = false;
        isEditServerProfileMode = false;
        serverProfileDialogStage.close();
    }

    private void closeAuthenticationProfileDialogStage() {
        isEditAuthenticationProfileMode = false;
        isAddAuthenticationProfileMode = false;
        authenticationProfileDialogStage.close();
    }

    private void loadCurrentlyEditedServerProfileInfo() {
        serverProfileNameField.setText(editedServerProfile.getProfileName());
        serverNameOrIPField.setText(editedServerProfile.getServerHostName());
        smtpPortField.setText(editedServerProfile.getPort());
        useTLSCheckBox.setSelected(editedServerProfile.isTLSEnabled());
    }

    private void loadCurrentlyEditedAuthenticationProfileInfo() {
        emailAddressField.setText(editedAuthenticationProfile.getEmailAddress());
        serverProfilesComboBox.getSelectionModel().select(editedAuthenticationProfile.getServerProfile());
        enableProfileCheckBox.setSelected(editedAuthenticationProfile.isEnabled());
    }

    private void openServerProfileDialog() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/jquinss/samplemailer/fxml/ServerProfileDialog.fxml"));

        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();
        Scene scene = new Scene(parent, 460, 228);
        scene.getStylesheets().add(getClass().getResource("/com/jquinss/samplemailer/styles/application.css").toString());
        serverProfileDialogStage = new Stage();
        serverProfileDialogStage.setResizable(false);
        serverProfileDialogStage.setTitle("Server Profiles");
        serverProfileDialogStage.getIcons().add(new Image(getClass().getResource("/com/jquinss/samplemailer/images/logo.png").toString()));
        serverProfileDialogStage.initModality(Modality.APPLICATION_MODAL);
        serverProfileDialogStage.setScene(scene);
        serverProfileDialogStage.setOnCloseRequest(e -> {
            closeServerProfileDialogStage();
        });
        serverProfileDialogStage.showAndWait();
    }

    private void openAuthenticationProfileDialog() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/jquinss/samplemailer/fxml/AuthenticationProfileDialog.fxml"));

        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();
        Scene scene = new Scene(parent, 335, 217);
        scene.getStylesheets().add(getClass().getResource("/com/jquinss/samplemailer/styles/application.css").toString());
        authenticationProfileDialogStage = new Stage();
        authenticationProfileDialogStage.setResizable(false);
        authenticationProfileDialogStage.setTitle("Authentication Profiles");
        authenticationProfileDialogStage.getIcons().add(new Image(getClass().getResource("/com/jquinss/samplemailer/images/logo.png").toString()));
        authenticationProfileDialogStage.initModality(Modality.APPLICATION_MODAL);
        authenticationProfileDialogStage.setScene(scene);
        authenticationProfileDialogStage.setOnCloseRequest(e -> {
            closeAuthenticationProfileDialogStage();
        });
        authenticationProfileDialogStage.showAndWait();
    }

    private void initializeServerProfilesListView() {
        serverProfilesListView.setItems(smtpAuthenticationManager.getServerProfileObservableList());
    }

    private void initializeServerProfilesComboBox() {
        serverProfilesComboBox.setItems(smtpAuthenticationManager.getServerProfileObservableList());
    }

    private void initializeAuthenticationProfilesTableView() {
        initializeAuthenticationProfilesTableColumnCellFactory();
        authenticationProfilesTableView.setItems(smtpAuthenticationManager.getSmtpAuthenticationProfileObservableList());
    }

    private void initializeAuthenticationProfilesTableColumnCellFactory() {
        authenticationProfileNameTableColumn.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().getEmailAddress());
        });

        authenticationProfileStateTableColumn.setCellValueFactory(cellData -> {
            boolean state = cellData.getValue().isEnabled();
            return state ? new SimpleStringProperty("Yes") : new SimpleStringProperty("No");
        });
    }

    private void initializeServerProfileTextFormatters() {
        smtpPortField.setTextFormatter(new TextFormatter<>(new IntRangeStringConverter(0, 65536), 25));
    }
}