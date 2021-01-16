package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

public class SampleMailerController {

    @FXML
    private MenuItem saveTemplatesBtn;

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
    private CheckBox toggleMailFromField;

    @FXML
    private TextField toField;

    @FXML
    private TextField ccField;

    @FXML
    private TextField bccField;

    @FXML
    private ListView<?> attachmentList;

    @FXML
    private Button addAttachmentBtn;

    @FXML
    private Button removeAttachmentBtn;

    @FXML
    private TableView<?> headerTableView;

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
    private ListView<?> templateListView;

    @FXML
    private Button addTemplateBtn;

    @FXML
    private Button applyTemplateBtn;

    @FXML
    private Button removeTemplateBtn;

    @FXML
    private TableView<?> schedulerTableView;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button scheduleEmailBtn;

    @FXML
    private Button cancelScheduledEmailBtn;

    @FXML
    private TextField subjectTextField;

    @FXML
    private TextArea bodyTextField;

    @FXML
    private TextField numberEmailsTextField;

    @FXML
    private TextField delayTextField;

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
    void addAttachment(ActionEvent event) {

    }

    @FXML
    void addHeader(ActionEvent event) {

    }

    @FXML
    void addTemplate(ActionEvent event) {

    }

    @FXML
    void applyTemplate(ActionEvent event) {

    }

    @FXML
    void cancelEmail(ActionEvent event) {

    }

    @FXML
    void cancelScheduledEmail(ActionEvent event) {

    }

    @FXML
    void clearEmail(ActionEvent event) {

    }

    @FXML
    void exitApp(ActionEvent event) {

    }

    @FXML
    void modifyHeader(ActionEvent event) {

    }

    @FXML
    void openAboutInfo(ActionEvent event) {

    }

    @FXML
    void openSettingsDialog(ActionEvent event) {

    }

    @FXML
    void removeAttachment(ActionEvent event) {

    }

    @FXML
    void removeHeader(ActionEvent event) {

    }

    @FXML
    void removeTemplate(ActionEvent event) {

    }

    @FXML
    void saveTemplates(ActionEvent event) {

    }

    @FXML
    void scheduleEmail(ActionEvent event) {

    }

    @FXML
    void sendEmail(ActionEvent event) {

    }

    @FXML
    void toggleMailFrom(ActionEvent event) {

    }

    @FXML
    void toggleTLS(ActionEvent event) {

    }

}
