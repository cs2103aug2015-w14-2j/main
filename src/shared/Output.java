package shared;
import java.util.ArrayList;
import java.util.Objects;

public class Output {
	////@@author A0133888N
	private String returnMessage;
	private ArrayList<ArrayList<String>> outputArrayList = new ArrayList<ArrayList<String>>();
	private Priority priority; 
	private int indexUpdated;
	public static enum Priority {
		LOW, HIGH ;
	}
	
	// Priority of message should be low by default since most messages are of lower priority
	public Output() {
		this.priority = Priority.LOW;
	}
	
	public void setPriority(Priority priority) {
		this.priority = priority;
	}
	
	public Priority getPriority() {
		return this.priority;
	}
	
	public void setReturnMessage(String message) {
		returnMessage = message;
	}
	
	public String getReturnMessage() {
		return returnMessage;
	}
	
	public ArrayList<ArrayList<String>> getTasks() {
		return outputArrayList;
	}
	
	public void setOutput(ArrayList<ArrayList<String>> tasksArrayList) {
		outputArrayList = tasksArrayList;
	}
	
	public int getIndexUpdated() {
		return indexUpdated;
	}

	public void setIndexUpdated(int indexUpdated) {
		this.indexUpdated = indexUpdated;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Output)) {
			return false;
		} else {
			Output that = (Output) obj;
			return Objects.equals(this.getReturnMessage(), that.getReturnMessage())
					&& Objects.equals(this.getTasks(), that.getTasks())
					&& Objects.equals(this.getPriority(), that.getPriority())
					&& Objects.equals(this.getIndexUpdated(), that.getIndexUpdated());
		}
	}
}
