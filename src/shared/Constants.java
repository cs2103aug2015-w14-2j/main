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
	
	public static final String HELP_MESSAGE_CREATE = "e.g. create [task name] from [time] [date] to [time] [date]";
	public static final String HELP_MESSAGE_EDIT = "e.g. edit [index] to [new task name] start to [time] [date] end to [time] [date]";
	public static final String HELP_MESSAGE_DELETE = "e.g. delete [index]";
	public static final String HELP_MESSAGE_MARK = "e.g. mark [index]";
	public static final String HELP_MESSAGE_UNMARK = "e.g. unmark [index]";
	public static final String HELP_MESSAGE_DISPLAY = "e.g. display done";
	public static final String HELP_MESSAGE_SEARCH = "e.g. search [date]";
	public static final String HELP_MESSAGE_UNDO = "e.g. undo";
	public static final String HELP_MESSAGE_SAVE = "e.g. save [path]";
	public static final String HELP_MESSAGE_HELP = "Enter help to see all the help messages.";
	
	public static final String TIME_EARLIEST = "12am";
	public static final String TIME_LATEST = "11:59pm";
	
	//The text version of full help message is originally written by A0131188H
	public static final String[] HELP_MESSAGE_FULL = {
			String.format(
					"\nEnter \"%1$s\" to return to the normal view.",
					COMMAND_QUIT_HELP),
			"===================================================================",
			 "COMMANDS",
			 "To create floating tasks",
			 "create [task name]",
			 "",
			 "To create deadline-based tasks",
			 "create [task name] by [time] [date]",
			 "",
			 "To create event-based tasks",
			 "create [task name] from [time] to [time] [date]",
			 "OR",
			 "create [task name] from [time] [date] to [time] [date]",
			 "",
			 "To show different views",
			 "display",
			 "display all", 
			 "display mark / display done", 
			 "display unmark / display undone",
			 "display floating",
			 "",
			 "To search tasks on a specific date",
			 "search [date] / display [date]",
			 "",
			 "To search by task name",
			 "search [task name] / display [task name]",
			 "",
			 "To edit tasks",
			 "edit [index] to [new task name]", 
			 "start to [time] [date]",
			 "end to [time] [date]",
			 "OR",
			 "edit [old task name] to [new task name]",
			 "start to [time] [date]", 
			 "end to [time] [date]",
			 "",
			 "To delete tasks",
			 "delete [index]",
			 "delete [task name]",
			 "delete all",
			 "",
			 "To mark or unmark tasks",
			 "mark [index]",
			 "mark [task name]",
			 "unmark [index]",
			 "unmark [task name]",

			 "To undo command",
			 "undo",
			 "",
			 "To save path",
			 "save [path]",
			 "",
			 "To toggle between day and night view",
			 "day",
			 "night",
			 "",
			 "To toggle between view with year displayed", 
			 "and without year displayed",
			 "show year",
			 "hide year",
			 "",
			 "To open and close this help menu",
			 "help",
			 "quit help",
			 "",
			 "[TIME] ACCEPTED",
			 "hh am/pm",
			 "hh:mm am/pm OR hh.mm am/pm", 
			 "hh:mm OR hh.mm",
			 "",
			 "[DATE] ACCEPTED",
			 "dd/mm OR dd-mm OR dd [month]",
			 "dd/mm/yyyy OR dd-mm-yyyy OR dd [month] yyyy",
			 "yesterday/ytd OR today OR tomorrow/tmr",
			 "last/this/next [day]",
			 "",
			 "[DAY] ACCEPTED",
			 "Monday / Tuesday / Wednesday / Thursday / Friday / Saturday / Sunday",
			 "Mon / Tues / Wed / Thurs / Fri / Sat / Sun",
			 "",
			 "[MONTH] ACCEPTED",
			 "January / February / March / April / May / June / July / ",
			 "August / September / October / November / December",
			 "Jan / Feb / Mar / Apr / Jun / Jul / Aug / Sep / Oct / Nov / Dec",
			 "",
			 "SHORTCUTS ACCEPTED",
			 "Command   Alias",
			 "create    c",
			 "display   dp",
			 "search    s",
			 "edit      e",
			 "delete    dl",
			 "mark      m",
			 "unmark    um",
			 "undo      u"
		};

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
