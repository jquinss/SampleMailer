package mail;

import java.util.concurrent.Callable;

import javax.mail.internet.MimeMessage;

import javax.mail.MessagingException;
import javax.mail.Transport;

public class EmailTask implements Callable<Void> {
	private final MimeMessage message;
	private final int numEmails;
	private final int delayInSeconds;
	
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
					Transport.send(message);
					totalEmailsSent += 1;
				}
				catch (MessagingException e) {
					
				}
				finally {
					Thread.sleep(delayInSeconds * 1000);
				}
			}
		}
		catch (InterruptedException e) {
			
		}
		return null;
	}
}
