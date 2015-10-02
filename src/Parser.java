
import java.util.ArrayList;

public class Parser {

	private static final String MESSAGE_ERROR = "INVALID INPUT";
	//private static final ArrayList<String> STRING_ERROR = new ArrayList<String>(); 
	private ArrayList<String> returnString;

	public ArrayList<String> evaluateInput(String line) {
		String[] temp = line.split(" ");
		ArrayList<String> commands = new ArrayList<String>();
		for (int i = 0; i < temp.length; i++) {
			commands.add(temp[i]);
		}

		switch (commands.get(0)) {
		case "create": {
			System.out.println("Create selected");
			return create(commands);

		}
		case "display": {
			System.out.println("Display selected");
			return display(commands);
		}
		case "delete": {
			System.out.println("Delete selected");
			return delete(commands);

		}
			/*
			 * case "edit": { System.out.println("Edit selected"); String[]
			 * editSplit = new String[2]; editSplit = commands[0].split('-'); //
			 * split to the different types break;
			 * 
			 * }
			 */
		default: {
			System.out.println(MESSAGE_ERROR);

		}
		
		}

	}

	public ArrayList<String> create(ArrayList<String> commands) {
		// ArrayList<String> returnString = new ArrayList<String>();
		// String temp = new String() ; // to store the computed string after
		// patching parts together
		// store the event name
		returnString.add(commands.get(0));
		int taskType = 0;
		// 0 for floating task (default), 1 for deadline tasks , 2 for bounded
		// tasks

		// find a way to identify the different task using keywords
		for (int i = commands.size(); i > 1; i--) {
			if (commands.get(i).equals("to")) {
				if (commands.get(i - 2).equals("from")) {
					// it's a bounded task
					taskType = 2;
					break;
				}
			} else if (commands.get(i).equals("by")) {
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

	public ArrayList<String> boundedTask(ArrayList<String> commands) {
		ArrayList<String> result = new ArrayList<String>();
		String temp = new String(); 
		result.add(commands.get(0)); // add the 'create' string
		int i = 1; 
			while(!commands.get(i).equals("from")){
				temp+= commands.get(i);// create event string; 
				i++;
			}
		result.add(temp.trim()); // add the event string
		i++; result.add(timeBreakdown(commands.get(i))); // add 'from time' string
		i++; result.add(dateBreakdown(commands.get(i))); // add 'from date' string
		i++; //ignore the word 'to'
		i++; result.add(timeBreakdown(commands.get(i))); // add 'to time' string
		i++; result.add(dateBreakdown(commands.get(i))); // add 'to date' string
		return result;
	}

	public ArrayList<String> deadlineTask(ArrayList<String> commands) {
		ArrayList<String> result = new ArrayList<String>();
		String temp = new String(); 
		result.add(commands.get(0)); // add the 'create' string
		int i = 1; 
			while(!commands.get(i).equals("by")){
				temp+= commands.get(i);// create event string; 
				i++;
			}
		result.add(temp.trim()); // add the event string //word 'by' is already ignored with the value of current i
		i++; result.add(null); // add 'from time' string
		i++; result.add(null); // add 'from date' string
		
		i++; result.add(timeBreakdown(commands.get(i))); // add 'to time' string
		i++; result.add(dateBreakdown(commands.get(i))); // add 'to date' string
		return result;
	}

	public ArrayList<String> floatingTask(ArrayList<String> commands) {
		ArrayList<String> result = new ArrayList<String>();
		String temp = new String(); 
		result.add(commands.get(0)); // add the 'create' string
		
		for (int i = 1; i< commands.size(); i++){
			temp+= commands.get(i);
		}
		result.add(temp);
		return result;
	}

	private String timeBreakdown(String time) {
		String result = new String();
		String[] components = time.split(":");
		Integer hour = Integer.parseInt(components[0]);
		if (components[1].contains("am")) {
			components[0] = String.format("%02d", hour);
			result += components[0] + " ";
		} else {
			hour += 12;
			components[0] = hour.toString();
			result+= components[0] + " ";
		}
		components[1] = components[1].replaceAll("[^\\d.]", "");
		components[1] = String.format("%02d", Integer.parseInt(components[1]));
		result += components[1];;
		return result;
	}

	private String dateBreakdown(String date) {
		String result = new String();
		String[] components = date.split("-");
		for (int i = 0; i < components.length; i++) {
			components[i] = String.format("%02d", Integer.parseInt(components[i]));
			result += components[i] + " ";
		}
		return result.trim();
	}

	public ArrayList<String> display(ArrayList<String> commmands) {
		ArrayList<String> resultString = new ArrayList<String>();
		System.out.println("this is display not done yet");
		return resultString;

	}

	public ArrayList<String> delete(ArrayList<String> commands) {
		ArrayList<String> resultString = new ArrayList<String>();
		System.out.println("this is delete not done yet");
		return resultString;
	}

	public ArrayList<String> edit(String[] commands) {
		ArrayList<String> resultString = new ArrayList<String>();
		System.out.println("this is delete not done yet");
		return resultString;
	}
}
