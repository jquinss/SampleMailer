package com.jquinss.samplemailer.managers;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.jquinss.samplemailer.mail.EmailTemplate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TemplatesManager {
    private final SimpleBooleanProperty dataSaved = new SimpleBooleanProperty(true);
    private final ObservableList<EmailTemplate> emailTemplateObservableList = FXCollections.observableArrayList();

    public void addEmailTemplate(EmailTemplate template) {
        emailTemplateObservableList.add(template);
        dataSaved.set(false);
    }

    public void removeEmailTemplate(EmailTemplate template) {
        emailTemplateObservableList.remove(template);
        dataSaved.set(false);
    }

    public ObservableList<EmailTemplate> getEmailTemplateObservableList() {
        return emailTemplateObservableList;
    }

    public SimpleBooleanProperty IsDataSaved() {
        return dataSaved;
    }

    public void saveEmailTemplates(String fileName) throws IOException {
        Files.createDirectories(Path.of(SettingsManager.getInstance().getDataPath()));
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(fileName))) {
            output.writeObject(emailTemplateObservableList.stream().toList());
            dataSaved.set(true);
        }
    }

    public void loadEmailTemplates(String fileName, boolean append, boolean firstTimeLoad) throws IOException, ClassNotFoundException  {
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(fileName))) {
            List<?> templateList = (List<?>) input.readObject();
            if (!append) {
                emailTemplateObservableList.clear();
            }

            for (Object obj : templateList) {
                emailTemplateObservableList.add((EmailTemplate) obj);
            }
        }

        dataSaved.set(firstTimeLoad);
    }
}
