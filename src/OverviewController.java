import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

public class OverviewController {
	
	 @FXML
	 private Text message;
	 
	 @FXML
	 private TextField input;
	 
	 private String command;
	 
	 private String display = "";
	 private ArrayList<String> displayList;
	 
	 // Reference to the main application
	 private UIMain UIMain;
	 
	 public OverviewController() {
	 }
	 
	 /**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
	// Initialize the  table
		
		  // Listen for selection changes
	}
	
	public void getInput() {
		command = input.getText();
		
	}
	
	public boolean isEnter(char s) {
		return s == '\n' || s == '\r';
	}
	
	public ArrayList<String> processInput(String input) {
		ArrayList<String> example = new ArrayList();
		example.add("1. lecture 8am\n");
		example.add("task created successfully\n");
		
		return example; 
	}
	
	public void editDisplay() {
		for(String s : displayList)  {
			display = display + s;
		}
		
	}
	
	@FXML
	public void displayOutput() {

		input.setOnKeyPressed(new EventHandler<KeyEvent>()
	    {
	        @Override
	        public void handle(KeyEvent ke)
	        {
	            if (ke.getCode().equals(KeyCode.ENTER))
	            {
	        		getInput();
	        		displayList = processInput(command);
	        		editDisplay();
	        		message.setText(display);
	        		input.clear();
	        		displayList.clear();
	            }
	        }
	    });

	}
	

	
	/*
	input.setOnKeyPressed(new EventHandler<KeyEvent>()
    {
        @Override
        public void handle(KeyEvent ke)
        {
            if (ke.getCode().equals(KeyCode.ENTER))
            {
                doSomething();
            }
        }
    });
    */
	
    /**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setUIMain(UIMain UIMain) {
        this.UIMain = UIMain;

        // Add observable list data to the table
        //messageLabel.setText(UIMain.getMessage());
    }


}
