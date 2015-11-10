package test;

import static org.junit.Assert.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;

import shared.Constants;
import shared.Output;
import ui.view.OverviewController;

// @@author A0131188H
public class SystemTest {
	
	private OverviewController controller;
	private Output expected;
	private ArrayList<ArrayList<String>> expectedArrArrList;
	private ArrayList<ArrayList<String>> expectedSearchList;

	private void resetIndex() {
		for (int i = 0; i < expectedArrArrList.size(); i++) {
			expectedArrArrList.get(i).set(0, i + 1 + Constants.EMPTY);
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
		answer.add(Constants.EMPTY);
		answer.add(Constants.EMPTY);
		answer.add(Constants.EMPTY);
		answer.add(Constants.EMPTY);
		answer.add(Constants.EMPTY);
		return answer;
	}

	private ArrayList<String> getTodayInfo() {
		LocalDateTime now = LocalDateTime.now();
		ArrayList<String> answer = new ArrayList<String>();
		answer.add(getDayOfWeek(now));
		answer.add(Constants.TODAY.toUpperCase());
		answer.add(Constants.EMPTY);
		answer.add(Constants.EMPTY);
		return answer;
	}

	private String getDayOfWeek(LocalDateTime dt) {
		return dt.getDayOfWeek().toString().substring(0, 3);
	}

	private String getDay(LocalDateTime dt) {
		return dt.getDayOfMonth() + Constants.EMPTY;
	}

	private String getMonth(LocalDateTime dt) {
		return dt.getMonth().toString().substring(0, 3);
	}

	private String getYear(LocalDateTime dt) {
		return dt.getYear() + Constants.EMPTY;
	}

	@Before
	public void setUp() {
		controller = new OverviewController();
		expected = new Output();
		expectedArrArrList = new ArrayList<ArrayList<String>>();
		expectedSearchList = new ArrayList<ArrayList<String>>();
	}

	@Test
	public void systemTest1() {
		controller.processInput("delete all");
		// ==================================================================================
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

		ArrayList<String> task4b = new ArrayList<String>();
		task4b.add("4");
		task4b.add("submit ninja report");
		task4b.addAll(getEmptyDTInfo());
		task4b.add("3pm");
		task4b.addAll(getDateInfo(dt4));
		task4b.add("UNDONE");
		task4b.add("false");

		ArrayList<String> task5 = new ArrayList<String>();
		task5.add("5");
		task5.add("buy bread");
		task5.addAll(getEmptyDTInfo());
		task5.addAll(getEmptyDTInfo());
		task5.add("UNDONE");
		task5.add("");
		// ==================================================================================
		String input1 = "create attend yoga class from 7pm to 8:30pm this thurs";
		Output output1 = controller.processInput(input1);
		expectedArrArrList.add(task1);
		expected.setOutput(clean());
		expected.setReturnMessage("\"attend yoga class\" has been created!");
		assertEquals(expected, output1);
		// ==================================================================================
		String input2 = "create annual general meeting from 10:00 to 11:00 this friday";
		Output output2 = controller.processInput(input2);
		expectedArrArrList.add(task2);
		expected.setOutput(clean());
		expected.setReturnMessage("\"annual general meeting\" has been created!");
		assertEquals(expected, output2);
		// ==================================================================================
		String input3 = "create send meeting minutes by 8AM next Mon";
		Output output3 = controller.processInput(input3);
		expectedArrArrList.add(task3);
		expected.setOutput(clean());
		expected.setReturnMessage("\"send meeting minutes\" has been created!");
		assertEquals(expected, output3);
		// ==================================================================================
		String input4 = "create submit progress report by 6PM next Wednesday";
		Output output4 = controller.processInput(input4);
		expectedArrArrList.add(task4);
		expected.setOutput(clean());
		expected.setReturnMessage("\"submit progress report\" has been created!");
		assertEquals(expected, output4);
		// ==================================================================================
		String input5 = "create buy bread";
		Output output5 = controller.processInput(input5);
		expectedArrArrList.add(task5);
		expected.setOutput(clean());
		expected.setReturnMessage("\"buy bread\" has been created!");
		assertEquals(expected, output5);
		// ==================================================================================
		String input6 = "display all";
		Output output6 = controller.processInput(input6);
		expected.setOutput(expectedArrArrList);
		expected.setReturnMessage("All tasks are now displayed!");
		assertEquals(expected, output6);
		// ==================================================================================
		String input7 = "edit 4 to submit ninja report end to 3pm";
		Output output7 = controller.processInput(input7);
		expectedArrArrList.set(3, task4b);
		expected.setOutput(clean());
		expected.setReturnMessage("\"submit progress report\" has been edited!");
		assertEquals(expected, output7);
		// ==================================================================================
		String input8 = "display all";
		Output output8 = controller.processInput(input8);
		expected.setOutput(expectedArrArrList);
		expected.setReturnMessage("All tasks are now displayed!");
		assertEquals(expected, output8);
		// ==================================================================================
		String input9 = "delete annual general meeting";
		Output output9 = controller.processInput(input9);
		expectedArrArrList.remove(1);
		resetIndex();
		expected.setOutput(clean());
		expected.setReturnMessage("\"annual general meeting\" has been deleted!");
		expected.setPriority(Output.Priority.HIGH);
		assertEquals(expected, output9);
		expected.setPriority(Output.Priority.LOW);
		// ==================================================================================
		String input10 = "display all";
		Output output10 = controller.processInput(input10);
		expected.setOutput(expectedArrArrList);
		expected.setReturnMessage("All tasks are now displayed!");
		assertEquals(expected, output10);
		// ==================================================================================
		String input11 = "mark 2";
		Output output11 = controller.processInput(input11);
		expectedArrArrList.get(1).set(12, "DONE");
		expected.setOutput(clean());
		expected.setReturnMessage("\"send meeting minutes\" has been marked done.");
		assertEquals(expected, output11);
		// ==================================================================================
		String input12 = "display all";
		Output output12 = controller.processInput(input12);
		expected.setOutput(expectedArrArrList);
		expected.setReturnMessage("All tasks are now displayed!");
		assertEquals(expected, output12);
		// ==================================================================================
		String input13 = "unmark send meeting minutes";
		Output output13 = controller.processInput(input13);
		expectedArrArrList.get(1).set(12, "UNDONE");
		expected.setOutput(new ArrayList<ArrayList<String>>());
		expected.setReturnMessage("\"send meeting minutes\" has been marked undone.");
		assertEquals(expected, output13);
		// ==================================================================================
		String input14 = "display all";
		Output output14 = controller.processInput(input14);
		expected.setOutput(expectedArrArrList);
		expected.setReturnMessage("All tasks are now displayed!");
		assertEquals(expected, output14);
	}

	@Test
	public void systemTest2() {
		controller.processInput("delete all");
		// ==================================================================================
		ArrayList<String> task1 = new ArrayList<String>();
		task1.add("1");
		task1.add("make-up class");
		task1.add("8am");
		task1.addAll(getTodayInfo());
		task1.add("9:30am");
		task1.addAll(getTodayInfo());
		task1.add("UNDONE");
		task1.add("");

		ArrayList<String> task2 = new ArrayList<String>();
		LocalDateTime dt2 = LocalDateTime.now().with(DayOfWeek.MONDAY).plusWeeks(1).plusDays(4);
		task2.add("2");
		task2.add("alumni gathering");
		task2.add("7:15pm");
		task2.addAll(getDateInfo(dt2));
		task2.add("11:59pm");
		task2.addAll(getDateInfo(dt2));
		task2.add("UNDONE");
		task2.add("");

		ArrayList<String> task3 = new ArrayList<String>();
		LocalDateTime dt3 = LocalDateTime.now().with(DayOfWeek.MONDAY).minusWeeks(1).plusDays(6);
		task3.add("3");
		task3.add("birthday");
		task3.add("12am");
		task3.addAll(getDateInfo(dt3));
		task3.add("11:59pm");
		task3.addAll(getDateInfo(dt3));
		task3.add("UNDONE");
		task3.add("");

		ArrayList<String> task4 = new ArrayList<String>();
		LocalDateTime dt4 = LocalDateTime.now().plusDays(1);
		task4.add("4");
		task4.add("submit alumni report");
		task4.addAll(getEmptyDTInfo());
		task4.add("5am");
		task4.addAll(getDateInfo(dt4));
		task4.add("UNDONE");
		task4.add("false");

		ArrayList<String> task4b = new ArrayList<String>();
		task4b.add("4");
		task4b.add("submit alumni report");
		task4b.addAll(getEmptyDTInfo());
		task4b.add("5am");
		task4b.addAll(getDateInfo(dt4));
		task4b.add("UNDONE");
		task4b.add("false");

		ArrayList<String> task5 = new ArrayList<String>();
		task5.add("5");
		task5.add("buy bread");
		task5.addAll(getEmptyDTInfo());
		task5.addAll(getEmptyDTInfo());
		task5.add("UNDONE");
		task5.add("");

		ArrayList<String> task5b = new ArrayList<String>();
		task5b.add("5");
		task5b.add("agar agar");
		task5b.addAll(getEmptyDTInfo());
		task5b.addAll(getEmptyDTInfo());
		task5b.add("UNDONE");
		task5b.add("");
		// ==================================================================================
		String input1 = "create make-up class from today 8:00 to 09:30am";
		Output output1 = controller.processInput(input1);
		expectedArrArrList.add(task1);
		expected.setOutput(clean());
		expected.setReturnMessage("\"make-up class\" has been created!");
		assertEquals(expected, output1);
		// ==================================================================================
		String input2 = "create alumni gathering from 7:15PM to 23:59 next fri";
		Output output2 = controller.processInput(input2);
		expectedArrArrList.add(task2);
		expected.setOutput(clean());
		expected.setReturnMessage("\"alumni gathering\" has been created!");
		assertEquals(expected, output2);
		// ==================================================================================
		String input3 = "create birthday on last sun";
		Output output3 = controller.processInput(input3);
		expectedArrArrList.add(0, task3);
		expected.setOutput(clean());
		expected.setReturnMessage("\"birthday\" has been created!");
		assertEquals(expected, output3);
		// ==================================================================================
		String input4 = "create submit alumni report by 5am tmr";
		Output output4 = controller.processInput(input4);
		expectedArrArrList.add(2, task4);
		resetIndex();
		expected.setOutput(clean());
		expected.setReturnMessage("\"submit alumni report\" has been created!");
		assertEquals(expected, output4);
		// ==================================================================================
		String input5 = "create buy bread";
		Output output5 = controller.processInput(input5);
		expectedArrArrList.add(task5);
		expected.setOutput(clean());
		expected.setReturnMessage("\"buy bread\" has been created!");
		assertEquals(expected, output5);
		// ==================================================================================
		String input6 = "display all";
		Output output6 = controller.processInput(input6);
		expected.setOutput(expectedArrArrList);
		expected.setReturnMessage("All tasks are now displayed!");
		assertEquals(expected, output6);
		// ==================================================================================
		String input7 = "edit alumni to hello jello start to 5pm";
		Output output7 = controller.processInput(input7);
		expectedSearchList.add(new ArrayList<String>(task4));
		expectedSearchList.get(0).set(0, "1");
		expectedSearchList.add(new ArrayList<String>(task2));
		expectedSearchList.get(1).set(0, "2");
		expected.setOutput(expectedSearchList);
		expected.setReturnMessage("All tasks with keyword \"alumni\" are now displayed!");
		assertEquals(expected, output7);
		// ==================================================================================
		String input8 = "edit 1";
		Output output8 = controller.processInput(input8);
		expectedArrArrList.get(2).set(1, "hello jello");
		expected.setOutput(clean());
		expected.setReturnMessage("Invalid: Task specified does not have this operation.");
		expected.setPriority(Output.Priority.HIGH);
		assertEquals(expected, output8);
		expected.setPriority(Output.Priority.LOW);
		// ==================================================================================
		String input9 = "edit bread to agar agar";
		Output output9 = controller.processInput(input9);
		expectedSearchList = clean();
		expectedSearchList.add(new ArrayList<String>(task5));
		expectedSearchList.get(0).set(0, "1");
		expected.setOutput(expectedSearchList);
		expected.setReturnMessage("All tasks with keyword \"bread\" are now displayed!");
		assertEquals(expected, output9);
		// ==================================================================================
		String input10 = "edit 1";
		Output output10 = controller.processInput(input10);
		expectedArrArrList.set(4, task5b);
		expected.setOutput(clean());
		expected.setReturnMessage("\"buy bread\" has been edited!");
		assertEquals(expected, output10);
		// ==================================================================================
		String input11 = "display all";
		Output output11 = controller.processInput(input11);
		expected.setOutput(expectedArrArrList);
		expected.setReturnMessage("All tasks are now displayed!");
		assertEquals(expected, output11);
		// ==================================================================================
		String input12 = "undo";
		Output output12 = controller.processInput(input12);
		expectedArrArrList.set(4, task5);
		expected.setOutput(clean());
		expected.setReturnMessage("\"edit\" action has been undone!");
		assertEquals(expected, output12);
		// ==================================================================================
		String input13 = "mark birthday";
		Output output13 = controller.processInput(input13);
		expectedArrArrList.get(4).set(12, "DONE");
		expected.setOutput(clean());
		expected.setReturnMessage("\"birthday\" has been marked done.");
		assertEquals(expected, output13);
		// ==================================================================================
		String input14 = "unmark 1";
		Output output14 = controller.processInput(input14);
		expectedArrArrList.get(4).set(12, "UNDONE");
		expected.setOutput(clean());
		expected.setReturnMessage("\"birthday\" has been marked undone.");
		assertEquals(expected, output14);
		// ==================================================================================
		String input15 = "help";
		Output output15 = controller.processInput(input15);
		expected.setReturnMessage(" ");
		assertEquals(expected, output15);
		// ==================================================================================
		String input16 = "quit help";
		Output output16 = controller.processInput(input16);
		expected.setReturnMessage(" ");
		assertEquals(expected, output16);
		// ==================================================================================
		String input17 = "day";
		Output output17 = controller.processInput(input17);
		expected.setReturnMessage(" ");
		assertEquals(expected, output17);
		// ==================================================================================
		String input18 = "night";
		Output output18 = controller.processInput(input18);
		expected.setReturnMessage(" ");
		assertEquals(expected, output18);
		// ==================================================================================
		String input19 = "show year";
		Output output19 = controller.processInput(input19);
		expected.setReturnMessage(" ");
		assertEquals(expected, output19);
		// ==================================================================================
		String input20 = "hide year";
		Output output20 = controller.processInput(input20);
		expected.setReturnMessage(" ");
		assertEquals(expected, output20);
	}

	@Test
	public void systemTest3() {
		controller.processInput("delete all");
		// ==================================================================================
		ArrayList<String> task1 = new ArrayList<String>();
		LocalDateTime dt1 = LocalDateTime.parse("09 11 2015 " + Constants.DUMMY_TIME_S, Constants.DTFormatter);
		task1.add("1");
		task1.add("lab revision");
		task1.addAll(getEmptyDTInfo());
		task1.add("10:30pm");
		task1.addAll(getDateInfo(dt1));
		task1.add("UNDONE");
		task1.add("true");

		ArrayList<String> task2 = new ArrayList<String>();
		LocalDateTime dt2 = LocalDateTime.parse("29 02 2016 " + Constants.DUMMY_TIME_S, Constants.DTFormatter);
		task2.add("2");
		task2.add("prom");
		task2.add("6pm");
		task2.addAll(getDateInfo(dt2));
		task2.add("11pm");
		task2.addAll(getDateInfo(dt2));
		task2.add("UNDONE");
		task2.add("");

		ArrayList<String> task3 = new ArrayList<String>();
		LocalDateTime dt3 = LocalDateTime.parse("06 01 2016 " + Constants.DUMMY_TIME_S, Constants.DTFormatter);
		task3.add("3");
		task3.add("birthday");
		task3.add("12am");
		task3.addAll(getDateInfo(dt3));
		task3.add("11:59pm");
		task3.addAll(getDateInfo(dt3));
		task3.add("UNDONE");
		task3.add("");

		ArrayList<String> task4 = new ArrayList<String>();
		LocalDateTime dt4 = LocalDateTime.parse("22 01 2016 " + Constants.DUMMY_TIME_S, Constants.DTFormatter);
		task4.add("4");
		task4.add("submit alumni report");
		task4.addAll(getEmptyDTInfo());
		task4.add("5am");
		task4.addAll(getDateInfo(dt4));
		task4.add("UNDONE");
		task4.add("false");

		ArrayList<String> task5 = new ArrayList<String>();
		LocalDateTime dt5a = LocalDateTime.parse("10 03 2016 " + Constants.DUMMY_TIME_S, Constants.DTFormatter);
		LocalDateTime dt5b = LocalDateTime.parse("02 04 2016 " + Constants.DUMMY_TIME_S, Constants.DTFormatter);
		task5.add("5");
		task5.add("staycation");
		task5.add("9am");
		task5.addAll(getDateInfo(dt5a));
		task5.add("9pm");
		task5.addAll(getDateInfo(dt5b));
		task5.add("UNDONE");
		task5.add("");

		ArrayList<String> task5b = new ArrayList<String>();
		LocalDateTime dt5c = LocalDateTime.parse("05 05 2016 " + Constants.DUMMY_TIME_S, Constants.DTFormatter);
		task5b.add("5");
		task5b.add("staycation");
		task5b.add("9am");
		task5b.addAll(getDateInfo(dt5c));
		task5b.add("9pm");
		task5b.addAll(getDateInfo(dt5c));
		task5b.add("UNDONE");
		task5b.add("");

		ArrayList<String> task6 = new ArrayList<String>();
		task6.add("5");
		task6.add("dance in the rain");
		task6.addAll(getEmptyDTInfo());
		task6.addAll(getEmptyDTInfo());
		task6.add("UNDONE");
		task6.add("");
		// ==================================================================================
		String input1 = "create lab revision by 10.30pm 9nov 2015";
		Output output1 = controller.processInput(input1);
		expectedArrArrList.add(task1);
		expected.setOutput(clean());
		expected.setReturnMessage("\"lab revision\" has been created!");
		assertEquals(expected, output1);
		// ==================================================================================
		String input2 = "create prom from 6pm to 11pm 29 february 2016";
		Output output2 = controller.processInput(input2);
		expectedArrArrList.add(task2);
		resetIndex();
		expected.setOutput(clean());
		expected.setReturnMessage("\"prom\" has been created!");
		assertEquals(expected, output2);
		// ==================================================================================
		String input3 = "create birthday on 6/1/2016";
		Output output3 = controller.processInput(input3);
		expectedArrArrList.add(1, task3);
		resetIndex();
		expected.setOutput(clean());
		expected.setReturnMessage("\"birthday\" has been created!");
		assertEquals(expected, output3);
		// ==================================================================================
		String input4 = "create submit alumni report by 5am 22-1-2016";
		Output output4 = controller.processInput(input4);
		expectedArrArrList.add(2, task4);
		resetIndex();
		expected.setOutput(clean());
		expected.setReturnMessage("\"submit alumni report\" has been created!");
		assertEquals(expected, output4);
		// ==================================================================================
		String input5 = "create staycation from 9am 10march 2016 to 9pm 2 apr 2016";
		Output output5 = controller.processInput(input5);
		expectedArrArrList.add(task5);
		resetIndex();
		expected.setOutput(clean());
		expected.setReturnMessage("\"staycation\" has been created!");
		assertEquals(expected, output5);
		// ==================================================================================
		String input6 = "create dance in the rain";
		Output output6 = controller.processInput(input6);
		expectedArrArrList.add(task6);
		resetIndex();
		expected.setOutput(clean());
		expected.setReturnMessage("\"dance in the rain\" has been created!");
		assertEquals(expected, output6);
		// ==================================================================================
		String input7 = "display all";
		Output output7 = controller.processInput(input7);
		expected.setOutput(expectedArrArrList);
		expected.setReturnMessage("All tasks are now displayed!");
		assertEquals(expected, output7);
		// ==================================================================================
		String input8 = "create ";
		Output output8 = controller.processInput(input8);
		expected.setOutput(clean());
		expected.setReturnMessage("Invalid Command!");
		expected.setPriority(Output.Priority.HIGH);
		assertEquals(expected, output8);
		expected.setPriority(Output.Priority.LOW);
		// ==================================================================================
		String input9 = "edit staycation start to 5/5/2016 end to 5/5/2016";
		Output output9 = controller.processInput(input9);
		expectedArrArrList.set(4, task5b);
		resetIndex();
		expected.setOutput(clean());
		expected.setReturnMessage("\"staycation\" has been edited!");
		assertEquals(expected, output9);
		// ==================================================================================
		String input10 = "display all";
		Output output10 = controller.processInput(input10);
		expected.setOutput(expectedArrArrList);
		expected.setReturnMessage("All tasks are now displayed!");
		assertEquals(expected, output10);
		// ==================================================================================
		String input11 = "search 29/02/2016";
		Output output11 = controller.processInput(input11);
		expectedSearchList = clean();
		task2.set(0, "1");
		expectedSearchList.add(task2);
		expected.setOutput(expectedSearchList);
		expected.setReturnMessage("All tasks with date \"29 02 2016\" are now displayed!");
		assertEquals(expected, output11);
		// ==================================================================================
		String input12 = "search staycation";
		Output output12 = controller.processInput(input12);
		expectedSearchList = clean();
		task5b.set(0, "1");
		expectedSearchList.add(task5b);
		expected.setOutput(expectedSearchList);
		expected.setReturnMessage("All tasks with keyword \"staycation\" are now displayed!");
		assertEquals(expected, output12);
		// ==================================================================================
		String input13 = "delete 1";
		Output output13 = controller.processInput(input13);
		expectedArrArrList.remove(4);
		resetIndex();
		expected.setOutput(clean());
		expected.setReturnMessage("\"staycation\" has been deleted!");
		expected.setPriority(Output.Priority.HIGH);
		assertEquals(expected, output13);
		expected.setPriority(Output.Priority.LOW);
		// ==================================================================================
		String input14 = "display all";
		Output output14 = controller.processInput(input14);
		expected.setOutput(expectedArrArrList);
		expected.setReturnMessage("All tasks are now displayed!");
		assertEquals(expected, output14);
		// ==================================================================================
		String input15 = "mark 1";
		Output output15 = controller.processInput(input15);
		expectedArrArrList.get(0).set(12, "DONE");
		expectedArrArrList.get(0).set(13, "false");
		expected.setOutput(clean());
		expected.setReturnMessage("\"lab revision\" has been marked done.");
		assertEquals(expected, output15);
		// ==================================================================================
		String input16 = "rubbish";
		Output output16 = controller.processInput(input16);
		expected.setOutput(clean());
		expected.setReturnMessage("Invalid Command!");
		expected.setPriority(Output.Priority.HIGH);
		assertEquals(expected, output16);
		expected.setPriority(Output.Priority.LOW);
		// ==================================================================================
		String input17 = "display floating";
		Output output17 = controller.processInput(input17);
		expectedSearchList = clean();
		task6.set(0, "1");
		expectedSearchList.add(task6);
		expected.setOutput(expectedSearchList);
		expected.setReturnMessage("All floating tasks are now displayed!");
		assertEquals(expected, output17);
		// ==================================================================================
		String input18 = "display done";
		Output output18 = controller.processInput(input18);
		expectedSearchList = clean();
		task1.set(0, "1");
		expectedSearchList.add(task1);
		expected.setOutput(expectedSearchList);
		expected.setReturnMessage("All DONE tasks are now displayed!");
		assertEquals(expected, output18);
		// ==================================================================================
		String input19 = "display unmark";
		Output output19 = controller.processInput(input19);
		expectedArrArrList.remove(0);
		resetIndex();
		expected.setOutput(expectedArrArrList);
		expected.setReturnMessage("All UNDONE tasks are now displayed!");
		assertEquals(expected, output19);
	}

}
