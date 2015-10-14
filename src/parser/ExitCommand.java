package parser;

public class ExitCommand extends AbstractCommand {
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ExitCommand)) {
			return false;
		} else {
			return true;
		}
	}
}
