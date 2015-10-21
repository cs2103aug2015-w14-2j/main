package logic;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.junit.Test;

import shared.Output;
import shared.command.CreateCommand;
import shared.command.DeleteCommand;
import shared.command.DisplayCommand;
import shared.command.EditCommand;
import shared.command.DeleteCommand.Scope;
import shared.command.MarkCommand;
import shared.command.MarkCommand.markField;
import shared.task.AbstractTask;
import shared.task.BoundedTask;
import shared.task.DeadlineTask;
import shared.task.FloatingTask;
import shared.task.AbstractTask.Status;


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
		expectedFloatingTask.add("1");
		expectedFloatingTask.add("birthday");
		expectedFloatingTask.add("");
		expectedFloatingTask.add("");
		expectedFloatingTask.add("");
		expectedFloatingTask.add("");
		
		ArrayList<String> expectedDeadlineTask = new ArrayList<String>();
		expectedDeadlineTask.add("2");
		expectedDeadlineTask.add("assignment");
		expectedDeadlineTask.add("");
		expectedDeadlineTask.add("");
		expectedDeadlineTask.add("08:00");
		expectedDeadlineTask.add("13-10-2015");
		
		ArrayList<String> expectedBoundedTask = new ArrayList<String>();
		expectedBoundedTask.add("3");
		expectedBoundedTask.add("dinner");
		expectedBoundedTask.add("08:00");
		expectedBoundedTask.add("12-10-2015");
		expectedBoundedTask.add("08:00");
		expectedBoundedTask.add("13-10-2015");
		
		expectedList.add(expectedFloatingTask);
		expectedList.add(expectedDeadlineTask);
		expectedList.add(expectedBoundedTask);
		expected.setOutput(expectedList);
		expected.setReturnMessage("All tasks are now displayed!");
		
		assertEquals(expected, output);
		ArrayList<AbstractTask> expectedTaskList = new ArrayList<AbstractTask>();
		expectedTaskList.add(new FloatingTask("birthday"));
		expectedTaskList.add(new DeadlineTask("assignment", dummyEnd));
		expectedTaskList.add(new BoundedTask("dinner", dummyStart, dummyEnd));
		assertEquals(expectedTaskList, logic.getLastDisplayedTest());
	}
	
	@Test
	public void displayDoneTasks() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		FloatingTask doneFloating = new FloatingTask("birthday");
		doneFloating.setStatus(Status.DONE);
		mockTaskList.add(doneFloating);
		mockTaskList.add(new DeadlineTask("assignment", dummyEnd));
		mockTaskList.add(new BoundedTask("dinner", dummyStart, dummyEnd));
		
		logic.setTaskListTest(mockTaskList);
		
		DisplayCommand testCommand = new DisplayCommand(DisplayCommand.Scope.DONE);	
		Output output = logic.executeCommand(testCommand);
		
		Output expected = new Output();
		ArrayList<ArrayList<String>> expectedList = new ArrayList<ArrayList<String>>();
		ArrayList<String> expectedFloatingTask = new ArrayList<String>();
		expectedFloatingTask.add("1");
		expectedFloatingTask.add("birthday");
		expectedFloatingTask.add("");
		expectedFloatingTask.add("");
		expectedFloatingTask.add("");
		expectedFloatingTask.add("");
		
		expectedList.add(expectedFloatingTask);
		expected.setOutput(expectedList);
		expected.setReturnMessage("All tasks that are DONE are now displayed!");
		assertEquals(expected, output);
		ArrayList<AbstractTask> expectedLastDisplayed = new ArrayList<AbstractTask>();
		expectedLastDisplayed.add(doneFloating);
		assertEquals(expectedLastDisplayed, logic.getLastDisplayedTest());
	}
	
	@Test
	public void displayUndoneTasks() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		FloatingTask doneFloating = new FloatingTask("birthday");
		doneFloating.setStatus(Status.DONE);
		mockTaskList.add(doneFloating);
		mockTaskList.add(new DeadlineTask("assignment", dummyEnd));
		mockTaskList.add(new BoundedTask("dinner", dummyStart, dummyEnd));
		
		logic.setTaskListTest(mockTaskList);
		
		DisplayCommand testCommand = new DisplayCommand(DisplayCommand.Scope.UNDONE);	
		Output output = logic.executeCommand(testCommand);
		
		Output expected = new Output();
		ArrayList<ArrayList<String>> expectedList = new ArrayList<ArrayList<String>>();
		ArrayList<String> expectedDeadlineTask = new ArrayList<String>();
		expectedDeadlineTask.add("1");
		expectedDeadlineTask.add("assignment");
		expectedDeadlineTask.add("");
		expectedDeadlineTask.add("");
		expectedDeadlineTask.add("08:00");
		expectedDeadlineTask.add("13-10-2015");
		
		ArrayList<String> expectedBoundedTask = new ArrayList<String>();
		expectedBoundedTask.add("2");
		expectedBoundedTask.add("dinner");
		expectedBoundedTask.add("08:00");
		expectedBoundedTask.add("12-10-2015");
		expectedBoundedTask.add("08:00");
		expectedBoundedTask.add("13-10-2015");
		
		expectedList.add(expectedDeadlineTask);
		expectedList.add(expectedBoundedTask);
		expected.setOutput(expectedList);
		expected.setReturnMessage("All tasks that are UNDONE are now displayed!");
		assertEquals(expected, output);
		ArrayList<AbstractTask> expectedLastDisplayed = new ArrayList<AbstractTask>();
		expectedLastDisplayed.add(new DeadlineTask("assignment", dummyEnd));
		expectedLastDisplayed.add(new BoundedTask("dinner", dummyStart, dummyEnd));
		
		assertEquals(expectedLastDisplayed, logic.getLastDisplayedTest());
	}
	
	@Test 
	public void displayByKeyword() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		mockTaskList.add(new FloatingTask("birthday"));
		mockTaskList.add(new DeadlineTask("examday", dummyEnd));
		mockTaskList.add(new BoundedTask("dinner", dummyStart, dummyEnd));
		
		logic.setTaskListTest(mockTaskList);

		DisplayCommand testCommand = new DisplayCommand("day");	
		Output output = logic.executeCommand(testCommand);
		
		Output expected = new Output();
		ArrayList<ArrayList<String>> expectedList = new ArrayList<ArrayList<String>>();
		ArrayList<String> expectedFloatingTask = new ArrayList<String>();
		expectedFloatingTask.add("1");
		expectedFloatingTask.add("birthday");
		expectedFloatingTask.add("");
		expectedFloatingTask.add("");
		expectedFloatingTask.add("");
		expectedFloatingTask.add("");
		
		ArrayList<String> expectedDeadlineTask = new ArrayList<String>();
		expectedDeadlineTask.add("2");
		expectedDeadlineTask.add("examday");
		expectedDeadlineTask.add("");
		expectedDeadlineTask.add("");
		expectedDeadlineTask.add("08:00");
		expectedDeadlineTask.add("13-10-2015");
		
		expectedList.add(expectedFloatingTask);
		expectedList.add(expectedDeadlineTask);
		expected.setOutput(expectedList);
		expected.setReturnMessage("All tasks with keyword \"day\" are now displayed!");
		
		assertEquals(expected, output);
		ArrayList<AbstractTask> expectedLastDisplayed = new ArrayList<AbstractTask>();
		expectedLastDisplayed.add(new FloatingTask("birthday"));
		expectedLastDisplayed.add(new DeadlineTask("examday", dummyEnd));
		assertEquals(expectedLastDisplayed, logic.getLastDisplayedTest());
	}
	
	@Test
	public void displayByDate() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		FloatingTask datelessFloating = new FloatingTask("birthday");
		mockTaskList.add(datelessFloating);
		mockTaskList.add(new DeadlineTask("assignment", dummyEnd));
		mockTaskList.add(new BoundedTask("dinner", dummyStart, dummyEnd));
		
		logic.setTaskListTest(mockTaskList);
		
		DisplayCommand testCommand = new DisplayCommand(dummyEnd);	
		Output output = logic.executeCommand(testCommand);
		
		Output expected = new Output();
		ArrayList<ArrayList<String>> expectedList = new ArrayList<ArrayList<String>>();
		ArrayList<String> expectedDeadlineTask = new ArrayList<String>();
		expectedDeadlineTask.add("1");
		expectedDeadlineTask.add("assignment");
		expectedDeadlineTask.add("");
		expectedDeadlineTask.add("");
		expectedDeadlineTask.add("08:00");
		expectedDeadlineTask.add("13-10-2015");
		
		ArrayList<String> expectedBoundedTask = new ArrayList<String>();
		expectedBoundedTask.add("2");
		expectedBoundedTask.add("dinner");
		expectedBoundedTask.add("08:00");
		expectedBoundedTask.add("12-10-2015");
		expectedBoundedTask.add("08:00");
		expectedBoundedTask.add("13-10-2015");
		
		expectedList.add(expectedDeadlineTask);
		expectedList.add(expectedBoundedTask);
		expected.setOutput(expectedList);
		expected.setReturnMessage("All tasks with date \"13 10 2015\" are now displayed!");
		assertEquals(expected, output);
		ArrayList<AbstractTask> expectedLastDisplayed = new ArrayList<AbstractTask>();
		expectedLastDisplayed.add(new DeadlineTask("assignment", dummyEnd));
		expectedLastDisplayed.add(new BoundedTask("dinner", dummyStart, dummyEnd));
		assertEquals(expectedLastDisplayed, logic.getLastDisplayedTest());
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
		expected.setReturnMessage("Please display tasks at least once to edit by index.");
		
		assertEquals(expected, output);
	}
	
	@Test
	public void editTaskNameByIndex() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		mockTaskList.add(new FloatingTask("birthday"));
		mockTaskList.add(new FloatingTask("birth"));
		mockTaskList.add(new FloatingTask("birthay"));
		mockTaskList.add(new FloatingTask("vday"));
		mockTaskList.add(new FloatingTask("birtay"));
		
		logic.setTaskListTest(mockTaskList);
		
		DisplayCommand displayCommand = new DisplayCommand("day");
		logic.executeCommand(displayCommand);
		
		// item 2 after filtering should be "vday"
		EditCommand testCommand = new EditCommand(2);
		ArrayList<EditCommand.editField> editFields = new ArrayList<EditCommand.editField>();
		editFields.add(EditCommand.editField.NAME);
		testCommand.setEditFields(editFields);
		testCommand.setNewName("assignment");
		Output output = logic.executeCommand(testCommand);
		
		Output expected = new Output();
		expected.setReturnMessage("Edit done successfully!");

		assertEquals(expected, output);
		// vday is item of index 3 in mockTaskList
		AbstractTask editedTask = logic.getTaskListTest().get(3);
		FloatingTask expectedTask = new FloatingTask("assignment");
		assertEquals(expectedTask, editedTask);
	}
	
	@Test
	public void editTaskStartByIndex() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		mockTaskList.add(new BoundedTask("dinner", dummyStart, dummyEnd));
		
		logic.setTaskListTest(mockTaskList);
		logic.setLastDisplayed(mockTaskList);
		
		EditCommand testCommand = new EditCommand(1);
		ArrayList<EditCommand.editField> editFields = new ArrayList<EditCommand.editField>();
		editFields.add(EditCommand.editField.START_DATE);
		editFields.add(EditCommand.editField.START_TIME);
		testCommand.setEditFields(editFields);
		testCommand.setNewStartDate("14 10 2015");
		testCommand.setNewStartTime("10 00");
		Output output = logic.executeCommand(testCommand);
		
		Output expected = new Output();
		expected.setReturnMessage("Edit done successfully!");
		
		assertEquals(expected, output);
		
		AbstractTask editedTask = logic.getTaskListTest().get(0);
		LocalDateTime newStart = LocalDateTime.parse("14 10 2015 10 00", DTFormatter);
		BoundedTask expectedTask = new BoundedTask("dinner", newStart, dummyEnd);
		assertEquals(expectedTask, editedTask);
	}
	
	@Test
	public void editTaskEndByIndex() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		mockTaskList.add(new DeadlineTask("assignment", dummyEnd));
		
		logic.setTaskListTest(mockTaskList);
		logic.setLastDisplayed(mockTaskList);
		
		EditCommand testCommand = new EditCommand(1);
		ArrayList<EditCommand.editField> editFields = new ArrayList<EditCommand.editField>();
		editFields.add(EditCommand.editField.END_DATE);
		editFields.add(EditCommand.editField.END_TIME);
		testCommand.setEditFields(editFields);
		testCommand.setNewEndDate("14 10 2015");
		testCommand.setNewEndTime("10 00");
		Output output = logic.executeCommand(testCommand);
		
		Output expected = new Output();
		expected.setReturnMessage("Edit done successfully!");
		
		assertEquals(expected, output);
		
		AbstractTask editedTask = logic.getTaskListTest().get(0);
		LocalDateTime newEnd = LocalDateTime.parse("14 10 2015 10 00", DTFormatter);
		DeadlineTask expectedTask = new DeadlineTask("assignment", newEnd);
		assertEquals(expectedTask, editedTask);
	}
	
	@Test
	public void editTaskNameByNameOneHIT() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		mockTaskList.add(new FloatingTask("birthday"));
		
		logic.setTaskListTest(mockTaskList);
		
		EditCommand testCommand = new EditCommand("birthday");
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
	public void editTaskNameByNameAndEndOneHIT() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		mockTaskList.add(new DeadlineTask("birthday", dummyEnd));
		
		logic.setTaskListTest(mockTaskList);
		
		EditCommand testCommand = new EditCommand("birthday");
		ArrayList<EditCommand.editField> editFields = new ArrayList<EditCommand.editField>();
		editFields.add(EditCommand.editField.NAME);
		editFields.add(EditCommand.editField.END_TIME);
		editFields.add(EditCommand.editField.END_DATE);
		testCommand.setEditFields(editFields);
		testCommand.setNewName("assignment");
		testCommand.setNewEndTime("08 00");
		testCommand.setNewEndDate("12 10 2015");
		Output output = logic.executeCommand(testCommand);
		
		Output expected = new Output();
		expected.setReturnMessage("Edit done successfully!");
		
		assertEquals(expected, output);
		
		AbstractTask editedTask = logic.getTaskListTest().get(0);
		DeadlineTask expectedTask = new DeadlineTask("assignment", dummyStart);
		assertEquals(expectedTask, editedTask);
	}
	
	@Test
	public void editTaskNameByNamePartialHIT() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		mockTaskList.add(new FloatingTask("birthday"));
		
		logic.setTaskListTest(mockTaskList);
		
		EditCommand testCommand = new EditCommand("day");
		ArrayList<EditCommand.editField> editFields = new ArrayList<EditCommand.editField>();
		editFields.add(EditCommand.editField.NAME);
		testCommand.setEditFields(editFields);
		testCommand.setNewName("assignment");
		Output output = logic.executeCommand(testCommand);
		
		Output expected = new Output();
		expected.setReturnMessage("All tasks with keyword \"day\" are now displayed!");
		
		ArrayList<ArrayList<String>> expectedList = new ArrayList<ArrayList<String>>();
		ArrayList<String> expectedFloatingTask = new ArrayList<String>();
		expectedFloatingTask.add("1");
		expectedFloatingTask.add("birthday");
		expectedFloatingTask.add("");
		expectedFloatingTask.add("");
		expectedFloatingTask.add("");
		expectedFloatingTask.add("");
		expectedList.add(expectedFloatingTask);
		expected.setOutput(expectedList);
		
		assertEquals(expected, output);
		
		AbstractTask editedTask = logic.getTaskListTest().get(0);
		FloatingTask expectedTask = new FloatingTask("birthday");
		assertEquals(expectedTask, editedTask);
		
		// Second step of edit by keyword partial or multiple hit
		
		EditCommand editByIndex = new EditCommand(1);
		Output secondOutput = logic.executeCommand(editByIndex);
		
		Output expectedSecondOutput = new Output();
		expectedSecondOutput.setReturnMessage("Edit done successfully!");
		
		assertEquals(expectedSecondOutput, secondOutput);
		
		AbstractTask editedAgainTask = logic.getTaskListTest().get(0);
		FloatingTask expectedAgainTask = new FloatingTask("assignment");
		assertEquals(expectedAgainTask, editedAgainTask);
		
	}
	
	@Test
	public void indexDeleteWithoutDisplay() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		mockTaskList.add(new FloatingTask("birthday"));
		
		logic.setTaskListTest(mockTaskList);
		
		DeleteCommand testCommand = new DeleteCommand(1);
		Output output = logic.executeCommand(testCommand);
		Output expected = new Output();
		expected.setReturnMessage("Please display tasks at least once to delete by index.");
		
		assertEquals(expected, output);
	}
	
	@Test
	public void deleteTaskByIndex() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		mockTaskList.add(new DeadlineTask("assign", dummyEnd));
		mockTaskList.add(new DeadlineTask("assignment", dummyEnd));
		mockTaskList.add(new FloatingTask("birthday"));
		mockTaskList.add(new DeadlineTask("assignmentday", dummyEnd));
		mockTaskList.add(new BoundedTask("dinnerday", dummyStart, dummyEnd));
		
		logic.setTaskListTest(mockTaskList);
		DisplayCommand displayCommand = new DisplayCommand("day");
		logic.executeCommand(displayCommand);
		
		// birthday will be first task in last displayed
		DeleteCommand testCommand = new DeleteCommand(1);
		Output output = logic.executeCommand(testCommand);
		
		Output expected = new Output();
		expected.setReturnMessage("\"birthday\" has been deleted!");
		
		assertEquals(expected, output);
		ArrayList<AbstractTask> expectedTaskList = new ArrayList<AbstractTask>();
		expectedTaskList.add(new DeadlineTask("assign", dummyEnd));
		expectedTaskList.add(new DeadlineTask("assignment", dummyEnd));
		expectedTaskList.add(new DeadlineTask("assignmentday", dummyEnd));
		expectedTaskList.add(new BoundedTask("dinnerday", dummyStart, dummyEnd));
		assertEquals(expectedTaskList, logic.getTaskListTest());
	}
	
	@Test
	public void deleteTaskByKeywordOneHIT() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		mockTaskList.add(new FloatingTask("birthday"));
		mockTaskList.add(new DeadlineTask("assignment", dummyEnd));
		mockTaskList.add(new BoundedTask("dinner", dummyStart, dummyEnd));
		
		logic.setTaskListTest(mockTaskList);
		
		DeleteCommand testCommand = new DeleteCommand("birthday");
		Output output = logic.executeCommand(testCommand);
		
		Output expected = new Output();
		expected.setReturnMessage("\"birthday\" has been deleted!");
		
		assertEquals(expected, output);
		ArrayList<AbstractTask> expectedTaskList = new ArrayList<AbstractTask>();
		expectedTaskList.add(new DeadlineTask("assignment", dummyEnd));
		expectedTaskList.add(new BoundedTask("dinner", dummyStart, dummyEnd));
		assertEquals(expectedTaskList, logic.getTaskListTest());
	}
	
	@Test
	public void deleteTaskByKeywordOnePartialHIT() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		mockTaskList.add(new FloatingTask("birthday"));
		mockTaskList.add(new DeadlineTask("assignment", dummyEnd));
		mockTaskList.add(new BoundedTask("dinner", dummyStart, dummyEnd));
		
		logic.setTaskListTest(mockTaskList);
		
		DeleteCommand testCommand = new DeleteCommand("day");
		Output output = logic.executeCommand(testCommand);
		
		Output expected = new Output();
		ArrayList<ArrayList<String>> expectedList = new ArrayList<ArrayList<String>>();
		ArrayList<String> expectedFloatingTask = new ArrayList<String>();
		expectedFloatingTask.add("1");
		expectedFloatingTask.add("birthday");
		expectedFloatingTask.add("");
		expectedFloatingTask.add("");
		expectedFloatingTask.add("");
		expectedFloatingTask.add("");
		expectedList.add(expectedFloatingTask);
		expected.setOutput(expectedList);
		expected.setReturnMessage("All tasks with keyword \"day\" are now displayed!");
		
		assertEquals(expected, output);
		ArrayList<AbstractTask> expectedTaskList =  new ArrayList<AbstractTask>();
		expectedTaskList.add(new FloatingTask("birthday"));
		expectedTaskList.add(new DeadlineTask("assignment", dummyEnd));
		expectedTaskList.add(new BoundedTask("dinner", dummyStart, dummyEnd));
		
		assertEquals(expectedTaskList, logic.getTaskListTest());
	}
	
	@Test
	public void deleteTaskByKeywordMultipleHIT() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		FloatingTask floatingTask = new FloatingTask("birthday");
		mockTaskList.add(floatingTask);
		mockTaskList.add(new DeadlineTask("examday", dummyEnd));
		mockTaskList.add(new BoundedTask("dinner", dummyStart, dummyEnd));
		
		logic.setTaskListTest(mockTaskList);
		
		DeleteCommand deleteCommand = new DeleteCommand("day");
		Output output = logic.executeCommand(deleteCommand);
		
		Output expected = new Output();
		ArrayList<ArrayList<String>> expectedList = new ArrayList<ArrayList<String>>();
		ArrayList<String> expectedFloatingTask = new ArrayList<String>();
		expectedFloatingTask.add("1");
		expectedFloatingTask.add("birthday");
		expectedFloatingTask.add("");
		expectedFloatingTask.add("");
		expectedFloatingTask.add("");
		expectedFloatingTask.add("");
		expectedList.add(expectedFloatingTask);
		
		ArrayList<String> expectedDeadlineTask = new ArrayList<String>();
		expectedDeadlineTask.add("2");
		expectedDeadlineTask.add("examday");
		expectedDeadlineTask.add("");
		expectedDeadlineTask.add("");
		expectedDeadlineTask.add("08:00");
		expectedDeadlineTask.add("13-10-2015");
		expectedList.add(expectedDeadlineTask);
		
		expected.setOutput(expectedList);
		expected.setReturnMessage("All tasks with keyword \"day\" are now displayed!");
		
		assertEquals(expected, output);
		ArrayList<AbstractTask> expectedTaskList = new ArrayList<AbstractTask>();
		expectedTaskList.add(new FloatingTask("birthday"));
		expectedTaskList.add(new DeadlineTask("examday", dummyEnd));
		expectedTaskList.add(new BoundedTask("dinner", dummyStart, dummyEnd));
		assertEquals(expectedTaskList, logic.getTaskListTest());
	}
	
	@Test
	public void deleteAllTasks() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		mockTaskList.add(new FloatingTask("birthday"));
		mockTaskList.add(new DeadlineTask("examday", dummyEnd));
		mockTaskList.add(new BoundedTask("dinner", dummyStart, dummyEnd));
		
		logic.setTaskListTest(mockTaskList);
		
		DeleteCommand deleteCommand = new DeleteCommand(Scope.ALL);
		Output output = logic.executeCommand(deleteCommand);
		
		Output expected = new Output();
		expected.setReturnMessage("All tasks have been deleted!");
		
		assertEquals(expected, output);	
		ArrayList<AbstractTask> expectedTaskList = new ArrayList<AbstractTask>();
		assertEquals(expectedTaskList, logic.getTaskListTest());
	}
	
	@Test
	public void indexMarkWithoutDisplay() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		mockTaskList.add(new FloatingTask("birthday"));
		
		logic.setTaskListTest(mockTaskList);
		
		MarkCommand testCommand = new MarkCommand(1);
		testCommand.setMarkField(markField.MARK);
		Output output = logic.executeCommand(testCommand);
		Output expected = new Output();
		expected.setReturnMessage("Please display tasks at least once to mark by index.");
		
		assertEquals(expected, output);
	}
	
	@Test
	public void markTaskByIndex() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		mockTaskList.add(new DeadlineTask("assign", dummyEnd));
		mockTaskList.add(new DeadlineTask("assignment", dummyEnd));
		mockTaskList.add(new FloatingTask("birthday"));
		mockTaskList.add(new DeadlineTask("assignmentday", dummyEnd));
		mockTaskList.add(new BoundedTask("dinnerday", dummyStart, dummyEnd));
		
		logic.setTaskListTest(mockTaskList);
		DisplayCommand displayCommand = new DisplayCommand("day");
		logic.executeCommand(displayCommand);
		
		// birthday will be first task in last displayed
		MarkCommand testCommand = new MarkCommand(1);
		testCommand.setMarkField(markField.MARK);
		Output output = logic.executeCommand(testCommand);
		
		Output expected = new Output();
		expected.setReturnMessage("\"birthday\" has been marked done.");
		assertEquals(expected, output);
		AbstractTask expectedTask = new FloatingTask("birthday");
		expectedTask.setStatus(Status.DONE);
		assertEquals(expectedTask, logic.getTaskListTest().get(2));
	}
	
	@Test
	public void markTaskByKeywordOneHIT() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		mockTaskList.add(new FloatingTask("birthday"));
		mockTaskList.add(new DeadlineTask("assignment", dummyEnd));
		mockTaskList.add(new BoundedTask("dinner", dummyStart, dummyEnd));
		
		logic.setTaskListTest(mockTaskList);
		
		MarkCommand testCommand = new MarkCommand("birthday");
		testCommand.setMarkField(markField.MARK);
		Output output = logic.executeCommand(testCommand);
		
		Output expected = new Output();
		expected.setReturnMessage("\"birthday\" has been marked done.");
		
		assertEquals(expected, output);
		AbstractTask expectedTask = new FloatingTask("birthday");
		expectedTask.setStatus(Status.DONE);
		assertEquals(expectedTask, logic.getTaskListTest().get(0));
	}
	
	@Test
	public void markTaskByKeywordOnePartialHIT() {
		//TODO
	}
	
	@Test
	public void markTaskByKeywordMultipleHIT() {
		//TODO
	}


}
