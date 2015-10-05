import java.util.ArrayList;

public class Logic {

	// Templates for program feedback
	private static final String MESSAGE_CREATION = "\"%1$s\" has been successfully created!";
	private static final String MESSAGE_UPDATE = "\"%1$s\" has been successfully edited!";
	private static final String MESSAGE_INVALID_COMMAND = "Invalid Command!";
	private static final String MESSAGE_DISPLAY_ALL_COMMAND = "all tasks are now displayed!";
	private static final String MESSAGE_EDIT_NAME = "name of \"%1$s\" has been changed to \"%2$s\"!";

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
			// case "delete":
			// return deleteTask(parsedCommand);
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

	private ArrayList<ArrayList<String>> createTask(ArrayList<String> parsedCommand) {
		
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
		if ((taskIdentifier.toCharArray())[0] == '#') {
			int taskIndex = Integer.parseInt(taskIdentifier.substring(1));
			taskList.get(taskIndex).setName(parsedCommand.get(3));
			// issue with name
			return feedbackForAction("edit", parsedCommand.get(3));
		} else {
			return feedbackForAction("invalid", null);
		}
	}

	private ArrayList<ArrayList<String>> editTaskStart(
			ArrayList<String> parsedCommand) {
		String taskIdentifier = parsedCommand.get(2);
		if ((taskIdentifier.toCharArray())[0] == '#') {
			int taskIndex = Integer.parseInt(taskIdentifier.substring(1));
			// Check for instance of currentTask!
			((BoundedTask) taskList.get(taskIndex)).setStartTime(parsedCommand
					.get(3));
			// issue with name
			return feedbackForAction("edit", parsedCommand.get(3));
		} else {
			return feedbackForAction("invalid", null);
		}
	}

	private ArrayList<ArrayList<String>> editTaskEnd(
			ArrayList<String> parsedCommand) {
		String taskIdentifier = parsedCommand.get(2);
		if ((taskIdentifier.toCharArray())[0] == '#') {
			int taskIndex = Integer.parseInt(taskIdentifier.substring(1));
			taskList.get(taskIndex).setName(parsedCommand.get(3));
			// issue with name
			return feedbackForAction("edit", parsedCommand.get(3));
		} else {
			return feedbackForAction("invalid", null);
		}
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
		case "edit":
			customMessage = String.format(MESSAGE_UPDATE, taskName);
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
			contentsCheck = contentsCheck && !checkForEmpty(commandArray.get(i));
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
			contentsCheck = contentsCheck && !checkForEmpty(commandArray.get(i));
		}
		for (int i = 2; i <= 3; i++) {
			contentsCheck = contentsCheck && checkForEmpty(commandArray.get(i));
		}
		for (int i = 4; i <= 5; i++) {
			contentsCheck = contentsCheck && !checkForEmpty(commandArray.get(i));
		}
		return sizeCheck && contentsCheck;
	}
	
	private boolean isBoundedTask(ArrayList<String> commandArray) {
		boolean sizeCheck = (commandArray.size() == 6);
		boolean contentsCheck = true;
		for (int i = 0; i <= 5; i++) {
			contentsCheck = contentsCheck && !checkForEmpty(commandArray.get(i));
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
	
	protected ArrayList<AbstractTask> getLastDisplayed() {
		return lastDisplayedList;
	}

}
