package parser;

import java.time.LocalDateTime;
import java.util.Objects;

public class DisplayCommand extends AbstractCommand{
	
	private IdentifierType identifierType;
	private String taskName;
	private Type taskKeyword;
	private LocalDateTime taskDate;
	
	protected static enum Type {
		ALL, DONE, UNDONE;
	}
	
	private static enum IdentifierType {
		TASK, DATE, KEY;
	}
	
	DisplayCommand(String taskIdentifier) {
		identifierType = IdentifierType.TASK;
		taskName = taskIdentifier;
	}

	DisplayCommand(LocalDateTime date) {
		identifierType = IdentifierType.DATE;
		taskDate = date;
	}
	
	DisplayCommand(Type type) {
		identifierType = IdentifierType.KEY;
		taskKeyword = type;
	}
	
	public IdentifierType getIdentifierType() {
		return identifierType;
	}
	
	public String getTaskName() {
		return taskName;
	}
	
	public Type getTaskKeyword() {
		return taskKeyword;
	}
	
	public LocalDateTime getTaskDate() {
		return taskDate;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DisplayCommand)) {
			return false;
		} else {
			DisplayCommand that = (DisplayCommand) obj;
			return this.getIdentifierType().equals(that.getIdentifierType())
					&& Objects.equals(this.getTaskDate(), that.getTaskDate())
					&& Objects.equals(this.getTaskName(), that.getTaskName())
					&& Objects.equals(this.getTaskKeyword(), that.getTaskKeyword());
		}
	}
	
}
