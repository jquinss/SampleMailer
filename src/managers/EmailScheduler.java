package managers;

import mail.ScheduledEmailTask;
import util.Observable;
import util.Observer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.scene.control.TableView;

public class EmailScheduler extends TableViewManager<ScheduledEmailTask> implements Observer {
	private final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(3);
	private final String TASK_ID_PREFIX = "TASK";
	private int taskNumber = 1;
	
	public EmailScheduler(TableView<ScheduledEmailTask> tableView) {
		super(tableView);
	}
	
	public void cancelScheduledEmailTask(ScheduledEmailTask scheduledEmailTask) {
		scheduledEmailTask.cancel(true);
	}
	
	public void scheduleEmailTask(ScheduledEmailTask scheduledEmailTask) {
		scheduledEmailTask.setTaskId(generateTaskId());
		scheduledExecutor.schedule(scheduledEmailTask, scheduledEmailTask.getScheduledDateTimeFromNowInSeconds(), TimeUnit.SECONDS);
		scheduledEmailTask.setObserver(this);
		addItem(scheduledEmailTask);
	}
	
	private String generateTaskId() {
		String taskId = TASK_ID_PREFIX + String.format("%07d", taskNumber);
		++taskNumber;
		
		return taskId;
	}
	
	public void update(Observable observable) {
		ScheduledEmailTask scheduledEmailTask = (ScheduledEmailTask) observable;
		removeItem(scheduledEmailTask);
	}
	
	public ScheduledExecutorService getScheduledExecutorService() {
		return scheduledExecutor;
	}
}
