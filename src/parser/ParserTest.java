package parser;

import static org.junit.Assert.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.junit.Test;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ParserTest {

	DateTimeFormatter DTFormatter = DateTimeFormatter.ofPattern("dd MM yyyy HH mm");
	Parser parser = new Parser();
	InvalidCommand expectedInvalid = new InvalidCommand();

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
		String input = "c buy apples, oranges and starfruits by 10-10-2015 12pm";
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
		String input = "create lecture from 10-10-2015 12pm to 2pm 10-10-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("lecture", LocalDateTime.parse("10 10 2015 12 00", DTFormatter), LocalDateTime.parse("10 10 2015 14 00", DTFormatter));		
		assertEquals(expected, output);
	}

	@Test
	public void cBTManyWordsName() {
		String input = "c buy apples, oranges and starfruits from 12pm 10-10-2015 to 10-10-2015 2pm";
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
		AbstractCommand output = parser.parseInput(input);
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

	//*****//
	
	@Test
	public void createInvalidTime1() {
		String input = "create example by 25:00 10-10-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("example by 25:00 10-10-2015");
		assertEquals(expected, output);
	}
	
	@Test
	public void createInvalidTime2() {
		String input = "create example by 25pm 10-10-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("example by 25pm 10-10-2015");
		assertEquals(expected, output);
	}
	
	@Test
	public void createInvalidTime3() {
		String input = "create example by 0am 10-10-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("example by 0am 10-10-2015");
		assertEquals(expected, output);
	}
	
	//*****//

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
		String input = "create example from 1-10-2015 10am to 10-1-2015 12pm";
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
		String input = "create example by 10am 1-10";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("01 10 " + (new DateTime()).getYear() + " 10 00", DTFormatter));		
		assertEquals(expected, output);
	}

	@Test
	public void createValidDate5() {
		String input = "create example from 10am 1-1 to 12pm 10-10";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("01 01 " + (new DateTime()).getYear() + " 10 00", DTFormatter), LocalDateTime.parse("10 10 " + (new DateTime()).getYear() + " 12 00", DTFormatter));		
		assertEquals(expected, output);
	}

	@Test
	public void createValidDate6() {
		String input = "create example by 10am 1-10";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("01 10 " + (new DateTime()).getYear() + " 10 00", DTFormatter));		
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
		String input = "create example by 10am 10/10";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("10 10 " + (new DateTime()).getYear() + " 10 00", DTFormatter));		
		assertEquals(expected, output);
	}

	@Test
	public void createValidDate11() {
		String input = "create example from 10am 1/10 to 12pm 10/1";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("01 10 " + (new DateTime()).getYear() + " 10 00", DTFormatter), LocalDateTime.parse("10 01 " + (new DateTime()).getYear() + " 12 00", DTFormatter));		
		assertEquals(expected, output);
	}

	@Test
	public void createValidDate12() {
		String input = "create example by 10am 1/1";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("01 01 " + (new DateTime()).getYear() + " 10 00", DTFormatter));		
		assertEquals(expected, output);
	}

	//===================================================================
	// CREATE WITH NATURAL LANGUAGE DATE FORMATS
	//===================================================================

	@Test
	public void createDTWithTmr() {
		String input = "create watch dancing with the stars by 4pm TMR";
		AbstractCommand output = parser.parseInput(input);
		DateTime dt = new DateTime().plusDays(1);
		CreateCommand expected = new CreateCommand("watch dancing with the stars", LocalDateTime.parse(dt.getDayOfMonth() + " " + dt.getMonthOfYear() + " " + dt.getYear() + " " + "16 00", DTFormatter));
		assertEquals(expected, output);
	}

	@Test
	public void createDTWithYesterday() {
		String input = "create should have been done by yesterday 12pm";
		AbstractCommand output = parser.parseInput(input);
		DateTime dt = new DateTime().minusDays(1);
		CreateCommand expected = new CreateCommand("should have been done", LocalDateTime.parse(dt.getDayOfMonth() + " " + dt.getMonthOfYear() + " " + dt.getYear() + " " + "12 00", DTFormatter));
		assertEquals(expected, output);
	}

	@Test
	public void createBTWithYtdAndTomorrow() {
		String input = "create a special event from ytd 1pm to 6:30pm tomorrow";
		AbstractCommand output = parser.parseInput(input);
		DateTime dty = new DateTime().minusDays(1);
		DateTime dtt = new DateTime().plusDays(1);
		CreateCommand expected = new CreateCommand("a special event", 
				LocalDateTime.parse(dty.getDayOfMonth() + " " + dty.getMonthOfYear() + " " + dty.getYear() + " " + "13 00", DTFormatter),
				LocalDateTime.parse(dtt.getDayOfMonth() + " " + dtt.getMonthOfYear() + " " + dtt.getYear() + " " + "18 30", DTFormatter));
		assertEquals(expected, output);
	}

	@Test
	public void createBTWithYesterdayAndTmr() {
		String input = "create staycation from 10pm YESTERDAY to tmr 11pm";
		AbstractCommand output = parser.parseInput(input);
		DateTime dty = new DateTime().minusDays(1);
		DateTime dtt = new DateTime().plusDays(1);
		CreateCommand expected = new CreateCommand("staycation", 
				LocalDateTime.parse(dty.getDayOfMonth() + " " + dty.getMonthOfYear() + " " + dty.getYear() + " " + "22 00", DTFormatter),
				LocalDateTime.parse(dtt.getDayOfMonth() + " " + dtt.getMonthOfYear() + " " + dtt.getYear() + " " + "23 00", DTFormatter));
		assertEquals(expected, output);
	}

	@Test
	public void createDTWithNextMonday() {
		String input = "create hand in annual report by 2pm next Monday";
		AbstractCommand output = parser.parseInput(input);
		DateTime dt = new DateTime().withDayOfWeek(DateTimeConstants.MONDAY).plusWeeks(1);
		CreateCommand expected = new CreateCommand("hand in annual report", LocalDateTime.parse(dt.getDayOfMonth() + " " + dt.getMonthOfYear() + " " + dt.getYear() + " " + "14 00", DTFormatter));
		assertEquals(expected, output);
	}

	@Test
	public void createDTWithThisFri() {
		String input = "create eat steak by this Fri 15:30";
		AbstractCommand output = parser.parseInput(input);
		DateTime dt = new DateTime().withDayOfWeek(DateTimeConstants.MONDAY).plusDays(4);
		CreateCommand expected = new CreateCommand("eat steak", LocalDateTime.parse(dt.getDayOfMonth() + " " + dt.getMonthOfYear() + " " + dt.getYear() + " " + "15 30", DTFormatter));
		assertEquals(expected, output);
	}

	@Test
	public void createDTWithLastSaturday() {
		String input = "create call mom by 7:07 last Saturday";
		AbstractCommand output = parser.parseInput(input);
		DateTime dt = new DateTime().withDayOfWeek(DateTimeConstants.MONDAY).minusWeeks(1).plusDays(5);
		CreateCommand expected = new CreateCommand("call mom", LocalDateTime.parse(dt.getDayOfMonth() + " " + dt.getMonthOfYear() + " " + dt.getYear() + " " + "07 07", DTFormatter));
		assertEquals(expected, output);
	}

	@Test
	public void createBTWithLastSunAndNextThursday() {
		String input = "create hibernate like a polar bear from 08:18 last sun to next Thursday 12:16am";
		AbstractCommand output = parser.parseInput(input);
		DateTime dts = new DateTime().withDayOfWeek(DateTimeConstants.MONDAY).minusWeeks(1).plusDays(6);
		DateTime dte = new DateTime().withDayOfWeek(DateTimeConstants.MONDAY).plusWeeks(1).plusDays(3);
		CreateCommand expected = new CreateCommand("hibernate like a polar bear", 
				LocalDateTime.parse(dts.getDayOfMonth() + " " + dts.getMonthOfYear() + " " + dts.getYear() + " " + "08 18", DTFormatter),
				LocalDateTime.parse(dte.getDayOfMonth() + " " + dte.getMonthOfYear() + " " + dte.getYear() + " " + "00 16", DTFormatter));
		assertEquals(expected, output);
	}

	@Test
	public void createBTWithLastSundayAndThisWed() {
		String input = "create meditate from last sunday 7pm to 6:06 THIS WED";
		AbstractCommand output = parser.parseInput(input);
		DateTime dts = new DateTime().withDayOfWeek(DateTimeConstants.MONDAY).minusWeeks(1).plusDays(6);
		DateTime dte = new DateTime().withDayOfWeek(DateTimeConstants.MONDAY).plusDays(2);
		CreateCommand expected = new CreateCommand("meditate", 
				LocalDateTime.parse(dts.getDayOfMonth() + " " + dts.getMonthOfYear() + " " + dts.getYear() + " " + "19 00", DTFormatter),
				LocalDateTime.parse(dte.getDayOfMonth() + " " + dte.getMonthOfYear() + " " + dte.getYear() + " " + "06 06", DTFormatter));
		assertEquals(expected, output);
	}

	@Test
	public void createBTWithTodayAndNextTues() {
		String input = "create renovate house from ToDaY 4:19pm to 09:28am next tUeS";
		AbstractCommand output = parser.parseInput(input);
		DateTime dts = new DateTime();
		DateTime dte = new DateTime().withDayOfWeek(DateTimeConstants.MONDAY).plusWeeks(1).plusDays(1);
		CreateCommand expected = new CreateCommand("renovate house", 
				LocalDateTime.parse(dts.getDayOfMonth() + " " + dts.getMonthOfYear() + " " + dts.getYear() + " " + "16 19", DTFormatter),
				LocalDateTime.parse(dte.getDayOfMonth() + " " + dte.getMonthOfYear() + " " + dte.getYear() + " " + "09 28", DTFormatter));
		assertEquals(expected, output);
	}

	//===================================================================
	// CREATE WITH INVALID DATES
	//===================================================================
	
	//*****//

	@Test
	public void createDeadlineTaskWeirdDay1() {
		String input = "create something by 10:00 32-09-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("something by 10:00 32-09-2015");
		assertEquals(expected, output);
	}
	
	@Test
	public void createDeadlineTaskWeirdDay2() {
		String input = "create something by 10:00 -10-09-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("something by 10:00 -10-09-2015");
		assertEquals(expected, output);
	}

	
	@Test
	public void createDeadlineTaskWeirdDay3() {
		String input = "create something by 10:00 0-09-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("something by 10:00 0-09-2015");
		assertEquals(expected, output);
	}
	
	@Test
	public void createDeadlineTaskWeirdMonth1() {
		String input = "create something by 10:00 3-13-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("something by 10:00 3-13-2015");
		assertEquals(expected, output);
	}
	
	@Test
	public void createDeadlineTaskWeirdMonth2() {
		String input = "create something by 10:00 3--10-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("something by 10:00 3--10-2015");
		assertEquals(expected, output);
	}
	
	@Test
	public void createDeadlineTaskWeirdMonth3() {
		String input = "create something by 10:00 3-0-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("something by 10:00 3-0-2015");
		assertEquals(expected, output);
	}
		
	@Test
	public void createDeadlineTaskWeirdYear() {
		String input = "create something by 10:00 3-10-100";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("something by 10:00 3-10-100");
		assertEquals(expected, output);
	}

	//*****//

	//===================================================================
	// CREATE WITH DAY+MONTH COMBINATIONS
	//===================================================================
	
	@Test
	public void createDeadlineTaskValidDateJan() {
		String input = "create something by 10:00 31-1-2016";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("something", LocalDateTime.parse("31 01 2016 10 00", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createDeadlineTaskValidDateFeb1() {
		String input = "create something by 10:17 28-2-2016";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("something", LocalDateTime.parse("28 02 2016 10 17", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createDeadlineTaskValidDateFeb2() {
		String input = "create something by 10:17am 29-2-2016";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("something", LocalDateTime.parse("29 02 2016 10 17", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createDeadlineTaskValidDateMar() {
		String input = "create something by 16:59 31-3-2016";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("something", LocalDateTime.parse("31 03 2016 16 59", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createDeadlineTaskValidDateApr() {
		String input = "create something by 4:59pm 30-4-2016";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("something", LocalDateTime.parse("30 04 2016 16 59", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createDeadlineTaskValidDateMay() {
		String input = "create something by 12am 31-5-2016";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("something", LocalDateTime.parse("31 05 2016 00 00", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createDeadlineTaskValidDateJun() {
		String input = "create something by 11:00 30-6-2016";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("something", LocalDateTime.parse("30 06 2016 11 00", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createDeadlineTaskValidDateJul() {
		String input = "create something by 5:05am 31-7-2016";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("something", LocalDateTime.parse("31 07 2016 05 05", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createDeadlineTaskValidDateAug() {
		String input = "create something by 4:09pm 31-8-2016";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("something", LocalDateTime.parse("31 08 2016 16 09", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createDeadlineTaskValidDateSep() {
		String input = "create something by 1:37aM 30-9-2016";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("something", LocalDateTime.parse("30 09 2016 01 37", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createDeadlineTaskValidDateOct() {
		String input = "create something by 01:37AM 31-10-2016";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("something", LocalDateTime.parse("31 10 2016 01 37", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createDeadlineTaskValidDateNov() {
		String input = "create something by 3:10Pm 30-11-2016";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("something", LocalDateTime.parse("30 11 2016 15 10", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createDeadlineTaskValidDateDec() {
		String input = "create something by 03:10pm 31-12-2016";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("something", LocalDateTime.parse("31 12 2016 15 10", DTFormatter));
		assertEquals(expected, output);
	}
	
	//*****//
	
	@Test
	public void createDeadlineTaskInvalidDateFeb() {
		String input = "create something by 10:00 30-2-2016";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("something by 10:00 30-2-2016");
		assertEquals(expected, output);
	}
	
	@Test
	public void createDeadlineTaskInvalidDateFeb3() {
		String input = "create something by 10:00 31-2-2016";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("something by 10:00 31-2-2016");
		assertEquals(expected, output);
	}
	
	@Test
	public void createDeadlineTaskInvalidDateApr() {
		String input = "create something by 10:00 31-4-2016";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("something by 10:00 31-4-2016");
		assertEquals(expected, output);
	}
	
	@Test
	public void createDeadlineTaskInvalidDateJun() {
		String input = "create something by 10:00 31-6-2016";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("something by 10:00 31-6-2016");
		assertEquals(expected, output);
	}
	
	@Test
	public void createDeadlineTaskInvalidDateSep() {
		String input = "create something by 10:00 31-09-2016";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("something by 10:00 31-09-2016");
		assertEquals(expected, output);
	}
	
	@Test
	public void createDeadlineTaskInvalidDateNov() {
		String input = "create something by 10:00 31-11-2016";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("something by 10:00 31-11-2016");
		assertEquals(expected, output);
	}
	
	//*****//

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
		String input = "create test by test test by 10am 05-05";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("test by test test", LocalDateTime.parse("05 05 " + (new DateTime()).getYear() + " 10 00", DTFormatter));
		assertEquals(expected, output);
	}

	@Test
	public void createDTWithKeyword2() {
		String input = "create test test by test by 10am 5-5";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("test test by test", LocalDateTime.parse("05 05 " + (new DateTime()).getYear() + " 10 00", DTFormatter));
		assertEquals(expected, output);
	}

	@Test
	public void createDTWithKeyword3() {
		String input = "create test test test by by 10am 5-05";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("test test test by", LocalDateTime.parse("05 05 " + (new DateTime()).getYear() + " 10 00", DTFormatter));
		assertEquals(expected, output);
	}

	@Test
	public void createDTWithKeyword4() {
		String input = "create test from test test by 10am 05-5";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("test from test test", LocalDateTime.parse("05 05 " + (new DateTime()).getYear() + " 10 00", DTFormatter));
		assertEquals(expected, output);
	}

	@Test
	public void createDTWithKeyword5() {
		String input = "create test test from test by 10am 25-09-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("test test from test", LocalDateTime.parse("25 09 2015 10 00", DTFormatter));
		assertEquals(expected, output);
	}

	@Test
	public void createDTWithKeyword6() {
		String input = "create test test test from by 10am 26-9-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("test test test from", LocalDateTime.parse("26 09 2015 10 00", DTFormatter));
		assertEquals(expected, output);
	}

	@Test
	public void createDTWithKeyword7() {
		String input = "create test to test test by 10am 2-2-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("test to test test", LocalDateTime.parse("02 02 2015 10 00", DTFormatter));
		assertEquals(expected, output);
	}

	@Test
	public void createDTWithKeyword8() {
		String input = "create test test to test by 10am 16-3";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("test test to test", LocalDateTime.parse("16 03 2015 10 00", DTFormatter));
		assertEquals(expected, output);
	}

	@Test
	public void createDTWithKeyword9() {
		String input = "create test test test to by 10am 6-07";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("test test test to", LocalDateTime.parse("06 07 2015 10 00", DTFormatter));
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
	public void editByIndex() {
		String input = "edit #5";
		AbstractCommand output = parser.parseInput(input);

		EditCommand expected = new EditCommand("5");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		expected.setEditFields(editType);

		assertEquals(expected, output);
	}

	@Test
	public void editByIndexNSTSDETED() {
		String input = "edit #2 to sad start 3pm 20-10-2015 end 5pm 20-10-2015";
		AbstractCommand output = parser.parseInput(input);

		EditCommand expected = new EditCommand("2");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.NAME);
		expected.setNewName("sad");
		editType.add(EditCommand.editField.START_TIME);
		expected.setNewStartTime("15 00");
		editType.add(EditCommand.editField.START_DATE);
		expected.setNewStartDate("20 10 2015");
		editType.add(EditCommand.editField.END_TIME);
		expected.setNewEndTime("17 00");
		editType.add(EditCommand.editField.END_DATE);
		expected.setNewEndDate("20 10 2015");
		expected.setEditFields(editType);

		assertEquals(expected, output);
	}

	@Test
	public void editBySearch() {
		String input = "edit happy";
		AbstractCommand output = parser.parseInput(input);

		EditCommand expected = new EditCommand("happy");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		expected.setEditFields(editType);

		assertEquals(expected, output);
	}

	@Test
	public void editBySearchNSTSDETED() {
		String input = "edit happy to sad start 3pm 20-10-2015 end 5pm 20-10-2015";
		AbstractCommand output = parser.parseInput(input);

		EditCommand expected = new EditCommand("happy");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.NAME);
		expected.setNewName("sad");
		editType.add(EditCommand.editField.START_TIME);
		expected.setNewStartTime("15 00");
		editType.add(EditCommand.editField.START_DATE);
		expected.setNewStartDate("20 10 2015");
		editType.add(EditCommand.editField.END_TIME);
		expected.setNewEndTime("17 00");
		editType.add(EditCommand.editField.END_DATE);
		expected.setNewEndDate("20 10 2015");
		expected.setEditFields(editType);

		assertEquals(expected, output);
	}

	@Test
	public void eBySearchNSTSDETED() {
		String input = "e happy to sad start 3pm 20-10-2015 end 5pm 20-10-2015";
		AbstractCommand output = parser.parseInput(input);

		EditCommand expected = new EditCommand("happy");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.NAME);
		expected.setNewName("sad");
		editType.add(EditCommand.editField.START_TIME);
		expected.setNewStartTime("15 00");
		editType.add(EditCommand.editField.START_DATE);
		expected.setNewStartDate("20 10 2015");
		editType.add(EditCommand.editField.END_TIME);
		expected.setNewEndTime("17 00");
		editType.add(EditCommand.editField.END_DATE);
		expected.setNewEndDate("20 10 2015");
		expected.setEditFields(editType);

		assertEquals(expected, output);
	}

	@Test
	public void editBySearchNSDSTEDET() {
		String input = "edit happy to sad start 20-10-2015 3pm end 20-10-2015 5pm";
		AbstractCommand output = parser.parseInput(input);

		EditCommand expected = new EditCommand("happy");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.NAME);
		expected.setNewName("sad");
		editType.add(EditCommand.editField.START_TIME);
		expected.setNewStartTime("15 00");
		editType.add(EditCommand.editField.START_DATE);
		expected.setNewStartDate("20 10 2015");
		editType.add(EditCommand.editField.END_TIME);
		expected.setNewEndTime("17 00");
		editType.add(EditCommand.editField.END_DATE);
		expected.setNewEndDate("20 10 2015");
		expected.setEditFields(editType);

		assertEquals(expected, output);
	}

	@Test
	public void editBySearchNSTSD() {
		String input = "edit happy to sad start 3pm 20-10-2015";
		AbstractCommand output = parser.parseInput(input);

		EditCommand expected = new EditCommand("happy");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.NAME);
		expected.setNewName("sad");
		editType.add(EditCommand.editField.START_TIME);
		expected.setNewStartTime("15 00");
		editType.add(EditCommand.editField.START_DATE);
		expected.setNewStartDate("20 10 2015");
		expected.setEditFields(editType);

		assertEquals(expected, output);
	}

	@Test
	public void editBySearchNEDET() {
		String input = "edit happy to sad end 20-10-2015 3pm";
		AbstractCommand output = parser.parseInput(input);

		EditCommand expected = new EditCommand("happy");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.NAME);
		expected.setNewName("sad");
		editType.add(EditCommand.editField.END_TIME);
		expected.setNewEndTime("15 00");
		editType.add(EditCommand.editField.END_DATE);
		expected.setNewEndDate("20 10 2015");
		expected.setEditFields(editType);

		assertEquals(expected, output);
	}

	@Test
	public void editBySearchSTSDETED() {
		String input = "edit happy start 1pm 20-10-2015 end 3pm 20-10-2015";
		AbstractCommand output = parser.parseInput(input);

		String input2 = "edit happy to start 1pm 20-10-2015 end 3pm 20-10-2015";
		AbstractCommand output2 = parser.parseInput(input2);

		EditCommand expected = new EditCommand("happy");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.START_TIME);
		expected.setNewStartTime("13 00");
		editType.add(EditCommand.editField.START_DATE);
		expected.setNewStartDate("20 10 2015");
		editType.add(EditCommand.editField.END_TIME);
		expected.setNewEndTime("15 00");
		editType.add(EditCommand.editField.END_DATE);
		expected.setNewEndDate("20 10 2015");
		expected.setEditFields(editType);

		assertEquals(expected, output);
		assertEquals(output2, expected);
	}

	@Test
	public void editBySearchSTET() {
		String input = "edit tuition start 1pm end 3pm";
		AbstractCommand output = parser.parseInput(input);

		String input2 = "edit tuition to start 1pm end 3pm";
		AbstractCommand output2 = parser.parseInput(input2);

		EditCommand expected = new EditCommand("tuition");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.START_TIME);
		expected.setNewStartTime("13 00");
		editType.add(EditCommand.editField.END_TIME);
		expected.setNewEndTime("15 00");
		expected.setEditFields(editType);		

		assertEquals(expected, output);
		assertEquals(output2, expected);
	}

	@Test
	public void editBySearchSDED() {
		String input = "edit tuition to start 10-10-2015 end 10-10-2015";
		AbstractCommand output = parser.parseInput(input);

		EditCommand expected = new EditCommand("tuition");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.START_DATE);
		expected.setNewStartDate("10 10 2015");
		editType.add(EditCommand.editField.END_DATE);
		expected.setNewEndDate("10 10 2015");
		expected.setEditFields(editType);		

		assertEquals(expected, output);
	}

	@Test
	public void editBySearchN() {
		String input = "edit hello to jello";
		AbstractCommand output = parser.parseInput(input);

		EditCommand expected = new EditCommand("hello");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.NAME);
		expected.setNewName("jello");
		expected.setEditFields(editType);		

		assertEquals(expected, output);
	}

	@Test
	public void editBySearchST() {
		String input = "edit hello monkey start 8pm";
		AbstractCommand output = parser.parseInput(input);

		String input2 = "edit hello monkey to start 8pm";
		AbstractCommand output2 = parser.parseInput(input2);


		EditCommand expected = new EditCommand("hello monkey");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.START_TIME);
		expected.setNewStartTime("20 00");
		expected.setEditFields(editType);		

		assertEquals(expected, output);
		assertEquals(output2, expected);
	}

	@Test
	public void editBySearchSD() {
		String input = "edit hello monkey start 19-9-2015";
		AbstractCommand output = parser.parseInput(input);

		String input2 = "edit hello monkey to start 19-9-2015";
		AbstractCommand output2 = parser.parseInput(input2);


		EditCommand expected = new EditCommand("hello monkey");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.START_DATE);
		expected.setNewStartDate("19 09 2015");
		expected.setEditFields(editType);		

		assertEquals(expected, output);
		assertEquals(output2, expected);
	}

	@Test
	public void editBySearchET() {
		String input = "edit hello monkey end 12pm";
		AbstractCommand output = parser.parseInput(input);

		String input2 = "edit hello monkey to end 12pm";
		AbstractCommand output2 = parser.parseInput(input2);


		EditCommand expected = new EditCommand("hello monkey");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.END_TIME);
		expected.setNewEndTime("12 00");
		expected.setEditFields(editType);		

		assertEquals(expected, output);
		assertEquals(output2, expected);
	}

	@Test
	public void editBySearchED() {
		String input = "edit hello monkey end 19-12-2015";
		AbstractCommand output = parser.parseInput(input);

		String input2 = "edit hello monkey to end 19-12-2015";
		AbstractCommand output2 = parser.parseInput(input2);


		EditCommand expected = new EditCommand("hello monkey");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.END_DATE);
		expected.setNewEndDate("19 12 2015");
		expected.setEditFields(editType);		

		assertEquals(expected, output);
		assertEquals(output2, expected);
	}

	@Test
	public void editEmpty() {
		String input = "edit";
		AbstractCommand output = parser.parseInput(input);

		InvalidCommand expected = new InvalidCommand();

		assertEquals(expected, output);
	}

	@Test
	public void editWithYtdAndTmr() {
		String input = "edit holiday at Maldives start ytd end tmr";
		AbstractCommand output = parser.parseInput(input);

		EditCommand expected = new EditCommand("holiday at Maldives");
		DateTime dty = new DateTime().minusDays(1);
		DateTime dtt = new DateTime().plusDays(1);
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.START_DATE);
		expected.setNewStartDate(dty.getDayOfMonth() + " " + dty.getMonthOfYear() + " " + dty.getYear());
		editType.add(EditCommand.editField.END_DATE);
		expected.setNewEndDate(dtt.getDayOfMonth() + " " + dtt.getMonthOfYear() + " " + dtt.getYear());
		expected.setEditFields(editType);		

		assertEquals(expected, output);
	}

	@Test
	public void editWithYesterdayAndTomorrow() {
		String input = "edit holiday at Maldives start yesterday end tomorrow";
		AbstractCommand output = parser.parseInput(input);

		EditCommand expected = new EditCommand("holiday at Maldives");
		DateTime dty = new DateTime().minusDays(1);
		DateTime dtt = new DateTime().plusDays(1);
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.START_DATE);
		expected.setNewStartDate(dty.getDayOfMonth() + " " + dty.getMonthOfYear() + " " + dty.getYear());
		editType.add(EditCommand.editField.END_DATE);
		expected.setNewEndDate(dtt.getDayOfMonth() + " " + dtt.getMonthOfYear() + " " + dtt.getYear());
		expected.setEditFields(editType);		

		assertEquals(expected, output);
	}

	@Test
	public void editWithTodayAndNextWeek() {
		String input = "edit church conference to church camp start today end next week";
		AbstractCommand output = parser.parseInput(input);

		EditCommand expected = new EditCommand("church conference");
		DateTime dty = new DateTime();
		DateTime dtt = new DateTime().plusWeeks(1);
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.NAME);
		expected.setNewName("church camp");
		editType.add(EditCommand.editField.START_DATE);
		expected.setNewStartDate(dty.getDayOfMonth() + " " + dty.getMonthOfYear() + " " + dty.getYear());
		editType.add(EditCommand.editField.END_DATE);
		expected.setNewEndDate(dtt.getDayOfMonth() + " " + dtt.getMonthOfYear() + " " + dtt.getYear());
		expected.setEditFields(editType);		

		assertEquals(expected, output);
	}

	//*******************************************************************
	//*******************************************************************
	// 	FOR DELETE COMMAND
	//*******************************************************************
	//*******************************************************************

	//===================================================================
	// STANDARD DELETE TESTS
	//===================================================================

	@Test
	public void deleteByIndex() {
		String input = "delete #15";
		AbstractCommand output = parser.parseInput(input);

		DeleteCommand expected = new DeleteCommand("15");

		assertEquals(expected, output);
	}

	@Test
	public void deleteBySearchOneWord() {
		String input = "delete meeting";
		AbstractCommand output = parser.parseInput(input);

		DeleteCommand expected = new DeleteCommand("meeting");

		assertEquals(expected, output);
	}

	@Test
	public void deleteBySearchManyWords() {
		String input = "delete group project meeting";
		AbstractCommand output = parser.parseInput(input);

		DeleteCommand expected = new DeleteCommand("group project meeting");

		assertEquals(expected, output);
	}

	@Test
	public void dlBySearchManyWords() {
		String input = "dl group project meeting";
		AbstractCommand output = parser.parseInput(input);

		DeleteCommand expected = new DeleteCommand("group project meeting");

		assertEquals(expected, output);
	}

	@Test
	public void deleteEmpty() {
		String input = "delete";
		AbstractCommand output = parser.parseInput(input);

		assertEquals(expectedInvalid, output);
	}

	@Test
	public void deleteAll() {
		String input = "delete all";
		AbstractCommand output = parser.parseInput(input);

		DeleteCommand expected = new DeleteCommand(DeleteCommand.Scope.ALL);

		assertEquals(expected, output);
	}

	@Test
	public void deleteDone() {
		String input = "delete done";
		AbstractCommand output = parser.parseInput(input);

		DeleteCommand expected = new DeleteCommand(DeleteCommand.Scope.DONE);

		assertEquals(expected, output);
	}

	@Test
	public void deleteUndone() {
		String input = "delete undone";
		AbstractCommand output = parser.parseInput(input);

		DeleteCommand expected = new DeleteCommand(DeleteCommand.Scope.UNDONE);

		assertEquals(expected, output);
	}

	
	//*******************************************************************
	//*******************************************************************
	// 	FOR DISPLAY COMMAND
	//*******************************************************************
	//*******************************************************************

	//===================================================================
	// STANDARD DISPLAY TESTS
	//===================================================================

	@Test
	public void displayBySearchOneWord() {
		String input = "display meeting";
		AbstractCommand output = parser.parseInput(input);

		DisplayCommand expected = new DisplayCommand("meeting");

		assertEquals(expected, output);
	}

	@Test
	public void displayBySearchManyWords() {
		String input = "display group project meeting";
		AbstractCommand output = parser.parseInput(input);

		DisplayCommand expected = new DisplayCommand("group project meeting");

		assertEquals(expected, output);
	}

	@Test
	public void dpBySearchManyWords() {
		String input = "dp group project meeting";
		AbstractCommand output = parser.parseInput(input);

		DisplayCommand expected = new DisplayCommand("group project meeting");

		assertEquals(expected, output);
	}

	@Test
	public void displayEmpty() {
		String input = "display";
		AbstractCommand output = parser.parseInput(input);

		DisplayCommand expected = new DisplayCommand(DisplayCommand.Scope.ALL);

		assertEquals(expected, output);
	}

	@Test
	public void displayAll() {
		String input = "display all";
		AbstractCommand output = parser.parseInput(input);

		DisplayCommand expected = new DisplayCommand(DisplayCommand.Scope.ALL);

		assertEquals(expected, output);
	}

	@Test
	public void displayDone() {
		String input = "display done";
		AbstractCommand output = parser.parseInput(input);

		DisplayCommand expected = new DisplayCommand(DisplayCommand.Scope.DONE);

		assertEquals(expected, output);
	}

	@Test
	public void displayUndone() {
		String input = "display undone";
		AbstractCommand output = parser.parseInput(input);

		DisplayCommand expected = new DisplayCommand(DisplayCommand.Scope.UNDONE);

		assertEquals(expected, output);
	}

	
	//*******************************************************************
	//*******************************************************************
	// 	FOR SEARCH COMMAND
	//*******************************************************************
	//*******************************************************************

	//===================================================================
	// STANDARD SEARCH TESTS
	//===================================================================

}
