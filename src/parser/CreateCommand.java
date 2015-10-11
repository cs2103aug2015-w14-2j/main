package parser;

import java.time.LocalDateTime;

public class CreateCommand extends AbstractCommand{

	private String taskName;
	private Type taskType;
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;
	
	private static enum Type {
		FLOATING,
		DEADLINE,
		BOUNDED;
	}
	
	CreateCommand(String name) {
		taskName = name;
		taskType = Type.FLOATING;
		startDateTime = null;
		endDateTime = null;
	}
	
	CreateCommand(String name, LocalDateTime end) {
		taskName = name;
		endDateTime = end;
		taskType = Type.DEADLINE;
	}
	
	CreateCommand(String name, LocalDateTime start, LocalDateTime end) {
		taskName = name;
		startDateTime = start;
		endDateTime = end;
		taskType = Type.BOUNDED;
	}

	public String getTaskName() {
		return taskName;
	}
	
	public Type getTaskType() {
		return taskType;
	}

	public LocalDateTime getStartDateTime() {
		return startDateTime;
	}

	public LocalDateTime getEndDateTime() {
		return endDateTime;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CreateCommand)) {
			return false;
		} else {
			CreateCommand that = (CreateCommand) obj;
			if (this.getTaskType().equals(that.getTaskType())) {
				if (this.getTaskType().equals(Type.FLOATING)) {
					return this.getTaskName().equals(that.getTaskName());
				} else if (this.getTaskName().equals(Type.DEADLINE)) {
					return this.getTaskName().equals(that.getTaskName()) && 
								 this.getEndDateTime().equals(that.getEndDateTime());
				} else if (this.getTaskName().equals(Type.BOUNDED)) {
					return this.getTaskName().equals(that.getTaskName()) && 
								 this.startDateTime.equals(that.getStartDateTime()) &&
							   this.getEndDateTime().equals(that.getEndDateTime());
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}
	
}
