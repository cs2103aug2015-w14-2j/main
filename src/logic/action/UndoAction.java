package logic.action;

import java.util.Stack;

import logic.TaskList;
import shared.Constants;
import shared.Output;
import shared.Output.Priority;
import shared.command.AbstractCommand;

//@@author A0124828B
public class UndoAction extends AbstractAction {

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
			// Earliest recorded version for current run of program
			Output feedback = new Output(Constants.MESSAGE_INVALID_COMMAND);
			feedback.setPriority(Priority.HIGH);
			return feedback;
		} else {
			taskListStack.pop();
			this.taskList.replaceContents((taskListStack.peek()).clone());
			AbstractCommand undoneCommand = cmdHistoryStack.pop();
			String undoMessage = undoneCommand.getUndoMessage();
			return new Output(undoMessage);
		}
	}

}