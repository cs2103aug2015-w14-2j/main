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
	
	public Type getTasktype() {
		return taskType;
	}

	public LocalDateTime getStartDateTime() {
		return startDateTime;
	}

	public LocalDateTime getEndDateTime() {
		return endDateTime;
	}

	
}
