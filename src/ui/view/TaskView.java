package ui.view;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class TaskView extends Group {
	
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
	private final int OVERDUE = 13;
	
	private final int MAXIMUM_LENGTH = 50;
	private final int BOUNEDED_CONTAINER_HEIGHT = 44;
	private final int UNBOUNEDED_CONTAINER_HEIGHT = 25;
	private final int INDEX_FONT = 14;
	private final int TASKNAME_FONT = 16;
	private final int TASKNAME_INDENTATION = 40;
	private final int CONTAINER_WIDTH = 600;
	private final int CALENDARVIEW_TRANSLATE_X = 440;
	private final Color COLOR_TASK_CONTAINER = Color.rgb(51, 122, 183);//Color.rgb(59, 135, 200);// moderately dark blue
	private final Color COLOR_EMERGENT = Color.rgb(230,0,0);//  red
	private final Color COLOR_DONE = Color.rgb(166, 166, 166); //moderately dark grey
	private final Color COLOR_OVERDUE = Color.rgb(179, 0, 0); //dark red

	
	private boolean hasYear;
	private boolean isFloating;
	private boolean isDone;
	private boolean isToday;
	private boolean isOverDue;
	private ArrayList<String> list;
	private List<String> start;
	private List<String> end;
	private Text taskNameText;
	private Text indexText;
	private String index;
	private String taskName;
	
	private StackPane stackPane;
	private Rectangle container;
	private Color backgroundColor;
	private CalendarView calendarView;
	
	public TaskView(ArrayList<String> list, boolean hasYear) {
		initialize(list, hasYear);
		setIsFloating();
		setIsDone();
		setIsToday();
		setIsOverDue();
		createTaskContainer();
		markEmergent();
		markOverdue();
		setStart();
		setEnd();
		setBackgroundColor();
		setIndex();
		setTaskName();
		setCalendarView();
	}
	
	private void initialize(ArrayList<String> list, boolean hasYear) {
		this.hasYear = hasYear;
		this.list = list;
		stackPane = new StackPane();
		stackPane.setAlignment(Pos.CENTER_LEFT);
		this.getChildren().add(stackPane);
		
	}
	
	private void setIsFloating() {
		if (list.get(START_TIME).length() == 0 && list.get(END_TIME).length() == 0) {
			isFloating = true;
		} else {
			isFloating = false;
		}
	}
	
	private void setIsDone () {
		if (list.size() < 11) {
			isDone = false;
		}
		
		String done = list.get(MARK);
		if(done.equals("DONE")) {
			isDone = true;
		} else {
			isDone = false;
		}
	}
	
	private void setIsToday() {
		if (list.get(END_DATE).equals("TODAY")) {
			isToday = true;
		} else {
			isToday = false;
		}
	}
	private void markEmergent () {
		if (list.get(START_DATE).equals("") && isToday && !isDone) {
			container.setFill(COLOR_EMERGENT);
		} else {
		}
	}
	
	private void setIsOverDue() {
		if (list.get(OVERDUE).equals("true")) {
			isOverDue = true;
		} else {
			isOverDue = false;
		}
	}
	
	private void markOverdue() {
		if (isOverDue && !isDone) {
			container.setFill(COLOR_OVERDUE);
		}
	}
	
	private void createTaskContainer() {
		container = new Rectangle();
		container.setWidth(CONTAINER_WIDTH);
		if(isFloating) {
			container.setHeight(UNBOUNEDED_CONTAINER_HEIGHT);
		} else {
			container.setHeight(BOUNEDED_CONTAINER_HEIGHT);
		}
		
		if (isDone) {
			container.setFill(COLOR_DONE);
		} else {
			container.setFill(COLOR_TASK_CONTAINER);
		} 
		stackPane.getChildren().add(container);
		
	}
	
	private void setStart() {
		start = list.subList(START_TIME, START_YEAR + 1);
	}
	
	private void setEnd() {
		end = list.subList(END_TIME, END_YEAR + 1); 
	}
	
	private void setBackgroundColor() {
		backgroundColor = (Color) container.getFill(); 
	}
	
	private void setCalendarView() {
		calendarView = new CalendarView(start, end, isDone, hasYear, backgroundColor);
		
		if(isFloating) {
		} else {
			stackPane.getChildren().add(calendarView);
			calendarView.setTranslateX(CALENDARVIEW_TRANSLATE_X);
		}
	}
	
	private void setIndex() {
		index = list.get(0) + ".";
		indexText = new Text();
		indexText.setText(index);
		stackPane.getChildren().add(indexText);
		indexText.setTranslateX(10);
		indexText.setFont(Font.font ("Monaco", INDEX_FONT));
		indexText.setFill(Color.WHITE);
	}
	
	private void setTaskName() {
		taskName = list.get(1);
		 if(isOverDue) {
			 taskName = "[OVERDUE] " + taskName;
		 }
		 
		if (taskName.length() > MAXIMUM_LENGTH) {
			taskName = taskName.substring(0, MAXIMUM_LENGTH) + " ...";
		}
		taskNameText = new Text();
		taskNameText.setText(taskName);
		stackPane.getChildren().add(taskNameText);
		taskNameText.setFont(Font.font (TASKNAME_FONT));
		taskNameText.setTextAlignment(TextAlignment.LEFT);
		taskNameText.setTranslateX(TASKNAME_INDENTATION); 
		taskNameText.setFill(Color.WHITE);
	}
	

}
