package shared.command;

import java.time.LocalDateTime;
import java.util.Objects;

//@@author A0124828B
public class CreateCommand extends AbstractCommand {

	private Type taskType;
	private String taskName;
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;
	private String undoMessage = "\"create\" action has been undone!";

	public static enum Type {
		FLOATING, DEADLINE, BOUNDED;
	}

	public CreateCommand(String taskName) {
		this.taskType = Type.FLOATING;
		this.taskName = taskName;
	}

	public CreateCommand(String taskName, LocalDateTime endDateTime) {
		this.taskType = Type.DEADLINE;
		this.taskName = taskName;
		this.endDateTime = endDateTime;
	}

	public CreateCommand(String taskName, LocalDateTime startDateTime, LocalDateTime endDateTime) {
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

	public String getUndoMessage() {
		return undoMessage;
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
