package parser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.regex.Pattern;

import shared.Constants;

//@@author A0131188H
public class DateTimeChecker {

	/**
	 * Accepts 24-hour format: 8:00, 08:00, 20:00 8.00, 08.00, 20.00 Accepts
	 * 12-hour format: 1:00am, 1:00pm 1.00am, 1.00pm 1am, 1pm
	 */
	protected boolean isTime(String str) {
		if (Pattern.matches(Constants.TIME_FORMAT_1, str)) {
			return (str.contains(Constants.COLON) || 
					str.contains(Constants.DOT));
		} else if (Pattern.matches(Constants.TIME_FORMAT_2, str)) {
			return true;
		}

		String[] strParts;
		if (str.contains(Constants.DOT)) {
			strParts = str.split(Constants.SPLITTER_DOT);
		} else {
			strParts = str.split(Constants.SPLITTER_COLON);
		}

		if (strParts.length != 2) {
			return false;
		}

		String hour = strParts[0];
		String minute = strParts[1];
		if (!(Pattern.matches(Constants.TIME_INTEGER, hour) && 
				Pattern.matches(Constants.TIME_INTEGER, minute))) {
			return false;
		}

		int hourInInt = Integer.parseInt(hour);
		int minuteInInt = Integer.parseInt(minute);

		return hourInInt >= 0 && hourInInt <= 23 
				&& minuteInInt >= 0 && minuteInInt <= 59;
	}

	/**
	 * Accepts DD-MM-YYYY and DD/MM/YYYY Accepts DD-MM and DD/MM - If date is
	 * not over in current year, current year is assumed - If date is over in
	 * current year, following year is assumed
	 */
	protected boolean isDate(String str) {
		LocalDateTime now = LocalDateTime.now();
		String[] strPartsArr = str.split(Constants.SPLITTER_DATE);
		ArrayList<String> strParts = arrayToArrayList(strPartsArr);

		if (strParts.size() == 2) {
			strParts.add(String.valueOf(now.getYear()));
		}

		if (strParts.size() != 3) {
			return false;
		}

		String day = strParts.get(0);
		String month = strParts.get(1);
		String year = strParts.get(2);
		if (!(Pattern.matches(Constants.TIME_INTEGER, day)
				&& Pattern.matches(Constants.TIME_INTEGER, month) 
				&& Pattern.matches(Constants.TIME_INTEGER, year))) {
			return false;
		}

		if (!(Integer.parseInt(year) > 1915 && Integer.parseInt(year) < 2115)) {
			return false;
		}

		switch (month) {
		case "4":
		case "6":
		case "9":
		case "11":
		case "04":
		case "06":
		case "09":
			return 0 < Integer.parseInt(day) && Integer.parseInt(day) <= 30;

		case "1":
		case "3":
		case "5":
		case "7":
		case "8":
		case "10":
		case "12":
		case "01":
		case "03":
		case "05":
		case "07":
		case "08":
			return 0 < Integer.parseInt(day) && Integer.parseInt(day) <= 31;

		case "2":
		case "02":
			if (isLeapYear(Integer.parseInt(year))) {
				return 0 < Integer.parseInt(day) && Integer.parseInt(day) <= 29;
			} else {
				return 0 < Integer.parseInt(day) && Integer.parseInt(day) <= 28;
			}

		default:
			return false;
		}
	}

	/**
	 * Accepts DDmonth-in-Eng - If date is not over in current year, current
	 * year is assumed - If date is over in current year, following year is
	 * assumed e.g. 1jan
	 */
	protected boolean isMonthInEngDate(String str1) {
		if (isDayMonth(str1)) {
			return isDate(getDayOfDayMonth(str1)
					+ Constants.SLASH
					+ getMonthOfDayMonth(str1)
					+ Constants.SLASH
					+ getCorrectYear(getDayOfDayMonth(str1),
							getMonthOfDayMonth(str1)));
		} else {
			return false;
		}
	}

	/**
	 * Accepts DD month-in-Eng - If date is not over in current year, current
	 * year is assumed - If date is over in current year, following year is
	 * assumed e.g. 1 jan
	 */
	protected boolean isMonthInEngDate1(String str1, String str2) {
		if (isInteger(str1) && getMonthInt(str2) != -1) {
			return isDate(str1 + Constants.SLASH + getMonthStr(str2)
					+ Constants.SLASH + getCorrectYear(str1, getMonthStr(str2)));
		} else {
			return false;
		}
	}

	/**
	 * Accepts DDmonth-in-Eng YYYY e.g. 1jan 2016
	 */
	protected boolean isMonthInEngDate2(String str1, String str2) {
		if (isDayMonth(str1) && isInteger(str2)) {
			return isDate(getDayOfDayMonth(str1) + Constants.SLASH
					+ getMonthOfDayMonth(str1) + Constants.SLASH + str2);
		} else {
			return false;
		}
	}

	/**
	 * Accepts DD month-in-Eng YYYY e.g. 1 jan 2016
	 */
	protected boolean isMonthInEngDate(String str1, String str2, String str3) {
		if ((isInteger(str1) && getMonthInt(str2) != -1 && isInteger(str3))) {
			return isDate(str1 + Constants.SLASH + getMonthStr(str2)
					+ Constants.SLASH + str3);
		} else {
			return false;
		}
	}

	/**
	 * Accepts yesterday/ytd/ today/tonight/ tomorrow/tmr
	 */
	protected boolean isYtdOrTodayOrTmr(String str) {
		String[] ytdOrTodayOrTmr = { Constants.YESTERDAY, Constants.YTD,
				Constants.TODAY, Constants.TONIGHT, Constants.TOMORROW,
				Constants.TMR };
		return isInArray(str, ytdOrTodayOrTmr);
	}

	/**
	 * Accepts [last/this/next] [day] e.g. last mon, this tuesday, this Wed,
	 * next Thursday
	 */
	protected boolean isNaturalLanguageDate(String str1, String str2) {
		String[] lastOrThisOrNext = { Constants.LAST, Constants.THIS,
				Constants.NEXT };
		String[] days = { Constants.MONDAY, Constants.MON, Constants.TUESDAY,
				Constants.TUES, Constants.WEDNESDAY, Constants.WED,
				Constants.THURSDAY, Constants.THURS, Constants.FRIDAY,
				Constants.FRI, Constants.SATURDAY, Constants.SAT,
				Constants.SUNDAY, Constants.SUN };
		boolean isLastOrThisOrNext = isInArray(str1, lastOrThisOrNext);
		boolean isDay = isInArray(str2, days);
		return isLastOrThisOrNext && isDay;
	}

	/**
	 * Helper methods
	 */
	protected String getDayOfDayMonth(String dayMonth) {
		assert (isDayMonth(dayMonth)); // check done by isDayMonth

		String firstChar = dayMonth.substring(0, 1);
		String removeFirstChar = dayMonth.substring(1);
		String firstTwoChars = dayMonth.substring(0, 2);
		if (isInteger(firstChar) && getMonthInt(removeFirstChar) != -1) {
			return firstChar;
		} else {
			return firstTwoChars;
		}
	}

	protected String getMonthOfDayMonth(String dayMonth) {
		assert (isDayMonth(dayMonth)); // check done by isDayMonth

		String firstChar = dayMonth.substring(0, 1);
		String removeFirstChar = dayMonth.substring(1);
		String removeFirstTwoChars = dayMonth.substring(2);
		if (isInteger(firstChar) && getMonthInt(removeFirstChar) != -1) {
			return getMonthStr(removeFirstChar);
		} else {
			return getMonthStr(removeFirstTwoChars);
		}
	}

	protected String getMonthStr(String str) {
		return String.valueOf(getMonthInt(str));
	}

	private int getMonthInt(String str) {
		switch (str.toLowerCase()) {
		case Constants.JANUARY:
		case Constants.JAN:
			return 1;

		case Constants.FEBRUARY:
		case Constants.FEB:
			return 2;

		case Constants.MARCH:
		case Constants.MAR:
			return 3;

		case Constants.APRIL:
		case Constants.APR:
			return 4;

		case Constants.MAY:
			return 5;

		case Constants.JUNE:
		case Constants.JUN:
			return 6;

		case Constants.JULY:
		case Constants.JUL:
			return 7;

		case Constants.AUGUST:
		case Constants.AUG:
			return 8;

		case Constants.SEPTEMBER:
		case Constants.SEP:
			return 9;

		case Constants.OCTOBER:
		case Constants.OCT:
			return 10;

		case Constants.NOVEMBER:
		case Constants.NOV:
			return 11;

		case Constants.DECEMBER:
		case Constants.DEC:
			return 12;

		default:
			return -1;
		}
	}

	private String getCorrectYear(String day, String month) {
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

	private boolean isDayMonth(String str) {
		if (str.length() < 4) {
			return false;
		}

		String firstChar = str.substring(0, 1);
		String removeFirstChar = str.substring(1);
		String firstTwoChars = str.substring(0, 2);
		String removeFirstTwoChars = str.substring(2);

		if (isInteger(firstChar) && getMonthInt(removeFirstChar) != -1) {
			return true;
		} else if (isInteger(firstTwoChars)
				&& getMonthInt(removeFirstTwoChars) != -1) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isLeapYear(int year) {
		return (year % 400 == 0) || ((year % 4 == 0) && (year % 100 != 0));
	}

	private boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private boolean isInArray(String str, String[] array) {
		for (int i = 0; i < array.length; i++) {
			if (str.toLowerCase().equals(array[i])) {
				return true;
			}
		}
		return false;
	}

	private ArrayList<String> arrayToArrayList(String[] array) {
		ArrayList<String> arrayList = new ArrayList<String>();
		for (int i = 0; i < array.length; i++) {
			arrayList.add(array[i]);
		}
		return arrayList;
	}

}
