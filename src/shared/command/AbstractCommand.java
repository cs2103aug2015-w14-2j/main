package shared.command;

//@@author A0124828B
public abstract class AbstractCommand {
	public static enum CmdType {
		CREATE, EDIT, DISPLAY, DELETE, INVALID, EXIT, MARK, SAVE, UI, UNDO
	}

	public abstract CmdType getCmdType();

	public abstract String getUndoMessage();
}
