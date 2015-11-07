package storage;

import static org.junit.Assert.assertEquals;

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

import org.junit.Before;
import org.junit.Test;

import shared.task.AbstractTask;
import shared.task.AbstractTask.Status;
import shared.task.BoundedTask;
import shared.task.DeadlineTask;
import shared.task.FloatingTask;

//@@author A0122404Y
public class StorageTest {
	DateTimeFormatter DTFormatter = DateTimeFormatter.ofPattern("dd MM yyyy HH mm");

	public boolean compare(ArrayList<AbstractTask> first, ArrayList<AbstractTask> second) {
		if (first.size() != second.size()) {
			return false;
		} else if (first.size() == 0 && second.size() == 0) {
			return true;
		} else {
			if (first.get(0).equals(second.get(0))) {
				first.remove(0);
				second.remove(0);
				return compare(first, second);
			} else {
				return false;
			}
		}
	}
	public static void deleteFolder(File folder) {
	    File[] files = folder.listFiles();
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
	                deleteFolder(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	    folder.delete();
	}

	public boolean compareTextFile(File firstFile, File secondFile) throws IOException {
		FileInputStream stream1 = null;
		try {
			stream1 = new FileInputStream(firstFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BufferedReader reader1 = new BufferedReader(new InputStreamReader(stream1));
		FileInputStream stream2 = null;
		try {
			stream2 = new FileInputStream(secondFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BufferedReader reader2 = new BufferedReader(new InputStreamReader(stream2));
		String line1, line2;

		while (true) // Continue while there are equal lines
		{
			line1 = reader1.readLine();
			line2 = reader2.readLine();

			if (line1 == null) // End of file 1
			{
				return (line2 == null ? true : false); // Equal only if file 2
														// also ended
			}

			if (!line1.equalsIgnoreCase(line2)) // Different lines, or end of
												// file 2
			{
				return false;
			}
		}

	}

	private Storage storage;

	@Before
	public void setUp() {
		storage = new Storage();
	}

	@Test
	public void readAllDone() {
		
		File contentFile = storage.openFile();
		
		ArrayList<AbstractTask> testSet = new ArrayList<AbstractTask>();
		BoundedTask third = new BoundedTask("test bounded task", LocalDateTime.parse("07 11 2015 14 00", DTFormatter),
				LocalDateTime.parse("07 11 2015 17 00", DTFormatter));
		third.setStatus(Status.DONE);
		testSet.add(third);
		
		FloatingTask first = new FloatingTask("test floating task");
		first.setStatus(Status.DONE);
		testSet.add(first);

		DeadlineTask second = new DeadlineTask("test deadline task", LocalDateTime.parse("07 11 2015 17 00", DTFormatter));
		second.setStatus(Status.DONE);
		testSet.add(second);
		
		storage.write(testSet);
		ArrayList<AbstractTask> expected = new ArrayList<AbstractTask>();
		expected = storage.read();
		
		deleteFolder(contentFile);
		assertEquals(true, compare(expected, testSet));

	}

	@Test
	public void readAllUndone() {
		
		File contentFile = storage.openFile();
		
		ArrayList<AbstractTask> testSet = new ArrayList<AbstractTask>();
		BoundedTask third = new BoundedTask("test bounded task", LocalDateTime.parse("07 11 2015 14 00", DTFormatter),
				LocalDateTime.parse("07 11 2015 17 00", DTFormatter));
		third.setStatus(Status.UNDONE);
		testSet.add(third);
		
		FloatingTask first = new FloatingTask("test floating task");
		first.setStatus(Status.UNDONE);
		testSet.add(first);

		DeadlineTask second = new DeadlineTask("test deadline task", LocalDateTime.parse("07 11 2015 17 00", DTFormatter));
		second.setStatus(Status.UNDONE);
		testSet.add(second);
		
		storage.write(testSet);
		ArrayList<AbstractTask> expected = new ArrayList<AbstractTask>();
		expected = storage.read();
		
		deleteFolder(contentFile);
		assertEquals(true, compare(expected, testSet));

	}

	@Test
	public void readMixed() {
		
		File contentFile = storage.openFile();
		
		ArrayList<AbstractTask> testSet = new ArrayList<AbstractTask>();
		BoundedTask third = new BoundedTask("test bounded task", LocalDateTime.parse("07 11 2015 14 00", DTFormatter),
				LocalDateTime.parse("07 11 2015 17 00", DTFormatter));
		third.setStatus(Status.UNDONE);
		testSet.add(third);
		
		FloatingTask first = new FloatingTask("test floating task");
		first.setStatus(Status.DONE);
		testSet.add(first);

		DeadlineTask second = new DeadlineTask("test deadline task", LocalDateTime.parse("07 11 2015 17 00", DTFormatter));
		second.setStatus(Status.DONE);
		testSet.add(second);
		
		storage.write(testSet);
		ArrayList<AbstractTask> expected = new ArrayList<AbstractTask>();
		expected = storage.read();
		
		deleteFolder(contentFile);
		assertEquals(true, compare(expected, testSet));

	}

	@Test
	public void readEmpty() {
		
		File contentFile = storage.openFile();
		
		ArrayList<AbstractTask> testSet = new ArrayList<AbstractTask>();
		ArrayList<AbstractTask> expected = new ArrayList<AbstractTask>();
		expected = storage.read();
		
		deleteFolder(contentFile);
		assertEquals(true, compare(expected, testSet));

	}
	@Test
	public void testSetPath(){
		String testNewPath = "newFolder";

		File file = storage.openFile();
		ArrayList<AbstractTask> sampleContent = new ArrayList<AbstractTask>();

		FloatingTask first = new FloatingTask("test floating task");
			first.setStatus(Status.DONE);
			sampleContent.add(first);

		storage.write(sampleContent);

		storage.setPath(testNewPath);

		ArrayList<AbstractTask> output = storage.read();

		deleteFolder(file);
		assertEquals(output, sampleContent);
	}

	

}