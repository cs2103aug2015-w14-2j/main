package logic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Stack;

import logic.action.CreateAction;
import logic.action.DeleteAction;
import logic.action.DisplayAction;
import logic.action.EditAction;
import logic.action.MarkAction;
import logic.action.UIAction;
import parser.Parser;
import shared.Constants;
import shared.Output;
import shared.Output.Priority;
import shared.command.AbstractCommand;
import shared.command.CreateCommand;
import shared.command.DeleteCommand;
import shared.command.DisplayCommand;
import shared.command.EditCommand;
import shared.command.EditCommand.Nature;
import shared.command.ExitCommand;
import shared.command.InvalidCommand;
import shared.command.MarkCommand;
import shared.command.SaveCommand;
import shared.command.UICommand;
import shared.command.UndoCommand;
import shared.task.AbstractTask;
import shared.task.DeadlineTask;
import shared.task.AbstractTask.Status;
import storage.Storage;

//@@author A0124828B
public class Logic implements LogicInterface {

	// Templates for program feedback
	private static final String MESSAGE_SAVEPATH = "\"%1$s\" has been set as new save path!";
	private static final String MESSAGE_SAVEPATH_FAIL = "\"%1$s\" is an invalid save path!";

	// Data structure for tasks
	private TaskList taskList;

	// Data structure for last displayed list
	private TaskList latestDisplayedList = new TaskList();

	// Data structure for Undo Functionality
	private Stack<TaskList> taskListStack = new Stack<TaskList>();
	private Stack<AbstractCommand> cmdHistoryStack = new Stack<AbstractCommand>();
	private DisplayCommand latestDisplayCmd = new DisplayCommand(
			DisplayCommand.Scope.DEFAULT);

	// Variables for two level edit
	private EditCommand latestComplexEdit = new EditCommand(Nature.SIMPLE);
	private ExtendedBoolean shouldKeepComplexEdit = new ExtendedBoolean(false);

	private static Parser parser = new Parser();

	private Storage storage;

	public Logic(Storage storage) {
		this.storage = storage;
		loadFromStorage();
		loadStateForUndo();
	}

	private void loadFromStorage() {
		taskList = new TaskList(this.storage.read());
	}

	private void loadStateForUndo() {
		TaskList clonedList = this.taskList.clone();
		this.taskListStack.push(clonedList);
	}

	public Output processInput(String userCmd) {
		AbstractCommand parsedCmd = parser.parseInput(userCmd);
		return executeCommand(parsedCmd);
	}

	protected Output executeCommand(AbstractCommand parsedCmd) {
		runBackgroundRoutines();
		Output feedbackToUI = null;

		if (parsedCmd instanceof CreateCommand) {
			CreateCommand cmd = (CreateCommand) parsedCmd;
			CreateAction action = new CreateAction(cmd, taskList,
					latestDisplayedList);
			feedbackToUI = action.execute();
		} else if (parsedCmd instanceof DisplayCommand) {
			DisplayCommand cmd = (DisplayCommand) parsedCmd;
			DisplayAction action = new DisplayAction(cmd, taskList,
					latestDisplayedList, latestDisplayCmd);
			return feedbackToUI = action.execute();
		} else if (parsedCmd instanceof EditCommand) {
			EditCommand cmd = (EditCommand) parsedCmd;
			EditAction action = new EditAction(cmd, taskList,
					latestDisplayedList, latestDisplayCmd);
			action.setLatestComplexEdit(latestComplexEdit);
			action.setShouldKeepComplexEdit(shouldKeepComplexEdit);
			feedbackToUI = action.execute();
		} else if (parsedCmd instanceof DeleteCommand) {
			DeleteCommand cmd = (DeleteCommand) parsedCmd;
			DeleteAction action = new DeleteAction(cmd, taskList,
					latestDisplayedList, latestDisplayCmd);
			feedbackToUI = action.execute();
		} else if (parsedCmd instanceof MarkCommand) {
			MarkCommand cmd = (MarkCommand) parsedCmd;
			MarkAction action = new MarkAction(cmd, taskList,
					latestDisplayedList, latestDisplayCmd);
			feedbackToUI = action.execute();
		} else if (parsedCmd instanceof UndoCommand) {
			return undoPreviousAction();
		} else if (parsedCmd instanceof UICommand) {
			UIAction action = new UIAction();
			return feedbackToUI = action.execute();
		} else if (parsedCmd instanceof SaveCommand) {
			return setPath((SaveCommand) parsedCmd);
		} else if (parsedCmd instanceof InvalidCommand) {
			Output feedback = new Output(Constants.MESSAGE_INVALID_COMMAND);
			feedback.setPriority(Priority.HIGH);
			return feedback;
		} else if (parsedCmd instanceof ExitCommand) {
			System.exit(0);
		} else {
			Output feedback = new Output(Constants.MESSAGE_INVALID_COMMAND);
			feedback.setPriority(Priority.HIGH);
			return feedback;
		}
		recordChange(parsedCmd);
		refreshLatestDisplayed();
		return feedbackToUI;

	}

	private void runBackgroundRoutines() {
		checkEditKeywordPreservation();
		updateOverdueStatus();
	}

	private void checkEditKeywordPreservation() {
		if (shouldKeepComplexEdit.getState()) {
			this.cmdHistoryStack.pop();
		} else {
			latestComplexEdit = new EditCommand(Nature.SIMPLE);
		}
		shouldKeepComplexEdit.setFalse();
	}

	private void recordChange(AbstractCommand parsedCmd) {
		storage.write(this.taskList.getTasks());
		TaskList clonedList = this.taskList.clone();
		this.taskListStack.push(clonedList);
		this.cmdHistoryStack.push(parsedCmd);
	}

	private void refreshLatestDisplayed() {
		DisplayAction action = new DisplayAction(latestDisplayCmd, taskList,
				latestDisplayedList, latestDisplayCmd);
		action.execute();
	}

	/*
	 * Methods for Undo Functionality
	 */

	private Output undoPreviousAction() {
		if (taskListStack.size() == 1) {
			// Earliest recorded version for current run of program
			Output feedback = new Output(Constants.MESSAGE_INVALID_COMMAND);
			feedback.setPriority(Priority.HIGH);
			return feedback;
		} else {
			taskListStack.pop();
			this.taskList = (taskListStack.peek()).clone();
			refreshLatestDisplayed();
			storage.write(this.taskList.getTasks());
			AbstractCommand undoneCommand = cmdHistoryStack.pop();
			String undoMessage = undoneCommand.getUndoMessage();
			return new Output(undoMessage);
		}
	}

	private Output setPath(SaveCommand parsedCmd) {
		boolean isValidPath = storage.changePath(parsedCmd.getPath());
		if (isValidPath) {
			return new Output(MESSAGE_SAVEPATH, parsedCmd.getPath());
		} else {
			Output feedback = new Output(MESSAGE_SAVEPATH_FAIL,
					parsedCmd.getPath());
			feedback.setPriority(Priority.HIGH);
			return feedback;
		}
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
		DisplayAction displayAction = new DisplayAction(latestDisplayCmd,
				taskList, latestDisplayedList, latestDisplayCmd);
		return displayAction.execute();
	}

}
