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
	public static final boolean isWin = System.getProperty("os.name").toLowerCase().contains("win");
	public static final int numberOfEquals = isWin ? 69 : 68;
	public static final String line = new String(new char[numberOfEquals]).replace("\0", "=");
	public static final String padding = "%-12s";
	
	//The text version of full help message is originally written by A0131188H
	public static final String[] HELP_MESSAGE_FULL = {
			 String.format("\nEnter \"%1$s\" to return to the normal view.", COMMAND_QUIT_HELP),
			 line,
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
			 "OR",
			 "create [task name] on [date]",
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
			 "",
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
			 "Command    Alias",
			 String.format(padding, "create") + "c, add, a",
			 String.format(padding, "display") + "dp",
			 String.format(padding, "search") + "s",
			 String.format(padding, "edit") + "e",
			 String.format(padding, "delete") + "dl",
			 String.format(padding, "mark") + "m",
			 String.format(padding, "unmark") + "um",
			 String.format(padding, "undo") + "u"
		};

	// UI constants ends
	
	// @@author A0131188H
	// Parser constants
	/**
   * Formatter constants
   */
	public static final DateTimeFormatter DTFormatter =  DateTimeFormatter.ofPattern("dd MM yyyy HH mm");
	public static final String FORMATTER_2DP = "%02d";
	
	/**
   * Command constants
   */
	public static final String CMD_CREATE = "create";
	public static final String CMD_C = "c";
	public static final String CMD_ADD = "add";
	public static final String CMD_A = "a";
	public static final String CMD_DISPLAY = "display";
	public static final String CMD_DP = "dp";
	public static final String CMD_DELETE = "delete";
	public static final String CMD_DL = "dl";
	public static final String CMD_EDIT = "edit";
	public static final String CMD_E = "e";
	public static final String CMD_SEARCH = "search";
	public static final String CMD_S = "s";
	public static final String CMD_MARK = "mark";
	public static final String CMD_M = "m";
	public static final String CMD_UNMARK = "unmark";
	public static final String CMD_UM = "um";
	public static final String CMD_UNDO = "undo";
	public static final String CMD_U = "u";
	public static final String CMD_SAVE = "save";
	public static final String CMD_EXIT = "exit";
	public static final String CMD_DAY = "day";
	public static final String CMD_NIGHT = "night";
	public static final String CMD_HIDE = "hide";
	public static final String CMD_SHOW = "show";
	public static final String CMD_YEAR = "year";
	public static final String CMD_HELP = "help";
	public static final String CMD_QUIT = "quit";
	
	/**
   * Scope constants
   */
	public static final String SCOPE_ALL = "all";
	public static final String SCOPE_DONE = "done";
	public static final String SCOPE_UNDONE = "undone";
	public static final String SCOPE_FLOATING = "floating";
	
	/**
   * Keyword constants
   */
	public static final String KEYWORD_BY = "by";
	public static final String KEYWORD_FROM = "from";
	public static final String KEYWORD_TO = "to";
	public static final String KEYWORD_ON = "on";
	public static final String KEYWORD_START = "start";
	public static final String KEYWORD_END = "end";
	
	public static final int NUM_AFTER_BY = 3;
	public static final int NUM_AFTER_ON = 2;
	public static final int NUM_AFTER_TO = 3;
	public static final int NUM_BETWEEN_FROM_TO = 3;
	
	/**
   * Splitter constants
   */
	public static final String SPLITTER_DATE = "(-|\\/|\\s)";
	public static final String SPLITTER_WHITESPACE = " ";
	public static final String SPLITTER_COLON = ":";
	public static final String SPLITTER_DOT = "\\.";
  
	/**
   * Date constants
   */
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
	
	public static final String JANUARY = "january";
	public static final String JAN = "jan";
	public static final String FEBRUARY = "february";
	public static final String FEB = "feb";
	public static final String MARCH = "march";
	public static final String MAR = "mar";
	public static final String APRIL = "april";
	public static final String APR = "apr";
	public static final String MAY = "may";
	public static final String JUNE = "june";
	public static final String JUN = "jun";
	public static final String JULY = "july";
	public static final String JUL = "jul";
	public static final String AUGUST = "august";
	public static final String AUG = "aug";
	public static final String SEPTEMBER = "september";
	public static final String SEP = "sep";
	public static final String OCTOBER = "october";
	public static final String OCT = "oct";
	public static final String NOVEMBER = "november";
	public static final String NOV = "nov";
	public static final String DECEMBER = "december";
	public static final String DEC = "dec";
	
	/**
   * Time constants
   */
	public static final String TIME_FORMAT_1 = "(0[1-9]|[1-9]|1[012])(:|.)[0-5][0-9](?i)(am|pm)";
	public static final String TIME_FORMAT_2 = "(1[012]|[1-9])(?i)(am|pm)";
	
	public static final String TIME_INTEGER = "0|00|(^[0-9]*[1-9][0-9]*$)";
	
	public static final String DUMMY_TIME_S = "00 00";
	public static final String DUMMY_TIME_E = "23 59";
	
	public static final String AM = "am";
	public static final String PM = "pm";
	
	/**
   * General constants
   */
	public static final String EMPTY = "";
	public static final String WHITESPACE = " ";
	public static final String SLASH = "/";
	public static final String COLON = ":";
	public static final String DOT = ".";
	// Parser constants ends
	
	
	
	//@@author A0124828B
	//Logic constants
	public static final String MESSAGE_INVALID_COMMAND = "Invalid Command!";
	public static final String MESSAGE_INVALID_KEYWORD = "No task with keyword \"%1$s\" has been found.";
	public static final String MESSAGE_INVALID_INDEX = "Invalid: There is no task with the given index.";
	public static final int MESSAGE_LENGTH = 72;
	//Logic constants ends
	
}
