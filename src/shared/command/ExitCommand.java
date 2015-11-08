package shared.command;

//@@author A0131188H
public class ExitCommand extends AbstractCommand {

	private String undoMessage = "\"exit\" action cannot be undone!";

	public String getUndoMessage() {
		return undoMessage;
	}

	public CmdType getCmdType() {
		return CmdType.EXIT;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ExitCommand)) {
			return false;
		} else {
			return true;
		}
	}
}
