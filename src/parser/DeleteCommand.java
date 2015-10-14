package parser;

import java.util.Objects;

public class DeleteCommand extends AbstractCommand{
	
	private Type type;
	private int index;
	private String searchKeyword;
	private Scope scope;
	
	protected static enum Scope {
		ALL, DONE, UNDONE;
	}
	
	private static enum Type {
		INDEX, SEARCHKEYWORD, SCOPE;
	}
	
	DeleteCommand(int index) {
		this.type = Type.INDEX;
		this.index = index;
	}
	
	DeleteCommand(String searchKeyword) {
		this.type = Type.SEARCHKEYWORD;
		this.searchKeyword = searchKeyword;
	}
	
	DeleteCommand(Scope scope) {
		this.type = Type.SCOPE;
		this.scope = scope;
	}

	public Type getType() {
		return this.type;
	}
	
	public int getIndex() {
		return this.index;
	}

	public String getSearchKeyword() {
		return this.searchKeyword;
	}
	
	public Scope getScope() {
		return this.scope;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DeleteCommand)) {
			return false;
		} else {
			DeleteCommand that = (DeleteCommand) obj;
			return this.getType().equals(that.getType())
					&& this.getIndex() == that.getIndex()
					&& Objects.equals(this.getSearchKeyword(), that.getSearchKeyword())
					&& Objects.equals(this.getScope(), that.getScope());
		}
	}
	
}
