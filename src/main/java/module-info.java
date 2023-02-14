module com.jquinss.samplemailer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires jakarta.mail;
    requires org.dnsjava;
    requires org.apache.poi.scratchpad;


    opens com.jquinss.samplemailer.app to javafx.fxml;
    opens com.jquinss.samplemailer.controllers to javafx.fxml;
    opens com.jquinss.samplemailer.mail to javafx.base;

    exports com.jquinss.samplemailer.mail;
    exports com.jquinss.samplemailer.app;
    exports com.jquinss.samplemailer.controllers;
    exports com.jquinss.samplemailer.util;
}