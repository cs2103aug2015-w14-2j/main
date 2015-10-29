package ui.view;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class CalendarView extends Group {
	
	private final int BOUNEDED_CONTAINER_HEIGHT = 44;
	private final int CALENDARBOX_HEIGHT = BOUNEDED_CONTAINER_HEIGHT - 4;
	private final int CALENDAR_NORMAL_WIDTH = 70;
	private final int CALENDAR_WIDE_WIDTH = 150;
	
	private final int DASH_FONTSIZE = 18;
	private final int BY_FONTSIZE = 14;
	
	private boolean isDone;
	private boolean hasStart;
	private boolean isSameDay;
	private boolean hasYear;
	private List<String> start;
	private List<String> end;
	private StackPane stackPane;
	private CalendarBox leftView;
	private CalendarBox rightView;
	private Rectangle boxLeft;
	private Rectangle boxRight;
	
	public CalendarView(List<String> start, List<String> end, boolean isDone, boolean hasYear) {

		initialize(start, end, isDone, hasYear);
		addContent();
		this.getChildren().add(stackPane);
		
	}
	
	private void initialize(List<String> start, List<String> end, boolean isDone, boolean hasYear) {
		this.start = start;
		this.end = end;
		this.isDone = isDone;
		this.hasYear = hasYear;
		this.stackPane = new StackPane();
		StackPane stackPane = new StackPane();
		stackPane.setAlignment(Pos.CENTER_LEFT);
		boxLeft = createEmptyCalendarBox();
		boxRight = createEmptyCalendarBox();
		
		hasStart = hasStart(start);
		isSameDay = isSameDay(start, end);
		
		//this.getChildren().add(stackPane);
		System.out.println("here");
	}
	
	private void addContent() {
		if (!hasStart) {
			addBoxRight();
			
		} else if (isSameDay) {
			addWideBox();
		} else {
			addTwoBoxes();
		}

		
	}
	
	private void addBoxRight() {
		boxLeft.setWidth(CALENDAR_NORMAL_WIDTH);
		leftView = new CalendarBox(boxLeft, start, end, isDone, hasYear, false);
		stackPane.getChildren().add(leftView);
		leftView.setTranslateX(80);
		Text by = new Text();
		by.setText("by ");
		by.setFont(Font.font ("Monaco", FontWeight.BOLD, BY_FONTSIZE));
		by.setFill(Color.WHITE);
		stackPane.getChildren().add(by);
		by.setTranslateX(CALENDAR_NORMAL_WIDTH - 35);
	}
	
	private void addWideBox() {
		boxLeft.setWidth(CALENDAR_WIDE_WIDTH);
		leftView = new CalendarBox(boxLeft, start, end, isDone, hasYear, true);
		stackPane.getChildren().add(leftView);
	}
	
	private void addTwoBoxes() {
		boxLeft.setWidth(CALENDAR_NORMAL_WIDTH);
		leftView = new CalendarBox(boxLeft, start, end, isDone, hasYear, false);
		stackPane.getChildren().add(leftView);
		boxRight.setWidth(CALENDAR_NORMAL_WIDTH);
		rightView = new CalendarBox(boxRight, start, end, isDone, hasYear, false);
		stackPane.getChildren().add(rightView);
		rightView.setTranslateX(CALENDAR_NORMAL_WIDTH + 10);
		
		Text dash = new Text();
		dash.setText("-");
		dash.setFont(Font.font ("Monaco", FontWeight.BOLD, DASH_FONTSIZE));
		dash.setFill(Color.WHITE);
		stackPane.getChildren().add(dash);
		dash.setTranslateX(CALENDAR_NORMAL_WIDTH - 30);
	}
	
	private Rectangle createEmptyCalendarBox() {
		Rectangle calendarBox = new Rectangle();
		calendarBox.setHeight(CALENDARBOX_HEIGHT);
		calendarBox.setFill(Color.WHITE);
		return calendarBox;
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

}
