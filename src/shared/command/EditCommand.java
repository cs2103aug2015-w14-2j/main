package shared.command;

import java.util.ArrayList;
import java.util.Objects;

//@@author A0124828B
public class EditCommand extends AbstractCommand {

	private ArrayList<editField> editFields;
	private Type type;
	private Nature nature;
	
	private int index;
	private String searchKeyword;

	private String newName;
	private String newStartTime;
	private String newStartDate;
	private String newEndTime;
	private String newEndDate;
	private String undoMessage = "\"edit\" action has been undone!";

	public static enum editField {
		NAME, START_DATE, START_TIME, END_DATE, END_TIME;
	}

	public static enum Type {
		INDEX, SEARCHKEYWORD;
	}
	
	public static enum Nature {
		SIMPLE, COMPLEX;
	}
	
	public EditCommand(Nature nature) {
		this.nature = nature;
	}
	
	public EditCommand(int index) {
		this.type = Type.INDEX;
		this.index = index;
		this.nature = Nature.SIMPLE;
	}
	
	public EditCommand(String searchKeyword) {
		this.type = Type.SEARCHKEYWORD;
		this.searchKeyword = searchKeyword;
		this.nature = Nature.SIMPLE;
	}

	public ArrayList<editField> getEditFields() {
		return this.editFields;
	}

	public void setEditFields(ArrayList<editField> editFields) {
		this.editFields = editFields;
	}

	public Type getType() {
		return this.type;
	}

	public int getIndex() {
		return this.index;
	}

	public String getSearchKeyword() {
		return this.searchKeyword;
	}

	public String getNewName() {
		return this.newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public String getNewStartTime() {
		return this.newStartTime;
	}

	public void setNewStartTime(String newStartTime) {
		this.newStartTime = newStartTime;
	}

	public String getNewStartDate() {
		return this.newStartDate;
	}

	public void setNewStartDate(String newStartDate) {
		this.newStartDate = newStartDate;
	}

	public String getNewEndTime() {
		return this.newEndTime;
	}

	public void setNewEndTime(String newEndTime) {
		this.newEndTime = newEndTime;
	}

	public String getNewEndDate() {
		return this.newEndDate;
	}

	public void setNewEndDate(String newEndDate) {
		this.newEndDate = newEndDate;
	}
	
	public Nature getNature() {
		return this.nature;
	}
	
	public void setNature(Nature nature) {
		this.nature = nature;
	}
	
	public void replaceCmd(EditCommand newCmd) {
		this.nature = newCmd.getNature();
		this.editFields = newCmd.getEditFields();
		if (newCmd.getType() == Type.INDEX) {
			this.type = Type.INDEX;
		} else if (newCmd.getType() == Type.SEARCHKEYWORD) {
			this.type = Type.SEARCHKEYWORD;
		}
		
		if (editFields.contains(editField.NAME)) {
			this.newName = newCmd.getNewName();
		} else if (editFields.contains(editField.START_DATE)) {
			this.newStartDate = newCmd.getNewStartDate();
		} else if (editFields.contains(editField.START_TIME)) {
			this.newStartTime = newCmd.getNewStartTime(); 
		} else if (editFields.contains(editField.END_DATE)) {
			this.newEndDate = newCmd.getNewEndDate();
		} else if (editFields.contains(editField.END_TIME)) {
			this.newEndTime = newCmd.getNewEndTime();
		}
}
	
	public String getUndoMessage() {
		return undoMessage;
	}
	
	public CmdType getCmdType() {
		return CmdType.EDIT;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof EditCommand)) {
			return false;
		} else {
			EditCommand that = (EditCommand) obj;
			return this.getEditFields().equals(that.getEditFields())
					&& this.getType().equals(that.getType())
					&& this.getIndex() == that.getIndex()
					&& Objects.equals(this.getNewEndDate(), that.getNewEndDate())
					&& Objects.equals(this.getNewEndTime(), that.getNewEndTime())
					&& Objects.equals(this.getNewName(), that.getNewName())
					&& Objects.equals(this.getNewStartDate(), that.getNewStartDate())
					&& Objects.equals(this.getNewStartTime(), that.getNewStartTime())
					&& Objects.equals(this.getSearchKeyword(), that.getSearchKeyword());
		}
	}
}
