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
		assertEquals("2015-02-01", ((DeadlineTask) task).getEndDate());
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
		assertEquals("2015-02-01", ((BoundedTask) task).getEndDate());
		assertEquals("08:00", ((BoundedTask) task).getEndTime());
		assertEquals("2014-11-01", ((BoundedTask) task).getStartDate());
		assertEquals("16:00", ((BoundedTask) task).getStartTime());
	}

}
