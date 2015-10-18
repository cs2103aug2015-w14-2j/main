package parser;

import java.util.ArrayList;
import java.util.regex.Pattern;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Parser {
	
	private DateTimeFormatter DTFormatter = DateTimeFormatter.ofPattern("dd MM yyyy HH mm");
	
	private static String DEADLINE_END_KEYWORD = "by";
	private static String BOUNDED_START_KEYWORD = "from";
	private static String BOUNDED_END_KEYWORD = "to";
	private static String EDIT_NAME_KEYWORD = "to";
	private static String EDIT_START_KEYWORD = "start";
	private static String EDIT_END_KEYWORD = "end";
	
	private static String[] YTD_OR_TODAY_OR_TMR = { "yesterday", "ytd", "today", "tomorrow", "tmr" };
	private static String[] LAST_OR_THIS_OR_NEXT = { "last", "this", "next" };
	private static String[] DAYS = { "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday",
																		"mon", "tues", "wed", "thurs", "fri", "sat", "sun", "week", "wk" };
	
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
				
			case "search" :
			case "s" :
				return search(args);
				
			default :
				return invalidCommand();
		}
	}

	private AbstractCommand create(ArrayList<String> args) {
		if (args.size() == 0) {
			return invalidCommand();
		}
		
		if (isBounded(args)) {
			args = processBounded(args);
			return createBounded(args);
		} else if (isDeadline(args)) {
			args = processDeadline(args);
			return createDeadline(args);
		} else if (isFloating(args)) {
			return createFloating(args);
		} else {
			return invalidCommand();
		}
	}

	private AbstractCommand createFloating(ArrayList<String> args) {
		String name = getName(args, args.size());
		
		if (name.length() == 0) {
			return invalidCommand();
		}
		
		return new CreateCommand(name);
	}
	
	private AbstractCommand createDeadline(ArrayList<String> args) {
		int index = getIndexOf(args, DEADLINE_END_KEYWORD);
		String name = getName(args, index);
		String time = getTime(args.get(index + 1));
		String date = getDate(args.get(index + 2));
		LocalDateTime dateTime = LocalDateTime.parse(date + " " + time, DTFormatter);
		
		if (name.length() == 0) {
			return invalidCommand();
		}
		
		return new CreateCommand(name, dateTime);
	}

	private AbstractCommand createBounded(ArrayList<String> args) {
		int sIndex = getIndexOf(args, BOUNDED_START_KEYWORD);
		int eIndex = getIndexOf(args, BOUNDED_END_KEYWORD);
		String name = getName(args, sIndex);
		String sTime = getTime(args.get(sIndex + 1));
		String sDate = getDate(args.get(sIndex + 2));
		String eTime = getTime(args.get(eIndex + 1));
		String eDate = getDate(args.get(eIndex + 2));
		LocalDateTime sDateTime = LocalDateTime.parse(sDate + " " + sTime, DTFormatter);
		LocalDateTime eDateTime = LocalDateTime.parse(eDate + " " + eTime, DTFormatter);
		
		if (name.length() == 0) {
			return invalidCommand();
		}
		
		return new CreateCommand(name, sDateTime, eDateTime);
	}
	
	private boolean isFloating(ArrayList<String> args) {
		return args.size() > 0;
	}
	
	private boolean isDeadline(ArrayList<String> args) {
		int index = getIndexOf(args, DEADLINE_END_KEYWORD);
		if (index != -1 && hasThreeArgsAftIndex(args, index)) {
			return isDateTime(args.get(index + 1), args.get(index + 2), args.get(index + 3));
		}	else if (index != -1 && hasTwoArgsAftIndex(args, index)) {
			return isDateTime(args.get(index + 1), args.get(index + 2));
		} else {
			return false;
		}
	}

	private boolean isBounded(ArrayList<String> args) {
//		print(args);
		
		int sIndex = getIndexOf(args, BOUNDED_START_KEYWORD);
		int eIndex = getIndexOf(args, BOUNDED_END_KEYWORD);
		
		boolean bool1 = false;
		if (sIndex != -1 && hasThreeArgsAftIndex(args, sIndex)) {
			bool1 = bool1 || isDateTime(args.get(sIndex + 1), args.get(sIndex + 2), args.get(sIndex + 3));
		}	
		if (sIndex != -1 && hasTwoArgsAftIndex(args, sIndex)) {
			bool1 = bool1 || isDateTime(args.get(sIndex + 1), args.get(sIndex + 2));
		}
		
		boolean bool2 = false;
		if (eIndex != -1 && hasThreeArgsAftIndex(args, eIndex)) {
			bool2 = bool2 || isDateTime(args.get(eIndex + 1), args.get(eIndex + 2), args.get(eIndex + 3));
		}	
		if (eIndex != -1 && hasTwoArgsAftIndex(args, eIndex)) {
			bool2 = bool2 || isDateTime(args.get(eIndex + 1), args.get(eIndex + 2));
		}
		
//		System.out.println("bool1 = " + bool1);
//		System.out.println("bool2 = " + bool2);
		
		return bool1 && bool2;
//		if (sIndex != -1 && eIndex != -1 && eIndex - sIndex == 3 && 
//				hasTwoArgsAftIndex(args, sIndex) && hasTwoArgsAftIndex(args, eIndex)) {
//			return isDateTime(args.get(sIndex + 1), args.get(sIndex + 2)) && 
//						 isDateTime(args.get(eIndex + 1), args.get(eIndex + 2));
//		} else {
//			return false;
//		}
	}

	private ArrayList<String> processBounded(ArrayList<String> args) {
		int sIndex = getIndexOf(args, BOUNDED_START_KEYWORD);		
		if (isYtdOrTmr(args.get(sIndex + 2))) {
			args.add(sIndex + 2, getActualDate(args.get(sIndex + 2)));
			args.remove(sIndex + 3);
		}

		
		if (hasThreeArgsAftIndex(args, sIndex) && isNaturalLanguageDate(args.get(sIndex + 2), args.get(sIndex + 3))) {
			args.add(sIndex + 2, getActualDate(args.get(sIndex + 2), args.get(sIndex + 3)));
			args.remove(sIndex + 3);
			args.remove(sIndex + 3);
		}
		
		int eIndex = getIndexOf(args, BOUNDED_END_KEYWORD);
		if (isYtdOrTmr(args.get(eIndex + 2))) {
			args.add(eIndex + 2, getActualDate(args.get(eIndex + 2)));
			args.remove(eIndex + 3);
		}
		
		if (hasThreeArgsAftIndex(args, eIndex) && isNaturalLanguageDate(args.get(eIndex + 2), args.get(eIndex + 3))) {
			args.add(eIndex + 2, getActualDate(args.get(eIndex + 2), args.get(eIndex + 3)));
			args.remove(eIndex + 3);
			args.remove(eIndex + 3);
		}

		return args;
	}

	private ArrayList<String> processDeadline(ArrayList<String> args) {
		int index = getIndexOf(args, DEADLINE_END_KEYWORD);
		if (isYtdOrTmr(args.get(index + 2))) {
			args.add(index + 2, getActualDate(args.get(index + 2)));
			args.remove(index + 3);
		}
		if (hasThreeArgsAftIndex(args, index) && isNaturalLanguageDate(args.get(index + 2), args.get(index + 3))) {
			args.add(index + 2, getActualDate(args.get(index + 2), args.get(index + 3)));
			args.remove(index + 3);
		}
		return args;
	}
	
	private AbstractCommand display(ArrayList<String> args) {
		if (args.size() == 0) {
			return new DisplayCommand(DisplayCommand.Scope.ALL);
		}
		
		String firstWord = args.get(0).toLowerCase();
		if (firstWord.equals("all")) {
			return new DisplayCommand(DisplayCommand.Scope.ALL);
		} else if (firstWord.equals("done")) {
			return new DisplayCommand(DisplayCommand.Scope.DONE);
		} else if (firstWord.equals("undone")) {
			return new DisplayCommand(DisplayCommand.Scope.UNDONE);
		} else {
			return search(args);
		}
	}
	
	private AbstractCommand search(ArrayList<String> args) {
		if (args.size() == 0) {
			return new DisplayCommand(DisplayCommand.Scope.ALL);
		}
		
		String firstWord = args.get(0).toLowerCase();
		if (isDate(firstWord)) {
			return new DisplayCommand(LocalDateTime.parse(getDate(firstWord)));
		} else {
			return new DisplayCommand(getName(args, args.size()));
		}
	}

	private AbstractCommand delete(ArrayList<String> args) {
		if (args.size() == 0) {
			return invalidCommand();
		}
		
		String firstWord = args.get(0).toLowerCase();
		if (firstWord.equals("all")) {
			return new DeleteCommand(DeleteCommand.Scope.ALL);
		} else if (firstWord.equals("done")) {
			return new DeleteCommand(DeleteCommand.Scope.DONE);
		} else if (firstWord.equals("undone")) {
			return new DeleteCommand(DeleteCommand.Scope.UNDONE);
		} else if (isHashInteger(firstWord)) {
			return new DeleteCommand(firstWord.substring(1));
		} else {
			return new DeleteCommand(getName(args, args.size()));
		}
	}
	
	private AbstractCommand edit(ArrayList<String> args) {
		if (args.size() == 0) {
			return invalidCommand();
		}
		
		EditCommand output;
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		
		int index = getIndexOf(args, EDIT_NAME_KEYWORD);
		int start = getIndexOf(args, EDIT_START_KEYWORD);
		int end = getIndexOf(args, EDIT_END_KEYWORD);

		int endPointSearch;
		if (index != -1) { // index exists
			endPointSearch = index;
		} else if (start != -1) { // index not exists, start exists
			endPointSearch = start;
		} else if (end != -1) { // index and start not exists, end exists
			endPointSearch = end;
		} else {
			endPointSearch = args.size();
		}
		
		int endPointName;
		if (start != -1 && end != -1) {
			endPointName = start;
		} else if (start != -1) {
			endPointName = start;
		} else if (end != -1) {
			endPointName = end;
		} else {
			endPointName = args.size();
		}
		
		int startPointName;
		if (index == -1) {
			startPointName = endPointName;
		} else {
			startPointName = index + 1;
		}
		
		int endPointStart;
		if (end != -1) {
			endPointStart = end;
		} else {
			endPointStart = args.size();
		}
		
		String search = getName(args, endPointSearch);
		if (isHashInteger(search)) {
			output = new EditCommand(search.substring(1));
		} else {
			output = new EditCommand(search);
		}
		
		String newName = getName(args, startPointName, endPointName);
		if (newName.length() != 0) {
			editType.add(EditCommand.editField.NAME);
			output.setNewName(newName);
		}
		
		if (start != -1) {

			int indexOfsTime = getTimeBetween(args, start + 1, endPointStart);
			if (indexOfsTime != -1) {
				String stime = getTime(args.get(indexOfsTime));	
				editType.add(EditCommand.editField.START_TIME);
				output.setNewStartTime(stime);
			}
			
			int indexOfsDate = getDateBetween(args, start + 1, endPointStart);
			if (indexOfsDate != -1) {
				String sdate = getDate(args.get(indexOfsDate));
				editType.add(EditCommand.editField.START_DATE);
				output.setNewStartDate(sdate);
			}
			
		}
		
		if (end != -1) {
			
			int indexOfeTime = getTimeBetween(args, end + 1, args.size());
			if (indexOfeTime != -1) {
				String etime = getTime(args.get(indexOfeTime));
				editType.add(EditCommand.editField.END_TIME);
				output.setNewEndTime(etime);
			}
			
			int indexOfeDate = getDateBetween(args, end + 1, args.size());
			if (indexOfeDate != -1) {
				String edate = getDate(args.get(indexOfeDate));
				editType.add(EditCommand.editField.END_DATE);
				output.setNewEndDate(edate);
			}
			
		}
		
		output.setEditFields(editType);
		return output;
	}

	private int getDateBetween(ArrayList<String> args, int start, int end) {
		for (int i = start; i < end; i++) {
			if (isDate(args.get(i)) || isYtdOrTmr(args.get(i))) {
				return i;
			} else if ((i + 1) < end && isNaturalLanguageDate(args.get(i), args.get(i + 1))) {
				return i;
			}
		}
		return -1;
	}

	private int getTimeBetween(ArrayList<String> args, int start, int end) {
		for (int i = start; i < end; i++) {
			if (isTime(args.get(i))) {
				return i;
			}
		}
		return -1;
	}

	private boolean isHashInteger(String str) {
		String[] strParts = str.split("");
		try {
			int i = Integer.parseInt(str.substring(1));
			if (i <= 0) {
				return false;
			}
			return strParts[0].equals("#");
		} catch(NumberFormatException e) { 
			return false; 
    } catch(NullPointerException e) {
    	return false;
    }
	}

	private AbstractCommand invalidCommand() {
		return new InvalidCommand();
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
	
	private String getActualDate(String str) {
		DateTime dt = new DateTime();
		switch (str.toLowerCase()) {
			case "yesterday" :
			case "ytd" :
				dt = dt.minusDays(1);
				break;
		
			case "tomorrow" :
			case "tmr" :
				dt = dt.plusDays(1);
				break;
				
			case "today" :
				break;
				
			default :
				return str;		
		}
		return dt.getDayOfMonth() + " " + dt.getMonthOfYear() + " " + dt.getYear();
	}
	
	private String getActualDate(String str1, String str2) {
		DateTime today = new DateTime();
		DateTime dt = today.withDayOfWeek(DateTimeConstants.MONDAY);
		
		switch (str2.toLowerCase()) {
			case "monday" :
			case "mon" :
				break;
				
			case "tuesday" :
			case "tues" :
				dt = dt.plusDays(1);
				break;
				
			case "wednesday" :
			case "wed" :
				dt = dt.plusDays(2);
				break;
				
			case "thursday" :
			case "thurs" :
				dt = dt.plusDays(3);
				break;
				
			case "friday" :
			case "fri" :
				dt = dt.plusDays(4);
				break;
				
			case "saturday" :
			case "sat" : 
				dt = dt.plusDays(5);
				break;
				
			case "sunday" :
			case "sun" :
				dt = dt.plusDays(6);
				break;
				
			case "week" :
			case "wk" :
				break;
			
			default :
		}
		
		if (str1.equals("last")) {
			dt = dt.minusWeeks(1);
		} else if (str1.equals("next")) {
			dt = dt.plusWeeks(1);
		} else if (str1.equals("this")) {
		} else {
		}

		return dt.getDayOfMonth() + " " + dt.getMonthOfYear() + " " + dt.getYear();
	}
	
	private String processYtdOrTmr(String ytdOrTmr) {
		return getActualDate(ytdOrTmr);
	}
	
	private boolean isDateTime(String str1, String str2) {
		if (isTime(str1) && isDate(str2)) {
			return true;
		} else if (isTime(str1) && isYtdOrTmr(str2)) {
			return true;
		//} else if (isDate(arg1) && isTime(arg2)) {
		//	return true;
		} else {
			return false;
		}
	}
	
	private boolean isDateTime(String str1, String str2, String str3) {
		return (isTime(str1) && isNaturalLanguageDate(str2, str3));
	}
	
	private boolean isYtdOrTmr(String str) {
		for (int i = 0; i < YTD_OR_TODAY_OR_TMR.length; i++) {
			if (str.toLowerCase().equals(YTD_OR_TODAY_OR_TMR[i])) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isNaturalLanguageDate(String str1, String str2) {
		boolean bool1 = false;
		for (int i = 0; i < LAST_OR_THIS_OR_NEXT.length; i++) {
			if (str1.toLowerCase().equals(LAST_OR_THIS_OR_NEXT[i])) {
				bool1 = true;
			}
		}
		
		boolean bool2 = false;
		for (int i = 0; i < DAYS.length; i++) {
			if (str2.toLowerCase().equals(DAYS[i])) {
				bool2 = true;
			}
		}
		
		return bool1 && bool2;
	}

	private String getName(ArrayList<String> args, int stopIndex) {
		String output = "";
		for (int i = 0; i < stopIndex; i++) {
			output += args.get(i) + " ";
		}
		return output.trim();
	}
	
	private String getName(ArrayList<String> args, int startIndex, int stopIndex) {
		String output = "";
		for (int i = startIndex; i < stopIndex; i++) {
			output += args.get(i) + " ";
		}
		return output.trim();
	}
	
	private String getDate(String date) {
		if (isYtdOrTmr(date)) {
			return processYtdOrTmr(date);
		} else {
			String[] dateParts = date.split("(-|\\/|\\s)");
			String day = String.format("%02d", Integer.parseInt(dateParts[0]));
			String month = String.format("%02d", Integer.parseInt(dateParts[1]));
			String year = formatYear(dateParts[2]);
			return day + " " + month + " " + year;
		}
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
		return index + 2 <= args.size() - 1;
	}
	
	private boolean hasThreeArgsAftIndex(ArrayList<String> args, int index) {
		return index + 3 <= args.size() - 1;
	}
	
	private ArrayList<String> arrayToArrayList(String[] array) {
		ArrayList<String> arrayList = new ArrayList<String>();
		for (int i = 0; i < array.length; i++) {
			arrayList.add(array[i]);
		}
		return arrayList;
	}
	
	private void print(ArrayList<String> args) {
		for (int i = 0; i < args.size(); i++) {
			System.out.println(args.get(i));
		}
		System.out.println();
	}
}
