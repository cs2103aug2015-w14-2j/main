package parser;

public class InvalidCommand extends AbstractCommand {
	
	public boolean equals(Object obj) {
		if (!(obj instanceof InvalidCommand)) {
			return false;
		} else {
			return true;
		}
	}
	
}
