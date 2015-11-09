package parser;

import java.util.ArrayList;

import shared.Constants;

//@@author A0131188H
public class DateProcessor {
	DateTimeChecker dtChecker = new DateTimeChecker();
	
	/**
   * Processes yesterday/today/tomorrow to DD/MM/YYYY
   * Processes [last/this/next] [day] to DD/MM/YYYY
   * Processes month-in-Eng to DD/MM/YYYY
   */

	protected ArrayList<String> processDeadline(ArrayList<String> args) {
		IndexParser indexParser = new IndexParser(args);
		TimeParser timeParser = new TimeParser(args);
		DateParser dateParser = new DateParser(args);
		
		int index = indexParser.getIndex(Constants.KEYWORD_BY);
		
		assert(index != -1); // check done by isDeadline
		
		int timeIndex = timeParser.getTimeIndex(index, args.size());
		int dateIndex = dateParser.getDateIndex(index, args.size());
		
		assert(timeIndex != -1); // check done by isDeadline
		assert(dateIndex != -1); // check done by isDeadline
		
		args = processDate(args, dateIndex);

		assert(dtChecker.isDate(args.get(dateIndex))); // done by processDeadline
		
		return args;
	}

	protected ArrayList<String> processBounded(ArrayList<String> args) {
		IndexParser indexParser = new IndexParser(args);
		TimeParser timeParser = new TimeParser(args);
		DateParser dateParser = new DateParser(args);
		
		int sIndex = indexParser.getIndex(Constants.KEYWORD_FROM);
		int eIndex = indexParser.getIndex(Constants.KEYWORD_TO);
		
		assert(sIndex != -1); // check done by isBounded
		assert(eIndex != -1); // check done by isBounded
		
		int sTimeIndex = timeParser.getTimeIndex(sIndex, eIndex);
		int sDateIndex = dateParser.getDateIndex(sIndex, eIndex);
		int eTimeIndex = timeParser.getTimeIndex(eIndex, args.size());
		int eDateIndex = dateParser.getDateIndex(eIndex, args.size());
		
		assert(sTimeIndex != -1); // check done by isBounded
		assert(eTimeIndex != -1); // check done by isBounded
		assert(sDateIndex != -1 || eDateIndex != -1); // check done by isBounded

		// case 1: one date entered for start date and end date,
		// 				 the date is between "from" and "to"
		// case 2: one date entered for start date and end date,
		// 				 the date is after "to"
		// case 3: one date entered for start date and
		//				 one date entered for end date
		if (sDateIndex != -1 && eDateIndex == -1) { // case 1
			args = processDate(args, sDateIndex);
			args.add(eIndex + 1, args.get(sDateIndex));
			
			indexParser = refreshIndexParser(args);
			dateParser = refreshDateParser(args);
			timeParser = refreshTimeParser(args);
			
			eTimeIndex = timeParser.getTimeIndex(eIndex, args.size());
			eDateIndex = dateParser.getDateIndex(eIndex, args.size());
			
		} else if (sDateIndex == -1 && eDateIndex != -1) { // case 2
			args = processDate(args, eDateIndex);
			args.add(sIndex + 1, args.get(eDateIndex));

			indexParser = refreshIndexParser(args);
			dateParser = refreshDateParser(args);
			timeParser = refreshTimeParser(args);
			
			eIndex = indexParser.getIndex(Constants.KEYWORD_TO);
			sTimeIndex = timeParser.getTimeIndex(sIndex, eIndex);
			sDateIndex = dateParser.getDateIndex(sIndex, eIndex);
			eTimeIndex = timeParser.getTimeIndex(eIndex, args.size());
			eDateIndex = dateParser.getDateIndex(eIndex, args.size());
		
		} else { // case 3
			args = processDate(args, sDateIndex);
			
			indexParser = refreshIndexParser(args);
			dateParser = refreshDateParser(args);
			timeParser = refreshTimeParser(args);
			
			eTimeIndex = timeParser.getTimeIndex(eIndex, args.size());
			eDateIndex = dateParser.getDateIndex(eIndex, args.size());
			
			args = processDate(args, eDateIndex);
		}
		
		assert(dtChecker.isDate(args.get(sDateIndex))); // done by processBounded
		assert(dtChecker.isDate(args.get(eDateIndex))); // done by processBounded
		
		return args;
	}
	
	protected ArrayList<String> processAllDay(ArrayList<String> args) {
		IndexParser indexParser = new IndexParser(args);
		DateParser dateParser = new DateParser(args);
		
		int index = indexParser.getIndex(Constants.KEYWORD_ON);
		
		assert(index != -1); // check done by isAllDay
		
		int dateIndex = dateParser.getDateIndex(index, args.size());
		
		assert(dateIndex != -1);
		
		args = processDate(args, dateIndex);
		
		assert(dtChecker.isDate(args.get(dateIndex))); // done by processAllDay
		
		return args;
	}
	
	protected ArrayList<String> processDate(ArrayList<String> args, int dateIndex) {
		assert(dateIndex != -1); // check done by processDeadline or processBounded
		
		DateParser dateParser = new DateParser(args);
		
		ArrayList<String> pArgs = new ArrayList<String>(args);
		
		String datePart1 = pArgs.get(dateIndex);
		String datePart2 = Constants.EMPTY;
		String datePart3 = Constants.EMPTY;
		if (dateIndex + 1 < pArgs.size()) {
			datePart2 = pArgs.get(dateIndex + 1);
		}
		if (dateIndex + 2 < pArgs.size()) {
			datePart3 = pArgs.get(dateIndex + 2);
		}
		
		if (dtChecker.isDate(datePart1)) {
			
		} else if (dtChecker.isYtdOrTodayOrTmr(datePart1)) {
			pArgs.set(dateIndex, dateParser.getActualDate(datePart1));	
			
		} else if (dtChecker.isNaturalLanguageDate(datePart1, datePart2)) {
			pArgs.set(dateIndex, dateParser.getActualDate(datePart1, datePart2));
			pArgs.remove(dateIndex + 1);
				
		} else if (dtChecker.isMonthInEngDate(datePart1, datePart2, datePart3)) {
			String day = datePart1;
			String month = dtChecker.getMonthStr(datePart2);
			String year = datePart3;
			String date = day + Constants.SLASH + month + Constants.SLASH + year;
			pArgs.set(dateIndex, dateParser.getDate(date));
			pArgs.remove(dateIndex + 2);
			pArgs.remove(dateIndex + 1);
		
		} else if (dtChecker.isMonthInEngDate1(datePart1, datePart2)) {
			String day = datePart1;
			String month = dtChecker.getMonthStr(datePart2);
			String year = dateParser.getCorrectYear(day, month);
			String date = day + Constants.SLASH + month + Constants.SLASH + year;
			pArgs.set(dateIndex, dateParser.getDate(date));
			pArgs.remove(dateIndex + 1);
				
		} else if (dtChecker.isMonthInEngDate2(datePart1, datePart2)) {
			String dayMonth = datePart1;
			String day = dtChecker.getDayOfDayMonth(dayMonth);
			String month = dtChecker.getMonthOfDayMonth(dayMonth);
			String year = datePart2;
			String date = day + Constants.SLASH + month + Constants.SLASH + year;
			pArgs.set(dateIndex, dateParser.getDate(date));
			pArgs.remove(dateIndex + 1);
				
		} else if (dtChecker.isMonthInEngDate(datePart1)) {
			String dayMonth = datePart1;
			String day = dtChecker.getDayOfDayMonth(dayMonth);
			String month = dtChecker.getMonthOfDayMonth(dayMonth);
			String year = dateParser.getCorrectYear(day, month);
			String date = day + Constants.SLASH + month + Constants.SLASH + year;
			pArgs.set(dateIndex, dateParser.getDate(date));		
		}
		
		return pArgs;
	}
	
	
	
  /**
   * Helper methods
   */
	private DateParser refreshDateParser(ArrayList<String> args) {
		return new DateParser(args);
	}
	
	private TimeParser refreshTimeParser(ArrayList<String> args) {
		return new TimeParser(args);
	}
	
	private IndexParser refreshIndexParser(ArrayList<String> args) {
		return new IndexParser(args);
	}
	
}
