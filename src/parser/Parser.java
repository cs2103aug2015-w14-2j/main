package parser;

import java.util.ArrayList;
import java.util.regex.Pattern;
import shared.command.AbstractCommand;
import shared.command.CreateCommand;
import shared.command.DeleteCommand;
import shared.command.DisplayCommand;
import shared.command.EditCommand;
import shared.command.UICommand;
import shared.command.ExitCommand;
import shared.command.HelpCommand;
import shared.command.InvalidCommand;
import shared.command.MarkCommand;
import shared.command.SaveCommand;
import shared.command.UndoCommand;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Parser {
	
	private DateTimeFormatter DTFormatter = DateTimeFormatter.ofPattern("dd MM yyyy HH mm");
	
	private static String BY = "by";
	private static String FROM = "from";
	private static String TO = "to";
	private static String START = "start";
	private static String END = "end";
	
	private static String ALL = "all";
	private static String DONE = "done";
	private static String UNDONE = "undone";
	private static String FLOATING = "floating";
	private static String MARK = "mark";
	private static String UNMARK = "unmark";
	
	private static String WEEK = "week";
	private static String YEAR = "year";
	
	private static String[] YTD_OR_TODAY_OR_TMR = { "yesterday", "ytd", "today", "tonight", "tomorrow", "tmr" };
	private static String[] LAST_OR_THIS_OR_NEXT = { "last", "this", "next" };
	private static String[] DAYS = { "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday",
																		"mon", "tues", "wed", "thurs", "fri", "sat", "sun" };

	private static String dummyTime = "00 00";
	
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
				
			case "mark" :
			case "m" :
				return mark(args, "mark");
				
			case "unmark" :
			case "um" :
				return mark(args, "unmark");
				
			case "undo" :
			case "u" :
				return undo();
				
			case "help" :
				return help();
				
			case "save" :
				return save(args);
				
			case "exit" :
				return exit();
				
			case "day" :
			case "night" :
			case "hide" :
			case "show" :
				return empty(args);
				
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
		assert(isFloating(args));  // check done by isFloating
		
		String name = getName(args, args.size());
		
		if (name.length() == 0) {
			return invalidCommand();
		}
		
		return new CreateCommand(name);
	}
	
	private AbstractCommand createDeadline(ArrayList<String> args) {
		assert(isDeadline(args));  // check done by isDeadline
		
		int index = getIndexOf(args, BY);
		
		String name = getName(args, index);
		String time = getTime(args.get(getTimeIndexBetween(args, index, args.size())));
		String date = getDate(args.get(getDateIndexBetween(args, index, args.size())));
		LocalDateTime dateTime = LocalDateTime.parse(date + " " + time, DTFormatter);
		
		if (name.length() == 0) {
			return invalidCommand();
		}
		
		return new CreateCommand(name, dateTime);
	}

	private AbstractCommand createBounded(ArrayList<String> args) {	
		assert(isBounded(args));  // check done by isBounded
		
		int sIndex = getIndexOf(args, FROM);
		int eIndex = getIndexOf(args, TO);
		
		String name = getName(args, sIndex);
		String sTime = getTime(args.get(getTimeIndexBetween(args, sIndex, eIndex)));
		String sDate = getDate(args.get(getDateIndexBetween(args, sIndex, eIndex)));
		String eTime = getTime(args.get(getTimeIndexBetween(args, eIndex, args.size())));
		String eDate = getDate(args.get(getDateIndexBetween(args, eIndex, args.size())));
		LocalDateTime sDateTime = LocalDateTime.parse(sDate + " " + sTime, DTFormatter);
		LocalDateTime eDateTime = LocalDateTime.parse(eDate + " " + eTime, DTFormatter);
		
		if (name.length() == 0) {
			return invalidCommand();
		}
		
		return new CreateCommand(name, sDateTime, eDateTime);
	}
	
	private AbstractCommand display(ArrayList<String> args) {
		if (args.size() == 0) {
			return new DisplayCommand(DisplayCommand.Scope.DEFAULT);
		}
		
		String firstWord = args.get(0).toLowerCase();
		if (firstWord.equals(ALL) && args.size() == 1) {
			return new DisplayCommand(DisplayCommand.Scope.ALL);
		} else if (firstWord.equals(DONE) && args.size() == 1) {
			return new DisplayCommand(DisplayCommand.Scope.DONE);
		} else if (firstWord.equals(UNDONE) && args.size() == 1) {
			return new DisplayCommand(DisplayCommand.Scope.UNDONE);
		} else if (firstWord.equals(FLOATING) && args.size() == 1) {
			return new DisplayCommand(DisplayCommand.Scope.FLOATING);
		} else if (firstWord.equals(WEEK) && args.size() == 1) {
			return new DisplayCommand(LocalDateTime.parse(stringify(LocalDateTime.now()) + " " + dummyTime, DTFormatter), LocalDateTime.parse(stringify(LocalDateTime.now().plusWeeks(1)) + " " + dummyTime, DTFormatter));
		} else {
			return search(args);
		}
	}
	
	private AbstractCommand search(ArrayList<String> args) {
		if (args.size() == 0) {
			return new DisplayCommand(DisplayCommand.Scope.UNDONE);
		}
		
		int fromIndex = getIndexOf(args, FROM);
		int toIndex = getIndexOf(args, TO);
		int dateIndex = getDateIndexBetween(args, 0, args.size());
		
		if (fromIndex != -1 && toIndex != -1) {
			int startDateIndex = getDateIndexBetween(args, fromIndex, toIndex);
			int endDateIndex;
			endDateIndex = getDateIndexBetween(args, toIndex, args.size());
			if (startDateIndex != -1 && endDateIndex != -1) {
				args = processDate(args, startDateIndex);
				endDateIndex = getDateIndexBetween(args, toIndex, args.size());
				args = processDate(args, endDateIndex);
				return new DisplayCommand(LocalDateTime.parse(getDate(args.get(startDateIndex)) + " " + dummyTime, DTFormatter), 
																	LocalDateTime.parse(getDate(args.get(endDateIndex)) + " " + dummyTime, DTFormatter));
			}
			
		} else if (fromIndex != -1 && toIndex == -1) {
			int startDateIndex = getDateIndexBetween(args, fromIndex, args.size());
			if (startDateIndex != -1 && processDate(args, startDateIndex).size() == 2) {
				args = processDate(args, startDateIndex);
				return new DisplayCommand(LocalDateTime.parse(getDate(args.get(startDateIndex)) + " " + dummyTime, DTFormatter), DisplayCommand.Type.SEARCHDATEONWARDS);
			}
			
		} else if (dateIndex != -1 && fromIndex == -1 && toIndex == -1) {
			if (processDate(args, dateIndex).size() == 1) {
				args = processDate(args, dateIndex);
				return new DisplayCommand(LocalDateTime.parse(getDate(args.get(dateIndex)) + " " + dummyTime, DTFormatter), DisplayCommand.Type.SEARCHDATE);
			}
		}
		
		return new DisplayCommand(getName(args, args.size()));
	}

	private AbstractCommand delete(ArrayList<String> args) {
		if (args.size() == 0) {
			return invalidCommand();
		}
		
		String firstWord = args.get(0).toLowerCase();
		if (firstWord.equals(ALL)) {
			return new DeleteCommand(DeleteCommand.Scope.ALL);
		} else if (firstWord.equals(DONE)) {
			return new DeleteCommand(DeleteCommand.Scope.DONE);
		} else if (firstWord.equals(UNDONE)) {
			return new DeleteCommand(DeleteCommand.Scope.UNDONE);
		} else if (isInteger(firstWord)) {
			return new DeleteCommand(Integer.parseInt(firstWord));
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
		
		int index = getIndexOf(args, TO);
		int start = getIndexOf(args, START);
		int end = getIndexOf(args, END);

		int endPointSearch;
		if (index != -1) { // [index] exists => end point of search at [index] ("to")
			endPointSearch = index;
		} else if (start != -1) { // [index] not exists, [start] exists => end point of search at [start]
			endPointSearch = start;
		} else if (end != -1) { // [index] and [start] not exists, [end] exists => end point of search at [end]
			endPointSearch = end;
		} else { // end point of search at end of args
			endPointSearch = args.size();
		}
		
		int endPointName;
		if (start != -1) { // [start] exists => end point of name at [start]
			endPointName = start;
		} else if (end != -1) { // [end] exists => end point of name at [end]
			endPointName = end;
		} else { // end point of name at end of args
			endPointName = args.size();
		}
		
		int startPointName;
		if (index == -1) { // no fields for edit name
			startPointName = endPointName;
		} else { // start point of name is one after [index] ("to")
			startPointName = index + 1;
		}
		
		int endPointStart;
		if (end != -1) { // [end] exists => search for start datetime to end at [end]
			endPointStart = end;
		} else { // no fields for end datetime => search for start datetime until end of args
			endPointStart = args.size();
		}
		
		String search = getNameWithSlash(args, endPointSearch);
		if (isInteger(search)) {
			output = new EditCommand(Integer.parseInt(search));
		} else {
			output = new EditCommand(getName(args, endPointSearch));
		}
		
		String newName = getName(args, startPointName, endPointName);
		if (newName.length() != 0) {
			editType.add(EditCommand.editField.NAME);
			output.setNewName(newName);
		}
		
		if (start != -1) {

			int indexOfsTime = getTimeIndexBetween(args, start, endPointStart);
			if (indexOfsTime != -1) {
				String stime = getTime(args.get(indexOfsTime));	
				editType.add(EditCommand.editField.START_TIME);
				output.setNewStartTime(stime);
			}
			
			int indexOfsDate = getDateIndexBetween(args, start, endPointStart);
			if (indexOfsDate != -1) {
				args = processDate(args, indexOfsDate);
				String sdate = getDate(args.get(indexOfsDate));
				editType.add(EditCommand.editField.START_DATE);
				output.setNewStartDate(sdate);
			}
			
		}
		
		if (end != -1) {
			
			int indexOfeTime = getTimeIndexBetween(args, end, args.size());
			if (indexOfeTime != -1) {
				String etime = getTime(args.get(indexOfeTime));
				editType.add(EditCommand.editField.END_TIME);
				output.setNewEndTime(etime);
			}
			
			int indexOfeDate = getDateIndexBetween(args, end, args.size());
			if (indexOfeDate != -1) {
				args = processDate(args, indexOfeDate);
				String edate = getDate(args.get(indexOfeDate));
				editType.add(EditCommand.editField.END_DATE);
				output.setNewEndDate(edate);
			}
			
		}
		
		output.setEditFields(editType);
		return output;
	}
	
	private AbstractCommand mark(ArrayList<String> args, String str) {
		if (args.size() == 0) {
			return invalidCommand();
		}
		
		MarkCommand output;
		
		String firstWord = args.get(0).toLowerCase();
		if (isInteger(firstWord)) {
			output = new MarkCommand(Integer.parseInt(firstWord));
		} else {
			output = new MarkCommand(getName(args, args.size()));
		}
		
		if (str.equals(MARK)) {
			output.setMarkField(MarkCommand.markField.MARK);
		} else if (str.equals(UNMARK)) {
			output.setMarkField(MarkCommand.markField.UNMARK);
		} else {
			return invalidCommand();
		}
		
		return output;
	}
	
	private AbstractCommand undo() {
		return new UndoCommand();
	}

	private AbstractCommand help() {
		return new HelpCommand();
	}
	
	private AbstractCommand save(ArrayList<String> args) {
		if (args.size() == 0) {
			return invalidCommand();
		}
		
		return new SaveCommand(args.get(0));
	}
	
	private AbstractCommand exit() {
		return new ExitCommand();
	}
	
	private AbstractCommand empty(ArrayList<String> args) {
		if (args.size() == 0) {
			return new UICommand();
		} else if (args.size() == 1 && args.get(0).equals(YEAR)) {
			return new UICommand();
		} else {
			return invalidCommand();
		}
	}
	
	private AbstractCommand invalidCommand() {
		return new InvalidCommand();
	}

	
	
	
	// process yesterday/today/tomorrow to dd-mm-yyyy
	// process last/this/next + day to dd-mm-yyyy
	// process day + month-in-English + year to dd-mm-yyyy
	// process day + month-in-English to dd-mm-yyyy
	private ArrayList<String> processDeadline(ArrayList<String> args) {
		int index = getIndexOf(args, BY);
		
		assert(index != -1); // check done by isDeadline
		
		int timeIndex = getTimeIndexBetween(args, index, args.size());
		int dateIndex = getDateIndexBetween(args, index, args.size());
		
		assert(timeIndex != -1);  // check done by isDeadline
		assert(dateIndex != -1);  // check done by isDeadline
		
		args = processDate(args, dateIndex);
		
		assert(isDate(args.get(dateIndex))); // done by processDeadline
		
		return args;
	}
	
	// process yesterday/today/tomorrow to dd-mm-yyyy
	// process last/this/next + day to dd-mm-yyyy
	// process day + month-in-English + year to dd-mm-yyyy
	// process day + month-in-English to dd-mm-yyyy
	private ArrayList<String> processBounded(ArrayList<String> args) {
		int sIndex = getIndexOf(args, FROM);
		int eIndex = getIndexOf(args, TO);
		
		assert(sIndex != -1); // check done by isBounded
		assert(eIndex != -1); // check done by isBounded
		
		int sTimeIndex = getTimeIndexBetween(args, sIndex, eIndex);
		int sDateIndex = getDateIndexBetween(args, sIndex, eIndex);
		int eTimeIndex = getTimeIndexBetween(args, eIndex, args.size());
		int eDateIndex = getDateIndexBetween(args, eIndex, args.size());
		
		assert(sTimeIndex != -1); // check done by isBounded
		assert(eTimeIndex != -1); // check done by isBounded
		assert(sDateIndex != -1 || eDateIndex != -1); // check done by isBounded

		if (sDateIndex != -1 && eDateIndex == -1) { // same date for start and end, date is between "from" and "to"
			args = processDate(args, sDateIndex);
			args.add(eIndex + 1, args.get(sDateIndex));
			
			eIndex = getIndexOf(args, TO);
			eTimeIndex = getTimeIndexBetween(args, eIndex, args.size());
			eDateIndex = getDateIndexBetween(args, eIndex, args.size());
			
		} else if (sDateIndex == -1 && eDateIndex != -1) { // same date for start and end, date is after "to"
			args = processDate(args, eDateIndex);
			args.add(sIndex + 1, args.get(eDateIndex));
			
			sDateIndex = getDateIndexBetween(args, sIndex, eIndex);
			eTimeIndex = getTimeIndexBetween(args, eIndex, args.size());
			eDateIndex = getDateIndexBetween(args, eIndex, args.size());
			
		} else {
			args = processDate(args, sDateIndex);
			eTimeIndex = getTimeIndexBetween(args, eIndex, args.size());
			eDateIndex = getDateIndexBetween(args, eIndex, args.size());
			args = processDate(args, eDateIndex);
		}
		
		assert(isDate(args.get(sDateIndex))); // done by processBounded
		assert(isDate(args.get(eDateIndex))); // done by processBounded
		
		return args;
	}
	
	private ArrayList<String> processDate(ArrayList<String> args, int dateIndex) {
		ArrayList<String> pArgs = new ArrayList<String>(args);
		
		if (!isDate(pArgs.get(dateIndex))) {
			if (isYtdOrTodayOrTmr(pArgs.get(dateIndex))) {
				pArgs.set(dateIndex, getActualDate(pArgs.get(dateIndex)));
				
			} else if ((dateIndex + 1) < pArgs.size() && isNaturalLanguageDate(pArgs.get(dateIndex), pArgs.get(dateIndex + 1))) {
				pArgs.set(dateIndex, getActualDate(pArgs.get(dateIndex), pArgs.get(dateIndex + 1)));
				pArgs.remove(dateIndex + 1);
				
			} else if ((dateIndex + 2) < pArgs.size() && isMonthInEnglishDate(pArgs.get(dateIndex), pArgs.get(dateIndex + 1), pArgs.get(dateIndex + 2))) {
				String day = pArgs.get(dateIndex);
				String month = String.valueOf(getMonthValue(pArgs.get(dateIndex + 1)));
				String year = pArgs.get(dateIndex + 2);
				pArgs.set(dateIndex, getDate(day + "/" + month + "/" + year));
				pArgs.remove(dateIndex + 2);
				pArgs.remove(dateIndex + 1);
			
			} else if ((dateIndex + 1) < pArgs.size() && isMonthInEnglishDate1(pArgs.get(dateIndex), pArgs.get(dateIndex + 1))) {
				String day = pArgs.get(dateIndex);
				String month = String.valueOf(getMonthValue(pArgs.get(dateIndex + 1)));
				String year = getCorrectYear(day, month);
				pArgs.set(dateIndex, getDate(day + "/" + month + "/" + year));
				pArgs.remove(dateIndex + 1);
				
			} else if ((dateIndex + 1) < pArgs.size() && isMonthInEnglishDate2(pArgs.get(dateIndex), pArgs.get(dateIndex + 1))) {
				String dayMonth = pArgs.get(dateIndex);
				String day = getDayOfDayMonth(dayMonth);
				String month = getMonthOfDayMonth(dayMonth);
				String year = pArgs.get(dateIndex + 1);
				pArgs.set(dateIndex, getDate(day + "/" + month + "/" + year));
				pArgs.remove(dateIndex + 1);
				
			} else if (isMonthInEnglishDate(pArgs.get(dateIndex))) {
				String dayMonth = pArgs.get(dateIndex);
				String day = getDayOfDayMonth(dayMonth);
				String month = getMonthOfDayMonth(dayMonth);
				String year = getCorrectYear(day, month);
				pArgs.set(dateIndex, getDate(day + "/" + month + "/" + year));
				
			}
		}
		
		return pArgs;
	}
	
	private boolean isFloating(ArrayList<String> args) {
		return args.size() > 0;
	}
	
	private boolean isDeadline(ArrayList<String> args) {
		int index = getIndexOf(args, BY);
		
		if (index == -1) {
			return false;
		}
		
		int timeIndex = getTimeIndexBetween(args, index, args.size());
		int dateIndex = getDateIndexBetween(args, index, args.size());
		
		return (timeIndex != -1) && (dateIndex != -1);
	}

	private boolean isBounded(ArrayList<String> args) {
		int sIndex = getIndexOf(args, FROM);
		int eIndex = getIndexOf(args, TO);
		
		if (sIndex == -1 || eIndex == -1) {
			return false;
		}
		
		int sTimeIndex = getTimeIndexBetween(args, sIndex, eIndex);
		int sDateIndex = getDateIndexBetween(args, sIndex, eIndex);
		int eTimeIndex = getTimeIndexBetween(args, eIndex, args.size());
		int eDateIndex = getDateIndexBetween(args, eIndex, args.size());
		
		return (sTimeIndex != -1) && (eTimeIndex != -1) && (sDateIndex != -1 || eDateIndex != -1);
	}
	
	private boolean isInteger(String str) {
		str = str.trim();
    try {
      Integer.parseInt(str);
      return true;
	  } catch(NumberFormatException e) {
	      return false;
	  }
	}
	
	// Accepts 24-hour format: 8:00, 08:00, 20:00
	// Accepts 12-hour format: 1:00am, 1:00AM, 1:00pm, 1:00PM, 1am, 1AM, 1pm, 1PM
	public boolean isTime(String str) {
		String tf12first = "(1[012]|[1-9]|0[1-9]):[0-5][0-9](?i)(am|pm)";
		String tf12second = "(1[012]|[1-9])(?i)(am|pm)";		
		
		if (Pattern.matches(tf12first, str) || Pattern.matches(tf12second, str)) {
			return true;
		}
		
		String[] strParts = str.split(":");
		
		if (strParts.length != 2) {
			return false;
		}
		
		String hour = strParts[0];
		String minute = strParts[1];
		String integer = "0|00|(^[0-9]*[1-9][0-9]*$)";
		if (! (Pattern.matches(integer, hour) && Pattern.matches(integer, minute))) {
			return false;
		}
		
		int hourInInt = Integer.parseInt(hour);
		int minuteInInt = Integer.parseInt(minute);
		
		return hourInInt >= 0 && hourInInt <= 23 && minuteInInt >= 0 && minuteInInt <= 59;
	}

	// Accepts dd-mm-yyyy and dd/mm/yyyy
	// Accepts dd-mm and dd/mm
	// Accepts dd month-In-English yyyy and ddmonth-In-English yyyy
	// Accepts dd month-In-English and ddmonth-In-English
	public boolean isDate(String str) {
		LocalDateTime dt = LocalDateTime.now();
		String[] strPartsTemp = str.split("(-|\\/|\\s)");
		ArrayList<String> strParts = arrayToArrayList(strPartsTemp);
		
		if (strParts.size() == 2) {
			strParts.add(String.valueOf(dt.getYear()));
		}
		
		if (strParts.size() != 3) {
			return false;
		}
		
		String day = strParts.get(0);
		String month = strParts.get(1);
		String year = strParts.get(2);
		String integer = "^[0-9]*[1-9][0-9]*$";
		if (! (Pattern.matches(integer, day) && Pattern.matches(integer, month) && Pattern.matches(integer, year))) {
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
		
		if (isInteger(firstChar) && getMonthValue(removeFirstChar) != -1) {
			return true;
		} else if (isInteger(firstTwoChars) && getMonthValue(removeFirstTwoChars) != -1) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isLeapYear(int year) {
		return (year % 400 == 0) || ((year % 4 == 0) && (year % 100 != 0));
	}
	
	private boolean isYtdOrTodayOrTmr(String str) {
		for (int i = 0; i < YTD_OR_TODAY_OR_TMR.length; i++) {
			if (str.toLowerCase().equals(YTD_OR_TODAY_OR_TMR[i])) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isNaturalLanguageDate(String str1, String str2) {
		boolean isLastOrThisOrNext = isInArray(str1, LAST_OR_THIS_OR_NEXT);		
		boolean isDay = isInArray(str2, DAYS);
		return isLastOrThisOrNext && isDay;
	}
	
	private boolean isMonthInEnglishDate(String str1) { // accepts 1jan
		if (isDayMonth(str1)) {
			return isDate(getDayOfDayMonth(str1) + "/" + getMonthOfDayMonth(str1) + "/" + getCorrectYear(str1, String.valueOf(getMonthValue(getMonthOfDayMonth(str1)))));
		} else {
			return false;
		}
	}
	
	private boolean isMonthInEnglishDate1(String str1, String str2) { // accepts 1 jan
		if (isInteger(str1) && getMonthValue(str2) != -1) {
			return isDate(str1 + "/" + getMonthValue(str2) + "/" + getCorrectYear(str1, String.valueOf(getMonthValue(str2))));
		} else {
			return false;
		}
	}
	
	private boolean isMonthInEnglishDate2(String str1, String str2) { // accepts 1jan 2015 
		if (isDayMonth(str1) && isInteger(str2)) {
			return isDate(getDayOfDayMonth(str1) + "/" + getMonthOfDayMonth(str1) + "/" + str2);
		} else {
			return false;
		}
	}

	private boolean isMonthInEnglishDate(String str1, String str2, String str3) { // accepts 1 jan 2015
		if ((isInteger(str1) && getMonthValue(str2) != -1 && isInteger(str3))) {
			return isDate(str1 + "/" + getMonthValue(str2) + "/" + str3);
		} else {
			return false;
		}
	}
		
	private int getDateIndexBetween(ArrayList<String> args, int start, int end) {
		for (int i = start; i < end; i++) {
			if (isDate(args.get(i)) || isYtdOrTodayOrTmr(args.get(i))) {
				return i;
			} else if ((i + 1) < end && isNaturalLanguageDate(args.get(i), args.get(i + 1))) {
				return i;
			} else if ((i + 2) < end && isMonthInEnglishDate(args.get(i), args.get(i + 1), args.get(i + 2))) {
				return i;
			} else if ((i + 1) < end && isMonthInEnglishDate1(args.get(i), args.get(i + 1))) {
				return i;
			} else if ((i + 1) < end && isMonthInEnglishDate2(args.get(i), args.get(i + 1))) {
				return i;
			} else if (isMonthInEnglishDate(args.get(i))) {
				return i;
			} 
		}
		return -1;
	}
	
	private int getTimeIndexBetween(ArrayList<String> args, int start, int end) {
		for (int i = start; i < end; i++) {
			if (isTime(args.get(i))) {
				return i;
			}
		}
		return -1;
	}
	
	// Returns month in integer of month in English
	// Returns -1 if input is not month in English
	private int getMonthValue(String str) {
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

	// Returns day of dayMonth (i.e. 1 in 1jan)
	private String getDayOfDayMonth(String dayMonth) {
		assert(isDayMonth(dayMonth));
		
		String firstChar = dayMonth.substring(0, 1);
		String removeFirstChar = dayMonth.substring(1);
		String firstTwoChars = dayMonth.substring(0, 2);
		if (isInteger(firstChar) && getMonthValue(removeFirstChar) != -1) {
			return firstChar;
		} else {
			return firstTwoChars;
		} 
	}

	// Return month in integer of dayMonth (i.e. jan in 1jan)
	private String getMonthOfDayMonth(String dayMonth) {
		assert(isDayMonth(dayMonth));
		
		String firstChar = dayMonth.substring(0, 1);
		String removeFirstChar = dayMonth.substring(1);
		String removeFirstTwoChars = dayMonth.substring(2);
		if (isInteger(firstChar) && getMonthValue(removeFirstChar) != -1) {
			return String.valueOf(getMonthValue(removeFirstChar));
		} else {
			return String.valueOf(getMonthValue(removeFirstTwoChars));
		} 
	}
		
	private String getActualDate(String str) {
		LocalDateTime dt = LocalDateTime.now();
		
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
			case "tonight" :
				break;
				
			default :
				return str;
		}
		
		return dt.getDayOfMonth() + "-" + dt.getMonthValue() + "-" + dt.getYear();
	}
	
	private String getActualDate(String str1, String str2) {
		LocalDateTime today = LocalDateTime.now();
		LocalDateTime dt = today.with(DayOfWeek.MONDAY);

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
				
			default :
		}
		
		if (str1.equals("last")) {
			dt = dt.minusWeeks(1);
		} else if (str1.equals("next")) {
			dt = dt.plusWeeks(1);
		} else if (str1.equals("this")) {
		} else {
		}

		return dt.getDayOfMonth() + "-" + dt.getMonthValue() + "-" + dt.getYear();
	}

	private String getDate(String date) {
		assert(isDate(date));

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
	
	private String getCorrectYear(String day, String month) {
		LocalDateTime dt = LocalDateTime.now();
		String year;
		if (Integer.parseInt(month) < dt.getMonthValue()) {
			year = String.valueOf(dt.plusYears(1).getYear());
		} else if (Integer.parseInt(month) == dt.getMonthValue() && Integer.parseInt(day) < dt.getDayOfMonth()) {
			year = String.valueOf(dt.plusYears(1).getYear());
		} else {
			year = String.valueOf(dt.getYear());
		}
		return year;
	}

	private String getTime(String time) {
		assert(isTime(time));
		
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
		assert(isTime(time));
		
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
		assert(isTime(time));
		
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
		assert(isTime(time));
		
		if (time.contains("am")) {
			return "am";
		} else if (time.contains("pm")) {
			return "pm";
		} else {
			return "";
		}
	}

	private String getName(ArrayList<String> args, int stopIndex) {
		String output = "";
		for (int i = 0; i < stopIndex; i++) {
			output += args.get(i) + " ";
		}
		return removeSlash(output.trim());
	}
	
	private String getName(ArrayList<String> args, int startIndex, int stopIndex) {
		String output = "";
		for (int i = startIndex; i < stopIndex; i++) {
			output += args.get(i) + " ";
		}
		return removeSlash(output.trim());
	}
	
	private String getNameWithSlash(ArrayList<String> args, int stopIndex) {
		String output = "";
		for (int i = 0; i < stopIndex; i++) {
			output += args.get(i) + " ";
		}
		return output.trim();
	}
	
	private String removeSlash(String str) {
		return str.replace("/", "");
	}

	private int getIndexOf(ArrayList<String> args, String keyword) {
		int index = -1;
		for (int i = 0; i < args.size(); i++) {
			if (args.get(i).toLowerCase().equals(keyword)) {
				index = i;
			}
		}
		return index;
	}
	
	public String stringify(LocalDateTime date) {
		return String.format("%02d", date.getDayOfMonth()) + " " + String.format("%02d", date.getMonthValue()) + " " + date.getYear();
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
	
	private void print(ArrayList<String> args) {
		for (int i = 0; i < args.size(); i++) {
			System.out.println(args.get(i));
		}
		System.out.println();
	}
}
