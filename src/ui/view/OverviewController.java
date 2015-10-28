package ui.view;

import ui.Main;

import java.io.File;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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
import shared.Output.Priority;
import logic.Logic;

public class OverviewController {
	
	private final int MAXIMUM_LENGTH = 40;
	private final int INDEX_FONT = 14;
	private final int TASKNAME_FONT = 16;
	private final int DASH_FONT = 18;
	private final int BY_FONT = 14;
	private final int BOUNEDED_CONTAINER_HEIGHT = 60;
	private final int UNBOUNEDED_CONTAINER_HEIGHT = 30;
	private final int INDEX = 0;
	private final int TASKNAME = 1;
	private final int START_TIME = 2;
	private final int START_WEEKDAY = 3;
	private final int START_DATE = 4;
	private final int START_MONTH = 5;
	private final int START_YEAR = 6;
	private final int END_TIME = 7;
	private final int END_WEEKDAY = 8;
	private final int END_DATE = 9;
	private final int END_MONTH = 10;
	private final int END_YEAR = 11;
	private final int MARK = 12;
	private final int TASKNAME_INDENTATION = 40;
	private final String DAY_COLOR = "#c9daf8";
	private final String NIGHT_COLOR = "#1a237e;";
	private final Color COLOR_TASK_CONTAINER = Color.rgb(59, 135, 200);// moderately dark blue
	private final Color COLOR_EMERGENT = Color.RED;
	private final Color COLOR_DONE = Color.rgb(166, 166, 166); //moderately dark grey
	
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
	private boolean hasYear = false;
	
	Logic logic = new Logic();
	
	// Obtain a suitable logger.
	private static Logger logger = Logger.getLogger("UILogger");
	
	@FXML
	public void initialize() {
		
		vbox = new VBox(10);
		vbox.setPrefWidth(600);
		vbox.setPrefHeight(600);
		vbox.setStyle(String.format("-fx-background-color: %1$s;", DAY_COLOR));
		taskScrollPane.setContent(vbox);
		taskScrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
		setMessageStyle(returnMessage);
		setMessageStyle(helpMessage);
		
		logger.log(Level.INFO, "going to initialize the overview");
		
		try {
			Output output = processInput("display");
			Output lastDisplay = processInput("display");
	    	display(output, lastDisplay);
		} catch (Exception ex) {
			logger.log(Level.WARNING, "display command processing error", ex);
		}
		
		logger.log(Level.INFO, "end of processing display command");
		
    	input.textProperty().addListener((observable, oldValue, newValue) -> {
    	    changeView(oldValue, newValue);
    	    genereateHelpMessage(newValue);
    	    displayYear(oldValue, newValue);
    	    
    	});
    	
    	vbox.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

    	     @Override
    	     public void handle(MouseEvent event) {
    	         input.requestFocus();
    	         event.consume();
    	     }
    	});
    	
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
	
	private void setMessageStyle(Text message) {
		message.setFont(Font.font(14));
	}
	
	
	private void changeView(String oldValue, String newValue) {
		if (oldValue.equals("night") && newValue.equals("")) {
			vbox.setStyle(String.format("-fx-background-color: %1$s;", NIGHT_COLOR));
		}
		
		if (oldValue.equals("day") && newValue.equals("")) {
			vbox.setStyle(String.format("-fx-background-color: %1$s;", DAY_COLOR));
		}
	}
	
	private void clearReturnMessage() {
		if(returnMessage.getText().length() > 0 && helpMessage.getText().length() > 0) {
			returnMessage.setText("");
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
				helpMessage.setText("e.g. create ... from ... to ...");
				break;
			case "edit" :
				helpMessage.setText("e.g. edit index to ...(new name)");
				break;
			case "delete" :
				helpMessage.setText("e.g. delete index");
				break;
			case "display" :
				helpMessage.setText("e.g. display ...(time, task name)");
				break;
			case "mark" :
				helpMessage.setText("e.g. mark index");
				break;
			case "ummark" :
				helpMessage.setText("e.g. ummark index");
				break;
			default : 
				helpMessage.setText("");
				break;
		}
	}
	
	
	private void displayInitialView(ArrayList<ArrayList<String>> outputArrayList) {
	
	}
	
	private void displayTasks(ArrayList<ArrayList<String>> outputArrayList, ArrayList<ArrayList<String>> currentOutputArrayList) {
		/*
		 		System.out.println(outputArrayList.size());
		 
		for (ArrayList<String> list : outputArrayList) {
			System.out.println(list);
		}
		System.out.println(currentOutputArrayList.size());
		for (ArrayList<String> list : currentOutputArrayList) {
			System.out.println(list);
		}
		*/
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
	
	private Rectangle createTaskContainer(boolean isFloatingTask, boolean isDone) {
		Rectangle r1 = new Rectangle();
		r1.setWidth(600);
		if(isFloatingTask) {
			r1.setHeight(UNBOUNEDED_CONTAINER_HEIGHT);
		} else {
			r1.setHeight(BOUNEDED_CONTAINER_HEIGHT);
		}
		
		if (isDone) {
			r1.setFill(COLOR_DONE);
			ImageView imageView = new ImageView();
	      //  Image image = new Image(OverviewController.class.getResource("tick.png"));
	       // imageView.setImage(image);
		} else {
			r1.setFill(COLOR_TASK_CONTAINER);
		} 
		 
		return r1;
	}
	
	private String getIndex(ArrayList<String> list) {
		String index = list.get(0) + ".";
		return index;
	}
	
	private String getTaskName(ArrayList<String> list) {
		String taskName = list.get(1);
		
		if (taskName.length() > MAXIMUM_LENGTH) {
			taskName = taskName.substring(0, MAXIMUM_LENGTH);
		}
		return taskName;
	}
	
	private List<String> getStartTimeDate(ArrayList<String> list) {
		List<String> start = list.subList(START_TIME, START_YEAR + 1);
		if(start.get(0).equals("")) {
			return null;
		} else {
			return start;
		}
	}
	
	private List<String> getEndTimeDate(ArrayList<String> list) {
		List<String> end = list.subList(END_TIME, END_YEAR + 1); 
		if (end.get(0).equals("")) {
			return null;
		} else {
			return end;
		}
	}
	
	private void setIndex(Text index, Rectangle r1) {
		/*
		 if (r1.getHeight() == UNBOUNEDED_CONTAINER_HEIGHT) {
			index.setTranslateY(-10); 
		 } else {
			 index.setTranslateY(-18); 
		 }
		 */
		index.setTranslateX(10);
		 index.setFont(Font.font ("Monaco", INDEX_FONT));
		 index.setFill(Color.WHITE);
		
	}
	
	private void setTaskName(Text taskName, Rectangle r1) {
		 taskName.setTextAlignment(TextAlignment.LEFT);
		 taskName.setFont(Font.font (TASKNAME_FONT));
		 taskName.setTextAlignment(TextAlignment.LEFT);
		 taskName.setTranslateX(TASKNAME_INDENTATION); 
		 taskName.setFill(Color.WHITE);

		 if (r1.getHeight() == UNBOUNEDED_CONTAINER_HEIGHT) {
			// taskName.setTranslateY(-2); 
		 } else {
			 //taskName.setTranslateY(-15); 
		 }
	}
	
	
	private boolean isDone (ArrayList<String> list) {
		if (list.size() < 7) {
			return false;
		}
		
		String done = list.get(MARK);
		if(done.length() == 4) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean hasStart(List<String> start) {
		if (start == null) {
			return false;
		}
		if(start.get(0).length() == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	private boolean isSameDay(List<String> start, List<String> end) {
		if (start == null) {
			return false;
		}
		if (start.get(2).equals(end.get(2))) {
			return true;
		} else {
			return false;
		}
	}
	
	private Rectangle createEmptyCalendarBox() {
		Rectangle r1 = new Rectangle();
		r1.setHeight(55);
		r1.setFill(Color.ALICEBLUE);
		return r1;
	}
	
	private Group createCalendarBoxWithText(Rectangle r1, List<String> list, boolean isDone, boolean hasYear) {
		Group group = new Group();
		StackPane stackPane = new StackPane();
		stackPane.setAlignment(Pos.CENTER);
		stackPane.getChildren().add(r1);
		
		Rectangle weekDaybackGround = new Rectangle();
		weekDaybackGround.setWidth(r1.getWidth() * 0.97);
		weekDaybackGround.setHeight(r1.getHeight() * 0.25);
		if (!isDone) {
			weekDaybackGround.setFill(COLOR_TASK_CONTAINER);
		} else {
			weekDaybackGround.setFill(COLOR_DONE);
		}
		
		stackPane.getChildren().add(weekDaybackGround);
		weekDaybackGround.setTranslateY(-20);
		Text weekDay = new Text();
		weekDay.setText(list.get(1));
		weekDay.setFill(Color.WHITE);
		stackPane.getChildren().add(weekDay);
		weekDay.setTranslateY(-20);
		Text time = new Text();
		time.setText(list.get(0));
		stackPane.getChildren().add(time);
		time.setTranslateY(0);
		Text dateMonth = new Text();
		if (!hasYear) {
			dateMonth.setText(list.get(2) + " " + list.get(3));
		} else {
			dateMonth.setText(list.get(2) + " " + list.get(3) + " '" + list.get(4).substring(2, 4));
			dateMonth.setFont(Font.font(dateMonth.getFont().getSize() - 2));
		}

		stackPane.getChildren().add(dateMonth);
		dateMonth.setTranslateY(20);
		group.getChildren().add(stackPane);
		return group;
		
	}
	
	private Group createWideCalendarBoxWithText (Rectangle r1, List<String> start, List<String> end, boolean isDone, boolean hasYear) {
		Group group = new Group();
		StackPane stackPane = new StackPane();
		stackPane.setAlignment(Pos.CENTER);
		stackPane.getChildren().add(r1);
		
		Rectangle weekDaybackGround = new Rectangle();
		weekDaybackGround.setWidth(r1.getWidth() * 0.98);
		weekDaybackGround.setHeight(r1.getHeight() * 0.25);
		if (!isDone) {
			weekDaybackGround.setFill(COLOR_TASK_CONTAINER);
		} else {
			weekDaybackGround.setFill(COLOR_DONE);
		}

		stackPane.getChildren().add(weekDaybackGround);
		weekDaybackGround.setTranslateY(-20);
		
		Text weekDay = new Text();
		weekDay.setText(start.get(1));
		stackPane.getChildren().add(weekDay);
		weekDay.setTranslateY(-20);
		weekDay.setFill(Color.WHITE);
		Text time = new Text();
		time.setText(start.get(0) + " - " + end.get(0));
		stackPane.getChildren().add(time);
		time.setTranslateY(0);
		Text dateMonth = new Text();
		if (!hasYear) {
			dateMonth.setText(start.get(2) + " " + start.get(3));
		} else {
			dateMonth.setText(start.get(2) + " " + start.get(3) + " " + start.get(4));
		}
		stackPane.getChildren().add(dateMonth);
		dateMonth.setTranslateY(20);
		group.getChildren().add(stackPane);
		return group;
	}
	private Group createCalendarView(List<String> start, List<String> end, boolean isDone) {
		Group group = new Group();
		StackPane stackPane = new StackPane();
		stackPane.setAlignment(Pos.CENTER_LEFT);
		Rectangle r1 = createEmptyCalendarBox();
		Rectangle r2 = createEmptyCalendarBox();
		
		boolean hasStart = hasStart(start);
		boolean isSameDay = isSameDay(start, end);
		
		Group leftView = new Group();
		Group rightView = new Group();
		if (!hasStart) {
			r1.setWidth(60);
			leftView = createCalendarBoxWithText(r1, end, isDone, hasYear);
			stackPane.getChildren().add(leftView);
			leftView.setTranslateX(70);
			Text by = new Text();
			by.setText("by ");
			by.setFont(Font.font ("Monaco", FontWeight.BOLD, BY_FONT));
			by.setFill(Color.WHITE);
			stackPane.getChildren().add(by);
			by.setTranslateX(45);
			
		} else if (isSameDay) {
			r1.setWidth(130);
			leftView = createWideCalendarBoxWithText(r1, start, end, isDone, hasYear);
			stackPane.getChildren().add(leftView);
		} else {
			r1.setWidth(60);
			leftView = createCalendarBoxWithText(r1, start, isDone, hasYear);
			stackPane.getChildren().add(leftView);
			r2.setWidth(60);
			rightView = createCalendarBoxWithText(r2, end, isDone, hasYear);
			stackPane.getChildren().add(rightView);
			rightView.setTranslateX(70);
			
			Text dash = new Text();
			dash.setText("-");
			dash.setFont(Font.font ("Monaco", FontWeight.BOLD, DASH_FONT));
			dash.setFill(Color.WHITE);
			stackPane.getChildren().add(dash);
			dash.setTranslateX(60);
		}
		
		
		group.getChildren().add(stackPane);
		
		return group;
	}
	
	private boolean isFloatingTask(ArrayList<String> list) {
		if (list.get(START_TIME).length() == 0 && list.get(END_TIME).length() == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isToday() {
		return false;
	}
	private void markEmergent (Rectangle r1, ArrayList<String> list) {
		if (list.get(START_DATE).equals("") && isToday()) {
			r1.setFill(COLOR_EMERGENT);
		} else {
		}
	}
	
	private Group createTaskGroup(ArrayList<String> list) {
		Group group = new Group();
		StackPane stackPane = new StackPane();
		
		
		boolean isFloatingTask = isFloatingTask(list);
		List<String> start = null;
		List<String> end = null;
		Boolean isDone = isDone(list);
		List<Group> calendarViewList = new ArrayList();
		Group calendarView = null;
		
		if (!isFloatingTask) {
			start = getStartTimeDate(list);
			end = getEndTimeDate(list);
			calendarView = createCalendarView(start, end, isDone);
		}
		
		calendarViewList.add(calendarView);
		
		
		
		Rectangle r1 = createTaskContainer(isFloatingTask, isDone);
		markEmergent(r1, list);
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
		stackPane.setAlignment(Pos.CENTER_LEFT);
		setTaskName(t1, r1);

		if(isFloatingTask) {
		} else {
			stackPane.getChildren().add(calendarViewList.get(0));
			calendarViewList.get(0).setTranslateX(450);

		}

		group.getChildren().add(stackPane);
		
		return group;
	}
	
	private void clearHelpMessage() {
		if (helpMessage.getText().length() > 0) {
			helpMessage.setText("");
		}
		
	}
	
	private void display(Output output, Output lastDisplay) {
		returnMessage.setText("");
		String message = output.getReturnMessage();
		clearHelpMessage();
		returnMessage.setText(message);
		
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
		
		FillTransition textWait = new FillTransition(Duration.millis(800), returnMessage, Color.BLACK, Color.BLACK);
		textWait.setCycleCount(1);
		textWait.play();
		
		FillTransition textHighlight = new FillTransition(Duration.millis(1500), returnMessage, Color.BLACK, color);
		textHighlight.setCycleCount(1);
		textHighlight.play();
		
		FillTransition textBlack = new FillTransition(Duration.millis(1500), returnMessage, color, Color.BLACK);
		textBlack.setCycleCount(1);
		textBlack.play();
		
	    SequentialTransition sT = new SequentialTransition(textWait, textHighlight, textBlack);
	        sT.play();
		
	}
	
	private void getInput() {
		command = input.getText();
		
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
		displayOutput();

	}
	
	public void onClickScrollPane() {
		System.out.println("test 000");
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
