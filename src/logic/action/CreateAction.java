package logic.action;

import java.util.logging.Level;
import java.util.logging.Logger;

import logic.TaskList;
import shared.Constants;
import shared.Output;
import shared.SharedLogger;
import shared.command.CreateCommand;
import shared.task.BoundedTask;
import shared.task.DeadlineTask;
import shared.task.FloatingTask;

//@@author A0124828B
public class CreateAction extends AbstractAction {
	private Logger logger = SharedLogger.getInstance().getLogger();
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
		assert parsedCmd.getTaskName() != null;
		FloatingTask newFloatingTask = new FloatingTask(parsedCmd.getTaskName());
		this.taskList.addTask(newFloatingTask);
		logger.log(Level.INFO, "Creating floating task in CreateAction");
		return new Output(MESSAGE_CREATION, parsedCmd.getTaskName());
	}

	private Output createDeadlineTask(CreateCommand parsedCmd) {
		assert parsedCmd.getTaskName() != null;
		assert parsedCmd.getEndDateTime() != null;
		DeadlineTask newDeadlineTask = new DeadlineTask(
				parsedCmd.getTaskName(), parsedCmd.getEndDateTime());
		this.taskList.addTask(newDeadlineTask);
		logger.log(Level.INFO, "Creating deadline task in CreateAction");
		return new Output(MESSAGE_CREATION, parsedCmd.getTaskName());
	}

	private Output createBoundedTask(CreateCommand parsedCmd) {
		assert parsedCmd.getTaskName() != null;
		assert parsedCmd.getStartDateTime() != null;
		assert parsedCmd.getEndDateTime() != null;
		try {
			BoundedTask newBoundedTask = new BoundedTask(
					parsedCmd.getTaskName(), parsedCmd.getStartDateTime(),
					parsedCmd.getEndDateTime());
			this.taskList.addTask(newBoundedTask);
			logger.log(Level.INFO, "Creating bounded task in CreateAction");
		} catch (IllegalArgumentException e) {
			// Handles exception where supplied start datetime is later than end datetime
			return new Output(e);
		}
		return new Output(MESSAGE_CREATION, parsedCmd.getTaskName());
	}

}
