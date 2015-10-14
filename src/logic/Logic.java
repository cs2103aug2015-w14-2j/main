package logic;

import java.util.ArrayList;

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
	private static final String MESSAGE_DISPLAY_ALL_COMMAND = "all tasks are now displayed!";

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
		return displayAllTasks();
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
		output.setReturnMessage(MESSAGE_DISPLAY_ALL_COMMAND);
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
		assert(parsedCommand.getIndex() > 0);
	
		if (parsedCommand.getIndex() > lastDisplayedList.size()) {
			return feedbackForAction("invalid", null);
		}
		AbstractTask taskToEdit = lastDisplayedList.get(parsedCommand.getIndex() - 1);
		ArrayList<EditCommand.editField> editFields = parsedCommand.getEditFields();
		for (int i = 0; i < editFields.size(); i++) {
			if (editFields.get(i) == editField.NAME) {
				taskToEdit.setName(parsedCommand.getNewName());
			} else if (editFields.get(i) == editField.START_DATE && taskToEdit instanceof BoundedTask) {
				((BoundedTask) taskToEdit).setStartDate(parsedCommand.getNewStartDate());
			} else if (editFields.get(i) == editField.START_TIME && taskToEdit instanceof BoundedTask) {
				((BoundedTask) taskToEdit).setStartTime(parsedCommand.getNewStartTime());
			} else if (editFields.get(i) == editField.END_DATE && !(taskToEdit instanceof FloatingTask)) {
				if (taskToEdit instanceof DeadlineTask) {
					((DeadlineTask) taskToEdit).setEndDate(parsedCommand.getNewEndDate());
				} else if (taskToEdit instanceof BoundedTask) {
					((BoundedTask) taskToEdit).setEndDate(parsedCommand.getNewEndDate());
				}
			} else if (editFields.get(i) == editField.END_TIME && !(taskToEdit instanceof FloatingTask)) {
				if (taskToEdit instanceof DeadlineTask) {
					((DeadlineTask) taskToEdit).setEndTime(parsedCommand.getNewEndTime());
				} else if (taskToEdit instanceof BoundedTask) {
					((BoundedTask) taskToEdit).setEndTime(parsedCommand.getNewEndTime());
				}
			}
		}
		return feedbackForAction("edit", null);
	}
	
	private Output editByKeyword(EditCommand parsedCommand) {
		
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

	private static int getEditIndex(String indexString) {
		return Integer.parseInt(indexString.substring(1));
	}

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
			output.setReturnMessage(String.format(MESSAGE_SINGLE_DELETION, taskName));
			break;
		case "updateError":
			output.setReturnMessage(String.format(MESSAGE_UPDATE_ERROR, taskName));
			break;
		case "deleteError":
			output.setReturnMessage(String.format(MESSAGE_DELETION_ERROR, taskName));
			break;
		case "invalid":
			output.setReturnMessage(String.format(MESSAGE_INVALID_COMMAND, taskName));
			break;
		}

		return output;
	}
	
// Commented out as format of return message not finalised
//	private static Output feedbackForAction(String action, String editType,
//			String taskName, String newValue) {
//		Output returnMessage = new Output();
//		ArrayList<String> messageHolder = new ArrayList<String>();
//		String customMessage;
//
//		switch (action) {
//		case "edit":
//			customMessage = String.format(MESSAGE_UPDATE, editType, taskName,
//					newValue);
//			messageHolder.add(customMessage);
//			returnMessage.add(messageHolder);
//			break;
//		case "invalid":
//			messageHolder.add(MESSAGE_INVALID_COMMAND);
//			returnMessage.add(messageHolder);
//			break;
//		}
//
//		return returnMessage;
//	}

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
