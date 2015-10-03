import java.util.ArrayList;

public class Logic {

	// Templates for program feedback
	private static final String MESSAGE_CREATION = "\"%1$s\" has been successfully created!";
	private static final String MESSAGE_UPDATE = "\"%1$s\" has been successfully edited!";
	private static final String MESSAGE_INVALID_COMMAND = "Invalid Command!";
	private static final String MESSAGE_DISPLAY_ALL_COMMAND = "All task are displayed!";
	
	// Data structure for tasks
	private ArrayList<AbstractTask> taskList = new ArrayList<AbstractTask>();
	
	//variable for feedbackForAction
	private static String customMessage;

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

	private ArrayList<ArrayList<String>> executeCommand(ArrayList<String> parsedCommand) {

		switch (getFirstElement(parsedCommand)) {
		case "create":
			return createTask(parsedCommand);
		case "display":
			return displayTasks(parsedCommand);
		case "edit":
			return editTask(parsedCommand);
//		case "delete":
//			return deleteTask(parsedCommand);
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
		if (parsedCommand.size() == 2) {
			return createFloatingTask(parsedCommand);
		} else if (parsedCommand.size() == 4
				&& checkForNull(parsedCommand.get(2), parsedCommand.get(3))) {
			return createDeadlineTask(parsedCommand);
		} else if (parsedCommand.size() == 6) {
			return createBoundedTask(parsedCommand);
		} else {
			return feedbackForAction("invalid", null);
		}
	}

	private ArrayList<ArrayList<String>> createFloatingTask(ArrayList<String> parsedCommand) {
		FloatingTask newFloatingTask = new FloatingTask(parsedCommand.get(1));
		taskList.add(newFloatingTask);
		storage.write(taskList);
		return feedbackForAction("create", parsedCommand.get(1));
	}

	private ArrayList<ArrayList<String>> createDeadlineTask(ArrayList<String> parsedCommand) {
		DeadlineTask newDeadlineTask = new DeadlineTask(parsedCommand.get(1),
				parsedCommand.get(4), parsedCommand.get(5));
		taskList.add(newDeadlineTask);
		storage.write(taskList);
		return feedbackForAction("create", parsedCommand.get(1));
	}

	private ArrayList<ArrayList<String>> createBoundedTask(ArrayList<String> parsedCommand) {
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
	
	private ArrayList<ArrayList<String>> displayTasks(ArrayList<String> parsedCommand) {
		return displayAllTasks();
	}
	
	private ArrayList<ArrayList<String>> displayAllTasks() {
		lastDisplayedList = taskList;
		ArrayList<ArrayList<String>> returnMessage = new ArrayList<ArrayList<String>>();		
		
		for (int i = 0; i < taskList.size(); i++) {
			AbstractTask currentTask = taskList.get(i);
			ArrayList<String> indexedArray = (currentTask.toArray());
			indexedArray.set(0, String.valueOf(i + 1));
			returnMessage.set(i, indexedArray);
		}
		ArrayList<String> finalMessage = new ArrayList<String>();
		finalMessage.set(0, MESSAGE_DISPLAY_ALL_COMMAND);
		returnMessage.add(finalMessage);
		return returnMessage;
	}
	
	/* 
	 * Methods for editing tasks
	 */
	
	private ArrayList<ArrayList<String>> editTask(ArrayList<String> parsedCommand) {
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
	
	private ArrayList<ArrayList<String>> editTaskName(ArrayList<String> parsedCommand) {
		String taskIdentifier = parsedCommand.get(2);
		if ((taskIdentifier.toCharArray())[0] == '#') {
			int taskIndex = Integer.parseInt(taskIdentifier.substring(1));
			taskList.get(taskIndex).setName(parsedCommand.get(3));
			//issue with name
			return feedbackForAction("edit", parsedCommand.get(3));
		} else {
			return feedbackForAction("invalid", null);
		}
	}
	
	private ArrayList<ArrayList<String>> editTaskStart(ArrayList<String> parsedCommand) {
		String taskIdentifier = parsedCommand.get(2);
		if ((taskIdentifier.toCharArray())[0] == '#') {
			int taskIndex = Integer.parseInt(taskIdentifier.substring(1));
			//Check for instance of currentTask!
			((BoundedTask) taskList.get(taskIndex)).setStartTime(parsedCommand.get(3));
			//issue with name
			return feedbackForAction("edit", parsedCommand.get(3));
		} else {
			return feedbackForAction("invalid", null);
		}
	}
	
	private ArrayList<ArrayList<String>> editTaskEnd(ArrayList<String> parsedCommand) {
		String taskIdentifier = parsedCommand.get(2);
		if ((taskIdentifier.toCharArray())[0] == '#') {
			int taskIndex = Integer.parseInt(taskIdentifier.substring(1));
			taskList.get(taskIndex).setName(parsedCommand.get(3));
			//issue with name
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
	
	private static boolean checkForNull(String stringA, String stringB) {
		return stringA.equals(null) && stringB.equals(null);
	}
	
	private static ArrayList<ArrayList<String>> feedbackForAction(String action, String taskName) {
		ArrayList<ArrayList<String>> returnMessage = new ArrayList<ArrayList<String>>();		
		
		switch(action) {
		case "create":
			customMessage = String.format(MESSAGE_CREATION, taskName);
			getFirstElement(returnMessage).set(0, customMessage);
		case "edit":
			customMessage = String.format(MESSAGE_UPDATE, taskName);
			getFirstElement(returnMessage).set(0, customMessage);
		case "invalid":
			getFirstElement(returnMessage).set(0, MESSAGE_INVALID_COMMAND);
		}
		
		return returnMessage;
	}
	
	 
}
