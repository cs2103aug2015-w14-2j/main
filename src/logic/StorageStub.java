package logic;

import java.util.ArrayList;

import shared.task.AbstractTask;
import shared.task.FloatingTask;
import storage.Storage;

public class StorageStub extends Storage {
	@Override
	public ArrayList<AbstractTask> read() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		mockTaskList.add(new FloatingTask("test"));
		return mockTaskList;
	}
}
