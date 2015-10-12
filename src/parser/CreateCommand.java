package parser;

import java.time.LocalDateTime;
import java.util.Objects;

public class CreateCommand extends AbstractCommand {

	private String taskName;
	private Type taskType;
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;

	private static enum Type {
		FLOATING, DEADLINE, BOUNDED;
	}

	CreateCommand(String name) {
		taskName = name;
		startDateTime = null;
		endDateTime = null;
		taskType = Type.FLOATING;
	}

	CreateCommand(String name, LocalDateTime end) {
		taskName = name;
		startDateTime = null;
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
			return Objects.equals(this.getTaskType(), that.getTaskType())
					&& Objects.equals(this.getTaskName(), that.getTaskName())
					&& Objects.equals(this.getStartDateTime(), that.getStartDateTime())
					&& Objects.equals(this.getEndDateTime(), that.getEndDateTime());
		}
	}

}
