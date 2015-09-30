import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Storage {	
	public static void write(ArrayList<AbstractTask> taskList) {
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
			FileWriter writer = new FileWriter("src/test.json");
			writer.write(storage);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<AbstractTask> read() {
		try {
			FileInputStream in = new FileInputStream("src/test.json");
		  BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			
		  ArrayList<AbstractTask> taskList = new ArrayList<AbstractTask>();
		  String storage = reader.readLine();
		  System.out.println(storage);
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
		  
			return taskList;
		} catch (IOException e) {
			e.printStackTrace();
			throw new Error("hello!");
		}
	}
}
