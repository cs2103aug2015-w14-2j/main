package logic;

import shared.Output;

//@@author A0124828B

public interface LogicInterface {

	public Output processInput(String command);
	
	public Output getLastDisplayed();
	
	public Output loadDefaultView();
}
