package logic.action;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import logic.TaskList;
import shared.Constants;
import shared.Output;
import shared.Output.Priority;
import shared.command.DisplayCommand;
import shared.task.AbstractTask;
import shared.task.FloatingTask;
import shared.task.AbstractTask.Status;

//@@author A0124828B
public class DisplayAction extends AbstractAction {

	private static final String MESSAGE_DISPLAY_ALL = "All tasks are now displayed!";
	private static final String MESSAGE_DISPLAY_EMPTY = "There are no tasks to display :'(";
	private static final String MESSAGE_DISPLAY_FLOATING = "All floating tasks are now displayed!";
	private static final String MESSAGE_DISPLAY_DEFAULT = "Welcome to Flexi-List!";
	private static final String MESSAGE_DISPLAY_STATUS = "All %1$s tasks are now displayed!";
	private static final String MESSAGE_DISPLAY_KEYWORD = "All tasks with keyword \"%1$s\" are now displayed!";
	private static final String MESSAGE_DISPLAY_DATE = "All tasks with date \"%1$s\" are now displayed!";

	private DisplayCommand displayCommand;

	public DisplayAction(DisplayCommand displayCommand, TaskList taskList,
			TaskList latestDisplayed, DisplayCommand latestDisplayCmd) {
		this.displayCommand = displayCommand;
		this.taskList = taskList;
		this.latestDisplayedList = latestDisplayed;
		this.latestDisplayCmd = latestDisplayCmd;
	}

	public Output execute() {
		switch (this.displayCommand.getType()) {
		case SCOPE:
			return displayByScope(this.displayCommand);
		case SEARCHKEY:
			return displayByName(this.displayCommand.getSearchKeyword());
		case SEARCHDATE:
			return displayOnDate(this.displayCommand);
		default:
			// should not reach this code
			Output feedback = new Output(Constants.MESSAGE_INVALID_COMMAND);
			feedback.setPriority(Priority.HIGH);
			return feedback;
		}
	}

	private Output displayByScope(DisplayCommand parsedCmd) {
		assert (parsedCmd.getScope() != null);

		switch (parsedCmd.getScope()) {
		case ALL:
			return displayAllTasks();
		case FLOATING:
			return displayFloating();
		case DEFAULT:
			return displayDefault();
		case DONE:
			return displayStatus(Status.DONE);
		case UNDONE:
			return displayStatus(Status.UNDONE);
		default:
			// should not reach this code
			Output feedback = new Output(Constants.MESSAGE_INVALID_COMMAND);
			feedback.setPriority(Priority.HIGH);
			return feedback;
		}
	}

	private Output displayAllTasks() {
		latestDisplayCmd
				.replaceCmd(new DisplayCommand(DisplayCommand.Scope.ALL));
		TaskList sortedTaskList = this.taskList.getDateSortedClone();
		ArrayList<ArrayList<String>> outputList = new ArrayList<ArrayList<String>>();
		Output output = new Output();

		for (int i = 0; i < sortedTaskList.size(); i++) {
			AbstractTask currentTask = sortedTaskList.getTask(i);
			ArrayList<String> taskArray = (currentTask.toArray());
			taskArray.add(0, String.valueOf(i + 1));
			outputList.add(taskArray);
		}
		latestDisplayedList.replaceContents(sortedTaskList);
		output.setOutput(outputList);
		if (outputList.size() < 1) {
			output.setReturnMessage(MESSAGE_DISPLAY_EMPTY);
		} else {
			output.setReturnMessage(MESSAGE_DISPLAY_ALL);
		}
		return output;
	}

	private Output displayFloating() {
		latestDisplayCmd.replaceCmd(new DisplayCommand(
				DisplayCommand.Scope.FLOATING));
		ArrayList<ArrayList<String>> outputList = new ArrayList<ArrayList<String>>();
		TaskList filteredList = new TaskList();
		Output output = new Output();

		for (int i = 0; i < this.taskList.size(); i++) {
			AbstractTask currentTask = this.taskList.getTask(i);
			if (currentTask instanceof FloatingTask) {
				filteredList.addTask(currentTask);
				ArrayList<String> taskArray = (currentTask.toArray());
				taskArray.add(0, String.valueOf(filteredList.size()));
				outputList.add(taskArray);
			}
		}
		latestDisplayedList.replaceContents(filteredList);
		output.setOutput(outputList);
		if (outputList.size() < 1) {
			output.setReturnMessage(MESSAGE_DISPLAY_EMPTY);
		} else {
			output.setReturnMessage(MESSAGE_DISPLAY_FLOATING);
		}

		return output;
	}

	/*
	 * Creates default view of 7 timed tasks closest to current date and 3 of
	 * the newest floating tasks 1. Get 7 timed tasks using filterAfterDate() 2.
	 * Get 3 of the newest floating tasks by traversing taskList from behind
	 */

	private Output displayDefault() {
		latestDisplayCmd.replaceCmd(new DisplayCommand(
				DisplayCommand.Scope.DEFAULT));
		TaskList filteredList = new TaskList();
		TaskList undoneTaskList = this.taskList.filterByStatus(Status.UNDONE);

		// filterByOverdue
		TaskList overdueList = undoneTaskList.filterByOverdue(true);

		if (overdueList.size() > 1) {
			overdueList = overdueList.getDateSortedClone();
			overdueList = overdueList.subList(overdueList.size() - 1, overdueList.size());
		}

		TaskList datedTaskList = undoneTaskList
				.filterInclusiveAfterDate(LocalDate.now());
		if (datedTaskList.size() > 10) {
			datedTaskList = datedTaskList.subList(0, 10);
		}
		datedTaskList = datedTaskList.getDateSortedClone();

		TaskList floatingTaskList = new TaskList();
		for (AbstractTask task : undoneTaskList.getTasks()) {
			if (task instanceof FloatingTask) {
				floatingTaskList.addTask(task);
			}
		}
		if (floatingTaskList.size() > 3) {
			floatingTaskList = floatingTaskList.subList(
					floatingTaskList.size() - 3, floatingTaskList.size());
		}
		filteredList.addAll(overdueList);
		filteredList.addAll(datedTaskList);
		filteredList.addAll(floatingTaskList);
		assert (filteredList.size() <= 14);
		latestDisplayedList.replaceContents(filteredList);

		ArrayList<ArrayList<String>> outputList = new ArrayList<ArrayList<String>>();
		Output output = new Output();

		int i = 1;
		for (AbstractTask task : filteredList.getTasks()) {
			ArrayList<String> taskArray = (task.toArray());
			taskArray.add(0, String.valueOf(i));
			outputList.add(taskArray);
			i++;
		}
		output.setOutput(outputList);
		if (outputList.size() < 1) {
			output.setReturnMessage(MESSAGE_DISPLAY_EMPTY);
		} else {
			output.setReturnMessage(MESSAGE_DISPLAY_DEFAULT);
		}
		return output;
	}

	private Output displayStatus(Status status) {
		if (status == Status.DONE) {
			latestDisplayCmd.replaceCmd(new DisplayCommand(
					DisplayCommand.Scope.DONE));
		} else {
			latestDisplayCmd.replaceCmd(new DisplayCommand(
					DisplayCommand.Scope.UNDONE));
		}
		ArrayList<ArrayList<String>> outputList = new ArrayList<ArrayList<String>>();
		TaskList sortedTaskList = this.taskList.getDateSortedClone();
		TaskList filteredList = new TaskList();
		Output output = new Output();

		for (int i = 0; i < sortedTaskList.size(); i++) {
			AbstractTask currentTask = sortedTaskList.getTask(i);
			if (currentTask.getStatus() == status) {
				filteredList.addTask(currentTask);
				ArrayList<String> taskArray = (currentTask.toArray());
				taskArray.add(0, String.valueOf(filteredList.size()));
				outputList.add(taskArray);
			}
		}
		latestDisplayedList.replaceContents(filteredList);
		output.setOutput(outputList);
		if (outputList.size() < 1) {
			output.setReturnMessage(MESSAGE_DISPLAY_EMPTY);
		} else {
			output.setReturnMessage(String.format(MESSAGE_DISPLAY_STATUS,
					status.toString()));
		}
		return output;
	}

	private Output displayByName(ArrayList<String> keywords) {
		latestDisplayCmd.replaceCmd(new DisplayCommand(keywords));
		TaskList undoneTaskList = this.taskList.filterByStatus(Status.UNDONE);
		TaskList sortedTaskList = undoneTaskList.getDateSortedClone();
		latestDisplayedList.replaceContents(sortedTaskList
				.filterByNames(keywords));
		ArrayList<ArrayList<String>> outputList = new ArrayList<ArrayList<String>>();
		Output output = new Output();

		int i = 1;
		for (AbstractTask task : latestDisplayedList.getTasks()) {
			ArrayList<String> taskArray = (task.toArray());
			taskArray.add(0, String.valueOf(i));
			outputList.add(taskArray);
			i++;
		}

		output.setOutput(outputList);
		String searchTerms = stringify(keywords);
		if (outputList.size() < 1) {
			output = new Output(Constants.MESSAGE_INVALID_KEYWORD, searchTerms);
			output.setPriority(Priority.HIGH);
		} else {
			output.setReturnMessage(String.format(MESSAGE_DISPLAY_KEYWORD,
					searchTerms));
		}

		return output;
	}

	private Output displayOnDate(DisplayCommand parsedCmd) {
		latestDisplayCmd.replaceCmd(new DisplayCommand(parsedCmd.getSearchDate()));
		LocalDate queryDate = parsedCmd.getSearchDate().toLocalDate();
		TaskList undoneTaskList = this.taskList.filterByStatus(Status.UNDONE);
		TaskList sortedTaskList = undoneTaskList.getDateSortedClone();
		latestDisplayedList.replaceContents(sortedTaskList
				.filterByDate(queryDate));
		ArrayList<ArrayList<String>> outputList = new ArrayList<ArrayList<String>>();
		Output output = new Output();

		int i = 1;
		for (AbstractTask task : latestDisplayedList.getTasks()) {
			ArrayList<String> taskArray = (task.toArray());
			taskArray.add(0, String.valueOf(i));
			outputList.add(taskArray);
			i++;
		}

		output.setOutput(outputList);
		DateTimeFormatter DTFormatter = DateTimeFormatter
				.ofPattern("dd MM yyyy");
		String returnDate = queryDate.format(DTFormatter);
		if (outputList.size() < 1) {
			output.setReturnMessage(MESSAGE_DISPLAY_EMPTY);
		} else {
			output.setReturnMessage(String.format(MESSAGE_DISPLAY_DATE,
					returnDate));
		}
		return output;
	}
	
	private String stringify(ArrayList<String> stringArray) {
		String returnString = "";
		for (String string: stringArray) {
			returnString = returnString + string + " ";
		}
		//Trim away space left by last element
		returnString = returnString.trim();
		return returnString;
	}
}
