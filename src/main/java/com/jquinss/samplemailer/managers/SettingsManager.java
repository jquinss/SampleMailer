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
	private static final String SETTINGS_DIR_NAME = "SampleMailer";
	private static final String SETTINGS_ROOT_DIR = OSChecker.getOSDataDirectory() + File.separator + SETTINGS_DIR_NAME;
	private static final String SETTINGS_PATH = SETTINGS_ROOT_DIR + File.separator + SETTINGS_FILE_NAME;
	
	private Properties settings;
	private Properties defaultSettings;
	private ObjectSerializer objectSerializer;
	
	private SettingsManager() {
	}
	
	public static synchronized SettingsManager getInstance() {
		if (instance == null) {
			instance = new SettingsManager();
		}
		
		return instance;
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
		objectSerializer = new ObjectSerializer(SETTINGS_PATH);
		
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
		objectSerializer = new ObjectSerializer(SETTINGS_PATH);
		
		try {
			Files.createDirectories(Paths.get(SETTINGS_ROOT_DIR));
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
