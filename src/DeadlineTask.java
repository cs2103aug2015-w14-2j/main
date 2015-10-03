import java.time.LocalDateTime;

public class DeadlineTask extends AbstractTask {

	private LocalDateTime endDateTime;

	public DeadlineTask(String name, String stopTime, String stopDate) {
		super(name);
		endDateTime = LocalDateTime.parse(stopDate + " " + stopTime, DTFormatter);
	}

	public String getEndDate() {
		return endDateTime.toLocalDate().toString();
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

}