package com.jquinss.samplemailer.mail;

import java.io.Serializable;

public class SMTPAuthenticationProfile implements Serializable {
    private String emailAddress;
    private ServerProfile serverProfile;
    private boolean enabled;

    public SMTPAuthenticationProfile(String emailAddress, ServerProfile serverProfile, boolean enabled) {
        this.emailAddress = emailAddress;
        this.serverProfile = serverProfile;
        this.enabled = enabled;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ServerProfile getServerProfile() {
        return serverProfile;
    }

    public void setServerProfile(ServerProfile serverProfile) {
        this.serverProfile = serverProfile;
    }

}
