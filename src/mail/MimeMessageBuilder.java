package mail;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import static javax.mail.Message.RecipientType;

import java.io.File;
import java.util.List;


public class MimeMessageBuilder {
	private MimeMessage message;
	
	public MimeMessageBuilder(Session session) {
		message = new MimeMessage(session);
	}
	
	public void setFrom(String from) throws AddressException, MessagingException {
		message.setFrom(new InternetAddress(from));
	}
	
	public void setRecipients(RecipientType type, String recipients) throws MessagingException {
		message.setRecipients(type, recipients);
	}
	
	public void setHeaders(List<Header> headers) throws MessagingException {
		for (Header header : headers) {
			message.setHeader(header.getName(), header.getValue());
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
	
	public MimeMessage getMessage() {
		return message;
	}
}
