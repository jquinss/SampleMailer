package com.jquinss.samplemailer.mail;

import java.time.LocalDateTime;
import java.util.concurrent.FutureTask;

import com.jquinss.samplemailer.exceptions.InvalidDateTimeException;
import com.jquinss.samplemailer.util.Observable;
import com.jquinss.samplemailer.util.Observer;

import static java.time.temporal.ChronoUnit.SECONDS;

public class ScheduledEmailTask extends FutureTask<Void> implements Observable {
	private final LocalDateTime scheduledDateTime;
	private String taskId;
	private Observer observer;
	
	public ScheduledEmailTask(EmailTask emailTask, LocalDateTime scheduledDateTime) throws InvalidDateTimeException {
		super(emailTask);
		
		LocalDateTime currentDateTime = LocalDateTime.now();
		
		if (scheduledDateTime.isBefore(currentDateTime)) {
			throw new InvalidDateTimeException("Invalid date. Date " + scheduledDateTime + " is before date " + currentDateTime);
		}
		
		this.scheduledDateTime = scheduledDateTime;
	}
	
	public String getTaskId() {
		return taskId;
	}
	
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	public LocalDateTime getScheduledDateTime() {
		return scheduledDateTime;
	}
	
	public long getScheduledDateTimeFromNowInSeconds() {
		return SECONDS.between(LocalDateTime.now(), scheduledDateTime);
	}
	
	public void setObserver(Observer observer) {
		this.observer = observer;
	}
	
	@Override
	protected void done() {
		super.done();
		if (observer != null) {
			notifyObserver();
		}
		
	}
	
	public void notifyObserver() {
		observer.update(this);
	}
}
