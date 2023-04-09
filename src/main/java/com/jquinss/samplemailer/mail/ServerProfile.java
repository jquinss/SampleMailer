package com.jquinss.samplemailer.mail;

import java.io.Serializable;

public class ServerProfile implements Serializable {
    private String profileName;
    private String serverHostName;
    private String port;
    private boolean TLSEnabled;

    public ServerProfile(String profileName, String serverHostName, String port, boolean TLSEnabled) {
        this.profileName = profileName;
        this.serverHostName = serverHostName;
        this.port = port;
        this.TLSEnabled = TLSEnabled;
    }

    public boolean isTLSEnabled() {
        return TLSEnabled;
    }

    public void setTLSEnabled(boolean TLSEnabled) {
        this.TLSEnabled = TLSEnabled;
    }

    public String getServerHostName() {
        return serverHostName;
    }

    public void setServerHostName(String serverHostName) {
        this.serverHostName = serverHostName;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }
}
