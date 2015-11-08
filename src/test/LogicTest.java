package test;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import logic.Logic;
import logic.StorageStub;
import logic.TaskList;

import org.junit.Test;

import shared.Output;
import shared.Output.Priority;
import shared.command.CreateCommand;
import shared.command.DeleteCommand;
import shared.command.DisplayCommand;
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

//@@author A0124828B
public class LogicTest {
	private StorageStub storageStub = new StorageStub();
	private Logic logic = new Logic(storageStub);
	DateTimeFormatter DTFormatter = DateTimeFormatter
			.ofPattern("dd MM yyyy HH mm");
	LocalDateTime dummyStart = LocalDateTime.parse("12 10 2015 08 00",
			DTFormatter);
	LocalDateTime dummyEnd = LocalDateTime.parse("13 10 2015 08 00",
			DTFormatter);

	CreateCommand createFloating = new CreateCommand("birthday");
	CreateCommand createDeadline = new CreateCommand("assignment", dummyEnd);
	CreateCommand createBounded = new CreateCommand("dinner", dummyStart,
			dummyEnd);
	DisplayCommand displayAll = new DisplayCommand(DisplayCommand.Scope.ALL);

	private ArrayList<String> arrayToArrayList(String[] stringArray) {
		ArrayList<String> arrayListToReturn = new ArrayList<String>();
		for (int i = 0; i < stringArray.length; i++) {
			arrayListToReturn.add(stringArray[i]);
		}
		return arrayListToReturn;
	}
	
	/*
	 * CREATEACTION TESTS
	 */
	
	@Test
	public void createFloatingTask() {
		Output output = logic.executeCommand(createFloating);
		Output expected = new Output();
		expected.setReturnMessage("\"birthday\" has been created!");
		assertEquals(expected, output);

		FloatingTask expectedTask = new FloatingTask("birthday");
		AbstractTask createdTask = (logic.getTaskList()).getTask(0);
		assertEquals(expectedTask, createdTask);
	}

	@Test
	public void createDeadlineTask() {
		Output output = logic.executeCommand(createDeadline);
		Output expected = new Output();
		expected.setReturnMessage("\"assignment\" has been created!");
		assertEquals(expected, output);

		DeadlineTask expectedTask = new DeadlineTask("assignment", dummyEnd);
		AbstractTask createdTask = (logic.getTaskList()).getTask(0);
		assertEquals(expectedTask, createdTask);
	}

	@Test
	public void createBoundedTask() {
		Output output = logic.executeCommand(createBounded);
		Output expected = new Output();
		expected.setReturnMessage("\"dinner\" has been created!");
		assertEquals(expected, output);

		BoundedTask expectedTask = new BoundedTask("dinner", dummyStart,
				dummyEnd);
		AbstractTask createdTask = (logic.getTaskList()).getTask(0);
		assertEquals(expectedTask, createdTask);
	}

	@Test
	public void createBoundedTaskWithWrongDateOrder() {
		CreateCommand createWrongBounded = new CreateCommand("dinner",
				dummyEnd, dummyStart);
		Output output = logic.executeCommand(createWrongBounded);
		Output expected = new Output();
		expected.setReturnMessage("Invalid: Start date time must be before End date time!");
		expected.setPriority(Priority.HIGH);
		assertEquals(expected, output);

		int expectedTaskListSize = 0;
		int actualTaskListSize = (logic.getTaskList()).size();
		assertEquals(expectedTaskListSize, actualTaskListSize);
	}

	/*
	 * DISPLAYACTION TESTS
	 */
	
	@Test
	public void displayAllTasks() {
		logic.executeCommand(createFloating);
		logic.executeCommand(createDeadline);
		logic.executeCommand(createBounded);
		Output output = logic.executeCommand(displayAll);

		String[] boundedTask = { "1", "dinner", "8am", "MON", "12", "OCT",
				"2015", "8am", "TUE", "13", "OCT", "2015", "UNDONE", "" };
		ArrayList<String> expectedBoundedTask = arrayToArrayList(boundedTask);

		String[] deadlineTask = { "2", "assignment", "", "", "", "", "", "8am",
				"TUE", "13", "OCT", "2015", "UNDONE", "true" };
		ArrayList<String> expectedDeadlineTask = arrayToArrayList(deadlineTask);

		String[] floatingTask = { "3", "birthday", "", "", "", "", "", "", "",
				"", "", "", "UNDONE", "" };
		ArrayList<String> expectedFloatingTask = arrayToArrayList(floatingTask);

		Output expected = new Output();
		ArrayList<ArrayList<String>> expectedList = new ArrayList<ArrayList<String>>();
		expectedList.add(expectedBoundedTask);
		expectedList.add(expectedDeadlineTask);
		expectedList.add(expectedFloatingTask);
		expected.setOutput(expectedList);
		expected.setReturnMessage("All tasks are now displayed!");
		assertEquals(expected, output);

		TaskList expectedTaskList = new TaskList();
		expectedTaskList
				.addTask(new BoundedTask("dinner", dummyStart, dummyEnd));
		expectedTaskList.addTask(new DeadlineTask("assignment", dummyEnd));
		expectedTaskList.addTask(new FloatingTask("birthday"));
		assertEquals(expectedTaskList, logic.getLastDisplayedList());
	}

	@Test
	public void displayFloatingTasks() {
		logic.executeCommand(createFloating);
		logic.executeCommand(createDeadline);
		logic.executeCommand(createBounded);

		DisplayCommand displayFloating = new DisplayCommand(
				DisplayCommand.Scope.FLOATING);
		Output output = logic.executeCommand(displayFloating);

		String[] floatingTask = { "1", "birthday", "", "", "", "", "", "", "",
				"", "", "", "UNDONE", "" };
		ArrayList<String> expectedFloatingTask = arrayToArrayList(floatingTask);

		Output expected = new Output();
		ArrayList<ArrayList<String>> expectedList = new ArrayList<ArrayList<String>>();
		expectedList.add(expectedFloatingTask);
		expected.setOutput(expectedList);
		expected.setReturnMessage("All floating tasks are now displayed!");
		assertEquals(expected, output);

		TaskList expectedTaskList = new TaskList();
		expectedTaskList.addTask(new FloatingTask("birthday"));
		assertEquals(expectedTaskList, logic.getLastDisplayedList());
	}

	@Test
	public void displayDefault() {
		LocalDateTime currentStart = LocalDateTime.now().plusDays(1);
		LocalDateTime currentEnd = currentStart.plusDays(1);
		LocalDateTime overdueEnd = currentStart.minusDays(2);

		CreateCommand createDeadline = new CreateCommand("assignment",
				currentEnd);
		CreateCommand createOverdue = new CreateCommand("late work", overdueEnd);
		CreateCommand createBounded = new CreateCommand("dinner", currentStart,
				currentEnd);

		BoundedTask dummyBounded = new BoundedTask("dinner", currentStart,
				currentEnd);
		DeadlineTask dummyOverdue = new DeadlineTask("late work", overdueEnd);

		logic.executeCommand(createFloating);
		logic.executeCommand(createOverdue);
		logic.executeCommand(createDeadline);
		logic.executeCommand(createBounded);

		DisplayCommand displayDefault = new DisplayCommand(
				DisplayCommand.Scope.DEFAULT);
		Output output = logic.executeCommand(displayDefault);

		String[] floatingTask = { "4", "birthday", "", "", "", "", "", "", "",
				"", "", "", "UNDONE", "" };
		ArrayList<String> expectedFloatingTask = arrayToArrayList(floatingTask);

		ArrayList<String> expectedDeadlineTask = new ArrayList<String>();
		expectedDeadlineTask.add("3");
		expectedDeadlineTask.add("assignment");
		expectedDeadlineTask.add("");
		expectedDeadlineTask.add("");
		expectedDeadlineTask.add("");
		expectedDeadlineTask.add("");
		expectedDeadlineTask.add("");
		expectedDeadlineTask.add(dummyBounded.getFriendlyEndTime());
		expectedDeadlineTask.add(currentEnd.getDayOfWeek().toString()
				.substring(0, 3));
		expectedDeadlineTask.add(String.valueOf(currentEnd.getDayOfMonth()));
		expectedDeadlineTask.add(currentEnd.getMonth().toString()
				.substring(0, 3));
		expectedDeadlineTask.add(String.valueOf(currentEnd.getYear()));
		expectedDeadlineTask.add("UNDONE");
		expectedDeadlineTask.add("false");

		ArrayList<String> expectedBoundedTask = new ArrayList<String>();
		expectedBoundedTask.add("2");
		expectedBoundedTask.add("dinner");
		expectedBoundedTask.add(dummyBounded.getFriendlyStartTime());
		expectedBoundedTask.add(currentStart.getDayOfWeek().toString()
				.substring(0, 3));
		expectedBoundedTask.add(String.valueOf(currentStart.getDayOfMonth()));
		expectedBoundedTask.add(currentStart.getMonth().toString()
				.substring(0, 3));
		expectedBoundedTask.add(String.valueOf(currentStart.getYear()));
		expectedBoundedTask.add(dummyBounded.getFriendlyEndTime());
		expectedBoundedTask.add(currentEnd.getDayOfWeek().toString()
				.substring(0, 3));
		expectedBoundedTask.add(String.valueOf(currentEnd.getDayOfMonth()));
		expectedBoundedTask.add(currentEnd.getMonth().toString()
				.substring(0, 3));
		expectedBoundedTask.add(String.valueOf(currentEnd.getYear()));
		expectedBoundedTask.add("UNDONE");
		expectedBoundedTask.add("");

		ArrayList<String> expectedOverdueTask = new ArrayList<String>();
		expectedOverdueTask.add("1");
		expectedOverdueTask.add("late work");
		expectedOverdueTask.add("");
		expectedOverdueTask.add("");
		expectedOverdueTask.add("");
		expectedOverdueTask.add("");
		expectedOverdueTask.add("");
		expectedOverdueTask.add(dummyOverdue.getFriendlyEndTime());
		expectedOverdueTask.add(overdueEnd.getDayOfWeek().toString()
				.substring(0, 3));
		expectedOverdueTask.add(String.valueOf(overdueEnd.getDayOfMonth()));
		expectedOverdueTask.add(overdueEnd.getMonth().toString()
				.substring(0, 3));
		expectedOverdueTask.add(String.valueOf(overdueEnd.getYear()));
		expectedOverdueTask.add("UNDONE");
		expectedOverdueTask.add("true");

		Output expected = new Output();
		ArrayList<ArrayList<String>> expectedList = new ArrayList<ArrayList<String>>();
		expectedList.add(expectedOverdueTask);
		expectedList.add(expectedBoundedTask);
		expectedList.add(expectedDeadlineTask);
		expectedList.add(expectedFloatingTask);
		expected.setOutput(expectedList);
		expected.setReturnMessage("Welcome to Flexi-List!");

		assertEquals(expected.getTasks(), output.getTasks());

		TaskList expectedTaskList = new TaskList();
		expectedTaskList.addTask(new DeadlineTask("late work", overdueEnd));
		expectedTaskList.addTask(new BoundedTask("dinner", currentStart,
				currentEnd));
		expectedTaskList.addTask(new DeadlineTask("assignment", currentEnd));
		expectedTaskList.addTask(new FloatingTask("birthday"));
		assertEquals(expectedTaskList, logic.getLastDisplayedList());
	}

	@Test
	public void displayDoneTasks() {
		MarkCommand markFloating = new MarkCommand("birthday");
		markFloating.setMarkField(markField.MARK);
		logic.executeCommand(createFloating);
		logic.executeCommand(createDeadline);
		logic.executeCommand(createBounded);
		logic.executeCommand(displayAll);
		logic.executeCommand(markFloating);

		DisplayCommand displayDone = new DisplayCommand(
				DisplayCommand.Scope.DONE);
		Output output = logic.executeCommand(displayDone);

		String[] floatingTask = { "1", "birthday", "", "", "", "", "", "", "",
				"", "", "", "DONE", "" };
		ArrayList<String> expectedFloatingTask = arrayToArrayList(floatingTask);

		Output expected = new Output();
		ArrayList<ArrayList<String>> expectedList = new ArrayList<ArrayList<String>>();
		expectedList.add(expectedFloatingTask);
		expected.setOutput(expectedList);
		expected.setReturnMessage("All DONE tasks are now displayed!");
		assertEquals(expected, output);

		TaskList expectedLastDisplayed = new TaskList();
		FloatingTask doneFloating = new FloatingTask("birthday");
		doneFloating.setStatus(Status.DONE);
		expectedLastDisplayed.addTask(doneFloating);
		assertEquals(expectedLastDisplayed, logic.getLastDisplayedList());
	}

	@Test
	public void displayUndoneTasks() {
		MarkCommand markFloating = new MarkCommand("birthday");
		markFloating.setMarkField(markField.MARK);
		logic.executeCommand(createFloating);
		logic.executeCommand(createDeadline);
		logic.executeCommand(createBounded);
		logic.executeCommand(displayAll);
		logic.executeCommand(markFloating);

		DisplayCommand testCommand = new DisplayCommand(
				DisplayCommand.Scope.UNDONE);
		Output output = logic.executeCommand(testCommand);

		String[] boundedTask = { "1", "dinner", "8am", "MON", "12", "OCT",
				"2015", "8am", "TUE", "13", "OCT", "2015", "UNDONE", "" };
		ArrayList<String> expectedBoundedTask = arrayToArrayList(boundedTask);

		String[] deadlineTask = { "2", "assignment", "", "", "", "", "", "8am",
				"TUE", "13", "OCT", "2015", "UNDONE", "true" };
		ArrayList<String> expectedDeadlineTask = arrayToArrayList(deadlineTask);

		Output expected = new Output();
		ArrayList<ArrayList<String>> expectedList = new ArrayList<ArrayList<String>>();
		expectedList.add(expectedBoundedTask);
		expectedList.add(expectedDeadlineTask);
		expected.setOutput(expectedList);
		expected.setReturnMessage("All UNDONE tasks are now displayed!");
		assertEquals(expected.getTasks(), output.getTasks());

		TaskList expectedLastDisplayed = new TaskList();
		expectedLastDisplayed.addTask(new BoundedTask("dinner", dummyStart,
				dummyEnd));
		expectedLastDisplayed.addTask(new DeadlineTask("assignment", dummyEnd));
		assertEquals(expectedLastDisplayed, logic.getLastDisplayedList());
	}

	@Test
	public void displayByKeyword() {
		logic.executeCommand(createFloating);
		CreateCommand createDeadline = new CreateCommand("examday", dummyEnd);
		logic.executeCommand(createDeadline);
		logic.executeCommand(createBounded);

		ArrayList<String> keywords = new ArrayList<String>();
		keywords.add("day");
		DisplayCommand displayKeyword = new DisplayCommand(keywords);
		Output output = logic.executeCommand(displayKeyword);

		String[] deadlineTask = { "1", "examday", "", "", "", "", "", "8am",
				"TUE", "13", "OCT", "2015", "UNDONE", "true" };
		ArrayList<String> expectedDeadlineTask = arrayToArrayList(deadlineTask);

		String[] floatingTask = { "2", "birthday", "", "", "", "", "", "", "",
				"", "", "", "UNDONE", "" };
		ArrayList<String> expectedFloatingTask = arrayToArrayList(floatingTask);

		Output expected = new Output();
		ArrayList<ArrayList<String>> expectedList = new ArrayList<ArrayList<String>>();
		expectedList.add(expectedDeadlineTask);
		expectedList.add(expectedFloatingTask);
		expected.setOutput(expectedList);
		expected.setReturnMessage("All tasks with keyword \"day\" are now displayed!");
		assertEquals(expected, output);

		TaskList expectedLastDisplayed = new TaskList();
		expectedLastDisplayed.addTask(new DeadlineTask("examday", dummyEnd));
		expectedLastDisplayed.addTask(new FloatingTask("birthday"));
		assertEquals(expectedLastDisplayed, logic.getLastDisplayedList());
	}

	@Test
	public void displayByDate() {
		logic.executeCommand(createFloating);
		logic.executeCommand(createDeadline);
		logic.executeCommand(createBounded);

		DisplayCommand displayCommand = new DisplayCommand(dummyEnd);
		Output output = logic.executeCommand(displayCommand);

		String[] boundedTask = { "1", "dinner", "8am", "MON", "12", "OCT",
				"2015", "8am", "TUE", "13", "OCT", "2015", "UNDONE", "" };
		ArrayList<String> expectedBoundedTask = arrayToArrayList(boundedTask);

		String[] deadlineTask = { "2", "assignment", "", "", "", "", "", "8am",
				"TUE", "13", "OCT", "2015", "UNDONE", "true" };
		ArrayList<String> expectedDeadlineTask = arrayToArrayList(deadlineTask);

		Output expected = new Output();
		ArrayList<ArrayList<String>> expectedList = new ArrayList<ArrayList<String>>();
		expectedList.add(expectedBoundedTask);
		expectedList.add(expectedDeadlineTask);
		expected.setOutput(expectedList);
		expected.setReturnMessage("All tasks with date \"13 10 2015\" are now displayed!");
		assertEquals(expected, output);

		TaskList expectedLastDisplayed = new TaskList();
		expectedLastDisplayed.addTask(new BoundedTask("dinner", dummyStart,
				dummyEnd));
		expectedLastDisplayed.addTask(new DeadlineTask("assignment", dummyEnd));
		assertEquals(expectedLastDisplayed, logic.getLastDisplayedList());
	}

	/*
	 * EDITACTION TESTS
	 */
	
	@Test
	public void editTaskNameByIndex() {
		CreateCommand createFloating1 = new CreateCommand("birthday");
		CreateCommand createFloating2 = new CreateCommand("birth");
		CreateCommand createFloating3 = new CreateCommand("birthday");
		CreateCommand createFloating4 = new CreateCommand("vday");
		CreateCommand createFloating5 = new CreateCommand("birtay");

		logic.executeCommand(createFloating1);
		logic.executeCommand(createFloating2);
		logic.executeCommand(createFloating3);
		logic.executeCommand(createFloating4);
		logic.executeCommand(createFloating5);

		ArrayList<String> keywords = new ArrayList<String>();
		keywords.add("day");
		DisplayCommand displayCommand = new DisplayCommand(keywords);
		logic.executeCommand(displayCommand);

		// item 2 after filtering should be "vday"
		EditCommand editCommand = new EditCommand(2);
		ArrayList<EditCommand.editField> editFields = new ArrayList<EditCommand.editField>();
		editFields.add(EditCommand.editField.NAME);
		editCommand.setEditFields(editFields);
		editCommand.setNewName("assignment");
		Output output = logic.executeCommand(editCommand);

		Output expected = new Output();
		expected.setReturnMessage("\"vday\" has been edited!");

		assertEquals(expected, output);

		// vday is item of index 3 in mockTaskList
		AbstractTask editedTask = logic.getTaskList().getTask(3);
		FloatingTask expectedTask = new FloatingTask("assignment");
		assertEquals(expectedTask, editedTask);
	}

	@Test
	public void editTaskStartByIndex() {
		logic.executeCommand(createBounded);
		logic.executeCommand(displayAll);

		EditCommand editCommand = new EditCommand(1);
		ArrayList<EditCommand.editField> editFields = new ArrayList<EditCommand.editField>();
		editFields.add(EditCommand.editField.START_DATE);
		editFields.add(EditCommand.editField.START_TIME);
		editCommand.setEditFields(editFields);
		editCommand.setNewStartDate("11 10 2015");
		editCommand.setNewStartTime("10 00");
		Output output = logic.executeCommand(editCommand);

		Output expected = new Output();
		expected.setReturnMessage("\"dinner\" has been edited!");

		assertEquals(expected, output);

		AbstractTask editedTask = logic.getTaskList().getTask(0);
		LocalDateTime newStart = LocalDateTime.parse("11 10 2015 10 00",
				DTFormatter);
		BoundedTask expectedTask = new BoundedTask("dinner", newStart, dummyEnd);
		assertEquals(expectedTask, editedTask);
	}

	@Test
	public void editTaskStartByIndexWithWrongDateOrder() {
		logic.executeCommand(createBounded);
		logic.executeCommand(displayAll);

		EditCommand editCommand = new EditCommand(1);
		ArrayList<EditCommand.editField> editFields = new ArrayList<EditCommand.editField>();
		editFields.add(EditCommand.editField.START_DATE);
		editFields.add(EditCommand.editField.START_TIME);
		editCommand.setEditFields(editFields);
		editCommand.setNewStartDate("14 10 2015");
		editCommand.setNewStartTime("10 00");
		Output output = logic.executeCommand(editCommand);

		Output expected = new Output();
		expected.setReturnMessage("Invalid: Start date time must be before End date time!");
		expected.setPriority(Priority.HIGH);

		assertEquals(expected, output);

		AbstractTask editedTask = logic.getTaskList().getTask(0);
		BoundedTask expectedTask = new BoundedTask("dinner", dummyStart,
				dummyEnd);
		assertEquals(expectedTask, editedTask);
	}

	@Test
	public void editDeadlineTaskEndByIndex() {
		logic.executeCommand(createDeadline);
		logic.executeCommand(displayAll);

		EditCommand editCommand = new EditCommand(1);
		ArrayList<EditCommand.editField> editFields = new ArrayList<EditCommand.editField>();
		editFields.add(EditCommand.editField.END_DATE);
		editFields.add(EditCommand.editField.END_TIME);
		editCommand.setEditFields(editFields);
		editCommand.setNewEndDate("14 10 2015");
		editCommand.setNewEndTime("10 00");
		Output output = logic.executeCommand(editCommand);

		Output expected = new Output();
		expected.setReturnMessage("\"assignment\" has been edited!");

		assertEquals(expected, output);

		AbstractTask editedTask = logic.getTaskList().getTask(0);
		LocalDateTime newEnd = LocalDateTime.parse("14 10 2015 10 00",
				DTFormatter);
		DeadlineTask expectedTask = new DeadlineTask("assignment", newEnd);
		assertEquals(expectedTask, editedTask);
	}

	@Test
	public void editBoundedTaskEndByIndex() {
		logic.executeCommand(createBounded);
		logic.executeCommand(displayAll);

		EditCommand editCommand = new EditCommand(1);
		ArrayList<EditCommand.editField> editFields = new ArrayList<EditCommand.editField>();
		editFields.add(EditCommand.editField.END_DATE);
		editFields.add(EditCommand.editField.END_TIME);
		editCommand.setEditFields(editFields);
		editCommand.setNewEndDate("14 10 2015");
		editCommand.setNewEndTime("10 00");
		Output output = logic.executeCommand(editCommand);

		Output expected = new Output();
		expected.setReturnMessage("\"dinner\" has been edited!");

		assertEquals(expected, output);

		AbstractTask editedTask = logic.getTaskList().getTask(0);
		LocalDateTime newEnd = LocalDateTime.parse("14 10 2015 10 00",
				DTFormatter);
		BoundedTask expectedTask = new BoundedTask("dinner", dummyStart, newEnd);
		assertEquals(expectedTask, editedTask);
	}

	@Test
	public void editTaskEndByIndexWithWrongDateOrder() {
		logic.executeCommand(createBounded);
		logic.executeCommand(displayAll);

		EditCommand editCommand = new EditCommand(1);
		ArrayList<EditCommand.editField> editFields = new ArrayList<EditCommand.editField>();
		editFields.add(EditCommand.editField.END_DATE);
		editFields.add(EditCommand.editField.END_TIME);
		editCommand.setEditFields(editFields);
		editCommand.setNewEndDate("10 10 2015");
		editCommand.setNewEndTime("10 00");
		Output output = logic.executeCommand(editCommand);

		Output expected = new Output();
		expected.setReturnMessage("Invalid: Start date time must be before End date time!");
		expected.setPriority(Priority.HIGH);

		assertEquals(expected, output);

		AbstractTask editedTask = logic.getTaskList().getTask(0);
		BoundedTask expectedTask = new BoundedTask("dinner", dummyStart,
				dummyEnd);
		assertEquals(expectedTask, editedTask);
	}

	@Test
	public void editTaskNameByNameOneHIT() {
		logic.executeCommand(createFloating);

		EditCommand editCommand = new EditCommand("birthday");
		ArrayList<EditCommand.editField> editFields = new ArrayList<EditCommand.editField>();
		editFields.add(EditCommand.editField.NAME);
		editCommand.setEditFields(editFields);
		editCommand.setNewName("assignment");
		Output output = logic.executeCommand(editCommand);

		Output expected = new Output();
		expected.setReturnMessage("\"birthday\" has been edited!");

		assertEquals(expected, output);

		AbstractTask editedTask = logic.getTaskList().getTask(0);
		FloatingTask expectedTask = new FloatingTask("assignment");
		assertEquals(expectedTask, editedTask);
	}

	@Test
	public void editTaskNameAndEndByNameOneHIT() {
		logic.executeCommand(createDeadline);

		EditCommand editCommand = new EditCommand("assignment");
		ArrayList<EditCommand.editField> editFields = new ArrayList<EditCommand.editField>();
		editFields.add(EditCommand.editField.NAME);
		editFields.add(EditCommand.editField.END_TIME);
		editFields.add(EditCommand.editField.END_DATE);
		editCommand.setEditFields(editFields);
		editCommand.setNewName("birthday");
		editCommand.setNewEndTime("08 00");
		editCommand.setNewEndDate("12 10 2015");
		Output output = logic.executeCommand(editCommand);

		Output expected = new Output();
		expected.setReturnMessage("\"assignment\" has been edited!");

		assertEquals(expected, output);

		AbstractTask editedTask = logic.getTaskList().getTask(0);
		DeadlineTask expectedTask = new DeadlineTask("birthday", dummyStart);
		assertEquals(expectedTask, editedTask);
	}

	@Test
	public void editTaskNameByNamePartialHIT() {
		logic.executeCommand(createFloating);

		EditCommand editCommand = new EditCommand("day");
		ArrayList<EditCommand.editField> editFields = new ArrayList<EditCommand.editField>();
		editFields.add(EditCommand.editField.NAME);
		editCommand.setEditFields(editFields);
		editCommand.setNewName("assignment");
		Output output = logic.executeCommand(editCommand);

		Output expected = new Output();
		expected.setReturnMessage("All tasks with keyword \"day\" are now displayed!");

		String[] floatingTask = { "1", "birthday", "", "", "", "", "", "", "",
				"", "", "", "UNDONE", "" };
		ArrayList<String> expectedFloatingTask = arrayToArrayList(floatingTask);

		ArrayList<ArrayList<String>> expectedList = new ArrayList<ArrayList<String>>();
		expectedList.add(expectedFloatingTask);
		expected.setOutput(expectedList);
		assertEquals(expected, output);

		AbstractTask editedTask = logic.getTaskList().getTask(0);
		FloatingTask expectedTask = new FloatingTask("birthday");
		assertEquals(expectedTask, editedTask);

		// Second step of edit by keyword partial or multiple hit

		EditCommand editByIndex = new EditCommand(1);
		Output secondOutput = logic.executeCommand(editByIndex);

		Output expectedSecondOutput = new Output();
		expectedSecondOutput.setReturnMessage("\"birthday\" has been edited!");

		assertEquals(expectedSecondOutput, secondOutput);

		AbstractTask editedAgainTask = logic.getTaskList().getTask(0);
		FloatingTask expectedAgainTask = new FloatingTask("assignment");
		assertEquals(expectedAgainTask, editedAgainTask);

	}

	@Test
	public void editTaskStartAndEndOneMonthLater() {
		logic.executeCommand(createBounded);
		logic.executeCommand(displayAll);

		EditCommand editCommand = new EditCommand(1);
		ArrayList<EditCommand.editField> editFields = new ArrayList<EditCommand.editField>();
		editFields.add(EditCommand.editField.START_DATE);
		editFields.add(EditCommand.editField.END_DATE);
		editCommand.setEditFields(editFields);
		editCommand.setNewStartDate("12 11 2015");
		editCommand.setNewEndDate("13 11 2015");
		Output output = logic.executeCommand(editCommand);

		Output expected = new Output();
		expected.setReturnMessage("\"dinner\" has been edited!");
		assertEquals(expected, output);

		AbstractTask editedTask = logic.getTaskList().getTask(0);
		LocalDateTime newStart = LocalDateTime.parse("12 11 2015 08 00",
				DTFormatter);
		LocalDateTime newEnd = LocalDateTime.parse("13 11 2015 08 00",
				DTFormatter);
		BoundedTask expectedTask = new BoundedTask("dinner", newStart, newEnd);
		assertEquals(expectedTask, editedTask);
	}
	
	/*
	 * DELETEACTION TESTS
	 */

	@Test
	public void deleteTaskByIndex() {
		logic.executeCommand(createFloating);
		logic.executeCommand(createDeadline);
		logic.executeCommand(createBounded);
		logic.executeCommand(displayAll);

		// dinner will be first task in last displayed
		DeleteCommand deleteCommand = new DeleteCommand(1);
		Output output = logic.executeCommand(deleteCommand);

		Output expected = new Output();
		expected.setReturnMessage("\"dinner\" has been deleted!");
		expected.setPriority(Priority.HIGH);

		assertEquals(expected, output);
		TaskList expectedTaskList = new TaskList();
		expectedTaskList.addTask(new FloatingTask("birthday"));
		expectedTaskList.addTask(new DeadlineTask("assignment", dummyEnd));
		assertEquals(expectedTaskList, logic.getTaskList());
	}

	@Test
	public void deleteTaskByKeywordOneHIT() {
		logic.executeCommand(createFloating);
		logic.executeCommand(createDeadline);
		logic.executeCommand(createBounded);

		DeleteCommand deleteCommand = new DeleteCommand("birthday");
		Output output = logic.executeCommand(deleteCommand);

		Output expected = new Output();
		expected.setReturnMessage("\"birthday\" has been deleted!");
		expected.setPriority(Priority.HIGH);

		assertEquals(expected, output);
		TaskList expectedTaskList = new TaskList();
		expectedTaskList.addTask(new DeadlineTask("assignment", dummyEnd));
		expectedTaskList
				.addTask(new BoundedTask("dinner", dummyStart, dummyEnd));
		assertEquals(expectedTaskList, logic.getTaskList());
	}

	@Test
	public void deleteTaskByKeywordOnePartialHIT() {
		logic.executeCommand(createFloating);
		logic.executeCommand(createDeadline);
		logic.executeCommand(createBounded);

		DeleteCommand deleteCommand = new DeleteCommand("day");
		Output output = logic.executeCommand(deleteCommand);

		String[] floatingTask = { "1", "birthday", "", "", "", "", "", "", "",
				"", "", "", "UNDONE", "" };
		ArrayList<String> expectedFloatingTask = arrayToArrayList(floatingTask);

		Output expected = new Output();
		ArrayList<ArrayList<String>> expectedList = new ArrayList<ArrayList<String>>();
		expectedList.add(expectedFloatingTask);
		expected.setOutput(expectedList);
		expected.setReturnMessage("All tasks with keyword \"day\" are now displayed!");
		assertEquals(expected, output);
		
		TaskList expectedTaskList = new TaskList();
		expectedTaskList.addTask(new FloatingTask("birthday"));
		expectedTaskList.addTask(new DeadlineTask("assignment", dummyEnd));
		expectedTaskList
				.addTask(new BoundedTask("dinner", dummyStart, dummyEnd));

		assertEquals(expectedTaskList, logic.getTaskList());
	}

	@Test
	public void deleteTaskByKeywordMultipleHIT() {
		CreateCommand createDeadline = new CreateCommand("examday", dummyEnd);
		logic.executeCommand(createFloating);
		logic.executeCommand(createDeadline);
		logic.executeCommand(createBounded);

		DeleteCommand deleteCommand = new DeleteCommand("day");
		Output output = logic.executeCommand(deleteCommand);

		String[] deadlineTask = { "1", "examday", "", "", "", "", "", "8am",
				"TUE", "13", "OCT", "2015", "UNDONE", "true" };
		ArrayList<String> expectedDeadlineTask = arrayToArrayList(deadlineTask);

		String[] floatingTask = { "2", "birthday", "", "", "", "", "", "", "",
				"", "", "", "UNDONE", "" };
		ArrayList<String> expectedFloatingTask = arrayToArrayList(floatingTask);

		Output expected = new Output();
		ArrayList<ArrayList<String>> expectedList = new ArrayList<ArrayList<String>>();
		expectedList.add(expectedDeadlineTask);
		expectedList.add(expectedFloatingTask);
		expected.setOutput(expectedList);
		expected.setReturnMessage("All tasks with keyword \"day\" are now displayed!");
		assertEquals(expected, output);
		
		TaskList expectedTaskList = new TaskList();
		expectedTaskList.addTask(new FloatingTask("birthday"));
		expectedTaskList.addTask(new DeadlineTask("examday", dummyEnd));
		expectedTaskList
				.addTask(new BoundedTask("dinner", dummyStart, dummyEnd));
		assertEquals(expectedTaskList, logic.getTaskList());
	}

	@Test
	public void deleteAllTasks() {
		logic.executeCommand(createFloating);
		logic.executeCommand(createDeadline);
		logic.executeCommand(createBounded);

		DeleteCommand deleteCommand = new DeleteCommand(Scope.ALL);
		Output output = logic.executeCommand(deleteCommand);

		Output expected = new Output();
		expected.setReturnMessage("All tasks have been deleted!");
		expected.setPriority(Priority.HIGH);

		assertEquals(expected, output);
		TaskList expectedTaskList = new TaskList();
		assertEquals(expectedTaskList, logic.getTaskList());
	}

	/*
	 * MARKACTION TESTS
	 */
	
	@Test
	public void markTaskByIndex() {
		logic.executeCommand(createFloating);
		logic.executeCommand(createDeadline);
		logic.executeCommand(createBounded);
		logic.executeCommand(displayAll);

		// dinner will be first task in last displayed
		MarkCommand markCommand = new MarkCommand(1);
		markCommand.setMarkField(markField.MARK);
		Output output = logic.executeCommand(markCommand);

		Output expected = new Output();
		expected.setReturnMessage("\"dinner\" has been marked done.");
		assertEquals(expected, output);
		
		AbstractTask expectedTask = new BoundedTask("dinner", dummyStart,
				dummyEnd);
		expectedTask.setStatus(Status.DONE);
		assertEquals(expectedTask, logic.getTaskList().getTask(2));
	}

	@Test
	public void markTaskByKeywordOneHIT() {
		logic.executeCommand(createFloating);
		logic.executeCommand(createDeadline);
		logic.executeCommand(createBounded);
		
		MarkCommand markCommand = new MarkCommand("birthday");
		markCommand.setMarkField(markField.MARK);
		Output output = logic.executeCommand(markCommand);

		Output expected = new Output();
		expected.setReturnMessage("\"birthday\" has been marked done.");
		assertEquals(expected, output);
		
		AbstractTask expectedTask = new FloatingTask("birthday");
		expectedTask.setStatus(Status.DONE);
		assertEquals(expectedTask, logic.getTaskList().getTask(0));
	}

	@Test
	public void markTaskByKeywordOnePartialHIT() {
		logic.executeCommand(createFloating);
		logic.executeCommand(createDeadline);
		logic.executeCommand(createBounded);
		
		MarkCommand markCommand = new MarkCommand("day");
		markCommand.setMarkField(markField.MARK);
		Output output = logic.executeCommand(markCommand);
		
		String[] floatingTask = { "1", "birthday", "", "", "", "", "", "", "",
				"", "", "", "UNDONE", "" };
		ArrayList<String> expectedFloatingTask = arrayToArrayList(floatingTask);

		Output expected = new Output();
		ArrayList<ArrayList<String>> expectedList = new ArrayList<ArrayList<String>>();
		expectedList.add(expectedFloatingTask);
		expected.setOutput(expectedList);
		expected.setReturnMessage("All tasks with keyword \"day\" are now displayed!");
		assertEquals(expected, output);
		
		TaskList expectedTaskList = new TaskList();
		expectedTaskList.addTask(new FloatingTask("birthday"));
		expectedTaskList.addTask(new DeadlineTask("assignment", dummyEnd));
		expectedTaskList
				.addTask(new BoundedTask("dinner", dummyStart, dummyEnd));

		assertEquals(expectedTaskList, logic.getTaskList());
	}

	@Test
	public void markTaskByKeywordMultipleHIT() {
		CreateCommand createDeadline = new CreateCommand("examday", dummyEnd);
		logic.executeCommand(createFloating);
		logic.executeCommand(createDeadline);
		logic.executeCommand(createBounded);

		MarkCommand markCommand = new MarkCommand("day");
		markCommand.setMarkField(markField.MARK);
		Output output = logic.executeCommand(markCommand);

		String[] deadlineTask = { "1", "examday", "", "", "", "", "", "8am",
				"TUE", "13", "OCT", "2015", "UNDONE", "true" };
		ArrayList<String> expectedDeadlineTask = arrayToArrayList(deadlineTask);

		String[] floatingTask = { "2", "birthday", "", "", "", "", "", "", "",
				"", "", "", "UNDONE", "" };
		ArrayList<String> expectedFloatingTask = arrayToArrayList(floatingTask);

		Output expected = new Output();
		ArrayList<ArrayList<String>> expectedList = new ArrayList<ArrayList<String>>();
		expectedList.add(expectedDeadlineTask);
		expectedList.add(expectedFloatingTask);
		expected.setOutput(expectedList);
		expected.setReturnMessage("All tasks with keyword \"day\" are now displayed!");
		assertEquals(expected, output);
		
		TaskList expectedTaskList = new TaskList();
		expectedTaskList.addTask(new FloatingTask("birthday"));
		expectedTaskList.addTask(new DeadlineTask("examday", dummyEnd));
		expectedTaskList
				.addTask(new BoundedTask("dinner", dummyStart, dummyEnd));
		assertEquals(expectedTaskList, logic.getTaskList());
	}

	/*
	 * UNDOACTION TESTS
	 */
	
	@Test
	public void undoPreviousActionCreate() {
		logic.executeCommand(createFloating);

		UndoCommand undoCommand = new UndoCommand();
		Output output = logic.executeCommand(undoCommand);
		Output expected = new Output();
		expected.setReturnMessage("\"create\" action has been undone!");
		assertEquals(expected, output);
		
		int expectedTaskListSize = 0;
		int actualTaskListSize = logic.getTaskList().size();
		assertEquals(expectedTaskListSize, actualTaskListSize);
	}

	@Test
	public void undoPreviousActionDelete() {
		DeleteCommand deleteCommand = new DeleteCommand(1);
		logic.executeCommand(createFloating);
		logic.executeCommand(displayAll);
		logic.executeCommand(deleteCommand);

		UndoCommand undoCommand = new UndoCommand();
		Output output = logic.executeCommand(undoCommand);
		Output expected = new Output();
		expected.setReturnMessage("\"delete\" action has been undone!");
		assertEquals(expected, output);
		assertTrue(logic.getTaskList().size() == 1);
		
		FloatingTask expectedTask = new FloatingTask("birthday");
		assertEquals(expectedTask, logic.getTaskList().getTask(0));
	}

}
