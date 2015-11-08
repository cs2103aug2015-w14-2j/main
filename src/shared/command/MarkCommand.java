package shared.command;

import java.util.Objects;

//@@author A0131188H
public class MarkCommand extends AbstractCommand {

	private markField markField;
	private Type type;
	private int index;
	private String searchKeyword;
	private String undoMessage = "\"mark\" action has been undone!";

	public static enum markField {
		MARK, UNMARK;
	}

	public static enum Type {
		INDEX, SEARCHKEYWORD;
	}

	public MarkCommand(int index) {
		this.type = Type.INDEX;
		this.index = index;
	}

	public MarkCommand(String searchKeyword) {
		this.type = Type.SEARCHKEYWORD;
		this.searchKeyword = searchKeyword;
	}

	public void setMarkField(markField markField) {
		this.markField = markField;
	}

	public markField getMarkField() {
		return this.markField;
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

	public String getUndoMessage() {
		return undoMessage;
	}

	public CmdType getCmdType() {
		return CmdType.MARK;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MarkCommand)) {
			return false;
		} else {
			MarkCommand that = (MarkCommand) obj;
			return this.getType().equals(that.getType())
					&& this.getIndex() == that.getIndex()
					&& Objects.equals(this.getSearchKeyword(),
							that.getSearchKeyword())
					&& Objects.equals(this.getMarkField(), that.getMarkField());
		}
	}
}
