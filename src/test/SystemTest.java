package test;
import static org.junit.Assert.*;
import static org.loadui.testfx.Assertions.verifyThat;
import static org.loadui.testfx.controls.Commons.hasText;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.loadui.testfx.utils.FXTestUtils;

import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import shared.Constants;
import shared.Output;
import ui.Main;
import ui.view.OverviewController;

//[IMPORTANT] Moving your mouse or typing during testing will stop it.

// @@author A0131188H (Hi, you need to write this line below UI testing)
public class SystemTest {
	
	//@@author A0133888N
	private static GuiTest uiController;
	private static Main mainApp;

	@BeforeClass
	public static void setUpClass() {
		FXTestUtils.launchApp(Main.class);
		
		uiController = new GuiTest() {
			@Override
			protected Parent getRootNode() {
				return mainApp.getPrimaryStage().getScene().getRoot();
			}
		};
	}
	
	/**
	 * The app needs a short time to start, while the testing starts right away.
	 * Therefore, there need to be a bit buffer time, or the test input is not entered in
	 * the app, but somewhere else (e.g. This file), leading to test failure.
	 */
	public static void pause() {
		try {
			Thread.sleep(350);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test public void uiTest() {
		testInputClear();
		testInvalid();
		testCreate();
		testImmediateHelpMessage();
		testMark();
		testGetLastInput();
	}
	
	public void testInputClear() {
		pause();
		uiController.type("tests start");
		uiController.push(KeyCode.ENTER);	
		uiController.type("create assignment 1");
		uiController.push(KeyCode.ENTER);	
		verifyThat("#input", hasText(""));
	}
	
	public void testInvalid() {
		pause();
		uiController.type("delete all");
		uiController.push(KeyCode.ENTER);	
		uiController.type("an invalid input");
		uiController.push(KeyCode.ENTER);	
		verifyThat("#returnMessageLabel", hasText("Invalid Command!"));
	}
	
	public void testCreate() {
		pause();
		uiController.type("delete all");
		uiController.push(KeyCode.ENTER);	
		uiController.type("create event1 from 10am today to 12pm tmr");
		uiController.push(KeyCode.ENTER);	
		verifyThat("#returnMessageLabel", hasText("\"event1\" has been created!"));
		uiController.type("create event2 from 10am today to 12pm today");
		uiController.push(KeyCode.ENTER);	
		verifyThat("#returnMessageLabel", hasText("\"event2\" has been created!"));
		uiController.type("create event3 by 11pm today");
		uiController.push(KeyCode.ENTER);	
		verifyThat("#returnMessageLabel", hasText("\"event3\" has been created!"));
	}
	
	public void testImmediateHelpMessage() {
		pause();
		uiController.push(KeyCode.ENTER);	
		uiController.type("create");	
		verifyThat("#helpMessageLabel", hasText(Constants.HELP_MESSAGE_CREATE));
		uiController.push(KeyCode.ENTER);
		uiController.type("edit");	
		verifyThat("#helpMessageLabel", hasText(Constants.HELP_MESSAGE_EDIT));
		uiController.push(KeyCode.ENTER);
		uiController.type("display");	
		verifyThat("#helpMessageLabel", hasText(Constants.HELP_MESSAGE_DISPLAY));
		uiController.push(KeyCode.ENTER);
		uiController.type("delete");	
		verifyThat("#helpMessageLabel", hasText(Constants.HELP_MESSAGE_DELETE));
		uiController.push(KeyCode.ENTER);
		uiController.type("undo");	
		verifyThat("#helpMessageLabel", hasText(Constants.HELP_MESSAGE_UNDO));
		uiController.push(KeyCode.ENTER);
		uiController.type("mark");	
		verifyThat("#helpMessageLabel", hasText(Constants.HELP_MESSAGE_MARK));
		uiController.push(KeyCode.ENTER);
		uiController.type("search");	
		verifyThat("#helpMessageLabel", hasText(Constants.HELP_MESSAGE_SEARCH));
		uiController.push(KeyCode.ENTER);
		uiController.type("save");	
		verifyThat("#helpMessageLabel", hasText(Constants.HELP_MESSAGE_SAVE));
		uiController.push(KeyCode.ENTER);
		uiController.type("help");	
		verifyThat("#helpMessageLabel", hasText(Constants.HELP_MESSAGE_HELP));
		uiController.push(KeyCode.ENTER);
		uiController.type("quit help");
		uiController.push(KeyCode.ENTER);
	}
	
	private void testMark() {
		pause();
		uiController.type("delete all");
		uiController.push(KeyCode.ENTER);
		uiController.type("create a task");
		uiController.push(KeyCode.ENTER);	
		uiController.type("mark 1");
		uiController.push(KeyCode.ENTER);	
		verifyThat("#returnMessageLabel", hasText("\"a task\" has been marked done."));
	}
	
	private void testGetLastInput() {
		pause();
		uiController.type("delete all");
		uiController.push(KeyCode.ENTER);
		uiController.type("create meeting");
		uiController.push(KeyCode.ENTER);
		uiController.push(KeyCode.UP);
		verifyThat("#input", hasText("create meeting"));
		uiController.push(KeyCode.DOWN);
		verifyThat("#input", hasText(""));
	}

	
	private OverviewController controller;
	private Output expected;
	ArrayList<ArrayList<String>> expectedArrArrList;
	ArrayList<ArrayList<String>> expectedSearchList;
	
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
	
	private ArrayList<String> getTodayInfo() {
		LocalDateTime now = LocalDateTime.now();
		ArrayList<String> answer = new ArrayList<String>();
		answer.add(getDayOfWeek(now));
		answer.add("TODAY");
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
		expectedSearchList = new ArrayList<ArrayList<String>>();
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
		String input10 = "display all";
		Output output10 = controller.processInput(input10);
		expected.setOutput(expectedArrArrList);
		expected.setReturnMessage("All tasks are now displayed!");
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
		String input14 = "display all";
		Output output14 = controller.processInput(input14);
		expected.setOutput(expectedArrArrList);
		expected.setReturnMessage("All tasks are now displayed!");
		assertEquals(expected, output14);
	}
	
	@Test
	public void systemTest2() {
		expectedArrArrList = new ArrayList<ArrayList<String>>();
		controller.processInput("delete all");
		//==================================================================================
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
		LocalDateTime dt2 = LocalDateTime.parse("28 11 " + getCorrectYear("28 11") + " " + Constants.sDummyTime, Constants.DTFormatter);
		task2.add("2");
		task2.add("alumni gathering");
		task2.add("7:15pm");
		task2.addAll(getDateInfo(dt2));
		task2.add("11:59pm");
		task2.addAll(getDateInfo(dt2));
		task2.add("UNDONE");
		task2.add("");
		
		ArrayList<String> task3 = new ArrayList<String>();
		LocalDateTime dt3 = LocalDateTime.parse("20 01 2016 " + Constants.sDummyTime, Constants.DTFormatter);
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
		//==================================================================================
		String input1 = "create make-up class from today 8:00 to 09:30am";
		Output output1 = controller.processInput(input1);
		expectedArrArrList.add(task1);
		expected.setOutput(clean());
		expected.setReturnMessage("\"make-up class\" has been created!");
		assertEquals(expected, output1);
		//==================================================================================
		String input2 = "create alumni gathering from 7:15PM to 23:59 28 nov";
		Output output2 = controller.processInput(input2);
		expectedArrArrList.add(task2);
		expected.setOutput(clean());
		expected.setReturnMessage("\"alumni gathering\" has been created!");
		assertEquals(expected, output2);
		//==================================================================================
		String input3 = "create birthday on 20jan 2016";
		Output output3 = controller.processInput(input3);
		expectedArrArrList.add(task3);
		expected.setOutput(clean());
		expected.setReturnMessage("\"birthday\" has been created!");
		assertEquals(expected, output3);
		//==================================================================================
		String input4 = "create submit alumni report by 5am tmr";
		Output output4 = controller.processInput(input4);
		expectedArrArrList.add(1, task4);
		resetIndex();		
		expected.setOutput(clean());
		expected.setReturnMessage("\"submit alumni report\" has been created!");
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
		String input7 = "edit alumni to hello jello start to 5pm";
		Output output7 = controller.processInput(input7);
		expectedSearchList.add(new ArrayList<String>(task4));
		expectedSearchList.get(0).set(0, "1");
		expectedSearchList.add(new ArrayList<String>(task2));
		expectedSearchList.get(1).set(0, "2");
		expected.setOutput(expectedSearchList);
		expected.setReturnMessage("All tasks with keyword \"alumni\" are now displayed!");
		assertEquals(expected, output7);
		//==================================================================================
		String input8 = "edit 1";
		Output output8 = controller.processInput(input8);
		expectedArrArrList.get(1).set(1, "hello jello");
		expected.setOutput(clean());
		expected.setReturnMessage("Invalid: Task specified does not have this operation.");
		expected.setPriority(Output.Priority.HIGH);
		assertEquals(expected, output8);
		expected.setPriority(Output.Priority.LOW);
		//==================================================================================
		String input9 = "edit bread to agar agar";
		Output output9 = controller.processInput(input9);
		expectedSearchList = clean();
		expectedSearchList.add(new ArrayList<String>(task5));
		expectedSearchList.get(0).set(0, "1");
		expected.setOutput(expectedSearchList);
		expected.setReturnMessage("All tasks with keyword \"bread\" are now displayed!");
		assertEquals(expected, output9);
		//==================================================================================
		String input10 = "edit 1";
		Output output10 = controller.processInput(input10);
		expectedArrArrList.set(4, task5b);
		expected.setOutput(clean());
		expected.setReturnMessage("\"buy bread\" has been edited!");
		assertEquals(expected, output10);
		//==================================================================================
		String input11 = "display all";
		Output output11 = controller.processInput(input11);
		expected.setOutput(expectedArrArrList);
		expected.setReturnMessage("All tasks are now displayed!");
		assertEquals(expected, output11);
		//==================================================================================
		String input12 = "undo";
		Output output12 = controller.processInput(input12);
		expectedArrArrList.set(4, task5);
		expected.setOutput(clean());
		expected.setReturnMessage("\"edit\" action has been undone!");
		assertEquals(expected, output12);
		//==================================================================================
		String input13 = "mark birthday";
		Output output13 = controller.processInput(input13);
		expectedArrArrList.get(4).set(12, "DONE");
		expected.setOutput(clean());
		expected.setReturnMessage("\"birthday\" has been marked done.");
		assertEquals(expected, output13);
		//==================================================================================
		String input14 = "unmark 4";
		Output output14 = controller.processInput(input14);
		expectedArrArrList.get(4).set(12, "UNDONE");
		expected.setOutput(clean());
		expected.setReturnMessage("\"birthday\" has been marked undone.");
		assertEquals(expected, output14);
		//==================================================================================
		String input15 = "help";
		Output output15 = controller.processInput(input15);
		expected.setReturnMessage(" ");
		assertEquals(expected, output15);
		//==================================================================================
		String input16 = "quit help";
		Output output16 = controller.processInput(input16);
		expected.setReturnMessage(" ");
		assertEquals(expected, output16);
		//==================================================================================
		String input17 = "day";
		Output output17 = controller.processInput(input17);
		expected.setReturnMessage(" ");
		assertEquals(expected, output17);
		//==================================================================================
		String input18 = "night";
		Output output18 = controller.processInput(input18);
		expected.setReturnMessage(" ");
		assertEquals(expected, output18);
		//==================================================================================
		String input19 = "show year";
		Output output19 = controller.processInput(input19);
		expected.setReturnMessage(" ");
		assertEquals(expected, output19);
		//==================================================================================
		String input20 = "hide year";
		Output output20 = controller.processInput(input20);
		expected.setReturnMessage(" ");
		assertEquals(expected, output20);
	}

	public String getCorrectYear(String str) {
		LocalDateTime dt = LocalDateTime.now();
		String[] strParts = str.split(" ");
		String day = strParts[0];
		String month = strParts[1];
		String year;
		
		if (Integer.parseInt(month) < dt.getMonthValue()) {
			year = String.valueOf(dt.plusYears(1).getYear());
		} else if (Integer.parseInt(month) == dt.getMonthValue() && Integer.parseInt(day) < dt.getDayOfMonth()) {
			year = String.valueOf(dt.plusYears(1).getYear());
		} else {
			year = String.valueOf(dt.getYear());
		}
		
		return year;
	}
}
