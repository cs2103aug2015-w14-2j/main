package parser;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ParserTest {
	DateTimeFormatter DTFormatter = DateTimeFormatter.ofPattern("dd MM yyyy HH mm");
	Parser parser = new Parser();

	@Before
	public void setUp() throws Exception {
	}

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
}
