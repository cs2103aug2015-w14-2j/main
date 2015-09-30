import java.util.ArrayList;

public class Parser {

	public ArrayList<String> evaluateInput(String line) {

		ArrayList<String> test = new ArrayList<String>();
 		String[] commands = new String[6];
		commands = line.split(" ");

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
		returnString.add(commands[0]);
		returnString.add(commands[1]);

		//edit start time //store start time
		returnString.addAll(timeBreakdown(commands[2]));
		
		// edit start date// store start date
		returnString.addAll(dateBreakdown(commands[3]));
		
		// edit end time // store end time
		returnString.addAll(timeBreakdown(commands[4]));

		// edit the end date// store the end date
		returnString.addAll(dateBreakdown(commands[5]));
		

		

		return returnString;

	}
	private ArrayList<String> timeBreakdown(String time){
		ArrayList<String> result = new ArrayList<String>();
		String[] components = time.split(":");
		Integer hour = Integer.parseInt(components[0]);
		if(components[1].contains("am")){
			components[0] = String.format("%02d", hour);
			result.add(components[0]);
		}
		else{
			hour+= 12;
			components[0]  = hour.toString();
			result.add(components[0]);
		}
		components[1] =components[1].replaceAll("[^\\d.]", "");
		components[1] = String.format("%02d", Integer.parseInt(components[1]));
		result.add(components[1]);
		return result;
	}
	
	private ArrayList<String> dateBreakdown(String date){
		ArrayList<String> result = new ArrayList <String>();
		String[] components = date.split("-");
		for (int i = 0 ; i < components.length; i++){
			components[i] = String.format("%02d", Integer.parseInt(components[i]));
			result.add(components[i]);
		}
		return result; 
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
