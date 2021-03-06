package logic.action;

import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import logic.TaskList;
import shared.Constants;
import shared.Output;
import shared.SharedLogger;
import shared.Output.Priority;
import shared.command.AbstractCommand;

//@@author A0124828B

/**
 * Undo is implemented by keeping a stack of states of taskList as well as a stack of user 
 * entered commands. 
 * Display, Undo, Save and UI commands cannot be undone
 */

public class UndoAction extends AbstractAction {
	private Logger logger = SharedLogger.getInstance().getLogger();
	private TaskList taskList;
	private Stack<TaskList> taskListStack;
	private Stack<AbstractCommand> cmdHistoryStack;

	public UndoAction(TaskList taskList, Stack<TaskList> taskListStack,
			Stack<AbstractCommand> cmdStack) {
		this.taskList = taskList;
		this.taskListStack = taskListStack;
		this.cmdHistoryStack = cmdStack;
	}

	public Output execute() {
		if (taskListStack.size() == 1) {
			// Bottom of the stack, earliest recorded version for current run of
			// program
			Output feedback = new Output(Constants.MESSAGE_INVALID_COMMAND);
			feedback.setPriority(Priority.HIGH);
			logger.log(Level.INFO, "Reached end of stack, invalid undo!");
			return feedback;
		} else {
			taskListStack.pop();
			this.taskList.replaceContents((taskListStack.peek()).clone());
			AbstractCommand undoneCommand = cmdHistoryStack.pop();
			String undoMessage = undoneCommand.getUndoMessage();
			logger.log(Level.INFO, "Undo successful!");
			return new Output(undoMessage);
		}
	}

}
