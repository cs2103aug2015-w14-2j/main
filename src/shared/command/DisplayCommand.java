package shared.command;

import java.time.LocalDateTime;
import java.util.Objects;

public class DisplayCommand extends AbstractCommand{
	
	private Type type;
	private String searchKeyword;
	private LocalDateTime searchDate;
	private Scope scope;
	private String undoMessage = "\"display\" action cannot be undone!";
	
	public static enum Scope {
		ALL, DONE, UNDONE, DEFAULT, FLOATING, OVERDUE;
	}
	
	public static enum Type {
		SEARCHKEY, SEARCHDATE, SCOPE;
	}
	
	public DisplayCommand(String searchKeyword) {
		this.type = Type.SEARCHKEY;
		this.searchKeyword = searchKeyword;
	}

	public DisplayCommand(LocalDateTime searchDate, Type type) {
		this.type = type;
		this.searchDate = searchDate;
	}
	
	public DisplayCommand(Scope scope) {
		this.type = Type.SCOPE;
		this.scope = scope;
	}
	
	public Type getType() {
		return this.type;
	}
	
	public String getSearchKeyword() {
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
	
	public void replaceCmd(DisplayCommand newCmd) {
		if (newCmd.getType() == Type.SEARCHKEY) {
			this.type = Type.SEARCHKEY;
			this.searchKeyword = newCmd.getSearchKeyword();
		} else if (newCmd.getType() == Type.SEARCHDATE) {
			this.type = Type.SEARCHDATE;
			this.searchDate = newCmd.getSearchDate();
		} else if (newCmd.getType() == Type.SCOPE) {
			this.type = Type.SCOPE;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DisplayCommand)) {
			return false;
		} else {
			DisplayCommand that = (DisplayCommand) obj;
			return this.getType().equals(that.getType())
					&& Objects.equals(this.getSearchKeyword(), that.getSearchKeyword())
					&& Objects.equals(this.getSearchDate(), that.getSearchDate())
					&& Objects.equals(this.getScope(), that.getScope());
		}
	}
	
}
