package parser;

import java.util.ArrayList;

import shared.Constants;

//@@author A0131188H
public class NameParser {
	private ArrayList<String> args;

	public NameParser(ArrayList<String> args) {
		this.args = args;
	}

	protected String getName(int stop) {
		String output = Constants.EMPTY;
		for (int i = 0; i < stop; i++) {
			output += args.get(i) + Constants.WHITESPACE;
		}
		return removeSlash(output.trim());
	}

	protected String getName(int start, int stop) {
		String output = Constants.EMPTY;
		for (int i = start; i < stop; i++) {
			output += args.get(i) + Constants.WHITESPACE;
		}
		return removeSlash(output.trim());
	}

	protected String getNameWithSlash(int stop) {
		String output = Constants.EMPTY;
		for (int i = 0; i < stop; i++) {
			output += args.get(i) + Constants.WHITESPACE;
		}
		return output.trim();
	}

	/**
	 * Removes slash character from every element in args
	 */
	protected ArrayList<String> removeSlash(ArrayList<String> args) {
		for (int i = 0; i < args.size(); i++) {
			args.set(i, removeSlash(args.get(i)));
		}
		return args;
	}

	private String removeSlash(String str) {
		return str.replace(Constants.SLASH, Constants.EMPTY);
	}

}
