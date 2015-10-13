package parser;

import java.time.LocalDateTime;
import java.util.Objects;

public class CreateCommand extends AbstractCommand {

	private Type taskType;
	private String taskName;
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;

	private static enum Type {
		FLOATING, DEADLINE, BOUNDED;
	}

	CreateCommand(String taskName) {
		this.taskType = Type.FLOATING;
		this.taskName = taskName;
	}

	CreateCommand(String taskName, LocalDateTime endDateTime) {
		this.taskType = Type.DEADLINE;
		this.taskName = taskName;
		this.endDateTime = endDateTime;
	}

	CreateCommand(String taskName, LocalDateTime startDateTime, LocalDateTime endDateTime) {
		this.taskType = Type.BOUNDED;
		this.taskName = taskName;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;

	}

	public Type getTaskType() {
		return this.taskType;
	}
	
	public String getTaskName() {
		return this.taskName;
	}

	public LocalDateTime getStartDateTime() {
		return this.startDateTime;
	}

	public LocalDateTime getEndDateTime() {
		return this.endDateTime;
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
