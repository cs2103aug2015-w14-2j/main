package logic.action;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import logic.TaskList;
import shared.Constants;
import shared.Output;
import shared.SharedLogger;
import shared.Output.Priority;
import shared.command.DisplayCommand;
import shared.task.AbstractTask.Status;

//@@author A0124828B
public class DisplayAction extends AbstractAction {
	private Logger logger = SharedLogger.getInstance().getLogger();
	private static final String MESSAGE_DISPLAY_ALL = "All tasks are now displayed!";
	private static final String MESSAGE_DISPLAY_EMPTY = "There are no tasks to display :'(";
	private static final String MESSAGE_DISPLAY_FLOATING = "All floating tasks are now displayed!";
	private static final String MESSAGE_DISPLAY_DEFAULT = "Welcome to Flexi-List!";
	private static final String MESSAGE_DISPLAY_STATUS = "All %1$s tasks are now displayed!";
	private static final String MESSAGE_DISPLAY_KEYWORD = "All tasks with keyword \"%1$s\" are now displayed!";
	private static final String MESSAGE_DISPLAY_DATE = "All tasks with date \"%1$s\" are now displayed!";
	private static final String MESSAGE_DISPLAY_DATE_EMPTY = "There are no tasks with date \"%1$s\" :)";
	private static final int OVERDUE_COUNT = 1;
	private static final int DATED_COUNT = 10;
	private static final int FLOATING_COUNT = 3;

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
		latestDisplayedList.replaceContents(sortedTaskList);
		logger.log(Level.INFO, "Executed display all in DisplayAction");
		Output output = new Output(sortedTaskList);
		if (sortedTaskList.size() < 1) {
			output.setReturnMessage(MESSAGE_DISPLAY_EMPTY);
		} else {
			output.setReturnMessage(MESSAGE_DISPLAY_ALL);
		}
		return output;
	}

	private Output displayFloating() {
		latestDisplayCmd.replaceCmd(new DisplayCommand(
				DisplayCommand.Scope.FLOATING));
		TaskList filteredList = this.taskList.filterForFloating();
		latestDisplayedList.replaceContents(filteredList);
		logger.log(Level.INFO, "Executed display floating in DisplayAction");
		Output output = new Output(filteredList);
		if (filteredList.size() < 1) {
			output.setReturnMessage(MESSAGE_DISPLAY_EMPTY);
		} else {
			output.setReturnMessage(MESSAGE_DISPLAY_FLOATING);
		}

		return output;
	}

	/**
	 * Creates default view of combination of overdue, dated and floating tasks
	 * Number of each task can be changed in the final static integers declared
	 * above
	 */
	
	private Output displayDefault() {
		latestDisplayCmd.replaceCmd(new DisplayCommand(
				DisplayCommand.Scope.DEFAULT));
		TaskList filteredList = new TaskList();
		TaskList undoneTaskList = this.taskList.filterByStatus(Status.UNDONE);

		// Filtering OVERDUE_COUNT number of overdue task that is closest to
		// current date
		TaskList overdueList = undoneTaskList.filterByOverdue(true);
		if (overdueList.size() > OVERDUE_COUNT) {
			overdueList = overdueList.getDateSortedClone();
			overdueList = overdueList.subList(overdueList.size()
					- OVERDUE_COUNT + 1, overdueList.size());
		}

		// Filtering DATED_COUNT number of dated task that is closest to and
		// after current date
		TaskList datedTaskList = undoneTaskList
				.filterInclusiveAfterDate(LocalDate.now());
		datedTaskList = datedTaskList.filterByOverdue(false);
		if (datedTaskList.size() > DATED_COUNT) {
			datedTaskList = datedTaskList.subList(0, DATED_COUNT);
		}
		datedTaskList = datedTaskList.getDateSortedClone();

		// Filtering FLOATING_COUNT number of floating task
		TaskList floatingTaskList = undoneTaskList.filterForFloating();
		if (floatingTaskList.size() > FLOATING_COUNT) {
			floatingTaskList = floatingTaskList.subList(floatingTaskList.size()
					- FLOATING_COUNT, floatingTaskList.size());
		}
		filteredList.addAll(overdueList);
		filteredList.addAll(datedTaskList);
		filteredList.addAll(floatingTaskList);

		assert (filteredList.size() <= OVERDUE_COUNT + FLOATING_COUNT
				+ DATED_COUNT);

		latestDisplayedList.replaceContents(filteredList);
		logger.log(Level.INFO, "Executed display default in DisplayAction");
		Output output = new Output(filteredList);
		if (filteredList.size() < 1) {
			output.setReturnMessage(MESSAGE_DISPLAY_EMPTY);
		} else {
			output.setReturnMessage(MESSAGE_DISPLAY_DEFAULT);
		}
		return output;
	}

	private Output displayStatus(Status status) {
		assert status != null;
		if (status == Status.DONE) {
			latestDisplayCmd.replaceCmd(new DisplayCommand(
					DisplayCommand.Scope.DONE));
		} else {
			latestDisplayCmd.replaceCmd(new DisplayCommand(
					DisplayCommand.Scope.UNDONE));
		}
		TaskList sortedTaskList = this.taskList.getDateSortedClone();
		TaskList filteredList = sortedTaskList.filterByStatus(status);

		latestDisplayedList.replaceContents(filteredList);
		logger.log(Level.INFO, "Executed display by status in DisplayAction");
		Output output = new Output(filteredList);
		if (filteredList.size() < 1) {
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
		
		logger.log(Level.INFO, "Executed display by name in DisplayAction");
		Output output = new Output(latestDisplayedList);
		String searchTerms = stringify(keywords);
		if (latestDisplayedList.size() < 1) {
			output = new Output(Constants.MESSAGE_INVALID_KEYWORD, searchTerms);
			output.setPriority(Priority.HIGH);
		} else {
			output.setReturnMessage(String.format(MESSAGE_DISPLAY_KEYWORD,
					searchTerms));
		}

		return output;
	}

	private Output displayOnDate(DisplayCommand parsedCmd) {
		latestDisplayCmd.replaceCmd(new DisplayCommand(parsedCmd
				.getSearchDate()));
		LocalDate queryDate = parsedCmd.getSearchDate().toLocalDate();
		TaskList undoneTaskList = this.taskList.filterByStatus(Status.UNDONE);
		TaskList sortedTaskList = undoneTaskList.getDateSortedClone();
		latestDisplayedList.replaceContents(sortedTaskList
				.filterByDate(queryDate));

		logger.log(Level.INFO, "Executed display by date in DisplayAction");
		Output output = new Output(latestDisplayedList);
		DateTimeFormatter DTFormatter = DateTimeFormatter
				.ofPattern("dd MM yyyy");
		String returnDate = queryDate.format(DTFormatter);
		if (latestDisplayedList.size() < 1) {
			output.setReturnMessage(String.format(MESSAGE_DISPLAY_DATE_EMPTY,
					returnDate));
		} else {
			output.setReturnMessage(String.format(MESSAGE_DISPLAY_DATE,
					returnDate));
		}
		return output;
	}

	private String stringify(ArrayList<String> stringArray) {
		String returnString = "";
		for (String string : stringArray) {
			returnString = returnString + string + " ";
		}
		// Trim away space left by last element
		returnString = returnString.trim();
		return returnString;
	}
}
