package test;
import static org.junit.Assert.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.Test;

import logic.Logic;
import shared.Output;
import storage.Storage;

public class SystemTest {

	@Test
	public void test() {
		Storage storage = new Storage();
		Logic logic = new Logic(storage);
		Output expected = new Output();
		ArrayList<ArrayList<String>> expectedArrayList = new ArrayList<ArrayList<String>>();
		
		logic.processInput("delete all");
		//==================================================================================
		String input1 = "create attend yoga class from 7pm to 8:30pm this thurs";
		Output output1 = logic.processInput(input1);
		
		LocalDateTime thisThurs = LocalDateTime.now().with(DayOfWeek.THURSDAY);
		ArrayList<String> task1 = new ArrayList<String>();
		task1.add("1");
		task1.add("attend yoga class");
		task1.add("7pm");
		task1.add(thisThurs.getDayOfWeek().toString().substring(0, 3));
		task1.add(thisThurs.getDayOfMonth() + "");
		task1.add(thisThurs.getMonth().toString().substring(0, 3));
		task1.add(thisThurs.getYear() + "");
		task1.add("8:30pm");
		task1.add(thisThurs.getDayOfWeek().toString().substring(0, 3));
		task1.add(thisThurs.getDayOfMonth() + "");
		task1.add(thisThurs.getMonth().toString().substring(0, 3));
		task1.add(thisThurs.getYear() + "");
		task1.add("UNDONE");
		expectedArrayList.add(task1);
		
		expected.setOutput(new ArrayList<ArrayList<String>>());
		expected.setReturnMessage("\"attend yoga class\" has been created!");
		assertEquals(expected, output1);
		//==================================================================================
		String input2 = "create annual general meeting from 10:00 to 11:00 this friday";
		Output output2 = logic.processInput(input2);
		
		LocalDateTime thisFri = LocalDateTime.now().with(DayOfWeek.FRIDAY);
		ArrayList<String> task2 = new ArrayList<String>();
		task2.add("2");
		task2.add("annual general meeting");
		task2.add("10am");
		task2.add(thisFri.getDayOfWeek().toString().substring(0, 3));
		task2.add(thisFri.getDayOfMonth() + "");
		task2.add(thisFri.getMonth().toString().substring(0, 3));
		task2.add(thisFri.getYear() + "");
		task2.add("11am");
		task2.add(thisFri.getDayOfWeek().toString().substring(0, 3));
		task2.add(thisFri.getDayOfMonth() + "");
		task2.add(thisFri.getMonth().toString().substring(0, 3));
		task2.add(thisFri.getYear() + "");
		task2.add("UNDONE");
		expectedArrayList.add(task2);
		
		expected.setOutput(new ArrayList<ArrayList<String>>());
		expected.setReturnMessage("\"annual general meeting\" has been created!");
		assertEquals(expected, output2);
		//==================================================================================
		String input3 = "create send meeting minutes by 8AM next Mon";
		Output output3 = logic.processInput(input3);
		
		LocalDateTime nextMon = LocalDateTime.now().with(DayOfWeek.MONDAY).plusWeeks(1);
		ArrayList<String> task3 = new ArrayList<String>();
		task3.add("3");
		task3.add("send meeting minutes");
		task3.add("");
		task3.add("");
		task3.add("");
		task3.add("");
		task3.add("");
		task3.add("8am");
		task3.add(nextMon.getDayOfWeek().toString().substring(0, 3));
		task3.add(nextMon.getDayOfMonth() + "");
		task3.add(nextMon.getMonth().toString().substring(0, 3));
		task3.add(nextMon.getYear() + "");
		task3.add("UNDONE");
		expectedArrayList.add(task3);
		
		expected.setOutput(new ArrayList<ArrayList<String>>());
		expected.setReturnMessage("\"send meeting minutes\" has been created!");
		assertEquals(expected, output3);
		//==================================================================================
		String input4 = "create submit progress report by 6PM next Wednesday";
		Output output4 = logic.processInput(input4);
		
		LocalDateTime nextWed = LocalDateTime.now().with(DayOfWeek.MONDAY).plusWeeks(1).plusDays(2);
		ArrayList<String> task4 = new ArrayList<String>();
		task4.add("4");
		task4.add("submit progress report");
		task4.add("");
		task4.add("");
		task4.add("");
		task4.add("");
		task4.add("");
		task4.add("6pm");
		task4.add(nextWed.getDayOfWeek().toString().substring(0, 3));
		task4.add(nextWed.getDayOfMonth() + "");
		task4.add(nextWed.getMonth().toString().substring(0, 3));
		task4.add(nextWed.getYear() + "");
		task4.add("UNDONE");
		expectedArrayList.add(task4);
		
		expected.setOutput(new ArrayList<ArrayList<String>>());
		expected.setReturnMessage("\"submit progress report\" has been created!");
		assertEquals(expected, output4);
		//==================================================================================
		String input5 = "create buy bread";
		Output output5 = logic.processInput(input5);
		
		ArrayList<String> task5 = new ArrayList<String>();
		task5.add("5");
		task5.add("buy bread");
		task5.add("");
		task5.add("");
		task5.add("");
		task5.add("");
		task5.add("");
		task5.add("");
		task5.add("");
		task5.add("");
		task5.add("");
		task5.add("");
		task5.add("UNDONE");
		expectedArrayList.add(task5);
		
		expected.setOutput(new ArrayList<ArrayList<String>>());
		expected.setReturnMessage("\"buy bread\" has been created!");
		assertEquals(expected, output5);
		//==================================================================================
		String input6 = "display";
		Output output6 = logic.processInput(input6);
		
		expected.setOutput(expectedArrayList);
		expected.setReturnMessage("Welcome to Flexi-List!");
		
		assertEquals(expected, output6);
		//==================================================================================
		String input7 = "edit 4 to submit ninja report end to 3pm";
		Output output7 = logic.processInput(input7);
		
		ArrayList<String> task7 = new ArrayList<String>();
		task7.add("4");
		task7.add("submit ninja report");
		task7.add("");
		task7.add("");
		task7.add("");
		task7.add("");
		task7.add("");
		task7.add("3pm");
		task7.add(nextWed.getDayOfWeek().toString().substring(0, 3));
		task7.add(nextWed.getDayOfMonth() + "");
		task7.add(nextWed.getMonth().toString().substring(0, 3));
		task7.add(nextWed.getYear() + "");
		task7.add("UNDONE");
		expectedArrayList.set(3, task7);
		
		expected.setOutput(new ArrayList<ArrayList<String>>());
		expected.setReturnMessage("\"submit progress report\" has been edited!");
		assertEquals(expected, output7);
		//==================================================================================
		String input8 = "display all";
		Output output8 = logic.processInput(input8);
		
		expected.setOutput(expectedArrayList);
		expected.setReturnMessage("All tasks are now displayed!");
		
		assertEquals(expected, output8);
		//==================================================================================
		String input9 = "delete annual general meeting";
		Output output9 = logic.processInput(input9);
		
		expectedArrayList.remove(1);
		for (int i = 1; i < expectedArrayList.size(); i++) {
			expectedArrayList.get(i).set(0, i + 1 + "");
		}
		
		expected.setOutput(new ArrayList<ArrayList<String>>());
		expected.setReturnMessage("\"annual general meeting\" has been deleted!");
		expected.setPriority(Output.Priority.HIGH);
		assertEquals(expected, output9);
		expected.setPriority(Output.Priority.LOW);
		//==================================================================================
		String input10 = "display";
		Output output10 = logic.processInput(input10);
		
		expected.setOutput(expectedArrayList);
		expected.setReturnMessage("Welcome to Flexi-List!");
		assertEquals(expected, output10);
		//==================================================================================
		String input11 = "mark 2";
		Output output11 = logic.processInput(input11);
		
		expectedArrayList.get(1).set(12, "DONE");
		
		expected.setOutput(new ArrayList<ArrayList<String>>());
		expected.setReturnMessage("\"send meeting minutes\" has been marked done.");
		assertEquals(expected, output11);
		//==================================================================================
		String input12 = "display all";
		Output output12 = logic.processInput(input12);
		
		expected.setOutput(expectedArrayList);
		expected.setReturnMessage("All tasks are now displayed!");
		assertEquals(expected, output12);
		//==================================================================================
		String input13 = "unmark send meeting minutes";
		Output output13 = logic.processInput(input13);
		
		expectedArrayList.get(1).set(12, "UNDONE");
		
		expected.setOutput(new ArrayList<ArrayList<String>>());
		expected.setReturnMessage("\"send meeting minutes\" has been marked undone.");
		assertEquals(expected, output13);
		//==================================================================================
		String input14 = "display";
		Output output14 = logic.processInput(input14);
		
		expected.setOutput(expectedArrayList);
		expected.setReturnMessage("Welcome to Flexi-List!");
		assertEquals(expected, output14);
	}

}
