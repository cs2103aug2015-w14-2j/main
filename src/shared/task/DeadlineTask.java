package shared.task;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class DeadlineTask extends AbstractTask {

	private LocalDateTime endDateTime;

	public DeadlineTask(String name, LocalDateTime endDateTime) {
		super(name);
		this.endDateTime = endDateTime;
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
	
	public LocalDateTime getEndDateTime() {
		return endDateTime;
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
		returnArray.add((this.getStatus()).toString());
		
		return returnArray;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DeadlineTask)) {
			return false;
		} else {
			DeadlineTask that = (DeadlineTask) obj;
			return Objects.equals(this.getName(), that.getName()) &&
					Objects.equals(this.getStatus(), that.getStatus()) &&
					Objects.equals(this.getEndDate(), that.getEndDate()) &&
					Objects.equals(this.getEndTime(), that.getEndTime());
		}
	}
}