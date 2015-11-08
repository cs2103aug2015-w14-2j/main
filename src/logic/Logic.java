package logic;

import java.time.LocalDateTime;
import java.util.Stack;

import logic.action.AbstractAction;
import logic.action.CreateAction;
import logic.action.DeleteAction;
import logic.action.DisplayAction;
import logic.action.EditAction;
import logic.action.MarkAction;
import logic.action.SaveAction;
import logic.action.UIAction;
import logic.action.UndoAction;
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
import shared.command.MarkCommand;
import shared.command.SaveCommand;
import shared.command.UndoCommand;
import shared.task.AbstractTask;
import shared.task.DeadlineTask;
import shared.task.AbstractTask.Status;
import storage.Storage;

//@@author A0124828B

public class Logic implements LogicInterface {

	private TaskList taskList;

	// TaskList storing the tasks last shown in UI
	private TaskList latestDisplayedList = new TaskList();

	// Data structure for Undo Functionality
	private Stack<TaskList> taskListStack = new Stack<TaskList>();
	private Stack<AbstractCommand> cmdHistoryStack = new Stack<AbstractCommand>();
	private DisplayCommand latestDisplayCmd = new DisplayCommand(
			DisplayCommand.Scope.DEFAULT);

	// Variables for complex edit
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

	/**
	 * MAIN LOGIC The method processInput takes in the user command as a string
	 * from UI and sends it to Parser, which returns an AbstractCommand object.
	 * After checking the type of the command object, a corresponding
	 * AbstractAction object is created and executed using a simplified version
	 * of the command pattern.
	 */

	public Output processInput(String userCmd) {
		AbstractCommand parsedCmd = parser.parseInput(userCmd);
		return executeCommand(parsedCmd);
	}

	public Output executeCommand(AbstractCommand parsedCmd) {
		assert(parsedCmd != null);
		
		Output feedbackToUI = null;
		AbstractAction action = null;
		prepareForExecution();

		switch (parsedCmd.getCmdType()) {
		case CREATE:
			CreateCommand createCmd = (CreateCommand) parsedCmd;
			action = new CreateAction(createCmd, taskList, latestDisplayedList);
			break;
		case EDIT:
			EditCommand editCmd = (EditCommand) parsedCmd;
			action = new EditAction(editCmd, taskList, latestDisplayedList,
					latestDisplayCmd);
			((EditAction) action).setComplexEdit(latestComplexEdit);
			((EditAction) action).setShouldComplexEdit(shouldKeepComplexEdit);
			break;
		case DELETE:
			DeleteCommand deleteCmd = (DeleteCommand) parsedCmd;
			action = new DeleteAction(deleteCmd, taskList, latestDisplayedList,
					latestDisplayCmd);
			break;
		case MARK:
			MarkCommand markCmd = (MarkCommand) parsedCmd;
			action = new MarkAction(markCmd, taskList, latestDisplayedList,
					latestDisplayCmd);
			break;
		case UNDO:
			action = new UndoAction(taskList, taskListStack, cmdHistoryStack);
			break;
		case UI:
			action = new UIAction();
			break;
		case SAVE:
			SaveCommand saveCmd = (SaveCommand) parsedCmd;
			action = new SaveAction(saveCmd, storage);
			break;
		case DISPLAY:
			DisplayCommand displayCmd = (DisplayCommand) parsedCmd;
			action = new DisplayAction(displayCmd, taskList,
					latestDisplayedList, latestDisplayCmd);
			return action.execute();
		case INVALID:
			Output feedback = new Output(Constants.MESSAGE_INVALID_COMMAND);
			feedback.setPriority(Priority.HIGH);
			return feedback;
		case EXIT:
			System.exit(0);
		}
		feedbackToUI = action.execute();
		postExecutionRoutine(parsedCmd);
		return feedbackToUI;
	}

	/**
	 * BACKGROUND ROUTINES These functions are ran before and after
	 * executeCommand() to facilitate changes to the state of variables such as
	 * taskList, taskListStack, latestDisplayedList, cmdHistoryStack.
	 */

	private void prepareForExecution() {
		checkEditKeywordPreservation();
		updateOverdueStatus();
	}

	private void postExecutionRoutine(AbstractCommand parsedCmd) {
		recordChange(parsedCmd);
		updateOverdueStatus();
		refreshLatestDisplayed();
	}

	private void checkEditKeywordPreservation() {
		if (shouldKeepComplexEdit.getState()) {
			this.cmdHistoryStack.pop();
		} else {
			latestComplexEdit = new EditCommand(Nature.SIMPLE);
		}
		shouldKeepComplexEdit.setFalse();
	}

	// UndoCommand will not be tracked as it is not undo-able!
	private void recordChange(AbstractCommand parsedCmd) {
		storage.write(this.taskList.getTasks());
		if (!(parsedCmd instanceof UndoCommand)) {
			TaskList clonedList = this.taskList.clone();
			this.taskListStack.push(clonedList);
			this.cmdHistoryStack.push(parsedCmd);
		}
	}

	private void refreshLatestDisplayed() {
		DisplayAction action = new DisplayAction(latestDisplayCmd, taskList,
				latestDisplayedList, latestDisplayCmd);
		action.execute();
	}
	
	private void updateOverdueStatus() {
		for (AbstractTask task : this.taskList.getTasks()) {
			if (task instanceof DeadlineTask) {
				DeadlineTask deadlineTask = (DeadlineTask) task;
				refreshOverdue(deadlineTask);
			}
		}
	}

	private void refreshOverdue(DeadlineTask task) {
		LocalDateTime dateTimeNow = LocalDateTime.now();
		if (dateTimeNow.isAfter(task.getEndDateTime())
				&& task.getStatus().equals(Status.UNDONE)) {
			task.setOverdue(true);
		}
	}

	/**
	 * LOGIC TEST METHODS These methods are written for testing of the Logic
	 * Component of Flexi-List
	 */

	public TaskList getTaskList() {
		return this.taskList;
	}

	public TaskList getLastDisplayedList() {
		return this.latestDisplayedList;
	}

	/**
	 * UI OBSERVER METHODS These methods will be called by UI to refresh the
	 * program view with the latest display state of Flexi-List
	 */

	public Output getLastDisplayed() {
		return new Output(latestDisplayedList);
	}

	public Output loadDefaultView() {
		latestDisplayCmd = new DisplayCommand(DisplayCommand.Scope.DEFAULT);
		DisplayAction displayAction = new DisplayAction(latestDisplayCmd,
				taskList, latestDisplayedList, latestDisplayCmd);
		return displayAction.execute();
	}

}
