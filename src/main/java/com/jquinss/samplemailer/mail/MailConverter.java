package com.jquinss.samplemailer.mail;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

/**
 * 
 * Helper class used to convert an email in EML format to a MimeMessage object
 *
 */

public class MailConverter {
	private MailConverter() {}
	
	public static MimeMessage emlToMime(File file) throws MessagingException, FileNotFoundException, IOException  {
		MimeMessage msg = null;
		
		try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(file))) {
			
			Session session = Session.getInstance(new Properties());
			
			try {
				msg = new MimeMessage(session, input);
				msg.setFileName(file.getName());
			}
			catch (MessagingException e) {
				throw e;
			}
		}
		catch (FileNotFoundException e) {
			throw e;
		}
		
		catch (IOException e) {
			throw e;
		}
		
		return msg;
	}
}