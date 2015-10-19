package parser;

public class UndoCommand extends AbstractCommand {
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof UndoCommand)) {
			return false;
		} else {
			return true;
		}
	}
}
