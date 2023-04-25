package com.jquinss.samplemailer.managers;

import com.jquinss.samplemailer.util.OSChecker;
import com.jquinss.samplemailer.util.ObjectSerializer;

import java.util.Properties;
import java.io.File;
import java.io.IOException;
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
	
	private Properties settings;
	private Properties defaultSettings;
	private ObjectSerializer objectSerializer;
	
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
		return settings;
	}
	
	public Properties getDefaultSettings() {
		return defaultSettings;
	}
	
	public void setSettings(Properties settings) {
		this.settings = settings;
	}
	
	public void loadSettings(Properties defaultSettings) {
		objectSerializer = new ObjectSerializer(SETTINGS_FILE_PATH);
		
		this.defaultSettings = defaultSettings;
		
		if (!objectSerializer.fileExists()) {
			settings = this.defaultSettings;
		}
		else {
			try {
				objectSerializer.openFileForRead();
				settings = (Properties) objectSerializer.readObject();
			}
			catch (ClassNotFoundException | IOException e) {
				settings = defaultSettings;
			}
			finally {
				try {
					objectSerializer.closeInput();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void saveSettings() {
		objectSerializer = new ObjectSerializer(SETTINGS_FILE_PATH);
		
		try {
			Files.createDirectories(Paths.get(DATA_PATH));
			objectSerializer.openFileForWrite();
			objectSerializer.writeObject(settings);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				objectSerializer.closeOutput();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
