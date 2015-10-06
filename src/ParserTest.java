import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;

public class ParserTest {
	
	private Parser parser;
	private ArrayList<String> outputForInvalid;
	
  @Before
  public void setUp(){
  	parser = new Parser();
  	outputForInvalid = new ArrayList<String>();
  	outputForInvalid.add("invalid");
  	outputForInvalid.add("");
  	outputForInvalid.add("");
  	outputForInvalid.add("");
  	outputForInvalid.add("");
  	outputForInvalid.add("");
  }
  
	//*******************************************************************
	//*******************************************************************
	// COMMAND = CREATE
	//*******************************************************************
	//*******************************************************************
	
	//===================================================================
	// STANDARD CREATE TESTS
	//===================================================================
/*	
  @Test
	public void createFloatingTask() {
		String input = "create buy groceries";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("buy groceries");
		output.add("");
		output.add("");
		output.add("");
		output.add("");
		assertEquals(output, parser.evaluateInput(input));
	}
  */
	@Test
	public void createDeadlineTask() {
		String input = "create complete tutorial by 18:00 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("complete tutorial");
		output.add("");
		output.add("");
		output.add("18 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createBoundedTask() {
		String input = "create attend lecture from 10:00 20-09-2015 to 12:00 20-09-2015";
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
	// RANDOM CREATE TESTS
	//===================================================================
	
	//Decide what happens!
	@Test
	public void createFloatingTaskExtra() {
		//i think this is still a valid floating event. there's no way we can argue that this doesn't make sense
		//to the user. who knows the users is typing exactly what he/she meant?
		String input = "create 1 2 3 4 5 6 7 8 9 10";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("1 2 3 4 5 6 7 8 9 10");
		output.add("");
		output.add("");
		output.add("");
		output.add("");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskExtra() {
		String input = "create something by 18:00 1-1-15 something";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	@Test
	public void createBoundedTaskExtra() {
		String input = "create something from 18:00 1-1-15 to 18:00 2-1-15 something";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	
	//===================================================================
	// TEST TIME FORMAT
	//===================================================================
	
	@Test
	public void createDeadlineTaskCheckTimeFormat1() {
		String input = "create complete tutorial by 6:30pm 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("complete tutorial");
		output.add("");
		output.add("");
		output.add("18 30");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskCheckTimeFormat2() {
		String input = "create complete tutorial by 6:30PM 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("complete tutorial");
		output.add("");
		output.add("");
		output.add("18 30");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskCheckTimeFormat3() {
		String input = "create complete tutorial by 6:30am 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("complete tutorial");
		output.add("");
		output.add("");
		output.add("06 30");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskCheckTimeFormat4() {
		String input = "create complete tutorial by 6:30AM 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("complete tutorial");
		output.add("");
		output.add("");
		output.add("06 30");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	//--------------------------------------------------------------------------
	/*@Test
	public void createDeadlineTaskCheckTimeFormat5() {
		String input = "create complete tutorial by 6:30mm 20-09-2015";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskCheckTimeFormat6() {
		String input = "create complete tutorial by 13:30pm 20-09-2015";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}*/
	//---------------------------------------------------------------------------
	@Test
	public void createDeadlineTaskCheckTimeFormat7() {
		String input = "create complete tutorial by 8:00 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("complete tutorial");
		output.add("");
		output.add("");
		output.add("08 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskCheckTimeFormat8() {
		String input = "create complete tutorial by 08:00 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("complete tutorial");
		output.add("");
		output.add("");
		output.add("08 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskCheckTimeFormat9() {
		String input = "create complete tutorial by 00:00 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("complete tutorial");
		output.add("");
		output.add("");
		output.add("00 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskCheckTimeFormat10() {
		String input = "create complete tutorial by 0:00 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("complete tutorial");
		output.add("");
		output.add("");
		output.add("00 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskCheckTimeFormat11() {
		String input = "create complete tutorial by 24:00 20-09-2015";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	@Test
	public void createBoundedTaskCheckTimeFormat() {
		String input = "create attend lecture from 10:00 20-09-2015 to 12:00pm 20-09-2015";
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
	// TEST DATE FORMAT
	//===================================================================
	
	@Test
	public void createDeadlineTaskDateSlash() {
		String input = "create complete tutorial by 18:00 20/09/2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("complete tutorial");
		output.add("");
		output.add("");
		output.add("18 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	public void createDeadlineTaskDateSlashAndDash1() {
		String input = "create complete tutorial by 18:00 20/09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("complete tutorial");
		output.add("");
		output.add("");
		output.add("18 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	public void createDeadlineTaskDateSlashAndDash2() {
		String input = "create complete tutorial by 18:00 20-09/2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("complete tutorial");
		output.add("");
		output.add("");
		output.add("18 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskWeirdDay1() {
		String input = "create something by 10:00 32-09-2015";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskWeirdDay2() {
		String input = "create something by 10:00 -10-09-2015";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskWeirdDay3() {
		String input = "create something by 10:00 0-09-2015";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskWeirdMonth1() {
		String input = "create something by 10:00 3-13-2015";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskWeirdMonth2() {
		String input = "create something by 10:00 3--10-2015";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskWeirdMonth3() {
		String input = "create something by 10:00 3-0-2015";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskWeirdYear1() {
		String input = "create something by 10:00 3-10-100";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskWeirdYear2() {
		String input = "create something by 10:00 3-10-2116";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskWeirdYear3() {
		String input = "create something by 10:00 3-10-1964";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	@Test
	public void createBoundedTaskDateFormat1() {
		String input = "create something from 10:00 9-09-2015 to 12:00 09-9-15";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("something");
		output.add("10 00");
		output.add("2015 09 09");
		output.add("12 00");
		output.add("2015 09 09");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createBoundedTaskDateFormat2() {
		String input = "create something from 10:00 9-9-15 to 12:00 09-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("something");
		output.add("10 00");
		output.add("2015 09 09");
		output.add("12 00");
		output.add("2015 09 09");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	//===================================================================
	// TEST VALID DAY+MONTH COMBINATION
	//===================================================================
	
	@Test
	public void createDeadlineTaskValidDateJan() {
		String input = "create something by 10:00 31-1-2016";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("something");
		output.add("");
		output.add("");
		output.add("10 00");
		output.add("2016 01 31");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskValidDateFeb() {
		String input = "create something by 10:00 28-2-2016";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("something");
		output.add("");
		output.add("");
		output.add("10 00");
		output.add("2016 02 28");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskInvalidDateFeb1() {
		String input = "create something by 10:00 29-2-2016";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskInvalidDateFeb2() {
		String input = "create something by 10:00 30-2-2016";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskInvalidDateFeb3() {
		String input = "create something by 10:00 31-2-2016";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskValidDateMar() {
		String input = "create something by 10:00 31-3-2016";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("something");
		output.add("");
		output.add("");
		output.add("10 00");
		output.add("2016 03 31");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskValidDateApr() {
		String input = "create something by 10:00 30-4-2016";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("something");
		output.add("");
		output.add("");
		output.add("10 00");
		output.add("2016 04 30");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskInvalidDateApr() {
		String input = "create something by 10:00 31-4-2016";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskValidDateMay() {
		String input = "create something by 10:00 31-5-2016";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("something");
		output.add("");
		output.add("");
		output.add("10 00");
		output.add("2016 05 31");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskValidDateJun() {
		String input = "create something by 10:00 30-6-2016";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("something");
		output.add("");
		output.add("");
		output.add("10 00");
		output.add("2016 06 30");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskInvalidDateJun() {
		String input = "create something by 10:00 31-6-2016";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskValidDateJul() {
		String input = "create something by 10:00 31-7-2016";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("something");
		output.add("");
		output.add("");
		output.add("10 00");
		output.add("2016 07 31");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskValidDateAug() {
		String input = "create something by 10:00 31-8-2016";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("something");
		output.add("");
		output.add("");
		output.add("10 00");
		output.add("2016 08 31");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskValidDateSep() {
		String input = "create something by 10:00 30-9-2016";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("something");
		output.add("");
		output.add("");
		output.add("10 00");
		output.add("2016 09 30");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskInvalidDateSep() {
		String input = "create something by 10:00 31-9-2016";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskValidDateOct() {
		String input = "create something by 10:00 31-10-2016";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("something");
		output.add("");
		output.add("");
		output.add("10 00");
		output.add("2016 10 31");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskValidDateNov() {
		String input = "create something by 10:00 30-11-2016";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("something");
		output.add("");
		output.add("");
		output.add("10 00");
		output.add("2016 11 30");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskInvalidDateNov() {
		String input = "create something by 10:00 31-11-2016";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskValidDateDec() {
		String input = "create something by 10:00 31-12-2016";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("something");
		output.add("");
		output.add("");
		output.add("10 00");
		output.add("2016 12 31");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	//===================================================================
	// TEST WITH NO NAME
	//===================================================================
	
	@Test
	public void createEmpty() {
		String input = "create";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskNoName() {
		String input = "create by 18:00 20-09-2015";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	@Test
	public void createBoundedTaskNoName() {
		String input = "create from 10:00 20-09-2015 to 12:00 20-09-2015";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
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
		output.add("");
		output.add("");
		output.add("");
		output.add("");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createFloatingTaskWithKeyword2() {
		String input = "create buy by groceries";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("buy by groceries");
		output.add("");
		output.add("");
		output.add("");
		output.add("");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createFloatingTaskWithKeyword3() {
		String input = "create buy groceries from";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("buy groceries from");
		output.add("");
		output.add("");
		output.add("");
		output.add("");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createFloatingTaskWithKeyword4() {
		String input = "create buy from groceries";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("buy from groceries");
		output.add("");
		output.add("");
		output.add("");
		output.add("");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createFloatingTaskWithKeyword5() {
		String input = "create buy groceries to";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("buy groceries to");
		output.add("");
		output.add("");
		output.add("");
		output.add("");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createFloatingTaskWithKeyword6() {
		String input = "create buy to groceries";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("buy to groceries");
		output.add("");
		output.add("");
		output.add("");
		output.add("");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createFloatingTaskWithKeyword7() {
		String input = "create buy from groceries to fruits";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("buy from groceries to fruits");
		output.add("");
		output.add("");
		output.add("");
		output.add("");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskWithKeyword1() {
		String input = "create assignment by by 18:00 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("assignment by");
		output.add("");
		output.add("");
		output.add("18 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskWithKeyword2() {
		String input = "create assignment by teacher by 18:00 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("assignment by teacher");
		output.add("");
		output.add("");
		output.add("18 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskWithKeyword3() {
		String input = "create assignment from by 18:00 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("assignment from");
		output.add("");
		output.add("");
		output.add("18 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskWithKeyword4() {
		String input = "create assignment from teacher by 18:00 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("assignment from teacher");
		output.add("");
		output.add("");
		output.add("18 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskWithKeyword5() {
		String input = "create assignment to by 18:00 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("assignment to");
		output.add("");
		output.add("");
		output.add("18 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskWithKeyword6() {
		String input = "create assignment to teacher by 18:00 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("assignment to teacher");
		output.add("");
		output.add("");
		output.add("18 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createDeadlineTaskWithKeyword7() {
		String input = "create from assignment to teacher by 18:00 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("from assignment to teacher");
		output.add("");
		output.add("");
		output.add("18 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createBoundedTaskWithKeyword1() {
		String input = "create attend lecture by from 10:00 20-09-2015 to 12:00 20-09-2015";
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
	public void createBoundedTaskWithKeyword2() {
		String input = "create attend lecture by teacher from 10:00 20-09-2015 to 12:00 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("attend lecture by teacher");
		output.add("10 00");
		output.add("20 09 2015");
		output.add("12 00");
		output.add("20 09 2015");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createBoundedTaskWithKeyword3() {
		String input = "create attend lecture from from 10:00 20-09-2015 to 12:00 20-09-2015";
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
	public void createBoundedTaskWithKeyword4() {
		String input = "create attend lecture from guest from 10:00 20-09-2015 to 12:00 20-09-2015";
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
	public void createBoundedTaskWithKeyword5() {
		String input = "create attend lecture to from 10:00 20-09-2015 to 12:00 20-09-2015";
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
		String input = "create attend lecture to sing from 10:00 20-09-2015 to 12:00 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("attend lecture to sing");
		output.add("10 00");
		output.add("2015 09 20");
		output.add("12 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void createBoundedTaskWithKeyword7() {
		String input = "create attend lecture from teacher to student from 10:00 20-09-2015 to 12:00 20-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("create");
		output.add("attend lecture from teacher to student");
		output.add("10 00");
		output.add("2015 09 20");
		output.add("12 00");
		output.add("2015 09 20");
		assertEquals(output, parser.evaluateInput(input));
	}

	//*******************************************************************
	//*******************************************************************
	// COMMAND = DISPLAY
	//*******************************************************************
	//*******************************************************************
	
	//===================================================================
	// STANDARD DISPLAY ALL TESTS
	//===================================================================
	
	@Test
	public void displayAll() {
		String input = "display";
		ArrayList<String> output = new ArrayList<String>();
		output.add("display");
		output.add("");
		output.add("");
		output.add("");
		output.add("");
		output.add("");
		assertEquals(output, parser.evaluateInput(input));
	}

	//*******************************************************************
	//*******************************************************************
	// COMMAND = DELETE
	//*******************************************************************
	//*******************************************************************
	
	//===================================================================
	// STANDARD DELETE INDEX TESTS
	//===================================================================
	
	@Test
	public void deleteIndex() {
		String input = "delete #1";
		ArrayList<String> output = new ArrayList<String>();
		output.add("delete");
		output.add("#1");
		output.add("");
		output.add("");
		output.add("");
		output.add("");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void deleteIndexZero() {
		String input = "delete #0";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	@Test
	public void deleteIndexNegInt() {
		String input = "delete #-1";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	/* Decide what happens!
	@Test
	public void deleteIndexExtra() {
		String input = "delete #1 something random here and there";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	
	//*******************************************************************
	//*******************************************************************
	// COMMAND = EDIT
	//*******************************************************************
	//*******************************************************************
	
	//===================================================================
	// STANDARD EDIT INDEX TESTS
	//===================================================================
	
	@Test
	public void editNameIndex() {
		String input = "edit-name #1 family outing";
		ArrayList<String> output = new ArrayList<String>();
		output.add("edit");
		output.add("name");
		output.add("#1");
		output.add("family outing");
		output.add("");
		output.add("");
		assertEquals(output, parser.evaluateInput(input));
	}

	@Test
	public void editNameIndexZero() {
		String input = "edit-name #0 random";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	@Test
	public void editNameIndexNegInt() {
		String input = "edit-name #-1 random";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	@Test
	public void editStartIndex() {
		String input = "edit-start #5 18:00 9-9-15";
		ArrayList<String> output = new ArrayList<String>();
		output.add("edit");
		output.add("start");
		output.add("#5");
		output.add("18 00");
		output.add("2015 09 09");
		output.add("");
		assertEquals(output, parser.evaluateInput(input));
	}
	
	@Test
	public void editStartIndexInvalid1() {
		String input = "edit-start #1 something 18:00 9-9-15";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	@Test
	public void editStartIndexInvalid2() {
		String input = "edit-start #1 18:00 something 9-9-15";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	/* Decide what happens!
	@Test
	public void editStartIndexExtra() {
		String input = "edit-start #1 18:00 9-9-15 something random";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	
	@Test
	public void editStartIndexInvalidTime1() {
		String input = "edit-start #1 24:00 9-9-15";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	@Test
	public void editStartIndexInvalidTime2() {
		String input = "edit-start #1 time 9-9-15";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	@Test
	public void editStartIndexInvalidDate1() {
		String input = "edit-start #1 18:00 9-9-3000";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	@Test
	public void editStartIndexInvalidDate2() {
		String input = "edit-start #1 18:00 day";
		assertEquals(outputForInvalid, parser.evaluateInput(input));
	}
	
	@Test
	public void editEndIndex() {
		String input = "edit-end #10 18:00 09-09-2015";
		ArrayList<String> output = new ArrayList<String>();
		output.add("edit");
		output.add("end");
		output.add("#10");
		output.add("18 00");
		output.add("2015 09 09");
		output.add("");
		assertEquals(output, parser.evaluateInput(input));
	}
	*/
}