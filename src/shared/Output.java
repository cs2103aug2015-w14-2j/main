package shared;
import java.util.ArrayList;
import java.util.Objects;

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
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Output)) {
			return false;
		} else {
			Output that = (Output) obj;
			return Objects.equals(this.getReturnMessage(), that.getReturnMessage())
					&& Objects.equals(this.getTasks(), that.getTasks());
		}
	}
	
}
