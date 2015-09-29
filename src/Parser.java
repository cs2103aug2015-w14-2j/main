import java.util.ArrayList;

public class Parser {

	public ArrayList<String> evaluateInput(String line) {

		ArrayList<String> test = new ArrayList<String>();
 		String[] commands = new String[10];
		commands = line.split(" ");

		// test display
		for (int i = 0; i < commands.length; i++) {
			System.out.println(commands[i]);
		}
		switch (commands[0]) {
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
			System.out.println("ERROR");
			return test;
		
		}
		}
	}

	public ArrayList<String> create(String[] commands) {
		ArrayList<String> returnString = new ArrayList<String>();
		// store the event name
		returnString.add(commands[1]);
		// store the start time
		returnString.add(commands[2]);
		// edit the date
		String[] dateBreakdown = new String[3];
		dateBreakdown = commands[3].split("-");
		// store the date
		for (int i = 0; i < dateBreakdown.length; i++) {
			returnString.add(dateBreakdown[i]);
		}
		// store end time
		returnString.add(commands[4]);
		// reset dateBreakdown
		dateBreakdown = new String[3];
		// edit the end date
		dateBreakdown = commands[5].split("-");
		// store the end date
		for (int i = 0; i < dateBreakdown.length; i++) {
			returnString.add(dateBreakdown[i]);
		}

		return returnString;

	}

	public ArrayList<String> display(String[] commmands) {
		ArrayList<String >resultString = new ArrayList<String>();
		System.out.println("this is display not done yet");
		return resultString; 

	}

	public ArrayList<String> delete(String[] commands) {
		ArrayList<String >resultString = new ArrayList<String>();
		System.out.println("this is delete not done yet");
		return resultString; 
	}

	public ArrayList<String> edit(String[] commands){
		ArrayList<String >resultString = new ArrayList<String>();
		System.out.println("this is delete not done yet");
		return resultString; 
	}
}
