package logic.action;

import logic.TaskList;
import shared.Constants;
import shared.Output;
import shared.command.CreateCommand;
import shared.task.BoundedTask;
import shared.task.DeadlineTask;
import shared.task.FloatingTask;

//@@author A0124828B
public class CreateAction extends AbstractAction {
	private static final String MESSAGE_CREATION = "\"%1$s\" has been created!";

	private CreateCommand createCommand;

	public CreateAction(CreateCommand createCommand, TaskList taskList,
			TaskList latestDisplayed) {
		this.createCommand = createCommand;
		this.taskList = taskList;
		this.latestDisplayedList = latestDisplayed;
	}

	public Output execute() {
		switch (this.createCommand.getTaskType()) {
		case FLOATING:
			return createFloatingTask(this.createCommand);
		case DEADLINE:
			return createDeadlineTask(this.createCommand);
		case BOUNDED:
			return createBoundedTask(this.createCommand);
		default:
			return new Output(Constants.MESSAGE_INVALID_COMMAND);
		}
	}

	private Output createFloatingTask(CreateCommand parsedCmd) {
		FloatingTask newFloatingTask = new FloatingTask(parsedCmd.getTaskName());
		this.taskList.addTask(newFloatingTask);
		String feedbackMessage = String.format(MESSAGE_CREATION,
				parsedCmd.getTaskName());
		return new Output(feedbackMessage);
	}

	private Output createDeadlineTask(CreateCommand parsedCmd) {
		DeadlineTask newDeadlineTask = new DeadlineTask(
				parsedCmd.getTaskName(), parsedCmd.getEndDateTime());
		this.taskList.addTask(newDeadlineTask);
		String feedbackMessage = String.format(MESSAGE_CREATION,
				parsedCmd.getTaskName());
		return new Output(feedbackMessage);
	}

	private Output createBoundedTask(CreateCommand parsedCmd) {
		try {
			BoundedTask newBoundedTask = new BoundedTask(
					parsedCmd.getTaskName(), parsedCmd.getStartDateTime(),
					parsedCmd.getEndDateTime());
			this.taskList.addTask(newBoundedTask);
		} catch (IllegalArgumentException e) {
			return new Output(e);
		}
		String feedbackMessage = String.format(MESSAGE_CREATION,
				parsedCmd.getTaskName());
		return new Output(feedbackMessage);
	}

}
