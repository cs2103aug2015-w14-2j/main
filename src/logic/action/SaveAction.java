package logic.action;

import shared.Output;
import shared.Output.Priority;
import shared.command.SaveCommand;
import storage.Storage;

//@@author A0124828B
public class SaveAction extends AbstractAction {
	private static final String MESSAGE_SAVEPATH = "\"%1$s\" has been set as new save path!";
	private static final String MESSAGE_SAVEPATH_FAIL = "\"%1$s\" is an invalid save path!";

	private SaveCommand saveCommand;
	private Storage storage;

	public SaveAction(SaveCommand saveCmd, Storage storage) {
		this.saveCommand = saveCmd;
		this.storage = storage;
	}

	public Output execute() {
		boolean isValidPath = storage.changePath(this.saveCommand.getPath());
		if (isValidPath) {
			return new Output(MESSAGE_SAVEPATH, this.saveCommand.getPath());
		} else {
			Output feedback = new Output(MESSAGE_SAVEPATH_FAIL,
					this.saveCommand.getPath());
			feedback.setPriority(Priority.HIGH);
			return feedback;
		}
	}

}
