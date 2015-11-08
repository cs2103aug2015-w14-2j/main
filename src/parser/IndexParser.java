package parser;

import java.util.ArrayList;

//@@author A0131188H
public class IndexParser {
	private ArrayList<String> args;
	
	public IndexParser(ArrayList<String> args) {
		this.args = args;
	}
	
	/**
	 * Checks if keyword is in args
   * Returns index of first occurrence of keyword if true; 
   * Returns -1 if false
   */
	protected int getIndexOfFirst(String keyword) {
		int index = -1;
		for (int i = 0; i < args.size(); i++) {
			if (args.get(i).toLowerCase().equals(keyword)) {
				index = i;
				break;
			}
		}
		return index;
	}
	
	/**
	 * Checks if keyword is in args
   * Returns index of last occurrence of keyword if true; 
   * Returns -1 if false
   */
	protected int getIndex(String keyword) {
		int index = -1;
		for (int i = 0; i < args.size(); i++) {
			if (args.get(i).toLowerCase().equals(keyword)) {
				index = i;
			}
		}
		return index;
	}
	
	/**
	 * Checks if keyword1 and keyword2 are adjacent elements in args
   * Returns index of keyword1 if true; Returns -1 if false
   */
	protected int getIndex(String keyword1, String keyword2) {
		int index = -1;
		for (int i = 0; i < args.size(); i++) {
			if (i + 1 < args.size() && 
					args.get(i).toLowerCase().equals(keyword1) && 
					args.get(i + 1).toLowerCase().equals(keyword2)) {
				index = i;
			}
		}
		return index;
	}	
	
}
