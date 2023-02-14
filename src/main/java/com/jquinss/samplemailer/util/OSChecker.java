package com.jquinss.samplemailer.util;

/**
 * 
 * Helper class used to determine which OS is being used and the data directory.
 * 
 */

import java.io.File;

public class OSChecker {
	public enum OS {
		WINDOWS, LINUX, MAC, SOLARIS
	}
	
	private OSChecker() { /* make the constructor private so it cannot be instantiated  */ }
	
	private static OS os = null;
	private static String DATA_DIRECTORY = null;
	
	public static OS getOSVersion() {
		if (os == null) {
			String operSys = System.getProperty("os.name").toLowerCase();
			
			if (operSys.contains("wind"))
				os = OS.WINDOWS;
			else if (operSys.contains("nix") || operSys.contains("nux") 
					|| operSys.contains("aix"))
				os = OS.LINUX;
			else if (operSys.contains("mac"))
				os = OS.MAC;
			else if (operSys.contains("sunos"))
				os = OS.SOLARIS;
		}
		
		return os;
	}
	
	public static String getOSDataDirectory() {
		
		if (DATA_DIRECTORY == null) {
			switch (getOSVersion()) {
				case MAC -> DATA_DIRECTORY = System.getProperty("user.home") + File.separator +
						"Library" + File.separator + "Application Support";
				case LINUX, SOLARIS -> DATA_DIRECTORY = System.getProperty("user.home");
				default -> DATA_DIRECTORY = System.getenv("AppData");
			}
		}
		
		return DATA_DIRECTORY;
	}
}