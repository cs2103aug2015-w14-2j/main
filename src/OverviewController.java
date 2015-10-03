import java.lang.reflect.Array;
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
	 private ArrayList<ArrayList<String>> displayList;
	 
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
	
	public ArrayList<ArrayList<String>> processInput(String input) {
		ArrayList<ArrayList<String>> example = new ArrayList();
		ArrayList<String> example2 = new ArrayList(); 
		ArrayList<String> example3 = new ArrayList(); 
		example2.add("1.");
		example2.add("attend lecture");
		example2.add("8:00");
		example2.add("3-10-2015");
		example3.add("task created successfully");
		example.add(example2);
		example.add(example3);
		return example; 
	}
	
	public void editDisplay() {
		for(ArrayList<String> list : displayList)  {
			for(String s : list) {
				display = display + s + "  ";
			}
			display = display + "\n";

		}
		display = display + "======================================\n";
		
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
