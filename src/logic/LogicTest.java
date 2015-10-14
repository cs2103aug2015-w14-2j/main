package logic;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.junit.Test;

import parser.CreateCommand;
import parser.DisplayCommand;
import parser.EditCommand;
import shared.AbstractTask;
import shared.BoundedTask;
import shared.DeadlineTask;
import shared.FloatingTask;
import shared.Output;


public class LogicTest {
	private Logic logic = new Logic();
	DateTimeFormatter DTFormatter = DateTimeFormatter.ofPattern("dd MM yyyy HH mm");
	LocalDateTime dummyStart = LocalDateTime.parse("12 10 2015 08 00", DTFormatter);
	LocalDateTime dummyEnd = LocalDateTime.parse("13 10 2015 08 00", DTFormatter);
	
	@Test
	public void createFloatingTask() {
		logic.setTaskListTest(new ArrayList<AbstractTask>());
		CreateCommand testCommand = new CreateCommand("meeting");
		Output output = logic.executeCommand(testCommand);
		Output expected = new Output();
		expected.setReturnMessage("\"meeting\" has been successfully created!");
		assertEquals(expected, output);
		FloatingTask expectedTask = new FloatingTask("meeting");
		AbstractTask createdTask = (logic.getTaskListTest()).get(0);
		assertEquals(expectedTask, createdTask);
	}
	
	@Test
	public void createDeadlineTask() {
		logic.setTaskListTest(new ArrayList<AbstractTask>());
		CreateCommand testCommand = new CreateCommand("assignment", dummyEnd);
		Output output = logic.executeCommand(testCommand);
		Output expected = new Output();
		expected.setReturnMessage("\"assignment\" has been successfully created!");
		assertEquals(expected, output);
		DeadlineTask expectedTask = new DeadlineTask("assignment", dummyEnd);
		AbstractTask createdTask = (logic.getTaskListTest()).get(0);
		assertEquals(expectedTask, createdTask);
	}
	
	@Test
	public void createBoundedTask() {
		logic.setTaskListTest(new ArrayList<AbstractTask>());
		CreateCommand testCommand = new CreateCommand("dinner", dummyStart, dummyEnd);
		Output output = logic.executeCommand(testCommand);
		Output expected = new Output();
		expected.setReturnMessage("\"dinner\" has been successfully created!");
		assertEquals(expected, output);
		BoundedTask expectedTask = new BoundedTask("dinner", dummyStart, dummyEnd);
		AbstractTask createdTask = (logic.getTaskListTest()).get(0);
		assertEquals(expectedTask, createdTask);
	}
	
	@Test
	public void displayAllTasks() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		mockTaskList.add(new FloatingTask("birthday"));
		mockTaskList.add(new DeadlineTask("assignment", dummyEnd));
		mockTaskList.add(new BoundedTask("dinner", dummyStart, dummyEnd));
		
		logic.setTaskListTest(mockTaskList);
		
		DisplayCommand testCommand = new DisplayCommand(DisplayCommand.Scope.ALL);	
		Output output = logic.executeCommand(testCommand);
		
		Output expected = new Output();
		ArrayList<ArrayList<String>> expectedList = new ArrayList<ArrayList<String>>();
		ArrayList<String> expectedFloatingTask = new ArrayList<String>();
		expectedFloatingTask.add("1.");
		expectedFloatingTask.add("birthday");
		expectedFloatingTask.add("");
		expectedFloatingTask.add("");
		expectedFloatingTask.add("");
		expectedFloatingTask.add("");
		
		ArrayList<String> expectedDeadlineTask = new ArrayList<String>();
		expectedDeadlineTask.add("2.");
		expectedDeadlineTask.add("assignment");
		expectedDeadlineTask.add("");
		expectedDeadlineTask.add("");
		expectedDeadlineTask.add("08:00");
		expectedDeadlineTask.add("13-10-2015");
		
		ArrayList<String> expectedBoundedTask = new ArrayList<String>();
		expectedBoundedTask.add("3.");
		expectedBoundedTask.add("dinner");
		expectedBoundedTask.add("08:00");
		expectedBoundedTask.add("12-10-2015");
		expectedBoundedTask.add("08:00");
		expectedBoundedTask.add("13-10-2015");
		
		expectedList.add(expectedFloatingTask);
		expectedList.add(expectedDeadlineTask);
		expectedList.add(expectedBoundedTask);
		expected.setOutput(expectedList);
		expected.setReturnMessage("all tasks are now displayed!");
		
		assertEquals(expected, output);
		assertEquals(mockTaskList, logic.getLastDisplayed());
	}
	
	@Test
	public void indexUpdateWithoutDisplay() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		mockTaskList.add(new FloatingTask("birthday"));
		
		logic.setTaskListTest(mockTaskList);
		
		EditCommand testCommand = new EditCommand(1);
		ArrayList<EditCommand.editField> editFields = new ArrayList<EditCommand.editField>();
		editFields.add(EditCommand.editField.NAME);
		testCommand.setEditFields(editFields);
		testCommand.setNewName("assignment");
		Output output = logic.executeCommand(testCommand);
		
		Output expected = new Output();
		expected.setReturnMessage("Please display tasks at least once to edit by index");
		
		assertEquals(expected, output);
	}
	
	@Test
	public void editTaskNameByIndex() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		mockTaskList.add(new FloatingTask("birthday"));
//		mockTaskList.add(new DeadlineTask("assignment", "23 59", "06 10 2015"));
//		mockTaskList.add(new BoundedTask("dinner", "19 00", "07 10 2015", "22 00", "07 10 2015"));
		
		logic.setTaskListTest(mockTaskList);
		logic.setLastDisplayed(mockTaskList);
		
		EditCommand testCommand = new EditCommand(1);
		ArrayList<EditCommand.editField> editFields = new ArrayList<EditCommand.editField>();
		editFields.add(EditCommand.editField.NAME);
		testCommand.setEditFields(editFields);
		testCommand.setNewName("assignment");
		Output output = logic.executeCommand(testCommand);
		
		Output expected = new Output();
		expected.setReturnMessage("Edit done successfully!");
		
		assertEquals(expected, output);
		
		AbstractTask editedTask = logic.getTaskListTest().get(0);
		FloatingTask expectedTask = new FloatingTask("assignment");
		assertEquals(expectedTask, editedTask);
	}
	
	@Test
	public void editTaskStartDateByIndex() {
		
	}
	
	@Test
	public void editTaskStartTimeByIndex() {
		
	}
	
//	@Test
//	public void editTaskEndByIndex() {
//		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
//		mockTaskList.add(new BoundedTask("dinner", "19 00", "07 10 2015", "22 00", "07 10 2015"));
//		
//		logic.setTaskListTest(mockTaskList);
//		logic.setLastDisplayed(mockTaskList);
//		
//		ArrayList<String> testInput = new ArrayList<String>();		
//		testInput.add("edit");
//		testInput.add("end");
//		testInput.add("#1");
//		testInput.add("20 00");
//		testInput.add("08 10 2015");
//		testInput.add("");
//		
//		ArrayList<ArrayList<String>> expectedOutput = new ArrayList<ArrayList<String>>();
//		ArrayList<String> expectedReturnMessage = new ArrayList<String>();
//		
//		expectedReturnMessage.add("end time and date of \"dinner\" has been successfully changed to \"20:00 08-10-2015\"!");
//		expectedOutput.add(expectedReturnMessage);
//		assertEquals(expectedOutput, logic.executeCommand(testInput));
//		AbstractTask editedTask = logic.getTaskListTest().get(0);
//		assertEquals("20:00", ((BoundedTask) editedTask).getEndTime());
//		assertEquals("08-10-2015", ((BoundedTask) editedTask).getEndDate());
//	}
//	
//	@Test
//	public void indexDeleteWithoutDisplay() {
//		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
//		mockTaskList.add(new FloatingTask("birthday"));
//		
//		logic.setTaskListTest(mockTaskList);
//		
//		ArrayList<String> testInput = new ArrayList<String>();		
//		testInput.add("delete");
//		testInput.add("#1");
//		testInput.add("");
//		testInput.add("");
//		testInput.add("");
//		testInput.add("");
//		
//		ArrayList<ArrayList<String>> expectedOutput = new ArrayList<ArrayList<String>>();
//		ArrayList<String> expectedReturnMessage = new ArrayList<String>();
//		
//		expectedReturnMessage.add("Please display tasks at least once to delete by index");
//		expectedOutput.add(expectedReturnMessage);
//		assertEquals(expectedOutput, logic.executeCommand(testInput));
//	}
//	
//	@Test
//	public void deleteTaskByIndex() {
//		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
//		FloatingTask testTask = new FloatingTask("birthday");
//		mockTaskList.add(testTask);
//		
//		logic.setTaskListTest(mockTaskList);
//		logic.setLastDisplayed(mockTaskList);
//		
//		ArrayList<String> testInput = new ArrayList<String>();		
//		testInput.add("delete");
//		testInput.add("#1");
//		testInput.add("");
//		testInput.add("");
//		testInput.add("");
//		testInput.add("");
//		
//		ArrayList<ArrayList<String>> expectedOutput = new ArrayList<ArrayList<String>>();
//		ArrayList<String> expectedReturnMessage = new ArrayList<String>();
//		
//		expectedReturnMessage.add("\"birthday\" has been deleted!");
//		expectedOutput.add(expectedReturnMessage);
//		assertEquals(expectedOutput, logic.executeCommand(testInput));
//		assertEquals(false, logic.getTaskListTest().contains(testTask));
//	}

}
