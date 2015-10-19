package storage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import shared.AbstractTask;
import shared.BoundedTask;
import shared.DeadlineTask;
import shared.FloatingTask;

public class Storage {
	//there should be a default file path.
	//private filePath string;
	//check the validity of the new file path, if valid then change the file path. (STFW)
	//write 
	public void write(ArrayList<AbstractTask> taskList) {
		String storageString = getStorageString(taskList);
		
		try {
			FileWriter writer = new FileWriter("src/storage.txt");
			writer.write(storageString);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new Error("Unable to write to storage");
		}
	}
	private String getStorageString(ArrayList<AbstractTask> taskList) {
		String storageString = "";
		
		for (int i = 0; i < taskList.size(); i++)  {
			AbstractTask task = taskList.get(i);
			if (i == 0) {
				storageString += task.toString();
			} else {
				storageString += "," + task.toString();
			}
		}
		
		return storageString;
	}
	//read(file directory)
	public ArrayList<AbstractTask> read() {	  
		try {
			ArrayList<AbstractTask> taskList = new ArrayList<AbstractTask>();
			//change to not fixed
			File file = new File("src/storage.txt");
		  if (!file.exists()) {
		  	file.createNewFile();
		  }
		  
			FileInputStream stream = new FileInputStream(file);
		  BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		  
		  if (stream.available() != 0) {
			  String storageString = reader.readLine();
			  taskList = getTaskList(storageString);
		  }
		  
		  reader.close();
		  return taskList;
		} catch (IOException e) {
			e.printStackTrace();
			throw new Error("Unable to read from storage");
		}
	}
	
	
	//create function to put event name into one para 
	private ArrayList<AbstractTask> getTaskList(String storageString) {
		DateTimeFormatter DTFormatter = DateTimeFormatter.ofPattern("dd MM yyyy HH mm");
		ArrayList<AbstractTask> taskList = new ArrayList<AbstractTask>();
		String[] tasks = storageString.split(",");
	  
	  
		  for (int i = 0; i < tasks.length; i++) {
		  	String task = tasks[i];
		  	String[] taskParts = task.split(" ");
		  	if (taskParts.length == 1) {
		  		taskList.add(new FloatingTask(taskParts[0]));
		  	} else if (taskParts.length == 3) {
		  		LocalDateTime endDateTime = LocalDateTime.parse(taskParts[2].replace("-", " ") + " " + taskParts[1].replace(":", " "), DTFormatter);
		  		taskList.add(new DeadlineTask(taskParts[0], endDateTime));
		  	} else if (taskParts.length == 5) {
		  		LocalDateTime startDateTime = LocalDateTime.parse(taskParts[2].replace("-", " ") + " " + taskParts[1].replace(":", " "), DTFormatter);
		  		LocalDateTime endDateTime = LocalDateTime.parse(taskParts[4].replace("-", " ") + " " + taskParts[3].replace(":", " "), DTFormatter);
		  		taskList.add(new BoundedTask(taskParts[0], startDateTime, endDateTime));
		  	}
		  }
		  
		  return taskList;
	}
}