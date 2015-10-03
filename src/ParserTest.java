import static org.junit.Assert.*;
import java.util.ArrayList;
import org.junit.Test;

public class ParserTest {
	Parser parser = new Parser();
	
	//===================================================================
	//===================================================================
	// COMMAND = CREATE
	//===================================================================
	//===================================================================
	
	//===================================================================
	// STANDARD CREATE TESTS
	//===================================================================
	
	@Test
	public void createFloatingTask() {
		String input = "create buy groceries";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("buy groceries");
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		assertEquals(output, parser.evaluateInput(input));
	}

	@Test
	public void createDeadlineTask() {
		String input = "create assignment by 6pm 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("assignment");
		output.add("null");
		output.add("null");
		output.add("18 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createBoundedTask() {
		String input = "create attend lecture from 10am 20-09-2015 to 12pm 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("attend lecture");
		output.add("10 00");
		output.add("2015 09 20");
		output.add("12 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	//===================================================================
	// TEST SINGLE DIGIT TIME, DAY AND MONTH => PAD WITH ZERO
	//===================================================================
	
	@Test
	public void createDeadlineTaskWithSingleDigitDayMonth() {
		String input = "create assignment by 8:08am 2-9-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("assignment");
		output.add("null");
		output.add("null");
		output.add("08 08");
		output.add("2015 09 02");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createBoundedTaskWithSingleDigitTimeDayMonth() {
		String input = "create attend lecture from 7:07am 10-09-2015 to 6pm 10-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("attend lecture");
		output.add("07 07");
		output.add("2015 10 09");
		output.add("18 00");
		output.add("2015 09 10");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	//===================================================================
	// TEST WITH KEYWORDS (FROM, TO, BY) IN NAME
	//===================================================================
	
	@Test
	public void createFloatingTaskWithKeyword1() {
		String input = "create buy groceries by";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("buy groceries by");
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createFloatingTaskWithKeyword2() {
		String input = "create buy by groceries";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("buy by groceries");
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createFloatingTaskWithKeyword3() {
		String input = "create buy groceries from";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("buy groceries from");
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createFloatingTaskWithKeyword4() {
		String input = "create buy from groceries";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("buy from groceries");
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createFloatingTaskWithKeyword5() {
		String input = "create buy groceries to";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("buy groceries to");
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createFloatingTaskWithKeyword6() {
		String input = "create buy to groceries";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("buy to groceries");
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createFloatingTaskWithKeyword7() {
		String input = "create buy from groceries to fruits";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("buy from groceries to fruits");
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskWithKeyword1() {
		String input = "create assignment by by 6pm 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("assignment by");
		output.add("null");
		output.add("null");
		output.add("18 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskWithKeyword2() {
		String input = "create assignment by teacher by 6pm 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("assignment by teacher");
		output.add("null");
		output.add("null");
		output.add("18 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskWithKeyword3() {
		String input = "create assignment from by 6pm 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("assignment from");
		output.add("null");
		output.add("null");
		output.add("18 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskWithKeyword4() {
		String input = "create assignment from teacher by 6pm 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("assignment from teacher");
		output.add("null");
		output.add("null");
		output.add("18 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskWithKeyword5() {
		String input = "create assignment to by 6pm 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("assignment to");
		output.add("null");
		output.add("null");
		output.add("18 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskWithKeyword6() {
		String input = "create assignment to teacher by 6pm 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("assignment to teacher");
		output.add("null");
		output.add("null");
		output.add("18 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskWithKeyword7() {
		String input = "create from assignment to teacher by 6pm 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("from assignment to teacher");
		output.add("null");
		output.add("null");
		output.add("18 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createBoundedTaskWithKeyword1() {
		String input = "create attend lecture from from 10am 20-09-2015 to 12pm 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("attend lecture from");
		output.add("10 00");
		output.add("2015 09 20");
		output.add("12 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createBoundedTaskWithKeyword2() {
		String input = "create attend lecture from guest from 10am 20-09-2015 to 12pm 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("attend lecture from guest");
		output.add("10 00");
		output.add("2015 09 20");
		output.add("12 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createBoundedTaskWithKeyword3() {
		String input = "create attend lecture by from 10am 20-09-2015 to 12pm 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("attend lecture by");
		output.add("10 00");
		output.add("2015 09 20");
		output.add("12 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createBoundedTaskWithKeyword4() {
		String input = "create attend lecture by teacher from 10am 20-09-2015 to 12pm 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("attend lecture by teacher");
		output.add("10 00");
		output.add("2015 09 20");
		output.add("12 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createBoundedTaskWithKeyword5() {
		String input = "create attend lecture to from 10am 20-09-2015 to 12pm 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("attend lecture to");
		output.add("10 00");
		output.add("2015 09 20");
		output.add("12 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createBoundedTaskWithKeyword6() {
		String input = "create attend lecture to something from 10am 20-09-2015 to 12pm 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("attend lecture to something");
		output.add("10 00");
		output.add("2015 09 20");
		output.add("12 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createBoundedTaskWithKeyword7() {
		String input = "create attend lecture from teacher to student from 10am 20-09-2015 to 12pm 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("attend lecture from teacher to student");
		output.add("10 00");
		output.add("2015 09 20");
		output.add("12 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	//===================================================================
	// TEST WITH NO NAME
	//===================================================================
	
	@Test
	public void createEmpty() {
		String input = "create";
		ArrayList<String> output = new ArrayList<String>();
		output.add("invalid");
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskNoName() {
		String input = "create by 6pm 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("invalid");
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createBoundedTaskNoName() {
		String input = "create from 10am 20-09-2015 to 12pm 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("invalid");
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		assertEquals(output, parser.evaluateInput(input));
	}
	
	//===================================================================
	// TEST VALID TIME, DAY, MONTH AND YEAR COMPONENTS
	//===================================================================
	
	@Test
	public void createDeadlineTaskWeirdTime() {
		String input = "create something by 13pm 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("invalid");
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskWeirdDay1() {
		String input = "create something by 11pm 35-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("invalid");
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskWeirdDay2() {
		String input = "create something by 11pm -10-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("invalid");
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskWeirdDay3() {
		String input = "create something by 11pm 0-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("invalid");
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskWeirdMonth1() {
		String input = "create something by 11pm 3-13-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("invalid");
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskWeirdMonth2() {
		String input = "create something by 11pm 3-0-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("invalid");
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskWeirdYear1() {
		String input = "create something by 11pm 3-10-200";
		ArrayList<String> output = new ArrayList<String>();
		output.add("invalid");
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskWeirdYear2() {
		String input = "create something by 11pm 3-10-2116";
		ArrayList<String> output = new ArrayList<String>();
		output.add("invalid");
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskWeirdYear3() {
		String input = "create something by 11pm 3-10-1964";
		ArrayList<String> output = new ArrayList<String>();
		output.add("invalid");
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		assertEquals(output, parser.evaluateInput(input));
	}
	
	//===================================================================
	// TEST VALID DAY+MONTH COMBINATION
	//===================================================================
	
	@Test
	public void createDeadlineTaskValidDateJan() {
		String input = "create something by 11pm 31-1-2016";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("something");
		output.add(null);
		output.add(null);
		output.add("23 00");
		output.add("2016 01 31");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskValidDateFeb() {
		String input = "create something by 11pm 28-2-2016";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("something");
		output.add(null);
		output.add(null);
		output.add("23 00");
		output.add("2016 02 28");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskInvalidDateFeb1() {
		String input = "create something by 11pm 29-2-2016";
		ArrayList<String> output = new ArrayList<String>();
		output.add("invalid");
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskInvalidDateFeb2() {
		String input = "create something by 11pm 30-2-2016";
		ArrayList<String> output = new ArrayList<String>();
		output.add("invalid");
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskInvalidDateFeb3() {
		String input = "create something by 11pm 31-2-2016";
		ArrayList<String> output = new ArrayList<String>();
		output.add("invalid");
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskValidDateMar() {
		String input = "create something by 11pm 31-3-2016";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("something");
		output.add(null);
		output.add(null);
		output.add("23 00");
		output.add("2016 03 31");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskValidDateApr() {
		String input = "create something by 11pm 30-4-2016";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("something");
		output.add(null);
		output.add(null);
		output.add("23 00");
		output.add("2016 04 30");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskInvalidDateApr() {
		String input = "create something by 11pm 31-4-2016";
		ArrayList<String> output = new ArrayList<String>();
		output.add("invalid");
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskValidDateMay() {
		String input = "create something by 11pm 31-5-2016";
		ArrayList<String> output = new ArrayList<String>();
		output.add("invalid");
		output.add("create");
		output.add("something");
		output.add(null);
		output.add(null);
		output.add("23 00");
		output.add("2016 05 31");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskValidDateJun() {
		String input = "create something by 11pm 30-6-2016";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("something");
		output.add(null);
		output.add(null);
		output.add("23 00");
		output.add("2016 06 30");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskInvalidDateJun() {
		String input = "create something by 11pm 31-6-2016";
		ArrayList<String> output = new ArrayList<String>();
		output.add("invalid");
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskValidDateJul() {
		String input = "create something by 11pm 31-7-2016";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("something");
		output.add(null);
		output.add(null);
		output.add("23 00");
		output.add("2016 07 31");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskValidDateAug() {
		String input = "create something by 11pm 31-8-2016";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("something");
		output.add(null);
		output.add(null);
		output.add("23 00");
		output.add("2016 08 31");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskValidDateSep() {
		String input = "create something by 11pm 30-9-2016";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("something");
		output.add(null);
		output.add(null);
		output.add("23 00");
		output.add("2016 09 30");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskInvalidDateSep() {
		String input = "create something by 11pm 31-9-2016";
		ArrayList<String> output = new ArrayList<String>();
		output.add("invalid");
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskValidDateOct() {
		String input = "create something by 11pm 31-10-2016";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("something");
		output.add(null);
		output.add(null);
		output.add("23 00");
		output.add("2016 10 31");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskValidDateNov() {
		String input = "create something by 11pm 30-11-2016";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("something");
		output.add(null);
		output.add(null);
		output.add("23 00");
		output.add("2016 11 30");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskInvalidDateNov() {
		String input = "create something by 11pm 31-11-2016";
		ArrayList<String> output = new ArrayList<String>();
		output.add("invalid");
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		output.add(null);
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskValidDateDec() {
		String input = "create something by 11pm 31-12-2016";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("something");
		output.add(null);
		output.add(null);
		output.add("23 00");
		output.add("2016 12 31");
		assertEquals(output, parser.evaluateInput(input));
	}
}