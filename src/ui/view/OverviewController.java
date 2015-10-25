package ui.view;

import ui.Main;

import java.io.File;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import shared.Output;

import logic.Logic;

public class OverviewController {
	
	private final int MAXIMUM_LENGTH = 40;
	private final int INDEX_FONT = 14;
	private final int TASKNAME_FONT = 18;
	private final int BOUNEDED_CONTAINER_HEIGHT = 60;
	private final int UNBOUNEDED_CONTAINER_HEIGHT = 45;
	private final String DAY_COLOR = "#afeeee";
	private final String NIGHT_COLOR = "#1a237e;";
	
	@FXML
	private TextField input;
	
	@FXML
	private Text returnMessage;
	
	@FXML
	private Text helpMessage;
	
	@FXML
	private AnchorPane taskPane;
	
	@FXML 
	private ScrollPane taskScrollPane;
	
	VBox vbox;
	
	Main mainApp;
	
	private String command;
	
	Logic logic = new Logic();
	
	@FXML
	public void initialize() {
		
		vbox = new VBox(10);
		vbox.setPrefWidth(600);
		vbox.setPrefHeight(600);
		vbox.setStyle(String.format("-fx-background-color: %1$s;", DAY_COLOR));
		taskScrollPane.setContent(vbox);
		
		Output output = processInput("display");
		Output lastDisplay = processInput("display");
    	display(output, lastDisplay);
    	input.textProperty().addListener((observable, oldValue, newValue) -> {
    	    System.out.println("textfield changed from " + oldValue + " to " + newValue);
    	    changeView(oldValue, newValue);
    	    genereateHelpMessage(newValue);
    	    
    	});
	}
	
	private void changeView(String oldValue, String newValue) {
		if (oldValue.equals("night") && newValue.equals("")) {
			vbox.setStyle(String.format("-fx-background-color: %1$s;", NIGHT_COLOR));
		}
		
		if (oldValue.equals("day") && newValue.equals("")) {
			vbox.setStyle(String.format("-fx-background-color: %1$s;", DAY_COLOR));
		}
	}
	
	private void genereateHelpMessage(String input) {
		String[] inputWords = input.split(" ");
		String command;
		if (inputWords.length > 0) {
			command = inputWords[0];
		} else {
			command = ""; 
		}
		
		switch(command) {
			case "create" : 
				helpMessage.setText("create ... from ... to ...");
				break;
			case "edit" :
				helpMessage.setText("edit /index to ...(new name)");
				break;
			case "delete" :
				helpMessage.setText("delete /index");
				break;
			case "display" :
				helpMessage.setText("display ...(time, task name)");
				break;
			case "mark" :
				helpMessage.setText("mark /index");
				break;
			case "ummark" :
				helpMessage.setText("ummark /index");
				break;
			default : 
				helpMessage.setText("");
				break;
		}
	}
	
	
	private void displayInitialView(ArrayList<ArrayList<String>> outputArrayList) {
	
	}
	
	private void displayTasks(ArrayList<ArrayList<String>> outputArrayList) {
		vbox.getChildren().clear();
		for (ArrayList<String> list : outputArrayList) {
			Group group = createTaskGroup(list);
			vbox.getChildren().add(group);
			FadeTransition ft = new FadeTransition(Duration.millis(600), group);
			ft.setFromValue(0.0);
			ft.setToValue(1.0);
			ft.play();
		}
	}
	
	private Rectangle createTaskContainer(String start, String end, boolean isDone) {
		Rectangle r1 = new Rectangle();
		r1.setWidth(600);
		if(start.replaceAll("\\s+","").length() + end.replaceAll("\\s+","").length() == 0) {
			r1.setHeight(UNBOUNEDED_CONTAINER_HEIGHT);
		} else {
			r1.setHeight(BOUNEDED_CONTAINER_HEIGHT);
		}
		
		if (isDone) {
			r1.setFill(Color.CHARTREUSE);
		} else {
			r1.setFill(Color.LIGHTSKYBLUE);
		} 
		 
		return r1;
	}
	
	private String getIndex(ArrayList<String> list) {
		String index = list.get(0);
		return index;
	}
	
	private String getTaskName(ArrayList<String> list) {
		String taskName = list.get(1);
		
		if (taskName.length() > MAXIMUM_LENGTH) {
			taskName = taskName.substring(0, MAXIMUM_LENGTH);
		}
		return taskName;
	}
	
	private String getStartTimeDate(ArrayList<String> list) {
		String start = list.get(2) + " " + list.get(3);
		return start;
	}
	
	private String getEndTimeDate(ArrayList<String> list) {
		String end = list.get(4) + " " + list.get(5);
		return end;
	}
	
	private String modifyStart(String start) {
		if (start.replaceAll("\\s+","").length() > 0) {
			start = "from " + start;
		}
		return start;
	}
	
	private String modifyEnd(String start, String end) {
		if (start.replaceAll("\\s+","").length() > 0 && end.replaceAll("\\s+","").length() > 0) {
			end = "to " + end;
		} else if (start.replaceAll("\\s+","").length() == 0 && end.replaceAll("\\s+","").length() > 0) {
			end = "by " + end;
		} else {
		}
		return end;
	}
	
	private void setIndex(Text index, Rectangle r1) {
		 index.setTranslateX(-280); 
		 if (r1.getHeight() == UNBOUNEDED_CONTAINER_HEIGHT) {
			 index.setTranslateY(-2); 
		 } else {
			 index.setTranslateY(-15); 
		 }
		 index.setFont(Font.font ("Monaco", INDEX_FONT));
		 index.setFill(Color.BLUE);
		
	}
	
	private void setTaskName(Text taskName, Rectangle r1) {
		 taskName.setTextAlignment(TextAlignment.LEFT);
		 taskName.setFont(Font.font ("Monaco", FontWeight.BOLD, TASKNAME_FONT));
		 if (r1.getHeight() == UNBOUNEDED_CONTAINER_HEIGHT) {
			 taskName.setTranslateY(-2); 
		 } else {
			 taskName.setTranslateY(-15); 
		 }
	}
	
	private void setTimeDate(Text timeDate, int x, int y) {
		timeDate.setTranslateX(x);
		timeDate.setTranslateY(y);
		timeDate.setFont(Font.font ("Monaco"));
		timeDate.setFill(Color.DIMGREY);
	}
	
	private boolean isDone (ArrayList<String> list) {
		if (list.size() < 7) {
			return false;
		}
		
		String done = list.get(6);
		if(done.length() == 4) {
			return true;
		} else {
			return false;
		}
	}
	
	private Group createTaskGroup(ArrayList<String> list) {
		Group group = new Group();
		StackPane stackPane = new StackPane();
		
		String start = getStartTimeDate(list);
		String displayStart = modifyStart(start);
		String end = getEndTimeDate(list);
		String displayEnd = modifyEnd(start, end);
		
		Boolean isDone = isDone(list);
		
		Rectangle r1 = createTaskContainer(start, end, isDone);
		stackPane.getChildren().add(r1);
		
		Text t0 = new Text();
		String index = getIndex(list);
		String taskName = getTaskName(list);
		t0.setText(index);
		
		stackPane.getChildren().add(t0);
		setIndex(t0, r1);
		
		Text t1 = new Text();
		t1.setText(taskName);
		stackPane.getChildren().add(t1);
		setTaskName(t1, r1);

		 
		Text t2 = new Text();
		Text t3 = new Text();

		t2.setText(displayStart);
		setTimeDate(t2, -110, 10);
		t3.setText(displayEnd);
		setTimeDate(t3, 120, 10);
		
		stackPane.getChildren().add(t2);
		stackPane.getChildren().add(t3);
		
		group.getChildren().add(stackPane);
		
		return group;
	}
	
	private Group spacingGroup() {
		Group group = new Group();
		Rectangle r1 = new Rectangle();
		r1.setWidth(600);
		r1.setHeight(10);
		r1.setFill(Color.GHOSTWHITE);
		//r1.setFill(Color.PALETURQUOISE);
		group.getChildren().add(r1);
		return group;
	}
	
	private void display(Output output, Output lastDisplay) {
		returnMessage.setText("");
		String message = output.getReturnMessage();
		assert(message != null);
		
		helpMessage.setText("");
		returnMessage.setText(message);
		
		flashReturnMessage();

		ArrayList<ArrayList<String>> outputArrayList = new ArrayList();
		outputArrayList = lastDisplay.getTasks();
		
		if (outputArrayList.size() == 0) {

		} else {
			displayTasks(outputArrayList);
		}
		

	}
	
	private void flashReturnMessage() {
		
		FillTransition textWait = new FillTransition(Duration.millis(800), returnMessage, Color.BLACK, Color.BLACK);
		textWait.setCycleCount(1);
		textWait.play();
		
		FillTransition textRed = new FillTransition(Duration.millis(1500), returnMessage, Color.BLACK, Color.RED);
		textRed.setCycleCount(1);
		textRed.play();
		
		FillTransition textBlack = new FillTransition(Duration.millis(1500), returnMessage, Color.RED, Color.BLACK);
		textBlack.setCycleCount(1);
		textBlack.play();
		
	    SequentialTransition sT = new SequentialTransition(textWait, textRed, textBlack);
	        sT.play();
		
	}
	
	private void getInput() {
		command = input.getText();
		
	}
	
	public Output processInput(String input) {
		
		return logic.processInput(input);
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
	        		getInput();
	        		Output output = processInput(command);
	        		Output lastDisplay = logic.getLastDisplayed();
	            	display(output, lastDisplay);
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
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;

    }


}
