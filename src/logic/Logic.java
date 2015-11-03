package logic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

import parser.Parser;
import shared.Output;
import shared.Output.Priority;
import shared.command.AbstractCommand;
import shared.command.CreateCommand;
import shared.command.DeleteCommand;
import shared.command.DisplayCommand;
import shared.command.EditCommand;
import shared.command.ExitCommand;
import shared.command.InvalidCommand;
import shared.command.DeleteCommand.Scope;
import shared.command.EditCommand.editField;
import shared.command.MarkCommand;
import shared.command.MarkCommand.markField;
import shared.command.SaveCommand;
import shared.command.UICommand;
import shared.command.UndoCommand;
import shared.task.AbstractTask;
import shared.task.BoundedTask;
import shared.task.DeadlineTask;
import shared.task.FloatingTask;
import shared.task.AbstractTask.Status;
import storage.Storage;

public class Logic implements LogicInterface {

	// Templates for program feedback
	private static final String MESSAGE_CREATION = "\"%1$s\" has been created!";
	private static final String MESSAGE_UPDATE = "\"%1$s\" has been edited!";
	private static final String MESSAGE_UPDATE_WRONG_TYPE = "Invalid: Task specified does not have this operation.";
	private static final String MESSAGE_SINGLE_DELETION = "\"%1$s\" has been deleted!";
	private static final String MESSAGE_ALL_DELETION = "All tasks have been deleted!";
	private static final String MESSAGE_STATUS_DELETION = "All %1$s tasks have been deleted!";
	private static final String MESSAGE_MARK = "\"%1$s\" has been marked %2$s.";
	private static final String MESSAGE_INVALID_COMMAND = "Invalid Command!";
	private static final String MESSAGE_DISPLAY_ALL = "All tasks are now displayed!";
	private static final String MESSAGE_DISPLAY_EMPTY = "There are no tasks to display :'(";
	private static final String MESSAGE_DISPLAY_FLOATING = "All floating tasks are now displayed!";
	private static final String MESSAGE_DISPLAY_DEFAULT = "Welcome to Flexi-List!";
	private static final String MESSAGE_DISPLAY_STATUS = "All %1$s tasks are now displayed!";
	private static final String MESSAGE_DISPLAY_KEYWORD = "All tasks with keyword \"%1$s\" are now displayed!";
	private static final String MESSAGE_DISPLAY_DATE = "All tasks with date \"%1$s\" are now displayed!";
	private static final String MESSAGE_SAVEPATH = "\"%1$s\" has been set as new save path!";
	private static final String MESSAGE_SAVEPATH_FAIL = "\"%1$s\" is an invalid save path!";
	private static final String MESSAGE_INVALID_KEYWORD = "No task with keyword \"%1$s\" has been found.";
	private static final String MESSAGE_INVALID_DATE = "No task with date \"%1$s\" has been found.";

	private final static int MESSAGE_LENGTH = 80;

	// Data structure for tasks
	private TaskList taskList;

	// Data structure for last displayed list
	private TaskList latestDisplayedList = null;

	// Data structure for Undo Functionality
	private Stack<TaskList> taskListStack = new Stack<TaskList>();
	private Stack<AbstractCommand> cmdHistoryStack = new Stack<AbstractCommand>();

	private DisplayCommand latestDisplayCmd = new DisplayCommand(
			DisplayCommand.Scope.DEFAULT);
	private EditCommand latestEditKeyword = null;
	private boolean shouldPreserveEditKeyword = false;

	private static Parser parser = new Parser();

	private Storage storage;

	public Logic(Storage storage) {
		this.storage = storage;
		loadFromStorage();
		loadStateForUndo();
	}

	public Output processInput(String userCmd) {
		AbstractCommand parsedCmd = parser.parseInput(userCmd);
		return executeCommand(parsedCmd);
	}

	protected Output executeCommand(AbstractCommand parsedCmd) {
		runBackgroundRoutines();

		if (parsedCmd instanceof CreateCommand) {
			return createTask((CreateCommand) parsedCmd);
		} else if (parsedCmd instanceof DisplayCommand) {
			return displayTasks((DisplayCommand) parsedCmd);
		} else if (parsedCmd instanceof EditCommand) {
			return editTask((EditCommand) parsedCmd);
		} else if (parsedCmd instanceof DeleteCommand) {
			return deleteTask((DeleteCommand) parsedCmd);
		} else if (parsedCmd instanceof MarkCommand) {
			return markTask((MarkCommand) parsedCmd);
		} else if (parsedCmd instanceof UndoCommand) {
			return undoPreviousAction();
		} else if (parsedCmd instanceof UICommand) {
			return feedbackForAction("emptyString", null);
		} else if (parsedCmd instanceof SaveCommand) {
			return setPath((SaveCommand) parsedCmd);
		} else if (parsedCmd instanceof InvalidCommand) {
			return feedbackForAction("invalid", null);
		} else if (parsedCmd instanceof ExitCommand) {
			System.exit(0);
		} else {
			return feedbackForAction("invalid", null);
		}
		return null;

	}

	private void loadFromStorage() {
		taskList = new TaskList(this.storage.read());
	}

	private void loadStateForUndo() {
		TaskList clonedList = this.taskList.clone();
		this.taskListStack.push(clonedList);
	}

	private void runBackgroundRoutines() {
		checkEditKeywordPreservation();
		updateOverdueStatus();
	}

	private void checkEditKeywordPreservation() {
		if (!shouldPreserveEditKeyword) {
			latestEditKeyword = null;
		}
		shouldPreserveEditKeyword = false;
	}

	private void recordChange(AbstractCommand parsedCmd) {
		storage.write(this.taskList.getTasks());
		TaskList clonedList = this.taskList.clone();
		this.taskListStack.push(clonedList);
		this.cmdHistoryStack.push(parsedCmd);
	}

	private void refreshLatestDisplayed() {
		executeCommand(latestDisplayCmd);
	}

	/*
	 * Methods for task creation
	 */

	private Output createTask(CreateCommand parsedCmd) {

		switch (parsedCmd.getTaskType()) {
		case FLOATING:
			return createFloatingTask(parsedCmd);
		case DEADLINE:
			return createDeadlineTask(parsedCmd);
		case BOUNDED:
			return createBoundedTask(parsedCmd);
		default:
			return feedbackForAction("invalid", null);
		}
	}

	private Output createFloatingTask(CreateCommand parsedCmd) {
		FloatingTask newFloatingTask = new FloatingTask(parsedCmd.getTaskName());
		this.taskList.addTask(newFloatingTask);
		refreshLatestDisplayed();
		recordChange(parsedCmd);
		return feedbackForAction("create", parsedCmd.getTaskName());
	}

	private Output createDeadlineTask(CreateCommand parsedCmd) {
		DeadlineTask newDeadlineTask = new DeadlineTask(
				parsedCmd.getTaskName(), parsedCmd.getEndDateTime());
		this.taskList.addTask(newDeadlineTask);
		refreshLatestDisplayed();
		recordChange(parsedCmd);
		return feedbackForAction("create", parsedCmd.getTaskName());
	}

	private Output createBoundedTask(CreateCommand parsedCmd) {
		try {
			BoundedTask newBoundedTask = new BoundedTask(
					parsedCmd.getTaskName(), parsedCmd.getStartDateTime(),
					parsedCmd.getEndDateTime());
			this.taskList.addTask(newBoundedTask);
		} catch (IllegalArgumentException e) {
			return feedbackForAction(e);
		}
		refreshLatestDisplayed();
		recordChange(parsedCmd);
		return feedbackForAction("create", parsedCmd.getTaskName());
	}

	/*
	 * Methods for displaying tasks
	 */

	private Output displayTasks(DisplayCommand parsedCmd) {
		assert (parsedCmd.getType() != null);

		switch (parsedCmd.getType()) {
		case SCOPE:
			return displayByScope(parsedCmd);
		case SEARCHKEY:
			return displayByName(parsedCmd.getSearchKeyword());
		case SEARCHDATE:
			return displayOnDate(parsedCmd);
		default:
			// should not reach this code
			return feedbackForAction("invalid", null);
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
			return feedbackForAction("invalid", null);
		}
	}

	private Output displayAllTasks() {
		latestDisplayCmd = new DisplayCommand(DisplayCommand.Scope.ALL);
		TaskList sortedTaskList = this.taskList.getDateSortedClone();
		ArrayList<ArrayList<String>> outputList = new ArrayList<ArrayList<String>>();
		Output output = new Output();

		for (int i = 0; i < sortedTaskList.size(); i++) {
			AbstractTask currentTask = sortedTaskList.getTask(i);
			ArrayList<String> taskArray = (currentTask.toArray());
			taskArray.add(0, String.valueOf(i + 1));
			outputList.add(taskArray);
		}
		latestDisplayedList = sortedTaskList;
		output.setOutput(outputList);
		if (outputList.size() < 1) {
			output.setReturnMessage(MESSAGE_DISPLAY_EMPTY);
		} else {
			output.setReturnMessage(MESSAGE_DISPLAY_ALL);
		}
		return output;
	}

	private Output displayFloating() {
		latestDisplayCmd = new DisplayCommand(DisplayCommand.Scope.FLOATING);
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
		latestDisplayedList = filteredList;
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
		latestDisplayCmd = new DisplayCommand(DisplayCommand.Scope.DEFAULT);
		TaskList filteredList = new TaskList();
		TaskList undoneTaskList = this.taskList.filterByStatus(Status.UNDONE);

		// filterByOverdue
		TaskList overdueList = undoneTaskList.filterByOverdue(true);

		if (overdueList.size() > 1) {
			overdueList = overdueList.subList(0, 1);
		}

		TaskList datedTaskList = undoneTaskList
				.filterInclusiveAfterDate(LocalDate.now());
		if (datedTaskList.size() > 9) {
			datedTaskList = datedTaskList.subList(0, 9);
		}
		datedTaskList = datedTaskList.getDateSortedClone();

		TaskList floatingTaskList = new TaskList();
		for (AbstractTask task : undoneTaskList.getTasks()) {
			if (task instanceof FloatingTask) {
				floatingTaskList.addTask(task);
			}
		}
		if (floatingTaskList.size() > 6) {
			floatingTaskList = floatingTaskList.subList(
					floatingTaskList.size() - 6, floatingTaskList.size());
		}
		filteredList.addAll(overdueList);
		filteredList.addAll(datedTaskList);
		filteredList.addAll(floatingTaskList);
		assert (filteredList.size() <= 16);
		latestDisplayedList = filteredList;

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

	// private ArrayList<AbstractTask> loadDefaultOverdue() {
	//
	// }

	private Output displayStatus(Status status) {
		if (status == Status.DONE) {
			latestDisplayCmd = new DisplayCommand(DisplayCommand.Scope.DONE);
		} else {
			latestDisplayCmd = new DisplayCommand(DisplayCommand.Scope.UNDONE);
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
		latestDisplayedList = filteredList;
		output.setOutput(outputList);
		if (outputList.size() < 1) {
			output.setReturnMessage(MESSAGE_DISPLAY_EMPTY);
		} else {
			output.setReturnMessage(String.format(MESSAGE_DISPLAY_STATUS,
					status.toString()));
		}

		return output;
	}

	private Output displayByName(String keyword) {
		latestDisplayCmd = new DisplayCommand(keyword);
		TaskList undoneTaskList = this.taskList.filterByStatus(Status.UNDONE);
		TaskList sortedTaskList = undoneTaskList.getDateSortedClone();
		latestDisplayedList = sortedTaskList.filterByName(keyword);
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
		if (outputList.size() < 1) {
			output.setReturnMessage(MESSAGE_DISPLAY_EMPTY);
		} else {
			output.setReturnMessage(String.format(MESSAGE_DISPLAY_KEYWORD,
					keyword));
		}

		return output;
	}

	private Output displayOnDate(DisplayCommand parsedCmd) {
		latestDisplayCmd = new DisplayCommand(parsedCmd.getSearchDate(),
				DisplayCommand.Type.SEARCHDATE);
		LocalDate queryDate = parsedCmd.getSearchDate().toLocalDate();
		TaskList undoneTaskList = this.taskList.filterByStatus(Status.UNDONE);
		TaskList sortedTaskList = undoneTaskList.getDateSortedClone();
		latestDisplayedList = sortedTaskList.filterByDate(queryDate);
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

	/*
	 * Methods for editing tasks
	 */

	private Output editTask(EditCommand parsedCmd) {
		switch (parsedCmd.getType()) {
		case INDEX:
			return editByIndex(parsedCmd);
		case SEARCHKEYWORD:
			return editByKeyword(parsedCmd);
		default:
			return feedbackForAction("invalid", null);
		}
	}

	private Output editByIndex(EditCommand parsedCmd) {
		assert (parsedCmd.getIndex() > 0);

		if (latestDisplayedList == null) {
			return feedbackForAction("updateError", null);
		}

		if (parsedCmd.getIndex() > latestDisplayedList.size()) {
			return feedbackForAction("invalid", null);
		}
		int inputTaskIndex = parsedCmd.getIndex() - 1;
		AbstractTask taskToEdit = latestDisplayedList.getTask(inputTaskIndex);
		String originalName = taskToEdit.getName();
		int taskIndexInTaskList = taskList.indexOf(taskToEdit);
		AbstractTask actualTaskToEdit = taskList.getTask(taskIndexInTaskList);
		try {
			if (latestEditKeyword != null) {
				performEdit(latestEditKeyword, actualTaskToEdit);
			}
			performEdit(parsedCmd, actualTaskToEdit);
		} catch (IllegalArgumentException e) {
			// Happens when user tries to set start or end date that violates
			// chronological order
			return feedbackForAction(e);
		} catch (ClassCastException e) {
			// Happens when user tries to edit a non-existent field in task
			// e.g. edit start time of floating task
			return feedbackForAction("updateWrongType", null);
		}
		refreshLatestDisplayed();
		recordChange(parsedCmd);
		return feedbackForAction("edit", originalName);

	}

	private Output editByKeyword(EditCommand parsedCmd) {
		String keyword = parsedCmd.getSearchKeyword();
		TaskList filteredList = this.taskList.filterByName(keyword);
		if (filteredList.size() == 0) {
			return feedbackForAction("string!exist", keyword);
		} else if (filteredList.size() == 1
				&& filteredList.getTask(0).getName().equals(keyword)) {
			AbstractTask uniqueTask = filteredList.getTask(0);
			String originalName = uniqueTask.getName();
			try {
				performEdit(parsedCmd, uniqueTask);
			} catch (IllegalArgumentException e) {
				// Happens when user tries to set start or end date that
				// violates chronological order
				return feedbackForAction(e);
			} catch (ClassCastException e) {
				// Happens when user tries to edit a non-existent field in task
				// e.g. edit start time of floating task
				return feedbackForAction("updateWrongType", null);
			}
			refreshLatestDisplayed();
			recordChange(parsedCmd);
			return feedbackForAction("edit", originalName);
		} else {
			// record down additional content given by user
			latestEditKeyword = parsedCmd;
			shouldPreserveEditKeyword = true;
			return displayByName(keyword);
		}
	}

	/*
	 * Helper methods for editing task fields
	 */

	private void performEdit(EditCommand parsedCmd, AbstractTask taskToEdit)
			throws ClassCastException, IllegalArgumentException {
		ArrayList<editField> editFields = parsedCmd.getEditFields();
		if (taskToEdit instanceof BoundedTask) {
			performBoundedEdit(parsedCmd, (BoundedTask) taskToEdit);
			return;
		}
		if (editFields == null) {
			// only happens with two part edit
			return;
		}
		for (int i = 0; i < editFields.size(); i++) {
			try {
				if (editFields.get(i) == editField.NAME) {
					editTaskName(taskToEdit, parsedCmd.getNewName());
				} else if (editFields.get(i) == editField.START_DATE) {
					editStartDate(taskToEdit, parsedCmd.getNewStartDate());
				} else if (editFields.get(i) == editField.START_TIME) {
					editStartTime(taskToEdit, parsedCmd.getNewStartTime());
				} else if (editFields.get(i) == editField.END_DATE) {
					editEndDate(taskToEdit, parsedCmd.getNewEndDate());
				} else if (editFields.get(i) == editField.END_TIME) {
					editEndTime(taskToEdit, parsedCmd.getNewEndTime());
				}
			} catch (Exception e) {
				throw e;
			}
		}
	}

	private void performBoundedEdit(EditCommand parsedCmd,
			BoundedTask taskToEdit) throws ClassCastException,
			IllegalArgumentException {
		ArrayList<editField> editFields = parsedCmd.getEditFields();
		if (editFields == null) {
			// only happens with two part edit
			return;
		}
		LocalDateTime newStart;
		LocalDateTime newEnd;
		DateTimeFormatter DFormatter = DateTimeFormatter
				.ofPattern("dd MM yyyy");
		DateTimeFormatter TFormatter = DateTimeFormatter.ofPattern("HH mm");
		newStart = taskToEdit.getStartDateTime();
		newEnd = taskToEdit.getEndDateTime();
		for (int i = 0; i < editFields.size(); i++) {
			try {

				if (editFields.get(i) == editField.NAME) {
					editTaskName(taskToEdit, parsedCmd.getNewName());
				} else if (editFields.get(i) == editField.START_DATE) {
					LocalDate newDate = LocalDate.parse(
							parsedCmd.getNewStartDate(), DFormatter);
					newStart = newStart.withDayOfMonth(newDate.getDayOfMonth());
					newStart = newStart.withMonth(newDate.getMonthValue());
					newStart = newStart.withYear(newDate.getYear());
				} else if (editFields.get(i) == editField.START_TIME) {
					LocalTime newTime = LocalTime.parse(
							parsedCmd.getNewStartTime(), TFormatter);
					newStart = newStart.withHour(newTime.getHour());
					newStart = newStart.withMinute(newTime.getMinute());
				} else if (editFields.get(i) == editField.END_DATE) {
					LocalDate newDate = LocalDate.parse(
							parsedCmd.getNewEndDate(), DFormatter);
					newEnd = newEnd.withDayOfMonth(newDate.getDayOfMonth());
					newEnd = newEnd.withMonth(newDate.getMonthValue());
					newEnd = newEnd.withYear(newDate.getYear());
				} else if (editFields.get(i) == editField.END_TIME) {
					LocalTime newTime = LocalTime.parse(
							parsedCmd.getNewEndTime(), TFormatter);
					newEnd = newEnd.withHour(newTime.getHour());
					newEnd = newEnd.withMinute(newTime.getMinute());
				}
			} catch (Exception e) {
				throw e;
			}
		}
		if (newStart.isAfter(taskToEdit.getEndDateTime())) {
			taskToEdit.setEndDate(newEnd.format(DFormatter));
			taskToEdit.setEndTime(newEnd.format(TFormatter));
			taskToEdit.setStartDate(newStart.format(DFormatter));
			taskToEdit.setStartTime(newStart.format(TFormatter));
		} else {
			taskToEdit.setStartDate(newStart.format(DFormatter));
			taskToEdit.setStartTime(newStart.format(TFormatter));
			taskToEdit.setEndDate(newEnd.format(DFormatter));
			taskToEdit.setEndTime(newEnd.format(TFormatter));
		}

	}

	private void editTaskName(AbstractTask task, String name) {
		task.setName(name);
	}

	private void editStartDate(AbstractTask task, String startDate)
			throws ClassCastException, IllegalArgumentException {
		((BoundedTask) task).setStartDate(startDate);
	}

	private void editStartTime(AbstractTask task, String startTime)
			throws ClassCastException, IllegalArgumentException {
		((BoundedTask) task).setStartTime(startTime);
	}

	private void editEndDate(AbstractTask task, String endDate)
			throws ClassCastException, IllegalArgumentException {
		if (task instanceof FloatingTask) {
			throw new ClassCastException();
		} else if (task instanceof DeadlineTask) {
			((DeadlineTask) task).setEndDate(endDate);
		} else if (task instanceof BoundedTask) {
			((BoundedTask) task).setEndDate(endDate);
		}
	}

	private void editEndTime(AbstractTask task, String endTime)
			throws ClassCastException, IllegalArgumentException {
		if (task instanceof FloatingTask) {
			throw new ClassCastException();
		} else if (task instanceof DeadlineTask) {
			((DeadlineTask) task).setEndTime(endTime);
		} else if (task instanceof BoundedTask) {
			((BoundedTask) task).setEndTime(endTime);
		}
	}

	/*
	 * Methods for deleting tasks
	 */

	private Output deleteTask(DeleteCommand parsedCmd) {
		switch (parsedCmd.getType()) {
		case INDEX:
			return deleteByIndex(parsedCmd);
		case SEARCHKEYWORD:
			return deleteByKeyword(parsedCmd);
		case SCOPE:
			return deleteByScope(parsedCmd);
		default:
			// should not reach this code
			return feedbackForAction("invalid", null);
		}
	}

	private Output deleteByIndex(DeleteCommand parsedCmd) {
		assert (parsedCmd.getIndex() > 0);

		if (latestDisplayedList == null) {
			return feedbackForAction("deleteError", null);
		}

		if (parsedCmd.getIndex() > latestDisplayedList.size()) {
			return feedbackForAction("invalid", null);
		}
		int indexToDelete = parsedCmd.getIndex() - 1;
		AbstractTask taskToDelete = latestDisplayedList.getTask(indexToDelete);
		String taskName = taskToDelete.getName();
		taskList.removeTask(taskToDelete);
		refreshLatestDisplayed();
		recordChange(parsedCmd);
		return feedbackForAction("singleDelete", taskName);
	}

	private Output deleteByKeyword(DeleteCommand parsedCmd) {
		String keyword = parsedCmd.getSearchKeyword();
		TaskList filteredList = this.taskList.filterByName(keyword);
		if (filteredList.size() == 0) {
			return feedbackForAction("string!exist", keyword);
		} else if (filteredList.size() == 1
				&& filteredList.getTask(0).getName().equals(keyword)) {
			AbstractTask uniqueTask = filteredList.getTask(0);
			taskList.removeTask(uniqueTask);
			refreshLatestDisplayed();
			recordChange(parsedCmd);
			return feedbackForAction("singleDelete", keyword);
		} else {
			return displayByName(keyword);
		}
	}

	private Output deleteByScope(DeleteCommand parsedCmd) {
		switch (parsedCmd.getScope()) {
		case ALL:
			return deleteAllTasks(parsedCmd);
		case DONE:
			return deleteByScope(parsedCmd, Scope.DONE);
		case UNDONE:
			return deleteByScope(parsedCmd, Scope.UNDONE);
		default:
			// should not reach this code
			return feedbackForAction("invalid", null);

		}
	}

	private Output deleteAllTasks(DeleteCommand parsedCmd) {
		taskList.clear();
		refreshLatestDisplayed();
		recordChange(parsedCmd);
		return feedbackForAction("deleteAll", null);
	}

	private Output deleteByScope(DeleteCommand parsedCmd, Scope scope) {
		Status scopeStatus = Status.DONE;
		if (scope == Scope.UNDONE) {
			scopeStatus = Status.UNDONE;
		}
		for (AbstractTask task : this.taskList.getTasks()) {
			if (task.getStatus().equals(scopeStatus)) {
				taskList.removeTask(task);
			}
		}
		refreshLatestDisplayed();
		recordChange(parsedCmd);
		return feedbackForAction("deleteStatus", scopeStatus.toString());
	}

	/*
	 * Methods for marking tasks
	 */

	private Output markTask(MarkCommand parsedCmd) {
		switch (parsedCmd.getType()) {
		case INDEX:
			return markByIndex(parsedCmd);
		case SEARCHKEYWORD:
			return markByKeyword(parsedCmd);
		default:
			// should not reach this code
			return feedbackForAction("invalid", null);
		}
	}

	private Output markByIndex(MarkCommand parsedCmd) {
		assert (parsedCmd.getIndex() > 0);

		if (latestDisplayedList == null) {
			return feedbackForAction("markError", null);
		}
		if (parsedCmd.getIndex() > latestDisplayedList.size()) {
			return feedbackForAction("invalid", null);
		}

		int inputTaskIndex = parsedCmd.getIndex() - 1;
		AbstractTask displayTaskToMark = latestDisplayedList
				.getTask(inputTaskIndex);
		String taskName = displayTaskToMark.getName();
		int taskIndexInTaskList = this.taskList.indexOf(displayTaskToMark);
		AbstractTask actualTaskToMark = this.taskList.getTask(taskIndexInTaskList);

		Output feedback = feedbackForAction("markUndone", taskName);
		Status newStatus = Status.UNDONE;
		if (parsedCmd.getMarkField().equals(markField.MARK)) {
			newStatus = Status.DONE;
			feedback = feedbackForAction("markDone", taskName);
		}
		actualTaskToMark.setStatus(newStatus);
		removeOverdue(actualTaskToMark);
		refreshLatestDisplayed();
		recordChange(parsedCmd);
		return feedback;
	}

	private Output markByKeyword(MarkCommand parsedCmd) {
		String keyword = parsedCmd.getSearchKeyword();

		Output feedback = feedbackForAction("markUndone", keyword);
		Status newStatus = Status.UNDONE;
		if (parsedCmd.getMarkField().equals(markField.MARK)) {
			newStatus = Status.DONE;
			feedback = feedbackForAction("markDone", keyword);
		}

		TaskList filteredList = this.taskList.filterByName(keyword);
		if (filteredList.size() == 0) {
			return feedbackForAction("string!exist", keyword);
		} else if (filteredList.size() == 1
				&& filteredList.getTask(0).getName().equals(keyword)) {
			AbstractTask uniqueTask = filteredList.getTask(0);
			uniqueTask.setStatus(newStatus);
			removeOverdue(uniqueTask);
			refreshLatestDisplayed();
			recordChange(parsedCmd);
			return feedback;
		} else {
			return displayByName(keyword);
		}
	}

	/*
	 * Methods for Undo Functionality
	 */

	private Output undoPreviousAction() {
		if (taskListStack.size() == 1) {
			// Earliest recorded version for current run of program
			return feedbackForAction("invalid", null);
		} else {
			taskListStack.pop();
			this.taskList = (taskListStack.peek()).clone();
			refreshLatestDisplayed();
			storage.write(this.taskList.getTasks());
			AbstractCommand undoneCommand = cmdHistoryStack.pop();
			String undoMessage = undoneCommand.getUndoMessage();
			return feedbackForAction("undo", undoMessage);
		}
	}

	private Output setPath(SaveCommand parsedCmd) {
		boolean isValidPath = storage.setPath(parsedCmd.getPath());
		if (isValidPath) {
			return feedbackForAction("validPath", parsedCmd.getPath());
		} else {
			return feedbackForAction("invalidPath", parsedCmd.getPath());
		}
	}

	/*
	 * Feedback management
	 */

	// Constructs return messages for create, edit and delete commands
	private static Output feedbackForAction(String action, String content) {
		Output output = new Output();
		String returnMessage;

		switch (action) {
		case "create":
			returnMessage = getReturnMessage(MESSAGE_CREATION, content);
			output.setReturnMessage(returnMessage);
			break;
		case "edit":
			returnMessage = getReturnMessage(MESSAGE_UPDATE, content);
			output.setReturnMessage(returnMessage);
			break;
		case "updateWrongType":
			output.setPriority(Priority.HIGH);
			output.setReturnMessage(MESSAGE_UPDATE_WRONG_TYPE);
			break;
		case "singleDelete":
			output.setPriority(Priority.HIGH);
			returnMessage = getReturnMessage(MESSAGE_SINGLE_DELETION, content);
			output.setReturnMessage(returnMessage);
			break;
		case "deleteAll":
			output.setPriority(Priority.HIGH);
			output.setReturnMessage(MESSAGE_ALL_DELETION);
			break;
		case "deleteStatus":
			output.setPriority(Priority.HIGH);
			returnMessage = getReturnMessage(MESSAGE_STATUS_DELETION, content);
			output.setReturnMessage(returnMessage);
			break;
		case "markUndone":
			returnMessage = getReturnMessage(MESSAGE_MARK, content, "undone");
			output.setReturnMessage(returnMessage);
			break;
		case "markDone":
			returnMessage = getReturnMessage(MESSAGE_MARK, content, "done");
			output.setReturnMessage(returnMessage);
			break;
		case "undo":
			output.setReturnMessage(content);
			break;
		case "validPath":
			output.setReturnMessage(String.format(MESSAGE_SAVEPATH, content));
			break;
		case "invalidPath":
			output.setPriority(Priority.HIGH);
			output.setReturnMessage(String.format(MESSAGE_SAVEPATH_FAIL,
					content));
			break;
		case "invalid":
			output.setPriority(Priority.HIGH);
			output.setReturnMessage(MESSAGE_INVALID_COMMAND);
			break;
		case "string!exist":
			returnMessage = getReturnMessage(MESSAGE_INVALID_KEYWORD, content);
			output.setReturnMessage(returnMessage);
			break;
		case "date!exist":
			returnMessage = getReturnMessage(MESSAGE_INVALID_DATE, content);
			output.setReturnMessage(returnMessage);
			break;
		case "emptyString":
			output.setReturnMessage("");
			break;
		}

		return output;
	}

	private static String getReturnMessage(String template, String content) {
		int ellipsisLength = 3;
		String ellipsis = "...";

		int templateLength = String.format(template, "").length();
		int contentLength = content.length();

		if (templateLength + contentLength < MESSAGE_LENGTH) {
			return String.format(template, content);
		} else {
			int newContentLength = MESSAGE_LENGTH - templateLength
					- ellipsisLength;
			String newContent = content.substring(0, newContentLength)
					+ ellipsis;
			return String.format(template, newContent);
		}
	}

	private static String getReturnMessage(String template, String content1,
			String content2) {
		int ellipsisLength = 3;
		String ellipsis = "...";

		int templateLength = String.format(template, "", content2).length();
		int contentLength = content1.length();

		if (templateLength + contentLength < MESSAGE_LENGTH) {
			return String.format(template, content1, content2);
		} else {
			int newContentLength = MESSAGE_LENGTH - templateLength
					- ellipsisLength;
			String newContent = content1.substring(0, newContentLength)
					+ ellipsis;
			return String.format(template, newContent, content2);
		}
	}

	private Output feedbackForAction(Exception e) {
		Output output = new Output();
		output.setReturnMessage(e.getMessage());
		output.setPriority(Priority.HIGH);
		return output;
	}

	/*
	 * Overdue functionality
	 */
	// Ask for preferred version of if statements, nesting versus &&
	private void updateOverdueStatus() {
		LocalDateTime dateTimeNow = LocalDateTime.now();
		for (AbstractTask task : this.taskList.getTasks()) {
			if (task instanceof DeadlineTask) {
				DeadlineTask deadlineTask = (DeadlineTask) task;
				if (dateTimeNow.isAfter(deadlineTask.getEndDateTime())) {
					if (deadlineTask.getStatus().equals(Status.UNDONE)) {
						deadlineTask.setOverdue(true);
					}
				}
			}
		}
	}

	private void removeOverdue(AbstractTask task) {
		if (task instanceof DeadlineTask) {
			DeadlineTask deadlineTask = (DeadlineTask) task;
			deadlineTask.setOverdue(false);
		}
	}

	/*
	 * Protected methods for testing
	 */

	protected TaskList getTaskListTest() {
		return this.taskList;
	}

	protected void setTaskListTest(TaskList taskArray) {
		this.taskList = taskArray;
	}

	protected void setLastDisplayed(TaskList taskArray) {
		this.latestDisplayedList = taskArray;
	}

	protected TaskList getLastDisplayedTest() {
		return this.latestDisplayedList;
	}

	protected void setTaskListStack(Stack<TaskList> stack) {
		this.taskListStack = stack;
	}

	protected Stack<TaskList> getTaskListStackTest() {
		return this.taskListStack;
	}

	/*
	 * Methods for UI Observer
	 */

	public Output getLastDisplayed() {
		ArrayList<ArrayList<String>> outputList = new ArrayList<ArrayList<String>>();
		Output output = new Output();

		for (int i = 0; i < latestDisplayedList.size(); i++) {
			AbstractTask currentTask = latestDisplayedList.getTask(i);
			ArrayList<String> taskArray = (currentTask.toArray());
			taskArray.add(0, String.valueOf(i + 1));
			outputList.add(taskArray);
		}
		output.setOutput(outputList);
		return output;
	}

	public Output loadDefaultView() {
		latestDisplayCmd = new DisplayCommand(DisplayCommand.Scope.DEFAULT);
		return displayDefault();
	}

}
