package logic;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Stack;

import org.junit.Test;

import shared.Output;
import shared.Output.Priority;
import shared.command.CreateCommand;
import shared.command.DeleteCommand;
import shared.command.DisplayCommand;
import shared.command.DisplayCommand.Type;
import shared.command.EditCommand;
import shared.command.DeleteCommand.Scope;
import shared.command.MarkCommand;
import shared.command.MarkCommand.markField;
import shared.command.UndoCommand;
import shared.task.AbstractTask;
import shared.task.BoundedTask;
import shared.task.DeadlineTask;
import shared.task.FloatingTask;
import shared.task.AbstractTask.Status;

public class LogicTest {
	private Logic logic = new Logic();
	DateTimeFormatter DTFormatter = DateTimeFormatter
			.ofPattern("dd MM yyyy HH mm");
	LocalDateTime dummyStart = LocalDateTime.parse("12 10 2015 08 00",
			DTFormatter);
	LocalDateTime dummyEnd = LocalDateTime.parse("13 10 2015 08 00",
			DTFormatter);

	private ArrayList<String> arrayToArrayList(String[] stringArray) {
		ArrayList<String> arrayListToReturn = new ArrayList<String>();
		for (int i = 0; i < stringArray.length; i++) {
			arrayListToReturn.add(stringArray[i]);
		}
		return arrayListToReturn;
	}

	@Test
	public void createFloatingTask() {
		logic.setTaskListTest(new ArrayList<AbstractTask>());
		CreateCommand testCommand = new CreateCommand("meeting");
		Output output = logic.executeCommand(testCommand);
		Output expected = new Output();
		expected.setReturnMessage("\"meeting\" has been created!");
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
		expected.setReturnMessage("\"assignment\" has been created!");
		assertEquals(expected, output);
		DeadlineTask expectedTask = new DeadlineTask("assignment", dummyEnd);
		AbstractTask createdTask = (logic.getTaskListTest()).get(0);
		assertEquals(expectedTask, createdTask);
	}

	@Test
	public void createBoundedTask() {
		logic.setTaskListTest(new ArrayList<AbstractTask>());
		CreateCommand testCommand = new CreateCommand("dinner", dummyStart,
				dummyEnd);
		Output output = logic.executeCommand(testCommand);
		Output expected = new Output();
		expected.setReturnMessage("\"dinner\" has been created!");
		assertEquals(expected, output);
		BoundedTask expectedTask = new BoundedTask("dinner", dummyStart,
				dummyEnd);
		AbstractTask createdTask = (logic.getTaskListTest()).get(0);
		assertEquals(expectedTask, createdTask);
	}

	@Test
	public void createBoundedTaskWithWrongDateOrder() {
		logic.setTaskListTest(new ArrayList<AbstractTask>());
		CreateCommand testCommand = new CreateCommand("dinner", dummyEnd,
				dummyStart);
		Output output = logic.executeCommand(testCommand);
		Output expected = new Output();
		expected.setReturnMessage("Invalid: Start date time must be before End date time!");
		expected.setPriority(Priority.HIGH);
		assertEquals(expected, output);
		int expectedTaskListSize = 0;
		int actualTaskListSize = (logic.getTaskListTest()).size();
		assertEquals(expectedTaskListSize, actualTaskListSize);
	}

	@Test
	public void displayAllTasks() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		mockTaskList.add(new FloatingTask("birthday"));
		mockTaskList.add(new DeadlineTask("assignment", dummyEnd));
		mockTaskList.add(new BoundedTask("dinner", dummyStart, dummyEnd));

		logic.setTaskListTest(mockTaskList);

		DisplayCommand testCommand = new DisplayCommand(
				DisplayCommand.Scope.ALL);
		Output output = logic.executeCommand(testCommand);

		Output expected = new Output();
		ArrayList<ArrayList<String>> expectedList = new ArrayList<ArrayList<String>>();

		String[] boundedTask = { "1", "dinner", "8am", "MON", "12", "OCT",
				"2015", "8am", "TUE", "13", "OCT", "2015", "UNDONE", "" };
		ArrayList<String> expectedBoundedTask = arrayToArrayList(boundedTask);

		String[] deadlineTask = { "2", "assignment", "", "", "", "", "", "8am",
				"TUE", "13", "OCT", "2015", "UNDONE", "true" };
		ArrayList<String> expectedDeadlineTask = arrayToArrayList(deadlineTask);

		String[] floatingTask = { "3", "birthday", "", "", "", "", "", "", "",
				"", "", "", "UNDONE", "" };
		ArrayList<String> expectedFloatingTask = arrayToArrayList(floatingTask);

		expectedList.add(expectedBoundedTask);
		expectedList.add(expectedDeadlineTask);
		expectedList.add(expectedFloatingTask);
		expected.setOutput(expectedList);
		expected.setReturnMessage("All tasks are now displayed!");

		assertEquals(expected, output);
		ArrayList<AbstractTask> expectedTaskList = new ArrayList<AbstractTask>();

		expectedTaskList.add(new BoundedTask("dinner", dummyStart, dummyEnd));
		expectedTaskList.add(new DeadlineTask("assignment", dummyEnd));
		expectedTaskList.add(new FloatingTask("birthday"));
		assertEquals(expectedTaskList, logic.getLastDisplayedTest());
	}

	@Test
	public void displayFloatingTasks() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		mockTaskList.add(new FloatingTask("birthday"));
		mockTaskList.add(new DeadlineTask("assignment", dummyEnd));
		mockTaskList.add(new BoundedTask("dinner", dummyStart, dummyEnd));

		logic.setTaskListTest(mockTaskList);

		DisplayCommand testCommand = new DisplayCommand(
				DisplayCommand.Scope.FLOATING);
		Output output = logic.executeCommand(testCommand);

		Output expected = new Output();
		ArrayList<ArrayList<String>> expectedList = new ArrayList<ArrayList<String>>();

		String[] floatingTask = { "1", "birthday", "", "", "", "", "", "", "",
				"", "", "", "UNDONE", "" };
		ArrayList<String> expectedFloatingTask = arrayToArrayList(floatingTask);

		expectedList.add(expectedFloatingTask);
		expected.setOutput(expectedList);
		expected.setReturnMessage("All floating tasks are now displayed!");

		assertEquals(expected, output);
		ArrayList<AbstractTask> expectedTaskList = new ArrayList<AbstractTask>();
		expectedTaskList.add(new FloatingTask("birthday"));
		assertEquals(expectedTaskList, logic.getLastDisplayedTest());
	}

	@Test
	public void displayDefault() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		LocalDateTime currentStart = LocalDateTime.now();
		LocalDateTime currentEnd = LocalDateTime.now().plusDays(1);
		// Created for friendlyTime method in boundedTask
		BoundedTask testBounded = new BoundedTask("test", currentStart,
				currentEnd);
		mockTaskList.add(new FloatingTask("birthday"));
		mockTaskList.add(new DeadlineTask("assignment", currentEnd));
		mockTaskList.add(new DeadlineTask("assignment", currentEnd));
		mockTaskList.add(new DeadlineTask("assignment", currentEnd));
		mockTaskList.add(new DeadlineTask("assignment", currentEnd));
		mockTaskList.add(new BoundedTask("dinner", currentStart, currentEnd));
		mockTaskList.add(new BoundedTask("dinner", currentStart, currentEnd));
		mockTaskList.add(new BoundedTask("dinner", currentStart, currentEnd));
		mockTaskList.add(new BoundedTask("dinner", currentStart, currentEnd));

		logic.setTaskListTest(mockTaskList);

		DisplayCommand testCommand = new DisplayCommand(
				DisplayCommand.Scope.DEFAULT);
		Output output = logic.executeCommand(testCommand);

		Output expected = new Output();
		ArrayList<ArrayList<String>> expectedList = new ArrayList<ArrayList<String>>();

		String[] floatingTask = { "8", "birthday", "", "", "", "", "", "", "",
				"", "", "", "UNDONE", "" };
		ArrayList<String> expectedFloatingTask = arrayToArrayList(floatingTask);

		ArrayList<String> expectedDeadlineTask1 = new ArrayList<String>();
		expectedDeadlineTask1.add("5");
		expectedDeadlineTask1.add("assignment");
		expectedDeadlineTask1.add("");
		expectedDeadlineTask1.add("");
		expectedDeadlineTask1.add("");
		expectedDeadlineTask1.add("");
		expectedDeadlineTask1.add("");
		expectedDeadlineTask1.add(testBounded.getFriendlyEndTime());
		expectedDeadlineTask1.add(currentEnd.getDayOfWeek().toString()
				.substring(0, 3));
		expectedDeadlineTask1.add(String.valueOf(currentEnd.getDayOfMonth()));
		expectedDeadlineTask1.add(currentEnd.getMonth().toString()
				.substring(0, 3));
		expectedDeadlineTask1.add(String.valueOf(currentEnd.getYear()));
		expectedDeadlineTask1.add("UNDONE");
		expectedDeadlineTask1.add("false");

		ArrayList<String> expectedDeadlineTask2 = (ArrayList<String>) expectedDeadlineTask1
				.clone();
		expectedDeadlineTask2.set(0, "6");

		ArrayList<String> expectedDeadlineTask3 = (ArrayList<String>) expectedDeadlineTask1
				.clone();
		expectedDeadlineTask3.set(0, "7");

		ArrayList<String> expectedBoundedTask1 = new ArrayList<String>();
		expectedBoundedTask1.add("1");
		expectedBoundedTask1.add("dinner");
		expectedBoundedTask1.add(testBounded.getFriendlyStartTime());
		expectedBoundedTask1.add(currentStart.getDayOfWeek().toString()
				.substring(0, 3));
		expectedBoundedTask1.add(String.valueOf(currentStart.getDayOfMonth()));
		expectedBoundedTask1.add(currentStart.getMonth().toString()
				.substring(0, 3));
		expectedBoundedTask1.add(String.valueOf(currentStart.getYear()));
		expectedBoundedTask1.add(testBounded.getFriendlyEndTime());
		expectedBoundedTask1.add(currentEnd.getDayOfWeek().toString()
				.substring(0, 3));
		expectedBoundedTask1.add(String.valueOf(currentEnd.getDayOfMonth()));
		expectedBoundedTask1.add(currentEnd.getMonth().toString()
				.substring(0, 3));
		expectedBoundedTask1.add(String.valueOf(currentEnd.getYear()));
		expectedBoundedTask1.add("UNDONE");
		expectedBoundedTask1.add("");

		ArrayList<String> expectedBoundedTask2 = (ArrayList<String>) expectedBoundedTask1
				.clone();
		expectedBoundedTask2.set(0, "2");

		ArrayList<String> expectedBoundedTask3 = (ArrayList<String>) expectedBoundedTask1
				.clone();
		expectedBoundedTask3.set(0, "3");

		ArrayList<String> expectedBoundedTask4 = (ArrayList<String>) expectedBoundedTask1
				.clone();
		expectedBoundedTask4.set(0, "4");

		expectedList.add(expectedBoundedTask1);
		expectedList.add(expectedBoundedTask2);
		expectedList.add(expectedBoundedTask3);
		expectedList.add(expectedBoundedTask4);
		expectedList.add(expectedDeadlineTask1);
		expectedList.add(expectedDeadlineTask2);
		expectedList.add(expectedDeadlineTask3);
		expectedList.add(expectedFloatingTask);
		expected.setOutput(expectedList);
		expected.setReturnMessage("Welcome to Flexi-List!");
		
		assertEquals(expected.getTasks(), output.getTasks());
		
		ArrayList<AbstractTask> expectedTaskList = new ArrayList<AbstractTask>();
		expectedTaskList
				.add(new BoundedTask("dinner", currentStart, currentEnd));
		expectedTaskList
				.add(new BoundedTask("dinner", currentStart, currentEnd));
		expectedTaskList
				.add(new BoundedTask("dinner", currentStart, currentEnd));
		expectedTaskList
				.add(new BoundedTask("dinner", currentStart, currentEnd));
		expectedTaskList.add(new DeadlineTask("assignment", currentEnd));
		expectedTaskList.add(new DeadlineTask("assignment", currentEnd));
		expectedTaskList.add(new DeadlineTask("assignment", currentEnd));
		expectedTaskList.add(new FloatingTask("birthday"));
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

		DisplayCommand testCommand = new DisplayCommand(
				DisplayCommand.Scope.DONE);
		Output output = logic.executeCommand(testCommand);

		Output expected = new Output();
		ArrayList<ArrayList<String>> expectedList = new ArrayList<ArrayList<String>>();

		String[] floatingTask = { "1", "birthday", "", "", "", "", "", "", "",
				"", "", "", "DONE", "" };
		ArrayList<String> expectedFloatingTask = arrayToArrayList(floatingTask);

		expectedList.add(expectedFloatingTask);
		expected.setOutput(expectedList);
		expected.setReturnMessage("All DONE tasks are now displayed!");
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

		DisplayCommand testCommand = new DisplayCommand(
				DisplayCommand.Scope.UNDONE);
		Output output = logic.executeCommand(testCommand);

		Output expected = new Output();
		ArrayList<ArrayList<String>> expectedList = new ArrayList<ArrayList<String>>();

		String[] boundedTask = { "1", "dinner", "8am", "MON", "12", "OCT",
				"2015", "8am", "TUE", "13", "OCT", "2015", "UNDONE", "" };
		ArrayList<String> expectedBoundedTask = arrayToArrayList(boundedTask);

		String[] deadlineTask = { "2", "assignment", "", "", "", "", "", "8am",
				"TUE", "13", "OCT", "2015", "UNDONE", "true" };
		ArrayList<String> expectedDeadlineTask = arrayToArrayList(deadlineTask);

		expectedList.add(expectedBoundedTask);
		expectedList.add(expectedDeadlineTask);
		expected.setOutput(expectedList);
		expected.setReturnMessage("All UNDONE tasks are now displayed!");
		assertEquals(expected.getTasks(), output.getTasks());
		ArrayList<AbstractTask> expectedLastDisplayed = new ArrayList<AbstractTask>();
		expectedLastDisplayed.add(new BoundedTask("dinner", dummyStart,
				dummyEnd));
		expectedLastDisplayed.add(new DeadlineTask("assignment", dummyEnd));

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
		
		String[] deadlineTask = { "1", "examday", "", "", "", "", "", "8am",
				"TUE", "13", "OCT", "2015", "UNDONE", "true" };
		ArrayList<String> expectedDeadlineTask = arrayToArrayList(deadlineTask);

		String[] floatingTask = { "2", "birthday", "", "", "", "", "", "", "",
				"", "", "", "UNDONE", "" };
		ArrayList<String> expectedFloatingTask = arrayToArrayList(floatingTask);

		expectedList.add(expectedDeadlineTask);
		expectedList.add(expectedFloatingTask);
		expected.setOutput(expectedList);
		expected.setReturnMessage("All tasks with keyword \"day\" are now displayed!");

		assertEquals(expected, output);
		ArrayList<AbstractTask> expectedLastDisplayed = new ArrayList<AbstractTask>();
		expectedLastDisplayed.add(new DeadlineTask("examday", dummyEnd));
		expectedLastDisplayed.add(new FloatingTask("birthday"));
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

		DisplayCommand testCommand = new DisplayCommand(dummyEnd,
				Type.SEARCHDATE);
		Output output = logic.executeCommand(testCommand);

		Output expected = new Output();
		ArrayList<ArrayList<String>> expectedList = new ArrayList<ArrayList<String>>();

		String[] boundedTask = { "1", "dinner", "8am", "MON", "12", "OCT",
				"2015", "8am", "TUE", "13", "OCT", "2015", "UNDONE", "" };
		ArrayList<String> expectedBoundedTask = arrayToArrayList(boundedTask);

		String[] deadlineTask = { "2", "assignment", "", "", "", "", "", "8am",
				"TUE", "13", "OCT", "2015", "UNDONE", "true" };
		ArrayList<String> expectedDeadlineTask = arrayToArrayList(deadlineTask);

		expectedList.add(expectedBoundedTask);
		expectedList.add(expectedDeadlineTask);
		expected.setOutput(expectedList);
		expected.setReturnMessage("All tasks with date \"13 10 2015\" are now displayed!");
		assertEquals(expected, output);
		ArrayList<AbstractTask> expectedLastDisplayed = new ArrayList<AbstractTask>();
		expectedLastDisplayed.add(new BoundedTask("dinner", dummyStart,
				dummyEnd));
		expectedLastDisplayed.add(new DeadlineTask("assignment", dummyEnd));
		assertEquals(expectedLastDisplayed, logic.getLastDisplayedTest());
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
		expected.setReturnMessage("\"vday\" has been edited!");

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
		testCommand.setNewStartDate("11 10 2015");
		testCommand.setNewStartTime("10 00");
		Output output = logic.executeCommand(testCommand);

		Output expected = new Output();
		expected.setReturnMessage("\"dinner\" has been edited!");

		assertEquals(expected, output);

		AbstractTask editedTask = logic.getTaskListTest().get(0);
		LocalDateTime newStart = LocalDateTime.parse("11 10 2015 10 00",
				DTFormatter);
		BoundedTask expectedTask = new BoundedTask("dinner", newStart, dummyEnd);
		assertEquals(expectedTask, editedTask);
	}

	@Test
	public void editTaskStartByIndexWithWrongDateOrder() {
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
		expected.setReturnMessage("Invalid: Start date time must be before End date time!");
		expected.setPriority(Priority.HIGH);

		assertEquals(expected, output);

		AbstractTask editedTask = logic.getTaskListTest().get(0);
		BoundedTask expectedTask = new BoundedTask("dinner", dummyStart,
				dummyEnd);
		assertEquals(expectedTask, editedTask);
	}

	@Test
	public void editDeadlineTaskEndByIndex() {
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
		expected.setReturnMessage("\"assignment\" has been edited!");

		assertEquals(expected, output);

		AbstractTask editedTask = logic.getTaskListTest().get(0);
		LocalDateTime newEnd = LocalDateTime.parse("14 10 2015 10 00",
				DTFormatter);
		DeadlineTask expectedTask = new DeadlineTask("assignment", newEnd);
		assertEquals(expectedTask, editedTask);
	}

	@Test
	public void editBoundedTaskEndByIndex() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		mockTaskList.add(new BoundedTask("assignment", dummyStart, dummyEnd));

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
		expected.setReturnMessage("\"assignment\" has been edited!");

		assertEquals(expected, output);

		AbstractTask editedTask = logic.getTaskListTest().get(0);
		LocalDateTime newEnd = LocalDateTime.parse("14 10 2015 10 00",
				DTFormatter);
		BoundedTask expectedTask = new BoundedTask("assignment", dummyStart,
				newEnd);
		assertEquals(expectedTask, editedTask);
	}

	@Test
	public void editTaskEndByIndexWithWrongDateOrder() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		mockTaskList.add(new BoundedTask("assignment", dummyStart, dummyEnd));

		logic.setTaskListTest(mockTaskList);
		logic.setLastDisplayed(mockTaskList);

		EditCommand testCommand = new EditCommand(1);
		ArrayList<EditCommand.editField> editFields = new ArrayList<EditCommand.editField>();
		editFields.add(EditCommand.editField.END_DATE);
		editFields.add(EditCommand.editField.END_TIME);
		testCommand.setEditFields(editFields);
		testCommand.setNewEndDate("10 10 2015");
		testCommand.setNewEndTime("10 00");
		Output output = logic.executeCommand(testCommand);

		Output expected = new Output();
		expected.setReturnMessage("Invalid: Start date time must be before End date time!");
		expected.setPriority(Priority.HIGH);

		assertEquals(expected, output);

		AbstractTask editedTask = logic.getTaskListTest().get(0);
		BoundedTask expectedTask = new BoundedTask("assignment", dummyStart,
				dummyEnd);
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
		expected.setReturnMessage("\"birthday\" has been edited!");

		assertEquals(expected, output);

		AbstractTask editedTask = logic.getTaskListTest().get(0);
		FloatingTask expectedTask = new FloatingTask("assignment");
		assertEquals(expectedTask, editedTask);
	}

	@Test
	public void editTaskNameAndEndByNameOneHIT() {
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
		expected.setReturnMessage("\"birthday\" has been edited!");

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
		String[] floatingTask = { "1", "birthday", "", "", "", "", "", "", "",
				"", "", "", "UNDONE", "" };
		ArrayList<String> expectedFloatingTask = arrayToArrayList(floatingTask);
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
		expectedSecondOutput.setReturnMessage("\"birthday\" has been edited!");

		assertEquals(expectedSecondOutput, secondOutput);

		AbstractTask editedAgainTask = logic.getTaskListTest().get(0);
		FloatingTask expectedAgainTask = new FloatingTask("assignment");
		assertEquals(expectedAgainTask, editedAgainTask);

	}

	@Test
	public void editTaskStartAndEndOneMonthLater() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		mockTaskList.add(new BoundedTask("assignment", dummyStart, dummyEnd));

		logic.setTaskListTest(mockTaskList);
		logic.setLastDisplayed(mockTaskList);

		EditCommand testCommand = new EditCommand(1);
		ArrayList<EditCommand.editField> editFields = new ArrayList<EditCommand.editField>();
		editFields.add(EditCommand.editField.START_DATE);
		editFields.add(EditCommand.editField.END_DATE);
		testCommand.setEditFields(editFields);
		testCommand.setNewStartDate("12 11 2015");
		testCommand.setNewEndDate("13 11 2015");
		Output output = logic.executeCommand(testCommand);

		Output expected = new Output();
		expected.setReturnMessage("\"assignment\" has been edited!");
		// System.out.println(output.getReturnMessage());
		assertEquals(expected, output);

		AbstractTask editedTask = logic.getTaskListTest().get(0);
		LocalDateTime newStart = LocalDateTime.parse("12 11 2015 08 00",
				DTFormatter);
		LocalDateTime newEnd = LocalDateTime.parse("13 11 2015 08 00",
				DTFormatter);
		BoundedTask expectedTask = new BoundedTask("assignment", newStart,
				newEnd);
		assertEquals(expectedTask, editedTask);
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
		expected.setReturnMessage("\"dinnerday\" has been deleted!");
		expected.setPriority(Priority.HIGH);

		assertEquals(expected, output);
		ArrayList<AbstractTask> expectedTaskList = new ArrayList<AbstractTask>();
		expectedTaskList.add(new DeadlineTask("assign", dummyEnd));
		expectedTaskList.add(new DeadlineTask("assignment", dummyEnd));
		expectedTaskList.add(new FloatingTask("birthday"));
		expectedTaskList.add(new DeadlineTask("assignmentday", dummyEnd));
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
		expected.setPriority(Priority.HIGH);

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
		String[] floatingTask = { "1", "birthday", "", "", "", "", "", "", "",
				"", "", "", "UNDONE", "" };
		ArrayList<String> expectedFloatingTask = arrayToArrayList(floatingTask);
		expectedList.add(expectedFloatingTask);
		expected.setOutput(expectedList);
		expected.setReturnMessage("All tasks with keyword \"day\" are now displayed!");

		assertEquals(expected, output);
		ArrayList<AbstractTask> expectedTaskList = new ArrayList<AbstractTask>();
		expectedTaskList.add(new FloatingTask("birthday"));
		expectedTaskList.add(new DeadlineTask("assignment", dummyEnd));
		expectedTaskList.add(new BoundedTask("dinner", dummyStart, dummyEnd));
		

		assertEquals(expectedTaskList, logic.getTaskListTest());
	}

	@Test
	public void deleteTaskByKeywordMultipleHIT() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		mockTaskList.add(new FloatingTask("birthday"));
		mockTaskList.add(new DeadlineTask("examday", dummyEnd));
		mockTaskList.add(new BoundedTask("dinner", dummyStart, dummyEnd));

		logic.setTaskListTest(mockTaskList);

		DeleteCommand deleteCommand = new DeleteCommand("day");
		Output output = logic.executeCommand(deleteCommand);

		Output expected = new Output();
		ArrayList<ArrayList<String>> expectedList = new ArrayList<ArrayList<String>>();
		String[] deadlineTask = { "1", "examday", "", "", "", "", "", "8am",
				"TUE", "13", "OCT", "2015", "UNDONE", "true" };
		ArrayList<String> expectedDeadlineTask = arrayToArrayList(deadlineTask);

		String[] floatingTask = { "2", "birthday", "", "", "", "", "", "", "",
				"", "", "", "UNDONE" , ""};
		ArrayList<String> expectedFloatingTask = arrayToArrayList(floatingTask);
		
		expectedList.add(expectedDeadlineTask);
		expectedList.add(expectedFloatingTask);

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
		expected.setPriority(Priority.HIGH);

		assertEquals(expected, output);
		ArrayList<AbstractTask> expectedTaskList = new ArrayList<AbstractTask>();
		assertEquals(expectedTaskList, logic.getTaskListTest());
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

		// dinnerday will be first task in last displayed
		MarkCommand testCommand = new MarkCommand(1);
		testCommand.setMarkField(markField.MARK);
		Output output = logic.executeCommand(testCommand);

		Output expected = new Output();
		expected.setReturnMessage("\"dinnerday\" has been marked done.");
		assertEquals(expected, output);
		AbstractTask expectedTask = new BoundedTask("dinnerday", dummyStart,
				dummyEnd);
		expectedTask.setStatus(Status.DONE);
		assertEquals(expectedTask, logic.getTaskListTest().get(4));
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
		// TODO
	}

	@Test
	public void markTaskByKeywordMultipleHIT() {
		// TODO
	}

	@Test
	public void undoPreviousActionCreate() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		logic.setTaskListTest(mockTaskList);
		Stack<ArrayList<AbstractTask>> mockStack = new Stack<ArrayList<AbstractTask>>();
		ArrayList<AbstractTask> clonedList = logic.cloneTaskList(mockTaskList);
		mockStack.push(clonedList);
		logic.setTaskListStack(mockStack);
		CreateCommand testCommand = new CreateCommand("meeting");
		Output output = logic.executeCommand(testCommand);
		Output expected = new Output();
		expected.setReturnMessage("\"meeting\" has been created!");
		assertEquals(expected, output);
		FloatingTask expectedTask = new FloatingTask("meeting");
		AbstractTask createdTask = (logic.getTaskListTest()).get(0);
		assertEquals(expectedTask, createdTask);

		UndoCommand undoCommand = new UndoCommand();
		Output output2 = logic.executeCommand(undoCommand);
		Output expected2 = new Output();
		expected2.setReturnMessage("\"create\" action has been undone!");
		assertEquals(expected2, output2);
		int expectedTaskListSize = 0;
		int actualTaskListSize = logic.getTaskListTest().size();
		assertEquals(expectedTaskListSize, actualTaskListSize);
	}

	@Test
	public void undoPreviousActionDelete() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		logic.setTaskListTest(mockTaskList);
		Stack<ArrayList<AbstractTask>> mockStack = new Stack<ArrayList<AbstractTask>>();
		ArrayList<AbstractTask> clonedList = logic.cloneTaskList(mockTaskList);
		mockStack.push(clonedList);
		logic.setTaskListStack(mockStack);
		CreateCommand createCommand = new CreateCommand("meeting");
		logic.executeCommand(createCommand);
		DeleteCommand deleteCommand = new DeleteCommand(1);
		Output output = logic.executeCommand(deleteCommand);
		Output expected = new Output();
		expected.setPriority(Priority.HIGH);
		expected.setReturnMessage("\"meeting\" has been deleted!");
		assertEquals(expected, output);
		assertTrue(logic.getTaskListTest().size() == 0);

		UndoCommand undoCommand = new UndoCommand();
		Output output2 = logic.executeCommand(undoCommand);
		Output expected2 = new Output();
		expected2.setReturnMessage("\"delete\" action has been undone!");
		assertEquals(expected2, output2);
		assertTrue(logic.getTaskListTest().size() == 1);
		FloatingTask expectedTask = new FloatingTask("meeting");
		assertEquals(expectedTask, logic.getTaskListTest().get(0));
	}

}
