package com.jquinss.samplemailer.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

public class CustomMimeMessage extends MimeMessage {

	public CustomMimeMessage(Session session) {
		super(session);
	}
	
	// generate a message-ID only if one has not been assigned
	@Override
	protected void updateMessageID() throws MessagingException {
		if (this.getHeader("Message-ID") == null) {
			super.updateMessageID();
		}
	}
}
