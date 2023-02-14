package com.jquinss.samplemailer.util;

import java.util.regex.Pattern;


public class Validator {
	
	private Validator() { }

	public static boolean validateIntRange(int min, int max, int input) {
		return ((input >= min) && (input <= max));
	}
	
	public static boolean validateIntRange(int min, int max, String input) {
		int intInput;
		
		try {
			intInput = Integer.parseInt(input);
		}
		catch (NumberFormatException e) {
			return false;
		}
		
		return ((intInput >= min) && (intInput <= max));
	}
	
	public static boolean validateDoubleRange(double min, double max, double input) {
		return ((input >= min) && (input <= max));
	}
		
	public static boolean validateDoubleRange(double min, double max, String input) {
		
		double doubleInput = min - 1;
			
		try {
			doubleInput = Double.parseDouble(input);
		}
		catch (NumberFormatException e) {
			return false;
		}
			
		return ((doubleInput >= min) && (doubleInput <= max));
	}
	
	public static boolean validateStringLength(String input, int minLength, int maxLength) {
		return (input.length() >= minLength && input.length() <= maxLength);
	}
	
	public static boolean validateNonEmptyString(String input) {
		return input.length() != 0;
	}
	
	public static boolean validatePattern(String input, String regexPattern) {

		return Pattern.matches(regexPattern, input);
	}
}