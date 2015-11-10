package parser;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import shared.Constants;
import shared.SharedLogger;
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
import java.time.format.DateTimeParseException;

// @@author A0131188H
public class Parser {
	private Logger logger = SharedLogger.getInstance().getLogger();
	private DateProcessor dateProcessor = new DateProcessor();
	private IndexParser indexParser;
	private NameParser nameParser;
	private DateParser dateParser;
	private TimeParser timeParser;

	private void refreshParsers(ArrayList<String> args) {
		indexParser = new IndexParser(args);
		nameParser = new NameParser(args);
		dateParser = new DateParser(args);
		timeParser = new TimeParser(args);
	}

	/**
	 * Takes in raw user input string Gets cmd (first word of string) Gets args
	 * (rest of string) Calls command handler function of cmd with args Returns
	 * AbstractCommand
	 */
	public AbstractCommand parseInput(String rawInput) {
		String[] argsArr = rawInput.split(Constants.SPLITTER_WHITESPACE);
		ArrayList<String> args = arrayToArrayList(argsArr);
		refreshParsers(args);

		try {
			String cmd = args.remove(0);
			switch (cmd.toLowerCase()) {
			case Constants.CMD_CREATE:
			case Constants.CMD_C:
			case Constants.CMD_ADD:
			case Constants.CMD_A:
				return create(args);

			case Constants.CMD_DISPLAY:
			case Constants.CMD_DP:
				return display(args);

			case Constants.CMD_DELETE:
			case Constants.CMD_DL:
				return delete(args);

			case Constants.CMD_EDIT:
			case Constants.CMD_E:
				return edit(args);

			case Constants.CMD_SEARCH:
			case Constants.CMD_S:
				return search(args);

			case Constants.CMD_MARK:
			case Constants.CMD_M:
				return mark(args, Constants.CMD_MARK);

			case Constants.CMD_UNMARK:
			case Constants.CMD_UM:
				return mark(args, Constants.CMD_UNMARK);

			case Constants.CMD_UNDO:
			case Constants.CMD_U:
				return undo(args);

			case Constants.CMD_SAVE:
				return save(args);

			case Constants.CMD_EXIT:
				return exit(args);

			case Constants.CMD_DAY:
			case Constants.CMD_NIGHT:
			case Constants.CMD_HELP:
				return uiOneWord(args);

			case Constants.CMD_HIDE:
			case Constants.CMD_SHOW:
			case Constants.CMD_QUIT:
				args.add(0, cmd);
				return uiTwoWords(args);

			default:
				return invalidCommand();
			}
		} catch (DateTimeParseException e) {
			return invalidCommand();
		}
	}

	private AbstractCommand create(ArrayList<String> args)
			throws DateTimeParseException {
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
		assert (isFloating(args)); // check done by isFloating

		logger.log(Level.INFO, "Creating CreateCommand for floating task");

		String name = nameParser.getName(args.size());
		return new CreateCommand(name);
	}

	private AbstractCommand createDeadline(ArrayList<String> args)
			throws DateTimeParseException {
		assert (isDeadline(args)); // check done by isDeadline

		int index = indexParser.getIndex(Constants.KEYWORD_BY);
		String time = timeParser.getTime(index, args.size());
		String date = dateParser.getDate(index, args.size());
		String dateTimeStr = date + Constants.WHITESPACE + time;

		logger.log(Level.INFO, "Creating CreateCommand for deadline task");

		String name = nameParser.getName(index);
		LocalDateTime dateTime = getDateTime(dateTimeStr);
		return new CreateCommand(name, dateTime);
	}

	private AbstractCommand createBounded(ArrayList<String> args)
			throws DateTimeParseException {
		assert (isBounded(args)); // check done by isBounded

		int sIndex = indexParser.getIndex(Constants.KEYWORD_FROM);
		int eIndex = indexParser.getIndex(Constants.KEYWORD_TO);
		String sTime = timeParser.getTime(sIndex, eIndex);
		String sDate = dateParser.getDate(sIndex, eIndex);
		String eTime = timeParser.getTime(eIndex, args.size());
		String eDate = dateParser.getDate(eIndex, args.size());
		String sDateTimeStr = sDate + Constants.WHITESPACE + sTime;
		String eDateTimeStr = eDate + Constants.WHITESPACE + eTime;

		logger.log(Level.INFO, "Creating CreateCommand for bounded task");

		String name = nameParser.getName(sIndex);
		LocalDateTime sDateTime = getDateTime(sDateTimeStr);
		LocalDateTime eDateTime = getDateTime(eDateTimeStr);
		return new CreateCommand(name, sDateTime, eDateTime);
	}

	private AbstractCommand createAllDay(ArrayList<String> args)
			throws DateTimeParseException {
		assert (isAllDay(args)); // check done by isAllDay

		int index = indexParser.getIndex(Constants.KEYWORD_ON);
		String date = dateParser.getDate(index, args.size());
		String sDateTimeStr = date + Constants.WHITESPACE
				+ Constants.DUMMY_TIME_S;
		String eDateTimeStr = date + Constants.WHITESPACE
				+ Constants.DUMMY_TIME_E;

		logger.log(Level.INFO, "Creating CreateCommand for bounded task");

		String name = nameParser.getName(index);
		LocalDateTime sDateTime = getDateTime(sDateTimeStr);
		LocalDateTime eDateTime = getDateTime(eDateTimeStr);
		return new CreateCommand(name, sDateTime, eDateTime);
	}

	private AbstractCommand display(ArrayList<String> args)
			throws DateTimeParseException {
		if (args.isEmpty()) {
			logger.log(Level.INFO, "Creating DisplayCommand for default view");
			return new DisplayCommand(DisplayCommand.Scope.DEFAULT);
		}

		String firstWord = args.get(0).toLowerCase();
		boolean oneWord = args.size() == 1;
		boolean isAll = firstWord.equals(Constants.SCOPE_ALL) && oneWord;
		boolean isDone = (firstWord.equals(Constants.SCOPE_DONE) || 
				firstWord.equals(Constants.CMD_MARK)) && oneWord;
		boolean isUndone = (firstWord.equals(Constants.SCOPE_UNDONE) || 
				firstWord.equals(Constants.CMD_UNMARK)) && oneWord;
		boolean isFloating = firstWord.equals(Constants.SCOPE_FLOATING)
				&& oneWord;

		if (isAll) {
			logger.log(Level.INFO, "Creating DisplayCommand for all view");
			return new DisplayCommand(DisplayCommand.Scope.ALL);
		} else if (isDone) {
			logger.log(Level.INFO, "Creating DisplayCommand for done view");
			return new DisplayCommand(DisplayCommand.Scope.DONE);
		} else if (isUndone) {
			logger.log(Level.INFO, "Creating DisplayCommand for undone view");
			return new DisplayCommand(DisplayCommand.Scope.UNDONE);
		} else if (isFloating) {
			logger.log(Level.INFO, "Creating DisplayCommand for floating view");
			return new DisplayCommand(DisplayCommand.Scope.FLOATING);
		} else {
			return search(args);
		}
	}

	private AbstractCommand search(ArrayList<String> args)
			throws DateTimeParseException {
		if (args.isEmpty()) {
			logger.log(Level.INFO, "Creating DisplayCommand for default view");
			return new DisplayCommand(DisplayCommand.Scope.DEFAULT);
		}

		int dateIndex = dateParser.getDateIndex(0, args.size());
		boolean isDate = dateIndex != -1
				&& dateProcessor.processDate(args, dateIndex).size() == 1;

		if (isDate) {
			args = dateProcessor.processDate(args, dateIndex);
			String date = dateParser.getDate(args.get(dateIndex));
			String dateTimeStr = date + Constants.WHITESPACE
					+ Constants.DUMMY_TIME_S;
			logger.log(Level.INFO, "Creating DisplayCommand by search date");
			return new DisplayCommand(getDateTime(dateTimeStr));
		} else {
			args = nameParser.removeSlash(args);
			logger.log(Level.INFO, "Creating DisplayCommand by search keyword");
			return new DisplayCommand(args);
		}
	}

	private AbstractCommand delete(ArrayList<String> args) {
		if (args.isEmpty()) {
			return invalidCommand();
		}

		String firstWord = args.get(0).toLowerCase();
		boolean oneWord = args.size() == 1;
		boolean isAll = firstWord.equals(Constants.SCOPE_ALL) && oneWord;
		boolean isIndex = isPositiveInteger(firstWord) && oneWord;

		if (isAll) {
			logger.log(Level.INFO, "Creating DeleteCommand for all");
			return new DeleteCommand(DeleteCommand.Scope.ALL);
		} else if (isIndex) {
			logger.log(Level.INFO, "Creating DeleteCommand by index");
			return new DeleteCommand(Integer.parseInt(firstWord));
		} else {
			logger.log(Level.INFO, "Creating DeleteCommand by search keyword");
			return new DeleteCommand(nameParser.getName(args.size()));
		}
	}

	private AbstractCommand edit(ArrayList<String> args) {
		if (args.isEmpty()) {
			return invalidCommand();
		}

		EditCommand output;
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();

		// pre-process "start to" to "start" and "end to" to "end"
		int sIndex = indexParser.getIndex(Constants.KEYWORD_START,
				Constants.KEYWORD_TO);
		if (sIndex != -1) {
			args.remove(sIndex + 1);
		}
		int eIndex = indexParser.getIndex(Constants.KEYWORD_END,
				Constants.KEYWORD_TO);
		if (eIndex != -1) {
			args.remove(eIndex + 1);
		}
		int toIndex = indexParser.getIndexOfFirst(Constants.KEYWORD_TO);

		// index 0 to index endPointOldName (non-inclusive)
		// forms old task name
		int endPointOldName = getEndPointOldName(toIndex, sIndex, eIndex,
				args.size());

		// index startPointName to index endPointName (non-inclusive)
		// forms new task name
		int endPointName = getEndPointName(sIndex, eIndex, args.size());
		int startPointName = getStartPointName(toIndex, endPointName);

		// index sIndex to index endPointStart (non-inclusive)
		// forms range for new start datetime
		int endPointStart = getEndPointStart(eIndex, args.size());

		// find search index or search keyword
		String search = nameParser.getNameWithSlash(endPointOldName);
		if (isPositiveInteger(search)) {
			output = new EditCommand(Integer.parseInt(search));
		} else {
			output = new EditCommand(nameParser.getName(endPointOldName));
		}

		// edit name
		String newName = nameParser.getName(startPointName, endPointName);
		if (newName.length() != 0) {
			editType.add(EditCommand.editField.NAME);
			output.setNewName(newName);
		}

		if (sIndex != -1) {

			// edit start time
			int indexOfsTime = timeParser.getTimeIndex(sIndex, endPointStart);
			if (indexOfsTime != -1) {
				String sTime = timeParser.getTime(args.get(indexOfsTime));
				editType.add(EditCommand.editField.START_TIME);
				output.setNewStartTime(sTime);
			}

			// edit start date
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

			// edit end time
			int indexOfeTime = timeParser.getTimeIndex(eIndex, args.size());
			if (indexOfeTime != -1) {
				String eTime = timeParser.getTime(args.get(indexOfeTime));
				editType.add(EditCommand.editField.END_TIME);
				output.setNewEndTime(eTime);
			}

			// edit end date
			int indexOfeDate = dateParser.getDateIndex(eIndex, args.size());
			if (indexOfeDate != -1) {
				args = dateProcessor.processDate(args, indexOfeDate);
				refreshParsers(args);
				String eDate = dateParser.getDate(args.get(indexOfeDate));
				editType.add(EditCommand.editField.END_DATE);
				output.setNewEndDate(eDate);
			}

		}

		if (editType.isEmpty() && 
				output.getType().equals(EditCommand.Type.SEARCHKEYWORD)) {
			return invalidCommand();
		} else {
			logger.log(Level.INFO, "Creating EditCommand");
			output.setEditFields(editType);
			return output;
		}
	}

	private AbstractCommand mark(ArrayList<String> args, String field) {
		if (args.isEmpty()) {
			return invalidCommand();
		}

		MarkCommand output;

		String firstWord = args.get(0).toLowerCase();
		boolean oneWord = args.size() == 1;
		boolean isIndex = isPositiveInteger(firstWord) && oneWord;

		if (isIndex) {
			logger.log(Level.INFO, "Creating MarkCommand by index");
			output = new MarkCommand(Integer.parseInt(firstWord));
		} else {
			logger.log(Level.INFO, "Creating MarkCommand by search keyword");
			output = new MarkCommand(nameParser.getName(args.size()));
		}

		if (field.equals(Constants.CMD_MARK)) {
			output.setMarkField(MarkCommand.markField.MARK);
		} else if (field.equals(Constants.CMD_UNMARK)) {
			output.setMarkField(MarkCommand.markField.UNMARK);
		} else {
			return invalidCommand();
		}

		return output;
	}

	private AbstractCommand undo(ArrayList<String> args) {
		if (args.isEmpty()) {
			logger.log(Level.INFO, "Creating UndoCommand");
			return new UndoCommand();
		} else {
			return invalidCommand();
		}
	}

	private AbstractCommand save(ArrayList<String> args) {
		if (args.size() != 1) {
			return invalidCommand();
		} else {
			logger.log(Level.INFO, "Creating SaveCommand");
			String firstWord = args.get(0);
			return new SaveCommand(firstWord);
		}
	}

	private AbstractCommand exit(ArrayList<String> args) {
		if (args.size() != 0) {
			return invalidCommand();
		} else {
			logger.log(Level.INFO, "Creating ExitCommand");
			return new ExitCommand();
		}
	}

	private AbstractCommand uiOneWord(ArrayList<String> args) {
		if (args.size() != 0) {
			return invalidCommand();
		} else {
			logger.log(Level.INFO, "Creating UICommand");
			return new UICommand();
		}
	}

	private AbstractCommand uiTwoWords(ArrayList<String> args) {
		if (args.size() != 2) {
			return invalidCommand();
		}

		String firstWord = args.get(0);
		String secondWord = args.get(1);
		boolean isHideOrShowYear = (firstWord.equals(Constants.CMD_HIDE) || 
				firstWord.equals(Constants.CMD_SHOW)) && 
				secondWord.equals(Constants.CMD_YEAR);
		boolean isQuitHelp = firstWord.equals(Constants.CMD_QUIT) && 
				secondWord.equals(Constants.CMD_HELP);

		if (isHideOrShowYear || isQuitHelp) {
			logger.log(Level.INFO, "Creating UICommand");
			return new UICommand();
		} else {
			return invalidCommand();
		}
	}

	private AbstractCommand invalidCommand() {
		logger.log(Level.INFO, "Creating InvalidCommand");
		return new InvalidCommand();
	}

	
	
	/**
	 * Boolean methods to check task type
	 */
	private boolean isFloating(ArrayList<String> args) {
		return !args.isEmpty();
	}

	private boolean isDeadline(ArrayList<String> args) {
		int index = indexParser.getIndex(Constants.KEYWORD_BY);

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
		int sIndex = indexParser.getIndex(Constants.KEYWORD_FROM);
		int eIndex = indexParser.getIndex(Constants.KEYWORD_TO);

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
				sIndex = indexParserCopy.getIndex(Constants.KEYWORD_FROM);
				eIndex = indexParserCopy.getIndex(Constants.KEYWORD_TO);
				return argsCopy.size() == eIndex + Constants.NUM_AFTER_TO
						&& eIndex - sIndex == Constants.NUM_BETWEEN_FROM_TO;
			} else {
				return false;
			}
		}
	}

	private boolean isAllDay(ArrayList<String> args) {
		int index = indexParser.getIndex(Constants.KEYWORD_ON);

		if (index == -1) {
			return false;
		} else {
			String name = nameParser.getName(index);
			int dateIndex = dateParser.getDateIndex(index, args.size());

			if ((name.length() != 0) && (dateIndex != -1)) {
				ArrayList<String> argsCopy = dateProcessor.processAllDay(args);
				return argsCopy.size() == index + Constants.NUM_AFTER_ON;
			} else {
				return false;
			}
		}
	}

	
	
	/**
	 * Helper methods for command handler function edit
	 */
	private int getEndPointOldName(int toIndex, int sIndex, int eIndex,
			int maxIndex) {
		if (toIndex != -1) {
			return toIndex;
		} else if (sIndex != -1) {
			return sIndex;
		} else if (eIndex != -1) {
			return eIndex;
		} else {
			return maxIndex;
		}
	}

	private int getStartPointName(int toIndex, int endPointName) {
		if (toIndex == -1) {
			return endPointName;
		} else {
			return toIndex + 1;
		}
	}

	private int getEndPointName(int sIndex, int eIndex, int maxIndex) {
		if (sIndex != -1) {
			return sIndex;
		} else if (eIndex != -1) {
			return eIndex;
		} else {
			return maxIndex;
		}
	}

	private int getEndPointStart(int eIndex, int maxIndex) {
		if (eIndex != -1) {
			return eIndex;
		} else {
			return maxIndex;
		}
	}

	
	
	/**
	 * Helper methods
	 */
	private boolean isPositiveInteger(String str) {
		try {
			int integer = Integer.parseInt(str);
			return integer > 0;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private LocalDateTime getDateTime(String dateTimeStr) {
		return LocalDateTime.parse(dateTimeStr, Constants.DTFormatter);
	}

	private ArrayList<String> arrayToArrayList(String[] array) {
		ArrayList<String> arrayList = new ArrayList<String>();
		for (int i = 0; i < array.length; i++) {
			arrayList.add(array[i]);
		}
		return arrayList;
	}

}
