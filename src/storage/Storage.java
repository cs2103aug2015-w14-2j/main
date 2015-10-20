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
				System.out.println("file created");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new Error("Unable to create new file.");
			}
		}
		return file;

	}

	public void write(ArrayList<AbstractTask> taskList) {
		// locate the file
		File file = locateFile();

		// prepare the file writer
		FileWriter writer;
		try {
			writer = new FileWriter(file.getAbsolutePath());
			for (int i = 0; i < taskList.size(); i++) {
				writer.write(toString(taskList.get(i)));
				writer.write("\r\n");

			}
			writer.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw new Error("Unable to create file writer.");
		}

	}

	private String toString(AbstractTask task) {

		ArrayList<String> wordsList = new ArrayList<String>();
		String[] words = task.toString().split(" ");

		for (String eachWord : words) {
			String[] temp = eachWord.split(":");
			if (temp.length == 2) {
				wordsList.add(',' + eachWord);
			} else {
				wordsList.add(eachWord);
			}
		}
		String result = " ";
		for (int i = 0; i < wordsList.size(); i++) {
			result += wordsList.get(i) + " ";

	
		}
		return result.trim();
	}


	public ArrayList<AbstractTask> read() {

		DateTimeFormatter DTFormatter = DateTimeFormatter.ofPattern("dd MM yyyy HH mm");
		// locate the file
		File file = locateFile();
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		// create new ArrayList to put the data back in
		ArrayList<AbstractTask> taskList = new ArrayList<AbstractTask>();
		String storageString = null;

		try {
			while ((storageString = reader.readLine()) != null) {
				String[] parts = storageString.split(",");
				if (parts.length == 1){
					taskList.add(new FloatingTask(parts[0].trim()));
				}
				else if(parts.length == 2){
					taskList.add(new DeadlineTask(parts[0].trim(), LocalDateTime.parse(changeFormat(parts[1]), DTFormatter)));
				}
				else if(parts.length == 3){
					taskList.add(new BoundedTask(parts[0].trim(), LocalDateTime.parse(changeFormat(parts[1]), DTFormatter),LocalDateTime.parse(changeFormat(parts[2]), DTFormatter)));

				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("Error in changing string to tasks");
		}

		try {
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return taskList;

	}
	
	private String changeFormat(String timeDate){
		String result = "";
		String[] parts = timeDate.trim().split(" ");
		result += parts[1].replace("-"," ") +" ";
		result += parts[0].replace(":", " ");
		return result.trim();
	}

}
