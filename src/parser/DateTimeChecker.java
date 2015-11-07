package parser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.regex.Pattern;

import shared.Constants;

public class DateTimeChecker {
	// Accepts 24-hour format: 8:00, 08:00, 20:00,
	//												 8.00, 08.00, 20.00
	// Accepts 12-hour format: 1:00am, 1:00AM, 1:00pm, 1:00PM, 
	//												 1.00am, 1.00AM, 1.00pm, 1.00PM
	// 												 1am, 1AM, 1pm, 1PM
	public boolean isTime(String str) {
		String tf12first = "(0[1-9]|[1-9]|1[012])(:|.)[0-5][0-9](?i)(am|pm)";
		String tf12second = "(1[012]|[1-9])(?i)(am|pm)";		
		
		if (Pattern.matches(tf12first, str)) {
			if (str.contains(":") || str.contains(".")) {
				return true;
			} else {
				return false;
			}
		} else if (Pattern.matches(tf12second, str)) {
			return true;
		}
		
		String[] strParts;
		if (str.contains(".")) {
			strParts = str.split("\\.");
		} else {
			strParts = str.split(":");
		}
		
		if (strParts.length != 2) {
			return false;
		}
		
		String hour = strParts[0];
		String minute = strParts[1];
		String integer = "0|00|(^[0-9]*[1-9][0-9]*$)";
		if (!(Pattern.matches(integer, hour) && Pattern.matches(integer, minute))) {
			return false;
		}
		
		int hourInInt = Integer.parseInt(hour);
		int minuteInInt = Integer.parseInt(minute);
		
		return hourInInt >= 0 && hourInInt <= 23 && 
					 minuteInInt >= 0 && minuteInInt <= 59;
	}

	// Accepts dd-mm-yyyy and dd/mm/yyyy
	// Accepts dd-mm and dd/mm
	// Accepts dd month-In-English yyyy and ddmonth-In-English yyyy
	// - Requires processing
	// Accepts dd month-In-English and ddmonth-In-English
	// - Requires processing
	public boolean isDate(String str) {
		LocalDateTime now = LocalDateTime.now();
		String[] strPartsTemp = str.split(Constants.SPLITTER_DATE);
		ArrayList<String> strParts = arrayToArrayList(strPartsTemp);
		
		if (strParts.size() == 2) {
			strParts.add(String.valueOf(now.getYear()));
		}
		
		if (strParts.size() != 3) {
			return false;
		}
		
		String day = strParts.get(0);
		String month = strParts.get(1);
		String year = strParts.get(2);
		String integer = "^[0-9]*[1-9][0-9]*$";
		if (!(Pattern.matches(integer, day) && 
					Pattern.matches(integer, month) && 
					Pattern.matches(integer, year))) {
			return false;
		}
		
		if (!(Integer.parseInt(year) > 1915 && Integer.parseInt(year) < 2115)) {
			return false;
		}
		
		switch(month) {
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
		} else if (isInteger(firstTwoChars) && getMonthInt(removeFirstTwoChars) != -1) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isLeapYear(int year) {
		return (year % 400 == 0) || ((year % 4 == 0) && (year % 100 != 0));
	}
	
	protected boolean isYtdOrTodayOrTmr(String str) {
		String[] ytdOrTodayOrTmr = { "yesterday", "ytd", "today", "tonight", "tomorrow", "tmr" };
		return isInArray(str, ytdOrTodayOrTmr);
	}
	
	protected boolean isNaturalLanguageDate(String str1, String str2) {
		String[] lastOrThisOrNext = { "last", "this", "next" };
		String[] days = { "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday",
											"mon", "tues", "wed", "thurs", "fri", "sat", "sun" };
		boolean isLastOrThisOrNext = isInArray(str1, lastOrThisOrNext);		
		boolean isDay = isInArray(str2, days);
		return isLastOrThisOrNext && isDay;
	}
	
	protected boolean isMonthInEngDate(String str1) { // accepts 1jan
		if (isDayMonth(str1)) {
			return isDate(getDayOfDayMonth(str1) + Constants.SLASH + 
						 				getMonthOfDayMonth(str1) + Constants.SLASH + 
						 				getCorrectYear(getDayOfDayMonth(str1), getMonthOfDayMonth(str1)));
		} else {
			return false;
		}
	}
	
	protected boolean isMonthInEngDate1(String str1, String str2) { // accepts 1 jan
		if (isInteger(str1) && getMonthInt(str2) != -1) {
			return isDate(str1 + Constants.SLASH + 
										getMonthStr(str2) + Constants.SLASH + 
										getCorrectYear(str1, getMonthStr(str2)));
		} else {
			return false;
		}
	}
	
	protected boolean isMonthInEngDate2(String str1, String str2) { // accepts 1jan 2015 
		if (isDayMonth(str1) && isInteger(str2)) {
			return isDate(getDayOfDayMonth(str1) + Constants.SLASH + 
									  getMonthOfDayMonth(str1) + Constants.SLASH + 
									  str2);
		} else {
			return false;
		}
	}

	protected boolean isMonthInEngDate(String str1, String str2, String str3) { // accepts 1 jan 2015
		if ((isInteger(str1) && getMonthInt(str2) != -1 && isInteger(str3))) {
			return isDate(str1 + Constants.SLASH + 
										getMonthStr(str2) + Constants.SLASH + 
										str3);
		} else {
			return false;
		}
	}
	
	private int getMonthInt(String str) {
		switch(str.toLowerCase()) {
		case "jan":
		case "january":
			return 1;
				
		case "feb":
		case "february":
			return 2;
				
		case "mar":
		case "march":
			return 3;
				
		case "apr":
		case "april":
			return 4;
				
		case "may":
			return 5;
				
		case "jun":
		case "june":
			return 6;
				
		case "jul":
		case "july":
			return 7;
				
		case "aug":
		case "august":
			return 8;
				
		case "sep":
		case "september":
			return 9;
				
		case "oct":
		case "october":
			return 10;
				
		case "nov":
		case "november":
			return 11;
				
		case "dec":
		case "december":
			return 12;
				
		default:
			return -1;
		}
	}

	protected String getMonthStr(String str) {
		return String.valueOf(getMonthInt(str));
	}
	
	protected String getDayOfDayMonth(String dayMonth) {
		assert(isDayMonth(dayMonth)); // check done by isDayMonth
		
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
		assert(isDayMonth(dayMonth)); // check done by isDayMonth
		
		String firstChar = dayMonth.substring(0, 1);
		String removeFirstChar = dayMonth.substring(1);
		String removeFirstTwoChars = dayMonth.substring(2);
		if (isInteger(firstChar) && getMonthInt(removeFirstChar) != -1) {
			return getMonthStr(removeFirstChar);
		} else {
			return getMonthStr(removeFirstTwoChars);
		} 
	}
	
	protected String getCorrectYear(String day, String month) {
		LocalDateTime now = LocalDateTime.now();
		String year;
		if (Integer.parseInt(month) < now.getMonthValue()) {
			year = String.valueOf(now.plusYears(1).getYear());
		} else if (Integer.parseInt(month) == now.getMonthValue() && 
							 Integer.parseInt(day) < now.getDayOfMonth()) {
			year = String.valueOf(now.plusYears(1).getYear());
		} else {
			year = String.valueOf(now.getYear());
		}
		return year;
	}
	
	private boolean isInteger(String str) {
    try {
      Integer.parseInt(str);
      return true;
	  } catch(NumberFormatException e) {
	      return false;
	  }
	}
	
	private boolean isInArray(String str, String[] array) {
		for(int i = 0; i < array.length; i ++) {
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
