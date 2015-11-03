package parser;

import java.util.ArrayList;
import java.util.regex.Pattern;
import shared.Constants;
import shared.command.AbstractCommand;
import shared.command.CreateCommand;
import shared.command.DeleteCommand;
import shared.command.DisplayCommand;
import shared.command.EditCommand;
import shared.command.UICommand;
import shared.command.ExitCommand;
import shared.command.InvalidCommand;
import shared.command.MarkCommand;
import shared.command.SaveCommand;
import shared.command.UndoCommand;
import java.time.DayOfWeek;
import java.time.LocalDateTime;

public class Parser {
	
	public AbstractCommand parseInput(String rawInput) {
		rawInput = rawInput.trim();
		ArrayList<String> args = arrayToArrayList(rawInput.split(" "));
		String cmd = args.remove(0);
		
		switch (cmd.toLowerCase()) {
		case "create" :
		case "c" :
		case "add" :
		case "a" :
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
			return undo(args);
			
		case "save" :
			return save(args);
				
		case "exit" :
			return exit(args);
				
		case "day" :
		case "night" :
		case "hide" :
		case "show" :
		case "help" :
		case "quit" :
			args.add(0, cmd);
			return ui(args);
				
		default :
			return invalidCommand();
		}
	}

	private AbstractCommand create(ArrayList<String> args) {		
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
		assert(isFloating(args)); // check done by isFloating
		
		String name = getName(args, args.size());
		return new CreateCommand(name);
	}
	
	private AbstractCommand createDeadline(ArrayList<String> args) {
		assert(isDeadline(args)); // check done by isDeadline
		
		int index = getIndex(args, Constants.BY);
		String time = getTime(args.get(getTimeIndex(args, index, args.size())));
		String date = getDate(args.get(getDateIndex(args, index, args.size())));
		
		String name = getName(args, index);
		LocalDateTime dateTime = LocalDateTime.parse(date + " " + time, Constants.DTFormatter);
		return new CreateCommand(name, dateTime);
	}

	private AbstractCommand createBounded(ArrayList<String> args) {	
		assert(isBounded(args)); // check done by isBounded
		
		int sIndex = getIndex(args, Constants.FROM);
		int eIndex = getIndex(args, Constants.TO);
		String sTime = getTime(args.get(getTimeIndex(args, sIndex, eIndex)));
		String sDate = getDate(args.get(getDateIndex(args, sIndex, eIndex)));
		String eTime = getTime(args.get(getTimeIndex(args, eIndex, args.size())));
		String eDate = getDate(args.get(getDateIndex(args, eIndex, args.size())));
		
		String name = getName(args, sIndex);
		LocalDateTime sDateTime = LocalDateTime.parse(sDate + " " + sTime, Constants.DTFormatter);
		LocalDateTime eDateTime = LocalDateTime.parse(eDate + " " + eTime, Constants.DTFormatter);
		return new CreateCommand(name, sDateTime, eDateTime);
	}
	
	private AbstractCommand display(ArrayList<String> args) {
		if (args.size() == 0) {
			return new DisplayCommand(DisplayCommand.Scope.DEFAULT);
		}
		
		String firstWord = args.get(0).toLowerCase();
		if (firstWord.equals(Constants.ALL) && args.size() == 1) {
			return new DisplayCommand(DisplayCommand.Scope.ALL);
		} else if ((firstWord.equals(Constants.DONE) || firstWord.equals(Constants.MARK)) && args.size() == 1) {
			return new DisplayCommand(DisplayCommand.Scope.DONE);
		} else if ((firstWord.equals(Constants.UNDONE) || firstWord.equals(Constants.UNMARK)) && args.size() == 1) {
			return new DisplayCommand(DisplayCommand.Scope.UNDONE);
		} else if (firstWord.equals(Constants.FLOATING) && args.size() == 1) {
			return new DisplayCommand(DisplayCommand.Scope.FLOATING);
		} else if (firstWord.equals(Constants.OVERDUE) && args.size() == 1) {
			return new DisplayCommand(DisplayCommand.Scope.OVERDUE);
		} else if (firstWord.equals(Constants.WEEK) && args.size() == 1) {
			return new DisplayCommand(LocalDateTime.parse(stringify(LocalDateTime.now()) + " " + Constants.dummyTime, Constants.DTFormatter), 
																LocalDateTime.parse(stringify(LocalDateTime.now().plusWeeks(1)) + " " + Constants.dummyTime, Constants.DTFormatter));
		} else {
			return search(args);
		}
	}
	
	private AbstractCommand search(ArrayList<String> args) {
		if (args.size() == 0) {
			return new DisplayCommand(DisplayCommand.Scope.UNDONE);
		}
		
		int fromIndex = getIndex(args, Constants.FROM);
		int toIndex = getIndex(args, Constants.TO);
		int dateIndex = getDateIndex(args, 0, args.size());
		
		if (fromIndex != -1 && toIndex != -1) {
			int startDateIndex = getDateIndex(args, fromIndex, toIndex);
			int endDateIndex = getDateIndex(args, toIndex, args.size());
			if (startDateIndex != -1 && endDateIndex != -1) {
				args = processDate(args, startDateIndex);
				endDateIndex = getDateIndex(args, toIndex, args.size());
				args = processDate(args, endDateIndex);
				return new DisplayCommand(LocalDateTime.parse(getDate(args.get(startDateIndex)) + " " + Constants.dummyTime, Constants.DTFormatter), 
																	LocalDateTime.parse(getDate(args.get(endDateIndex)) + " " + Constants.dummyTime, Constants.DTFormatter));
			}
			
		} else if (fromIndex != -1 && toIndex == -1) {
			int startDateIndex = getDateIndex(args, fromIndex, args.size());
			if (startDateIndex != -1 && processDate(args, startDateIndex).size() == 2) {
				args = processDate(args, startDateIndex);
				return new DisplayCommand(LocalDateTime.parse(getDate(args.get(startDateIndex)) + " " + Constants.dummyTime, Constants.DTFormatter), 
																	DisplayCommand.Type.SEARCHDATEONWARDS);
			}
			
		} else if (dateIndex != -1 && fromIndex == -1 && toIndex == -1) {
			if (processDate(args, dateIndex).size() == 1) {
				args = processDate(args, dateIndex);
				return new DisplayCommand(LocalDateTime.parse(getDate(args.get(dateIndex)) + " " + Constants.dummyTime, Constants.DTFormatter), 
																	DisplayCommand.Type.SEARCHDATE);
			}
		}
		
		return new DisplayCommand(getName(args, args.size()));
	}

	private AbstractCommand delete(ArrayList<String> args) {
		if (args.size() == 0) {
			return invalidCommand();
		}
		
		String firstWord = args.get(0).toLowerCase();
		if (firstWord.equals(Constants.ALL) && args.size() == 1) {
			return new DeleteCommand(DeleteCommand.Scope.ALL);
		} else if (firstWord.equals(Constants.DONE) && args.size() == 1) {
			return new DeleteCommand(DeleteCommand.Scope.DONE);
		} else if (firstWord.equals(Constants.UNDONE) && args.size() == 1) {
			return new DeleteCommand(DeleteCommand.Scope.UNDONE);
		} else if (isInteger(firstWord) && args.size() == 1) {
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
		
		int start = getIndexOf(args, Constants.START, Constants.TO);
		if (start != -1) {
			args.remove(start + 1);
		}
		int end = getIndexOf(args, Constants.END, Constants.TO);
		if (end != -1) {
			args.remove(end + 1);
		}
		int index = getIndexOfFirst(args, Constants.TO);

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

			int indexOfsTime = getTimeIndex(args, start, endPointStart);
			if (indexOfsTime != -1) {
				String stime = getTime(args.get(indexOfsTime));	
				editType.add(EditCommand.editField.START_TIME);
				output.setNewStartTime(stime);
			}
			
			int indexOfsDate = getDateIndex(args, start, endPointStart);
			if (indexOfsDate != -1) {
				args = processDate(args, indexOfsDate);
				String sdate = getDate(args.get(indexOfsDate));
				editType.add(EditCommand.editField.START_DATE);
				output.setNewStartDate(sdate);
			}
			
		}
		
		if (end != -1) {
			
			int indexOfeTime = getTimeIndex(args, end, args.size());
			if (indexOfeTime != -1) {
				String etime = getTime(args.get(indexOfeTime));
				editType.add(EditCommand.editField.END_TIME);
				output.setNewEndTime(etime);
			}
			
			int indexOfeDate = getDateIndex(args, end, args.size());
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
		if (isInteger(firstWord) && args.size() == 1) {
			output = new MarkCommand(Integer.parseInt(firstWord));
		} else {
			output = new MarkCommand(getName(args, args.size()));
		}
		
		if (str.equals(Constants.MARK)) {
			output.setMarkField(MarkCommand.markField.MARK);
		} else if (str.equals(Constants.UNMARK)) {
			output.setMarkField(MarkCommand.markField.UNMARK);
		} else {
			return invalidCommand();
		}
		
		return output;
	}
	
	private AbstractCommand undo(ArrayList<String> args) {
		if (args.size() == 0) {
			return new UndoCommand();
		} else {
			return invalidCommand();
		}
	}

	private AbstractCommand save(ArrayList<String> args) {
		if (args.size() != 1) {
			return invalidCommand();
		}
		
		return new SaveCommand(args.get(0));
	}
	
	private AbstractCommand exit(ArrayList<String> args) {
		if (args.size() != 0) {
			return invalidCommand();
		} else {
			return new ExitCommand();
		}
	}
	
	private AbstractCommand ui(ArrayList<String> args) {
		if (args.size() == 1) {
			String firstWord = args.get(0);
			if (firstWord.equals(Constants.DAY) || firstWord.equals(Constants.NIGHT) || firstWord.equals(Constants.HELP)) {
				return new UICommand();
			} else {
				return invalidCommand();
			}
			
		} else if (args.size() == 2) {
			String firstWord = args.get(0);
			String secondWord = args.get(1);
			if ((firstWord.equals(Constants.SHOW) || firstWord.equals(Constants.HIDE)) && secondWord.equals(Constants.YEAR)) {
				return new UICommand();
			} else if (firstWord.equals(Constants.QUIT) && secondWord.equals(Constants.HELP)) {
				return new UICommand();
			} else {
				return invalidCommand();
			}
			
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
		int index = getIndex(args, Constants.BY);
		
		assert(index != -1); // check done by isDeadline
		
		int timeIndex = getTimeIndex(args, index, args.size());
		int dateIndex = getDateIndex(args, index, args.size());
		
		assert(timeIndex != -1); // check done by isDeadline
		assert(dateIndex != -1); // check done by isDeadline
		
		args = processDate(args, dateIndex);
		
		assert(isDate(args.get(dateIndex))); // done by processDeadline
		
		return args;
	}
	
	// process yesterday/today/tomorrow to dd-mm-yyyy
	// process last/this/next + day to dd-mm-yyyy
	// process day + month-in-English + year to dd-mm-yyyy
	// process day + month-in-English to dd-mm-yyyy
	private ArrayList<String> processBounded(ArrayList<String> args) {
		int sIndex = getIndex(args, Constants.FROM);
		int eIndex = getIndex(args, Constants.TO);
		
		assert(sIndex != -1); // check done by isBounded
		assert(eIndex != -1); // check done by isBounded
		
		int sTimeIndex = getTimeIndex(args, sIndex, eIndex);
		int sDateIndex = getDateIndex(args, sIndex, eIndex);
		int eTimeIndex = getTimeIndex(args, eIndex, args.size());
		int eDateIndex = getDateIndex(args, eIndex, args.size());
		
		assert(sTimeIndex != -1); // check done by isBounded
		assert(eTimeIndex != -1); // check done by isBounded
		assert(sDateIndex != -1 || eDateIndex != -1); // check done by isBounded

		// case 1: one date entered for both start date and end date,
		// 				 the date is between "from" and "to"
		// case 2: one date entered for both start date and end date,
		// 				 the date is after "to"
		// case 3: one date entered for start date and
		//				 one date entered for end date
		if (sDateIndex != -1 && eDateIndex == -1) {
			args = processDate(args, sDateIndex);
			args.add(eIndex + 1, args.get(sDateIndex));
			
			eIndex = getIndex(args, Constants.TO);
			eTimeIndex = getTimeIndex(args, eIndex, args.size());
			eDateIndex = getDateIndex(args, eIndex, args.size());
			
		} else if (sDateIndex == -1 && eDateIndex != -1) {
			args = processDate(args, eDateIndex);
			args.add(sIndex + 1, args.get(eDateIndex));
			
			sDateIndex = getDateIndex(args, sIndex, eIndex);
			eTimeIndex = getTimeIndex(args, eIndex, args.size());
			eDateIndex = getDateIndex(args, eIndex, args.size());
			
		} else {
			args = processDate(args, sDateIndex);
			eTimeIndex = getTimeIndex(args, eIndex, args.size());
			eDateIndex = getDateIndex(args, eIndex, args.size());
			args = processDate(args, eDateIndex);
		}
		
		assert(isDate(args.get(sDateIndex))); // done by processBounded
		assert(isDate(args.get(eDateIndex))); // done by processBounded
		
		return args;
	}
	
	private ArrayList<String> processDate(ArrayList<String> args, int dateIndex) {
		assert(dateIndex != -1); // check done by processDeadline or processBounded
		
		ArrayList<String> pArgs = new ArrayList<String>(args);
		
		String datePart1 = pArgs.get(dateIndex);
		String datePart2 = "";
		String datePart3 = "";
		if (dateIndex + 1 < pArgs.size()) {
			datePart2 = pArgs.get(dateIndex + 1);
		}
		if (dateIndex + 2 < pArgs.size()) {
			datePart3 = pArgs.get(dateIndex + 2);
		}
		
		if (isDate(datePart1)) {
			
		} else if (isYtdOrTodayOrTmr(datePart1)) {
			pArgs.set(dateIndex, getRealDate(datePart1));	
			
		} else if (isNaturalLanguageDate(datePart1, datePart2)) {
			pArgs.set(dateIndex, getRealDate(datePart1, datePart2));
			pArgs.remove(dateIndex + 1);
				
		} else if (isMonthInEngDate(datePart1, datePart2, datePart3)) {
			String day = datePart1;
			String month = getMonthStr(datePart2);
			String year = datePart3;
			pArgs.set(dateIndex, getDate(day + "/" + month + "/" + year));
			pArgs.remove(dateIndex + 2);
			pArgs.remove(dateIndex + 1);
		
		} else if (isMonthInEngDate1(datePart1, datePart2)) {
			String day = datePart1;
			String month = getMonthStr(datePart2);
			String year = getCorrectYear(day, month);
			pArgs.set(dateIndex, getDate(day + "/" + month + "/" + year));
			pArgs.remove(dateIndex + 1);
				
		} else if (isMonthInEngDate2(datePart1, datePart2)) {
			String dayMonth = datePart1;
			String day = getDayOfDayMonth(dayMonth);
			String month = getMonthOfDayMonth(dayMonth);
			String year = datePart2;
			pArgs.set(dateIndex, getDate(day + "/" + month + "/" + year));
			pArgs.remove(dateIndex + 1);
				
		} else if (isMonthInEngDate(datePart1)) {
			String dayMonth = datePart1;
			String day = getDayOfDayMonth(dayMonth);
			String month = getMonthOfDayMonth(dayMonth);
			String year = getCorrectYear(day, month);
			pArgs.set(dateIndex, getDate(day + "/" + month + "/" + year));		
		}
		
		return pArgs;
	}
	
	private boolean isFloating(ArrayList<String> args) {
		return !args.isEmpty();
	}
	
	private boolean isDeadline(ArrayList<String> args) {
		int index = getIndex(args, Constants.BY);
		
		if (index == -1) {
			return false;
		} else {
			String name = getName(args, index);
			int timeIndex = getTimeIndex(args, index, args.size());
			int dateIndex = getDateIndex(args, index, args.size());
			return (name.length() != 0) && 
						 (timeIndex != -1) && 
						 (dateIndex != -1);
		}
	}

	private boolean isBounded(ArrayList<String> args) {
		int sIndex = getIndex(args, Constants.FROM);
		int eIndex = getIndex(args, Constants.TO);
		
		if (sIndex == -1 || eIndex == -1) {
			return false;
		} else {
			String name = getName(args, sIndex);
			int sTimeIndex = getTimeIndex(args, sIndex, eIndex);
			int sDateIndex = getDateIndex(args, sIndex, eIndex);
			int eTimeIndex = getTimeIndex(args, eIndex, args.size());
			int eDateIndex = getDateIndex(args, eIndex, args.size());
			return (name.length() != 0) && 
						 (sTimeIndex != -1) && 
						 (eTimeIndex != -1) && 
						 (sDateIndex != -1 || eDateIndex != -1);
		}
	}
	
	private boolean isInteger(String str) {
    try {
      Integer.parseInt(str);
      return true;
	  } catch(NumberFormatException e) {
	      return false;
	  }
	}
	
	// Accepts 24-hour format: 8:00, 08:00, 20:00,
	//												 8.00, 08.00, 20.00
	// Accepts 12-hour format: 1:00am, 1:00AM, 1:00pm, 1:00PM, 
	//												 1.00am, 1.00AM, 1.00pm, 1.00PM
	// 												 1am, 1AM, 1pm, 1PM
	public boolean isTime(String str) {
		String tf12first = "(1[012]|[1-9]|0[1-9])(:|.)[0-5][0-9](?i)(am|pm)";
		String tf12second = "(1[012]|[1-9])(?i)(am|pm)";		
		
		if (Pattern.matches(tf12first, str) || Pattern.matches(tf12second, str)) {
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
		String[] strPartsTemp = str.split("(-|\\/|\\s)");
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
	
	private boolean isYtdOrTodayOrTmr(String str) {
		String[] ytdOrTodayOrTmr = { "yesterday", "ytd", "today", "tonight", "tomorrow", "tmr" };
		return isInArray(str, ytdOrTodayOrTmr);
	}
	
	private boolean isNaturalLanguageDate(String str1, String str2) {
		String[] lastOrThisOrNext = { "last", "this", "next" };
		String[] days = { "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday",
											"mon", "tues", "wed", "thurs", "fri", "sat", "sun" };
		boolean isLastOrThisOrNext = isInArray(str1, lastOrThisOrNext);		
		boolean isDay = isInArray(str2, days);
		return isLastOrThisOrNext && isDay;
	}
	
	private boolean isMonthInEngDate(String str1) { // accepts 1jan
		if (isDayMonth(str1)) {
			return isDate(getDayOfDayMonth(str1) + "/" + 
						 				getMonthOfDayMonth(str1) + "/" + 
						 				getCorrectYear(getDayOfDayMonth(str1), getMonthOfDayMonth(str1)));
		} else {
			return false;
		}
	}
	
	private boolean isMonthInEngDate1(String str1, String str2) { // accepts 1 jan
		if (isInteger(str1) && getMonthInt(str2) != -1) {
			return isDate(str1 + "/" + 
										getMonthStr(str2) + "/" + 
										getCorrectYear(str1, getMonthStr(str2)));
		} else {
			return false;
		}
	}
	
	private boolean isMonthInEngDate2(String str1, String str2) { // accepts 1jan 2015 
		if (isDayMonth(str1) && isInteger(str2)) {
			return isDate(getDayOfDayMonth(str1) + "/" + 
									  getMonthOfDayMonth(str1) + "/" + 
									  str2);
		} else {
			return false;
		}
	}

	private boolean isMonthInEngDate(String str1, String str2, String str3) { // accepts 1 jan 2015
		if ((isInteger(str1) && getMonthInt(str2) != -1 && isInteger(str3))) {
			return isDate(str1 + "/" + 
										getMonthStr(str2) + "/" + 
										str3);
		} else {
			return false;
		}
	}
	
	private int getTimeIndex(ArrayList<String> args, int start, int end) {
		for (int i = start; i < end; i++) {
			if (isTime(args.get(i))) {
				return i;
			}
		}
		return -1;
	}
	
	private int getDateIndex(ArrayList<String> args, int start, int end) {
		for (int i = start; i < end; i++) {
			if (isDate(args.get(i)) || isYtdOrTodayOrTmr(args.get(i))) {
				return i;
			} else if ((i + 1) < end && isNaturalLanguageDate(args.get(i), args.get(i + 1))) {
				return i;
			} else if ((i + 2) < end && isMonthInEngDate(args.get(i), args.get(i + 1), args.get(i + 2))) {
				return i;
			} else if ((i + 1) < end && isMonthInEngDate1(args.get(i), args.get(i + 1))) {
				return i;
			} else if ((i + 1) < end && isMonthInEngDate2(args.get(i), args.get(i + 1))) {
				return i;
			} else if (isMonthInEngDate(args.get(i))) {
				return i;
			} 
		}
		return -1;
	}
	
	// Accepts jan and returns 1
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

	// Accepts jan and returns 1 (str)
	// Accepts jan and returns "1"
	private String getMonthStr(String str) {
		return String.valueOf(getMonthInt(str));
	}
	
	// Accepts 2jan and returns "2"
	
	// Accepts 2jan and returns 2 (str)
	private String getDayOfDayMonth(String dayMonth) {
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

	// Accepts 2jan and returns 1 (str)
	
	// Accepts 2jan and returns "1"
	private String getMonthOfDayMonth(String dayMonth) {
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
		
	
	private String getRealDate(String str) {
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
			return str;
		}
		
		return now.getDayOfMonth() + "/" + now.getMonthValue() + "/" + now.getYear();
	}
	
	
	private String getRealDate(String str1, String str2) {
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
		
		if (str1.equals("last")) {
			date = date.minusWeeks(1);
		} else if (str1.equals("next")) {
			date = date.plusWeeks(1);
		} else if (str1.equals("this")) {
		} else {
		}

		return date.getDayOfMonth() + "/" + date.getMonthValue() + "/" + date.getYear();
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
		LocalDateTime now = LocalDateTime.now();
		String year;
		if (Integer.parseInt(month) < now.getMonthValue()) {
			year = String.valueOf(now.plusYears(1).getYear());
		} else if (Integer.parseInt(month) == now.getMonthValue() && Integer.parseInt(day) < now.getDayOfMonth()) {
			year = String.valueOf(now.plusYears(1).getYear());
		} else {
			year = String.valueOf(now.getYear());
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
		} else if (time.contains(".")) {
			String[] timeParts = time.split("\\.");
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
		} else if (time.contains(".")) {
			String[] timeParts = time.split("\\.");
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

	
	private int getIndexOfFirst(ArrayList<String> args, String keyword) {
		int index = -1;
		for (int i = 0; i < args.size(); i++) {
			if (args.get(i).toLowerCase().equals(keyword)) {
				index = i;
				break;
			}
		}
		return index;
	}
	
	
	private int getIndex(ArrayList<String> args, String keyword) {
		int index = -1;
		for (int i = 0; i < args.size(); i++) {
			if (args.get(i).toLowerCase().equals(keyword)) {
				index = i;
			}
		}
		return index;
	}
	
	
	private int getIndexOf(ArrayList<String> args, String keyword1, String keyword2) {
		int index = -1;
		for (int i = 0; i < args.size(); i++) {
			if (i + 1 < args.size() && 
					args.get(i).toLowerCase().equals(keyword1) && 
					args.get(i + 1).toLowerCase().equals(keyword2)) {
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
		}	System.out.println();
	}
}
