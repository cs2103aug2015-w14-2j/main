package logic;

//@@author A0124828B

public class ExtendedBoolean {
	private boolean state;
	
	public ExtendedBoolean(boolean state) {
		this.state = state;
	}
	
	public void setTrue() {
		this.state = true;
	}
	
	public void setFalse() {
		this.state = false;
	}
	
	public boolean getState() {
		return this.state;
	}
}
