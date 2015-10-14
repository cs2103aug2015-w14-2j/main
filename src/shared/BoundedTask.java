package shared;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class BoundedTask extends AbstractTask {

	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;

	public BoundedTask(String name, LocalDateTime startDateTime, LocalDateTime endDateTime) {
		super(name);
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime; 
	}
	
	public String getStartDate() {
		String startDate = padWithZero(startDateTime.getDayOfMonth()) + "-"
				+ padWithZero(startDateTime.getMonthValue()) + "-"
				+ startDateTime.getYear();
		return startDate;
	}
	
	public String getStartTime() {
		return startDateTime.toLocalTime().toString();
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

	public void setStartDate(String newStartDate) {
		String oldStartTime = padWithZero(startDateTime.getHour()) + " "
				+ padWithZero(startDateTime.getMinute());
		startDateTime = LocalDateTime.parse(newStartDate + " " + oldStartTime,
				DTFormatter);
	}

	public void setStartTime(String newStartTime) {
		String oldStartDate = padWithZero(startDateTime.getDayOfMonth()) + " "
				+ padWithZero(startDateTime.getMonthValue()) + " "
				+ startDateTime.getYear();
		startDateTime = LocalDateTime.parse(oldStartDate + " " + newStartTime,
				DTFormatter);
	}
	
	public void setEndDate(String newEndDate) {
		String oldEndTime = padWithZero(endDateTime.getHour()) + " "
				+ padWithZero(endDateTime.getMinute());
		endDateTime = LocalDateTime.parse(newEndDate + " " + oldEndTime,
				DTFormatter);
	}

	public void setEndTime(String newEndTime) {
		String oldEndDate = padWithZero(endDateTime.getDayOfMonth()) + " "
				+ padWithZero(endDateTime.getMonthValue()) + " "
				+ endDateTime.getYear();
		endDateTime = LocalDateTime.parse(oldEndDate + " " + newEndTime,
				DTFormatter);
	}
	
	public String toString() {
		return getName() + " " + getStartTime() + " " + String.format("%02d", startDateTime.getDayOfMonth()) + "-" + String.format("%02d", startDateTime.getMonthValue()) + "-" + startDateTime.getYear() + " " +
				getEndTime() + " " + String.format("%02d", endDateTime.getDayOfMonth()) + "-" + String.format("%02d", endDateTime.getMonthValue()) + "-" + endDateTime.getYear();
	}
	
	public ArrayList<String> toArray() {
		ArrayList<String> returnArray = new ArrayList<String>();
		returnArray.add(getName());
		returnArray.add(getStartTime());
		returnArray.add(String.format("%02d", startDateTime.getDayOfMonth()) + "-" + String.format("%02d", startDateTime.getMonthValue()) + "-" + startDateTime.getYear());
		returnArray.add(getEndTime());
		returnArray.add(String.format("%02d", endDateTime.getDayOfMonth()) + "-" + String.format("%02d", endDateTime.getMonthValue()) + "-" + endDateTime.getYear());
		
		return returnArray;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BoundedTask)) {
			return false;
		} else {
			BoundedTask that = (BoundedTask) obj;
			return Objects.equals(this.getName(), that.getName()) &&
					Objects.equals(this.getStatus(), that.getStatus()) &&
					Objects.equals(this.getStartDate(), that.getStartDate()) &&
					Objects.equals(this.getStartTime(), that.getStartTime()) &&
					Objects.equals(this.getEndDate(), that.getEndDate()) &&
					Objects.equals(this.getEndTime(), that.getEndTime());
		}
	}
}