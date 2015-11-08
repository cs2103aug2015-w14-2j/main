package shared;

import java.util.ArrayList;
import java.util.Objects;

import shared.task.AbstractTask;
import logic.TaskList;
// @@author A0133888N
/**
 * This class is used to enclose data transfered from logic to UI.
 */
public class Output {
	private String returnMessage;
	private ArrayList<ArrayList<String>> outputArrayList = new ArrayList<ArrayList<String>>();
	private Priority priority;
	private int indexUpdated;

	public static enum Priority {
		LOW, HIGH;
	}

	// Priority of message should be low by default since most messages are of
	// lower priority
	public Output() {
		this.priority = Priority.LOW;
	}
	
	public Output(String returnMessage) {
		this.priority = Priority.LOW;
		this.returnMessage = returnMessage;
	}
	
	public Output(String template, String variableContent) {
		this.priority = Priority.LOW;
		this.returnMessage = trimReturnMessage(template, variableContent);
	}
	
	public Output(TaskList taskList) {
		this.priority = Priority.LOW;
		ArrayList<ArrayList<String>> outputList = numberTaskList(taskList);
		this.setOutput(outputList);
	}
	
	public Output(Exception e) {
		this.setPriority(Priority.HIGH);
		this.setReturnMessage(e.getMessage());
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
	
	private ArrayList<ArrayList<String>> numberTaskList(TaskList taskList) {
		ArrayList<ArrayList<String>> outputList = new ArrayList<ArrayList<String>>();

		for (int i = 0; i < taskList.size(); i++) {
			AbstractTask currentTask = taskList.getTask(i);
			ArrayList<String> taskArray = (currentTask.toArray());
			taskArray.add(0, String.valueOf(i + 1));
			outputList.add(taskArray);
		}
		
		return outputList;
	}
	
	private static String trimReturnMessage(String template, String content) {
		int ellipsisLength = 3;
		String ellipsis = "...";

		int templateLength = String.format(template, "").length();
		int contentLength = content.length();

		if (templateLength + contentLength < Constants.MESSAGE_LENGTH) {
			return String.format(template, content);
		} else {
			int newContentLength = Constants.MESSAGE_LENGTH - templateLength
					- ellipsisLength;
			String newContent = content.substring(0, newContentLength)
					+ ellipsis;
			return String.format(template, newContent);
		}
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
