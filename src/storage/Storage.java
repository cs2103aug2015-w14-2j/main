package storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import shared.task.AbstractTask;
import shared.task.AbstractTask.Status;
import shared.task.BoundedTask;
import shared.task.DeadlineTask;
import shared.task.FloatingTask;

public class Storage {
	protected File locatePathFile() {
		File file = new File("src\\path.txt");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				//exception not handled
			}
		}
		return file;
	}

	protected String getStorageLocation(File file) {
		FileReader readFile = null;
		try {
			readFile = new FileReader(file);
		} catch (FileNotFoundException e) {
			System.out.println("getStorageLocation cannot process the input file.");
		}
		String text = null;
		try {		
			BufferedReader reader = new BufferedReader(readFile);
			text = reader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("error in reading getStorageLocation");
		}
		if (text == null) {
			text = "src\\storage.txt";
		}
		return text;
	}

	protected File getContentFile(String storageLocation) {

		File file = new File(storageLocation);

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("File creation failed.");
			}
		}
		return file;
	}
	
	
	public boolean containsIllegals(String toExamine) {
		Pattern pattern = Pattern.compile("[~#@*+%{}<>\\[\\]|\"\\_^]");
		Matcher matcher = pattern.matcher(toExamine);
		return matcher.find();
	}

	public  boolean setStorageLocation(File file, String newLoc) {
		File contentFile = openFile();

		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("cannnot read the file to write!!");
		}

		FileReader readFile = null;
		String oldLine = null;
		try {
			readFile = new FileReader(file);
			BufferedReader reader = new BufferedReader(readFile);

			oldLine = reader.readLine();
			reader.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.out.println("oldLine is " + oldLine);

		if (containsIllegals(newLoc)) {
			System.out.println("illegal return false");
			return false;
		}

		contentFile.delete();
		String newString = file.getParent() + "\\" + newLoc;
		File newTestFile = new File(newString);
		/// create a new folder if there isn't any
		boolean createFolder = newTestFile.mkdir();
		System.out.println("  +" + createFolder);
		if (createFolder == false) {
			System.out.println("folder already present");

		}

		try {
			writer.write(newString + "\\storage.txt");
			System.out.println("sucess");
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	public File openFile() {
		File file = locatePathFile();
		String storageLocation = getStorageLocation(file);
		File contentFile = getContentFile(storageLocation);
		return contentFile;
	}

	public boolean changePath(String newName) {
		File file = locatePathFile();
		return setStorageLocation(file, newName);
	}

	public String getPath() {
		File file = locatePathFile();
		String storageLocation = getStorageLocation(file);
		return storageLocation;
	}

	public void write(ArrayList<AbstractTask> taskList) {
		File file = openFile();
		FileWriter writer;
		try {
			writer = new FileWriter(file.getAbsolutePath());
			for (int i = 0; i < taskList.size(); i++) {
				writer.write(taskList.get(i).toString());
				writer.write("\r\n");

			}
			writer.close();
		} catch (IOException e1) {
			// exception will not be handled
		}

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
							(LocalDateTime.parse(parts[2], DTFormatter)), (LocalDateTime.parse(parts[3], DTFormatter)));
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

}
