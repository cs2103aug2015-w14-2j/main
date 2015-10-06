import java.util.ArrayList;
import java.util.regex.Pattern;

public class Parser {

	private static final String MESSAGE_ERROR = "INVALID INPUT";
	private static final String MESSAGE_CREATE = "CREATE SELECTED";
	private static final String MESSAGE_DISPLAY = "DISPLAY SELECTED";
	private static final String MESSAGE_DELETE = "DELETE SELECTED";
	private static final String MESSAGE_EDIT = "EDIT SELECTED";

	public ArrayList<String> evaluateInput(String line) {
		String[] temp = line.split(" ");
		ArrayList<String> commands = new ArrayList<String>();
		for (int i = 0; i < temp.length; i++) {
			commands.add(temp[i]);
		}

		switch (commands.get(0)) {
		case "create": {
			// System.out.println(MESSAGE_CREATE);
			return create(commands);
		}
		case "display": {
			// System.out.println(MESSAGE_DISPLAY);
			return display(commands);
		}
		case "delete": {
			// System.out.println(MESSAGE_DELETE);
			return delete(commands);
		}
		case "edit-start":
		case "edit-name" :
		case "edit-end"  :{
			// System.out.println(MESSAGE_EDIT);
			return edit(commands);
		}
		default: {
			// System.out.println(MESSAGE_ERROR);
			return isInvalid();
		}
		}

	}

	public ArrayList<String> create(ArrayList<String> commands) {
		int taskType = 0;
		// 0 for floating task (default), 1 for deadline tasks , 2 for bounded
		// tasks
		// find a way to identify the different task using keywords
		for (int i = commands.size() - 1; i > 0; i--) {

			if (commands.get(i).equals("to")) {
				if ((commands.get(i - 3).equals("from")) && isDateFormat(commands.get(i - 1).toString())
						&& (isTimeFormat(commands.get(i - 2).toString())))
					// it's a bounded task
					taskType = 2;
				break;
			}
			if (commands.get(i).equals("by") && isDateFormat(commands.get(i + 2).toString())
					&& (isTimeFormat(commands.get(i + 1).toString()))) {
				// it's a deadline task
				taskType = 1;
				break;
			}

		}

		// it's floating task if all conditions fails taskType stays at 0;

		if (taskType == 2) {
			return boundedTask(commands);
		} else if (taskType == 1) {
			return deadlineTask(commands);
		}
		return floatingTask(commands);

	}

	private ArrayList<String> boundedTask(ArrayList<String> commands) {
		ArrayList<String> result = new ArrayList<String>();
		String temp = new String();
		if (!isDateFormat(commands.get(commands.size() - 1).toString())) {
			return isInvalid();

		}
		result.add(commands.get(0)); // add the 'create' string
		int i = 1;
		while (!commands.get(i).equals("from")) {
			temp += commands.get(i) + " ";// create event string;
			i++;
		}
		result.add(temp.trim()); // add the event string
		i++;
		/*
		 * if(timeBreakdown(commands.get(i)).equals("invalid")){ return
		 * isInvalid();
		 * 
		 * }
		 */
		result.add(timeBreakdown(commands.get(i))); // add 'from time' string
		i++;
		result.add(dateBreakdown(commands.get(i))); // add 'from date' string
		i++; // ignore the word 'to'
		i++;/*
			 * if(timeBreakdown(commands.get(i)).equals("invalid")){ return
			 * isInvalid(); }
			 */
		result.add(timeBreakdown(commands.get(i))); // add 'to time' string
		i++;
		result.add(dateBreakdown(commands.get(i))); // add 'to date' string
		return result;
	}

	private ArrayList<String> deadlineTask(ArrayList<String> commands) {
		if (!isDateFormat(commands.get(commands.size() - 1).toString())) {
			return isInvalid();

		}
		ArrayList<String> result = new ArrayList<String>();
		String temp = new String();
		result.add(commands.get(0)); // add the 'create' string
		int i = 1;
		while (!commands.get(i).equals("by")) {
			temp += commands.get(i) + " ";// create event string;
			i++;
		}

		result.add(temp.trim()); // add the event string //word 'by' is already
									// ignored with the value of current i

		result.add(""); // add 'from time' string

		result.add(""); // add 'from date' string

		i++;
		//System.out.println(commands.get(i));
		if (!isTimeFormat(commands.get(i)) || !isDateFormat(commands.get(i + 1))) {
			return isInvalid();
		}
		if (timeBreakdown(commands.get(i)).equals("invalid")) {
			return isInvalid();
		}
		result.add(timeBreakdown(commands.get(i))); // add 'to time' string
		i++;
		//System.out.println(commands.get(i));
		result.add(dateBreakdown(commands.get(i))); // add 'to date' string

		if (i != commands.size() - 1) {
			// there are other things at the back.
			result.clear();
			result.add("invalid");
			for (int j = 1; j < 6; j++) {
				result.add("");
			}
		}
		return result;
	}

	private ArrayList<String> floatingTask(ArrayList<String> commands) {
		ArrayList<String> result = new ArrayList<String>();
		String temp = new String();
		result.add(commands.get(0)); // add the 'create' string

		for (int i = 1; i < commands.size(); i++) {
			temp += commands.get(i) + " ";
		}
		result.add(temp.trim()); // add event string

		for (int i = 0; i < 4; i++) {
			result.add("");
		}
		return result;
	}

	public ArrayList<String> display(ArrayList<String> commands) {
		ArrayList<String> resultString = new ArrayList<String>();
		resultString.add(commands.get(0)); // add "display" word
	
		for (int i = 0; i < 5; i++) {
			resultString.add(""); // add 4 "" to make length 6;
		}

		return resultString;

	}
	private boolean checkIndex(String test){
		Integer index = Integer.parseInt(test.replace("#", ""));
		if (index <=0){
			return false;
		}
		return true;
		
	}
	public ArrayList<String> delete(ArrayList<String> commands) {
		ArrayList<String> resultString = new ArrayList<String>();
		if(commands.size()!=2){
			return isInvalid();
		}
		resultString.add("delete");
		if (!checkIndex(commands.get(1))){
			return isInvalid();
		}
		
		resultString.add(commands.get(1));
		for (int i = 0 ; i < 4; i ++){
			resultString.add("");
		}

		
		return resultString;
	}

	public ArrayList<String> edit(ArrayList<String> commands) {
		ArrayList<String> resultString = new ArrayList<String>();
		String[] words = commands.toString().split(" ");
		
		
		
		String[] type = commands.get(0).toString().split("-");
		if (type[1].equals("name")) {
			resultString = editName(commands);
		} else if (type[1].equals("start")) {
			resultString = editStart(commands);
		} else if (type[2].equals("end")) {
			resultString = editEnd(words);
		} else {
			resultString.add("invalid");
			for (int i = 1; i < 6; i++) {
				resultString.add("");
			}
		}
		return resultString;
	}

	private ArrayList<String> editName(ArrayList<String>commands) {
		ArrayList<String> result = new ArrayList<String>();
		String temp = new String();
		result.add("edit"); // add 'edit'
		result.add("name"); // add 'name'
		if (!checkIndex(commands.get(1))){
			return isInvalid();
		}
		result.add(commands.get(1));// add index
		for (int i = 2; i < commands.size(); i++) {
			temp += commands.get(i) + " ";
		}
		result.add(temp.trim()); // add new name
		result.add(""); // add 2 "" at the back to make
		result.add(""); // it 6

		return result;

	}

	private ArrayList<String> editStart(ArrayList<String> commands) {
		ArrayList<String> result = new ArrayList<String>();
		result.add("edit"); // add 'edit'
		result.add("start"); // add 'start'
		result.add(commands.get(1)); // add index
		result.add(timeBreakdown(commands.get(2))); // add new time
		result.add(dateBreakdown(commands.get(3))); // add new date
		result.add(""); // add "" to make it 6

		return result;

	}

	private ArrayList<String> editEnd(String[] type) {
		ArrayList<String> result = new ArrayList<String>();
		result.add("edit"); // add 'edit'
		result.add("end"); // add 'end'
		result.add(type[1]);
		result.add(timeBreakdown(type[2])); // add new time
		result.add(dateBreakdown(type[3])); // add new date
		result.add(""); // add 2 "" to make it 6
		result.add("");
		return result;
	}

	private String dateBreakdown(String date) {
		if (!isDateFormat(date)) {
			return "invalid";
		}
		String[] components = date.split("(-|\\/)");
		String result = new String();
		for (int i = 0; i < components.length; i++) {
			if (i == components.length - 1) {
				if (Integer.parseInt(components[i]) <= 99) {
					components[i] = String.format("%02d", 2000 + Integer.parseInt(components[i]));
				}
			}
			components[i] = String.format("%02d", Integer.parseInt(components[i]));
			result += components[i] + " ";
		}
		return result.trim();
	}

	// dd-mm-yy or dd-mm-yyyy or dd/mm/yy or dd/mm/yyyy
		// dd or mm can be single digit or padded single digit or double digit
		// day+month combination works for all months except Feb (always 1 Feb - 28
		// Feb regardless of leap year)
		public static boolean isDateFormat(String str) {
			String df = "(((0[1-9])|([12])([0-9]?)|(3[01]?))(-|\\/)(0?[13578]|10|12)(-|\\/)((\\d{4})|(\\d{2}))|((0[1-9])|([12])([0-9]?)|(3[0]?))(-|\\/)(0?[2469]|11)(-|\\/)((\\d{4}|\\d{2})))$";
	//String newDf = "(?:(?:31(\/|-|\.)(?:0?[13578]|1[02]))\1|(?:(?:29|30)(\/|-|\.)(?:0?[1,3-9]|1[0-2])\2))(?:(?:1[6-9]|[2-9]\d)?\d{2})$|^(?:29(\/|-|\.)0?2\3(?:(?:(?:1[6-9]|[2-9]\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\d|2[0-8])(\/|-|\.)(?:(?:0?[1-9])|(?:1[0-2]))\4(?:(?:1[6-9]|[2-9]\d)?\d{2})$"
			if (Pattern.matches(df, str)) {
				String[] dateParts;
				if (str.contains("-")) {
					dateParts = str.split("-");
				} else if (str.contains("/")) {
					dateParts = str.split("/");
				} else {
					return false;
				}
	
				int day = Integer.parseInt(dateParts[0]);
				int month = Integer.parseInt(dateParts[1]);
	
				return (month == 2 && day > 28) ? false : true;
			} else {
				return false;
			}
		}

	private boolean isDateButIncorrect(String string) {
			String[] dateParts;
			if (string.contains("/")) {
				dateParts = string.split("/");
			} else if (string.contains("-")) {
				dateParts = string.split("-");
			} else {
				return false;
			}
			
			if (dateParts.length != 3) {
				return false;
			}
			
			String day = dateParts[0];
			String month = dateParts[1];
			String year = dateParts[2];
			
			if (isInt(day) && isInt(month) && isInt(year)) {
				return true;
			} else {
				return false;
			}
		}

	private boolean leapYear(int year, int month, int day) {
		boolean isLeapYear = ((year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0));
		return isLeapYear;
	}
	
	private String timeBreakdown(String time) {
	
		if (!isTimeFormat(time)) {
			return "invalid";
		} else {
			String result = new String();
			String[] components = time.split(":");
			Integer hour = Integer.parseInt(components[0]);
			if (hour ==24) {
				return "invalid";
			}
			if (hour<12&&(components[1].contains("pm") || components[1].contains("PM"))) {
				hour +=12;
				if (hour ==24){
					hour = 0 ; 
				}
				components[0] = hour.toString();
	
			}
	
			result += (String.format("%02d", Integer.parseInt(components[0])) + " ");
			components[1] = components[1].replaceAll("[^\\d.]", "");
			result += (String.format("%02d", Integer.parseInt(components[1])));
	
			return result;
		}
	
	}

	private boolean isTimeButIncorrect(String string) {
		String[] timeParts;
		if (string.contains(":")) {
			timeParts = string.split(":");
		} else {
			return false;
		}
		
		if (timeParts.length != 2) {
			return false;
		}
		
		String hour = timeParts[0];
		String minute = timeParts[1];
		String m = "";
		
		if (minute.length() != 2) {
			m = minute.substring(2);
			minute = minute.substring(0, 1);
			if (!(m.length() == 2)) {
				return false;
			}
		}
		
		if (isInt(hour) && isInt(minute)) {
			return true;
		} else {
			return false;
		}
	}

	// Accepts 24-hour format: 8:00, 08:00, 20:00
	// Accepts 12-hour format: 1:00am, 1:00AM, 1:00 am (not used by us), 1:00 AM
	// (not used by us),
	// 1:00pm, 1:00PM, 1:00 pm (not used by us), 1:00 PM (not used by us)
	// 1 or 2 or 3 or 4 or 5 or 6 or 7 or 8 or 9 or 10 or 11 or 12 +
	// : + (digit) + (digit) + (space (not used by us) || no space) + (am || AM
	// || pm || PM)
	public static boolean isTimeFormat(String str) {
		String tf24 = "([012]?[0-9]|1[0-9]|2[0-3]):[0-5][0-9]";
		String tf12 = "(1[012]|[1-9]):[0-5][0-9](\\s)?(?i)(am|pm)";
		return (Pattern.matches(tf24, str) || Pattern.matches(tf12, str));
	}

	private boolean isInt(String string) {
		for (int i = 0; i < string.length(); i++) {
			if (!isDigit(string.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	private boolean isDigit(char c) {
		return Character.isDigit(c);
	}

	private ArrayList<String> isInvalid() {
		ArrayList<String> invalidArray = new ArrayList<String>();
		invalidArray.add("invalid");
		invalidArray.add("");
		invalidArray.add("");
		invalidArray.add("");
		invalidArray.add("");
		invalidArray.add("");
		return invalidArray;
	}

}