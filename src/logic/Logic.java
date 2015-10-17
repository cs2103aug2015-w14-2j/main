package logic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

import parser.AbstractCommand;
import parser.CreateCommand;
import parser.DeleteCommand;
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
	private static final String MESSAGE_UPDATE = "%1$s of \"%2$s\" has been successfully changed to \"%3$s\"!";
	private static final String MESSAGE_UPDATE_ERROR = "Please display tasks at least once to edit by index";
	private static final String MESSAGE_SINGLE_DELETION = "\"%1$s\" has been deleted!";
	private static final String MESSAGE_DELETION_ERROR = "Please display tasks at least once to delete by index";
	private static final String MESSAGE_INVALID_COMMAND = "Invalid Command!";
	private static final String MESSAGE_DISPLAY_ALL = "All tasks are now displayed!";
	private static final String MESSAGE_DISPLAY_STATUS = "All tasks that are %1$s are now displayed!";
	private static final String MESSAGE_DISPLAY_KEYWORD = "All tasks with keyword \"%1$s\" are now displayed!";
	private static final String MESSAGE_DISPLAY_DATE = "All tasks with date \"%1$s\" are now displayed!";

	// Data structure for tasks
	private ArrayList<AbstractTask> taskList = new ArrayList<AbstractTask>();

	// Data structure for last displayed list
	private ArrayList<AbstractTask> lastDisplayedList = null;

	private static Parser parser = new Parser();

	private static Storage storage = new Storage();

	public Logic() {
		loadFromStorage();
	}

	public Output processInput(String userCommand) {
		AbstractCommand parsedCommand = parser.parseInput(userCommand);
		return executeCommand(parsedCommand);
	}

	private void loadFromStorage() {
		taskList = storage.read();
	}

	protected Output executeCommand(AbstractCommand parsedCommand) {

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
			return displayByName(parsedCommand);
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

	private Output displayByName(DisplayCommand parsedCommand) {
		String keyword = parsedCommand.getSearchKeyword();
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
		DateTimeFormatter DTFormatter = DateTimeFormatter.ofPattern("dd MM yyyy");
		String returnDate = queryDate.format(DTFormatter);
		output.setReturnMessage(String.format(MESSAGE_DISPLAY_DATE, returnDate));
		return output;
	}

	/*
	 * Methods for editing tasks
	 */

	private Output editTask(EditCommand parsedCommand) {
		if (lastDisplayedList == null) {
			return feedbackForAction("updateError", null);
		}
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

		if (parsedCommand.getIndex() > lastDisplayedList.size()) {
			return feedbackForAction("invalid", null);
		}
		int taskIndex = parsedCommand.getIndex() - 1;
		AbstractTask taskToEdit = lastDisplayedList.get(taskIndex);
		ArrayList<editField> editFields = parsedCommand.getEditFields();
		try {
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
		} catch (ClassCastException e) {
			// Happens when user tries to edit a non-existent field in task
			// e.g. edit start time of floating task
			return feedbackForAction("invalid", null);
		}

		return feedbackForAction("edit", null);
	}

	private Output editByKeyword(EditCommand parsedCommand) {

	}

	/*
	 * Helper methods for editing task fields
	 */

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
		if (lastDisplayedList == null) {
			return feedbackForAction("deleteError", null);
		}
		switch () {
		
		}
		if (parsedCommand.get(1).equals("all")) {
			return feedbackForAction("invalid", null);
		} else if (parsedCommand.get(1).equals("done")) {
			return feedbackForAction("invalid", null);
		} else if (parsedCommand.get(1).equals("undone")) {
			return feedbackForAction("invalid", null);
		} else if (parsedCommand.get(1).substring(0, 1).equals("#")) {
			return deleteByIndex(parsedCommand);
		} else {
			// delete by taskName
			return feedbackForAction("invalid", null);
		}
	}

	private Output deleteByIndex(ArrayList<String> parsedCommand) {
		String taskIdentifier = parsedCommand.get(1);
		int taskIndex = getEditIndex(taskIdentifier) - 1;
		if (taskIndex < 0 || taskIndex > taskList.size() - 1) {
			return feedbackForAction("invalid", null);
		}
		String taskName = taskList.get(taskIndex).getName();
		taskList.remove(taskIndex);
		return feedbackForAction("singleDelete", taskName);
	}

	/*
	 * Helper Methods for SLAP
	 */

	// Constructs return messages for create, edit and delete commands
	private static Output feedbackForAction(String action, String taskName) {
		Output output = new Output();

		switch (action) {
		case "create":
			output.setReturnMessage(String.format(MESSAGE_CREATION, taskName));
			break;
		case "edit":
			output.setReturnMessage("Edit done successfully!");
			break;
		case "singleDelete":
			output.setReturnMessage(String.format(MESSAGE_SINGLE_DELETION,
					taskName));
			break;
		case "updateError":
			output.setReturnMessage(String.format(MESSAGE_UPDATE_ERROR,
					taskName));
			break;
		case "deleteError":
			output.setReturnMessage(String.format(MESSAGE_DELETION_ERROR,
					taskName));
			break;
		case "invalid":
			output.setReturnMessage(String.format(MESSAGE_INVALID_COMMAND,
					taskName));
			break;
		}

		return output;
	}

	// Commented out as format of return message not finalised
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
		boolean startDateCheck =  Objects.equals(task.getStartDateTime().toLocalDate(), queryDate);
		boolean endDateCheck =  Objects.equals(task.getEndDateTime().toLocalDate(), queryDate);
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
