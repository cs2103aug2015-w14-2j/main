package parser;

import java.util.ArrayList;

import shared.Constants;

//@@author A0131188H
public class TimeParser {
	DateTimeChecker dtChecker = new DateTimeChecker();
	private ArrayList<String> args;

	public TimeParser(ArrayList<String> args) {
		this.args = args;
	}

	protected int getTimeIndex(int start, int end) {
		for (int i = start; i < end; i++) {
			if (dtChecker.isTime(args.get(i))) {
				return i;
			}
		}
		return -1;
	}

	protected String getTime(int start, int end) {
		int timeIndex = getTimeIndex(start, end);
		String time = args.get(timeIndex);
		return getTime(time);
	}

	protected String getTime(String time) {
		time = time.toLowerCase();
		assert (dtChecker.isTime(time));

		int hourInInt = getHour(time);
		int minuteInInt = getMinute(time);
		String AMPM = getAMPM(time);

		if (AMPM.equals(Constants.PM) && hourInInt != 12) {
			hourInInt += 12;
		}
		if (AMPM.equals(Constants.AM) && hourInInt == 12) {
			hourInInt = 0;
		}

		String hour = String.format(Constants.FORMATTER_2DP, hourInInt);
		String minute = String.format(Constants.FORMATTER_2DP, minuteInInt);
		return hour + Constants.WHITESPACE + minute;
	}

	/**
	 * Helper methods
	 */
	private int getHour(String time) {
		assert (dtChecker.isTime(time));

		time = time.replace(Constants.AM, Constants.EMPTY);
		time = time.replace(Constants.PM, Constants.EMPTY);
		if (time.contains(Constants.COLON)) {
			String[] timeParts = time.split(Constants.SPLITTER_COLON);
			return Integer.parseInt(timeParts[0]);
		} else if (time.contains(Constants.DOT)) {
			String[] timeParts = time.split(Constants.SPLITTER_DOT);
			return Integer.parseInt(timeParts[0]);
		} else {
			return Integer.parseInt(time);
		}
	}

	private int getMinute(String time) {
		assert (dtChecker.isTime(time));

		time = time.replace(Constants.AM, Constants.EMPTY);
		time = time.replace(Constants.PM, Constants.EMPTY);
		if (time.contains(Constants.COLON)) {
			String[] timeParts = time.split(Constants.SPLITTER_COLON);
			return Integer.parseInt(timeParts[1]);
		} else if (time.contains(Constants.DOT)) {
			String[] timeParts = time.split(Constants.SPLITTER_DOT);
			return Integer.parseInt(timeParts[1]);
		} else {
			return 0;
		}
	}

	private String getAMPM(String time) {
		assert (dtChecker.isTime(time));

		if (time.contains(Constants.AM)) {
			return Constants.AM;
		} else if (time.contains(Constants.PM)) {
			return Constants.PM;
		} else {
			return Constants.EMPTY;
		}
	}

}
