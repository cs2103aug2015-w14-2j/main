package ui.view;

import ui.Main;

import java.io.File;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javafx.animation.FillTransition;
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
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import shared.Output;

import logic.Logic;

public class OverviewController {
	
	@FXML
	private TextField input;
	
	@FXML
	private Text returnMessage;
	
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
		vbox.setStyle("-fx-background-color: #afeeee;");
		taskScrollPane.setContent(vbox);
		
		Output output = processInput("display all");
    	display(output);
		//change
	}
	
	
	private void displayInitialView(ArrayList<ArrayList<String>> outputArrayList) {
		//?
	}
	
	private void displayTasks(ArrayList<ArrayList<String>> outputArrayList) {
		vbox.getChildren().clear();
		for (ArrayList<String> list : outputArrayList) {
			Group group = taskGroup(list);
			vbox.getChildren().add(group);
			//Group spacing = spacingGroup();
			//vbox.getChildren().add(spacing);
			
		}
	}
	
	private Rectangle createTaskContainer() {
		Rectangle r1 = new Rectangle();
		r1.setWidth(600);
		r1.setHeight(60);
		r1.setFill(Color.LIGHTSKYBLUE);
		return r1;
	}
	
	private Group taskGroup(ArrayList<String> list) {
		Group group = new Group();
		StackPane stackPane = new StackPane();
		Rectangle r1 = createTaskContainer();
		stackPane.getChildren().add(r1);
		
		Text t0 = new Text();
		String outputString = "";
		String index = list.get(0);
		String taskName = list.get(1);
		t0.setText(index);
		
		Text t1 = new Text();
		t1.setText(taskName);

		 
		 stackPane.getChildren().add(t0);
		 t0.setTranslateX(-280); 
		 t0.setTranslateY(-17); 
		 t0.setFont(Font.font ("Monaco", 14));
		 t0.setFill(Color.BLUE);
		 stackPane.getChildren().add(t1);
		 t1.setTextAlignment(TextAlignment.LEFT);
		 //t1.setTranslateX(-220); 
		 t1.setFont(Font.font ("Monaco", 18));
		 t1.setTranslateY(-17); 
		 
		Text t2 = new Text();
		Text t3 = new Text();
		String start = list.get(2) + "   " + list.get(3);
		String end = list.get(4) + "   " + list.get(5);
		if (start.length() == 3) {	
		} else {
			t2.setText("      start\n" + start);
		}
		
		if (end.length() == 3) {	
		} else {
			t3.setText("      end\n" + end);
		}
		t2.setTranslateX(-110);
		t2.setTranslateY(8);
		t3.setTranslateX(120);
		t3.setTranslateY(8);
		
		stackPane.getChildren().add(t2);
		stackPane.getChildren().add(t3);
		t2.setFont(Font.font ("Monaco"));
		t3.setFont(Font.font ("Monaco"));
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
	
	private void display(Output output) {
		returnMessage.setText("");;
		String message = output.getReturnMessage();
		assert(message != null);
		returnMessage.setText(message);
		
		flashReturnMessage();

		ArrayList<ArrayList<String>> outputArrayList = new ArrayList();
		outputArrayList = output.getTasks();
		
		if (outputArrayList.size() == 0) {
		} else {
			displayTasks(outputArrayList);
		}
	}
	
	private void flashReturnMessage() {
		FillTransition textRed = new FillTransition(Duration.millis(1500), returnMessage, Color.BLACK, Color.RED);
		textRed.setCycleCount(1);
		textRed.play();
		
		FillTransition textBlack = new FillTransition(Duration.millis(1500), returnMessage, Color.RED, Color.BLACK);
		textBlack.setCycleCount(1);
		textBlack.play();
		
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
	            	//System.out.println(output.getTasks());
	            	//System.out.println(output.getReturnMessage());
	        		//editDisplay(output);
	        		//message.setText(display);
	        		/*
	        		Output expected = new Output();
	        		ArrayList<ArrayList<String>> expectedList = new ArrayList<ArrayList<String>>();
	        		ArrayList<String> expectedFloatingTask = new ArrayList<String>();
	        		expectedFloatingTask.add("1.");
	        		expectedFloatingTask.add("birthday");
	        		expectedFloatingTask.add("");
	        		expectedFloatingTask.add("");
	        		expectedFloatingTask.add("");
	        		expectedFloatingTask.add("");
	        		
	        		ArrayList<String> expectedDeadlineTask = new ArrayList<String>();
	        		expectedDeadlineTask.add("2.");
	        		expectedDeadlineTask.add("assignment");
	        		expectedDeadlineTask.add("");
	        		expectedDeadlineTask.add("");
	        		expectedDeadlineTask.add("08:00");
	        		expectedDeadlineTask.add("13-10-2015");
	        		
	        		ArrayList<String> expectedBoundedTask = new ArrayList<String>();
	        		expectedBoundedTask.add("3.");
	        		expectedBoundedTask.add("dinner");
	        		expectedBoundedTask.add("08:00");
	        		expectedBoundedTask.add("12-10-2015");
	        		expectedBoundedTask.add("08:00");
	        		expectedBoundedTask.add("13-10-2015");
	        		
	        		expectedList.add(expectedFloatingTask);
	        		expectedList.add(expectedDeadlineTask);
	        		expectedList.add(expectedBoundedTask);
	        		expected.setOutput(expectedList);
	        		expected.setReturnMessage("All tasks are now displayed!");
	            	display(expected);
	            	*/
	            	display(output);
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
