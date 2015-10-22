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
import shared.task.AbstractTask.Status;
import shared.task.BoundedTask;
import shared.task.DeadlineTask;
import shared.task.FloatingTask;

public class Storage {
	
	String filePath = "src/storage.txt";
	
	//private savePath(s)
	
	private File locateFile() {
		File file = new File(filePath);

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
		String result = " ";

		if (task.getStatus() == Status.DONE) {
			result += "done`";

		} else if (task.getStatus() == Status.UNDONE) {
			result += "undone`";
		}

		for (String eachWord : words) {
			String[] temp = eachWord.split(":");
			if (temp.length == 2) {
				wordsList.add('`' + eachWord);
			} else {
				wordsList.add(eachWord);
			}
		}
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
				
				String[] parts = storageString.split("`");
				if (parts.length == 2) {
					FloatingTask floatingTask = new FloatingTask(parts[1].trim());
					System.out.println(parts[0]);
					if (parts[0].trim().equals("done")) {
						floatingTask.setStatus(Status.DONE);
					}
					else{
						floatingTask.setStatus(Status.UNDONE);
					}
					taskList.add(floatingTask);
				} else if (parts.length == 3) {
					DeadlineTask deadlineTask = new DeadlineTask(parts[1].trim(),
							LocalDateTime.parse(changeFormat(parts[2]), DTFormatter));
					if (parts[0].trim().equals("done")) {
						deadlineTask.setStatus(Status.DONE);
					}
					else{
						deadlineTask.setStatus(Status.UNDONE);
					}
					taskList.add(deadlineTask);
				} else if (parts.length == 4) {
					BoundedTask boundedTask = new BoundedTask(parts[1].trim(),
							LocalDateTime.parse(changeFormat(parts[2]), DTFormatter),
							LocalDateTime.parse(changeFormat(parts[3]), DTFormatter));
					if (parts[0].trim().equals("done")) {
						boundedTask.setStatus(Status.DONE);
					}else{
						boundedTask.setStatus(Status.UNDONE);
					}
					taskList.add(boundedTask);
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

	private String changeFormat(String timeDate) {
		String result = "";
		String[] parts = timeDate.trim().split(" ");
		result += parts[1].replace("-", " ") + " ";
		result += parts[0].replace(":", " ");
		return result.trim();
	}

}
