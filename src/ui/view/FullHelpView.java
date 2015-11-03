//@@author A0133888N
package ui.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import shared.Constants;

/**
 * This class is used to display all of the help messages in the VBox.
 */
public class FullHelpView extends VBox {

	private static final String COLOR_BACKGROUND = "#c5eff7";// light blue
	private static final String HELP_FONT = "Monaco";
	private static final String HELP_FILE_PATH = "src/help.txt";
	private static final int HELP_FONTSIZE = 14;
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
			Text helpEntry = new Text();
			helpEntry.setText(helpMessage);
			helpEntry.setFont(Font.font(HELP_FONT, FontWeight.BOLD, HELP_FONTSIZE));
			this.getChildren().add(helpEntry);
			helpEntry.setTranslateX(TEXT_INDENT);
		}

	}

	/**
	 * Stores the text from the file to helpList.
	 */
	private void loadHelp() {
		File file = new File(HELP_FILE_PATH);

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				helpList.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		helpList.add(0, Constants.RETURN_TIP);

	}

}
