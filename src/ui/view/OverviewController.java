package ui.view;

import ui.Main;

import java.util.ArrayList;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.FadeTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import shared.Output;
import shared.Output.Priority;
import storage.Storage;
import logic.Logic;
import shared.Constants;

public class OverviewController {
	

	
	
	@FXML
	private TextField input;
	
	@FXML
	private Label returnMessageLabel;
	
	@FXML 
	private Text returnMessageText;
	
	@FXML
	private Label helpMessageLabel;
	
	@FXML
	private Text helpMessageText;
	
	@FXML
	private AnchorPane taskPane;
	
	@FXML 
	private ScrollPane taskScrollPane;
	
	VBox vbox;
	
	Main mainApp;
	
	private String command;
	private boolean hasYear = false;
	private ArrayList<String> commandRecord = new ArrayList();
	private int commandPointer = 0;
	private ReturnMessage returnMessage;
	private HelpMessage helpMessage;
	
	Storage storage = new Storage();
	Logic logic = new Logic(storage);
	
	// Obtain a suitable logger.
	private static Logger logger = Logger.getLogger("UILogger");
	
	@FXML
	public void initialize() {
		initializeVBox();
		initializeTaskScrollPane();
		initializeMessages();
		initializeDisplay();
		initializeListener();
		initializeCommandTrace();
		setFocus();

	}
	
	private void initializeVBox() {
		vbox = new VBox(3);
		vbox.setPrefWidth(600);
		vbox.setPrefHeight(705);
		vbox.setStyle(String.format("-fx-background-color: %1$s;", Constants.DAY_COLOR));
		
	}
	
	private void initializeTaskScrollPane() {
		taskScrollPane.setContent(vbox);
		taskScrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
	}
	
	private void initializeMessages() {
		helpMessage = new HelpMessage(helpMessageLabel, helpMessageText);
		returnMessage = new ReturnMessage(returnMessageLabel, returnMessageText);
	}
	
	private void initializeDisplay() {
		logger.log(Level.INFO, "going to initialize the overview");
		
		try {
			Output output = processInput("display");
			Output lastDisplay = processInput("display");
	    	display(output, lastDisplay);
		} catch (Exception ex) {
			logger.log(Level.WARNING, "display command processing error", ex);
		}
		
		logger.log(Level.INFO, "end of processing display command");
	}
	
	private void initializeListener() {
    	input.textProperty().addListener((observable, oldValue, newValue) -> {
    		clearReturnMessage();
    	    helpMessage.genereateHelpMessage(newValue);
    	    displayYear(oldValue, newValue);
    	});
	}
	
	private void initializeCommandTrace() {
    	
    	input.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode().equals(KeyCode.UP)) {
					decreaseCommandPointer();
					input.setText(commandRecord.get(commandPointer));

				} else if (event.getCode().equals(KeyCode.DOWN)) {
					boolean hasNext = increaseCommandPointer();
					if (hasNext) {
						input.setText(commandRecord.get(commandPointer));
					} else {
						input.setText("");
					}
				}
				
			}
    		
    	});
	}
	
	private void setFocus() {
    	vbox.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

   	     @Override
   	     public void handle(MouseEvent event) {
   	         input.requestFocus();
   	         event.consume();
   	     }
   	});
	}
	
	private void decreaseCommandPointer() {
		if(commandPointer > 0) {
			commandPointer --;
		} else {
			commandPointer = 0;
		}
	}
	
	private boolean increaseCommandPointer() {
		if (commandPointer >= commandRecord.size() - 1) {
			commandPointer  = commandRecord.size();
			return false;
		} else {
			commandPointer ++;
			return true;
		}
	}
	
	private void displayYear(String oldValue,  String newValue) {
		if(oldValue.equals(Constants.COMMAND_SHOW_YEAR) && newValue.equals("")) {
			hasYear = true;
			Output output = new Output();
			output = logic.getLastDisplayed();
			display(output, output);
		} else if (oldValue.equals(Constants.COMMAND_HIDE_YEAR) && newValue.equals("")) {
			hasYear = false;
			Output output = new Output();
			output = logic.getLastDisplayed();
			display(output, output);
		}
	}
	
	private void changeView(String viewCommand) {
		if (viewCommand.equals(Constants.COMMAND_NIGHT)) {
			vbox.setStyle(String.format("-fx-background-color: %1$s;", Constants.NIGHT_COLOR));
		}
		
		if (viewCommand.equals(Constants.COMMAND_DAY)) {
			vbox.setStyle(String.format("-fx-background-color: %1$s;", Constants.DAY_COLOR));
		}
	}
	
	private void clearReturnMessage() {
		if(returnMessageLabel.getText().equals(null)) {
			return;
		}
		if(helpMessageLabel.getText().equals(null)) {
			return;
		}
		if(returnMessage.hasReturnMessage() && helpMessage.hasHelpMessage()) {
			returnMessage.cleanReturnMessage();
			
		}
		
	}
	
	private void displayFullHelpMessage() {
		FullHelpView fullHelpView = new FullHelpView(); 
		taskScrollPane.setContent(fullHelpView);
	}
	
	private void displayTasks(ArrayList<ArrayList<String>> outputArrayList, ArrayList<ArrayList<String>> currentOutputArrayList) {
		vbox.getChildren().clear();
		if(outputArrayList.size() == 0) {
			return;
		}
		for (ArrayList<String> list : outputArrayList) {
			TaskView taskView = new TaskView(list, hasYear);//createTaskGroup(list);
			vbox.getChildren().add(taskView);
			fadeInTaskView(taskView);
		}
	}
	
	private void fadeInTaskView(TaskView taskView) {
		FadeTransition ft = new FadeTransition(Duration.millis(600), taskView);
		ft.setFromValue(0.0);
		ft.setToValue(1.0);
		ft.play();
		
	}

	private void display(Output output, Output lastDisplay) {
		
		returnMessage.cleanReturnMessage();
		helpMessage.cleanHelpMessage();
		
		String message = output.getReturnMessage();
		returnMessage.setReturnMessage(message);
		
		Priority priority = output.getPriority();
		returnMessage.flashReturnMessage(priority);

		ArrayList<ArrayList<String>> outputArrayList = lastDisplay.getTasks();
			
		ArrayList<ArrayList<String>> currentOutputArrayList = output.getTasks();
			
		displayTasks(outputArrayList, currentOutputArrayList);
		
	}
	
	private void getInput() {
		command = input.getText();
		commandRecord.add(command);
		commandPointer = commandRecord.size();
		
	}
	
	public Output processInput(String input) {
		return logic.processInput(input);
	}
	
	private void displayOutput() {
    		getInput();
    		Output output = processInput(command);
    		Output lastDisplay = logic.getLastDisplayed();
        	display(output, lastDisplay);
	}
	
	public void onEnter(){
		returnMessage.cleanReturnMessage();
		if (isEmptyInput()) {
			return;
		} else if(isQuitHelpInput()) {
			taskScrollPane.setContent(vbox);
		} else if(isHelpInput()) {
			displayFullHelpMessage();
		}
		else if (isChangeViewInput()) {
			changeView(input.getText());
		} else {
			displayOutput();
		}
		input.clear();
	}
	
	private boolean isEmptyInput() {
		return input.getText().equals("");
	}
	
	private boolean isQuitHelpInput() {
		return input.getText().equals(Constants.COMMAND_QUIT_HELP);
	}
	
	private boolean isHelpInput() {
		return input.getText().equals(Constants.COMMAND_HELP);
	}
	
	private boolean isChangeViewInput() {
		return input.getText().equals(Constants.COMMAND_DAY) || input.getText().equals(Constants.COMMAND_NIGHT);
	}
	
	//The method is required for changing focus to the text field.
	public void onClickScrollPane() {
	}

    /**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;

    }

}
