package textUI;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import shared.Output;

import com.sun.xml.internal.bind.v2.runtime.property.ValueProperty;

import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import logic.Logic;

public class OverviewController {
	
	Logic logic = new Logic();
	
	 @FXML
	 private Text message;
	 @FXML
	 private Text timeTextBox;
	 
	 private Calendar calendar = Calendar.getInstance();
	 private SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	 
	 @FXML
	 private TextField input;
	 
	 @FXML
	 private ScrollPane displayScroll;
	 
	 @FXML 
	 private AnchorPane displayAnchor;
	 
	 private String command;
	 
	 private String display = "";
	 
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
		displayScroll.setFitToHeight(true);
		displayScroll.setFitToWidth(true);
		message.wrappingWidthProperty().bind(displayScroll.widthProperty());;
		setTime();
	}
	
	private void setTime() {
		String currentTime = timeFormat.format(calendar.getTime());
		timeTextBox.setText(currentTime);
	}
	
	private void getInput() {
		command = input.getText();
		
	}
	
	private boolean isEnter(char s) {
		return s == '\n' || s == '\r';
	}
	
	
	
	public Output processInput(String input) {
		//return null;
		
		return logic.processInput(input);
		/*
		ArrayList<ArrayList<String>> example = new ArrayList();
		ArrayList<String> example2 = new ArrayList(); 
		ArrayList<String> example3 = new ArrayList(); 
		ArrayList<String> example4 = new ArrayList();
		
		example2.add("1.");
		example2.add("attend lecture long long long long long long long ");
		example2.add("8:00");
		example2.add("3-10-2015");
		example2.add("10:00");
		example2.add("3-10-2015");
		example4.add("2.");
		example4.add("attend lecture ");
		example4.add("8:00");
		example4.add("3-10-2015");
		example4.add("10:00");
		example4.add("3-10-2015");
		example3.add("all tasks displayed");
		example.add(example2);
		example.add(example4);
		example.add(example3);
		return example; 
		*/
	}
	
	private String formatIndex(String s) {
		
		assert(s != null);
		
		String indexPadded = String.format("%-5s", s);
		return indexPadded;
	}
	
	private String formatTaskName(String s) {
		
		assert(s != null);
		
		if(s.length() > 30) {
			s = s.substring(0, 29);
		}
		String namePadded = String.format("%-30s", s);
		return namePadded;
	}
	
	private String formatTime(String s) {
		
		assert( s != null);
		
		String timePadded = String.format("%-7s", s);
		return timePadded;
	}
	
	private String formatDate(String s) {
		
		assert( s != null);
		
		String datePadded = String.format("%-12s", s);
		return datePadded;
	}
	
	
	private void editTasks(ArrayList<String> list) {
		
		assert(list.size() >= 6);
		
		String index = list.get(0);
		String taskName = list.get(1);
		String startTime = list.get(2);
		String startDate = list.get(3);
		String endTime = list.get(4);
		String endDate = list.get(5);
		
		String oneTaskLine = formatIndex(index) +formatTaskName(taskName) + formatTime(startTime)  
				+ formatDate(startDate) + formatTime(endTime) + formatDate(endDate);
		oneTaskLine = oneTaskLine + "\n";
		
		display = display + oneTaskLine;

	}
	
	private boolean isMessage(ArrayList<String> list) {
		if(list.size() == 1) {
			return true;
		} else {
			return false;
		}
	}
	
	private void editMessage(ArrayList<String> list) {
		display = display + list.get(0);
		display = display + "\n";
	}
	
	private boolean isLongList(ArrayList<ArrayList<String>> displayList) {
		if(displayList.size() > 1) {
			return true;
		} else {
			return false;
		}
	}
	
	private void addbar() {
		display = display + "===================================\n";
	}
	
	//need to change to grid view
	private void editDisplay(Output output) {
		ArrayList<ArrayList<String>> list = output.getTasks();
		
		assert(list.size() > 0);		
		if(isLongList(list)) {
			addbar();
		}
		
		for(ArrayList<String> listForOneTask : list)  {
			if (!isMessage(listForOneTask)) {
				editTasks(listForOneTask);
			} else {
				editMessage(listForOneTask);
			}
		}
		
		if(isLongList(list)) {
			addbar();
		}
		
	}
	
	@FXML
	private void displayOutput() {

		input.setOnKeyPressed(new EventHandler<KeyEvent>()
	    {
	        @Override
	        public void handle(KeyEvent ke)
	        {
	            if (ke.getCode().equals(KeyCode.ENTER))
	            {
	            	timeTextBox.setText("");
	            	
	        		getInput();
	        		Output output = processInput(command);
	            	System.out.println(output.getTasks());
	            	System.out.println(output.getReturnMessage());
	        		editDisplay(output);
	        		message.setText(display);
	        		input.clear();


	            }
	        }
	    });

	}
	
    /**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setUIMain(UIMain UIMain) {
    	
    	assert(UIMain != null);
    	
        this.UIMain = UIMain;

        // Add observable list data to the table
        //messageLabel.setText(UIMain.getMessage());
    }


}
