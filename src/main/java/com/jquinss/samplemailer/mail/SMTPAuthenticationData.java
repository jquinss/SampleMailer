package com.jquinss.samplemailer.mail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SMTPAuthenticationData implements Serializable {
    private List<SMTPAuthenticationProfile> smtpAuthenticationProfileList;
    private List<ServerProfile> serverProfileList;

    public List<ServerProfile> getServerProfileList() {
        return serverProfileList;
    }

    public void setServerProfileList(List<ServerProfile> serverProfileList) {
        this.serverProfileList = serverProfileList;
    }

    public List<SMTPAuthenticationProfile> getSmtpAuthenticationProfileList() {
        return smtpAuthenticationProfileList;
    }

    public void setSmtpAuthenticationProfileList(List<SMTPAuthenticationProfile> smtpAuthenticationProfileList) {
        this.smtpAuthenticationProfileList = smtpAuthenticationProfileList;
    }
}
