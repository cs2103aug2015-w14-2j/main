package parser;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;

import shared.Constants;

public class DateParser {
	DateTimeChecker dtChecker = new DateTimeChecker();
	private ArrayList<String> args;
	
	public DateParser(ArrayList<String> args) {
		this.args = args;
	}
	
	protected int getDateIndex(int start, int end) {
		for (int i = start; i < end; i++) {
			System.out.println("test" + args.get(i));
			if (dtChecker.isDate(args.get(i)) || dtChecker.isYtdOrTodayOrTmr(args.get(i))) {
				return i;
			} else if ((i + 1) < end && dtChecker.isNaturalLanguageDate(args.get(i), args.get(i + 1))) {
				return i;
			} else if ((i + 2) < end && dtChecker.isMonthInEngDate(args.get(i), args.get(i + 1), args.get(i + 2))) {
				return i;
			} else if ((i + 1) < end && dtChecker.isMonthInEngDate1(args.get(i), args.get(i + 1))) {
				return i;
			} else if ((i + 1) < end && dtChecker.isMonthInEngDate2(args.get(i), args.get(i + 1))) {
				return i;
			} else if (dtChecker.isMonthInEngDate(args.get(i))) {
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
		System.out.println(date);
		assert(dtChecker.isDate(date));

		String[] dateParts = date.split("(-|\\/|\\s)");
		String day = String.format("%02d", Integer.parseInt(dateParts[0]));
		String month = String.format("%02d", Integer.parseInt(dateParts[1]));
		String year;
		if (dateParts.length == 2) { // no year entered
			year = getCorrectYear(day, month);
		} else {
			year = dateParts[2];
		}
		return day + " " + month + " " + year;
	}
	
	protected String getRealDate(String str) {
		//assert(isYtdOrTodayOrTmr(str));
		
		LocalDateTime now = LocalDateTime.now();
		
		switch (str.toLowerCase()) {
		case "yesterday" :
		case "ytd" :
			now = now.minusDays(1);
			break;
		
		case "tomorrow" :
		case "tmr" :
			now = now.plusDays(1);
			break;
				
		case "today" :
		case "tonight" :
			break;
				
		default :
		}
		
		return now.getDayOfMonth() + "/" + now.getMonthValue() + "/" + now.getYear();
	}
	
	protected String getRealDate(String str1, String str2) {
		//assert(isNaturalLanguageDate(str1, str2));
		
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime date = now.with(DayOfWeek.MONDAY);

		switch (str2.toLowerCase()) {
		case "monday" :
		case "mon" :
			break;
				
		case "tuesday" :
		case "tues" :
			date = date.plusDays(1);
			break;
				
		case "wednesday" :
		case "wed" :
			date = date.plusDays(2);
			break;
				
		case "thursday" :
		case "thurs" :
			date = date.plusDays(3);
			break;
				
		case "friday" :
		case "fri" :
			date = date.plusDays(4);
			break;
				
		case "saturday" :
		case "sat" : 
			date = date.plusDays(5);
			break;
				
		case "sunday" :
		case "sun" :
			date = date.plusDays(6);
			break;
				
		default :
		}
		
		if (str1.equals(Constants.LAST)) {
			date = date.minusWeeks(1);
		} else if (str1.equals(Constants.NEXT)) {
			date = date.plusWeeks(1);
		} else if (str1.equals(Constants.THIS)) {
		} else {
		}

		return date.getDayOfMonth() + "/" + date.getMonthValue() + "/" + date.getYear();
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
}
