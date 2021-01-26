package mail;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import javax.mail.Header;

/**
 * 
 * This class represents an email template. It saves email field information so the user can re-use it afterward.
 * 
 */

public class EmailTemplate implements Serializable {
	private static final long serialVersionUID = -45732833667801312L;
	private String name, server, mailFrom, from, subject, body, to, cc, bcc;
	private List<File> attachmentList;
	private List<Header> headerList;
	private boolean customFrom, tlsEnabled;
	
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
	
	public void setTLSEnabled(boolean tlsEnabled) { this.tlsEnabled = tlsEnabled; }
	
	public void setSubject(String subject) { this.subject = subject; }
	
	public void setBody(String body) { this.body = body; }
	
	public void setTo(String to) { this.to = to; }
	
	public void setCC(String cc) { this.cc = cc; }
	
	public void setBCC(String bcc) { this.bcc = bcc; }
	
	public void setAttachmentList(List<File> attachmentList) { this.attachmentList = attachmentList; }
	
	public void setHeaderList(List<Header> headerList) { this.headerList = headerList; }
	
	public void setNumEmails(int numEmails) { this.numEmails = numEmails; }
	
	public void setDelay(int delay) { this.delay = delay; }
	
	// getters
	public String getName() { return name; }
	
	public String getServer() { return server; }
	
	public String getMailFrom() { return mailFrom; }
	
	public String getFrom() { return from; }
	
	public boolean isCustomFrom() { return customFrom; }
	
	public boolean isTLSEnabled() { return tlsEnabled; }
	
	public String getSubject() { return subject; }
	
	public String getBody() { return body; }
	
	public String getTo() { return to; }
	
	public String getCC() { return cc; }
	
	public String getBCC() { return bcc; }
	
	public List<File> getAttachmentList() { return attachmentList; }
	
	public List<Header> getHeaderList() { return headerList; }
	
	public int getNumEmails() { return numEmails; }
	
	public int getDelay() { return delay; }
	
	@Override
	public String toString() {
		return name;
	}
}