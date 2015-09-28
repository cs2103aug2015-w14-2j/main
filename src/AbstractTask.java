import java.time.format.DateTimeFormatter;


public abstract class AbstractTask {

	private String taskName;
	
	private Status status;
	
	protected DateTimeFormatter DTFormatter = DateTimeFormatter.ofPattern("dd MM yyyy HH mm");
	
	private static enum Status {
		UNDONE, DONE;
	}
	
	public AbstractTask(String name) {
		taskName = name;
		status = Status.UNDONE;
	}
	
	public void setName(String name) {
		taskName = name;
	}
	
	public String getName() {
		return taskName;
	}
	
	public void setStatus(Status newStatus) {
		status = newStatus;
	}
	
	public Status getStatus() {
		return status;
	}
	
	// Ensures that output is always 2 digit long. eg. 9--> 09
	protected String padWithZero(int number) {
		return String.format("%02d", number);
	}
	
	
}
