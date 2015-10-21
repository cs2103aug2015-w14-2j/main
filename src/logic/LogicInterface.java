package logic;

import shared.Output;

public interface LogicInterface {

	public Output processInput(String command);
	
	public Output getLastDisplayed();
}
