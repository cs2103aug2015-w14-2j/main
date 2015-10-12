package parser;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class EditCommand extends AbstractCommand{
	
	private ArrayList<Type> editType;
	private IdentifierType identifierType;
	private int index;
	private String taskName;
	
	private String newName;
	private LocalDateTime newStartTime;
	private LocalDateTime newStartDate;
	private LocalDateTime newEndTime;
	private LocalDateTime newEndDate;
	
	protected static enum Type {
		NAME,
		START_DATE,
		START_TIME,
		END_DATE,
		END_TIME;
	}
	
	private static enum IdentifierType {
		INDEX,
		NAME;
	}
	
	EditCommand(String taskIdentifier) {
		identifierType = IdentifierType.NAME;
		taskName = taskIdentifier;		
	}
	
	EditCommand(int taskIdentifier) {
		identifierType = IdentifierType.INDEX;
		index = taskIdentifier;		
	}
	
	public ArrayList<Type> getEditType() {
		return editType;
	}
	
	public IdentifierType getIdentifierType() {
		return identifierType;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getTaskName() {
		return taskName;
	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public LocalDateTime getNewStartTime() {
		return newStartTime;
	}

	public void setNewStartTime(LocalDateTime newStartTime) {
		this.newStartTime = newStartTime;
	}

	public LocalDateTime getNewStartDate() {
		return newStartDate;
	}

	public void setNewStartDate(LocalDateTime newStartDate) {
		this.newStartDate = newStartDate;
	}

	public LocalDateTime getNewEndTime() {
		return newEndTime;
	}

	public void setNewEndTime(LocalDateTime newEndTime) {
		this.newEndTime = newEndTime;
	}

	public LocalDateTime getNewEndDate() {
		return newEndDate;
	}

	public void setNewEndDate(LocalDateTime newEndDate) {
		this.newEndDate = newEndDate;
	}
	
	public void setEditType(ArrayList<Type> editType) {
		this.editType = editType;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof EditCommand)) {
			return false;
		} else {
			EditCommand that = (EditCommand) obj;
			return this.getEditType().equals(that.getEditType()) &&
						 this.getIdentifierType().equals(that.getIdentifierType()) &&
						 this.getIndex() == that.getIndex() &&
						 this.getNewEndDate().equals(that.getNewEndDate()) &&
						 this.getNewEndTime().equals(that.getNewEndTime()) &&
						 this.getNewName().equals(that.getNewName()) &&
						 this.getNewStartDate().equals(that.getNewStartDate()) &&
						 this.getNewStartTime().equals(that.getNewStartTime()) &&
						 this.getTaskName().equals(that.getTaskName());
		}
	}
}
