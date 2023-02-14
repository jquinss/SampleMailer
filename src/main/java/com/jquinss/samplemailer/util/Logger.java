package com.jquinss.samplemailer.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class Logger {
	private final TextArea logArea;
	private DateTimeFormatter formatter;
	
	public Logger(TextArea logArea) {
		this.logArea = logArea;
		formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss");
	}
	
	public Logger(TextArea logArea, DateTimeFormatter dateTimeFormatter) {
		this(logArea);
		formatter = dateTimeFormatter;
	}
	
	public void logMessage(String text) {
		Platform.runLater(new Runnable() {
             @Override
             public void run() {
                 logArea.appendText(LocalDateTime.now().format(formatter) + ": " + text + "\n");
             }
         });
	}
	
	public void clearLogArea() {
		logArea.clear();
	}
}
