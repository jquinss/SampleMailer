package com.jquinss.samplemailer.mail;

import java.io.Serializable;

public class EmailHeader implements Serializable {
	private static final long serialVersionUID = 3141544703720291622L;
	
	private String name;
	private String value;
	
	public EmailHeader(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
	}
}
