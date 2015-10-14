package parser;

public class InvalidCommand extends AbstractCommand {
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof InvalidCommand)) {
			return false;
		} else {
			return true;
		}
	}
	
}
