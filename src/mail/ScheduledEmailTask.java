package mail;

import java.time.LocalDateTime;
import java.util.concurrent.FutureTask;

import static java.time.temporal.ChronoUnit.SECONDS;

public class ScheduledEmailTask extends FutureTask<Void> {
	private final String taskId;
	private final LocalDateTime scheduledDateTime;
	
	public ScheduledEmailTask(EmailTask emailTask, String taskId, LocalDateTime scheduledDateTime) {
		super(emailTask);
		this.taskId = taskId;
		this.scheduledDateTime = scheduledDateTime;
	}
	
	public String getTaskId() {
		return taskId;
	}
	
	public LocalDateTime getScheduledDatetime() {
		return scheduledDateTime;
	}
	
	public long getScheduledDateTimeFromNowInSeconds() {
		return SECONDS.between(LocalDateTime.now(), scheduledDateTime);
	}
}
