package shared.command;

//@@author A0131188H
public class InvalidCommand extends AbstractCommand {
	private String undoMessage = "\"invalid\" action cannot be undone!";

	public String getUndoMessage() {
		return undoMessage;
	}

	public CmdType getCmdType() {
		return CmdType.INVALID;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof InvalidCommand)) {
			return false;
		} else {
			return true;
		}
	}
}
