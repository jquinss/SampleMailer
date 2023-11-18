package com.jquinss.samplemailer.mail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.Message.RecipientType;

import com.jquinss.samplemailer.util.Logger;
import com.jquinss.samplemailer.util.IDGenerator;

import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import jakarta.mail.Address;
import jakarta.mail.MessageContext;

public class EmailTask implements Callable<Void> {
	private final List<MimeMessage> messages;
	private final int numEmails;
	private final int delayInSeconds;
	private Logger logger;
	
	public EmailTask(List<MimeMessage> messages, int numEmails, int delayInSeconds) {
		this.messages = messages;
		this.numEmails = numEmails;
		this.delayInSeconds = delayInSeconds;
	}
	
	@Override
	public Void call() {
		String taskID = "";
		int totalEmailsSent = 0;
		
		try {
			for (MimeMessage msg : messages) {
				msg.saveChanges();
				taskID = IDGenerator.generateRandomID(4, 4);
				String server = getSessionProperty(msg, "mail.smtp.host");
				
				logMessage(taskID + ": Sending email with Message-ID=" + msg.getMessageID());
				logMessage(taskID + ": From=" + getSessionProperty(msg, "mail.smtp.from"));
				logRecipients(taskID, msg.getRecipients(RecipientType.TO), RecipientType.TO);
				logRecipients(taskID, msg.getRecipients(RecipientType.CC), RecipientType.CC);
				logRecipients(taskID, msg.getRecipients(RecipientType.BCC), RecipientType.BCC);
				
				for(int counter = 1; counter <= numEmails; ++counter) {
					try {
						logMessage(taskID + ": Sending email " + counter +"/" + numEmails);
						Transport.send(msg);
						logMessage(taskID + ": Email has been sent to server " + server);
					
						totalEmailsSent += 1;
					}
					catch (MessagingException e) {
						logMessage(taskID + ": Error sending the email " + counter + "/" + numEmails + 
							". Error: " + e.getMessage());
					}
					finally {
						if (counter < numEmails) {
							Thread.sleep(delayInSeconds * 1000L);
						}
					}
				}
			}
		}
		catch (InterruptedException e) {
			logMessage(taskID + ": The email has been cancelled");
		}
		catch (MessagingException e) {
			
		}
		finally {
			logMessage(taskID + ": Total emails sent: " + totalEmailsSent + "/" + numEmails);
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
	
	private String getSessionProperty(MimeMessage msg, String property) {
		MessageContext msgContext = new MessageContext(msg);
		return msgContext.getSession().getProperty(property);
	}
	
	private void logRecipients(String taskID, Address[] rcpts, RecipientType to) {
		if (rcpts != null) {
			StringBuilder logText = new StringBuilder();
			List<String> recipients = new ArrayList<String>();
			for (Address address : rcpts) {
				recipients.add(address.toString());
			}
			logText.append(to.toString());
			logText.append("=");
			logText.append(String.join(", ", recipients));
			logMessage(taskID + ": " + logText.toString());
		}
	}
}
