import java.time.LocalDateTime;

public class BoundedTask extends AbstractTask {

	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;

	public BoundedTask(String name, String startTime, String startDate,
			String stopTime, String stopDate) {
		super(name);
		startDateTime = LocalDateTime.parse(startDate + " " + startTime,
				DTFormatter);
		endDateTime = LocalDateTime.parse(stopDate + " " + stopTime,
				DTFormatter);
	}
	
	public String getStartDate() {
		return startDateTime.toLocalDate().toString();
	}
	
	public String getStartTime() {
		return startDateTime.toLocalTime().toString();
	}
	
	public String getEndDate() {
		return endDateTime.toLocalDate().toString();
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
}
