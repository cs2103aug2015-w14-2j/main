package shared;

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

	// UI constants ends

}
