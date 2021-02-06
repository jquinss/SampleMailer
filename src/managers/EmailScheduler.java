package managers;

import mail.EmailTask;
import mail.ScheduledEmailTask;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.scene.control.TableView;

public class EmailScheduler extends TableViewManager<ScheduledEmailTask> {
	private final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(3);
	private final String TASK_ID_PREFIX = "TASK";
	private int taskNumber = 1;
	
	public EmailScheduler(TableView<ScheduledEmailTask> tableView) {
		super(tableView);
	}
	
	public void cancelScheduledEmailTask(ScheduledEmailTask scheduledEmailTask) {
		if (scheduledEmailTask.cancel(true) || scheduledEmailTask.isDone()) {
			removeItem(scheduledEmailTask);
		}
	}
	
	public void scheduleEmailTask(EmailTask emailTask, LocalDateTime scheduledDateTime) {
		ScheduledEmailTask scheduledEmailTask = new ScheduledEmailTask(emailTask, generateTaskId(), scheduledDateTime);
		scheduledExecutor.schedule(scheduledEmailTask, scheduledEmailTask.getScheduledDateTimeFromNowInSeconds(), TimeUnit.SECONDS);
		addItem(scheduledEmailTask);
	}
	
	private String generateTaskId() {
		String taskId = TASK_ID_PREFIX + String.format("%07d", taskNumber);
		++taskNumber;
		
		return taskId;
	}
}
