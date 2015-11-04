package logic.action;

import logic.TaskList;
import shared.Output;
import shared.command.AbstractCommand;
import shared.command.DisplayCommand;

//@@author A0124828B
public abstract class AbstractAction {
	
	protected TaskList taskList;
	protected TaskList latestDisplayedList;
	protected DisplayCommand latestDisplayCmd;
	
	public abstract Output execute();
	
	public void setTaskList(TaskList taskList) {
		this.taskList = taskList;
	};
	
	public void setLatestDisplayed (TaskList latestDisplayed) {
		this.latestDisplayedList = latestDisplayed;
	}
	public void setLatestDisplayCmd(DisplayCommand latestDisplayCmd) {
		this.latestDisplayCmd = latestDisplayCmd;
	}
}
