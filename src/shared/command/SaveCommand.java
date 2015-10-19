package shared.command;

public class SaveCommand extends AbstractCommand {
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SaveCommand)) {
			return false;
		} else {
			return true;
		}
	}
}
