package parser;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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

import java.time.LocalDateTime;

// @@author A0131188H
public class Parser {
	//private static Logger logger = Logger.getLogger("Logger");
	DateProcessor dateProcessor = new DateProcessor();
	DateTimeChecker dtChecker = new DateTimeChecker();
	IndexParser indexParser;
	NameParser nameParser;
	DateParser dateParser;
	TimeParser timeParser;
	
	private void refreshParsers(ArrayList<String> args) {
		indexParser = new IndexParser(args);
		nameParser = new NameParser(args);
		dateParser = new DateParser(args);
		timeParser = new TimeParser(args);
	}
	
	public AbstractCommand parseInput(String rawInput) {
		String[] argsArr = rawInput.trim().split(Constants.SPLITTER_WHITESPACE);
		ArrayList<String> args = arrayToArrayList(argsArr);
		refreshParsers(args);
		String cmd = args.remove(0);
		
		switch (cmd.toLowerCase()) {
		case Constants.CREATE :
		case Constants.C :
		case Constants.ADD :
		case Constants.A :
			return create(args);
				
		case Constants.DISPLAY :
		case Constants.DP :
			return display(args);
				
		case Constants.DELETE :
		case Constants.DL :
			return delete(args);
				
		case Constants.EDIT :
		case Constants.E :
			return edit(args);
				
		case Constants.SEARCH :
		case Constants.S :
			return search(args);
				
		case Constants.MARK :
		case Constants.M :
			return mark(args, Constants.MARK);
				
		case Constants.UNMARK :
		case Constants.UM :
			return mark(args, Constants.UNMARK);
				
		case Constants.UNDO :
		case Constants.U :
			return undo(args);
			
		case Constants.SAVE :
			return save(args);
				
		case Constants.EXIT :
			return exit(args);
				
		case Constants.DAY :
		case Constants.NIGHT :
		case Constants.HELP :
			return uiOneWord(args);
			
		case Constants.HIDE :
		case Constants.SHOW :
		case Constants.QUIT :
			args.add(0, cmd);
			return uiTwoWords(args);
				
		default :
			return invalidCommand();
		}
	}

	private AbstractCommand create(ArrayList<String> args) {
		if (isAllDay(args)) {
			args = dateProcessor.processAllDay(args);
			refreshParsers(args);
			return createAllDay(args);
		} else if (isBounded(args)) {
			args = dateProcessor.processBounded(args);
			refreshParsers(args);
			return createBounded(args);
		} else if (isDeadline(args)) {
			args = dateProcessor.processDeadline(args);
			refreshParsers(args);
			return createDeadline(args);
		} else if (isFloating(args)) {
			return createFloating(args);
		} else {
			return invalidCommand();
		}
	}

	private AbstractCommand createFloating(ArrayList<String> args) {
		assert(isFloating(args)); // check done by isFloating
		
		String name = nameParser.getName(args.size());
		return new CreateCommand(name);
	}
	
	private AbstractCommand createDeadline(ArrayList<String> args) {
		assert(isDeadline(args)); // check done by isDeadline
		
		int index = indexParser.getIndex(Constants.BY);
		String time = timeParser.getTime(index, args.size());
		String date = dateParser.getDate(index, args.size());
		
		String name = nameParser.getName(index);
		LocalDateTime dateTime = dtFormat(date + Constants.WHITESPACE + time);
		return new CreateCommand(name, dateTime);
	}

	private AbstractCommand createBounded(ArrayList<String> args) {	
		assert(isBounded(args)); // check done by isBounded
		
		int sIndex = indexParser.getIndex(Constants.FROM);
		int eIndex = indexParser.getIndex(Constants.TO);
		String sTime = timeParser.getTime(sIndex, eIndex);
		String sDate = dateParser.getDate(sIndex, eIndex);
		String eTime = timeParser.getTime(eIndex, args.size());
		String eDate = dateParser.getDate(eIndex, args.size());
		
		String name = nameParser.getName(sIndex);
		LocalDateTime sDateTime = dtFormat(sDate + Constants.WHITESPACE + sTime);
		LocalDateTime eDateTime = dtFormat(eDate + Constants.WHITESPACE + eTime);
		return new CreateCommand(name, sDateTime, eDateTime);
	}
	
	private AbstractCommand createAllDay(ArrayList<String> args) {
		assert(isAllDay(args)); // check done by isAllDay
		
		int index = indexParser.getIndex(Constants.ON);
		String date = dateParser.getDate(index, args.size());
		
		String name = nameParser.getName(index);
		LocalDateTime sDateTime = dtFormat(date + Constants.WHITESPACE + Constants.S_DUMMY_TIME);
		LocalDateTime eDateTime = dtFormat(date + Constants.WHITESPACE + Constants.E_DUMMY_TIME);
		return new CreateCommand(name, sDateTime, eDateTime);
	}
	
	private AbstractCommand display(ArrayList<String> args) {
		if (args.size() == 0) {
			return new DisplayCommand(DisplayCommand.Scope.DEFAULT);
		}
		
		String firstWord = args.get(0).toLowerCase();
		boolean oneWord = args.size() == 1;
		boolean isAll = firstWord.equals(Constants.ALL) && oneWord;
		boolean isDone = (firstWord.equals(Constants.DONE) || 
											firstWord.equals(Constants.MARK)) && oneWord;
		boolean isUndone = (firstWord.equals(Constants.UNDONE) || 
												firstWord.equals(Constants.UNMARK)) && oneWord;
		boolean isFloating = firstWord.equals(Constants.FLOATING) && oneWord;
		
		if (isAll) {
			return new DisplayCommand(DisplayCommand.Scope.ALL);
		} else if (isDone) {
			return new DisplayCommand(DisplayCommand.Scope.DONE);
		} else if (isUndone) {
			return new DisplayCommand(DisplayCommand.Scope.UNDONE);
		} else if (isFloating) {
			return new DisplayCommand(DisplayCommand.Scope.FLOATING);
		} else {
			return search(args);
		}
	}
	
	private AbstractCommand search(ArrayList<String> args) {
		if (args.size() == 0) {
			return new DisplayCommand(DisplayCommand.Scope.DEFAULT);
		}
		
		int dateIndex = dateParser.getDateIndex(0, args.size());
		boolean isDate = dateIndex != -1 && dateProcessor.processDate(args, dateIndex).size() == 1;
		
		if (isDate) {
			args = dateProcessor.processDate(args, dateIndex);
			String date = dateParser.getDate(args.get(dateIndex));
			return new DisplayCommand(dtFormat(date + Constants.WHITESPACE + Constants.S_DUMMY_TIME));
		} else {
			for (int i = 0; i < args.size(); i++) {
				args.set(i, nameParser.removeSlash(args.get(i)));
			}
			return new DisplayCommand(args);
		}
	}

	private AbstractCommand delete(ArrayList<String> args) {
		if (args.size() == 0) {
			return invalidCommand();
		}
		
		String firstWord = args.get(0).toLowerCase();
		boolean oneWord = args.size() == 1;
		boolean isAll = firstWord.equals(Constants.ALL) && oneWord;
		boolean isIndex = isInteger(firstWord) && oneWord;
		
		if (isAll) {
			return new DeleteCommand(DeleteCommand.Scope.ALL);
		} else if (isIndex) {
			return new DeleteCommand(Integer.parseInt(firstWord));
		} else {
			return new DeleteCommand(nameParser.getName(args.size()));
		}
	}
	
	private AbstractCommand edit(ArrayList<String> args) {	
		if (args.size() == 0) {
			return invalidCommand();
		}
		
		EditCommand output;
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		
		// pre-process "start to" to "start" and "end to" to "end"
		int sIndex = indexParser.getIndex(Constants.START, Constants.TO);
		if (sIndex != -1) {
			args.remove(sIndex + 1);
		}
		int eIndex = indexParser.getIndex(Constants.END, Constants.TO);
		if (eIndex != -1) {
			args.remove(eIndex + 1);
		}
		int toIndex = indexParser.getIndexOfFirst(Constants.TO);

		// index 0 to index endPointOldName (non-inclusive) 
		// forms old task name
		int endPointOldName;
		if (toIndex != -1) {
			endPointOldName = toIndex;
		} else if (sIndex != -1) {
			endPointOldName = sIndex;
		} else if (eIndex != -1) {
			endPointOldName = eIndex;
		} else {
			endPointOldName = args.size();
		}
		
		// index startPointName to index endPointName (non-inclusive) 
		// forms new task name
		int endPointName;
		if (sIndex != -1) {
			endPointName = sIndex;
		} else if (eIndex != -1) {
			endPointName = eIndex;
		} else {
			endPointName = args.size();
		}
		
		int startPointName;
		if (toIndex == -1) {
			startPointName = endPointName;
		} else {
			startPointName = toIndex + 1;
		}
		
		// index sIndex to index endPointStart (non-inclusive) 
		// forms range for new start datetime
		int endPointStart;
		if (eIndex != -1) {
			endPointStart = eIndex;
		} else {
			endPointStart = args.size();
		}
		
		String search = nameParser.getNameWithSlash(endPointOldName);
		if (isInteger(search)) {
			output = new EditCommand(Integer.parseInt(search));
		} else {
			output = new EditCommand(nameParser.getName(endPointOldName));
		}
		
		String newName = nameParser.getName(startPointName, endPointName);
		if (newName.length() != 0) {
			editType.add(EditCommand.editField.NAME);
			output.setNewName(newName);
		}
		
		if (sIndex != -1) {

			int indexOfsTime = timeParser.getTimeIndex(sIndex, endPointStart);
			if (indexOfsTime != -1) {
				String sTime = timeParser.getTime(args.get(indexOfsTime));	
				editType.add(EditCommand.editField.START_TIME);
				output.setNewStartTime(sTime);
			}
			
			int indexOfsDate = dateParser.getDateIndex(sIndex, endPointStart);
			if (indexOfsDate != -1) {
				args = dateProcessor.processDate(args, indexOfsDate);
				refreshParsers(args);
				String sDate = dateParser.getDate(args.get(indexOfsDate));
				editType.add(EditCommand.editField.START_DATE);
				output.setNewStartDate(sDate);
			}
			
		}
		
		if (eIndex != -1) {
			
			int indexOfeTime = timeParser.getTimeIndex(eIndex, args.size());
			if (indexOfeTime != -1) {
				String eTime = timeParser.getTime(args.get(indexOfeTime));
				editType.add(EditCommand.editField.END_TIME);
				output.setNewEndTime(eTime);
			}
			
			int indexOfeDate = dateParser.getDateIndex(eIndex, args.size());
			if (indexOfeDate != -1) {
				args = dateProcessor.processDate(args, indexOfeDate);
				refreshParsers(args);
				String eDate = dateParser.getDate(args.get(indexOfeDate));
				editType.add(EditCommand.editField.END_DATE);
				output.setNewEndDate(eDate);
			}
			
		}
		
		output.setEditFields(editType);
		return output;
	}
	
	private AbstractCommand mark(ArrayList<String> args, String field) {
		if (args.size() == 0) {
			return invalidCommand();
		}
		
		MarkCommand output;
		
		String firstWord = args.get(0).toLowerCase();
		boolean oneWord = args.size() == 1;
		boolean isIndex = isInteger(firstWord) && oneWord;
		
		if (isIndex) {
			output = new MarkCommand(Integer.parseInt(firstWord));
		} else {
			output = new MarkCommand(nameParser.getName(args.size()));
		}
		
		if (field.equals(Constants.MARK)) {
			output.setMarkField(MarkCommand.markField.MARK);
		} else if (field.equals(Constants.UNMARK)) {
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
		} else {
			String firstWord = args.get(0);
			return new SaveCommand(firstWord);
		}
	}
	
	private AbstractCommand exit(ArrayList<String> args) {
		if (args.size() != 0) {
			return invalidCommand();
		} else {
			return new ExitCommand();
		}
	}
	
	private AbstractCommand uiOneWord(ArrayList<String> args) {
		if (args.size() != 0) {
			return invalidCommand();
		} else {
			return new UICommand();
		}
	}
		
	private AbstractCommand uiTwoWords(ArrayList<String> args) {
		if (args.size() != 2) {
			return invalidCommand();
		}
		
		String firstWord = args.get(0);
		String secondWord = args.get(1);
		boolean isHideOrShowYear = (firstWord.equals(Constants.HIDE) || 
																firstWord.equals(Constants.SHOW)) && 
															 secondWord.equals(Constants.YEAR);
		boolean isQuitHelp = firstWord.equals(Constants.QUIT) && 
				 								 secondWord.equals(Constants.HELP);
		
		if (isHideOrShowYear || isQuitHelp) {
			return new UICommand();
		} else {
			return invalidCommand();
		}
	}
	
	private AbstractCommand invalidCommand() {
		return new InvalidCommand();
	}

	
	
	private boolean isFloating(ArrayList<String> args) {
		return !args.isEmpty();
	}
	
	private boolean isDeadline(ArrayList<String> args) {
		int index = indexParser.getIndex(Constants.BY);
		
		if (index == -1) {
			return false;
		} else {
			String name = nameParser.getName(index);
			int timeIndex = timeParser.getTimeIndex(index, args.size());
			int dateIndex = dateParser.getDateIndex(index, args.size());
			
			if ((name.length() != 0) && (timeIndex != -1) && (dateIndex != -1)) {
				ArrayList<String> argsCopy = dateProcessor.processDeadline(args);
				return argsCopy.size() == index + Constants.NUM_AFTER_BY;
			} else {
				return false;
			}
		}
	}

	private boolean isBounded(ArrayList<String> args) {
		int sIndex = indexParser.getIndex(Constants.FROM);
		int eIndex = indexParser.getIndex(Constants.TO);
		
		if (sIndex == -1 || eIndex == -1) {
			return false;
		} else {
			String name = nameParser.getName(sIndex);
			int sTimeIndex = timeParser.getTimeIndex(sIndex, eIndex);
			int sDateIndex = dateParser.getDateIndex(sIndex, eIndex);
			int eTimeIndex = timeParser.getTimeIndex(eIndex, args.size());
			int eDateIndex = dateParser.getDateIndex(eIndex, args.size());
			
			if ((name.length() != 0) && 
					(sTimeIndex != -1) && (eTimeIndex != -1) && 
					(sDateIndex != -1 || eDateIndex != -1)) {
				ArrayList<String> argsCopy = dateProcessor.processBounded(args);
				IndexParser indexParserCopy = new IndexParser(argsCopy);
				sIndex = indexParserCopy.getIndex(Constants.FROM);
				eIndex = indexParserCopy.getIndex(Constants.TO);
				return argsCopy.size() == eIndex + Constants.NUM_AFTER_TO && 
							 eIndex - sIndex == Constants.NUM_BETWEEN_FROM_TO;
			} else {
				return false;
			}
		}
	}
	
	private boolean isAllDay(ArrayList<String> args) {
		int index = indexParser.getIndex(Constants.ON);
		
		if (index != -1) {
			int dateIndex = dateParser.getDateIndex(index, args.size());
			if (dateIndex != 1) {
				ArrayList<String> argsCopy = dateProcessor.processAllDay(args);
				return argsCopy.size() == index + Constants.NUM_AFTER_ON;
			} else {
				return false;
			}
		} else {
			return false;
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
	
	private LocalDateTime dtFormat(String datetime) {
		return LocalDateTime.parse(datetime, Constants.DTFormatter);
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
