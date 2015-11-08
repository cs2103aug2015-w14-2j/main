package shared.task;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.junit.Test;

//@@author A0124828B

/**
 * TEST FOR TASK CLASSES 
 * These tests check if the 3 task classes are exported in
 * the correct format when they are converted to either an ArrayList<String> or
 * just a String
 */

public class TaskTest {
	DateTimeFormatter DTFormatter = DateTimeFormatter
			.ofPattern("dd MM yyyy HH mm");

	private ArrayList<String> arrayToArrayList(String[] stringArray) {
		ArrayList<String> arrayListToReturn = new ArrayList<String>();
		for (int i = 0; i < stringArray.length; i++) {
			arrayListToReturn.add(stringArray[i]);
		}
		return arrayListToReturn;
	}

	@Test
	public void toArrayFloating() {
		FloatingTask task = new FloatingTask("hello");
		String[] floatingTask = { "hello", "", "", "", "", "", "", "", "", "",
				"", "UNDONE", "" };
		ArrayList<String> expectedArray = arrayToArrayList(floatingTask);

		assertEquals(expectedArray, task.toArray());
	}

	@Test
	public void toArrayDeadline() {
		LocalDateTime myEnd = LocalDateTime.parse("13 10 2015 20 00",
				DTFormatter);
		DeadlineTask task = new DeadlineTask("hello", myEnd);
		String[] deadlineTask = { "hello", "", "", "", "", "", "8pm", "TUE",
				"13", "OCT", "2015", "UNDONE", "false" };
		ArrayList<String> expectedArray = arrayToArrayList(deadlineTask);

		assertEquals(expectedArray, task.toArray());
	}

	@Test
	public void toArrayBounded() {
		LocalDateTime myStart = LocalDateTime.parse("12 10 2015 00 00",
				DTFormatter);
		LocalDateTime myEnd = LocalDateTime.parse("13 10 2015 20 00",
				DTFormatter);
		BoundedTask task = new BoundedTask("hello", myStart, myEnd);
		String[] boundedTask = { "hello", "12am", "MON", "12", "OCT", "2015",
				"8pm", "TUE", "13", "OCT", "2015", "UNDONE", "" };
		ArrayList<String> expectedArray = arrayToArrayList(boundedTask);

		assertEquals(expectedArray, task.toArray());
	}

	@Test
	public void toStringFloating() {
		FloatingTask floatingTask = new FloatingTask("assignment");
		String output = floatingTask.toString();
		String expected = "UNDONE`assignment";
		assertEquals(expected, output);
	}

	@Test
	public void toStringDeadline() {
		LocalDateTime myEnd = LocalDateTime.parse("13 10 2015 20 00",
				DTFormatter);
		DeadlineTask deadlineTask = new DeadlineTask("assignment for biology",
				myEnd);
		String output = deadlineTask.toString();
		String expected = "UNDONE`assignment for biology`13 10 2015 20 00";
		assertEquals(expected, output);
	}

	@Test
	public void toStringBounded() {
		LocalDateTime myStart = LocalDateTime.parse("12 10 2015 00 00",
				DTFormatter);
		LocalDateTime myEnd = LocalDateTime.parse("13 10 2015 20 00",
				DTFormatter);
		BoundedTask boundedTask = new BoundedTask("hello", myStart, myEnd);
		String output = boundedTask.toString();
		String expected = "UNDONE`hello`12 10 2015 00 00`13 10 2015 20 00";
		assertEquals(expected, output);
	}

}
