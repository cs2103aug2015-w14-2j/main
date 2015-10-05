import java.util.ArrayList;


public class FloatingTask extends AbstractTask {
	
	public FloatingTask(String name) {
		super(name);
	}
	
	public String toString() {
		return getName();
	}
	
	public ArrayList<String> toArray() {
		ArrayList<String> returnArray = new ArrayList<String>();
		returnArray.add(getName());
		returnArray.add("");
		returnArray.add("");
		returnArray.add("");
		returnArray.add("");
		
		return returnArray;
	}
	
}