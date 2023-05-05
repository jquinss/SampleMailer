package com.jquinss.samplemailer.managers;

import com.jquinss.samplemailer.util.OSChecker;

import java.io.*;
import java.util.Properties;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SettingsManager {
	private static SettingsManager instance;
	private static final String SETTINGS_FILE_NAME = "Settings.dat";
	private static final String TEMPLATES_FILE_NAME = "Templates.dat";

	private static final String SMTP_AUTH_DATA_FILE_NAME = "smtp_auth_data.dat";
	private static final String APP_DIR_NAME = "SampleMailer";
	private static final String DATA_DIR_NAME = "data";
	private static final String DATA_PATH = OSChecker.getOSDataDirectory() + File.separator +
												APP_DIR_NAME + File.separator + DATA_DIR_NAME;;
	private static final String SETTINGS_FILE_PATH = DATA_PATH + File.separator + SETTINGS_FILE_NAME;
	private static final String TEMPLATES_FILE_PATH = DATA_PATH + File.separator + TEMPLATES_FILE_NAME;
	private static final String SMTP_AUTH_DATA_FILE_PATH = DATA_PATH + File.separator + SMTP_AUTH_DATA_FILE_NAME;
	
	private Properties customSettings;
	private Properties defaultSettings;
	
	private SettingsManager() { }
	
	public static synchronized SettingsManager getInstance() {
		if (instance == null) {
			instance = new SettingsManager();
		}
		
		return instance;
	}

	public String getTemplatesFilePath() {
		return TEMPLATES_FILE_PATH;
	}

	public String getSMTPAuthDataFilePath() { return SMTP_AUTH_DATA_FILE_PATH; }

	public String getDataPath() {
		return DATA_PATH;
	}
	
	public Properties getSettings() {
		return customSettings;
	}
	
	public Properties getDefaultSettings() {
		return defaultSettings;
	}
	
	public void setSettings(Properties settings) {
		this.customSettings = settings;
	}
	
	public void loadSettings(Properties defaultSettings) throws IOException, ClassNotFoundException {
		this.defaultSettings = defaultSettings;
		
		File settingsFilePath = new File(SETTINGS_FILE_PATH);
		try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(settingsFilePath))) {
			customSettings = (Properties) input.readObject();
		}
		catch (FileNotFoundException e) {
			// if the settings file is not found (first time application is started), set custom settings
			// to be equal to the default settings
			customSettings = this.defaultSettings;
		}
		catch (IOException | ClassNotFoundException e) {
			// if any other exception, set custom settings to be equal to the default settings and rethrow the
			// exception
			customSettings = this.defaultSettings;
			throw e;
		}
	}
	
	public void saveSettings() throws IOException {
		File settingsFilePath = new File(SETTINGS_FILE_PATH);

		try(ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(settingsFilePath))) {
			Files.createDirectories(Paths.get(DATA_PATH)); // creates the data directory if it does not exist
			output.writeObject(customSettings);
		}
	}
}
