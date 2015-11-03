//@@author A0133888N
package ui.view;

import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import shared.Constants;

public class HelpMessage {
	private Label helpMessageLabel;
	private Text helpMessageText;
	private String input;
	
	public HelpMessage(Label helpMessageLabel, Text helpMessageText) {
		initialize(helpMessageLabel, helpMessageText);
	}

	private void initialize(Label helpMessageLabel, Text helpMessageText) {
		this.helpMessageLabel = helpMessageLabel;
		this.helpMessageText = helpMessageText;
		this.helpMessageText.setFont(Font.font(14));
		this.helpMessageLabel.setFont(Font.font(14));
	}
	
	protected void genereateHelpMessage(String input) {
		this.input = input;
		String[] inputWords = this.input.split(" ");
		String command;
		
		if (inputWords.length > 0) {
			command = inputWords[0];
		} else {
			command = ""; 
		}
		
		switch(command) {
			case "create" : 
				helpMessageLabel.setText(Constants.HELP_MESSAGE_CREATE);
				break;
			case "edit" :
				helpMessageLabel.setText(Constants.HELP_MESSAGE_EDIT);
				break;
			case "delete" :
				helpMessageLabel.setText(Constants.HELP_MESSAGE_DELETE);
				break;
			case "display" :
				helpMessageLabel.setText(Constants.HELP_MESSAGE_DISPLAY);
				break;
			case "mark" :
				helpMessageLabel.setText(Constants.HELP_MESSAGE_MARK);
				break;
			case "ummark" :
				helpMessageLabel.setText(Constants.HELP_MESSAGE_UNMARK);
				break;
			default : 
				helpMessageLabel.setText("");
				break;
		}
	}
	
	protected void cleanHelpMessage() {
		helpMessageLabel.setText("");
		helpMessageText.setText("");
	}
	
	protected boolean hasHelpMessage() {
		return helpMessageLabel.getText().length() > 0;
	}
}
