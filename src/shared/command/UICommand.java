package shared.command;

//@@author A0131188H
public class UICommand extends AbstractCommand {
	private String undoMessage = "\"UI\" action cannot be undone!";
	
	public String getUndoMessage() {
		return undoMessage;
	}
	
	public CmdType getCmdType() {
		return CmdType.CREATE;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof UICommand);
	}
}
