package logic.action;

import java.util.logging.Level;
import java.util.logging.Logger;

import shared.Output;
import shared.SharedLogger;

//@@author A0124828B
public class UIAction extends AbstractAction {
	private Logger logger = SharedLogger.getInstance().getLogger();
	
	//UIAction has no return message
	public Output execute() {
		logger.log(Level.INFO, "Executing UIAction");
		return new Output(" ");
	}

}
