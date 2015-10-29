package ui.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javafx.scene.layout.VBox;

public class FullHelpView extends VBox{
	
	public FullHelpView() {
		//this.set
		
	}
	
	private ArrayList<String> loadHelp() {
		ArrayList<String> helpList = new ArrayList<String>();
		File file = new File("src/help.txt");
		
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
