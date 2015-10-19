package shared.command;

public class HelpCommand extends AbstractCommand {
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof HelpCommand)) {
			return false;
		} else {
			return true;
		}
	}
}
