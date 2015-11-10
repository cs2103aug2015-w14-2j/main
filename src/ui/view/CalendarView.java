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
import shared.Constants;

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
	private static final int BY_FONTSIZE = 14;
	private static final int LIST_SIZE = 5;
	private static final Color CALENDAR_BACKGROUND = Color.WHITE;

	private boolean isDone;
	private boolean hasStart;
	private boolean isSameDay;
	private boolean hasYear;
	private boolean isAllDay;
	private boolean isWide;
	private List<String> start;
	private List<String> end;
	private StackPane stackPane;
	private CalendarBox viewLeft;
	private CalendarBox viewRight;
	private Rectangle boxLeft;
	private Rectangle boxRight;
	private Color backgroundColor;

	public CalendarView(List<String> start, List<String> end, boolean isDone, boolean hasYear, Color backgroundColor)
			throws Exception {
		initialize(start, end, isDone, hasYear, backgroundColor);
		addContent();
		finalizeView();

	}

	private void initialize(List<String> start, List<String> end, boolean isDone, boolean hasYear,
			Color backgroundColor) throws Exception {
		if (start == null || start.size() != LIST_SIZE) {
			throw new Exception("Invalid start list from taskView");
		}
		if (end == null || end.size() != LIST_SIZE) {
			throw new Exception("Invalid end list from taskView");
		}

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
		setIsSameDay();
		setIsAllday();
	}

	private void finalizeView() {
		this.getChildren().add(stackPane);
	}

	private void addContent() {
		if (!hasStart) {
			addBoxOnRight();
		} else if (isSameDay) {
			if (isAllDay) {
				addBoxOnRightAllDay();
			} else {
				addWideBox();
			}
		} else {
			addTwoBoxes();
		}

	}

	/**
	 * Add a calendar box to the right, if it is a deadline task. And add the
	 * word "by".
	 */
	private void addBoxOnRight() {
		isWide = false;
		boxLeft.setWidth(CALENDAR_NORMAL_WIDTH);
		viewLeft = new CalendarBox(boxLeft, start, end, isDone, hasYear, isWide, backgroundColor, isAllDay);
		stackPane.getChildren().add(viewLeft);
		viewLeft.setTranslateX(CALENDAR_NORMAL_WIDTH + BOX_RIGHT_TRANSLATE_X);
		Text by = new Text();
		by.setText("by ");
		by.setFont(Font.font("Monaco", FontWeight.BOLD, BY_FONTSIZE));
		by.setFill(CALENDAR_BACKGROUND);
		stackPane.getChildren().add(by);
		by.setTranslateX(CALENDAR_NORMAL_WIDTH + BY_TRANSLATE_X);
	}

	private void addBoxOnRightAllDay() {
		isWide = true;
		boxLeft.setWidth(CALENDAR_WIDE_WIDTH);
		viewLeft = new CalendarBox(boxLeft, start, end, isDone, hasYear, isWide, backgroundColor, isAllDay);
		stackPane.getChildren().add(viewLeft);
	}

	/**
	 * Add a wide calendar box, if the task starts and ends on the same day.
	 */
	private void addWideBox() {
		isWide = true;
		boxLeft.setWidth(CALENDAR_WIDE_WIDTH);
		viewLeft = new CalendarBox(boxLeft, start, end, isDone, hasYear, isWide, backgroundColor, isAllDay);
		stackPane.getChildren().add(viewLeft);
	}

	/**
	 * Add two calendar boxes, if the task starts and ends on different dates.
	 * And add a dash in between.
	 */
	private void addTwoBoxes() {
		isWide = false;
		boxLeft.setWidth(CALENDAR_NORMAL_WIDTH);
		viewLeft = new CalendarBox(boxLeft, start, start, isDone, hasYear, isWide, backgroundColor, isAllDay);
		stackPane.getChildren().add(viewLeft);
		boxRight.setWidth(CALENDAR_NORMAL_WIDTH);
		viewRight = new CalendarBox(boxRight, end, end, isDone, hasYear, isWide, backgroundColor, isAllDay);
		stackPane.getChildren().add(viewRight);
		viewRight.setTranslateX(CALENDAR_NORMAL_WIDTH + BOX_RIGHT_TRANSLATE_X);

		Rectangle dash = new Rectangle();
		dash.setHeight(2);
		dash.setWidth(8);
		dash.setFill(CALENDAR_BACKGROUND);
		stackPane.getChildren().add(dash);
		dash.setTranslateX(CALENDAR_NORMAL_WIDTH + 1);// minor adjustment
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

	private void setIsSameDay() {
		if (start == null) {
			isSameDay = false;
		}
		if (start.get(2).equals(end.get(2))) {
			isSameDay = true;
		} else {
			isSameDay = false;
		}
	}

	private void setIsAllday() {
		if (!isSameDay) {
			isAllDay = false;
		} else if (start.get(0).equals(Constants.TIME_EARLIEST) && end.get(0).equals(Constants.TIME_LATEST)) {
			isAllDay = true;
		} else {
			isAllDay = false;
		}
	}

}
