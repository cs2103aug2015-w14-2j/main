import java.util.ArrayList;

public class Logic {

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

	Logic() {
		loadFromStorage();
	}

	public ArrayList<ArrayList<String>> processInput(String userCommand) {
		ArrayList<String> parsedCommand = parser.evaluateInput(userCommand);
		return executeCommand(parsedCommand);
	}

	private void loadFromStorage() {
		taskList = storage.read();
	}

	protected ArrayList<ArrayList<String>> executeCommand(
			ArrayList<String> parsedCommand) {

		switch (getFirstElement(parsedCommand)) {
		case "create":
			return createTask(parsedCommand);
		case "display":
			return displayTasks(parsedCommand);
		case "edit":
			return editTask(parsedCommand);
		case "delete":
			return deleteTask(parsedCommand);
		case "invalid": // do we still need this??
			return showInvalid();
		case "exit":
			System.exit(0);
		default:
			return feedbackForAction("invalid", null);
		}

	}

	private ArrayList<ArrayList<String>> showInvalid() {
		return feedbackForAction("invalid", null);
	}

	/*
	 * Methods for task creation
	 */

	private ArrayList<ArrayList<String>> createTask(
			ArrayList<String> parsedCommand) {

		if (isFloatingTask(parsedCommand)) {
			return createFloatingTask(parsedCommand);
		} else if (isDeadlineTask(parsedCommand)) {
			return createDeadlineTask(parsedCommand);
		} else if (isBoundedTask(parsedCommand)) {
			return createBoundedTask(parsedCommand);
		} else {
			return feedbackForAction("invalid", null);
		}
	}

	private ArrayList<ArrayList<String>> createFloatingTask(
			ArrayList<String> parsedCommand) {
		FloatingTask newFloatingTask = new FloatingTask(parsedCommand.get(1));
		taskList.add(newFloatingTask);
		storage.write(taskList);
		return feedbackForAction("create", parsedCommand.get(1));
	}

	private ArrayList<ArrayList<String>> createDeadlineTask(
			ArrayList<String> parsedCommand) {
		DeadlineTask newDeadlineTask = new DeadlineTask(parsedCommand.get(1),
				parsedCommand.get(4), parsedCommand.get(5));
		taskList.add(newDeadlineTask);
		storage.write(taskList);
		return feedbackForAction("create", parsedCommand.get(1));
	}

	private ArrayList<ArrayList<String>> createBoundedTask(
			ArrayList<String> parsedCommand) {
		BoundedTask newBoundedTask = new BoundedTask(parsedCommand.get(1),
				parsedCommand.get(2), parsedCommand.get(3),
				parsedCommand.get(4), parsedCommand.get(5));
		taskList.add(newBoundedTask);
		storage.write(taskList);
		return feedbackForAction("create", parsedCommand.get(1));
	}

	/*
	 * Methods for displaying tasks
	 */

	private ArrayList<ArrayList<String>> displayTasks(
			ArrayList<String> parsedCommand) {
		return displayAllTasks();
	}

	private ArrayList<ArrayList<String>> displayAllTasks() {
		lastDisplayedList = taskList;
		ArrayList<ArrayList<String>> returnMessage = new ArrayList<ArrayList<String>>();

		for (int i = 0; i < taskList.size(); i++) {
			AbstractTask currentTask = taskList.get(i);
			ArrayList<String> taskInArray = (currentTask.toArray());
			taskInArray.add(0, String.valueOf(i + 1) + ".");
			returnMessage.add(taskInArray);
		}
		ArrayList<String> finalMessage = new ArrayList<String>();
		finalMessage.add(MESSAGE_DISPLAY_ALL_COMMAND);
		returnMessage.add(finalMessage);
		return returnMessage;
	}

	/*
	 * Methods for editing tasks
	 */

	private ArrayList<ArrayList<String>> editTask(
			ArrayList<String> parsedCommand) {
		if (lastDisplayedList == null) {
			return feedbackForAction("updateError", null);
		}
		switch (parsedCommand.get(1)) {
		case "name":
			return editTaskName(parsedCommand);
		case "start":
			return editTaskStart(parsedCommand);
		case "end":
			return editTaskEnd(parsedCommand);
		default:
			throw new Error("Invalid edit type");
		}
	}

	private ArrayList<ArrayList<String>> editTaskName(
			ArrayList<String> parsedCommand) {
		String taskIdentifier = parsedCommand.get(2);
		if (taskIdentifier.substring(0, 1).equals("#")) {
			int taskIndex = getEditIndex(taskIdentifier) - 1;
			if (taskIndex < 0 || taskIndex > taskList.size() - 1) {
				return feedbackForAction("invalid", null);
			}
			String oldName = taskList.get(taskIndex).getName();
			taskList.get(taskIndex).setName(parsedCommand.get(3));
			return feedbackForAction("edit", "name", oldName,
					parsedCommand.get(3));
		} else {
			return feedbackForAction("invalid", null);
		}
	}

	private ArrayList<ArrayList<String>> editTaskStart(
			ArrayList<String> parsedCommand) {
		String taskIdentifier = parsedCommand.get(2);
		if (taskIdentifier.substring(0, 1).equals("#")) {
			int taskIndex = getEditIndex(taskIdentifier) - 1;
			if (taskIndex < 0 || taskIndex > taskList.size() - 1) {
				return feedbackForAction("invalid", null);
			}
			AbstractTask editedTask = taskList.get(taskIndex);
			if (editedTask instanceof FloatingTask) {
				// invalid task type
				return feedbackForAction("invalid", null);
			}
			String taskName = editedTask.getName();
			((BoundedTask) taskList.get(taskIndex)).setStartTime(parsedCommand
					.get(3));
			((BoundedTask) taskList.get(taskIndex)).setStartDate(parsedCommand
					.get(4));
			return feedbackForAction("edit", "start time and date", taskName,
					((BoundedTask) editedTask).getStartTime() + " "
							+ ((BoundedTask) editedTask).getStartDate());
		} else {
			return feedbackForAction("invalid", null);
		}
	}

	private ArrayList<ArrayList<String>> editTaskEnd(
			ArrayList<String> parsedCommand) {
		String taskIdentifier = parsedCommand.get(2);
		if (taskIdentifier.substring(0, 1).equals("#")) {
			int taskIndex = getEditIndex(taskIdentifier) - 1;
			if (taskIndex < 0 || taskIndex > taskList.size() - 1) {
				return feedbackForAction("invalid", null);
			}
			String taskName = taskList.get(taskIndex).getName();
			AbstractTask taskForEdit = taskList.get(taskIndex);
			if (taskForEdit instanceof DeadlineTask) {
				((DeadlineTask) taskForEdit).setEndTime(parsedCommand.get(3));
				((DeadlineTask) taskForEdit).setEndDate(parsedCommand.get(4));
				taskList.set(taskIndex, taskForEdit);
				return feedbackForAction("edit", "end time and date", taskName,
						((DeadlineTask) taskForEdit).getEndTime() + " "
								+ ((DeadlineTask) taskForEdit).getEndDate());
			} else if (taskForEdit instanceof BoundedTask) {
				((BoundedTask) taskForEdit).setEndTime(parsedCommand.get(3));
				((BoundedTask) taskForEdit).setEndDate(parsedCommand.get(4));
				taskList.set(taskIndex, taskForEdit);
				return feedbackForAction("edit", "end time and date", taskName,
						((BoundedTask) taskForEdit).getEndTime() + " "
								+ ((BoundedTask) taskForEdit).getEndDate());
			} else {
				// invalid task type
				return feedbackForAction("invalid", null);
			}
		} else {
			return feedbackForAction("invalid", null);
		}
	}

	/*
	 * Methods for deleting tasks
	 */
	
	private ArrayList<ArrayList<String>> deleteTask(
			ArrayList<String> parsedCommand) {
		if (lastDisplayedList == null) {
			return feedbackForAction("deleteError", null);
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
	
	private ArrayList<ArrayList<String>> deleteByIndex(
			ArrayList<String> parsedCommand) {
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

	private static <T> T getFirstElement(ArrayList<T> arrayList) {
		return arrayList.get(0);
	}

	private static boolean checkForEmpty(String string) {
		return string.equals("");
	}

	private static int getEditIndex(String indexString) {
		return Integer.parseInt(indexString.substring(1));
	}

	// Constructs return messages for create, edit and delete commands
	private static ArrayList<ArrayList<String>> feedbackForAction(
			String action, String taskName) {
		ArrayList<ArrayList<String>> returnMessage = new ArrayList<ArrayList<String>>();
		ArrayList<String> messageHolder = new ArrayList<String>();
		String customMessage;

		switch (action) {
		case "create":
			customMessage = String.format(MESSAGE_CREATION, taskName);
			messageHolder.add(customMessage);
			returnMessage.add(messageHolder);
			break;
		case "singleDelete":
			customMessage = String.format(MESSAGE_SINGLE_DELETION, taskName);
			messageHolder.add(customMessage);
			returnMessage.add(messageHolder);
			break;
		case "updateError":
			messageHolder.add(MESSAGE_UPDATE_ERROR);
			returnMessage.add(messageHolder);
			break;
		case "deleteError":
			messageHolder.add(MESSAGE_DELETION_ERROR);
			returnMessage.add(messageHolder);
			break;
		case "invalid":
			messageHolder.add(MESSAGE_INVALID_COMMAND);
			returnMessage.add(messageHolder);
			break;
		}

		return returnMessage;
	}

	private static ArrayList<ArrayList<String>> feedbackForAction(
			String action, String editType, String taskName, String newValue) {
		ArrayList<ArrayList<String>> returnMessage = new ArrayList<ArrayList<String>>();
		ArrayList<String> messageHolder = new ArrayList<String>();
		String customMessage;

		switch (action) {
		case "edit":
			customMessage = String.format(MESSAGE_UPDATE, editType, taskName,
					newValue);
			messageHolder.add(customMessage);
			returnMessage.add(messageHolder);
			break;
		case "invalid":
			messageHolder.add(MESSAGE_INVALID_COMMAND);
			returnMessage.add(messageHolder);
			break;
		}

		return returnMessage;
	}

	// Abstracted booleans to decide the type of task to create

	private boolean isFloatingTask(ArrayList<String> commandArray) {
		boolean sizeCheck = (commandArray.size() == 6);
		boolean contentsCheck = true;
		for (int i = 0; i <= 1; i++) {
			contentsCheck = contentsCheck
					&& !checkForEmpty(commandArray.get(i));
		}
		for (int i = 2; i <= 5; i++) {
			contentsCheck = contentsCheck && checkForEmpty(commandArray.get(i));
		}
		return sizeCheck && contentsCheck;
	}

	private boolean isDeadlineTask(ArrayList<String> commandArray) {
		boolean sizeCheck = (commandArray.size() == 6);
		boolean contentsCheck = true;
		for (int i = 0; i <= 1; i++) {
			contentsCheck = contentsCheck
					&& !checkForEmpty(commandArray.get(i));
		}
		for (int i = 2; i <= 3; i++) {
			contentsCheck = contentsCheck && checkForEmpty(commandArray.get(i));
		}
		for (int i = 4; i <= 5; i++) {
			contentsCheck = contentsCheck
					&& !checkForEmpty(commandArray.get(i));
		}
		return sizeCheck && contentsCheck;
	}

	private boolean isBoundedTask(ArrayList<String> commandArray) {
		boolean sizeCheck = (commandArray.size() == 6);
		boolean contentsCheck = true;
		for (int i = 0; i <= 5; i++) {
			contentsCheck = contentsCheck
					&& !checkForEmpty(commandArray.get(i));
		}
		return sizeCheck && contentsCheck;
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
