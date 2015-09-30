import java.util.ArrayList;
import java.util.Scanner;

public class Logic {

	// Templates for program feedback
	private static final String MESSAGE_WELCOME = "Good Day! What can I help you with?";
	private static final String MESSAGE_FLOATING_CREATION = "Floating task \"%1$s\" has been successfully created!";
	private static final String MESSAGE_DEADLINE_CREATION = "Deadline task \"%1$s\" with End Time: %2$s, End Date: %3$s has been successfully created!";
	private static final String MESSAGE_BOUNDED_CREATION = "Bounded task \"%1$s\" with Start Time: %2$s, Start Date: %3$s, End Time: %4$s, End Date: %5$s has been successfully created!";
	private static final String MESSAGE_INVALID_COMMAND = "Invalid Command!";
	
	// Data structure for tasks
	private static ArrayList<AbstractTask> taskList = new ArrayList<AbstractTask>();

	// Data structure for last displayed list
	private static ArrayList<AbstractTask> lastDisplayedList = null;
	
	// Scanner used for gathering input
	private static Scanner scanner = new Scanner(System.in);
	
	private static Parser parser = new Parser();
	
	private static Storage storage = new Storage();

	public static void main(String[] args) {
		loadFromStorage();
		showToUser(MESSAGE_WELCOME);
		respondToUser();
	}

	private static void loadFromStorage() {
		taskList = storage.read();
	}

	// Have to integrate with UI component
	private static void showToUser(String message) {
		System.out.println(message);
	}

	private static void respondToUser() {
		while (true) {
			System.out.print("Enter command:");
			String userCommand = scanner.nextLine();
			ArrayList<String> parsedCommand = parser.evaluateInput(userCommand);
			String feedback = executeCommand(parsedCommand);
			showToUser(feedback);
		}
	}

	private static String executeCommand(ArrayList<String> parsedCommand) {

		switch (parsedCommand.get(0)) {
		case "create":
			return createTask(parsedCommand);
//		case "display":
//			return displayTasks(parsedCommand);
//		case "edit":
//			return editTask(parsedCommand);
//		case "delete":
//			return deleteTask(parsedCommand);
		case "invalid":
			return showInvalid();
		case "exit":
			System.exit(0);
		default:
			throw new Error("Unrecognized command type");
		}

	}
	
	private static String showInvalid() {
		return MESSAGE_INVALID_COMMAND;
	}
	
	/*
	 * Methods for task creation
	 */
	
	private static String createTask(ArrayList<String> parsedCommand) {
		if (parsedCommand.size() == 2) {
			return createFloatingTask(parsedCommand);
		} else if (parsedCommand.size() == 4
				&& parsedCommand.get(2).equals(parsedCommand.get(3))
				&& parsedCommand.get(2).equals(null)) {
			return createDeadlineTask(parsedCommand);
		} else if (parsedCommand.size() == 6) {
			return createBoundedTask(parsedCommand);
		} else {
			throw new Error("Invalid create command");
		}
	}

	private static String createFloatingTask(ArrayList<String> parsedCommand) {
		FloatingTask newFloatingTask = new FloatingTask(parsedCommand.get(1));
		taskList.add(newFloatingTask);
		storage.write(taskList);
		return String.format(MESSAGE_FLOATING_CREATION, parsedCommand.get(1));
	}

	private static String createDeadlineTask(ArrayList<String> parsedCommand) {
		DeadlineTask newDeadlineTask = new DeadlineTask(parsedCommand.get(1),
				parsedCommand.get(4), parsedCommand.get(5));
		taskList.add(newDeadlineTask);
		storage.write(taskList);
		return String.format(MESSAGE_DEADLINE_CREATION, parsedCommand.get(1),
				parsedCommand.get(4), parsedCommand.get(5));
	}

	private static String createBoundedTask(ArrayList<String> parsedCommand) {
		BoundedTask newBoundedTask = new BoundedTask(parsedCommand.get(1),
				parsedCommand.get(2), parsedCommand.get(3),
				parsedCommand.get(4), parsedCommand.get(5));
		taskList.add(newBoundedTask);
		storage.write(taskList);
		return String.format(MESSAGE_BOUNDED_CREATION, parsedCommand.get(1),
				parsedCommand.get(2), parsedCommand.get(3),
				parsedCommand.get(4), parsedCommand.get(5));
	}	
	
	/*
	 * Methods for displaying tasks
	 */
	
	private static String displayTasks(ArrayList<String> parsedCommand) {
		return displayAllTasks();
	}
	
	private static String displayAllTasks() {
		lastDisplayedList = taskList;
		String output = "";
		for (int i = 0; i < taskList.size(); i++) {
			AbstractTask currentTask = taskList.get(i);
			output += (i + 1) + ". ";
			if (currentTask instanceof FloatingTask) {
				//Up to UI
			} else if (currentTask instanceof DeadlineTask) {
				//Up to UI
			} else if (currentTask instanceof BoundedTask) {
				//Up to UI
			}
		}
		return output;
	}
	
	/* 
	 * Methods for editing tasks
	 */
	
	private static String editTask(ArrayList<String> parsedCommand) {
		switch (parsedCommand.get(1)) {
//		case "name":
//			return editTaskName(parsedCommand);
//		case "start":
//			return editTaskStart(parsedCommand);
//		case "end":
//			return editTaskEnd(parsedCommand);
		default:
			throw new Error("Invalid edit type");
		}
	}
	
//	private static String editTaskName(ArrayList<String> parsedCommand) {
//		String taskIdentifier = parsedCommand.get(2);
//		if ((taskIdentifier.toCharArray())[0] == '#') {
//			int taskIndex = Integer.parseInt(taskIdentifier.substring(1));
//			taskList.get(taskIndex).setName(parsedCommand.get(3));
//		} else {
//			return "bla";
//		}
//	}
	
	
	
	 
}
