package logic;

import java.util.ArrayList;

import shared.task.AbstractTask;
import storage.Storage;

//@@author A0124828B
public class StorageStub extends Storage {
	@Override
	public ArrayList<AbstractTask> read() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		return mockTaskList;
	}
}
