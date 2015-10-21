package logic;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Stack;

import parser.Parser;
import shared.Output;
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
import shared.task.AbstractTask;
import shared.task.BoundedTask;
import shared.task.DeadlineTask;
import shared.task.FloatingTask;
import shared.task.AbstractTask.Status;
import storage.Storage;

public class Logic implements LogicInterface {

	// Templates for program feedback
	private static final String MESSAGE_CREATION = "\"%1$s\" has been successfully created!";
	private static final String MESSAGE_UPDATE = "Edit done successfully!";
	private static final String MESSAGE_UPDATE_ERROR = "Please display tasks at least once to edit by index.";
	private static final String MESSAGE_SINGLE_DELETION = "\"%1$s\" has been deleted!";
	private static final String MESSAGE_ALL_DELETION = "All tasks have been deleted!";
	private static final String MESSAGE_STATUS_DELETION = "All %1$s tasks have been deleted!";
	private static final String MESSAGE_DELETION_ERROR = "Please display tasks at least once to delete by index.";
	private static final String MESSAGE_MARK = "\"%1$s\" has been marked %2$s.";
	private static final String MESSAGE_MARK_ERROR = "Please display tasks at least once to mark by index.";
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
	
	// Data structure for Undo Functionality
	private Stack<ArrayList<AbstractTask>> taskListStack = new Stack<ArrayList<AbstractTask>>();
	private Stack<AbstractCommand> commandHistoryStack = new Stack<AbstractCommand>();

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
		} else if (parsedCommand instanceof MarkCommand) {
			return markTask((MarkCommand) parsedCommand);
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
	
	private void recordChange(AbstractCommand parsedCommand) {
		storage.write(taskList);
		taskListStack.push(taskList);
		commandHistoryStack.push(parsedCommand);
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
		recordChange(parsedCommand);
		return feedbackForAction("create", parsedCommand.getTaskName());
	}

	private Output createDeadlineTask(CreateCommand parsedCommand) {
		DeadlineTask newDeadlineTask = new DeadlineTask(
				parsedCommand.getTaskName(), parsedCommand.getEndDateTime());
		taskList.add(newDeadlineTask);
		recordChange(parsedCommand);
		return feedbackForAction("create", parsedCommand.getTaskName());
	}

	private Output createBoundedTask(CreateCommand parsedCommand) {
		BoundedTask newBoundedTask = new BoundedTask(
				parsedCommand.getTaskName(), parsedCommand.getStartDateTime(),
				parsedCommand.getEndDateTime());
		taskList.add(newBoundedTask);
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
			return displayAllTasks(parsedCommand);
		case DONE:
			return displayStatus(parsedCommand, Status.DONE);
		case UNDONE:
			return displayStatus(parsedCommand, Status.UNDONE);
		default:
			// should not reach this code
			return feedbackForAction("invalid", null);
		}
	}

	private Output displayAllTasks(DisplayCommand parsedCommand) {
		lastDisplayedList = taskList;
		ArrayList<ArrayList<String>> outputList = new ArrayList<ArrayList<String>>();
		Output output = new Output();

		for (int i = 0; i < taskList.size(); i++) {
			AbstractTask currentTask = taskList.get(i);
			ArrayList<String> taskArray = (currentTask.toArray());
			taskArray.add(0, String.valueOf(i + 1));
			outputList.add(taskArray);
		}
		output.setOutput(outputList);
		output.setReturnMessage(MESSAGE_DISPLAY_ALL);
		recordChange(parsedCommand);
		return output;
	}

	private Output displayStatus(DisplayCommand parsedCommand, Status status) {
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
		lastDisplayedList = filteredList;
		output.setOutput(outputList);
		output.setReturnMessage(String.format(MESSAGE_DISPLAY_STATUS,
				status.toString()));
		recordChange(parsedCommand);
		return output;
	}

	private Output displayByName(String keyword) {
		lastDisplayedList = filterByName(taskList, keyword);
		ArrayList<ArrayList<String>> outputList = new ArrayList<ArrayList<String>>();
		Output output = new Output();

		int i = 1;
		for (AbstractTask task : lastDisplayedList) {
			ArrayList<String> taskArray = (task.toArray());
			taskArray.add(0, String.valueOf(i));
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
			taskArray.add(0, String.valueOf(i));
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
		if (editFields == null) {
			// only happens with two part edit
			return;
		}
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

		if (lastDisplayedList == null) {
			return feedbackForAction("markError", null);
		}
		if (parsedCommand.getIndex() > lastDisplayedList.size()) {
			return feedbackForAction("invalid", null);
		}

		int taskIndex = parsedCommand.getIndex() - 1;
		AbstractTask taskToMark = lastDisplayedList.get(taskIndex);
		String taskName = taskToMark.getName();
		
		Output feedback = feedbackForAction("markUndone", taskName);
		Status newStatus = Status.UNDONE;
		if (parsedCommand.getMarkField().equals(markField.MARK)) {
			newStatus = Status.DONE;
			feedback = feedbackForAction("markDone", taskName);
		}
		taskToMark.setStatus(newStatus);
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
			return feedback;
		} else {
			return displayByName(keyword);
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
			output.setReturnMessage(MESSAGE_UPDATE);
			break;
		case "updateError":
			output.setReturnMessage(MESSAGE_UPDATE_ERROR);
			break;
		case "singleDelete":
			output.setReturnMessage(String.format(MESSAGE_SINGLE_DELETION,
					content));
			break;
		case "deleteError":
			output.setReturnMessage(MESSAGE_DELETION_ERROR);
			break;
		case "deleteAll":
			output.setReturnMessage(MESSAGE_ALL_DELETION);
			break;
		case "deleteStatus":
			output.setReturnMessage(String.format(MESSAGE_STATUS_DELETION,
					content));
			break;
		case "markUndone":
			output.setReturnMessage(String.format(MESSAGE_MARK, content, "undone"));
			break;
		case "markDone":
			output.setReturnMessage(String.format(MESSAGE_MARK, content, "done"));
			break;
		case "markError":
			output.setReturnMessage(MESSAGE_MARK_ERROR);
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

	protected ArrayList<AbstractTask> getLastDisplayedTest() {
		return lastDisplayedList;
	}
	
	/*
	 * Method for UI Observer
	 */
	
	public Output getLastDisplayed() {
		ArrayList<ArrayList<String>> outputList = new ArrayList<ArrayList<String>>();
		Output output = new Output();
		
		for (int i = 0; i < lastDisplayedList.size(); i++) {
			AbstractTask currentTask = lastDisplayedList.get(i);
			ArrayList<String> taskArray = (currentTask.toArray());
			taskArray.add(0, String.valueOf(i + 1));
			outputList.add(taskArray);
		}
		output.setOutput(outputList);
		return output;
	}

}
