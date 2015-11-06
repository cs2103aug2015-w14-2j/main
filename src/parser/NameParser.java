package parser;

import java.util.ArrayList;

public class NameParser {
	private ArrayList<String> args;
	
	public NameParser(ArrayList<String> args) {
		this.args = args;
	}
	
	protected String getName(int stop) {
		String output = "";
		for (int i = 0; i < stop; i++) {
			output += args.get(i) + " ";
		}
		return removeSlash(output.trim());
	}
	
	protected String getName(int start, int stop) {
		String output = "";
		for (int i = start; i < stop; i++) {
			output += args.get(i) + " ";
		}
		return removeSlash(output.trim());
	}
	
	protected String getNameWithSlash(int stop) {
		String output = "";
		for (int i = 0; i < stop; i++) {
			output += args.get(i) + " ";
		}
		return output.trim();
	}
	
	private String removeSlash(String str) {
		return str.replace("/", "");
	}

}
