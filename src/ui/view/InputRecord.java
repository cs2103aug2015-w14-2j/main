package ui.view;

import java.util.ArrayList;

public class InputRecord {
	
	private String command;
	private ArrayList<String> commandRecord;
	private int commandPointer;
	
	public InputRecord() {
		initialize();
	} 
	
	public void setCommand(String command) {
		this.command = command;
	}
	
	private void setCommand() {
		this.command = commandRecord.get(commandPointer);
	}
	
	public String getCommand() {
		return this.command;
	}
	
	public void addInputRecord(String command) {
		commandRecord.add(command);
	}
	
	public void setPointer() {
		commandPointer = commandRecord.size();
	}
	
	private void initialize() {
		command = "";
		commandRecord = new ArrayList<String>();
		commandPointer = 0;
	}
	
	protected void decreaseCommandPointer() {
		if(commandPointer > 0) {
			commandPointer --;
		} else {
			commandPointer = 0;
		}
		setCommand();
	}
	
	protected boolean increaseCommandPointer() {
		if (commandPointer >= commandRecord.size() - 1) {
			commandPointer  = commandRecord.size();
			return false;
		} else {
			commandPointer ++;
			setCommand();
			return true;
		}
	}
	
	protected String showLastInput() {
		decreaseCommandPointer();
		return getCommand();
	}
	
	protected String showNextInput() {
		boolean hasNext = increaseCommandPointer();
		if (hasNext) {
			return getCommand();
		} else {
			return "";
		}
	}
}
