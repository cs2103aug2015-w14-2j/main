package shared.task;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.junit.Test;

public class TaskTest {
	DateTimeFormatter DTFormatter = DateTimeFormatter.ofPattern("dd MM yyyy HH mm");

	@Test
	public void toArrayFloating() {
		FloatingTask task = new FloatingTask("hello");
		
		ArrayList<String> returnArray = new ArrayList<String>();
		returnArray.add("hello");
		returnArray.add("");
		returnArray.add("");
		returnArray.add("");
		returnArray.add("");
		returnArray.add("");
		returnArray.add("");
		returnArray.add("");
		returnArray.add("");
		returnArray.add("");
		returnArray.add("");
		returnArray.add("UNDONE");
		
		assertEquals(returnArray, task.toArray());
	}
	
	@Test
	public void toArrayDeadline() {
		LocalDateTime myEnd = LocalDateTime.parse("13 10 2015 20 00", DTFormatter);
		DeadlineTask task = new DeadlineTask("hello", myEnd);
		
		ArrayList<String> returnArray = new ArrayList<String>();
		returnArray.add("hello");
		returnArray.add("");
		returnArray.add("");
		returnArray.add("");
		returnArray.add("");
		returnArray.add("");
		returnArray.add("8:00pm");
		returnArray.add("TUE");
		returnArray.add("13");
		returnArray.add("OCT");
		returnArray.add("2015");
		returnArray.add("UNDONE");
		
		assertEquals(returnArray, task.toArray());
	}
	
	@Test
	public void toArrayBounded() {
		LocalDateTime myStart = LocalDateTime.parse("12 10 2015 00 00", DTFormatter);
		LocalDateTime myEnd = LocalDateTime.parse("13 10 2015 20 00", DTFormatter);
		BoundedTask task = new BoundedTask("hello", myStart, myEnd);
		
		ArrayList<String> returnArray = new ArrayList<String>();
		returnArray.add("hello");
		returnArray.add("12:00am");
		returnArray.add("MON");
		returnArray.add("12");
		returnArray.add("OCT");
		returnArray.add("2015");
		returnArray.add("8:00pm");
		returnArray.add("TUE");
		returnArray.add("13");
		returnArray.add("OCT");
		returnArray.add("2015");
		returnArray.add("UNDONE");
		
		assertEquals(returnArray, task.toArray());
	}

}
