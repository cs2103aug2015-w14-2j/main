//@@author A0133888N
package ui.view;

import java.util.ArrayList;
import java.util.Arrays;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import shared.Constants;

/**
 * This class is used to display all of the help messages in the VBox.
 */
public class FullHelpView extends VBox {

	private static final boolean isWin = System.getProperty("os.name").toLowerCase().contains("win");
	private static final String COLOR_BACKGROUND = "#ffffff";// white
	private static final String HELP_FONT = isWin ? "Courier New": "Monaco";
	private static final int HELP_FONTSIZE = 14;
	private static final int HELP_FONTSIZE_LARGE = 21;
	private static final int TEXT_INDENT = 10;
	private static final int HELP_SPACING = 5;
	private static final int HELPBOX_WIDTH = 600;
	private static final int HELPBOX_HEIGHT = 700;
	private static final int LINE_LENGTH = 70;

	private static final Color COLOR_TASKNAME = Color.rgb(0, 179, 0);// light green
	private static final Color COLOR_TIME = Color.rgb(0, 115, 230); // blue
	private static final Color COLOR_DATE = Color.rgb(230, 0, 0); // red
	private static final Color COLOR_INDEX = Color.rgb(255, 128, 0);// orange
	private static final Color COLOR_DAY_MONTH = Color.rgb(165, 145, 189);// light violet
	private static final Color COLOR_PATH = Color.rgb(128, 0, 128);// purple

	private ArrayList<String> helpList;

	public FullHelpView() {

		initialize();
		loadHelp();
		addHelpMessage();
	}

	private void initialize() {
		this.setSpacing(HELP_SPACING);
		this.setPrefWidth(HELPBOX_WIDTH);
		this.setPrefHeight(HELPBOX_HEIGHT);
		this.setStyle(String.format("-fx-background-color: %1$s;", COLOR_BACKGROUND));

		helpList = new ArrayList<String>();
	}

	/**
	 * Stores the text from the Constants to helpList.
	 */
	private void loadHelp() {
		helpList.addAll(Arrays.asList(Constants.HELP_MESSAGE_FULL));
	}

	/**
	 * Display the content of helpList line by line.
	 */
	private void addHelpMessage() {
		for (String helpMessage : helpList) {
			TextFlow helpTextFlow = new TextFlow();
			addText(helpTextFlow, helpMessage);
			formatTextFlow(helpTextFlow, helpMessage);
		}
	}

	private void addText(TextFlow helpTextFlow, String helpMessage) {
		for (String message : helpMessage.split(" ")) {
			Text helpEntry = new Text();
			helpEntry.setText(message + " ");
			formatText(helpEntry, message);
			helpTextFlow.getChildren().add(helpEntry);
		}
	}

	private void formatText(Text helpEntry, String message) {
		switch (message) {
		case "[task":
		case "name]":
		case "[old":
		case "task":
		case "[new":
			helpEntry.setFill(COLOR_TASKNAME);
			break;
		case "[time]":
		case "[TIME]":
			helpEntry.setFill(COLOR_TIME);
			break;
		case "[date]":
		case "[DATE]":
			helpEntry.setFill(COLOR_DATE);
			break;
		case "[index]":
			helpEntry.setFill(COLOR_INDEX);
			break;
		case "[DAY]":
		case "[MONTH]":
			helpEntry.setFill(COLOR_DAY_MONTH);
			break;
		case "[path]":
			helpEntry.setFill(COLOR_PATH);
			break;
		default:
			break;
		}
	}

	private void formatTextFlow(TextFlow helpTextFlow, String helpMessage) {
		String firstWord = helpMessage.split(" ")[0];
		if (firstWord == null) {
			return;
		}
		// A message that is too long cannot be displayed correctly.
		assert helpMessage.length() <= LINE_LENGTH;
		switch (firstWord) {
		case "COMMANDS":
		case "[TIME]":
		case "[DATE]":
		case "[DAY]":
		case "[MONTH]":
		case "SHORTCUTS":
			helpTextFlow.getChildren().forEach(helpEntry -> {
				((Text) helpEntry).setFont(Font.font(HELP_FONT, FontWeight.BOLD, HELP_FONTSIZE_LARGE));
				((Text) helpEntry).setUnderline(true);
			});
			break;
		case "To":
		case "Command":
		case "and":
			helpTextFlow.getChildren().forEach(helpEntry -> {
				((Text) helpEntry).setUnderline(true);
				((Text) helpEntry).setFont(Font.font(HELP_FONT, FontWeight.BOLD, HELP_FONTSIZE));
			});
			break;
		default:
			helpTextFlow.getChildren().forEach(helpEntry -> {
				((Text) helpEntry).setFont(Font.font(HELP_FONT, FontWeight.BOLD, HELP_FONTSIZE));
			});
			break;
		}
		this.getChildren().add(helpTextFlow);
		helpTextFlow.setTranslateX(TEXT_INDENT);
	}

}
