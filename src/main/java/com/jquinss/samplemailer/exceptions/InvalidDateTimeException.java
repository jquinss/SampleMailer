package com.jquinss.samplemailer.exceptions;

public class InvalidDateTimeException extends Exception {
	private static final long serialVersionUID = 906435078556580181L;

	public InvalidDateTimeException(String errorMessage) {
		super(errorMessage);
	}
}
