package logic.action;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import logic.TaskList;
import shared.Constants;
import shared.Output;
import shared.SharedLogger;
import shared.Output.Priority;
import shared.command.DisplayCommand;
import shared.command.MarkCommand;
import shared.command.MarkCommand.markField;
import shared.task.AbstractTask;
import shared.task.DeadlineTask;
import shared.task.AbstractTask.Status;

//@@author A0124828B
public class MarkAction extends AbstractAction {
	private Logger logger = SharedLogger.getInstance().getLogger();
	private static final String MESSAGE_MARK_DONE = "\"%1$s\" has been marked done.";
	private static final String MESSAGE_MARK_UNDONE = "\"%1$s\" has been marked undone.";
	private static final String MESSAGE_MARK_INVALID = "Invalid: Mark/Unmark operation has already been done.";

	private MarkCommand markCommand;

	public MarkAction(MarkCommand markCommand, TaskList taskList,
			TaskList latestDisplayed, DisplayCommand latestDisplayCmd) {
		this.markCommand = markCommand;
		this.taskList = taskList;
		this.latestDisplayedList = latestDisplayed;
		this.latestDisplayCmd = latestDisplayCmd;
	}

	public Output execute() {
		switch (this.markCommand.getType()) {
		case INDEX:
			return markByIndex(this.markCommand);
		case SEARCHKEYWORD:
			return markByKeyword(this.markCommand);
		default:
			// should not reach this code
			Output feedback = new Output(Constants.MESSAGE_INVALID_COMMAND);
			feedback.setPriority(Priority.HIGH);
			return feedback;
		}
	}

	private Output markByIndex(MarkCommand parsedCmd) {
		assert (parsedCmd.getIndex() > 0);

		if (parsedCmd.getIndex() > latestDisplayedList.size()) {
			logger.log(Level.INFO, "Supplied index for mark is out of bounds!");
			Output feedback = new Output(Constants.MESSAGE_INVALID_INDEX);
			feedback.setPriority(Priority.HIGH);
			return feedback;
		}

		int inputTaskIndex = parsedCmd.getIndex() - 1;
		AbstractTask displayTaskToMark = latestDisplayedList
				.getTask(inputTaskIndex);
		int taskIndexInTaskList = this.taskList.indexOf(displayTaskToMark);
		AbstractTask actualTaskToMark = this.taskList
				.getTask(taskIndexInTaskList);
		// Initialized as invalid in case parsedCmd behaves unexpectedly
		Output feedback = new Output(Constants.MESSAGE_INVALID_COMMAND);
		executeMark(parsedCmd, actualTaskToMark, feedback);
		return feedback;
	}

	/**
	 * case 1: no tasks with keyword found 
	 * case 2: one task with keyword found
	 * case 3: multiple tasks with keyword found
	 */

	private Output markByKeyword(MarkCommand parsedCmd) {
		String keyword = parsedCmd.getSearchKeyword();

		// Initialized as invalid in case parsedCmd behaves unexpectedly
		Output feedback = new Output(Constants.MESSAGE_INVALID_COMMAND);

		TaskList filteredList = this.taskList.filterByName(keyword);
		if (filteredList.size() == 0) {
			feedback = new Output(Constants.MESSAGE_INVALID_KEYWORD, keyword);
			feedback.setPriority(Priority.HIGH);
			logger.log(Level.INFO, "No task with given keyword to mark.");
			return feedback;
		} else if (filteredList.size() == 1
				&& filteredList.getTask(0).getName().equals(keyword)) {
			AbstractTask uniqueTask = filteredList.getTask(0);
			executeMark(parsedCmd, uniqueTask, feedback);
			return feedback;
		} else {
			ArrayList<String> keywords = new ArrayList<String>();
			keywords.add(keyword);
			DisplayCommand searchCmd = new DisplayCommand(keywords);
			DisplayAction searchAction = new DisplayAction(searchCmd, taskList,
					latestDisplayedList, latestDisplayCmd);
			logger.log(Level.INFO,
					"More than one task with keyword to mark, executing search.");
			return searchAction.execute();
		}
	}
	
	/**
	 * case 1: MarkCommand is invalid and will be marked so that it is not undo-able
	 * case 2: MarkCommand marks task as done
	 * case 3: MarkCommand marks task as undone
	 */
	
	private void executeMark(MarkCommand cmd, AbstractTask task, Output feedback) {
		String taskName = task.getName();
		if (isInvalidMark(cmd, task)) {
			logger.log(Level.INFO, "Invalid: MarkCommand does not change state");
			cmd.setInvalidMark(true);
			feedback.replaceWith(new Output(MESSAGE_MARK_INVALID));
		} else if (cmd.getMarkField().equals(markField.MARK)) {
			logger.log(Level.INFO, "Marked task with name:" + taskName);
			task.setStatus(Status.DONE);
			feedback.replaceWith(new Output(MESSAGE_MARK_DONE, taskName));
			removeOverdue(task);
		} else if (cmd.getMarkField().equals(markField.UNMARK)) {
			logger.log(Level.INFO, "Unmarked task with name:" + taskName);
			feedback.replaceWith(new Output(MESSAGE_MARK_UNDONE, taskName));
			task.setStatus(Status.UNDONE);
		}
	}

	// A mark command is deemed as invalid if its targeted task will not have
	// its status changed.
	private boolean isInvalidMark(MarkCommand cmd, AbstractTask task) {
		if (cmd.getMarkField().equals(markField.MARK)
				&& task.getStatus().equals(Status.DONE)) {
			return true;
		} else if (cmd.getMarkField().equals(markField.UNMARK)
				&& task.getStatus().equals(Status.UNDONE)) {
			return true;
		} else {
			return false;
		}
	}

	private void removeOverdue(AbstractTask task) {
		if (task instanceof DeadlineTask) {
			DeadlineTask deadlineTask = (DeadlineTask) task;
			deadlineTask.setOverdue(false);
		}
	}

}
