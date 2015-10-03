
import java.util.ArrayList;

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
			System.out.println(MESSAGE_CREATE);
			return create(commands);
		}
		case "display": {
			System.out.println(MESSAGE_DISPLAY);
			return display(commands);
		}
		case "delete": {
			System.out.println(MESSAGE_DELETE);
			return delete(commands);
		}
		case "edit": {
			System.out.println(MESSAGE_EDIT);
			return edit(commands);
		}
		default: {
			System.out.println(MESSAGE_ERROR);
			commands.get(0).replace(commands.get(0), "invalid");
			return commands;
		}
		}

	}

	public ArrayList<String> create(ArrayList<String> commands) {
		int taskType = 0;
		// 0 for floating task (default), 1 for deadline tasks , 2 for bounded
		// tasks

		// find a way to identify the different task using keywords
		for (int i = commands.size(); i > 1; i--) {
			if (commands.get(i).equals("to")) {
				if (validateDate(commands.get(i - 1).toString()) && (isTimeFormat(commands.get(i - 2).toString()))
						&& (commands.get(i - 3).equals("from")))
					// it's a bounded task
					taskType = 2;
				break;
			}
			if (commands.get(i).equals("by")&& validateDate(commands.get(i +2).toString()) && (isTimeFormat(commands.get(i + 1 ).toString()))) {
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
		result.add(commands.get(0)); // add the 'create' string
		int i = 1;
		while (!commands.get(i).equals("from")) {
			temp += commands.get(i) + " ";// create event string;
			i++;
		}
		result.add(temp.trim()); // add the event string
		i++;
		result.add(timeBreakdown(commands.get(i))); // add 'from time' string
		i++;
		result.add(dateBreakdown(commands.get(i))); // add 'from date' string
		i++; // ignore the word 'to'
		i++;
		result.add(timeBreakdown(commands.get(i))); // add 'to time' string
		i++;
		result.add(dateBreakdown(commands.get(i))); // add 'to date' string
		return result;
	}

	private ArrayList<String> deadlineTask(ArrayList<String> commands) {
		ArrayList<String> result = new ArrayList<String>();
		String temp = new String();
		result.add(commands.get(0)); // add the 'create' string
		int i = 1;
		while (!commands.get(i).equals("by")) {
			temp += commands.get(i);// create event string;
			i++;
		}
		result.add(temp.trim()); // add the event string //word 'by' is already
									// ignored with the value of current i
		i++;
		result.add(null); // add 'from time' string
		i++;
		result.add(null); // add 'from date' string

		i++;
		result.add(timeBreakdown(commands.get(i))); // add 'to time' string
		i++;
		result.add(dateBreakdown(commands.get(i))); // add 'to date' string
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
			result.add(null);
		}
		return result;
	}

	public ArrayList<String> display(ArrayList<String> commands) {
		ArrayList<String> resultString = new ArrayList<String>();
		resultString.add(commands.get(0)); // add "display" word
		if ((commands.get(1).equals("all")) || (commands.get(1).equals("done"))) {
			resultString.add("invalid");
			resultString.addAll(null);
		} else {
			resultString.add(commands.get(1));
		} // add "all" or "done" word
		for (int i = 0; i < 4; i++) {
			resultString.add(null); // add 4 null to make length 6;
		}

		return resultString;

	}

	public ArrayList<String> delete(ArrayList<String> commands) {
		ArrayList<String> resultString = new ArrayList<String>();
		resultString.add("delete");
		
		//delete by index
		return resultString;
	}

	public ArrayList<String> edit(ArrayList<String> commands) {
		ArrayList<String> resultString = new ArrayList<String>();
		String[] words = commands.toString().split(" ");
		String[] type = words[0].split("-");
		if (type[1].equals("name")) {
			resultString = editName(words);
		} else if (type[1].equals("start")) {
			resultString = editStart(words);
		} else if (type[2].equals("end")) {
			resultString = editEnd(words);
		}else{
			resultString.add("invalid");
			for (int i = 1 ; i< 6; i++){
				resultString.add(null);
			}
		}
		return resultString;
	}

	private ArrayList<String> editName(String[] type) {
		ArrayList<String> result = new ArrayList<String>();
		String temp = new String();
		result.add("edit"); // add 'edit'
		result.add("name"); // add 'name'
		result.add(type[1]);// add index
		for (int i = 1; i < type.length; i++) {
			temp += type[i];
		}
		result.add(temp); // add new name
		result.add(null); // add 2 null at the back to make
		result.add(null); // it 6
	
		return result;

	}

	private ArrayList<String> editStart(String[] type) {
		ArrayList<String> result = new ArrayList<String>();
		result.add("edit"); // add 'edit'
		result.add("start"); // add 'start'
		result.add(type[1]); // add index
		result.add(timeBreakdown(type[2])); // add new time
		result.add(dateBreakdown(type[3])); // add new date
		result.addAll(null); // add  null to make it 6
		
		return result;

	}

	private ArrayList<String> editEnd(String[] type) {
		ArrayList<String> result = new ArrayList<String>();
		result.add("edit"); // add 'edit'
		result.add("end"); // add 'end'
		result.add(type[1]);
		result.add(timeBreakdown(type[2])); // add new time
		result.add(dateBreakdown(type[3])); // add new date
		result.addAll(null); // add 2 null to make it 6
		result.addAll(null);
		return result;
	}
	
	
	private boolean isTimeFormat(String time){
		return true; 
	}

	private boolean validateTime(String time) {
		String[] components = time.split(":");
		Integer hour = Integer.parseInt(components[0]);
		if (components[1].contains("am") || (components[1].contains("pm"))) {
			if ((hour < 0) || (hour >= 13)) {
				return false;
			}

		} else if ((hour < 0) || (hour >= 25)) {
			return false;
		}

		Integer mins = Integer.parseInt(components[1].replaceAll("[^\\d.]", ""));
		if ((mins >= 60) || (mins < 0)) {
			return false;

		}

		return true;
	}

	private String timeBreakdown(String time) {

		if (!validateTime(time)) {
			return "invalid";
		} else {
			String result = new String();
			String[] components = time.split(":");

			result += (String.format("%02d", Integer.parseInt(components[0])) + " ");
			components[1] = components[1].replaceAll("[^\\d.]", "");
			result += (String.format("%02d", Integer.parseInt(components[1])));

			return result;
		}

	}

	private boolean validateDate(String date) {
		String[] components = date.split("-");

		int day = Integer.parseInt(components[0]);
		if (day <= 0 || day >= 32) {
			return false;
		}

		int month = Integer.parseInt(components[1]);
		if ((month <= 0) || (month >= 13)) {
			return false;
		}
		if (month == 2 || month == 4 || month == 6 || month == 9 || month == 11) {
			if (day >= 31) {
				return false;
			}
		}

		int year = Integer.parseInt(components[2]);
		if (leapYear(year, month, day)) {
			if (month == 2 && day >= 30) {
				return false;
			}
		} else {
			// not a leap
			if (month == 2 && day >= 29) {
				return false;
			}
		}

		return true;
	}

	private String dateBreakdown(String date) {
		if (!validateDate(date)) {
			return "invalid";
		}
		String[] components = date.split("-");
		String result = new String();

		for (int i = 0; i < components.length; i++) {
			components[i] = String.format("%02d", Integer.parseInt(components[i]));
			result += components[i] + " ";
		}
		return result.trim();
	}

	private boolean leapYear(int year, int month, int day) {
		boolean isLeapYear = ((year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0));
		return isLeapYear;
	}

}
