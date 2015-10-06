import java.util.ArrayList;

public class Output {
	public String returnMessage;
	public ArrayList<ArrayList<String>> outputArrayList = new ArrayList();
	
	public void setReturnMessage(String message) {
		returnMessage = message;
	}
	
	public String getReturnMessage() {
		return returnMessage;
	}
	
	public ArrayList<ArrayList<String>> getTasks() {
		return outputArrayList;
	}
	
	public void setOutput(ArrayList<ArrayList<String>> tasksArrayList) {
		outputArrayList = tasksArrayList;
	}
	
}
