package controllers;

import util.OSChecker;
import util.ObjectSerializer;
import java.util.Properties;
import java.io.File;
import java.io.IOException;

public class SettingsManager {
	private static SettingsManager instance;
	private static final String SETTINGS_PATH = OSChecker.getOSDataDirectory() + File.separator + "Settings.dat";
	private Properties settings;
	private ObjectSerializer objectSerializer;
	
	private SettingsManager() {
		objectSerializer = new ObjectSerializer(SETTINGS_PATH);
	}
	
	public static synchronized SettingsManager getInstance() {
		if (instance == null) {
			instance = new SettingsManager();
		}
		
		return instance;
	}
	
	public void loadSettings(Properties defaultSettings) {
		if (!objectSerializer.fileExists()) {
			settings = defaultSettings;
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
		try {
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
	
	public Properties getSettings() {
		return settings;
	}
}
