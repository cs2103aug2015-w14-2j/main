package test;
import static org.junit.Assert.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import shared.Output;
import ui.view.OverviewController;

// @@author A0131188H
public class SystemTest {
	
	private OverviewController controller;
	private Output expected;
	ArrayList<ArrayList<String>> expectedArrArrList;
	
	private void resetIndex() {
		for (int i = 0; i < expectedArrArrList.size(); i++) {
			expectedArrArrList.get(i).set(0, i + 1 + "");
		}
	}
	
	private ArrayList<ArrayList<String>> clean() {
		return new ArrayList<ArrayList<String>>();
	}
	
	private ArrayList<String> getDateInfo(LocalDateTime dt) {
		ArrayList<String> answer = new ArrayList<String>();
		answer.add(getDayOfWeek(dt));
		answer.add(getDay(dt));
		answer.add(getMonth(dt));
		answer.add(getYear(dt));
		return answer;
	}
	
	private ArrayList<String> getEmptyDTInfo() {
		ArrayList<String> answer = new ArrayList<String>();
		answer.add("");
		answer.add("");
		answer.add("");
		answer.add("");
		answer.add("");
		return answer;
	}
	
	private String getDayOfWeek(LocalDateTime dt) {
		return dt.getDayOfWeek().toString().substring(0, 3);
	}
	
	private String getDay(LocalDateTime dt) {
		return dt.getDayOfMonth() + "";
	}
	
	private String getMonth(LocalDateTime dt) {
		return dt.getMonth().toString().substring(0, 3);
	}
	
	private String getYear(LocalDateTime dt) {
		return dt.getYear() + "";
	}

	@Before
	public void setUp() {
		controller = new OverviewController(); 
		expected = new Output();
		expectedArrArrList = new ArrayList<ArrayList<String>>();
	}
	
	@Test 
	public void systemTest1() {
		controller.processInput("delete all");
		//==================================================================================
		ArrayList<String> task1 = new ArrayList<String>();
		LocalDateTime dt1 = LocalDateTime.now().with(DayOfWeek.THURSDAY);
		task1.add("1");
		task1.add("attend yoga class");
		task1.add("7pm");
		task1.addAll(getDateInfo(dt1));
		task1.add("8:30pm");
		task1.addAll(getDateInfo(dt1));
		task1.add("UNDONE");
		task1.add("");
		
		ArrayList<String> task2 = new ArrayList<String>();
		LocalDateTime dt2 = LocalDateTime.now().with(DayOfWeek.FRIDAY);
		task2.add("2");
		task2.add("annual general meeting");
		task2.add("10am");
		task2.addAll(getDateInfo(dt2));
		task2.add("11am");
		task2.addAll(getDateInfo(dt2));
		task2.add("UNDONE");
		task2.add("");
		
		ArrayList<String> task3 = new ArrayList<String>();
		LocalDateTime dt3 = LocalDateTime.now().with(DayOfWeek.MONDAY).plusWeeks(1);
		task3.add("3");
		task3.add("send meeting minutes");
		task3.addAll(getEmptyDTInfo());
		task3.add("8am");
		task3.addAll(getDateInfo(dt3));
		task3.add("UNDONE");
		task3.add("false");
		
		ArrayList<String> task4 = new ArrayList<String>();
		LocalDateTime dt4 = LocalDateTime.now().with(DayOfWeek.MONDAY).plusWeeks(1).plusDays(2);
		task4.add("4");
		task4.add("submit progress report");
		task4.addAll(getEmptyDTInfo());
		task4.add("6pm");
		task4.addAll(getDateInfo(dt4));
		task4.add("UNDONE");
		task4.add("false");
		
		ArrayList<String> task5 = new ArrayList<String>();
		task5.add("5");
		task5.add("buy bread");
		task5.addAll(getEmptyDTInfo());
		task5.addAll(getEmptyDTInfo());
		task5.add("UNDONE");
		task5.add("");
		
		ArrayList<String> task4b = new ArrayList<String>();
		task4b.add("4");
		task4b.add("submit ninja report");
		task4b.addAll(getEmptyDTInfo());
		task4b.add("3pm");
		task4b.addAll(getDateInfo(dt4));
		task4b.add("UNDONE");
		task4b.add("false");
		//==================================================================================
		String input1 = "create attend yoga class from 7pm to 8:30pm this thurs";
		Output output1 = controller.processInput(input1);
		expectedArrArrList.add(task1);
		expected.setOutput(clean());
		expected.setReturnMessage("\"attend yoga class\" has been created!");
		assertEquals(expected, output1);
		//==================================================================================
		String input2 = "create annual general meeting from 10:00 to 11:00 this friday";
		Output output2 = controller.processInput(input2);
		expectedArrArrList.add(task2);
		expected.setOutput(clean());
		expected.setReturnMessage("\"annual general meeting\" has been created!");
		assertEquals(expected, output2);
		//==================================================================================
		String input3 = "create send meeting minutes by 8AM next Mon";
		Output output3 = controller.processInput(input3);
		expectedArrArrList.add(task3);
		expected.setOutput(clean());
		expected.setReturnMessage("\"send meeting minutes\" has been created!");
		assertEquals(expected, output3);
		//==================================================================================
		String input4 = "create submit progress report by 6PM next Wednesday";
		Output output4 = controller.processInput(input4);
		expectedArrArrList.add(task4);
		expected.setOutput(clean());
		expected.setReturnMessage("\"submit progress report\" has been created!");
		assertEquals(expected, output4);
		//==================================================================================
		String input5 = "create buy bread";
		Output output5 = controller.processInput(input5);
		expectedArrArrList.add(task5);
		expected.setOutput(clean());
		expected.setReturnMessage("\"buy bread\" has been created!");
		assertEquals(expected, output5);
		//==================================================================================
		String input6 = "display all";
		Output output6 = controller.processInput(input6);
		expected.setOutput(expectedArrArrList);
		expected.setReturnMessage("All tasks are now displayed!");
		assertEquals(expected, output6);
		//==================================================================================
		String input7 = "edit 4 to submit ninja report end to 3pm";
		Output output7 = controller.processInput(input7);
		expectedArrArrList.set(3, task4b);
		expected.setOutput(clean());
		expected.setReturnMessage("\"submit progress report\" has been edited!");
		assertEquals(expected, output7);
		//==================================================================================
		String input8 = "display all";
		Output output8 = controller.processInput(input8);
		expected.setOutput(expectedArrArrList);
		expected.setReturnMessage("All tasks are now displayed!");
		assertEquals(expected, output8);
		//==================================================================================
		String input9 = "delete annual general meeting";
		Output output9 = controller.processInput(input9);
		expectedArrArrList.remove(1);
		resetIndex();		
		expected.setOutput(clean());
		expected.setReturnMessage("\"annual general meeting\" has been deleted!");
		expected.setPriority(Output.Priority.HIGH);
		assertEquals(expected, output9);
		expected.setPriority(Output.Priority.LOW);
		//==================================================================================
		String input10 = "display";
		Output output10 = controller.processInput(input10);
		expected.setOutput(expectedArrArrList);
		expected.setReturnMessage("Welcome to Flexi-List!");
		assertEquals(expected, output10);
		//==================================================================================
		String input11 = "mark 2";
		Output output11 = controller.processInput(input11);
		expectedArrArrList.get(1).set(12, "DONE");
		expected.setOutput(clean());
		expected.setReturnMessage("\"send meeting minutes\" has been marked done.");
		assertEquals(expected, output11);
		//==================================================================================
		String input12 = "display all";
		Output output12 = controller.processInput(input12);
		expected.setOutput(expectedArrArrList);
		expected.setReturnMessage("All tasks are now displayed!");
		assertEquals(expected, output12);
		//==================================================================================
		String input13 = "unmark send meeting minutes";
		Output output13 = controller.processInput(input13);
		expectedArrArrList.get(1).set(12, "UNDONE");
		expected.setOutput(new ArrayList<ArrayList<String>>());
		expected.setReturnMessage("\"send meeting minutes\" has been marked undone.");
		assertEquals(expected, output13);
		//==================================================================================
		String input14 = "display";
		Output output14 = controller.processInput(input14);
		expected.setOutput(expectedArrArrList);
		expected.setReturnMessage("Welcome to Flexi-List!");
		assertEquals(expected, output14);
	}
}
