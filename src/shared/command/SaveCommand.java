package shared.command;

import java.util.Objects;

//@@author A0131188H
public class SaveCommand extends AbstractCommand {

	private String path;
	private String undoMessage = "\"save\" action cannot be undone!";

	public SaveCommand(String path) {
		this.path = path;
	}

	public String getPath() {
		return this.path;
	}

	public String getUndoMessage() {
		return undoMessage;
	}

	public CmdType getCmdType() {
		return CmdType.SAVE;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SaveCommand)) {
			return false;
		} else {
			SaveCommand that = (SaveCommand) obj;
			return Objects.equals(this.getPath(), that.getPath());
		}
	}
}
