//@@author A0133888N
package ui.view;

import java.util.List;

import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * This class is used to implement details of a calendar box.
 */
public class CalendarBox extends Group {

	private static final int SPLITTER_TRANSLATE_X = -19;
	private static final int SPLITTER_TRANSLATE_X_WIDE = -59;
	private static final int WEEKDAY_BACKGROUND_WIDTH = 12;
	private static final int WEEKDAY_TRANSLATE_X = -28;
	private static final int WEEKDAY_TRANSLATE_X_WIDE_ADJUST = -40;
	private static final int WEEKDAY_TRANSLATE_Y_MID = -1;
	private static final int WEEKDAY_TRANSLATE_Y_SPACING = 12;
	private static final int WEEKDAY_BACKGROUND_X_TRANSLATE = -9;
	private static final int TIME_DATEMONTH_TRANSLATE_X = 9;
	private static final int TIME_TRANSLATE_Y = -10;
	private static final int DATEMONTH_TRANSLATE_Y = 11;
	private static final int LIST_SIZE = 5;
	private static final Color COLOR_WEEKDAY = Color.BLACK;
	private static final Color COLOR_WEEKDAY_BACKGROUND = Color.YELLOW;
	private static final Color COLOR_DONE = Color.rgb(166, 166, 166); // moderately dark grey

	private boolean isWide;
	private boolean isDone;
	private boolean hasYear;
	private boolean isAllDay;
	private StackPane stackPane;
	private Rectangle calendarBox;
	private Color backgroundColor;
	private List<String> start;
	private List<String> end;

	public CalendarBox(Rectangle calendarBox, List<String> start, List<String> end, boolean isDone, boolean hasYear,
			boolean isWide, Color backgroundColor, boolean isAllDay) {

		initialize(calendarBox, start, end, isDone, hasYear, isWide, backgroundColor, isAllDay);
		addSplitter(isDone, isWide);
		addWeekDayBox();
		addWeekDay(end);
		addTime();
		addDateMonth(end);

	}

	private void initialize(Rectangle calendarBox, List<String> start, List<String> end, boolean isDone,
			boolean hasYear, boolean isWide, Color backgroundColor, boolean isAllDay) {
		assert start.size() == LIST_SIZE;
		assert end.size() == LIST_SIZE;
		this.start = start;
		this.end = end;
		this.isDone = isDone;
		this.hasYear = hasYear;
		this.isWide = isWide;
		this.isAllDay = isAllDay;
		this.stackPane = new StackPane();
		this.calendarBox = calendarBox;
		this.backgroundColor = backgroundColor;
		stackPane.getChildren().add(this.calendarBox);
		this.getChildren().add(stackPane);
	}

	/**
	 * Add a line splitting the weekday and the time. The color of the line
	 * synchronizes with the background of the enclosing task view.
	 * 
	 * @param isDone
	 * @param isWide
	 */
	private void addSplitter(boolean isDone, boolean isWide) {
		Rectangle splitter = new Rectangle();
		splitter.setHeight(calendarBox.getHeight());
		splitter.setWidth(2);
		if (!isDone) {
			splitter.setFill(backgroundColor);
		} else {
			splitter.setFill(Color.WHITE);
		}
		stackPane.getChildren().add(splitter);

		if (!isWide) {
			splitter.setTranslateX(SPLITTER_TRANSLATE_X);
		} else {
			splitter.setTranslateX(SPLITTER_TRANSLATE_X_WIDE);
		}

	}

	/**
	 * The three letters of a weekday are displayed vertically.
	 * 
	 * @param list
	 */
	private void addWeekDay(List<String> list) {

		String weekDay = list.get(1);
		if (weekDay.isEmpty()) {
			return;
		}
		processWeekDay(weekDay, 0, WEEKDAY_TRANSLATE_X, WEEKDAY_TRANSLATE_Y_MID - WEEKDAY_TRANSLATE_Y_SPACING);
		processWeekDay(weekDay, 1, WEEKDAY_TRANSLATE_X, WEEKDAY_TRANSLATE_Y_MID);
		processWeekDay(weekDay, 2, WEEKDAY_TRANSLATE_X, WEEKDAY_TRANSLATE_Y_MID + WEEKDAY_TRANSLATE_Y_SPACING);
	}

	private void processWeekDay(String weekDay, int charIndex, int CoordinateX, int CoordinateY) {
		Text weekDayChar = new Text();
		String weekDayString = weekDay.substring(charIndex, charIndex + 1);
		weekDayChar.setText(weekDayString);
		weekDayChar.setFill(COLOR_WEEKDAY);
		weekDayChar.setStyle("-fx-line-spacing: 0px;");
		stackPane.getChildren().add(weekDayChar);
		if (!isWide) {
			weekDayChar.setTranslateX(CoordinateX);
		} else {
			weekDayChar.setTranslateX(CoordinateX + WEEKDAY_TRANSLATE_X_WIDE_ADJUST);
		}

		weekDayChar.setTranslateY(CoordinateY);
		weekDayChar.setFont(Font.font("Monaco", FontWeight.BOLD, 12));
	}

	private void addWeekDayBox() {
		Rectangle weekDaybackGround = new Rectangle();
		weekDaybackGround.setWidth(WEEKDAY_BACKGROUND_WIDTH);
		weekDaybackGround.setHeight(calendarBox.getHeight() * 0.95);
		if (!isDone) {
			weekDaybackGround.setFill(COLOR_WEEKDAY_BACKGROUND);
		} else {
			weekDaybackGround.setFill(COLOR_DONE);
		}

		stackPane.getChildren().add(weekDaybackGround);

		if (!isWide) {
			weekDaybackGround.setTranslateX(SPLITTER_TRANSLATE_X + WEEKDAY_BACKGROUND_X_TRANSLATE);
		} else {
			weekDaybackGround.setTranslateX(SPLITTER_TRANSLATE_X_WIDE + WEEKDAY_BACKGROUND_X_TRANSLATE);
		}

	}

	private void addTime() {
		if(isAllDay) {
			Text allDay = new Text();
			allDay.setText("ALL DAY");
			//allDay.setFont(Font.font(null, FontWeight.BOLD, allDay.getFont().getSize()));;
			stackPane.getChildren().add(allDay);
			allDay.setTranslateX(TIME_DATEMONTH_TRANSLATE_X);
			allDay.setTranslateY(TIME_TRANSLATE_Y);
		} else {
			Text time = new Text();
			if (!isWide) {
				time.setText(end.get(0));
			} else {
				time.setText(start.get(0) + " - " + end.get(0));
			}

			stackPane.getChildren().add(time);

			time.setTranslateX(TIME_DATEMONTH_TRANSLATE_X);
			time.setTranslateY(TIME_TRANSLATE_Y);
		}
	}

	private void addDateMonth(List<String> list) {
		assert list.get(2) + list.get(3) + list.get(4) != null; 
		Text dateMonth = new Text();
		if (isToday(list) || list.get(0).equals("")) {
			hasYear = false;
		}
		if (!hasYear) {
			dateMonth.setText(list.get(2) + " " + list.get(3));
		} else if (!isWide) {
			// In a normal calendar box, only the last two digits of a year is
			// displayed.
			// E.g. '15 for 2015, where the " '" indicates abbreviation.
			dateMonth.setText(list.get(2) + " " + list.get(3) + " '" + list.get(4).substring(2, 4));
			dateMonth.setFont(Font.font(dateMonth.getFont().getSize() - 3));
			dateMonth.setTranslateX(-1);// minor adjustment
		} else {
			// In a wide calendar box, the year is displayed completely.
			// E.g. 2015 for 2015.
			dateMonth.setText(start.get(2) + " " + start.get(3) + " " + start.get(4));
		}

		stackPane.getChildren().add(dateMonth);
		
		dateMonth.setTranslateY(DATEMONTH_TRANSLATE_Y);
		dateMonth.setTranslateX(TIME_DATEMONTH_TRANSLATE_X);
	}

	private boolean isToday(List<String> list) {
		if (list.get(2).equals("TODAY")) {
			return true;
		} else {
			return false;
		}
	}

}
