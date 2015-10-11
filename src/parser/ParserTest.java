package parser;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class ParserTest {
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
		CreateCommand output = (CreateCommand) parser.parseInput(input);
		CreateCommand expected = new CreateCommand("buy apples, oranges and starfruits");
		assertEquals(output, expected);
	}
}
