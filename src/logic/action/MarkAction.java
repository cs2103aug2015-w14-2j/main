package logic.action;

import java.util.ArrayList;

import logic.TaskList;
import shared.Constants;
import shared.Output;
import shared.Output.Priority;
import shared.command.DisplayCommand;
import shared.command.MarkCommand;
import shared.command.MarkCommand.markField;
import shared.task.AbstractTask;
import shared.task.DeadlineTask;
import shared.task.AbstractTask.Status;

//@@author A0124828B
public class MarkAction extends AbstractAction{
	
	private static final String MESSAGE_MARK_DONE = "\"%1$s\" has been marked done.";
	private static final String MESSAGE_MARK_UNDONE = "\"%1$s\" has been marked undone.";
	
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
			Output feedback = new Output(Constants.MESSAGE_INVALID_INDEX);
			feedback.setPriority(Priority.HIGH);
			return feedback;
		}

		int inputTaskIndex = parsedCmd.getIndex() - 1;
		AbstractTask displayTaskToMark = latestDisplayedList
				.getTask(inputTaskIndex);
		String taskName = displayTaskToMark.getName();
		int taskIndexInTaskList = this.taskList.indexOf(displayTaskToMark);
		AbstractTask actualTaskToMark = this.taskList
				.getTask(taskIndexInTaskList);

		Output feedback = new Output(MESSAGE_MARK_UNDONE, taskName);
		Status newStatus = Status.UNDONE;
		if (parsedCmd.getMarkField().equals(markField.MARK)) {
			newStatus = Status.DONE;
			feedback = new Output(MESSAGE_MARK_DONE, taskName);
		}
		actualTaskToMark.setStatus(newStatus);
		removeOverdue(actualTaskToMark);
		return feedback;
	}
	
	/**
	 * case 1: no tasks with keyword found
	 * case 2: one task with keyword found
	 * case 3: multiple tasks with keyword found
	 */
	
	private Output markByKeyword(MarkCommand parsedCmd) {
		String keyword = parsedCmd.getSearchKeyword();

		Output feedback = new Output(MESSAGE_MARK_UNDONE, keyword);
		Status newStatus = Status.UNDONE;
		if (parsedCmd.getMarkField().equals(markField.MARK)) {
			newStatus = Status.DONE;
			feedback = new Output(MESSAGE_MARK_DONE, keyword);
		}

		TaskList filteredList = this.taskList.filterByName(keyword);
		if (filteredList.size() == 0) {
			feedback = new Output(Constants.MESSAGE_INVALID_KEYWORD, keyword);
			feedback.setPriority(Priority.HIGH);
			return feedback;
		} else if (filteredList.size() == 1
				&& filteredList.getTask(0).getName().equals(keyword)) {
			AbstractTask uniqueTask = filteredList.getTask(0);
			uniqueTask.setStatus(newStatus);
			removeOverdue(uniqueTask);
			return feedback;
		} else {
			ArrayList<String> keywords = new ArrayList<String>();
			keywords.add(keyword);
			DisplayCommand searchCmd = new DisplayCommand(keywords);
			DisplayAction searchAction = new DisplayAction(searchCmd, taskList,
					latestDisplayedList, latestDisplayCmd);
			return searchAction.execute();
		}
	}
	
	private void removeOverdue(AbstractTask task) {
		if (task instanceof DeadlineTask) {
			DeadlineTask deadlineTask = (DeadlineTask) task;
			deadlineTask.setOverdue(false);
		}
	}

}
