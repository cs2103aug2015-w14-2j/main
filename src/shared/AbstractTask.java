package shared;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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
	
	public String getName() {
		return taskName;
	}
	
	public String getStatus() {
		return status.toString();
	}
	
	public void setName(String name) {
		taskName = name;
	}
	
	public void setStatus(Status newStatus) {
		status = newStatus;
	}

	// Ensures that output is always 2 digit long. eg. 9--> 09
	protected String padWithZero(int number) {
		return String.format("%02d", number);
	}	
	
	public abstract ArrayList<String> toArray();
	
}