package shared.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class BoundedTask extends AbstractTask {

	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;

	public BoundedTask(String name, LocalDateTime startDateTime,
			LocalDateTime endDateTime) throws IllegalArgumentException {
		super(name);
		if (endDateTime.isBefore(startDateTime)) {
			throw new IllegalArgumentException(
					"Invalid: Start date time must be before End date time!");
		} else {
			this.startDateTime = startDateTime;
			this.endDateTime = endDateTime;
		}
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

	public String getFriendlyStartTime() {
		String[] timeParts = this.getStartTime().split(":");
		int hourValue = Integer.parseInt(timeParts[0]);
		String timePeriod = "am";
		if (hourValue > 12) {
			hourValue -= 12;
			timePeriod = "pm";
		} else if (hourValue == 0) {
			hourValue = 12;
		}
		return String.valueOf(hourValue) + ":" + timeParts[1] + timePeriod;
	}

	public LocalDateTime getStartDateTime() {
		return startDateTime;
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

	public String getFriendlyEndTime() {
		String[] timeParts = this.getEndTime().split(":");
		int hourValue = Integer.parseInt(timeParts[0]);
		String timePeriod = "am";
		if (hourValue > 12) {
			hourValue -= 12;
			timePeriod = "pm";
		} else if (hourValue == 0) {
			hourValue = 12;
		}
		return String.valueOf(hourValue) + ":" + timeParts[1] + timePeriod;
	}

	public LocalDateTime getEndDateTime() {
		return endDateTime;
	}

	public void setStartDate(String newStartDate) throws IllegalArgumentException {
		String oldStartTime = padWithZero(startDateTime.getHour()) + " "
				+ padWithZero(startDateTime.getMinute());
		LocalDateTime newStart = LocalDateTime.parse(newStartDate + " "
				+ oldStartTime, DTFormatter);
		if (newStart.isAfter(this.endDateTime)) {
			throw new IllegalArgumentException(
					"Invalid: Start date time must be before End date time!");
		} else {
			this.startDateTime = newStart;
		}
	}

	public void setStartTime(String newStartTime) throws IllegalArgumentException {
		String oldStartDate = padWithZero(startDateTime.getDayOfMonth()) + " "
				+ padWithZero(startDateTime.getMonthValue()) + " "
				+ startDateTime.getYear();
		LocalDateTime newStart = LocalDateTime.parse(oldStartDate + " " + newStartTime,
				DTFormatter);
		if (newStart.isAfter(this.endDateTime)) {
			throw new IllegalArgumentException(
					"Invalid: Start date time must be before End date time!");
		} else {
			this.startDateTime = newStart;
		}
	}

	public void setEndDate(String newEndDate) throws IllegalArgumentException {
		String oldEndTime = padWithZero(endDateTime.getHour()) + " "
				+ padWithZero(endDateTime.getMinute());
		LocalDateTime newEnd = LocalDateTime.parse(newEndDate + " " + oldEndTime,
				DTFormatter);
		if (newEnd.isBefore(this.startDateTime)) {
			throw new IllegalArgumentException(
					"Invalid: Start date time must be before End date time!");
		}
	}

	public void setEndTime(String newEndTime) throws IllegalArgumentException {
		String oldEndDate = padWithZero(endDateTime.getDayOfMonth()) + " "
				+ padWithZero(endDateTime.getMonthValue()) + " "
				+ endDateTime.getYear();
		LocalDateTime newEnd = LocalDateTime.parse(oldEndDate + " " + newEndTime,
				DTFormatter);
		if (newEnd.isBefore(this.startDateTime)) {
			throw new IllegalArgumentException(
					"Invalid: Start date time must be before End date time!");
		}
	}

	public String toString() {
		return getName() + " " + getStartTime() + " "
				+ String.format("%02d", startDateTime.getDayOfMonth()) + "-"
				+ String.format("%02d", startDateTime.getMonthValue()) + "-"
				+ startDateTime.getYear() + " " + getEndTime() + " "
				+ String.format("%02d", endDateTime.getDayOfMonth()) + "-"
				+ String.format("%02d", endDateTime.getMonthValue()) + "-"
				+ endDateTime.getYear();
	}

	// Need to Optimise this code!
	public ArrayList<String> toArray() {
		ArrayList<String> returnArray = new ArrayList<String>();
		returnArray.add(getName());
		returnArray.add(getFriendlyStartTime());
		returnArray.add((startDateTime.getDayOfWeek().toString()).substring(0,
				3));
		returnArray.add(String.format("%02d", startDateTime.getDayOfMonth()));
		returnArray.add((startDateTime.getMonth().toString()).substring(0, 3));
		returnArray.add(String.valueOf(startDateTime.getYear()));
		returnArray.add(getFriendlyEndTime());
		returnArray
				.add((endDateTime.getDayOfWeek().toString()).substring(0, 3));
		returnArray.add(String.format("%02d", endDateTime.getDayOfMonth()));
		returnArray.add((endDateTime.getMonth().toString()).substring(0, 3));
		returnArray.add(String.valueOf(endDateTime.getYear()));
		returnArray.add((this.getStatus()).toString());

		return returnArray;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BoundedTask)) {
			return false;
		} else {
			BoundedTask that = (BoundedTask) obj;
			return Objects.equals(this.getName(), that.getName())
					&& Objects.equals(this.getStatus(), that.getStatus())
					&& Objects.equals(this.getStartDate(), that.getStartDate())
					&& Objects.equals(this.getStartTime(), that.getStartTime())
					&& Objects.equals(this.getEndDate(), that.getEndDate())
					&& Objects.equals(this.getEndTime(), that.getEndTime());
		}
	}
}