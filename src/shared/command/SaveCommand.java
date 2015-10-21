package shared.command;

public class SaveCommand extends AbstractCommand {
	private String undoMessage = "\"save\" action cannot be undone!";
	
	public String getUndoMessage() {
		return undoMessage;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SaveCommand)) {
			return false;
		} else {
			return true;
		}
	}
}
