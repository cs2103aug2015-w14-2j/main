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
			storage += "|" + task.toString();
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
		  String[] tasks = storage.split("|");
		  System.out.println(tasks[0]);
		  System.out.println(tasks[1]);
		  
			return taskList;
		} catch (IOException e) {
			e.printStackTrace();
			throw new Error("hello!");
		}
	}
}
