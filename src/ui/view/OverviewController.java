package ui.view;

import ui.Main;

import java.util.ArrayList;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.SequentialTransition;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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
	private Label returnMessage;
	
	@FXML 
	private Text returnMessageText;
	
	@FXML
	private Label helpMessage;
	
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
		returnMessage.setFont(Font.font(14));
		helpMessage.setFont(Font.font(14));
		returnMessageText.setFont(Font.font(14));
		helpMessageText.setFont(Font.font(14));
		returnMessage.setTextFill(Color.TRANSPARENT);
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
    	    genereateHelpMessage(newValue);
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
		if(oldValue.equals("show year") && newValue.equals("")) {
			hasYear = true;
			Output output = new Output();
			output = logic.getLastDisplayed();
			display(output, output);
		} else if (oldValue.equals("hide year") && newValue.equals("")) {
			hasYear = false;
			Output output = new Output();
			output = logic.getLastDisplayed();
			display(output, output);
		}
	}
	
	private void changeView(String oldValue) {
		if (oldValue.equals("night")) {
			vbox.setStyle(String.format("-fx-background-color: %1$s;", Constants.NIGHT_COLOR));
		}
		
		if (oldValue.equals("day")) {
			vbox.setStyle(String.format("-fx-background-color: %1$s;", Constants.DAY_COLOR));
		}
	}
	
	private void clearReturnMessage() {
		if(returnMessage.getText().equals(null)) {
			return;
		}
		if(helpMessage.getText().equals(null)) {
			return;
		}
		if(returnMessage.getText().length() > 0 && helpMessage.getText().length() > 0) {
			returnMessage.setText("");
			
		}
		
		if(returnMessageText.getText().length() > 0 && helpMessage.getText().length() > 0) {
			returnMessageText.setText("");
		}
		
	}
	
	private void genereateHelpMessage(String input) {
		String[] inputWords = input.split(" ");
		String command;
		
		clearReturnMessage();
		
		if (inputWords.length > 0) {
			command = inputWords[0];
		} else {
			command = ""; 
		}
		
		switch(command) {
			case "create" : 
				helpMessage.setText(Constants.HELP_MESSAGE_CREATE);
				break;
			case "edit" :
				helpMessage.setText(Constants.HELP_MESSAGE_EDIT);
				break;
			case "delete" :
				helpMessage.setText(Constants.HELP_MESSAGE_DELETE);
				break;
			case "display" :
				helpMessage.setText(Constants.HELP_MESSAGE_DISPLAY);
				break;
			case "mark" :
				helpMessage.setText(Constants.HELP_MESSAGE_MARK);
				break;
			case "ummark" :
				helpMessage.setText(Constants.HELP_MESSAGE_UNMARK);
				break;
			default : 
				helpMessage.setText("");
				break;
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
			Group group = createTaskGroup(list);
			vbox.getChildren().add(group);
			FadeTransition ft = new FadeTransition(Duration.millis(600), group);
			ft.setFromValue(0.0);
			ft.setToValue(1.0);
			ft.play();
		}
	}

	private Group createTaskGroup(ArrayList<String> list) {
		return new TaskView(list, hasYear);
	}
	
	private void clearHelpMessage() {
		if (helpMessage.getText().length() > 0) {
			helpMessage.setText("");
		}
		
	}
	
	private void display(Output output, Output lastDisplay) {

		returnMessage.setText("");
		returnMessageText.setText("");
		String message;
		
		if(output.getReturnMessage() == null) {
			message = "";
		} else {
			message = output.getReturnMessage();
		}

		clearHelpMessage();
		returnMessage.setText(message);
		returnMessageText.setText(message);
			
		Priority priority = output.getPriority();
		flashReturnMessage(priority);

		ArrayList<ArrayList<String>> outputArrayList = new ArrayList();
		outputArrayList = lastDisplay.getTasks();
			
		ArrayList<ArrayList<String>> currentOutputArrayList = new ArrayList();
		currentOutputArrayList = output.getTasks();
			
		displayTasks(outputArrayList, currentOutputArrayList);
		

		
	}
	
	private void flashReturnMessage(Priority priority) {
		Color color;
		
		switch (priority) {
			case LOW :
				color = Color.GREEN;
				break;
			case HIGH :
				color = Color.RED;
				break;
			default : 
				color = Color.BLACK;
				break;
		}
		
		FillTransition textWait = new FillTransition(Duration.millis(600), returnMessageText, Color.BLACK, Color.BLACK);
		textWait.setCycleCount(1);
		
		FillTransition textHighlight = new FillTransition(Duration.millis(1200), returnMessageText, Color.BLACK, color);
		textHighlight.setCycleCount(1);
		
		FillTransition textBlack = new FillTransition(Duration.millis(1200), returnMessageText, color, Color.BLACK);
		textBlack.setCycleCount(1);
		
	    SequentialTransition sT = new SequentialTransition(textWait, textHighlight, textBlack);
	    sT.play();

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
    		input.clear();

	}
	
	public void onEnter(){
		if (isEmptyInput()) {
			return;
		} else if(isQuitHelpInput()) {
			input.clear();
			taskScrollPane.setContent(vbox);
		} else if(isHelpInput()) {
			returnMessage.setText("");
			returnMessageText.setText("");
			displayFullHelpMessage();
			input.clear();
		}
		else if (isChangeViewInput()) {
			changeView(input.getText());
			input.clear();
		} else {
			displayOutput();
		}
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
		return input.getText().equals("day") || input.getText().equals("night");
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
