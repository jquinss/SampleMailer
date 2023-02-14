package com.jquinss.samplemailer.enums;

public enum ContentType {
	TEXT_PLAIN,
	TEXT_HTML;
	
	@Override
	public String toString() {
		return switch (this) {
			case TEXT_PLAIN -> "text/plain";
			case TEXT_HTML -> "text/html";
			default -> throw new IllegalArgumentException();
		};
	}
}