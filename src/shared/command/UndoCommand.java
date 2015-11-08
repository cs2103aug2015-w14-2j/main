package shared.command;

//@@author A0131188H
public class UndoCommand extends AbstractCommand {
	private String undoMessage = "\"undo\" action cannot be undone!";

	public String getUndoMessage() {
		return undoMessage;
	}

	public CmdType getCmdType() {
		return CmdType.UNDO;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof UndoCommand)) {
			return false;
		} else {
			return true;
		}
	}
}
