//@@author A0133888N
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

/**
 * This class is used to display tasks in Flexi-List. A task displayed includes
 * its index, task name, time and date (if applicable). It also includes a
 * background, where its color depends the type of the task.
 */
public class TaskView extends Group {

	private static final boolean isWin = System.getProperty("os.name").toLowerCase().contains("win");
	// Unused indexes are commented out to avoid warning, but they are remained
	// for future reference.

	// private static final int INDEX = 0;
	// private static final int TASKNAME = 1;
	private static final int START_TIME = 2;
	// private static final int START_WEEKDAY = 3;
	private static final int START_DATE = 4;
	// private static final int START_MONTH = 5;
	private static final int START_YEAR = 6;
	private static final int END_TIME = 7;
	// private static final int END_WEEKDAY = 8;
	private static final int END_DATE = 9;
	// private static final int END_MONTH = 10;
	private static final int END_YEAR = 11;
	private static final int MARK = 12;
	private static final int OVERDUE = 13;
	private static final int LISTSIZE = 14;

	private static final int MAXIMUM_LENGTH = isWin ? 40 : 50;
	private static final int BOUNEDED_CONTAINER_HEIGHT = 44;
	private static final int UNBOUNEDED_CONTAINER_HEIGHT = 25;
	private static final int INDEX_FONT = 14;
	private static final int TASKNAME_FONT = 16;
	private static final int TASKNAME_INDENTATION = 40;
	private static final int CONTAINER_WIDTH = 600;
	private static final int CALENDARVIEW_TRANSLATE_X = 440;
	private static final Color COLOR_TASK_CONTAINER = Color.rgb(51, 122, 183); // moderately dark blue
	private static final Color COLOR_EMERGENT = Color.rgb(255, 126, 85); // slightly dark orange
	private static final Color COLOR_DONE = Color.rgb(166, 166, 166); // moderately dark grey
	private static final Color COLOR_OVERDUE = Color.rgb(222, 103, 100); // sightly light red

	private boolean hasYear;
	private boolean isFloating;
	private boolean isDone;
	private boolean isToday;
	private boolean isOverDue;
	private boolean isEmergent;
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

	public TaskView(ArrayList<String> list, boolean hasYear) throws Exception {
		initialize(list, hasYear);
		setIsFloating();
		setIsDone();
		setIsToday();
		setIsEmergent();
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

	private void initialize(ArrayList<String> list, boolean hasYear) throws Exception {
		if(list.size() != LISTSIZE) {
			throw new Exception("List size from output does not match");
		}
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

	private void setIsDone() throws Exception{
		String done = list.get(MARK);
		if(done == null) {
			throw new Exception("The mark done field of the task is missing");
		}
		
		if (done.equals("DONE")) {
			isDone = true;
		} else if (done.equals("UNDONE")) {
			isDone = false;
		} else {
			throw new Exception("An invalid value is assigned to the mark done field");
		}
	}

	private void setIsToday() {
		if (list.get(END_DATE).equals("TODAY")) {
			isToday = true;
		} else {
			isToday = false;
		}
	}

	private void setIsEmergent() {
		isEmergent = list.get(START_DATE).equals("") && isToday && !isDone && !isOverDue;

	}

	private void markEmergent() {
		if (isEmergent) {
			container.setFill(COLOR_EMERGENT);
		} else {
		}
	}

	private void setIsOverDue() throws Exception{
		if (list.get(OVERDUE).equals("true")) {
			isOverDue = true;
			//This field is an empty string for bounded tasks and floating tasks.
		} else if (list.get(OVERDUE).equals("false") || list.get(OVERDUE).isEmpty()){
			isOverDue = false;
		} else {
			throw new Exception("An invalid value is assigned to the over due field");
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
		if (isFloating) {
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
		assert START_TIME >= 0;
		assert START_YEAR + 1 < LISTSIZE;
		start = list.subList(START_TIME, START_YEAR + 1);
	}

	private void setEnd() {
		assert END_TIME >= 0;
		assert END_YEAR + 1 < LISTSIZE;
		end = list.subList(END_TIME, END_YEAR + 1);
	}

	private void setBackgroundColor() {
		backgroundColor = (Color) container.getFill();
	}

	private void setCalendarView() {
		try {
			calendarView = new CalendarView(start, end, isDone, hasYear, backgroundColor);
		} catch (Exception e) {
			System.err.println("Error in creating calendar view " + e.getMessage());
		}

		if (isFloating) {
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
		indexText.setFont(Font.font("Monaco", INDEX_FONT));
		indexText.setFill(Color.WHITE);
	}

	private void setTaskName() {
		taskName = list.get(1);
		if (isEmergent && !isOverDue) {
			taskName = "[DUE TODAY] " + taskName;
		} else if (isOverDue) {
			taskName = "[OVERDUE] " + taskName;
		}

		if (taskName.length() > MAXIMUM_LENGTH) {
			taskName = taskName.substring(0, MAXIMUM_LENGTH) + " ...";
		}
		taskNameText = new Text();
		taskNameText.setText(taskName);
		stackPane.getChildren().add(taskNameText);
		taskNameText.setFont(Font.font(TASKNAME_FONT));
		taskNameText.setTextAlignment(TextAlignment.LEFT);
		taskNameText.setTranslateX(TASKNAME_INDENTATION);
		taskNameText.setFill(Color.WHITE);
	}

}
