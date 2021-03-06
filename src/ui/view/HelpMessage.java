//@@author A0133888N
package ui.view;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import shared.Constants;

/**
 * This class is used to generate and display immediate help messages upon
 * typing.
 */
public class HelpMessage {

	private static final int MESSAGE_FONT = 15;

	private Label helpMessageLabel;
	private Text helpMessageText;
	private String input;

	public HelpMessage(Label helpMessageLabel, Text helpMessageText) {
		initialize(helpMessageLabel, helpMessageText);
	}

	private void initialize(Label helpMessageLabel, Text helpMessageText) {
		this.helpMessageLabel = helpMessageLabel;
		this.helpMessageText = helpMessageText;
		this.helpMessageText.setFont(Font.font(MESSAGE_FONT));
		this.helpMessageLabel.setFont(Font.font(MESSAGE_FONT));
	}

	/**
	 * Generate help messages with regard to the input. Only specific inputs
	 * that match certain keywords will work.
	 * 
	 * @param input
	 */
	protected void genereateHelpMessage(String input) {
		this.input = input;
		String[] inputWords = this.input.split(" ");
		String command;

		if (inputWords.length > 0) {
			command = inputWords[0];
		} else {
			command = "";
		}

		switch (command) {
		case "create":
			helpMessageLabel.setText(Constants.HELP_MESSAGE_CREATE);
			break;
		case "edit":
			helpMessageLabel.setText(Constants.HELP_MESSAGE_EDIT);
			break;
		case "delete":
			helpMessageLabel.setText(Constants.HELP_MESSAGE_DELETE);
			break;
		case "display":
			helpMessageLabel.setText(Constants.HELP_MESSAGE_DISPLAY);
			break;
		case "mark":
			helpMessageLabel.setText(Constants.HELP_MESSAGE_MARK);
			break;
		case "ummark":
			helpMessageLabel.setText(Constants.HELP_MESSAGE_UNMARK);
			break;
		case "undo":
			helpMessageLabel.setText(Constants.HELP_MESSAGE_UNDO);
			break;
		case "search":
			helpMessageLabel.setText(Constants.HELP_MESSAGE_SEARCH);
			break;
		case "save":
			helpMessageLabel.setText(Constants.HELP_MESSAGE_SAVE);
			break;
		case "help":
			helpMessageLabel.setText(Constants.HELP_MESSAGE_HELP);
			break;
		default:
			helpMessageLabel.setText("");
			break;
		}
	}

	protected void cleanHelpMessage() {
		assert helpMessageLabel != null;
		assert helpMessageText != null;
		helpMessageLabel.setText("");
		helpMessageText.setText("");
	}

	protected boolean hasHelpMessage() {
		return helpMessageLabel.getText().length() > 0;
	}

	protected void changeTheme(String command) {
		if (command.equals(Constants.COMMAND_DAY)) {
			helpMessageLabel.setTextFill(Color.BLACK);
			helpMessageText.setFill(Color.BLACK);
		} else if (command.equals(Constants.COMMAND_NIGHT)) {
			helpMessageLabel.setTextFill(Color.WHITE);
			helpMessageText.setFill(Color.WHITE);
		}
	}
}
