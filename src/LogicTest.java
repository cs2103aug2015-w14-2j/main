import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;


public class LogicTest {
	private Logic logic = new Logic();
	
	@Test
	public void createFloatingTask() {
		ArrayList<String> testInput = new ArrayList<String>();
		ArrayList<ArrayList<String>> expectedOutput = new ArrayList<ArrayList<String>>();
		ArrayList<String> placeholder = new ArrayList<String>();
		
		testInput.add("create");
		testInput.add("meeting");
		testInput.add("");
		testInput.add("");
		testInput.add("");
		testInput.add("");		
		placeholder.add("\"meeting\" has been successfully created!");
		expectedOutput.add(placeholder);
		assertEquals(expectedOutput, logic.executeCommand(testInput));
		
		ArrayList<AbstractTask> taskList = logic.getTaskListTest();
		AbstractTask task = taskList.get(taskList.size() - 1);
		assertEquals(FloatingTask.class, task.getClass());
		assertEquals("meeting", task.getName());
	}
	
	@Test
	public void createDeadlineTask() {
		ArrayList<String> testInput = new ArrayList<String>();
		ArrayList<ArrayList<String>> expectedOutput = new ArrayList<ArrayList<String>>();
		ArrayList<String> placeholder = new ArrayList<String>();
		
		testInput.add("create");
		testInput.add("deadline");
		testInput.add("");
		testInput.add("");
		testInput.add("08 00");
		testInput.add("01 02 2015");		
		placeholder.add("\"deadline\" has been successfully created!");
		expectedOutput.add(placeholder);
		assertEquals(expectedOutput, logic.executeCommand(testInput));
		
		ArrayList<AbstractTask> taskList = logic.getTaskListTest();
		AbstractTask task = taskList.get(taskList.size() - 1);
		assertEquals(DeadlineTask.class, task.getClass());
		assertEquals("deadline", task.getName());
		assertEquals("01-02-2015", ((DeadlineTask) task).getEndDate());
		assertEquals("08:00", ((DeadlineTask) task).getEndTime());
	}
	
	@Test
	public void createBoundedTask() {
		ArrayList<String> testInput = new ArrayList<String>();
		ArrayList<ArrayList<String>> expectedOutput = new ArrayList<ArrayList<String>>();
		ArrayList<String> placeholder = new ArrayList<String>();
		
		testInput.add("create");
		testInput.add("bounded");
		testInput.add("16 00");
		testInput.add("01 11 2014");
		testInput.add("08 00");
		testInput.add("01 02 2015");		
		placeholder.add("\"bounded\" has been successfully created!");
		expectedOutput.add(placeholder);
		assertEquals(expectedOutput, logic.executeCommand(testInput));
		
		ArrayList<AbstractTask> taskList = logic.getTaskListTest();
		AbstractTask task = taskList.get(taskList.size() - 1);
		assertEquals(BoundedTask.class, task.getClass());
		assertEquals("bounded", task.getName());
		assertEquals("01-02-2015", ((BoundedTask) task).getEndDate());
		assertEquals("08:00", ((BoundedTask) task).getEndTime());
		assertEquals("01-11-2014", ((BoundedTask) task).getStartDate());
		assertEquals("16:00", ((BoundedTask) task).getStartTime());
	}
	
	@Test
	public void displayAllTasks() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		mockTaskList.add(new FloatingTask("birthday"));
		mockTaskList.add(new DeadlineTask("assignment", "23 59", "06 10 2015"));
		mockTaskList.add(new BoundedTask("dinner", "19 00", "07 10 2015", "22 00", "07 10 2015"));
		
		logic.setTaskListTest(mockTaskList);
		
		ArrayList<String> testInput = new ArrayList<String>();
		testInput.add("display");
		testInput.add("");
		testInput.add("");
		testInput.add("");
		testInput.add("");
		testInput.add("");		
		
		ArrayList<ArrayList<String>> expectedOutput = new ArrayList<ArrayList<String>>();
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
		expectedDeadlineTask.add("23:59");
		expectedDeadlineTask.add("06-10-2015");
		
		
		ArrayList<String> expectedBoundedTask = new ArrayList<String>();
		expectedBoundedTask.add("3.");
		expectedBoundedTask.add("dinner");
		expectedBoundedTask.add("19:00");
		expectedBoundedTask.add("07-10-2015");
		expectedBoundedTask.add("22:00");
		expectedBoundedTask.add("07-10-2015");
		
		ArrayList<String> expectedReturnMessage = new ArrayList<String>();
		expectedReturnMessage.add("all tasks are now displayed!");
		
		expectedOutput.add(expectedFloatingTask);
		expectedOutput.add(expectedDeadlineTask);
		expectedOutput.add(expectedBoundedTask);
		expectedOutput.add(expectedReturnMessage);
		assertEquals(expectedOutput, logic.executeCommand(testInput));
		assertEquals(mockTaskList, logic.getLastDisplayed());
	}
	
	@Test
	public void indexUpdateWithoutDisplay() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		mockTaskList.add(new FloatingTask("birthday"));
		
		logic.setTaskListTest(mockTaskList);
		
		ArrayList<String> testInput = new ArrayList<String>();		
		testInput.add("edit");
		testInput.add("name");
		testInput.add("#1");
		testInput.add("superman");
		testInput.add("");
		testInput.add("");
		
		ArrayList<ArrayList<String>> expectedOutput = new ArrayList<ArrayList<String>>();
		ArrayList<String> expectedReturnMessage = new ArrayList<String>();
		
		expectedReturnMessage.add("Please display tasks at least once to edit by index");
		expectedOutput.add(expectedReturnMessage);
		assertEquals(expectedOutput, logic.executeCommand(testInput));
	}
	
	@Test
	public void editTaskNameByIndex() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		mockTaskList.add(new FloatingTask("birthday"));
//		mockTaskList.add(new DeadlineTask("assignment", "23 59", "06 10 2015"));
//		mockTaskList.add(new BoundedTask("dinner", "19 00", "07 10 2015", "22 00", "07 10 2015"));
		
		logic.setTaskListTest(mockTaskList);
		logic.setLastDisplayed(mockTaskList);
		
		ArrayList<String> testInput = new ArrayList<String>();		
		testInput.add("edit");
		testInput.add("name");
		testInput.add("#1");
		testInput.add("superman");
		testInput.add("");
		testInput.add("");
		
		ArrayList<ArrayList<String>> expectedOutput = new ArrayList<ArrayList<String>>();
		ArrayList<String> expectedReturnMessage = new ArrayList<String>();
		
		expectedReturnMessage.add("name of \"birthday\" has been successfully changed to \"superman\"!");
		expectedOutput.add(expectedReturnMessage);
		assertEquals(expectedOutput, logic.executeCommand(testInput));
		AbstractTask editedTask = logic.getTaskListTest().get(0);
		assertEquals("superman", editedTask.getName());
	}
	
	@Test
	public void editTaskStartByIndex() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		mockTaskList.add(new BoundedTask("dinner", "19 00", "07 10 2015", "22 00", "07 10 2015"));
		
		logic.setTaskListTest(mockTaskList);
		logic.setLastDisplayed(mockTaskList);
		
		ArrayList<String> testInput = new ArrayList<String>();		
		testInput.add("edit");
		testInput.add("start");
		testInput.add("#1");
		testInput.add("20 00");
		testInput.add("06 10 2015");
		testInput.add("");
		
		ArrayList<ArrayList<String>> expectedOutput = new ArrayList<ArrayList<String>>();
		ArrayList<String> expectedReturnMessage = new ArrayList<String>();
		
		expectedReturnMessage.add("start time and date of \"dinner\" has been successfully changed to \"20:00 06-10-2015\"!");
		expectedOutput.add(expectedReturnMessage);
		assertEquals(expectedOutput, logic.executeCommand(testInput));
		AbstractTask editedTask = logic.getTaskListTest().get(0);
		assertEquals("20:00", ((BoundedTask) editedTask).getStartTime());
		assertEquals("06-10-2015", ((BoundedTask) editedTask).getStartDate());
	}
	
	@Test
	public void editTaskEndByIndex() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		mockTaskList.add(new BoundedTask("dinner", "19 00", "07 10 2015", "22 00", "07 10 2015"));
		
		logic.setTaskListTest(mockTaskList);
		logic.setLastDisplayed(mockTaskList);
		
		ArrayList<String> testInput = new ArrayList<String>();		
		testInput.add("edit");
		testInput.add("end");
		testInput.add("#1");
		testInput.add("20 00");
		testInput.add("08 10 2015");
		testInput.add("");
		
		ArrayList<ArrayList<String>> expectedOutput = new ArrayList<ArrayList<String>>();
		ArrayList<String> expectedReturnMessage = new ArrayList<String>();
		
		expectedReturnMessage.add("end time and date of \"dinner\" has been successfully changed to \"20:00 08-10-2015\"!");
		expectedOutput.add(expectedReturnMessage);
		assertEquals(expectedOutput, logic.executeCommand(testInput));
		AbstractTask editedTask = logic.getTaskListTest().get(0);
		assertEquals("20:00", ((BoundedTask) editedTask).getEndTime());
		assertEquals("08-10-2015", ((BoundedTask) editedTask).getEndDate());
	}
	
	@Test
	public void indexDeleteWithoutDisplay() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		mockTaskList.add(new FloatingTask("birthday"));
		
		logic.setTaskListTest(mockTaskList);
		
		ArrayList<String> testInput = new ArrayList<String>();		
		testInput.add("delete");
		testInput.add("#1");
		testInput.add("");
		testInput.add("");
		testInput.add("");
		testInput.add("");
		
		ArrayList<ArrayList<String>> expectedOutput = new ArrayList<ArrayList<String>>();
		ArrayList<String> expectedReturnMessage = new ArrayList<String>();
		
		expectedReturnMessage.add("Please display tasks at least once to delete by index");
		expectedOutput.add(expectedReturnMessage);
		assertEquals(expectedOutput, logic.executeCommand(testInput));
	}
	
	@Test
	public void deleteTaskByIndex() {
		ArrayList<AbstractTask> mockTaskList = new ArrayList<AbstractTask>();
		FloatingTask testTask = new FloatingTask("birthday");
		mockTaskList.add(testTask);
		
		logic.setTaskListTest(mockTaskList);
		logic.setLastDisplayed(mockTaskList);
		
		ArrayList<String> testInput = new ArrayList<String>();		
		testInput.add("delete");
		testInput.add("#1");
		testInput.add("");
		testInput.add("");
		testInput.add("");
		testInput.add("");
		
		ArrayList<ArrayList<String>> expectedOutput = new ArrayList<ArrayList<String>>();
		ArrayList<String> expectedReturnMessage = new ArrayList<String>();
		
		expectedReturnMessage.add("\"birthday\" has been deleted!");
		expectedOutput.add(expectedReturnMessage);
		assertEquals(expectedOutput, logic.executeCommand(testInput));
		assertEquals(false, logic.getTaskListTest().contains(testTask));
	}

}
