package com.jquinss.samplemailer.managers;

import com.jquinss.samplemailer.exceptions.ServerProfileInUseException;
import com.jquinss.samplemailer.mail.SMTPAuthenticationData;
import com.jquinss.samplemailer.mail.SMTPAuthenticationProfile;
import com.jquinss.samplemailer.mail.ServerProfile;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SMTPAuthenticationManager {
    private final HashMap<String, SMTPAuthenticationProfile> smtpAuthenticationProfileHashMap = new HashMap<String, SMTPAuthenticationProfile>();
    private final ObservableList<SMTPAuthenticationProfile> smtpAuthenticationProfileObservableList = FXCollections.observableArrayList();
    private final ObservableList<ServerProfile> serverProfileObservableList = FXCollections.observableArrayList();

    public SMTPAuthenticationProfile getSMTPAuthenticationProfile(String emailAddress) {
        return smtpAuthenticationProfileHashMap.get(emailAddress);
    }

    public ObservableList<SMTPAuthenticationProfile> getSmtpAuthenticationProfileObservableList() {
        return smtpAuthenticationProfileObservableList;
    }

    public ObservableList<ServerProfile> getServerProfileObservableList() {
        return serverProfileObservableList;
    }

    public void addSMTPAuthenticationProfile(SMTPAuthenticationProfile smtpAuthenticationProfile) {
        smtpAuthenticationProfileObservableList.add(smtpAuthenticationProfile);
        smtpAuthenticationProfileHashMap.put(smtpAuthenticationProfile.getEmailAddress(), smtpAuthenticationProfile);
    }

    public void removeSMTPAuthenticationProfile(SMTPAuthenticationProfile smtpAuthenticationProfile) {
        smtpAuthenticationProfileHashMap.remove(smtpAuthenticationProfile.getEmailAddress());
        smtpAuthenticationProfileObservableList.remove(smtpAuthenticationProfile);
    }

    public void removeServerProfile(ServerProfile serverProfile) throws ServerProfileInUseException {
        if (isUsedServerProfile(serverProfile)) {
            throw new ServerProfileInUseException("Server profile " + serverProfile.getProfileName() + " is already in use");
        }
        serverProfileObservableList.remove(serverProfile);
    }

    public void replaceServerProfile(ServerProfile oldServerProfile, ServerProfile newServerProfile) {
        int oldServerProfileIndex = serverProfileObservableList.indexOf(oldServerProfile);
        serverProfileObservableList.remove(oldServerProfile);
        serverProfileObservableList.add(oldServerProfileIndex, newServerProfile);
    }

    public void replaceAuthenticationProfile(SMTPAuthenticationProfile oldSMTPAuthenticationProfile,
                                             SMTPAuthenticationProfile newSMTPAuthenticationProfile) {
        int oldSMTPAuthenticationProfileIndex = smtpAuthenticationProfileObservableList.indexOf(oldSMTPAuthenticationProfile);
        smtpAuthenticationProfileObservableList.remove(oldSMTPAuthenticationProfile);
        smtpAuthenticationProfileObservableList.add(oldSMTPAuthenticationProfileIndex, newSMTPAuthenticationProfile);
    }

    private boolean isUsedServerProfile(ServerProfile serverProfile) {
        for (SMTPAuthenticationProfile smtpAuthenticationProfile : smtpAuthenticationProfileObservableList) {
            if (smtpAuthenticationProfile.getServerProfile() == serverProfile) {
                return true;
            }
        }
        return false;
    }

    public boolean existsSMTPAuthenticationProfile(String emailAddress) {
        return smtpAuthenticationProfileHashMap.containsKey(emailAddress);
    }

    public void addServerProfile(ServerProfile serverProfile) {
        serverProfileObservableList.add(serverProfile);
    }

    public void loadSMTPAuthenticationData(String fileName) throws IOException, ClassNotFoundException {
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(fileName))) {
            SMTPAuthenticationData smtpAuthenticationData = (SMTPAuthenticationData) input.readObject();
            List<ServerProfile> serverProfileList = smtpAuthenticationData.getServerProfileList();
            List<SMTPAuthenticationProfile> smtpAuthenticationProfileList = smtpAuthenticationData.getSmtpAuthenticationProfileList();

            serverProfileObservableList.setAll(serverProfileList);
            smtpAuthenticationProfileObservableList.setAll(smtpAuthenticationProfileList);
        }
    }

    public void saveSMTPAuthenticationData(String fileName) throws IOException {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(fileName))) {
            SMTPAuthenticationData smtpAuthenticationData = new SMTPAuthenticationData();
            smtpAuthenticationData.setServerProfileList(serverProfileObservableList.stream().toList());
            smtpAuthenticationData.setSmtpAuthenticationProfileList(smtpAuthenticationProfileObservableList.stream().toList());
            output.writeObject(smtpAuthenticationData);
        }
    }

    public void saveSMTPAuthenticationProfiles(String fileName) throws IOException {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(fileName))) {
            output.writeObject(new ArrayList<SMTPAuthenticationProfile>(smtpAuthenticationProfileObservableList));
        }
    }

    public void saveServerProfiles(String fileName) throws IOException {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(fileName))) {
            output.writeObject(new ArrayList<ServerProfile>(serverProfileObservableList));
        }
    }
}
