import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Storage {
	public void write(ArrayList<AbstractTask> taskList) {
		String storage = "";
		for (int i = 0; i < taskList.size(); i++)  {
			AbstractTask task = taskList.get(i);
			if (i == 0) {
				storage += task.toString();
			} else {
				storage += "," + task.toString();
			}
		}
		
		try {
			FileWriter writer = new FileWriter("src/storage.txt");
			writer.write(storage);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new Error("Unable to write to storage");
		}
	}
	
	public ArrayList<AbstractTask> read() {
		try {
		  ArrayList<AbstractTask> taskList = new ArrayList<AbstractTask>();
			File file = new File("src/storage.txt");
		  
		  if (!file.exists()) {
		  	file.createNewFile();
		  }
		  
			FileInputStream in = new FileInputStream(file);
		  BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		  
		  if (in.available() != 0) {
			  String storage = reader.readLine();
			  String[] tasks = storage.split(",");
			  for (int i = 0; i < tasks.length; i++) {
			  	String task = tasks[i];
			  	String[] taskParts = task.split(" ");
			  	if (taskParts.length == 1) {
			  		taskList.add(new FloatingTask(taskParts[0]));
			  	} else if (taskParts.length == 3) {
			  		taskList.add(new DeadlineTask(taskParts[0], taskParts[1].replace(":", " "), taskParts[2].replace("-", " ")));
			  	} else if (taskParts.length == 5) {
			  		taskList.add(new BoundedTask(taskParts[0], taskParts[1].replace(":", " "), taskParts[2].replace("-", " "), taskParts[3].replace(":", " "), taskParts[4].replace("-", " ")));
			  	}
			  }
		  }
		  
		  reader.close();
			return taskList;
		} catch (IOException e) {
			e.printStackTrace();
			throw new Error("Unable to read from storage");
		}
	}
}