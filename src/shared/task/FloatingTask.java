package shared.task;

import java.util.ArrayList;
import java.util.Objects;

public class FloatingTask extends AbstractTask {

	public FloatingTask(String name) {
		super(name);
	}

	public String toString() {
		return this.getStatus().toString() + "`" + this.getName();
	}
	
	// Need to Optimize this code!
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
		returnArray.add((this.getStatus()).toString());
		returnArray.add("");

		return returnArray;
	}
	
	@Override 
	public int compareTo(AbstractTask task) {
		if (task instanceof FloatingTask) {
			return 0;
		} else {
			return 1;
		}
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

	@Override
	public AbstractTask clone() {
		FloatingTask newTask = new FloatingTask(this.getName());
		newTask.setStatus(this.getStatus());
		return newTask;
	}
}