package storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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

	// helper functions
	protected File locatePathFile() {
		//find the file and write in the default storage if there is no such file
		File file = new File("src/path.txt");
		FileWriter fw = null;
		try {
			fw = new FileWriter(file.getAbsoluteFile());
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BufferedWriter bw= null;
		bw = new BufferedWriter(fw);
		if (file.isFile()) {
			try {
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return file;
		} else {
			try {
				file.createNewFile();
			} catch (IOException e) {
				//exception will not be handled 
			}
		
				try {
					bw.close();
					fw.close();
				} catch (IOException e) {
					
					//exception will not be handled 
				}
			
			return file;
		}

	}

	protected String getPath(File pathFile) {
		File file = pathFile;
		FileInputStream stream = null;

		try {
			stream = new FileInputStream(pathFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String storageLocation = null;

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			storageLocation = reader.readLine();
			if (storageLocation== null){
			storageLocation = "src/storage.txt";}
			else{
				System.out.println(storageLocation);
			}
			
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			fw.write(storageLocation);
			fw.close();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		return storageLocation;
	}

	protected boolean setPath(String newName) {
		File file;
		file = locatePathFile();//settle exception inside
		File test = new File(newName);
		if (!test.isDirectory()) {
			return false;
		} else {
			FileWriter writer;
			try {
				writer = new FileWriter(file.getAbsolutePath());
				writer.write(newName);
				writer.close();
			} catch (IOException e) {
				return false; 
			}
			
			return true;
		}
	}

	protected File locateFile(String name) {
		File file = new File(name);
		if ((!file.exists())) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return file;
	}

	public File openFile() {
		// 1. find the file that contain the storage location
		File pathFile = null;
		pathFile = locatePathFile();

		// 2. read the contents in there to get the storage location
		String storageLocation = getPath(pathFile);

		// 3. locate the file in the storage location.
		File contentFile = locateFile(storageLocation);
		return contentFile;
	}
	// ---------------------------------------------------------------------------------------

	public void write(ArrayList<AbstractTask> taskList) {
		// locate the file
		File file = openFile();

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
			//exception will not be handled
		}

	}

	public String toString(AbstractTask task) {

		ArrayList<String> wordsList = new ArrayList<String>();
		String[] words = task.toString().split(" ");
		String result = "";

/*		if (task.getStatus() == Status.DONE) {
			result += "done`";

		} else if (task.getStatus() == Status.UNDONE) {
			result += "undone`";
		}
*/
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
		File file = openFile();
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
					FloatingTask floatingTask = new FloatingTask(parts[1]);
					if (parts[0].trim().equals("DONE")) {
						floatingTask.setStatus(Status.DONE);
					} else {
						floatingTask.setStatus(Status.UNDONE);
					}
					taskList.add(floatingTask);
				} else if (parts.length == 3) {
					DeadlineTask deadlineTask = new DeadlineTask(parts[1].trim(),
							LocalDateTime.parse(parts[2], DTFormatter));
					if (parts[0].trim().equals("DONE")) {
						deadlineTask.setStatus(Status.DONE);
					} else {
						deadlineTask.setStatus(Status.UNDONE);
					}
					taskList.add(deadlineTask);
				} else if (parts.length == 4) {
					BoundedTask boundedTask = new BoundedTask(parts[1].trim(),
							(LocalDateTime.parse(parts[2], DTFormatter)),
							(LocalDateTime.parse(parts[3], DTFormatter)));
					if (parts[0].trim().equals("DONE")) {
						boundedTask.setStatus(Status.DONE);
					} else {
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

/*	private String changeFormat(String timeDate) {
		String result = "";
		String[] parts = timeDate.trim().split(" ");
		result += parts[1].replace("-", " ") + " ";
		result += parts[0].replace(":", " ");
		return result.trim();
	}*/

}
