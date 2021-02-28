package mail;

import java.util.concurrent.Callable;

import javax.mail.internet.MimeMessage;

import util.Logger;

import javax.mail.MessagingException;
import javax.mail.Transport;

public class EmailTask implements Callable<Void> {
	private final MimeMessage message;
	private final int numEmails;
	private final int delayInSeconds;
	private Logger logger;
	
	public EmailTask(MimeMessage message, int numEmails, int delayInSeconds) {
		this.message = message;
		this.numEmails = numEmails;
		this.delayInSeconds = delayInSeconds;
	}
	
	@Override
	public Void call() {
		int counter = 1;
		int totalEmailsSent = 0;
		
		try {
			for(; counter <= numEmails; ++counter) {
				try {
					logMessage("Sending email " + counter +"/" + numEmails);
					Transport.send(message);
					logMessage("Email has been sent");
					
					totalEmailsSent += 1;
				}
				catch (MessagingException e) {
					logMessage("Error sending the email " + counter + "/" + numEmails + 
							". Error: " + e.getMessage());
				}
				finally {
					Thread.sleep(delayInSeconds * 1000);
				}
			}
		}
		catch (InterruptedException e) {
			logMessage("The email has been cancelled");
		}
		finally {
			logMessage("Emails sent: " + totalEmailsSent + "/" + numEmails);
		}
		return null;
	}
	
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	private void logMessage(String text) {
		if (logger != null) {
			logger.logMessage(text);
		}
	}
}
