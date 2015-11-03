//@@author A0133888N
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

/**
 * This class is used to display time and date of a task in a calendar view,
 * which includes time, date, weekday and year if applicable. If the task is a
 * deadline task, there is also a "by" word. If the task is a bounded task, with
 * different start and end date, there will be a dash connecting the two
 * calendar boxes.
 */
public class CalendarView extends Group {

	private static final int BOUNEDED_CONTAINER_HEIGHT = 44;
	private static final int CALENDARBOX_HEIGHT = BOUNEDED_CONTAINER_HEIGHT - 4;
	private static final int CALENDAR_NORMAL_WIDTH = 70;
	private static final int CALENDAR_WIDE_WIDTH = 150;
	private static final int BY_TRANSLATE_X = -15;
	private static final int BOX_RIGHT_TRANSLATE_X = 10;
	private static final int DASH_FONTSIZE = 18;
	private static final int BY_FONTSIZE = 14;
	private static final Color CALENDAR_BACKGROUND = Color.WHITE;

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
	private Color backgroundColor;

	public CalendarView(List<String> start, List<String> end, boolean isDone, boolean hasYear, Color backgroundColor) {

		initialize(start, end, isDone, hasYear, backgroundColor);
		addContent();
		finalizeView();

	}

	private void initialize(List<String> start, List<String> end, boolean isDone, boolean hasYear,
			Color backgroundColor) {
		this.start = start;
		this.end = end;
		this.isDone = isDone;
		this.hasYear = hasYear;
		this.backgroundColor = backgroundColor;

		stackPane = new StackPane();
		stackPane.setAlignment(Pos.CENTER_LEFT);
		boxLeft = createEmptyCalendarBox();
		boxRight = createEmptyCalendarBox();
		hasStart = hasStart(start);
		isSameDay = isSameDay(start, end);
	}

	private void finalizeView() {
		this.getChildren().add(stackPane);
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

	/**
	 * Add a calendar box to the right, if it is a deadline task. And add the
	 * word "by".
	 */
	private void addBoxRight() {
		boxLeft.setWidth(CALENDAR_NORMAL_WIDTH);
		leftView = new CalendarBox(boxLeft, start, end, isDone, hasYear, false, backgroundColor);
		stackPane.getChildren().add(leftView);
		leftView.setTranslateX(CALENDAR_NORMAL_WIDTH + BOX_RIGHT_TRANSLATE_X);
		Text by = new Text();
		by.setText("by ");
		by.setFont(Font.font("Monaco", FontWeight.BOLD, BY_FONTSIZE));
		by.setFill(CALENDAR_BACKGROUND);
		stackPane.getChildren().add(by);
		by.setTranslateX(CALENDAR_NORMAL_WIDTH + BY_TRANSLATE_X);
	}

	/**
	 * Add a wide calendar box, if the task starts and ends on the same day.
	 */
	private void addWideBox() {
		boxLeft.setWidth(CALENDAR_WIDE_WIDTH);
		leftView = new CalendarBox(boxLeft, start, end, isDone, hasYear, true, backgroundColor);
		stackPane.getChildren().add(leftView);
	}

	/**
	 * Add two calendar boxes, if the task starts and ends on different dates.
	 * And add a dash in between.
	 */
	private void addTwoBoxes() {
		boxLeft.setWidth(CALENDAR_NORMAL_WIDTH);
		leftView = new CalendarBox(boxLeft, start, start, isDone, hasYear, false, backgroundColor);
		stackPane.getChildren().add(leftView);
		boxRight.setWidth(CALENDAR_NORMAL_WIDTH);
		rightView = new CalendarBox(boxRight, end, end, isDone, hasYear, false, backgroundColor);
		stackPane.getChildren().add(rightView);
		rightView.setTranslateX(CALENDAR_NORMAL_WIDTH + BOX_RIGHT_TRANSLATE_X);

		Text dash = new Text();
		dash.setText("-");
		dash.setFont(Font.font("Monaco", FontWeight.BOLD, DASH_FONTSIZE));
		dash.setFill(CALENDAR_BACKGROUND);
		stackPane.getChildren().add(dash);
		dash.setTranslateX(CALENDAR_NORMAL_WIDTH);
	}

	private Rectangle createEmptyCalendarBox() {
		Rectangle calendarBox = new Rectangle();
		calendarBox.setHeight(CALENDARBOX_HEIGHT);
		calendarBox.setFill(CALENDAR_BACKGROUND);
		return calendarBox;
	}

	private boolean hasStart(List<String> start) {
		if (start == null) {
			return false;
		}
		if (start.get(0).length() == 0) {
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
