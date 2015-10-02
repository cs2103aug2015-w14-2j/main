import java.util.ArrayList;

public class Output {
	public String returnMessage;
	public ArrayList<String> tasks = new ArrayList();
	
	public void setReturnMessage(String message) {
		returnMessage = message;
	}
	
	public String getReturnMessage() {
		return returnMessage;
	}
	
	public ArrayList<String> getTasks() {
		return tasks;
	}
	
	public Output(String returnMessage) {
		this.returnMessage = returnMessage;
	}
}
