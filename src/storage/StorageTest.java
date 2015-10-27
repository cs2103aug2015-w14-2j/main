package storage;

import static org.junit.Assert.*;

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
		ArrayList<AbstractTask> expected = new ArrayList<AbstractTask>();
		ArrayList<AbstractTask> testSet = new ArrayList<AbstractTask>();
		expected = storage.read();

		FloatingTask first = new FloatingTask("lecture");
		first.setStatus(Status.DONE);
		testSet.add(first);

		DeadlineTask second = new DeadlineTask("tutorial", LocalDateTime.parse("01 01 2015 10 00", DTFormatter));
		second.setStatus(Status.DONE);
		testSet.add(second);

		BoundedTask third = new BoundedTask("recitation", LocalDateTime.parse("01 01 2015 12 00", DTFormatter),
				LocalDateTime.parse("01 01 2015 14 00", DTFormatter));
		third.setStatus(Status.DONE);
		testSet.add(third);

		assertEquals(true, compare(expected, testSet));

	}

	@Test
	public void readMixed() {
		ArrayList<AbstractTask> expected = new ArrayList<AbstractTask>();
		ArrayList<AbstractTask> testSet = new ArrayList<AbstractTask>();
		expected = storage.read();

		FloatingTask first = new FloatingTask("lecture");
		testSet.add(first);

		DeadlineTask second = new DeadlineTask("tutorial", LocalDateTime.parse("01 01 2015 10 00", DTFormatter));
		second.setStatus(Status.DONE);
		testSet.add(second);

		BoundedTask third = new BoundedTask("recitation", LocalDateTime.parse("01 01 2015 12 00", DTFormatter),
				LocalDateTime.parse("01 01 2015 14 00", DTFormatter));
		third.setStatus(Status.DONE);
		testSet.add(third);

		assertEquals(true, compare(expected, testSet));

	}

	@Test
	public void readAllUndone() {
		ArrayList<AbstractTask> expected = new ArrayList<AbstractTask>();
		ArrayList<AbstractTask> testSet = new ArrayList<AbstractTask>();
		expected = storage.read();

		FloatingTask first = new FloatingTask("lecture");

		testSet.add(first);

		DeadlineTask second = new DeadlineTask("tutorial", LocalDateTime.parse("01 01 2015 10 00", DTFormatter));

		testSet.add(second);

		BoundedTask third = new BoundedTask("recitation", LocalDateTime.parse("01 01 2015 12 00", DTFormatter),
				LocalDateTime.parse("01 01 2015 14 00", DTFormatter));

		testSet.add(third);

		assertEquals(true, compare(expected, testSet));

	}

	@Test
	public void readEmptyFile() {
		// src/storage.txt is empty
		ArrayList<AbstractTask> array1 = new ArrayList<AbstractTask>();
		array1 = storage.read();

		ArrayList<AbstractTask> array2 = new ArrayList<AbstractTask>();

		assertEquals(true, compare(array1, array2));
	}

	@Test
	public void readNonExistentFile() {
		// src/storage.txt is non-existent
		ArrayList<AbstractTask> array1 = new ArrayList<AbstractTask>();
		array1 = storage.read();

		ArrayList<AbstractTask> array2 = new ArrayList<AbstractTask>();

		assertEquals(true, compare(array1, array2));
	}

	@Test
	public void writeAllDone() throws IOException {

		File file = storage.openFile();
		File testFile = new File("src/teststorage.txt");
		ArrayList<AbstractTask> testSet = new ArrayList<AbstractTask>();
		

		FloatingTask first = new FloatingTask("lecture");
		first.setStatus(Status.DONE);
		testSet.add(first);

		DeadlineTask second = new DeadlineTask("tutorial", LocalDateTime.parse("01 01 2015 10 00", DTFormatter));
		second.setStatus(Status.DONE);
		testSet.add(second);

		BoundedTask third = new BoundedTask("recitation", LocalDateTime.parse("01 01 2015 12 00", DTFormatter),
				LocalDateTime.parse("01 01 2015 14 00", DTFormatter));
		third.setStatus(Status.DONE);
		testSet.add(third);

		// prepare the file writer
		FileWriter writer;
		try {
			writer = new FileWriter(testFile.getAbsolutePath());
			for (int i = 0; i < testSet.size(); i++) {
				writer.write(storage.toString(testSet.get(i)));
				writer.write("\r\n");

			}
			writer.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw new Error("Unable to create file writer.");
		}

		writer.close();

		assertEquals(true, compareTextFile(file, testFile));

	}
	@Test
	public void writeMixed() throws IOException {

		File file = storage.openFile();
		File testFile = new File("src/teststorage.txt");
		ArrayList<AbstractTask> testSet = new ArrayList<AbstractTask>();
		

		FloatingTask first = new FloatingTask("lecture");
		testSet.add(first);
		

		DeadlineTask second = new DeadlineTask("tutorial", LocalDateTime.parse("01 01 2015 10 00", DTFormatter));
		second.setStatus(Status.DONE);
		testSet.add(second);

		BoundedTask third = new BoundedTask("recitation", LocalDateTime.parse("01 01 2015 12 00", DTFormatter),
				LocalDateTime.parse("01 01 2015 14 00", DTFormatter));
		third.setStatus(Status.DONE);
		testSet.add(third);

		// prepare the file writer
		FileWriter writer;
		try {
			writer = new FileWriter(testFile.getAbsolutePath());
			for (int i = 0; i < testSet.size(); i++) {
				writer.write(storage.toString(testSet.get(i)));
				writer.write("\r\n");

			}
			writer.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw new Error("Unable to create file writer.");
		}

		writer.close();

		assertEquals(true, compareTextFile(file, testFile));

	}
	
	@Test
	public void writeAllUndone() throws IOException {

		File file = storage.openFile();
		File testFile = new File("src/teststorage.txt");
		ArrayList<AbstractTask> testSet = new ArrayList<AbstractTask>();
		
		FloatingTask first = new FloatingTask("lecture");
		testSet.add(first);

		DeadlineTask second = new DeadlineTask("tutorial", LocalDateTime.parse("01 01 2015 10 00", DTFormatter));

		testSet.add(second);

		BoundedTask third = new BoundedTask("recitation", LocalDateTime.parse("01 01 2015 12 00", DTFormatter),
				LocalDateTime.parse("01 01 2015 14 00", DTFormatter));
		testSet.add(third);

		// prepare the file writer
		FileWriter writer;
		try {
			writer = new FileWriter(testFile.getAbsolutePath());
			for (int i = 0; i < testSet.size(); i++) {
				writer.write(storage.toString(testSet.get(i)));
				writer.write("\r\n");

			}
			writer.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw new Error("Unable to create file writer.");
		}

		writer.close();

		assertEquals(true, compareTextFile(file, testFile));

	}
	
	@Test
	public void testSetPath() {
		try {
			String newPath = new String("src/folder");
			
			File pathFile = storage.locatePathFile();
			//String storageLocation = storage.getPath(pathFile);
			boolean possible = storage.setPath(newPath);
			System.out.println(possible);
			
			//test
			FileInputStream stream = null;
			try {
				stream = new FileInputStream(pathFile);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			
			String outputPath = reader.readLine();
			assertEquals(newPath, outputPath);
			
			
		} catch (IOException e) {
			System.out.println(e);	
		}
		
		
		
		
	}
	
	
	
	

}