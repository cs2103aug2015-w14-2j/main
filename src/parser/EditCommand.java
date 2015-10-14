package parser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class EditCommand extends AbstractCommand {

	private ArrayList<editField> editFields;
	private Type type;
	
	private int index;
	private String searchKeyword;

	private String newName;
	private LocalDateTime newStartTime;
	private LocalDateTime newStartDate;
	private LocalDateTime newEndTime;
	private LocalDateTime newEndDate;

	public static enum editField {
		NAME, START_DATE, START_TIME, END_DATE, END_TIME;
	}

	public static enum Type {
		INDEX, SEARCHKEYWORD;
	}

	public EditCommand(int index) {
		this.type = Type.INDEX;
		this.index = index;
	}
	
	public EditCommand(String searchKeyword) {
		this.type = Type.SEARCHKEYWORD;
		this.searchKeyword = searchKeyword;
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

	public LocalDateTime getNewStartTime() {
		return this.newStartTime;
	}

	public void setNewStartTime(LocalDateTime newStartTime) {
		this.newStartTime = newStartTime;
	}

	public LocalDateTime getNewStartDate() {
		return this.newStartDate;
	}

	public void setNewStartDate(LocalDateTime newStartDate) {
		this.newStartDate = newStartDate;
	}

	public LocalDateTime getNewEndTime() {
		return this.newEndTime;
	}

	public void setNewEndTime(LocalDateTime newEndTime) {
		this.newEndTime = newEndTime;
	}

	public LocalDateTime getNewEndDate() {
		return this.newEndDate;
	}

	public void setNewEndDate(LocalDateTime newEndDate) {
		this.newEndDate = newEndDate;
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
