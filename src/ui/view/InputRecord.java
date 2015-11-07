//@@author A0133888N
package ui.view;

import java.util.ArrayList;

/**
 * This class is used to record keyboard inputs. It works together with up and
 * down key to go to the previous and next input (if there exist).
 */
public class InputRecord {

	private String command;
	private ArrayList<String> commandRecord;
	private int commandPointer;

	public InputRecord() {
		initialize();
	}

	private void initialize() {
		command = "";
		commandRecord = new ArrayList<String>();
		commandPointer = 0;
	}

	protected void setCommand(String command) {
		this.command = command;
	}

	private void setCommand() {
		assert commandPointer >= 0 && commandPointer <= commandRecord.size();
		this.command = commandRecord.get(commandPointer);
	}

	protected String getCommand() {
		return this.command;
	}

	protected void addInputRecord(String command) {
		commandRecord.add(command);
	}

	protected void setNextPointer() {
		commandPointer = commandRecord.size();
	}

	protected void decreaseCommandPointer() {
		if (commandPointer > 0) {
			commandPointer--;
		} else {
			commandPointer = 0;
		}
		setCommand();
	}

	protected boolean increaseCommandPointer() {
		if (commandPointer >= commandRecord.size() - 1) {
			commandPointer = commandRecord.size();
			return false;
		} else {
			commandPointer++;
			setCommand();
			return true;
		}
	}

	/**
	 * It is used to show the previous input.
	 * 
	 * @return the previous input. If there is no previous input, show the first
	 *         input recorded.
	 * 
	 */
	protected String showLastInput() {
		decreaseCommandPointer();
		return getCommand();
	}

	/**
	 * It is used to show the next input.
	 * 
	 * @return the next input. If there is no next input, show nothing.
	 */
	protected String showNextInput() {
		boolean hasNext = increaseCommandPointer();
		if (hasNext) {
			return getCommand();
		} else {
			return "";
		}
	}
}
