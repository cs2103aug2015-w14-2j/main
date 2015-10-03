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
		returnArray.set(1, getName());
		returnArray.set(2, null);
		returnArray.set(3, null);
		returnArray.set(4, null);
		returnArray.set(5, null);
		
		return returnArray;
	}
	
}