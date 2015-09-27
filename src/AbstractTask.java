
public abstract class AbstractTask {

	private String taskName;
	
	private Status status;
	
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
	
	
}
