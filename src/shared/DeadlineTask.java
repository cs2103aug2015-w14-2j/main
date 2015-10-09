package shared;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class DeadlineTask extends AbstractTask {

	private LocalDateTime endDateTime;

	public DeadlineTask(String name, String stopTime, String stopDate) {
		super(name);
		endDateTime = LocalDateTime.parse(stopDate + " " + stopTime, DTFormatter);
	}

	public String getEndDate() {
		String endDate = padWithZero(endDateTime.getDayOfMonth()) + "-"
				+ padWithZero(endDateTime.getMonthValue()) + "-"
				+ endDateTime.getYear();
		return endDate;
	}

	public String getEndTime() {
		return endDateTime.toLocalTime().toString();
	}

	public void setEndDate(String newEndDate) {
		String oldEndTime = padWithZero(endDateTime.getHour()) + " " + padWithZero(endDateTime.getMinute());
		endDateTime = LocalDateTime.parse(newEndDate + " " + oldEndTime, DTFormatter);
	}

	public void setEndTime(String newEndTime) {
		String oldEndDate = padWithZero(endDateTime.getDayOfMonth()) + " " + padWithZero(endDateTime.getMonthValue()) + " "
			+ endDateTime.getYear();
		endDateTime = LocalDateTime.parse(oldEndDate + " " + newEndTime, DTFormatter);
	}

	public String toString() {
		return getName() + " " + getEndTime() + " " + String.format("%02d", endDateTime.getDayOfMonth()) + "-" + String.format("%02d", endDateTime.getMonthValue()) + "-" + endDateTime.getYear();
	}
	
	public ArrayList<String> toArray() {
		ArrayList<String> returnArray = new ArrayList<String>();
		returnArray.add(getName());
		returnArray.add("");
		returnArray.add("");
		returnArray.add(getEndTime());
		returnArray.add(String.format("%02d", endDateTime.getDayOfMonth()) + "-" + String.format("%02d", endDateTime.getMonthValue()) + "-" + endDateTime.getYear());
		
		return returnArray;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DeadlineTask)) {
			return false;
		} else {
			DeadlineTask that = (DeadlineTask) obj;
			return (this.getName().equals(that.getName()) &&
					  	this.getStatus().equals(that.getStatus()) &&
							this.getEndDate().equals(that.getEndDate()) &&
							this.getEndTime().equals(that.getEndTime()));
		}
	}
}