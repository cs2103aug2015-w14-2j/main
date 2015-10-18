package logic;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

import parser.AbstractCommand;
import parser.CreateCommand;
import parser.DeleteCommand;
import parser.DeleteCommand.Scope;
import parser.DisplayCommand;
import parser.EditCommand;
import parser.EditCommand.editField;
import parser.ExitCommand;
import parser.InvalidCommand;
import parser.Parser;
import shared.AbstractTask;
import shared.AbstractTask.Status;
import shared.BoundedTask;
import shared.DeadlineTask;
import shared.FloatingTask;
import shared.Output;
import storage.Storage;

public class Logic implements LogicInterface {

	// Templates for program feedback
	private static final String MESSAGE_CREATION = "\"%1$s\" has been successfully created!";
	private static final String MESSAGE_UPDATE_ERROR = "Please display tasks at least once to edit by index.";
	private static final String MESSAGE_SINGLE_DELETION = "\"%1$s\" has been deleted!";
	private static final String MESSAGE_ALL_DELETION = "All tasks have been deleted!";
	private static final String MESSAGE_STATUS_DELETION = "All %1$s tasks have been deleted!";
	private static final String MESSAGE_DELETION_ERROR = "Please display tasks at least once to delete by index.";
	private static final String MESSAGE_INVALID_COMMAND = "Invalid Command!";
	private static final String MESSAGE_DISPLAY_ALL = "All tasks are now displayed!";
	private static final String MESSAGE_DISPLAY_STATUS = "All tasks that are %1$s are now displayed!";
	private static final String MESSAGE_DISPLAY_KEYWORD = "All tasks with keyword \"%1$s\" are now displayed!";
	private static final String MESSAGE_DISPLAY_DATE = "All tasks with date \"%1$s\" are now displayed!";
	private static final String MESSAGE_INVALID_KEYWORD = "No task with keyword \"%1$s\" has been found.";
	private static final String MESSAGE_INVALID_DATE = "No task with date \"%1$s\" has been found.";

	// Data structure for tasks
	private ArrayList<AbstractTask> taskList = new ArrayList<AbstractTask>();

	// Data structure for last displayed list
	private ArrayList<AbstractTask> lastDisplayedList = null;

	private EditCommand lastEditKeyword = null;
	private boolean shouldPreserveEditKeyword = false;

	private static Parser parser = new Parser();

	private static Storage storage = new Storage();

	public Logic() {
		loadFromStorage();
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

	private void checkEditKeywordPreservation() {
		if (!shouldPreserveEditKeyword) {
			lastEditKeyword = null;
		}
		shouldPreserveEditKeyword = false;
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
		storage.write(taskList);
		return feedbackForAction("create", parsedCommand.getTaskName());
	}

	private Output createDeadlineTask(CreateCommand parsedCommand) {
		DeadlineTask newDeadlineTask = new DeadlineTask(
				parsedCommand.getTaskName(), parsedCommand.getEndDateTime());
		taskList.add(newDeadlineTask);
		storage.write(taskList);
		return feedbackForAction("create", parsedCommand.getTaskName());
	}

	private Output createBoundedTask(CreateCommand parsedCommand) {
		BoundedTask newBoundedTask = new BoundedTask(
				parsedCommand.getTaskName(), parsedCommand.getStartDateTime(),
				parsedCommand.getEndDateTime());
		taskList.add(newBoundedTask);
		storage.write(taskList);
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
		lastDisplayedList = taskList;
		ArrayList<ArrayList<String>> outputList = new ArrayList<ArrayList<String>>();
		Output output = new Output();

		for (int i = 0; i < taskList.size(); i++) {
			AbstractTask currentTask = taskList.get(i);
			ArrayList<String> taskArray = (currentTask.toArray());
			taskArray.add(0, String.valueOf(i + 1) + ".");
			outputList.add(taskArray);
		}
		output.setOutput(outputList);
		output.setReturnMessage(MESSAGE_DISPLAY_ALL);
		return output;
	}

	private Output displayStatus(Status status) {
		ArrayList<ArrayList<String>> outputList = new ArrayList<ArrayList<String>>();
		ArrayList<AbstractTask> filteredList = new ArrayList<AbstractTask>();
		Output output = new Output();

		for (int i = 0; i < taskList.size(); i++) {
			AbstractTask currentTask = taskList.get(i);
			if (currentTask.getStatus() == status) {
				filteredList.add(currentTask);
				ArrayList<String> taskArray = (currentTask.toArray());
				taskArray.add(0, String.valueOf(filteredList.size()) + ".");
				outputList.add(taskArray);
			}
		}
		lastDisplayedList = filteredList;
		output.setOutput(outputList);
		output.setReturnMessage(String.format(MESSAGE_DISPLAY_STATUS,
				status.toString()));
		return output;
	}

	private Output displayByName(String keyword) {
		lastDisplayedList = filterByName(taskList, keyword);
		ArrayList<ArrayList<String>> outputList = new ArrayList<ArrayList<String>>();
		Output output = new Output();

		int i = 1;
		for (AbstractTask task : lastDisplayedList) {
			ArrayList<String> taskArray = (task.toArray());
			taskArray.add(0, String.valueOf(i) + ".");
			outputList.add(taskArray);
			i++;
		}

		output.setOutput(outputList);
		output.setReturnMessage(String.format(MESSAGE_DISPLAY_KEYWORD, keyword));
		return output;
	}

	private Output displayByDate(DisplayCommand parsedCommand) {
		LocalDate queryDate = parsedCommand.getSearchDate().toLocalDate();
		lastDisplayedList = filterByDate(taskList, queryDate);
		ArrayList<ArrayList<String>> outputList = new ArrayList<ArrayList<String>>();
		Output output = new Output();

		int i = 1;
		for (AbstractTask task : lastDisplayedList) {
			ArrayList<String> taskArray = (task.toArray());
			taskArray.add(0, String.valueOf(i) + ".");
			outputList.add(taskArray);
			i++;
		}

		output.setOutput(outputList);
		DateTimeFormatter DTFormatter = DateTimeFormatter
				.ofPattern("dd MM yyyy");
		String returnDate = queryDate.format(DTFormatter);
		output.setReturnMessage(String.format(MESSAGE_DISPLAY_DATE, returnDate));
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

		if (lastDisplayedList == null) {
			return feedbackForAction("updateError", null);
		}

		if (parsedCommand.getIndex() > lastDisplayedList.size()) {
			return feedbackForAction("invalid", null);
		}
		int taskIndex = parsedCommand.getIndex() - 1;
		AbstractTask taskToEdit = lastDisplayedList.get(taskIndex);
		try {
			if (lastEditKeyword != null) {
				performEdit(lastEditKeyword, taskToEdit);
			}
			performEdit(parsedCommand, taskToEdit);
		} catch (ClassCastException e) {
			 // Happens when user tries to edit a non-existent field in task
			 // e.g. edit start time of floating task
			 return feedbackForAction("invalid", null);
		}
		return feedbackForAction("edit", null);
		
	}

	private Output editByKeyword(EditCommand parsedCommand) {
		String keyword = parsedCommand.getSearchKeyword();
		ArrayList<AbstractTask> filteredList = filterByName(taskList, keyword);
		if (filteredList.size() == 0) {
			return feedbackForAction("string!exist", keyword);
		} else if (filteredList.size() == 1
				&& filteredList.get(0).getName().equals(keyword)) {
			AbstractTask uniqueTask = filteredList.get(0);
			try {
				performEdit(parsedCommand, uniqueTask);
			} catch (ClassCastException e) {
				 // Happens when user tries to edit a non-existent field in task
				 // e.g. edit start time of floating task
				 return feedbackForAction("invalid", null);
			}
			return feedbackForAction("edit", null);
		} else {
			// record down additional content given by user
			lastEditKeyword = parsedCommand;
			shouldPreserveEditKeyword = true;
			return displayByName(keyword);
		}
	}

	/*
	 * Helper methods for editing task fields
	 */

	private void performEdit(EditCommand parsedCommand, AbstractTask taskToEdit)
			throws ClassCastException {
		ArrayList<editField> editFields = parsedCommand.getEditFields();
		// try {
		for (int i = 0; i < editFields.size(); i++) {
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
		}
		// } catch (ClassCastException e) {
		// // Happens when user tries to edit a non-existent field in task
		// // e.g. edit start time of floating task
		// return feedbackForAction("invalid", null);
		// }

		// return feedbackForAction("edit", null);
	}

	private void editTaskName(AbstractTask task, String name) {
		task.setName(name);
	}

	private void editStartDate(AbstractTask task, String startDate)
			throws ClassCastException {
		((BoundedTask) task).setStartDate(startDate);
	}

	private void editStartTime(AbstractTask task, String startTime)
			throws ClassCastException {
		((BoundedTask) task).setStartTime(startTime);
	}

	private void editEndDate(AbstractTask task, String endDate)
			throws ClassCastException {
		if (task instanceof FloatingTask) {
			throw new ClassCastException();
		} else if (task instanceof DeadlineTask) {
			((DeadlineTask) task).setEndDate(endDate);
		} else if (task instanceof BoundedTask) {
			((BoundedTask) task).setEndDate(endDate);
		}
	}

	private void editEndTime(AbstractTask task, String endTime)
			throws ClassCastException {
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

		if (lastDisplayedList == null) {
			return feedbackForAction("deleteError", null);
		}

		if (parsedCommand.getIndex() > lastDisplayedList.size()) {
			return feedbackForAction("invalid", null);
		}
		int taskIndex = parsedCommand.getIndex() - 1;
		AbstractTask taskToDelete = lastDisplayedList.get(taskIndex);
		String taskName = taskToDelete.getName();
		lastDisplayedList.remove(taskToDelete);
		taskList.remove(taskToDelete);
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
			return feedbackForAction("singleDelete", keyword);
		} else {
			return displayByName(keyword);
		}
	}

	private Output deleteByScope(DeleteCommand parsedCommand) {
		switch (parsedCommand.getScope()) {
		case ALL:
			return deleteAllTasks();
		case DONE:
			return deleteByScope(Scope.DONE);
		case UNDONE:
			return deleteByScope(Scope.UNDONE);
		default:
			// should not reach this code
			return feedbackForAction("invalid", null);

		}
	}

	private Output deleteAllTasks() {
		taskList.clear();
		return feedbackForAction("deleteAll", null);
	}

	private Output deleteByScope(Scope scope) {
		Status scopeStatus = Status.DONE;
		if (scope == Scope.UNDONE) {
			scopeStatus = Status.UNDONE;
		}
		for (AbstractTask task : taskList) {
			if (task.getStatus().equals(scopeStatus)) {
				taskList.remove(task);
			}
		}
		return feedbackForAction("deleteStatus", scopeStatus.toString());
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
			output.setReturnMessage("Edit done successfully!");
			break;
		case "singleDelete":
			output.setReturnMessage(String.format(MESSAGE_SINGLE_DELETION,
					content));
			break;
		case "updateError":
			output.setReturnMessage(String
					.format(MESSAGE_UPDATE_ERROR, content));
			break;
		case "deleteError":
			output.setReturnMessage(String.format(MESSAGE_DELETION_ERROR,
					content));
			break;
		case "deleteAll":
			output.setReturnMessage(MESSAGE_ALL_DELETION);
			break;
		case "deleteStatus":
			output.setReturnMessage(String.format(MESSAGE_STATUS_DELETION,
					content));
			break;
		case "invalid":
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

	// Commented out as format of return message not finalised
	//
	// private static Output feedbackForAction(String action, String editType,
	// String taskName, String newValue) {
	// Output returnMessage = new Output();
	// ArrayList<String> messageHolder = new ArrayList<String>();
	// String customMessage;
	//
	// switch (action) {
	// case "edit":
	// customMessage = String.format(MESSAGE_UPDATE, editType, taskName,
	// newValue);
	// messageHolder.add(customMessage);
	// returnMessage.add(messageHolder);
	// break;
	// case "invalid":
	// messageHolder.add(MESSAGE_INVALID_COMMAND);
	// returnMessage.add(messageHolder);
	// break;
	// }
	//
	// return returnMessage;
	// }

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

	/*
	 * Public methods for testing
	 */

	protected ArrayList<AbstractTask> getTaskListTest() {
		return taskList;
	}

	protected void setTaskListTest(ArrayList<AbstractTask> taskArray) {
		taskList = taskArray;
	}

	protected void setLastDisplayed(ArrayList<AbstractTask> taskArray) {
		lastDisplayedList = taskArray;
	}

	protected ArrayList<AbstractTask> getLastDisplayed() {
		return lastDisplayedList;
	}

}
