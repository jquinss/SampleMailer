package mail;

import java.time.LocalDateTime;
import java.util.concurrent.FutureTask;

import util.Observable;
import util.Observer;

import static java.time.temporal.ChronoUnit.SECONDS;

public class ScheduledEmailTask extends FutureTask<Void> implements Observable {
	private final LocalDateTime scheduledDateTime;
	private String taskId;
	private Observer observer;
	
	public ScheduledEmailTask(EmailTask emailTask, LocalDateTime scheduledDateTime) {
		super(emailTask);
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
