package parser;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Parser {
	
	private DateTimeFormatter DTFormatter = DateTimeFormatter.ofPattern("dd MM yyyy HH mm");
	private static String DEADLINE_END_KEYWORD = "by";
	private static String BOUNDED_START_KEYWORD = "from";
	private static String BOUNDED_END_KEYWORD = "to";
	
	public AbstractCommand parseInput(String rawInput) {
		ArrayList<String> args = arrayToArrayList(rawInput.split(" "));
		String cmd = args.remove(0);
		
		switch (cmd.toLowerCase()) {
			case "create" :
			case "c" :
				return create(args);
				
			case "display" :
			case "dp" :
				return display(args);
				
			case "delete" :
			case "dl" :
				return delete(args);
				
			case "edit" :
			case "e" :
				return edit(args);
				
			default :
				return invalidCommand();
		}
	}

	private AbstractCommand create(ArrayList<String> args) {
		if (isBounded(args)) {
			return createBounded(args);
		} else if (isDeadline(args)) {
			return createDeadline(args);
		} else if (isFloating(args)) {
			return createFloating(args);
		} else {
			return invalidCommand();
		}
	}

	private CreateCommand createFloating(ArrayList<String> args) {
		String name = getName(args, args.size());
		return new CreateCommand(name);
	}
	
	private CreateCommand createDeadline(ArrayList<String> args) {
		int index = getIndexOf(args, DEADLINE_END_KEYWORD);
		String name = getName(args, index);
		String time = getTime(args.get(index + 1));
		String date = getDate(args.get(index + 2));
		LocalDateTime dateTime = LocalDateTime.parse(date + " " + time, DTFormatter);
		return new CreateCommand(name, dateTime);
	}

	private CreateCommand createBounded(ArrayList<String> args) {
		int sIndex = getIndexOf(args, BOUNDED_START_KEYWORD);
		int eIndex = getIndexOf(args, BOUNDED_END_KEYWORD);
		String name = getName(args, sIndex);
		String sTime = getTime(args.get(sIndex + 1));
		String sDate = getDate(args.get(sIndex + 2));
		String eTime = getTime(args.get(eIndex + 1));
		String eDate = getDate(args.get(eIndex + 2));
		LocalDateTime sDateTime = LocalDateTime.parse(sDate + " " + sTime, DTFormatter);
		LocalDateTime eDateTime = LocalDateTime.parse(eDate + " " + eTime, DTFormatter);
		return new CreateCommand(name, sDateTime, eDateTime);
	}
	
	private boolean isFloating(ArrayList<String> args) {
		return args.size() > 0;
	}
	
	private boolean isDeadline(ArrayList<String> args) {
		int index = getIndexOf(args, DEADLINE_END_KEYWORD);
		if (index != -1 && hasTwoArgsAftIndex(args, index)) {
			return isDateTime(args.get(index + 1), args.get(index + 2)) && index > 0;
		} else {
			return false;
		}
	}

	private boolean isBounded(ArrayList<String> args) {
		int sIndex = getIndexOf(args, BOUNDED_START_KEYWORD);
		int eIndex = getIndexOf(args, BOUNDED_END_KEYWORD);	
		if (sIndex != -1 && eIndex != -1 && eIndex - sIndex == 3 && 
				hasTwoArgsAftIndex(args, sIndex) && hasTwoArgsAftIndex(args, eIndex)) {
			return isDateTime(args.get(sIndex + 1), args.get(sIndex + 2)) && 
						 isDateTime(args.get(eIndex + 1), args.get(eIndex + 2)) && 
						 sIndex > 0;
		} else {
			return false;
		}
	}

	private AbstractCommand display(ArrayList<String> args) {
	// TODO Auto-generated method stub
	return null;
	}

	private AbstractCommand delete(ArrayList<String> args) {
	// TODO Auto-generated method stub
	return null;
	}

	private AbstractCommand edit(ArrayList<String> args) {
	// TODO Auto-generated method stub
	return null;
	}

	private AbstractCommand invalidCommand() {
	// TODO Auto-generated method stub
	return null;
	}
	
	// Accepts 24-hour format: 8:00, 08:00, 20:00
	// Accepts 12-hour format: 1:00am, 1:00AM, 1:00pm, 1:00PM, 1am, 1AM, 1pm, 1PM
	public boolean isTime(String str) {
		String tf24 = "([012]?[0-9]|1[0-9]|2[0-3]):[0-5][0-9]";
		String tf12first = "(1[012]|[1-9]|0[1-9]):[0-5][0-9](?i)(am|pm)";
		String tf12second = "(1[012]|[1-9])(?i)(am|pm)";		
		return Pattern.matches(tf24, str) | Pattern.matches(tf12first, str) | Pattern.matches(tf12second, str);
	}


	// dd-mm-yy or dd-mm-yyyy or dd/mm/yy or dd/mm/yyyy
	// dd or mm can be single digit or padded single digit or double digit
	// day+month combination works for all months except Feb (always 1 Feb - 28 Feb regardless of leap year)
	public static boolean isDate(String str) {
		String df = "(((0[1-9])|([12])([0-9]?)|(3[01]?))(-|\\/)(0?[13578]|10|12)(-|\\/)((\\d{4})|(\\d{2}))|((0[1-9])|([12])([0-9]?)|(3[0]?))(-|\\/)(0?[2469]|11)(-|\\/)((\\d{4}|\\d{2})))$"; 
		if (Pattern.matches(df, str)) {
			String[] dateParts = str.split("(-|\\/)");
			int day = Integer.parseInt(dateParts[0]);
			int month = Integer.parseInt(dateParts[1]);
			return (month == 2 && day > 28) ? false : true;
			} else {
				return false;
			}
	}
	
	private boolean isDateTime(String str1, String str2) {
		if (isTime(str1) && isDate(str2)) {
			return true;
		//} else if (isDate(arg1) && isTime(arg2)) {
		//	return true;
		} else {
			return false;
		}
	}
	
	private String getName(ArrayList<String> args, int stopIndex) {
		String output = "";
		for (int i = 0; i < stopIndex; i++) {
			output += args.get(i) + " ";
		}
		return output.trim();
	}
	
	private String getDate(String date) {
		String[] dateParts = date.split("(-|\\/)");
		String day = String.format("%02d", Integer.parseInt(dateParts[0]));
		String month = String.format("%02d", Integer.parseInt(dateParts[1]));
		String year = formatYear(dateParts[2]);
		return day + " " + month + " " + year;
	}
	
	
	private String getTime(String time) {
		time = time.toLowerCase();
		int hourInInt = getHour(time);
		int minuteInInt = getMinute(time);
		String AMPM = getAMPM(time);
		if (AMPM.equals("pm") && hourInInt != 12) {
			hourInInt += 12;
		}
		if (AMPM.equals("am") && hourInInt == 12) {
			hourInInt = 0;
		}
		
		String hour = String.format("%02d", hourInInt);		
		String minute = String.format("%02d", minuteInInt);	
		return hour + " " + minute;
	}

	private int getHour(String time) {
		time = time.replace("am", "");
		time = time.replace("pm", "");
		if (time.contains(":")) {
			String[] timeParts = time.split(":");
			return Integer.parseInt(timeParts[0]);
		} else {
			return Integer.parseInt(time);
		}
	}
	
	private int getMinute(String time) {
		time = time.replace("am", "");
		time = time.replace("pm", "");
		if (time.contains(":")) {
			String[] timeParts = time.split(":");
			return Integer.parseInt(timeParts[1]);
		} else {
			return 0;
		}
	}

	private String getAMPM(String time) {
		if (time.contains("am")) {
			return "am";
		} else if (time.contains("pm")) {
			return "pm";
		} else {
			return "";
		}
	}
	
	private String formatYear(String year) {
		if (year.length() == 2) {
			return "20" + year;
		} else {
			return year;
		}
	}

	private int getIndexOf(ArrayList<String> args, String keyword) {
		int index = -1;
		for (int i = 0; i < args.size(); i++) {
			if (args.get(i).equals(keyword)) {
				index = i;
			}
		}
		return index;
	}
	
	private boolean hasTwoArgsAftIndex(ArrayList<String> args, int index) {
		return (args.get(index + 1) != null && args.get(index + 2) != null);
	}
	
	private ArrayList<String> arrayToArrayList(String[] array) {
		ArrayList<String> arrayList = new ArrayList<String>();
		for (int i = 0; i < array.length; i++) {
			arrayList.add(array[i]);
		}
		return arrayList;
	}
}
