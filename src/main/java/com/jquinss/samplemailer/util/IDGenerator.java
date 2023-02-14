package com.jquinss.samplemailer.util;
import java.util.Random;

public class IDGenerator {
	
	private static final int LOWER_ASCII_LETTER_DEC_CODE = 65;
	private static final int UPPER_ASCII_LETTER_DEC_CODE = 90;

	// generates a random ID with letters and numbers 
	public static String generateRandomID(int numLetters, int numDigits) {
		int range = UPPER_ASCII_LETTER_DEC_CODE - LOWER_ASCII_LETTER_DEC_CODE;
		StringBuilder id = new StringBuilder();
		
		Random rand = new Random();
		
		for (int i = 0; i < numLetters; i++) {
			id.append((char) (rand.nextInt(range) + LOWER_ASCII_LETTER_DEC_CODE));
		}
			
		for (int i = 0; i < numDigits; i++) {
			id.insert(rand.nextInt(id.length()), rand.nextInt(10));
		}
			
		return id.toString();
	}
}
