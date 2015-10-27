package logic;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import shared.command.UndoCommand;
import shared.task.AbstractTask;
import shared.task.BoundedTask;
import shared.task.DeadlineTask;
import shared.task.FloatingTask;
import shared.task.AbstractTask.Status;
import storage.Storage;

public class Logic implements LogicInterface {

	// Templates for program feedback
	private static final String MESSAGE_CREATION = "\"%1$s\" has been successfully created!";
	private static final String MESSAGE_UPDATE = "\"%1$s\" has been successfully edited!";
	private static final String MESSAGE_UPDATE_ERROR = "Please display tasks at least once to edit by index.";
	private static final String MESSAGE_UPDATE_WRONG_TYPE = "Invalid: Task specified does not have this operation.";
	private static final String MESSAGE_SINGLE_DELETION = "\"%1$s\" has been deleted!";
	private static final String MESSAGE_ALL_DELETION = "All tasks have been deleted!";
	private static final String MESSAGE_STATUS_DELETION = "All %1$s tasks have been deleted!";
	private static final String MESSAGE_DELETION_ERROR = "Please display tasks at least once to delete by index.";
	private static final String MESSAGE_MARK = "\"%1$s\" has been marked %2$s.";
	private static final String MESSAGE_MARK_ERROR = "Please display tasks at least once to mark by index.";
	private static final String MESSAGE_INVALID_COMMAND = "Invalid Command!";
	private static final String MESSAGE_DISPLAY_ALL = "All tasks are now displayed!";
	private static final String MESSAGE_DISPLAY_EMPTY = "There are no tasks to display :'(";
	private static final String MESSAGE_DISPLAY_FLOATING = "All floating tasks are now displayed!";
	private static final String MESSAGE_DISPLAY_DEFAULT = "Welcome to Flexi-List!";
	private static final String MESSAGE_DISPLAY_STATUS = "All tasks that are %1$s are now displayed!";
	private static final String MESSAGE_DISPLAY_KEYWORD = "All tasks with keyword \"%1$s\" are now displayed!";
	private static final String MESSAGE_DISPLAY_DATE = "All tasks with date \"%1$s\" are now displayed!";
	private static final String MESSAGE_INVALID_KEYWORD = "No task with keyword \"%1$s\" has been found.";
	private static final String MESSAGE_INVALID_DATE = "No task with date \"%1$s\" has been found.";

	// Data structure for tasks
	private ArrayList<AbstractTask> taskList = new ArrayList<AbstractTask>();

	// Data structure for last displayed list
	private ArrayList<AbstractTask> latestDisplayedList = null;

	// Data structure for Undo Functionality
	private Stack<ArrayList<AbstractTask>> taskListStack = new Stack<ArrayList<AbstractTask>>();
	private Stack<AbstractCommand> commandHistoryStack = new Stack<AbstractCommand>();

	private DisplayCommand latestDisplayCommand = null;
	private EditCommand latestEditKeyword = null;
	private boolean shouldPreserveEditKeyword = false;

	private static Parser parser = new Parser();

	private static Storage storage = new Storage();

	public Logic() {
		loadFromStorage();
		loadStateForUndo();
	}

	public Output processInput(String userCommand) {
		AbstractCommand parsedCommand = parser.parseInput(userCommand);
		return executeCommand(parsedCommand);
	}

	protected Output executeCommand(AbstractCommand parsedCommand) {
		checkEditKeywordPreservation();

		if (parsedCommand instanceof CreateCommand) {
			return createTask((CreateCommand) parsedCommand);
		} else if (parsedCommand instanceof DisplayCommand) {
			return displayTasks((DisplayCommand) parsedCommand);
		} else if (parsedCommand instanceof EditCommand) {
			return editTask((EditCommand) parsedCommand);
		} else if (parsedCommand instanceof DeleteCommand) {
			return deleteTask((DeleteCommand) parsedCommand);
		} else if (parsedCommand instanceof MarkCommand) {
			return markTask((MarkCommand) parsedCommand);
		} else if (parsedCommand instanceof UndoCommand) {
			return undoPreviousAction();
		} else if (parsedCommand instanceof InvalidCommand) {
			return feedbackForAction("invalid", null);
		} else if (parsedCommand instanceof ExitCommand) {
			System.exit(0);
		} else {
			return feedbackForAction("invalid", null);
		}
		return null;

	}

	private void loadFromStorage() {
		taskList = storage.read();
	}
	
	private void loadStateForUndo() {
		taskListStack.push((ArrayList<AbstractTask>)taskList.clone());
	}

	private void checkEditKeywordPreservation() {
		if (!shouldPreserveEditKeyword) {
			latestEditKeyword = null;
		}
		shouldPreserveEditKeyword = false;
	}

	private void recordChange(AbstractCommand parsedCommand) {
		storage.write(taskList);
		ArrayList<AbstractTask> snapshotList = (ArrayList<AbstractTask>) this.taskList.clone();
		this.taskListStack.push(snapshotList);
		this.commandHistoryStack.push(parsedCommand);
	}

	private void refreshLatestDisplayed() {
		executeCommand(latestDisplayCommand);
	}

	/*
	 * Methods for task creation
	 */

	private Output createTask(CreateCommand parsedCommand) {

		switch (parsedCommand.getTaskType()) {
		case FLOATING:
			return createFloatingTask(parsedCommand);
		case DEADLINE:
			return createDeadlineTask(parsedCommand);
		case BOUNDED:
			return createBoundedTask(parsedCommand);
		default:
			return feedbackForAction("invalid", null);
		}
	}

	private Output createFloatingTask(CreateCommand parsedCommand) {
		FloatingTask newFloatingTask = new FloatingTask(
				parsedCommand.getTaskName());
		taskList.add(newFloatingTask);
		refreshLatestDisplayed();
		recordChange(parsedCommand);
		return feedbackForAction("create", parsedCommand.getTaskName());
	}

	private Output createDeadlineTask(CreateCommand parsedCommand) {
		DeadlineTask newDeadlineTask = new DeadlineTask(
				parsedCommand.getTaskName(), parsedCommand.getEndDateTime());
		taskList.add(newDeadlineTask);
		refreshLatestDisplayed();
		recordChange(parsedCommand);
		return feedbackForAction("create", parsedCommand.getTaskName());
	}

	private Output createBoundedTask(CreateCommand parsedCommand) {
		try {
			BoundedTask newBoundedTask = new BoundedTask(parsedCommand.getTaskName(),
					parsedCommand.getStartDateTime(),
					parsedCommand.getEndDateTime());
			taskList.add(newBoundedTask);
		} catch (IllegalArgumentException e) {
			return feedbackForAction(e);
		}
		refreshLatestDisplayed();
		recordChange(parsedCommand);
		return feedbackForAction("create", parsedCommand.getTaskName());
	}

	/*
	 * Methods for displaying tasks
	 */

	private Output displayTasks(DisplayCommand parsedCommand) {
		assert (parsedCommand.getType() != null);

		switch (parsedCommand.getType()) {
		case SCOPE:
			return displayByScope(parsedCommand);
		case SEARCHKEY:
			return displayByName(parsedCommand.getSearchKeyword());
		case SEARCHDATE:
			return displayByDate(parsedCommand);
		default:
			// should not reach this code
			return feedbackForAction("invalid", null);
		}
	}

	private Output displayByScope(DisplayCommand parsedCommand) {
		assert (parsedCommand.getScope() != null);

		switch (parsedCommand.getScope()) {
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
		latestDisplayCommand = new DisplayCommand(DisplayCommand.Scope.ALL);
		latestDisplayedList = taskList;
		ArrayList<ArrayList<String>> outputList = new ArrayList<ArrayList<String>>();
		Output output = new Output();

		for (int i = 0; i < taskList.size(); i++) {
			AbstractTask currentTask = taskList.get(i);
			ArrayList<String> taskArray = (currentTask.toArray());
			taskArray.add(0, String.valueOf(i + 1));
			outputList.add(taskArray);
		}
		output.setOutput(outputList);
		if (outputList.size() < 1) {
			output.setReturnMessage(MESSAGE_DISPLAY_EMPTY);
		} else {
			output.setReturnMessage(MESSAGE_DISPLAY_ALL);
		}
		return output;
	}

	private Output displayFloating() {
		latestDisplayCommand = new DisplayCommand(DisplayCommand.Scope.FLOATING);
		ArrayList<ArrayList<String>> outputList = new ArrayList<ArrayList<String>>();
		ArrayList<AbstractTask> filteredList = new ArrayList<AbstractTask>();
		Output output = new Output();

		for (int i = 0; i < taskList.size(); i++) {
			AbstractTask currentTask = taskList.get(i);
			if (currentTask instanceof FloatingTask) {
				filteredList.add(currentTask);
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

	// Creates default view of 7 timed tasks closest to current date and 3 of
	// the newest floating tasks
	// 1. Get 7 timed tasks using filterAfterDate()
	// 2. Get 3 of the newest floating tasks by traversing taskList from behind
	private Output displayDefault() {
		latestDisplayCommand = new DisplayCommand(DisplayCommand.Scope.DEFAULT);
		ArrayList<AbstractTask> filteredList = filterInclusiveAfterDate(
				taskList, LocalDate.now());
		if (filteredList.size() > 7) {
			List<AbstractTask> size7List = filteredList.subList(0, 7);
			filteredList = new ArrayList<AbstractTask>(size7List);
		}
		ArrayList<AbstractTask> floatingTaskList = new ArrayList<AbstractTask>();
		for (AbstractTask task : taskList) {
			if (task instanceof FloatingTask) {
				floatingTaskList.add(task);
			}
		}
		if (floatingTaskList.size() > 3) {
			List<AbstractTask> size3List = floatingTaskList.subList(
					floatingTaskList.size() - 3, floatingTaskList.size());
			floatingTaskList = new ArrayList<AbstractTask>(size3List);
		}
		filteredList.addAll(floatingTaskList);
		assert (filteredList.size() <= 11);
		latestDisplayedList = filteredList;

		ArrayList<ArrayList<String>> outputList = new ArrayList<ArrayList<String>>();
		Output output = new Output();

		int i = 1;
		for (AbstractTask task : filteredList) {
			ArrayList<String> taskArray = (task.toArray());
			taskArray.add(0, String.valueOf(i));
			outputList.add(taskArray);
			i++;
		}
		
		// Marker for UI to separate default panel in DatedTasks and FloatingTasks
		ArrayList<String> floatingTaskMarker = new ArrayList<String>();
		floatingTaskMarker.add("");
//		outputList.add(filteredList.size() - floatingTaskList.size(), floatingTaskMarker);

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
			latestDisplayCommand = new DisplayCommand(DisplayCommand.Scope.DONE);
		} else {
			latestDisplayCommand = new DisplayCommand(
					DisplayCommand.Scope.UNDONE);
		}
		ArrayList<ArrayList<String>> outputList = new ArrayList<ArrayList<String>>();
		ArrayList<AbstractTask> filteredList = new ArrayList<AbstractTask>();
		Output output = new Output();

		for (int i = 0; i < taskList.size(); i++) {
			AbstractTask currentTask = taskList.get(i);
			if (currentTask.getStatus() == status) {
				filteredList.add(currentTask);
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
		latestDisplayCommand = new DisplayCommand(keyword);
		latestDisplayedList = filterByName(taskList, keyword);
		ArrayList<ArrayList<String>> outputList = new ArrayList<ArrayList<String>>();
		Output output = new Output();

		int i = 1;
		for (AbstractTask task : latestDisplayedList) {
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

	private Output displayByDate(DisplayCommand parsedCommand) {
		latestDisplayCommand = new DisplayCommand(
				parsedCommand.getSearchDate(), DisplayCommand.Type.SEARCHDATE);
		LocalDate queryDate = parsedCommand.getSearchDate().toLocalDate();
		latestDisplayedList = filterByDate(taskList, queryDate);
		ArrayList<ArrayList<String>> outputList = new ArrayList<ArrayList<String>>();
		Output output = new Output();

		int i = 1;
		for (AbstractTask task : latestDisplayedList) {
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

	private Output editTask(EditCommand parsedCommand) {
		switch (parsedCommand.getType()) {
		case INDEX:
			return editByIndex(parsedCommand);
		case SEARCHKEYWORD:
			return editByKeyword(parsedCommand);
		default:
			return feedbackForAction("invalid", null);
		}
	}

	private Output editByIndex(EditCommand parsedCommand) {
		assert (parsedCommand.getIndex() > 0);

		if (latestDisplayedList == null) {
			return feedbackForAction("updateError", null);
		}

		if (parsedCommand.getIndex() > latestDisplayedList.size()) {
			return feedbackForAction("invalid", null);
		}
		int taskIndex = parsedCommand.getIndex() - 1;
		AbstractTask taskToEdit = latestDisplayedList.get(taskIndex);
		String originalName = taskToEdit.getName();
		try {
			if (latestEditKeyword != null) {
				performEdit(latestEditKeyword, taskToEdit);
			}
			performEdit(parsedCommand, taskToEdit);
		} catch (IllegalArgumentException e) {
			// Happens when user tries to set start or end date that violates chronological order
			return feedbackForAction(e);
		} catch (ClassCastException e) {
			// Happens when user tries to edit a non-existent field in task
			// e.g. edit start time of floating task
			return feedbackForAction("updateWrongType", null);
		}
		recordChange(parsedCommand);
		return feedbackForAction("edit", originalName);

	}

	private Output editByKeyword(EditCommand parsedCommand) {
		String keyword = parsedCommand.getSearchKeyword();
		ArrayList<AbstractTask> filteredList = filterByName(taskList, keyword);
		if (filteredList.size() == 0) {
			return feedbackForAction("string!exist", keyword);
		} else if (filteredList.size() == 1
				&& filteredList.get(0).getName().equals(keyword)) {
			AbstractTask uniqueTask = filteredList.get(0);
			String originalName = uniqueTask.getName();
			try {
				performEdit(parsedCommand, uniqueTask);
			} catch (IllegalArgumentException e) {
				// Happens when user tries to set start or end date that violates chronological order
				return feedbackForAction(e);
			} catch (ClassCastException e) {
				// Happens when user tries to edit a non-existent field in task
				// e.g. edit start time of floating task
				return feedbackForAction("updateWrongType", null);
			}
			recordChange(parsedCommand);
			return feedbackForAction("edit", originalName);
		} else {
			// record down additional content given by user
			latestEditKeyword = parsedCommand;
			shouldPreserveEditKeyword = true;
			return displayByName(keyword);
		}
	}

	/*
	 * Helper methods for editing task fields
	 */

	private void performEdit(EditCommand parsedCommand, AbstractTask taskToEdit)
			throws ClassCastException, IllegalArgumentException {
		ArrayList<editField> editFields = parsedCommand.getEditFields();
		if (editFields == null) {
			// only happens with two part edit
			return;
		}
		for (int i = 0; i < editFields.size(); i++) {
			try{
				if (editFields.get(i) == editField.NAME) {
					editTaskName(taskToEdit, parsedCommand.getNewName());
				} else if (editFields.get(i) == editField.START_DATE) {
					editStartDate(taskToEdit, parsedCommand.getNewStartDate());
				} else if (editFields.get(i) == editField.START_TIME) {
					editStartTime(taskToEdit, parsedCommand.getNewStartTime());
				} else if (editFields.get(i) == editField.END_DATE) {
					editEndDate(taskToEdit, parsedCommand.getNewEndDate());
				} else if (editFields.get(i) == editField.END_TIME) {
					editEndTime(taskToEdit, parsedCommand.getNewEndTime());
				} 
			} catch (Exception e) {
				throw e;
			}
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

	private Output deleteTask(DeleteCommand parsedCommand) {
		switch (parsedCommand.getType()) {
		case INDEX:
			return deleteByIndex(parsedCommand);
		case SEARCHKEYWORD:
			return deleteByKeyword(parsedCommand);
		case SCOPE:
			return deleteByScope(parsedCommand);
		default:
			// should not reach this code
			return feedbackForAction("invalid", null);
		}
	}

	private Output deleteByIndex(DeleteCommand parsedCommand) {
		assert (parsedCommand.getIndex() > 0);

		if (latestDisplayedList == null) {
			return feedbackForAction("deleteError", null);
		}

		if (parsedCommand.getIndex() > latestDisplayedList.size()) {
			return feedbackForAction("invalid", null);
		}
		int taskIndex = parsedCommand.getIndex() - 1;
		AbstractTask taskToDelete = latestDisplayedList.get(taskIndex);
		String taskName = taskToDelete.getName();
		latestDisplayedList.remove(taskToDelete);
		taskList.remove(taskToDelete);
		recordChange(parsedCommand);
		return feedbackForAction("singleDelete", taskName);
	}

	private Output deleteByKeyword(DeleteCommand parsedCommand) {
		String keyword = parsedCommand.getSearchKeyword();
		ArrayList<AbstractTask> filteredList = filterByName(taskList, keyword);
		if (filteredList.size() == 0) {
			return feedbackForAction("string!exist", keyword);
		} else if (filteredList.size() == 1
				&& filteredList.get(0).getName().equals(keyword)) {
			AbstractTask uniqueTask = filteredList.get(0);
			taskList.remove(uniqueTask);
			recordChange(parsedCommand);
			return feedbackForAction("singleDelete", keyword);
		} else {
			return displayByName(keyword);
		}
	}

	private Output deleteByScope(DeleteCommand parsedCommand) {
		switch (parsedCommand.getScope()) {
		case ALL:
			return deleteAllTasks(parsedCommand);
		case DONE:
			return deleteByScope(parsedCommand, Scope.DONE);
		case UNDONE:
			return deleteByScope(parsedCommand, Scope.UNDONE);
		default:
			// should not reach this code
			return feedbackForAction("invalid", null);

		}
	}

	private Output deleteAllTasks(DeleteCommand parsedCommand) {
		taskList.clear();
		recordChange(parsedCommand);
		return feedbackForAction("deleteAll", null);
	}

	private Output deleteByScope(DeleteCommand parsedCommand, Scope scope) {
		Status scopeStatus = Status.DONE;
		if (scope == Scope.UNDONE) {
			scopeStatus = Status.UNDONE;
		}
		for (AbstractTask task : taskList) {
			if (task.getStatus().equals(scopeStatus)) {
				taskList.remove(task);
			}
		}
		recordChange(parsedCommand);
		return feedbackForAction("deleteStatus", scopeStatus.toString());
	}

	/*
	 * Methods for marking tasks
	 */

	private Output markTask(MarkCommand parsedCommand) {
		switch (parsedCommand.getType()) {
		case INDEX:
			return markByIndex(parsedCommand);
		case SEARCHKEYWORD:
			return markByKeyword(parsedCommand);
		default:
			// should not reach this code
			return feedbackForAction("invalid", null);
		}
	}

	private Output markByIndex(MarkCommand parsedCommand) {
		assert (parsedCommand.getIndex() > 0);

		if (latestDisplayedList == null) {
			return feedbackForAction("markError", null);
		}
		if (parsedCommand.getIndex() > latestDisplayedList.size()) {
			return feedbackForAction("invalid", null);
		}

		int taskIndex = parsedCommand.getIndex() - 1;
		AbstractTask taskToMark = latestDisplayedList.get(taskIndex);
		String taskName = taskToMark.getName();

		Output feedback = feedbackForAction("markUndone", taskName);
		Status newStatus = Status.UNDONE;
		if (parsedCommand.getMarkField().equals(markField.MARK)) {
			newStatus = Status.DONE;
			feedback = feedbackForAction("markDone", taskName);
		}
		taskToMark.setStatus(newStatus);
		recordChange(parsedCommand);
		return feedback;
	}

	private Output markByKeyword(MarkCommand parsedCommand) {
		String keyword = parsedCommand.getSearchKeyword();

		Output feedback = feedbackForAction("markUndone", keyword);
		Status newStatus = Status.UNDONE;
		if (parsedCommand.getMarkField().equals(markField.MARK)) {
			newStatus = Status.DONE;
			feedback = feedbackForAction("markDone", keyword);
		}

		ArrayList<AbstractTask> filteredList = filterByName(taskList, keyword);
		if (filteredList.size() == 0) {
			return feedbackForAction("string!exist", keyword);
		} else if (filteredList.size() == 1
				&& filteredList.get(0).getName().equals(keyword)) {
			AbstractTask uniqueTask = filteredList.get(0);
			uniqueTask.setStatus(newStatus);
			recordChange(parsedCommand);
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
			taskList = taskListStack.peek();
			refreshLatestDisplayed();
			storage.write(taskList);
//			ArrayList<AbstractTask> snapshotList = (ArrayList<AbstractTask>) taskList.clone();
//			this.taskListStack.push(snapshotList);
			AbstractCommand undoneCommand = commandHistoryStack.pop();
			String undoMessage = undoneCommand.getUndoMessage();
			return feedbackForAction("undo", undoMessage);
		}
	}

	/*
	 * Helper Methods for SLAP
	 */

	// Constructs return messages for create, edit and delete commands
	private static Output feedbackForAction(String action, String content) {
		Output output = new Output();

		switch (action) {
		case "create":
			output.setReturnMessage(String.format(MESSAGE_CREATION, content));
			break;
		case "edit":
			output.setReturnMessage(String.format(MESSAGE_UPDATE, content));
			break;
		case "updateError":
			output.setPriority(Priority.HIGH);
			output.setReturnMessage(MESSAGE_UPDATE_ERROR);
			break;
		case "updateWrongType":
			output.setPriority(Priority.HIGH);
			output.setReturnMessage(MESSAGE_UPDATE_WRONG_TYPE);
			break;
		case "singleDelete":
			output.setPriority(Priority.HIGH);
			output.setReturnMessage(String.format(MESSAGE_SINGLE_DELETION,
					content));
			break;
		case "deleteError":
			output.setPriority(Priority.HIGH);
			output.setReturnMessage(MESSAGE_DELETION_ERROR);
			break;
		case "deleteAll":
			output.setPriority(Priority.HIGH);
			output.setReturnMessage(MESSAGE_ALL_DELETION);
			break;
		case "deleteStatus":
			output.setPriority(Priority.HIGH);
			output.setReturnMessage(String.format(MESSAGE_STATUS_DELETION,
					content));
			break;
		case "markUndone":
			output.setReturnMessage(String.format(MESSAGE_MARK, content,
					"undone"));
			break;
		case "markDone":
			output.setReturnMessage(String
					.format(MESSAGE_MARK, content, "done"));
			break;
		case "markError":
			output.setPriority(Priority.HIGH);
			output.setReturnMessage(MESSAGE_MARK_ERROR);
			break;
		case "undo":
			output.setReturnMessage(content);
			break;
		case "invalid":
			output.setPriority(Priority.HIGH);
			output.setReturnMessage(String.format(MESSAGE_INVALID_COMMAND,
					content));
			break;
		case "string!exist":
			output.setReturnMessage(String.format(MESSAGE_INVALID_KEYWORD,
					content));
			break;
		case "date!exist":
			output.setReturnMessage(String
					.format(MESSAGE_INVALID_DATE, content));
			break;
		}

		return output;
	}
	
	private Output feedbackForAction(Exception e) {
		Output output = new Output();
		output.setReturnMessage(e.getMessage());
		output.setPriority(Priority.HIGH);
		return output;
	}

	private ArrayList<AbstractTask> filterByName(
			ArrayList<AbstractTask> masterList, String keyword) {
		ArrayList<AbstractTask> filteredList = new ArrayList<AbstractTask>();
		for (AbstractTask task : masterList) {
			if (task.getName().contains(keyword)) {
				filteredList.add(task);
			}
		}
		return filteredList;
	}

	private ArrayList<AbstractTask> filterByDate(
			ArrayList<AbstractTask> masterList, LocalDate queryDate) {
		ArrayList<AbstractTask> filteredList = new ArrayList<AbstractTask>();
		for (AbstractTask task : masterList) {
			if (task instanceof DeadlineTask
					&& isSameDate((DeadlineTask) task, queryDate)) {
				filteredList.add(task);
			} else if (task instanceof BoundedTask
					&& isSameDate((BoundedTask) task, queryDate)) {
				filteredList.add(task);
			}
		}
		return filteredList;
	}

	private ArrayList<AbstractTask> filterInclusiveAfterDate(
			ArrayList<AbstractTask> masterList, LocalDate queryDate) {
		ArrayList<AbstractTask> filteredList = new ArrayList<AbstractTask>();
		for (AbstractTask task : masterList) {
			if (task instanceof DeadlineTask
					&& isInclusiveAfterDate((DeadlineTask) task, queryDate)) {
				filteredList.add(task);
			} else if (task instanceof BoundedTask
					&& isInclusiveAfterDate((BoundedTask) task, queryDate)) {
				filteredList.add(task);
			}
		}
		return filteredList;
	}

	private boolean isSameDate(DeadlineTask task, LocalDate queryDate) {
		return Objects.equals(task.getEndDateTime().toLocalDate(), queryDate);
	}

	private boolean isSameDate(BoundedTask task, LocalDate queryDate) {
		boolean startDateCheck = Objects.equals(task.getStartDateTime()
				.toLocalDate(), queryDate);
		boolean endDateCheck = Objects.equals(task.getEndDateTime()
				.toLocalDate(), queryDate);
		return startDateCheck || endDateCheck;
	}

	private boolean isInclusiveAfterDate(DeadlineTask task, LocalDate queryDate) {
		return task.getEndDateTime().toLocalDate().isAfter(queryDate)
				|| isSameDate(task, queryDate);
	}

	private boolean isInclusiveAfterDate(BoundedTask task, LocalDate queryDate) {
		return task.getStartDateTime().toLocalDate().isAfter(queryDate)
				|| isSameDate(task, queryDate);
	}

	/*
	 * Public methods for testing
	 */

	protected ArrayList<AbstractTask> getTaskListTest() {
		return this.taskList;
	}

	protected void setTaskListTest(ArrayList<AbstractTask> taskArray) {
		this.taskList = taskArray;
	}

	protected void setLastDisplayed(ArrayList<AbstractTask> taskArray) {
		this.latestDisplayedList = taskArray;
	}

	protected ArrayList<AbstractTask> getLastDisplayedTest() {
		return this.latestDisplayedList;
	}
	
	protected void setTaskListStack(Stack<ArrayList<AbstractTask>> stack) {
		this.taskListStack = stack;
	}

	/*
	 * Method for UI Observer
	 */

	public Output getLastDisplayed() {
		ArrayList<ArrayList<String>> outputList = new ArrayList<ArrayList<String>>();
		Output output = new Output();

		for (int i = 0; i < latestDisplayedList.size(); i++) {
			AbstractTask currentTask = latestDisplayedList.get(i);
			ArrayList<String> taskArray = (currentTask.toArray());
			taskArray.add(0, String.valueOf(i + 1));
			outputList.add(taskArray);
		}
		output.setOutput(outputList);
		return output;
	}

	public Output loadDefaultView() {
		return displayDefault();
	}

}
