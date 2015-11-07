package parser;

import java.util.ArrayList;

public class IndexParser {
	private ArrayList<String> args;
	
	public IndexParser(ArrayList<String> args) {
		this.args = args;
	}
	
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
	
	protected int getIndex(String keyword) {
		int index = -1;
		for (int i = 0; i < args.size(); i++) {
			if (args.get(i).toLowerCase().equals(keyword)) {
				index = i;
			}
		}
		return index;
	}
	
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
