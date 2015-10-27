package shared.command;

public class UICommand extends AbstractCommand {
	private String undoMessage = "\"UI\" action cannot be undone!";
	
	public String getUndoMessage() {
		return undoMessage;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof UICommand);
	}
}
