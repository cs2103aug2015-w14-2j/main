package storage;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import shared.task.AbstractTask;
import shared.task.BoundedTask;
import shared.task.DeadlineTask;
import shared.task.FloatingTask;

public class StorageTest {
	DateTimeFormatter DTFormatter = DateTimeFormatter
			.ofPattern("dd MM yyyy HH mm");

	public boolean compare(ArrayList<AbstractTask> first,
			ArrayList<AbstractTask> second) {
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

	private Storage storage;

	@Before
	public void setUp() {
		storage = new Storage();
	}


	@Test
	public void readNormalFile() {
		// src/storage.txt contains
		// new floating
		// new bounded |08:00 22-10-2015 |10:00 22-10-2015
		// buy apples, oranges and starfruits


		ArrayList<AbstractTask> expected = new ArrayList<AbstractTask>();
		expected = storage.read();

		ArrayList<AbstractTask> testSet = new ArrayList<AbstractTask>();
		
		testSet.add(new FloatingTask("tutorial"));

		testSet.add(new DeadlineTask("math tutorial", LocalDateTime.parse(
				"01 01 2015 13 00", DTFormatter)));
		testSet.add(new BoundedTask("additional math tutorial", LocalDateTime.parse(
				"01 01 2015 13 00", DTFormatter), LocalDateTime.parse(
				"01 01 2015 15 00", DTFormatter)));

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
	public void writeNormalFileOrWriteEmptyFileOrNonExistentFile() {
		ArrayList<AbstractTask> arrayWrite = new ArrayList<AbstractTask>();
		arrayWrite.add(new FloatingTask("lecture"));
		arrayWrite.add(new DeadlineTask("tutorial", LocalDateTime.parse(
				"01 01 2015 10 00", DTFormatter)));
		arrayWrite.add(new BoundedTask("recitation", LocalDateTime.parse(
				"01 01 2015 12 00", DTFormatter), LocalDateTime.parse(
				"01 01 2015 14 00", DTFormatter)));
		storage.write(arrayWrite);

		ArrayList<AbstractTask> arrayRead = new ArrayList<AbstractTask>();
		arrayRead = storage.read();

		assertEquals(true, compare(arrayRead, arrayWrite));
	}
}