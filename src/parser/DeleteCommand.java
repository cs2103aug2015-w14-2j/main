package parser;

import java.util.Objects;

public class DeleteCommand extends AbstractCommand{
	
	private IdentifierType identifierType;
	private int index;
	private String taskName;
	private Type keyword;
	
	protected static enum Type {
		ALL, DONE, UNDONE;
	}
	
	private static enum IdentifierType {
		INDEX, NAME, KEY;
	}
	
	DeleteCommand(String taskIdentifier) {
		identifierType = IdentifierType.NAME;
		taskName = taskIdentifier;
	}

	DeleteCommand(int taskIdentifier) {
		identifierType = IdentifierType.INDEX;
		index = taskIdentifier;
	}
	
	DeleteCommand(Type type) {
		identifierType = IdentifierType.KEY;
		keyword = type;
	}
	
	public IdentifierType getIdentifierType() {
		return identifierType;
	}

	public int getIndex() {
		return index;
	}

	public String getTaskName() {
		return taskName;
	}
	
	public Type getKeyword() {
		return keyword;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DeleteCommand)) {
			return false;
		} else {
			DeleteCommand that = (DeleteCommand) obj;
			return this.getIdentifierType().equals(that.getIdentifierType())
					&& this.getIndex() == that.getIndex()
					&& Objects.equals(this.getTaskName(), that.getTaskName())
					&& Objects.equals(this.getKeyword(), that.getKeyword());
		}
	}
	
}
