package com.jquinss.samplemailer.enums;

public enum SSLProtocol {
	SSLv3,
	TLSv1,
	TLSv1_1,
	TLSv1_2;
	
	@Override
	public String toString() {
		return switch (this) {
			case SSLv3 -> "SSLv3";
			case TLSv1 -> "TLSv1";
			case TLSv1_1 -> "TLSv1.1";
			case TLSv1_2 -> "TLSv1.2";
			default -> throw new IllegalArgumentException();
		};
	}
}
