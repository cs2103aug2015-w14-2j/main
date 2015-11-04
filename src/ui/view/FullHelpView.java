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

	private static final String COLOR_BACKGROUND = "#ffffff";//"#c5eff7";// light blue
	private static final String HELP_FONT = "Monaco";
	private static final int HELP_FONTSIZE = 14;
	private static final int HELP_FONTSIZE_LARGE = 21;
	private static final int TEXT_INDENT = 10;
	private static final int HELP_SPACING = 5;
	private static final int HELPBOX_WIDTH = 600;
	private static final int HELPBOX_HEIGHT = 700;

	ArrayList<String> helpList;

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
	 * Display the content of helpList line by line.
	 */
	private void addHelpMessage() {
		for (String helpMessage : helpList) {
			TextFlow helpTextFlow = new TextFlow();
			for(String message : helpMessage.split(" ")) {
				Text helpEntry = new Text();
				helpEntry.setText(message + " ");
				formatText(helpEntry);
				helpTextFlow.getChildren().add(helpEntry);
			}
			formatTextFlow(helpTextFlow);

			this.getChildren().add(helpTextFlow);
			helpTextFlow.setTranslateX(TEXT_INDENT);
		}

	}

	/**
	 * Stores the text from the Constants to helpList.
	 */
	private void loadHelp() {
		helpList.addAll(Arrays.asList(Constants.HELP_MESSAGE_FULL));
		helpList.add(0, Constants.RETURN_TIP);
	}
	
	private void formatText(Text helpEntry) {
		String message = helpEntry.getText();
		System.out.println(helpEntry.getText());
		if(message.equals("[task ") || message.equals("name] ") || message.equals("[old ")  || message.equals("task ") || message.equals("[new ")) {
			helpEntry.setFill(Color.rgb(0, 179, 0));//green
		} else if (message.equals("[time] ") || message.equals("[TIME] ") ) {
			helpEntry.setFill(Color.rgb(0, 115, 230));//blue
		} else if (message.equals("[date] ") || message.equals("[DATE] ") ) {
			helpEntry.setFill(Color.rgb(230, 0, 0));//red
		}  else if (message.equals("[index] ") ) {
			helpEntry.setFill(Color.rgb(255, 128, 0));//orange
		}  else if (message.equals("[DAY] ") || message.equals("[MONTH] ") ) {
			helpEntry.setFill(Color.rgb(178, 161, 199));//grayish violet
		}
	}

	private void formatTextFlow(TextFlow helpTextFlow) {
		String firstWord = ((Text) helpTextFlow.getChildren().get(0)).getText();
		if (firstWord.startsWith("COMMANDS") || firstWord.startsWith("[")
				|| firstWord.startsWith("SHORTCUTS")) {
			helpTextFlow.getChildren().forEach(helpEntry -> {
				((Text) helpEntry).setFont(Font.font(HELP_FONT, FontWeight.BOLD, HELP_FONTSIZE_LARGE));
				((Text) helpEntry).setUnderline(true);
			});
		} else if (firstWord.startsWith("To") || firstWord.startsWith("Command") || firstWord.startsWith("and")) {
			helpTextFlow.getChildren().forEach(helpEntry -> {
				((Text) helpEntry).setUnderline(true);
				((Text) helpEntry).setFont(Font.font(HELP_FONT, FontWeight.BOLD, HELP_FONTSIZE));
			});
		} else {
			helpTextFlow.getChildren().forEach(helpEntry -> {
			((Text) helpEntry).setFont(Font.font(HELP_FONT, FontWeight.BOLD, HELP_FONTSIZE));
			});
		}
	}

}
