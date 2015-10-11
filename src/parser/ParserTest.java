package parser;

import static org.junit.Assert.*;
import org.junit.Test;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ParserTest {
	
	DateTimeFormatter DTFormatter = DateTimeFormatter.ofPattern("dd MM yyyy HH mm");
	Parser parser = new Parser();
	
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
		assertEquals(output, expected);
	}
	
	@Test
	public void createFTManyWordsName() {
		String input = "create buy apples, oranges and starfruits";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("buy apples, oranges and starfruits");
		assertEquals(output, expected);
	}
	
	@Test
	public void createDTOneWordName() {
		String input = "create lecture by 12pm 10-10-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("lecture", LocalDateTime.parse("10 10 2015 12 00", DTFormatter));		
		assertEquals(output, expected);
	}
	
	@Test
	public void createDTManyWordsName() {
		String input = "create buy apples, oranges and starfruits by 12pm 10-10-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("buy apples, oranges and starfruits", LocalDateTime.parse("10 10 2015 12 00", DTFormatter));		
		assertEquals(output, expected);
	}
	
	@Test
	public void createBTOneWordName() {
		String input = "create lecture from 12pm 10-10-2015 to 2pm 10-10-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("lecture", LocalDateTime.parse("10 10 2015 12 00", DTFormatter), LocalDateTime.parse("10 10 2015 14 00", DTFormatter));		
		assertEquals(output, expected);
	}
	
	@Test
	public void createBTManyWordsName() {
		String input = "create buy apples, oranges and starfruits from 12pm 10-10-2015 to 2pm 10-10-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("buy apples, oranges and starfruits", LocalDateTime.parse("10 10 2015 12 00", DTFormatter), LocalDateTime.parse("10 10 2015 14 00", DTFormatter));		
		assertEquals(output, expected);
	}
	
	//===================================================================
	// CREATE WITH DIFFERENT TIME FORMATS
	//===================================================================

		@Test
		public void createValidTime1() {
			String input = "create example by 8am 10-10-2015";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("10 10 2015 08 00", DTFormatter));		
			assertEquals(output, expected);
		}
		
		@Test
		public void createValidTime2() {
			String input = "create example from 12AM 10-10-2015 to 12pm 10-10-2015";
			CreateCommand output = (CreateCommand) parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("10 10 2015 00 00", DTFormatter), LocalDateTime.parse("10 10 2015 12 00", DTFormatter));		
			assertEquals(output, expected);
		}
		
		@Test
		public void createValidTime3() {
			String input = "create example by 8PM 10-10-2015";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("10 10 2015 20 00", DTFormatter));		
			assertEquals(output, expected);
		}
		
		@Test
		public void createValidTime4() {
			String input = "create example by 12:00am 10-10-2015";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("10 10 2015 00 00", DTFormatter));		
			assertEquals(output, expected);
		}
		
		@Test
		public void createValidTime5() {
			String input = "create example from 06:30AM 10-10-2015 to 6:15pm 10-10-2015";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("10 10 2015 06 30", DTFormatter), LocalDateTime.parse("10 10 2015 18 15", DTFormatter));		
			assertEquals(output, expected);
		}
		
		@Test
		public void createValidTime6() {
			String input = "create example by 12:45PM 10-10-2015";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("10 10 2015 12 45", DTFormatter));		
			assertEquals(output, expected);
		}
		
		@Test
		public void createValidTime7() {
			String input = "create example by 00:00 10-10-2015";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("10 10 2015 00 00", DTFormatter));		
			assertEquals(output, expected);
		}

		@Test
		public void createValidTime8() {
			String input = "create example from 4:15 10-10-2015 to 12:00 10-10-2015";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("10 10 2015 04 15", DTFormatter), LocalDateTime.parse("10 10 2015 12 00", DTFormatter));		
			assertEquals(output, expected);
		}
		
		@Test
		public void createValidTime9() {
			String input = "create example by 08:30 10-10-2015";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("10 10 2015 08 30", DTFormatter));		
			assertEquals(output, expected);
		}
		
		@Test
		public void createValidTime10() {
			String input = "create example by 16:45 10-10-2015";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("10 10 2015 16 45", DTFormatter));		
			assertEquals(output, expected);
		}
		
		//===================================================================
		// CREATE WITH DIFFERENT DATE FORMATS
		//===================================================================
		
		@Test
		public void createValidDate1() {
			String input = "create example by 10am 10-10-2015";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("10 10 2015 10 00", DTFormatter));		
			assertEquals(output, expected);
		}
		
		@Test
		public void createValidDate2() {
			String input = "create example from 10am 1-10-2015 to 12pm 10-1-2015";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("01 10 2015 10 00", DTFormatter), LocalDateTime.parse("10 01 2015 12 00", DTFormatter));		
			assertEquals(output, expected);
		}
		
		@Test
		public void createValidDate3() {
			String input = "create example by 10am 1-1-2015";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("01 01 2015 10 00", DTFormatter));		
			assertEquals(output, expected);
		}
		
		@Test
		public void createValidDate4() {
			String input = "create example by 10am 1-10-15";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("01 10 2015 10 00", DTFormatter));		
			assertEquals(output, expected);
		}
		
		@Test
		public void createValidDate5() {
			String input = "create example from 10am 1-1-15 to 12pm 10-10-15";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("01 01 2015 10 00", DTFormatter), LocalDateTime.parse("10 10 2015 12 00", DTFormatter));		
			assertEquals(output, expected);
		}
		
		@Test
		public void createValidDate6() {
			String input = "create example by 10am 1-10-15";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("01 10 2015 10 00", DTFormatter));		
			assertEquals(output, expected);
		}
		
		@Test
		public void createValidDate7() {
			String input = "create example by 10am 1/10/2015";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("01 10 2015 10 00", DTFormatter));		
			assertEquals(output, expected);
		}
		
		@Test
		public void createValidDate8() {
			String input = "create example from 10am 1/1/2015 to 12pm 10/10/2015";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("01 01 2015 10 00", DTFormatter), LocalDateTime.parse("10 10 2015 12 00", DTFormatter));		
			assertEquals(output, expected);
		}
		
		@Test
		public void createValidDate9() {
			String input = "create example by 10am 10/1/2015";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("10 01 2015 10 00", DTFormatter));		
			assertEquals(output, expected);
		}
		
		@Test
		public void createValidDate10() {
			String input = "create example by 10am 10/10/15";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("10 10 2015 10 00", DTFormatter));		
			assertEquals(output, expected);
		}
		
		@Test
		public void createValidDate11() {
			String input = "create example from 10am 1/10/15 to 12pm 10/1/15";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("01 10 2015 10 00", DTFormatter), LocalDateTime.parse("10 01 2015 12 00", DTFormatter));		
			assertEquals(output, expected);
		}
		
		@Test
		public void createValidDate12() {
			String input = "create example by 10am 1/1/15";
			AbstractCommand output = parser.parseInput(input);
			CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("01 01 2015 10 00", DTFormatter));		
			assertEquals(output, expected);
		}
		
}
