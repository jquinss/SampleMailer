package com.jquinss.samplemailer.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.jquinss.samplemailer.util.IntRangeStringConverter;
import jakarta.mail.MessagingException;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import org.xbill.DNS.TextParseException;

import javafx.scene.control.DatePicker;
import com.jquinss.samplemailer.exceptions.InvalidDateTimeException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.util.StringConverter;

import com.jquinss.samplemailer.mail.EmailTask;
import com.jquinss.samplemailer.mail.ScheduledEmailTask;
import com.jquinss.samplemailer.util.Logger;
import com.jquinss.samplemailer.util.Observable;
import com.jquinss.samplemailer.util.Observer;

public class SchedulerPaneController implements Observer {
	@FXML
	private TableView<ScheduledEmailTask> schedulerTableView;
	
	@FXML
	private DatePicker datePicker;

	@FXML
	private TextField hoursTextField;

	@FXML
	private TextField minutesTextField;

	@FXML
	private TextField secondsTextField;
	
	private SampleMailerController sampleMailerController;
	
	private final ObservableList<ScheduledEmailTask> scheduledEmailTasks = FXCollections.observableArrayList();
	private final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(3);
	private final String TASK_ID_PREFIX = "TASK";
	private int taskNumber = 1;
	private Logger logger;
	
	@FXML
	private void cancelScheduledEmail(ActionEvent event) {
		ScheduledEmailTask scheduledEmailTask = schedulerTableView.getSelectionModel().getSelectedItem();
		if (scheduledEmailTask != null) {
			scheduledEmailTask.cancel(true);
		}
	}
	
	@FXML
	private void scheduleEmail(ActionEvent event) {
		try {
			EmailTask emailTask = sampleMailerController.createEmailTask();
			emailTask.setLogger(logger);
			scheduleEmailTask(emailTask);
		}
		catch (MessagingException e) {
			sampleMailerController.showAlertDialog("Error", "Error creating email", "Error: " + e.getMessage(), AlertType.ERROR);
		}
		catch (InvalidDateTimeException e) {
			sampleMailerController.showAlertDialog("Error", "Error scheduling email", "Error: " + e.getMessage(), AlertType.ERROR);
		} catch (TextParseException e) {
			e.printStackTrace();
		}
	}
	
	private void scheduleEmailTask(EmailTask emailTask) throws InvalidDateTimeException {
		ScheduledEmailTask scheduledEmailTask = new ScheduledEmailTask(emailTask, getScheduledLocalDateTime());
		scheduledEmailTask.setTaskId(generateTaskId());
		scheduledExecutor.schedule(scheduledEmailTask, scheduledEmailTask.getScheduledDateTimeFromNowInSeconds(), TimeUnit.SECONDS);
		scheduledEmailTask.setObserver(this);
		scheduledEmailTasks.add(scheduledEmailTask);
	}
	
	private String generateTaskId() {
		String taskId = TASK_ID_PREFIX + String.format("%07d", taskNumber);
		++taskNumber;
		
		return taskId;
	}
	
	public void update(Observable observable) {
		ScheduledEmailTask scheduledEmailTask = (ScheduledEmailTask) observable;
		scheduledEmailTasks.remove(scheduledEmailTask);
	}
	
	void setMainController(SampleMailerController sampleMailerController) {
		this.sampleMailerController = sampleMailerController;
	}
	
	void setLogger(Logger logger) {
		this.logger = logger;
	}

	private void initializeTimeTextFields() {
		LocalTime time = LocalTime.now().plusMinutes(5);
		StringConverter<Integer> minSecConverter = new IntRangeStringConverter(0, 59, "%02d");
		secondsTextField.setTextFormatter(new TextFormatter<>(minSecConverter, time.getSecond()));
		minutesTextField.setTextFormatter(new TextFormatter<>(minSecConverter, time.getMinute()));
		hoursTextField.setTextFormatter(new TextFormatter<>(new IntRangeStringConverter(0, 23, "%02d"), time.getHour()));
	}

	private LocalDateTime getScheduledLocalDateTime() {
		LocalDate date = datePicker.getValue();
		LocalTime time = LocalTime.of(Integer.parseInt(hoursTextField.getText()),
												Integer.parseInt(minutesTextField.getText()),
												Integer.parseInt(secondsTextField.getText()), 0);
		return LocalDateTime.of(date, time);
	}
	
	void shutdownExecutors() {
		if (!scheduledExecutor.isShutdown()) {
			scheduledExecutor.shutdownNow();
		}
	}
	
	@FXML
	private void initialize() {
		schedulerTableView.setItems(scheduledEmailTasks);
		initializeTimeTextFields();
	}
}
