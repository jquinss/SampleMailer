package com.jquinss.samplemailer.mail;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * 
 * This class represents an email template. It saves email field information so the user can re-use it afterward.
 * 
 */

public class EmailTemplate implements Serializable {
	private static final long serialVersionUID = -45732833667801312L;
	private String name, server, mailFrom, from, subject, body, to, cc, bcc, headersFile;
	private List<File> attachments;
	private List<EmailHeader> headers;
	private List<String> excludedHeaderNames;
	private boolean customFrom, customServer, tlsEnabled, htmlText, addHeadersFromFile;
	
	private int numEmails = 1;
	private int delay = 0;
	
	public EmailTemplate(String name) {
		this.name = name;
	}
	
	// setters
	public void setName(String name) { this.name = name; }
	
	public void setServer(String server) { this.server = server; }
	
	public void setMailFrom(String mailFrom) { this.mailFrom = mailFrom; }
	
	public void setFrom(String from) { this.from = from; }
	
	public void setCustomFrom(boolean customFrom) { this.customFrom = customFrom; }
	
	public void setCustomServer(boolean customServer) { this.customServer = customServer; }
	
	public void setTLSEnabled(boolean tlsEnabled) { this.tlsEnabled = tlsEnabled; }
	
	public void setHTMLText(boolean htmlText) { this.htmlText = htmlText; }
	
	public void setAddHeadersFromFile(boolean addHeadersFromFile) { this.addHeadersFromFile = addHeadersFromFile; }
	
	public void setHeadersFile(String headersFile) { this.headersFile = headersFile; }
	
	public void setSubject(String subject) { this.subject = subject; }
	
	public void setBody(String body) { this.body = body; }
	
	public void setTo(String to) { this.to = to; }
	
	public void setCC(String cc) { this.cc = cc; }
	
	public void setBCC(String bcc) { this.bcc = bcc; }
	
	public void setAttachments(List<File> attachments) { this.attachments = attachments; }
	
	public void setHeaders(List<EmailHeader> headers) { this.headers = headers; }
	
	public void setExcludedHeaderNames(List<String> excludedHeaderNames) { this.excludedHeaderNames = excludedHeaderNames; }
	
	public void setNumEmails(int numEmails) { this.numEmails = numEmails; }
	
	public void setDelay(int delay) { this.delay = delay; }
	
	// getters
	public String getName() { return name; }
	
	public String getServer() { return server; }
	
	public String getMailFrom() { return mailFrom; }
	
	public String getFrom() { return from; }
	
	public boolean isCustomFrom() { return customFrom; }
	
	public boolean isCustomServer() { return customServer; }
	
	public boolean isTLSEnabled() { return tlsEnabled; }
	
	public boolean isHTMLText() { return htmlText; }
	
	public boolean isAddHeadersFromFile() { return addHeadersFromFile; }
	
	public String getHeadersFile() { return headersFile; }
	
	public String getSubject() { return subject; }
	
	public String getBody() { return body; }
	
	public String getTo() { return to; }
	
	public String getCC() { return cc; }
	
	public String getBCC() { return bcc; }
	
	public List<File> getAttachments() { return attachments; }
	
	public List<EmailHeader> getHeaders() { return headers; }
	
	public List<String> getExcludedHeaderNames() { return excludedHeaderNames; }
	
	public int getNumEmails() { return numEmails; }
	
	public int getDelay() { return delay; }
	
	@Override
	public String toString() {
		return name;
	}
}