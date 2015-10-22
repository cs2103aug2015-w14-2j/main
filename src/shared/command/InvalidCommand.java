package shared.command;

public class InvalidCommand extends AbstractCommand {
	private String undoMessage = "\"invalid\" action cannot be undone!";
	
	public String getUndoMessage() {
		return undoMessage;
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
