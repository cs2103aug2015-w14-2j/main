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

public class FullHelpView extends VBox{
	
	private final String COLOR_BACKGROUND = "#c5eff7";
	private final String HELP_FONT = "Monaco";
	private final String QUIT_HELP_COMMAND = "quit help";
	private final String HELP_FILE_PATH = "src/help.txt";
	private final String RETURN_TIP = String.format("\nEnter \"%1$s\" to return to the normal view.\n===================================================================", QUIT_HELP_COMMAND);
	private final int HELP_FONTSIZE = 14;
	private final int TEXT_INDENT = 10;
	private final int HELP_SPACING = 5;
	private final int HELPBOX_WIDTH = 600;
	private final int HELPBOX_HEIGHT = 700;
	
	public FullHelpView() {
		
		initialize();

		ArrayList<String> helpList = loadHelp();
		helpList.add(0, RETURN_TIP);
		for(String helpMessage : helpList) {
			addHelpMessage(helpMessage);
		}
		
	}
	
	private void initialize() {
		this.setSpacing(HELP_SPACING);
		this.setPrefWidth(HELPBOX_WIDTH);
		this.setPrefHeight(HELPBOX_HEIGHT);
		this.setStyle(String.format("-fx-background-color: %1$s;", COLOR_BACKGROUND));
		
	}
	
	private void addHelpMessage(String helpMessage) {
		Text helpEntry = new Text();
		helpEntry.setText(helpMessage);
		helpEntry.setFont(Font.font (HELP_FONT, FontWeight.BOLD, HELP_FONTSIZE));
		this.getChildren().add(helpEntry);
		helpEntry.setTranslateX(TEXT_INDENT);
		
	}
	
	private ArrayList<String> loadHelp() {
		ArrayList<String> helpList = new ArrayList<String>();
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
		
		return helpList;
	}

}
