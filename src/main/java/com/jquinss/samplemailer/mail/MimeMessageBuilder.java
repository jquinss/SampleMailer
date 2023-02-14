package com.jquinss.samplemailer.mail;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimeUtility;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import static jakarta.mail.Message.RecipientType;

import java.io.File;
import java.util.List;


public class MimeMessageBuilder {
	private final MimeMessage message;
	
	public MimeMessageBuilder(Session session) {
		message = new CustomMimeMessage(session);
	}
	
	public void setFrom(String from) throws MessagingException {
		message.setFrom(new InternetAddress(from));
	}
	
	public void setRecipients(RecipientType type, List<String> recipients) throws MessagingException {
		String rcpts = recipients.toString();
		rcpts = rcpts.substring(1, rcpts.length() - 1);
		message.setRecipients(type, rcpts);
	}
	
	public void setHeaders(List<EmailHeader> headers) throws MessagingException {
		for (EmailHeader header : headers) {
			message.setHeader(header.getName(), MimeUtility.fold(header.getName().length(), header.getValue()));
		}
	}
	
	public void setSubject(String subject) throws MessagingException {
		message.setSubject(subject);
	}
	
	public void setBody(String body, String contentType, String charset) throws MessagingException {
		message.setContent(body, contentType + "; charset= " + charset);
	}
	
	public void setMultiPartBody(List<File> attachments, String body, 
								String contentType, String charset) throws MessagingException {
		
		Multipart multipart = new MimeMultipart();
		
		MimeBodyPart bodyTextPart = new MimeBodyPart();
		bodyTextPart.setContent(body, contentType + "; charset= " + charset);
		multipart.addBodyPart(bodyTextPart);
		
		for (File attachment : attachments) {
			DataSource source = new FileDataSource(attachment);
			MimeBodyPart attachmentPart = new MimeBodyPart();
			attachmentPart.setDataHandler(new DataHandler(source));
			attachmentPart.setFileName(attachment.getName());
			multipart.addBodyPart(attachmentPart);
		}
		
		message.setContent(multipart);
	}
	
	public MimeMessage buildMessage() {
		return message;
	}
}
