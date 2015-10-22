package shared.command;

public class HelpCommand extends AbstractCommand {
	
	private String undoMessage = "\"help\" action cannot be undone!";
	
	public String getUndoMessage() {
		return undoMessage;
	}
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof HelpCommand)) {
			return false;
		} else {
			return true;
		}
	}
}
