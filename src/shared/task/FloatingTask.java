package shared.task;

import java.util.ArrayList;
import java.util.Objects;

public class FloatingTask extends AbstractTask {

	public FloatingTask(String name) {
		super(name);
	}

	public String toString() {
		return getName();
	}
	
	// Need to Optimise this code!
	public ArrayList<String> toArray() {
		ArrayList<String> returnArray = new ArrayList<String>();
		returnArray.add(getName());
		returnArray.add("");
		returnArray.add("");
		returnArray.add("");
		returnArray.add("");
		returnArray.add("");
		returnArray.add("");
		returnArray.add("");
		returnArray.add("");
		returnArray.add("");
		returnArray.add("");
		returnArray.add("");
		returnArray.add((this.getStatus()).toString());

		return returnArray;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FloatingTask)) {
			return false;
		} else {
			FloatingTask that = (FloatingTask) obj;

			return Objects.equals(this.getName(), that.getName())
					&& Objects.equals(this.getStatus(), that.getStatus());
		}
	}
}