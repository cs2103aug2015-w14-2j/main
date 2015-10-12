package parser;

import static org.junit.Assert.*;
import org.junit.Test;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ParserTest {
	
	DateTimeFormatter DTFormatter = DateTimeFormatter.ofPattern("dd MM yyyy HH mm");
	Parser parser = new Parser();
	InvalidCommand expectedInvalid = new InvalidCommand();
	
	private String dummyDate = "01 01 2015";
	private String dummyTime = "00 00";
	
	public void test(String rawInput, AbstractCommand expected) {
		assertEquals(parser.parseInput(rawInput), expected);
	}
	
	//*******************************************************************
	//*******************************************************************
	// 	FOR CREATE COMMAND
	//*******************************************************************
	//*******************************************************************
	
	//===================================================================
	// STANDARD CREATE TESTS
	//===================================================================
	
	@Test
	public void createFTOneWordName() {
		String input = "create lecture";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("lecture");
		assertEquals(expected, output);
	}
	
	@Test
	public void cFTManyWordsName() {
		String input = "c buy apples, oranges and starfruits";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("buy apples, oranges and starfruits");
		assertEquals(expected, output);
	}
	
	@Test
	public void createFTManyWordsName() {
		String input = "create buy apples, oranges and starfruits";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("buy apples, oranges and starfruits");
		assertEquals(expected, output);
	}
	
	@Test
	public void createDTOneWordName() {
		String input = "create lecture by 12pm 10-10-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("lecture", LocalDateTime.parse("10 10 2015 12 00", DTFormatter));		
		assertEquals(expected, output);
	}
	
	@Test
	public void cDTManyWordsName() {
		String input = "c buy apples, oranges and starfruits by 12pm 10-10-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("buy apples, oranges and starfruits", LocalDateTime.parse("10 10 2015 12 00", DTFormatter));		
		assertEquals(expected, output);
	}
	
	@Test
	public void createDTManyWordsName() {
		String input = "create buy apples, oranges and starfruits by 12pm 10-10-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("buy apples, oranges and starfruits", LocalDateTime.parse("10 10 2015 12 00", DTFormatter));		
		assertEquals(expected, output);
	}
	
	@Test
	public void createBTOneWordName() {
		String input = "create lecture from 12pm 10-10-2015 to 2pm 10-10-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("lecture", LocalDateTime.parse("10 10 2015 12 00", DTFormatter), LocalDateTime.parse("10 10 2015 14 00", DTFormatter));		
		assertEquals(expected, output);
	}
	
	@Test
	public void cBTManyWordsName() {
		String input = "c buy apples, oranges and starfruits from 12pm 10-10-2015 to 2pm 10-10-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("buy apples, oranges and starfruits", LocalDateTime.parse("10 10 2015 12 00", DTFormatter), LocalDateTime.parse("10 10 2015 14 00", DTFormatter));		
		assertEquals(expected, output);
	}
	
	@Test
	public void createBTManyWordsName() {
		String input = "create buy apples, oranges and starfruits from 12pm 10-10-2015 to 2pm 10-10-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("buy apples, oranges and starfruits", LocalDateTime.parse("10 10 2015 12 00", DTFormatter), LocalDateTime.parse("10 10 2015 14 00", DTFormatter));		
		assertEquals(expected, output);
	}
	
	@Test
	public void createFTNoName() {
		String input = "create";
		AbstractCommand output = parser.parseInput(input);
		assertEquals(output, expectedInvalid);
	}
	
	@Test
	public void createDTNoName() {
		String input = "create by 12pm 10-10-2015";
		AbstractCommand output = parser.parseInput(input);		
		assertEquals(output, expectedInvalid);
	}
	
	@Test
	public void createBTNoName() {
		String input = "create from 12pm 10-10-2015 to 2pm 10-10-2015";
		AbstractCommand output = parser.parseInput(input);	
		assertEquals(output, expectedInvalid);
	}
	
	//===================================================================
	// CREATE WITH DIFFERENT TIME FORMATS
	//===================================================================

		@Test
		public void createValidTime1() {
			String input = "create example by 8am 10-10-2015";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("10 10 2015 08 00", DTFormatter));		
			assertEquals(expected, output);
		}
		
		@Test
		public void createValidTime2() {
			String input = "create example from 12AM 10-10-2015 to 12pm 10-10-2015";
			CreateCommand output = (CreateCommand) parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("10 10 2015 00 00", DTFormatter), LocalDateTime.parse("10 10 2015 12 00", DTFormatter));		
			assertEquals(expected, output);
		}
		
		@Test
		public void createValidTime3() {
			String input = "create example by 8PM 10-10-2015";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("10 10 2015 20 00", DTFormatter));		
			assertEquals(expected, output);
		}
		
		@Test
		public void createValidTime4() {
			String input = "create example by 12:00am 10-10-2015";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("10 10 2015 00 00", DTFormatter));		
			assertEquals(expected, output);
		}
		
		@Test
		public void createValidTime5() {
			String input = "create example from 06:30AM 10-10-2015 to 6:15pm 10-10-2015";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("10 10 2015 06 30", DTFormatter), LocalDateTime.parse("10 10 2015 18 15", DTFormatter));		
			assertEquals(expected, output);
		}
		
		@Test
		public void createValidTime6() {
			String input = "create example by 12:45PM 10-10-2015";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("10 10 2015 12 45", DTFormatter));		
			assertEquals(expected, output);
		}
		
		@Test
		public void createValidTime7() {
			String input = "create example by 00:00 10-10-2015";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("10 10 2015 00 00", DTFormatter));		
			assertEquals(expected, output);
		}

		@Test
		public void createValidTime8() {
			String input = "create example from 4:15 10-10-2015 to 12:00 10-10-2015";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("10 10 2015 04 15", DTFormatter), LocalDateTime.parse("10 10 2015 12 00", DTFormatter));		
			assertEquals(expected, output);
		}
		
		@Test
		public void createValidTime9() {
			String input = "create example by 08:30 10-10-2015";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("10 10 2015 08 30", DTFormatter));		
			assertEquals(expected, output);
		}
		
		@Test
		public void createValidTime10() {
			String input = "create example by 16:45 10-10-2015";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("10 10 2015 16 45", DTFormatter));		
			assertEquals(expected, output);
		}
		
		@Test
		public void createInvalidTime1() {
			String input = "create example by 24:00 10-10-2015";
			AbstractCommand output = parser.parseInput(input);		
			assertEquals(output, expectedInvalid);
		}
		
		//===================================================================
		// CREATE WITH DIFFERENT DATE FORMATS
		//===================================================================
		
		@Test
		public void createValidDate1() {
			String input = "create example by 10am 10-10-2015";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("10 10 2015 10 00", DTFormatter));		
			assertEquals(expected, output);
		}
		
		@Test
		public void createValidDate2() {
			String input = "create example from 10am 1-10-2015 to 12pm 10-1-2015";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("01 10 2015 10 00", DTFormatter), LocalDateTime.parse("10 01 2015 12 00", DTFormatter));		
			assertEquals(expected, output);
		}
		
		@Test
		public void createValidDate3() {
			String input = "create example by 10am 1-1-2015";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("01 01 2015 10 00", DTFormatter));		
			assertEquals(expected, output);
		}
		
		@Test
		public void createValidDate4() {
			String input = "create example by 10am 1-10-15";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("01 10 2015 10 00", DTFormatter));		
			assertEquals(expected, output);
		}
		
		@Test
		public void createValidDate5() {
			String input = "create example from 10am 1-1-15 to 12pm 10-10-15";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("01 01 2015 10 00", DTFormatter), LocalDateTime.parse("10 10 2015 12 00", DTFormatter));		
			assertEquals(expected, output);
		}
		
		@Test
		public void createValidDate6() {
			String input = "create example by 10am 1-10-15";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("01 10 2015 10 00", DTFormatter));		
			assertEquals(expected, output);
		}
		
		@Test
		public void createValidDate7() {
			String input = "create example by 10am 1/10/2015";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("01 10 2015 10 00", DTFormatter));		
			assertEquals(expected, output);
		}
		
		@Test
		public void createValidDate8() {
			String input = "create example from 10am 1/1/2015 to 12pm 10/10/2015";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("01 01 2015 10 00", DTFormatter), LocalDateTime.parse("10 10 2015 12 00", DTFormatter));		
			assertEquals(expected, output);
		}
		
		@Test
		public void createValidDate9() {
			String input = "create example by 10am 10/1/2015";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("10 01 2015 10 00", DTFormatter));		
			assertEquals(expected, output);
		}
		
		@Test
		public void createValidDate10() {
			String input = "create example by 10am 10/10/15";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("10 10 2015 10 00", DTFormatter));		
			assertEquals(expected, output);
		}
		
		@Test
		public void createValidDate11() {
			String input = "create example from 10am 1/10/15 to 12pm 10/1/15";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("01 10 2015 10 00", DTFormatter), LocalDateTime.parse("10 01 2015 12 00", DTFormatter));		
			assertEquals(expected, output);
		}
		
		@Test
		public void createValidDate12() {
			String input = "create example by 10am 1/1/15";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("01 01 2015 10 00", DTFormatter));		
			assertEquals(expected, output);
		}
		
		// INVALID DATES
		// DAY+MONTH COMBINATIONS
		
		//===================================================================
		// TEST WITH KEYWORDS (FROM, TO, BY) IN NAME
		//===================================================================
		
		@Test
		public void createFTWithKeyword1() {
			String input = "create by something something something";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("by something something something");
			assertEquals(expected, output);
		}
		
		@Test
		public void createFTWithKeyword2() {
			String input = "create something by something something";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("something by something something");
			assertEquals(expected, output);
		}
		
		@Test
		public void createFTWithKeyword3() {
			String input = "create something something by something";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("something something by something");
			assertEquals(expected, output);
		}
		
		@Test
		public void createFTWithKeyword4() {
			String input = "create something something something by";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("something something something by");
			assertEquals(expected, output);
		}
		
		@Test
		public void createFTWithKeyword5() {
			String input = "create from something something something";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("from something something something");
			assertEquals(expected, output);
		}
		
		@Test
		public void createFTWithKeyword6() {
			String input = "create something from something something";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("something from something something");
			assertEquals(expected, output);
		}
		
		@Test
		public void createFTWithKeyword7() {
			String input = "create something something from something";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("something something from something");
			assertEquals(expected, output);
		}
		
		@Test
		public void createFTWithKeyword8() {
			String input = "create something something something from";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("something something something from");
			assertEquals(expected, output);
		}
		
		@Test
		public void createFTWithKeyword9() {
			String input = "create to something something something";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("to something something something");
			assertEquals(expected, output);
		}
		
		@Test
		public void createFTWithKeyword10() {
			String input = "create something to something something";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("something to something something");
			assertEquals(expected, output);
		}
		
		@Test
		public void createFTWithKeyword11() {
			String input = "create something something to something";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("something something to something");
			assertEquals(expected, output);
		}
		
		@Test
		public void createFTWithKeyword12() {
			String input = "create something something something to";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("something something something to");
			assertEquals(expected, output);
		}
		
		@Test
		public void createFTWithKeyword13() {
			String input = "create from something to something";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("from something to something");
			assertEquals(expected, output);
		}
		
		@Test
		public void createFTWithKeyword14() {
			String input = "create something from something to something";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("something from something to something");
			assertEquals(expected, output);
		}
		
		@Test
		public void createFTWithKeyword15() {
			String input = "create from something something to something something";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("from something something to something something");
			assertEquals(expected, output);
		}
		
		@Test
		public void createFTWithKeyword16() {
			String input = "create something from something something to something something";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("something from something something to something something");
			assertEquals(expected, output);
		}
		
		@Test
		public void createDTWithKeyword1() {
			String input = "create test by test test by 10am 05-05-15";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("test by test test", LocalDateTime.parse("05 05 2015 10 00", DTFormatter));
			assertEquals(expected, output);
		}
		
		@Test
		public void createDTWithKeyword2() {
			String input = "create test test by test by 10am 05-05-15";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("test test by test", LocalDateTime.parse("05 05 2015 10 00", DTFormatter));
			assertEquals(expected, output);
		}
		
		
		@Test
		public void createDTWithKeyword3() {
			String input = "create test test test by by 10am 05-05-15";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("test test test by", LocalDateTime.parse("05 05 2015 10 00", DTFormatter));
			assertEquals(expected, output);
		}
		
		@Test
		public void createDTWithKeyword4() {
			String input = "create test from test test by 10am 05-05-15";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("test from test test", LocalDateTime.parse("05 05 2015 10 00", DTFormatter));
			assertEquals(expected, output);
		}
		
		@Test
		public void createDTWithKeyword5() {
			String input = "create test test from test by 10am 05-05-15";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("test test from test", LocalDateTime.parse("05 05 2015 10 00", DTFormatter));
			assertEquals(expected, output);
		}
		
		
		@Test
		public void createDTWithKeyword6() {
			String input = "create test test test from by 10am 05-05-15";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("test test test from", LocalDateTime.parse("05 05 2015 10 00", DTFormatter));
			assertEquals(expected, output);
		}
		
		@Test
		public void createDTWithKeyword7() {
			String input = "create test to test test by 10am 05-05-15";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("test to test test", LocalDateTime.parse("05 05 2015 10 00", DTFormatter));
			assertEquals(expected, output);
		}
		
		@Test
		public void createDTWithKeyword8() {
			String input = "create test test to test by 10am 05-05-15";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("test test to test", LocalDateTime.parse("05 05 2015 10 00", DTFormatter));
			assertEquals(expected, output);
		}
		
		
		@Test
		public void createDTWithKeyword9() {
			String input = "create test test test to by 10am 05-05-15";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("test test test to", LocalDateTime.parse("05 05 2015 10 00", DTFormatter));
			assertEquals(expected, output);
		}
		
		public void createDTWithKeyword10() {
			String input = "create from test to test by 10am 05-05-15";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("from test to test", LocalDateTime.parse("05 05 2015 10 00", DTFormatter));
			assertEquals(expected, output);
		}
		
		public void createDTWithKeyword11() {
			String input = "create test from test to test by 10am 05-05-15";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("test from test to test", LocalDateTime.parse("05 05 2015 10 00", DTFormatter));
			assertEquals(expected, output);
		}
		
		public void createDTWithKeyword12() {
			String input = "create from test test to test test by 10am 05-05-15";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("from test test to test test", LocalDateTime.parse("05 05 2015 10 00", DTFormatter));
			assertEquals(expected, output);
		}
		
		public void createDTWithKeyword13() {
			String input = "create test from test test to test test by 10am 05-05-15";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("test from test test to test test", LocalDateTime.parse("05 05 2015 10 00", DTFormatter));
			assertEquals(expected, output);
		}
		
		public void createBTWithKeyword1() {
			String input = "create test by test test from 10am 5-5-15 to 13:00 05-05-2015";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("test by test test", LocalDateTime.parse("05 05 2015 10 00", DTFormatter), LocalDateTime.parse("05 05 2015 13 00", DTFormatter));
			assertEquals(expected, output);
		}
		
		public void createBTWithKeyword2() {
			String input = "create test from test test to test test from 10am 5-5-15 to 13:00 05-05-2015";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("test from test test to test test", LocalDateTime.parse("05 05 2015 10 00", DTFormatter), LocalDateTime.parse("05 05 2015 13 00", DTFormatter));
			assertEquals(expected, output);
		}
		
		//*******************************************************************
		//*******************************************************************
		// 	FOR EDIT COMMAND
		//*******************************************************************
		//*******************************************************************
		
		//===================================================================
		// STANDARD EDIT TESTS
		//===================================================================
		
		@Test
		public void editIndex() {
			String input = "edit #5";
			EditCommand output = (EditCommand) parser.parseInput(input);
			
			EditCommand expected = new EditCommand("5");
			ArrayList<EditCommand.Type> editType = new ArrayList<EditCommand.Type>();
			expected.setEditType(editType);
			
			assertEquals(output.getIdentifierType(), expected.getIdentifierType());
			assertEquals(output.getNewEndDate(), expected.getNewEndDate());
			assertEquals(output.getNewEndTime(), expected.getNewEndTime());
			assertEquals(output.getNewStartDate(), expected.getNewStartDate());
			assertEquals(output.getNewStartTime(), expected.getNewStartTime());
			assertEquals(output.getNewName(), expected.getNewName());
			assertEquals(output.getIndex(), expected.getIndex());
			assertEquals(output.getTaskName(), expected.getTaskName());
			assertEquals(output.getEditType(), expected.getEditType());
			
			//assertEquals(expected, output);
		}
		
		@Test
		public void editByIndexNSTSDETED() {
			String input = "edit #2 to sad start 3pm 20-10-2015 end 5pm 20-10-2015";
			EditCommand output = (EditCommand) parser.parseInput(input);
			
			EditCommand expected = new EditCommand("2");
			ArrayList<EditCommand.Type> editType = new ArrayList<EditCommand.Type>();
			editType.add(EditCommand.Type.NAME);
			expected.setNewName("sad");
			editType.add(EditCommand.Type.START_TIME);
			expected.setNewStartTime(LocalDateTime.parse(dummyDate + " 15 00", DTFormatter));
			editType.add(EditCommand.Type.START_DATE);
			expected.setNewStartDate(LocalDateTime.parse("20 10 2015 " + dummyTime, DTFormatter));
			editType.add(EditCommand.Type.END_TIME);
			expected.setNewEndTime(LocalDateTime.parse(dummyDate + " 17 00", DTFormatter));
			editType.add(EditCommand.Type.END_DATE);
			expected.setNewEndDate(LocalDateTime.parse("20 10 2015 " + dummyTime, DTFormatter));
			expected.setEditType(editType);
			
			assertEquals(output.getIdentifierType(), expected.getIdentifierType());
			assertEquals(output.getNewEndDate(), expected.getNewEndDate());
			assertEquals(output.getNewEndTime(), expected.getNewEndTime());
			assertEquals(output.getNewStartDate(), expected.getNewStartDate());
			assertEquals(output.getNewStartTime(), expected.getNewStartTime());
			assertEquals(output.getNewName(), expected.getNewName());
			assertEquals(output.getIndex(), expected.getIndex());
			assertEquals(output.getTaskName(), expected.getTaskName());
			assertEquals(output.getEditType(), expected.getEditType());
			
			//assertEquals(expected, output);
		}
		
		@Test
		public void editBySearchNSTSDETED() {
			String input = "edit happy to sad start 3pm 20-10-2015 end 5pm 20-10-2015";
			EditCommand output = (EditCommand) parser.parseInput(input);
			
			EditCommand expected = new EditCommand("happy");
			ArrayList<EditCommand.Type> editType = new ArrayList<EditCommand.Type>();
			editType.add(EditCommand.Type.NAME);
			expected.setNewName("sad");
			editType.add(EditCommand.Type.START_TIME);
			expected.setNewStartTime(LocalDateTime.parse(dummyDate + " 15 00", DTFormatter));
			editType.add(EditCommand.Type.START_DATE);
			expected.setNewStartDate(LocalDateTime.parse("20 10 2015 " + dummyTime, DTFormatter));
			editType.add(EditCommand.Type.END_TIME);
			expected.setNewEndTime(LocalDateTime.parse(dummyDate + " 17 00", DTFormatter));
			editType.add(EditCommand.Type.END_DATE);
			expected.setNewEndDate(LocalDateTime.parse("20 10 2015 " + dummyTime, DTFormatter));
			expected.setEditType(editType);
			
			assertEquals(output.getIdentifierType(), expected.getIdentifierType());
			assertEquals(output.getNewEndDate(), expected.getNewEndDate());
			assertEquals(output.getNewEndTime(), expected.getNewEndTime());
			assertEquals(output.getNewStartDate(), expected.getNewStartDate());
			assertEquals(output.getNewStartTime(), expected.getNewStartTime());
			assertEquals(output.getNewName(), expected.getNewName());
			assertEquals(output.getIndex(), expected.getIndex());
			assertEquals(output.getTaskName(), expected.getTaskName());
			assertEquals(output.getEditType(), expected.getEditType());
			
			//assertEquals(expected, output);
		}
		
		@Test
		public void editBySearchNSDSTEDET() {
			String input = "edit happy to sad start 20-10-2015 3pm end 20-10-2015 5pm";
			EditCommand output = (EditCommand) parser.parseInput(input);
			
			EditCommand expected = new EditCommand("happy");
			ArrayList<EditCommand.Type> editType = new ArrayList<EditCommand.Type>();
			editType.add(EditCommand.Type.NAME);
			expected.setNewName("sad");
			editType.add(EditCommand.Type.START_TIME);
			expected.setNewStartTime(LocalDateTime.parse(dummyDate + " 15 00", DTFormatter));
			editType.add(EditCommand.Type.START_DATE);
			expected.setNewStartDate(LocalDateTime.parse("20 10 2015 " + dummyTime, DTFormatter));
			editType.add(EditCommand.Type.END_TIME);
			expected.setNewEndTime(LocalDateTime.parse(dummyDate + " 17 00", DTFormatter));
			editType.add(EditCommand.Type.END_DATE);
			expected.setNewEndDate(LocalDateTime.parse("20 10 2015 " + dummyTime, DTFormatter));
			expected.setEditType(editType);
			
			assertEquals(output.getIdentifierType(), expected.getIdentifierType());
			assertEquals(output.getNewEndDate(), expected.getNewEndDate());
			assertEquals(output.getNewEndTime(), expected.getNewEndTime());
			assertEquals(output.getNewStartDate(), expected.getNewStartDate());
			assertEquals(output.getNewStartTime(), expected.getNewStartTime());
			assertEquals(output.getNewName(), expected.getNewName());
			assertEquals(output.getIndex(), expected.getIndex());
			assertEquals(output.getTaskName(), expected.getTaskName());
			assertEquals(output.getEditType(), expected.getEditType());
			
			//assertEquals(expected, output);
		}
		
		@Test
		public void editBySearchNSTSD() {
			String input = "edit happy to sad start 3pm 20-10-2015";
			EditCommand output = (EditCommand) parser.parseInput(input);
			
			EditCommand expected = new EditCommand("happy");
			ArrayList<EditCommand.Type> editType = new ArrayList<EditCommand.Type>();
			editType.add(EditCommand.Type.NAME);
			expected.setNewName("sad");
			editType.add(EditCommand.Type.START_TIME);
			expected.setNewStartTime(LocalDateTime.parse(dummyDate + " 15 00", DTFormatter));
			editType.add(EditCommand.Type.START_DATE);
			expected.setNewStartDate(LocalDateTime.parse("20 10 2015 " + dummyTime, DTFormatter));
			expected.setEditType(editType);
			
			assertEquals(output.getIdentifierType(), expected.getIdentifierType());
			assertEquals(output.getNewEndDate(), expected.getNewEndDate());
			assertEquals(output.getNewEndTime(), expected.getNewEndTime());
			assertEquals(output.getNewStartDate(), expected.getNewStartDate());
			assertEquals(output.getNewStartTime(), expected.getNewStartTime());
			assertEquals(output.getNewName(), expected.getNewName());
			assertEquals(output.getIndex(), expected.getIndex());
			assertEquals(output.getTaskName(), expected.getTaskName());
			assertEquals(output.getEditType(), expected.getEditType());
			
			//assertEquals(expected, output);
		}
		
		@Test
		public void editBySearchNEDET() {
			String input = "edit happy to sad end 20-10-2015 3pm";
			EditCommand output = (EditCommand) parser.parseInput(input);
			
			EditCommand expected = new EditCommand("happy");
			ArrayList<EditCommand.Type> editType = new ArrayList<EditCommand.Type>();
			editType.add(EditCommand.Type.NAME);
			expected.setNewName("sad");
			editType.add(EditCommand.Type.END_TIME);
			expected.setNewEndTime(LocalDateTime.parse(dummyDate + " 15 00", DTFormatter));
			editType.add(EditCommand.Type.END_DATE);
			expected.setNewEndDate(LocalDateTime.parse("20 10 2015 " + dummyTime, DTFormatter));
			expected.setEditType(editType);
			
			assertEquals(output.getIdentifierType(), expected.getIdentifierType());
			assertEquals(output.getNewEndDate(), expected.getNewEndDate());
			assertEquals(output.getNewEndTime(), expected.getNewEndTime());
			assertEquals(output.getNewStartDate(), expected.getNewStartDate());
			assertEquals(output.getNewStartTime(), expected.getNewStartTime());
			assertEquals(output.getNewName(), expected.getNewName());
			assertEquals(output.getIndex(), expected.getIndex());
			assertEquals(output.getTaskName(), expected.getTaskName());
			assertEquals(output.getEditType(), expected.getEditType());
			
			//assertEquals(expected, output);
		}
		
		@Test
		public void editBySearchSTSDETED() {
			String input = "edit happy to start 1pm 20-10-2015 end 3pm 20-10-2015";
			EditCommand output = (EditCommand) parser.parseInput(input);
			
			EditCommand expected = new EditCommand("happy");
			ArrayList<EditCommand.Type> editType = new ArrayList<EditCommand.Type>();
			editType.add(EditCommand.Type.START_TIME);
			expected.setNewStartTime(LocalDateTime.parse(dummyDate + " 13 00", DTFormatter));
			editType.add(EditCommand.Type.START_DATE);
			expected.setNewStartDate(LocalDateTime.parse("20 10 2015 " + dummyTime, DTFormatter));
			editType.add(EditCommand.Type.END_TIME);
			expected.setNewEndTime(LocalDateTime.parse(dummyDate + " 15 00", DTFormatter));
			editType.add(EditCommand.Type.END_DATE);
			expected.setNewEndDate(LocalDateTime.parse("20 10 2015 " + dummyTime, DTFormatter));
			expected.setEditType(editType);
			
			assertEquals(output.getIdentifierType(), expected.getIdentifierType());
			assertEquals(output.getNewEndDate(), expected.getNewEndDate());
			assertEquals(output.getNewEndTime(), expected.getNewEndTime());
			assertEquals(output.getNewStartDate(), expected.getNewStartDate());
			assertEquals(output.getNewStartTime(), expected.getNewStartTime());
			assertEquals(output.getNewName(), expected.getNewName());
			assertEquals(output.getIndex(), expected.getIndex());
			assertEquals(output.getTaskName(), expected.getTaskName());
			assertEquals(output.getEditType(), expected.getEditType());
			
			//assertEquals(expected, output);
		}
		
		@Test
		public void editBySearchSTET() {
			String input = "edit tuition start 1pm end 3pm";
			EditCommand output = (EditCommand) parser.parseInput(input);
			
			EditCommand expected = new EditCommand("tuition");
			ArrayList<EditCommand.Type> editType = new ArrayList<EditCommand.Type>();
			editType.add(EditCommand.Type.START_TIME);
			expected.setNewStartTime(LocalDateTime.parse(dummyDate + " 13 00", DTFormatter));
			editType.add(EditCommand.Type.END_TIME);
			expected.setNewEndTime(LocalDateTime.parse(dummyDate + " 15 00", DTFormatter));
			expected.setEditType(editType);
			
			assertEquals(output.getIdentifierType(), expected.getIdentifierType());
			assertEquals(output.getNewEndDate(), expected.getNewEndDate());
			assertEquals(output.getNewEndTime(), expected.getNewEndTime());
			assertEquals(output.getNewStartDate(), expected.getNewStartDate());
			assertEquals(output.getNewStartTime(), expected.getNewStartTime());
			assertEquals(output.getNewName(), expected.getNewName());
			assertEquals(output.getIndex(), expected.getIndex());
			assertEquals(output.getTaskName(), expected.getTaskName());
			assertEquals(output.getEditType(), expected.getEditType());
			
			assertEquals(expected, output);
		}
		
}
