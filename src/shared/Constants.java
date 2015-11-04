package shared;

import java.time.format.DateTimeFormatter;

/**
 * This class is used to store constants.
 */
public class Constants {

	// @@author A0133888N
	// UI constants
	public static final String DAY_COLOR = "#c9daf8";
	public static final String NIGHT_COLOR = "#1a237e;";

	public static final String COMMAND_HIDE_YEAR = "hide year";
	public static final String COMMAND_SHOW_YEAR = "show year";
	public static final String COMMAND_DAY = "day";
	public static final String COMMAND_NIGHT = "night";
	public static final String COMMAND_HELP = "help";
	public static final String COMMAND_QUIT_HELP = "quit help";
	public static final String HELP_MESSAGE_CREATE = "e.g. create ... from ... to ...";
	public static final String HELP_MESSAGE_EDIT = "e.g. edit index to ...(new name)";
	public static final String HELP_MESSAGE_DELETE = "e.g. delete index";
	public static final String HELP_MESSAGE_MARK = "e.g. mark index";
	public static final String HELP_MESSAGE_UNMARK = "e.g. ummark index";
	public static final String HELP_MESSAGE_DISPLAY = "e.g. display ...(date, task name, ...)";
	public static final String RETURN_TIP = String.format(
			"\nEnter \"%1$s\" to return to the normal view.\n===================================================================",
			COMMAND_QUIT_HELP);
	public static final String TIME_EARLIEST = "12am";
	public static final String TIME_LATEST = "11:59pm";

	// UI constants ends
	
	
	
	// @@author A0131188H
	// Parser constants
	public static final DateTimeFormatter DTFormatter =  DateTimeFormatter.ofPattern("dd MM yyyy HH mm");
	public static final String dummyTime = "00 00";
	
	public static final String BY = "by";
	public static final String FROM = "from";
	public static final String TO = "to";
	public static final String START = "start";
	public static final String END = "end";
	
	public static final String ALL = "all";
	public static final String DONE = "done";
	public static final String UNDONE = "undone";
	public static final String FLOATING = "floating";
	public static final String OVERDUE = "overdue";
	public static final String MARK = "mark";
	public static final String UNMARK = "unmark";
	
	public static final String LAST = "last";
	public static final String THIS = "this";
	public static final String NEXT = "next";
	
	public static final String AM = "am";
	public static final String PM = "pm";
	
	public static final String DAY = "day";
	public static final String NIGHT = "night";
	public static final String SHOW = "show";
	public static final String HIDE = "hide";
	public static final String YEAR = "year";
	public static final String HELP = "help";
	public static final String QUIT = "quit";
	// Parser constants ends
	
	//@@author A0124828B
	//Logic constants
	public static final String MESSAGE_INVALID_COMMAND = "Invalid Command!";
	public static final String MESSAGE_INVALID_KEYWORD = "No task with keyword \"%1$s\" has been found.";
	public static final int MESSAGE_LENGTH = 80;
	//Logic constants ends
	
}
