package shared.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class DeadlineTask extends AbstractTask {

	private LocalDateTime endDateTime;
	
	private boolean isOverdue = false;

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

	public String getFriendlyEndTime() {
		String[] timeParts = this.getEndTime().split(":");
		int hourValue = Integer.parseInt(timeParts[0]);
		String timePeriod = "am";
		
		if (hourValue == 12) {
			timePeriod = "pm";
		} else if (hourValue > 12) {
			hourValue -= 12;
			timePeriod = "pm";
		} else if (hourValue == 0) {
			hourValue = 12;
		}
		
		if (timeParts[1].equals("00")) {
			return String.valueOf(hourValue) + timePeriod;
		} else {
			return String.valueOf(hourValue) + ":" + timeParts[1] + timePeriod;
		}
	}
	
	public boolean isOverdue() {
		return this.isOverdue;
	}
	
	public void setOverdue(boolean state) {
		this.isOverdue = state;
	}

	public String toString() {
		return this.getStatus().toString() + "`" + this.getName() + "`"
				+ String.format("%02d", endDateTime.getDayOfMonth()) + " "
				+ String.format("%02d", endDateTime.getMonthValue()) + " "
				+ endDateTime.getYear() + " "
				+ String.format("%02d", endDateTime.getHour()) + " "
				+ String.format("%02d", endDateTime.getMinute());
		
	}

	// Need to Optimize this code!
	public ArrayList<String> toArray() {
		LocalDate today = LocalDate.now();
		
		ArrayList<String> returnArray = new ArrayList<String>();
		returnArray.add(getName());
		
		returnArray.add("");
		returnArray.add("");
		returnArray.add("");
		returnArray.add("");
		returnArray.add("");
		
		returnArray.add(getFriendlyEndTime());
		returnArray.add((endDateTime.getDayOfWeek().toString()).substring(0, 3));
		if (endDateTime.toLocalDate().equals(today)) {
			returnArray.add("TODAY");
			returnArray.add("");
			returnArray.add("");
		} else {
			returnArray.add(String.valueOf(endDateTime.getDayOfMonth()));
			returnArray.add((endDateTime.getMonth().toString()).substring(0, 3));
			returnArray.add(String.valueOf(endDateTime.getYear()));
		}
		
		returnArray.add((this.getStatus()).toString());
		returnArray.add(String.valueOf(isOverdue));

		return returnArray;
	}

	@Override
	public int compareTo(AbstractTask task) {
		if (task instanceof FloatingTask) {
			return -1;
		} else if (task instanceof DeadlineTask) {
			return this.getEndDateTime().compareTo(
					((DeadlineTask) task).getEndDateTime());
		} else {
			return this.getEndDateTime().compareTo(
					((BoundedTask) task).getStartDateTime());
		}
	}


	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DeadlineTask)) {
			return false;
		} else {
			DeadlineTask that = (DeadlineTask) obj;
			return Objects.equals(this.getName(), that.getName())
					&& Objects.equals(this.getStatus(), that.getStatus())
					&& Objects.equals(this.getEndDate(), that.getEndDate())
					&& Objects.equals(this.getEndTime(), that.getEndTime());
		}
	}

	@Override
	public AbstractTask clone() {
		DeadlineTask newTask = new DeadlineTask(this.getName(), this.endDateTime);
		newTask.setStatus(this.getStatus());
		newTask.setOverdue(isOverdue);
		return newTask;
	}
}