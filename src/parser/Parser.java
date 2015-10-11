package parser;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Parser {
	
	protected DateTimeFormatter DTFormatter = DateTimeFormatter.ofPattern("dd MM yyyy HH mm");
	private static String DEADLINE_END_KEYWORD = "by";
	private static String BOUNDED_START_KEYWORD = "from";
	private static String BOUNDED_END_KEYWORD = "to";
	
	public AbstractCommand parseInput(String inputStr) {
		ArrayList<String> inputArgs = arrayToArrayList(inputStr.split(" "));
		String cmd = inputArgs.remove(0);
		
		switch (cmd.toLowerCase()) {
			case "create" :
			case "c" :
				return create(inputArgs);
				
			case "display" :
			case "dp" :
				return display(inputArgs);
				
			case "delete" :
			case "dl" :
				return delete(inputArgs);
				
			case "edit" :
			case "e" :
				return edit(inputArgs);
				
			default :
				return invalidCommand();
		}
	}

	private AbstractCommand create(ArrayList<String> inputArgs) {
		if (isBoundedTask(inputArgs)) {
			return createBoundedTask(inputArgs);
		} else if (isDeadlineTask(inputArgs)) {
			return createDeadlineTask(inputArgs);
		} else if (isFloatingTask(inputArgs)) {
			return createFloatingTask(inputArgs);
		} else {
			return invalidCommand();
		}
	}

	private CreateCommand createFloatingTask(ArrayList<String> inputArgs) {
		String name = getName(inputArgs, inputArgs.size());
		return new CreateCommand(name);
	}
	
	private CreateCommand createDeadlineTask(ArrayList<String> inputArgs) {
		int endIndex = getIndexOf(inputArgs, DEADLINE_END_KEYWORD);
		String name = getName(inputArgs, endIndex);
		LocalDateTime endDateTime = LocalDateTime.parse(getDate(inputArgs.get(endIndex + 2)) + " " + getTime(inputArgs.get(endIndex + 1)), DTFormatter);
		return new CreateCommand(name, endDateTime);
	}

	private CreateCommand createBoundedTask(ArrayList<String> inputArgs) {
		int startIndex = getIndexOf(inputArgs, BOUNDED_START_KEYWORD);
		int endIndex = getIndexOf(inputArgs, BOUNDED_END_KEYWORD);
		String name = getName(inputArgs, startIndex);
		LocalDateTime startDateTime = LocalDateTime.parse(getDate(inputArgs.get(startIndex + 2)) + " " + getTime(inputArgs.get(startIndex + 1)), DTFormatter);
		LocalDateTime endDateTime = LocalDateTime.parse(getDate(inputArgs.get(endIndex + 2)) + " " + getTime(inputArgs.get(endIndex + 1)), DTFormatter);
		return new CreateCommand(name, startDateTime, endDateTime);
	}
	
	private boolean isFloatingTask(ArrayList<String> inputArgs) {
		return inputArgs.size() > 0;
	}
	
	private boolean isDeadlineTask(ArrayList<String> inputArgs) {
		int endIndex = getIndexOf(inputArgs, DEADLINE_END_KEYWORD);
		if (endIndex != -1 && hasTwoArgsAftIndex(inputArgs, endIndex)) {
			return isDateTime(inputArgs.get(endIndex + 1), inputArgs.get(endIndex + 2)) && endIndex > 0;
		} else {
			return false;
		}
	}

	private boolean isBoundedTask(ArrayList<String> inputArgs) {
		int startIndex = getIndexOf(inputArgs, BOUNDED_START_KEYWORD);
		int endIndex = getIndexOf(inputArgs, BOUNDED_END_KEYWORD);	
		if (startIndex != -1 && endIndex != -1 && endIndex - startIndex == 3 && hasTwoArgsAftIndex(inputArgs, startIndex) && hasTwoArgsAftIndex(inputArgs, endIndex)) {
			return isDateTime(inputArgs.get(startIndex + 1), inputArgs.get(startIndex + 2)) && isDateTime(inputArgs.get(endIndex + 1), inputArgs.get(endIndex + 2)) && startIndex > 0;
		} else {
			return false;
		}
	}

	private AbstractCommand display(ArrayList<String> inputArgs) {
	// TODO Auto-generated method stub
	return null;
	}

	private AbstractCommand delete(ArrayList<String> inputArgs) {
	// TODO Auto-generated method stub
	return null;
	}

	private AbstractCommand edit(ArrayList<String> inputArgs) {
	// TODO Auto-generated method stub
	return null;
	}

	private AbstractCommand invalidCommand() {
	// TODO Auto-generated method stub
	return null;
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
	
	//Accepts 24-hour format: 8:00, 08:00, 20:00
	// Accepts 12-hour format: 1:00am, 1:00AM, 1:00 am (not used by us), 1:00 AM
	// (not used by us),
	// 1:00pm, 1:00PM, 1:00 pm (not used by us), 1:00 PM (not used by us)
	// 1 or 2 or 3 or 4 or 5 or 6 or 7 or 8 or 9 or 10 or 11 or 12 +
	// : + (digit) + (digit) + (space (not used by us) || no space) + (am || AM
	// || pm || PM)
	public static boolean isTime(String str) {
		String tf24 = "([012]?[0-9]|1[0-9]|2[0-3]):[0-5][0-9]";
		String tf12first = "(1[012]|[1-9]):[0-5][0-9](\\s)?(?i)(am|pm)";
		String tf12second = "([1-9]|1[0-2])(?i)(am|pm)";
		return Pattern.matches(tf24, str) | Pattern.matches(tf12first, str) | Pattern.matches(tf12second, str);
	}


	//dd-mm-yy or dd-mm-yyyy or dd/mm/yy or dd/mm/yyyy
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

	private String formatYear(String year) {
		if (year.length() == 2) {
			return "20" + year;
		} else {
			return year;
		}
	}
	
	private String getName(ArrayList<String> inputArgs, int stopIndex) {
		String output = "";
		for (int i = 0; i < stopIndex; i++) {
			output += inputArgs.get(i) + " ";
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

	private int getIndexOf(ArrayList<String> inputArgs, String keyword) {
		int index = -1;
		for (int i = 0; i < inputArgs.size(); i++) {
			if (inputArgs.get(i).equals(keyword)) {
				index = i;
			}
		}
		return index;
	}
	
	private boolean hasTwoArgsAftIndex(ArrayList<String> inputArgs, int index) {
		return (inputArgs.get(index + 1) != null && inputArgs.get(index + 2) != null);
	}
	
	private ArrayList<String> arrayToArrayList(String[] array) {
		ArrayList<String> arrayList = new ArrayList<String>();
		for (int i = 0; i < array.length; i++) {
			arrayList.add(array[i]);
		}
		return arrayList;
	}
}
