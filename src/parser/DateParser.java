package parser;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;

import shared.Constants;

//@@author A0131188H
public class DateParser {
	DateTimeChecker dtChecker = new DateTimeChecker();
	private ArrayList<String> args;

	public DateParser(ArrayList<String> args) {
		this.args = args;
	}

	protected int getDateIndex(int start, int end) {
		for (int i = start; i < end; i++) {
			String first = args.get(i);
			String second = Constants.EMPTY;
			String third = Constants.EMPTY;
			if (i + 2 < end) {
				second = args.get(i + 1);
				third = args.get(i + 1);
			} else if (i + 1 < end) {
				second = args.get(i + 1);
			}

			if (dtChecker.isDate(first) || dtChecker.isYtdOrTodayOrTmr(first)) {
				return i;
			} else if (dtChecker.isNaturalLanguageDate(first, second)) {
				return i;
			} else if (dtChecker.isMonthInEngDate(first, second, third)) {
				return i;
			} else if (dtChecker.isMonthInEngDate1(first, second)) {
				return i;
			} else if (dtChecker.isMonthInEngDate2(first, second)) {
				return i;
			} else if (dtChecker.isMonthInEngDate(first)) {
				return i;
			}
		}
		return -1;
	}

	protected String getDate(int start, int end) {
		int dateIndex = getDateIndex(start, end);
		String date = args.get(dateIndex);
		return getDate(date);
	}

	protected String getDate(String date) {
		assert (dtChecker.isDate(date));

		String[] dateParts = date.split(Constants.SPLITTER_DATE);
		int dayRaw = Integer.parseInt(dateParts[0]);
		int monthRaw = Integer.parseInt(dateParts[1]);
		String day = String.format(Constants.FORMATTER_2DP, dayRaw);
		String month = String.format(Constants.FORMATTER_2DP, monthRaw);
		String year;
		if (dateParts.length == 2) { // no year entered
			year = getCorrectYear(day, month);
		} else {
			year = dateParts[2];
		}
		return day + Constants.WHITESPACE + month + Constants.WHITESPACE + year;
	}

	protected String getActualDate(String str) {
		assert (dtChecker.isYtdOrTodayOrTmr(str));

		LocalDateTime date = LocalDateTime.now();

		switch (str.toLowerCase()) {
		case Constants.YESTERDAY:
		case Constants.YTD:
			date = date.minusDays(1);
			break;

		case Constants.TOMORROW:
		case Constants.TMR:
			date = date.plusDays(1);
			break;

		case Constants.TODAY:
		case Constants.TONIGHT:
			break;

		default:
		}

		return date.getDayOfMonth() + Constants.SLASH + date.getMonthValue()
				+ Constants.SLASH + date.getYear();
	}

	protected String getActualDate(String str1, String str2) {
		assert (dtChecker.isNaturalLanguageDate(str1, str2));

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime date = now.with(DayOfWeek.MONDAY);

		switch (str2.toLowerCase()) {
		case Constants.MONDAY:
		case Constants.MON:
			break;

		case Constants.TUESDAY:
		case Constants.TUES:
			date = date.plusDays(1);
			break;

		case Constants.WEDNESDAY:
		case Constants.WED:
			date = date.plusDays(2);
			break;

		case Constants.THURSDAY:
		case Constants.THURS:
			date = date.plusDays(3);
			break;

		case Constants.FRIDAY:
		case Constants.FRI:
			date = date.plusDays(4);
			break;

		case Constants.SATURDAY:
		case Constants.SAT:
			date = date.plusDays(5);
			break;

		case Constants.SUNDAY:
		case Constants.SUN:
			date = date.plusDays(6);
			break;

		default:
		}

		if (str1.equals(Constants.LAST)) {
			date = date.minusWeeks(1);
		} else if (str1.equals(Constants.NEXT)) {
			date = date.plusWeeks(1);
		} else if (str1.equals(Constants.THIS)) {
		} else {
		}

		return date.getDayOfMonth() + Constants.SLASH + date.getMonthValue()
				+ Constants.SLASH + date.getYear();
	}

	protected String getCorrectYear(String day, String month) {
		LocalDateTime now = LocalDateTime.now();
		String year;
		if (Integer.parseInt(month) < now.getMonthValue()) {
			year = String.valueOf(now.plusYears(1).getYear());
		} else if (Integer.parseInt(month) == now.getMonthValue()
				&& Integer.parseInt(day) < now.getDayOfMonth()) {
			year = String.valueOf(now.plusYears(1).getYear());
		} else {
			year = String.valueOf(now.getYear());
		}
		return year;
	}

}
