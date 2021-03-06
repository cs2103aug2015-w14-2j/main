package shared.command;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

//@@author A0124828B
public class DisplayCommand extends AbstractCommand {

	private Type type;
	private ArrayList<String> searchKeyword;
	private LocalDateTime searchDate;
	private Scope scope;
	private String undoMessage = "\"display\" action cannot be undone!";

	public static enum Scope {
		ALL, DONE, UNDONE, DEFAULT, FLOATING;
	}

	public static enum Type {
		SEARCHKEY, SEARCHDATE, SCOPE;
	}

	public DisplayCommand(ArrayList<String> searchKeyword) {
		this.type = Type.SEARCHKEY;
		this.searchKeyword = searchKeyword;
	}

	public DisplayCommand(LocalDateTime searchDate) {
		this.type = Type.SEARCHDATE;
		this.searchDate = searchDate;
	}

	public DisplayCommand(Scope scope) {
		this.type = Type.SCOPE;
		this.scope = scope;
	}

	public Type getType() {
		return this.type;
	}

	public ArrayList<String> getSearchKeyword() {
		return this.searchKeyword;
	}

	public LocalDateTime getSearchDate() {
		return this.searchDate;
	}

	public Scope getScope() {
		return this.scope;
	}

	public String getUndoMessage() {
		return undoMessage;
	}

	public CmdType getCmdType() {
		return CmdType.DISPLAY;
	}

	public void replaceCmd(DisplayCommand newCmd) {
		if (newCmd.getType() == Type.SEARCHKEY) {
			this.type = Type.SEARCHKEY;
			this.searchKeyword = newCmd.getSearchKeyword();
		} else if (newCmd.getType() == Type.SEARCHDATE) {
			this.type = Type.SEARCHDATE;
			this.searchDate = newCmd.getSearchDate();
		} else if (newCmd.getType() == Type.SCOPE) {
			this.type = Type.SCOPE;
			this.scope = newCmd.getScope();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DisplayCommand)) {
			return false;
		} else {
			DisplayCommand that = (DisplayCommand) obj;
			return this.getType().equals(that.getType())
					&& Objects.equals(this.getSearchKeyword(),
							that.getSearchKeyword())
					&& Objects.equals(this.getSearchDate(),
							that.getSearchDate())
					&& Objects.equals(this.getScope(), that.getScope());
		}
	}

}
