package logic.action;

import logic.TaskList;
import shared.Constants;
import shared.Output;
import shared.Output.Priority;
import shared.command.DeleteCommand;
import shared.command.DisplayCommand;
import shared.task.AbstractTask;

//@@author A0124828B
public class DeleteAction extends AbstractAction{
	
	private static final String MESSAGE_SINGLE_DELETION = "\"%1$s\" has been deleted!";
	private static final String MESSAGE_ALL_DELETION = "All tasks have been deleted!";
	
	private DeleteCommand deleteCommand;
	
	public DeleteAction(DeleteCommand deleteCommand, TaskList taskList,
			TaskList latestDisplayed, DisplayCommand latestDisplayCmd) {
		this.deleteCommand = deleteCommand;
		this.taskList = taskList;
		this.latestDisplayedList = latestDisplayed;
		this.latestDisplayCmd = latestDisplayCmd;
	}
	
	public Output execute() {
		switch (this.deleteCommand.getType()) {
		case INDEX:
			return deleteByIndex(this.deleteCommand);
		case SEARCHKEYWORD:
			return deleteByKeyword(this.deleteCommand);
		case SCOPE:
			return deleteByScope(this.deleteCommand);
		default:
			// should not reach this code
			Output feedback = new Output(Constants.MESSAGE_INVALID_COMMAND);
			feedback.setPriority(Priority.HIGH);
			return feedback;
		}
	}
	
	private Output deleteByIndex(DeleteCommand parsedCmd) {
		assert (parsedCmd.getIndex() > 0);

		if (parsedCmd.getIndex() > latestDisplayedList.size()) {
			Output feedback = new Output(Constants.MESSAGE_INVALID_COMMAND);
			feedback.setPriority(Priority.HIGH);
			return feedback;
		}
		int indexToDelete = parsedCmd.getIndex() - 1;
		AbstractTask taskToDelete = latestDisplayedList.getTask(indexToDelete);
		String taskName = taskToDelete.getName();
		taskList.removeTask(taskToDelete);
		Output feedback = new Output(MESSAGE_SINGLE_DELETION, taskName);
		feedback.setPriority(Priority.HIGH);
		return feedback;
	}

	private Output deleteByKeyword(DeleteCommand parsedCmd) {
		String keyword = parsedCmd.getSearchKeyword();
		TaskList filteredList = this.taskList.filterByName(keyword);
		if (filteredList.size() == 0) {
			return new Output(Constants.MESSAGE_INVALID_KEYWORD, keyword);
		} else if (filteredList.size() == 1
				&& filteredList.getTask(0).getName().equals(keyword)) {
			AbstractTask uniqueTask = filteredList.getTask(0);
			taskList.removeTask(uniqueTask);
			Output feedback = new Output(MESSAGE_SINGLE_DELETION, keyword);
			feedback.setPriority(Priority.HIGH);
			return feedback;
		} else {
			DisplayCommand searchCmd = new DisplayCommand(keyword);
			DisplayAction searchAction = new DisplayAction(searchCmd, taskList,
					latestDisplayedList, latestDisplayCmd);
			return searchAction.execute();
		}
	}

	private Output deleteByScope(DeleteCommand parsedCmd) {
		switch (parsedCmd.getScope()) {
		case ALL:
			return deleteAllTasks(parsedCmd);
		default:
			// should not reach this code
			Output feedback = new Output(Constants.MESSAGE_INVALID_COMMAND);
			feedback.setPriority(Priority.HIGH);
			return feedback;

		}
	}

	private Output deleteAllTasks(DeleteCommand parsedCmd) {
		taskList.clear();
		Output feedback = new Output(MESSAGE_ALL_DELETION);
		feedback.setPriority(Priority.HIGH);
		return feedback;
	}

}
