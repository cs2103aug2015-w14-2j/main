package shared;

import java.time.format.DateTimeFormatter;

/**
 * This class is used to store constants.
 */
public class Constants {

	// @@author A0133888N
	// UI constants
	public static final String COLOR_DAY = "#c9daf8";
	public static final String COLOR_NIGHT = "#191970";
	public static final String COLOR_INPUT_DAY = "#337ab7";

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
	public static final String sDummyTime = "00 00";
	public static final String eDummyTime = "23 59";
	public static final String AM = "am";
	public static final String PM = "pm";
	
	public static final String CREATE = "create";
	public static final String C = "c";
	public static final String ADD = "add";
	public static final String A = "a";
	public static final String DISPLAY = "display";
	public static final String DP = "dp";
	public static final String DELETE = "delete";
	public static final String DL = "dl";
	public static final String EDIT = "edit";
	public static final String E = "e";
	public static final String SEARCH = "search";
	public static final String S = "s";
	public static final String MARK = "mark";
	public static final String M = "m";
	public static final String UNMARK = "unmark";
	public static final String UM = "um";
	public static final String UNDO = "undo";
	public static final String U = "u";
	public static final String SAVE = "save";
	public static final String EXIT = "exit";
	public static final String DAY = "day";
	public static final String NIGHT = "night";
	public static final String HIDE = "hide";
	public static final String SHOW = "show";
	public static final String YEAR = "year";
	public static final String HELP = "help";
	public static final String QUIT = "quit";
	
	public static final String BY = "by";
	public static final String FROM = "from";
	public static final String TO = "to";
	public static final String ON = "on";
	public static final String START = "start";
	public static final String END = "end";
	
	public static final int NUM_AFTER_BY = 3;
	public static final int NUM_AFTER_ON = 2;
	public static final int NUM_AFTER_TO = 3;
	public static final int NUM_BETWEEN_FROM_TO = 3;
	
	public static final String ALL = "all";
	public static final String DONE = "done";
	public static final String UNDONE = "undone";
	public static final String FLOATING = "floating";
	
	public static final String YESTERDAY = "yesterday";
	public static final String YTD = "ytd";
	public static final String TODAY = "today";
	public static final String TONIGHT = "tonight";
	public static final String TOMORROW = "tomorrow";
	public static final String TMR = "tmr";
	
	public static final String LAST = "last";
	public static final String THIS = "this";
	public static final String NEXT = "next";
	
	public static final String MONDAY = "monday";
	public static final String MON = "mon";
	public static final String TUESDAY = "tuesday";
	public static final String TUES = "tues";
	public static final String WEDNESDAY = "wednesday";
	public static final String WED = "wed";
	public static final String THURSDAY = "thursday";
	public static final String THURS = "thurs";
	public static final String FRIDAY= "friday";
	public static final String FRI = "fri";
	public static final String SATURDAY = "saturday";
	public static final String SAT = "sat";
	public static final String SUNDAY= "sunday";
	public static final String SUN = "sun";
	
	public static final String SPLITTER_DATE = "(-|\\/|\\s)";
	public static final String SPLITTER_WHITESPACE = " ";
	public static final String FORMATTER_2DP = "%02d";
	public static final String EMPTY = "";
	public static final String WHITESPACE = " ";
	public static final String SLASH = "/";
	// Parser constants ends
	
	//@@author A0124828B
	//Logic constants
	public static final String MESSAGE_INVALID_COMMAND = "Invalid Command!";
	public static final String MESSAGE_INVALID_KEYWORD = "No task with keyword \"%1$s\" has been found.";
	public static final int MESSAGE_LENGTH = 72;
	//Logic constants ends
	
}
