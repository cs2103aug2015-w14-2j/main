package storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import shared.task.AbstractTask;
import shared.task.BoundedTask;
import shared.task.DeadlineTask;
import shared.task.FloatingTask;

public class Storage {
	
	
	private File locateFile() {
		File file = new File("src/storage.txt");

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new Error("Unable to create new file.");
			}
		}
		return file;

}

public void write(ArrayList<AbstractTask> taskList){
	//locate the file
	File file = locateFile();
	
	//prepare the file writer
	FileWriter writer;
	try {
		writer = new FileWriter(file.getAbsolutePath());
		for(int i = 0 ; i < taskList.size();i++){
			writer.write(toString(taskList.get(i)));
			writer.write('\n');
		}
		writer.close();
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
		throw new Error("Unable to create file writer.");
	}

}

private String toString(AbstractTask task){
	String result = new String();
	result += task.getName();
	result +=',';
	//result += ;
	//result += task.get
	return result;
}

public ArrayList<AbstractTask> read(){
	DateTimeFormatter DTFormatter = DateTimeFormatter.ofPattern("dd MM yyyy HH mm");
	//locate the file 
	File file = locateFile(); 	
	FileInputStream stream = null;
	try {
		stream = new FileInputStream(file);
	} catch (FileNotFoundException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

	//create new ArrayList to put the data back in
	ArrayList<AbstractTask> taskList = new ArrayList<AbstractTask>();
	
	try {
		while(stream.available()!=0){
		String storageString = null;
		try {
			storageString = new String(reader.readLine());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(storageString);
		String[] parts = storageString.split(",");
		if (parts.length == 1){
			taskList.add(new FloatingTask(parts[0]));
		}
		else if (parts.length == 2){
			taskList.add(new DeadlineTask(parts[0], LocalDateTime.parse(parts[1], DTFormatter)));
		}
		else if (parts.length == 3){
			taskList.add(new BoundedTask(parts[0],LocalDateTime.parse(parts[1], DTFormatter),LocalDateTime.parse(parts[2], DTFormatter)));
		}
		}
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	try {
		reader.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return taskList;
}


}