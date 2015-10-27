package parser;

import static org.junit.Assert.*;
import org.junit.Test;
import shared.command.AbstractCommand;
import shared.command.CreateCommand;
import shared.command.DeleteCommand;
import shared.command.DisplayCommand;
import shared.command.EditCommand;
import shared.command.ExitCommand;
import shared.command.HelpCommand;
import shared.command.InvalidCommand;
import shared.command.MarkCommand;
import shared.command.SaveCommand;
import shared.command.UICommand;
import shared.command.UndoCommand;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ParserTest {
	
	Parser parser = new Parser();
	
	InvalidCommand expectedInvalid = new InvalidCommand();
	
	DateTimeFormatter DTFormatter = DateTimeFormatter.ofPattern("dd MM yyyy HH mm");
	String dummyTime = "00 00";
	LocalDateTime currentDate = LocalDateTime.now();
	LocalDateTime currentMon = LocalDateTime.now().with(DayOfWeek.MONDAY);
	
	public String stringify(LocalDateTime date) {
		return String.format("%02d", date.getDayOfMonth()) + " " + String.format("%02d", date.getMonthValue()) + " " + date.getYear();
	}
	
	public String getCorrectYear(String str) {
		LocalDateTime dt = LocalDateTime.now();
		String[] strParts = str.split(" ");
		String day = strParts[0];
		String month = strParts[1];
		String year;
		
		if (Integer.parseInt(month) < dt.getMonthValue()) {
			year = String.valueOf(dt.plusYears(1).getYear());
		} else if (Integer.parseInt(month) == dt.getMonthValue() && Integer.parseInt(day) < dt.getDayOfMonth()) {
			year = String.valueOf(dt.plusYears(1).getYear());
		} else {
			year = String.valueOf(dt.getYear());
		}
		
		return year;
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
		String input = "create lecture!";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("lecture!");
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
		String input = "create lecture by 12:10pm 10-10-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("lecture", LocalDateTime.parse("10 10 2015 12 10", DTFormatter));		
		assertEquals(expected, output);
	}

	@Test
	public void cDTManyWordsName() {
		String input = "c buy apples, oranges and starfruits by 1-1-2015 12:20PM";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("buy apples, oranges and starfruits", LocalDateTime.parse("01 01 2015 12 20", DTFormatter));		
		assertEquals(expected, output);
	}

	@Test
	public void createDTManyWordsName() {
		String input = "create buy apples, oranges and starfruits by 12pm 28/2/2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("buy apples, oranges and starfruits", LocalDateTime.parse("28 02 2015 12 00", DTFormatter));		
		assertEquals(expected, output);
	}

	@Test
	public void createBTOneWordName() {
		String input = "create lecture from 21/10/2015 12PM to 2:19PM 1-11-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("lecture", LocalDateTime.parse("21 10 2015 12 00", DTFormatter), LocalDateTime.parse("01 11 2015 14 19", DTFormatter));		
		assertEquals(expected, output);
	}

	@Test
	public void cBTManyWordsName() {
		String input = "c buy apples, oranges and starfruits from 12am 11-11-2015 to 12-12-2015 0:17";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("buy apples, oranges and starfruits", LocalDateTime.parse("11 11 2015 00 00", DTFormatter), LocalDateTime.parse("12 12 2015 00 17", DTFormatter));		
		assertEquals(expected, output);
	}

	@Test
	public void createBTManyWordsName() {
		String input = "create buy apples, oranges and starfruits from 00:29 10-10-2015 to 2:15 10-10-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("buy apples, oranges and starfruits", LocalDateTime.parse("10 10 2015 00 29", DTFormatter), LocalDateTime.parse("10 10 2015 02 15", DTFormatter));		
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
	
	@Test
	public void createFTWhitespace() {
		String input = "create   ";
		AbstractCommand output = parser.parseInput(input);
		assertEquals(output, expectedInvalid);
	}

	//===================================================================
	// CREATE WITH DIFFERENT TIME FORMATS
	//===================================================================

	@Test // 12hour: single digit + am
	public void createValidTime1() {
		String input = "create example by 8am 06-7-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("06 07 2015 08 00", DTFormatter));		
		assertEquals(expected, output);
	}

	@Test // 12hour: double digit + AM && 12hour: double digit + pm
	public void createValidTime2() {
		String input = "create example from 12AM 4-4-2015 to 10pm 5-04-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("04 04 2015 00 00", DTFormatter), LocalDateTime.parse("05 04 2015 22 00", DTFormatter));		
		assertEquals(expected, output);
	}

	@Test
	public void createValidTime3() { // 12hour: single digit + PM
		String input = "create example by 9PM 03-03-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("03 03 2015 21 00", DTFormatter));		
		assertEquals(expected, output);
	}

	@Test
	public void createValidTime4() {
		String input = "create example by 12:30am 8-12-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("08 12 2015 00 30", DTFormatter));		
		assertEquals(expected, output);
	}

	@Test
	public void createValidTime5() {
		String input = "create example from 06:30AM 05-1-2015 to 7:15pm 10-02-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("05 01 2015 06 30", DTFormatter), LocalDateTime.parse("10 02 2015 19 15", DTFormatter));		
		assertEquals(expected, output);
	}

	@Test
	public void createValidTime6() {
		String input = "create example by 11:48PM 25-09-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("25 09 2015 23 48", DTFormatter));		
		assertEquals(expected, output);
	}

	@Test
	public void createValidTime7() {
		String input = "create example by 00:41 12-12-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("12 12 2015 00 41", DTFormatter));		
		assertEquals(expected, output);
	}

	@Test
	public void createValidTime8() {
		String input = "create example from 4:15 6-01-2015 to 0:21 07-08-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("06 01 2015 04 15", DTFormatter), LocalDateTime.parse("07 08 2015 00 21", DTFormatter));		
		assertEquals(expected, output);
	}

	@Test
	public void createValidTime9() {
		String input = "create example by 08:30 09-10-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("09 10 2015 08 30", DTFormatter));		
		assertEquals(expected, output);
	}

	@Test
	public void createValidTime10() {
		String input = "create example by 16:45 6-6-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("06 06 2015 16 45", DTFormatter));		
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
		String input = "create example by 2am 01-01-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("01 01 2015 02 00", DTFormatter));		
		assertEquals(expected, output);
	}

	@Test
	public void createValidDate2() {
		String input = "create example from 2-01-2015 3AM to 02-1-2015 3:15am";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("02 01 2015 03 00", DTFormatter), LocalDateTime.parse("02 01 2015 03 15", DTFormatter));		
		assertEquals(expected, output);
	}

	@Test
	public void createValidDate3() {
		String input = "create example by 4:28AM 1-1-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("01 01 2015 04 28", DTFormatter));		
		assertEquals(expected, output);
	}
	
	@Test
	public void createValidDate4() {
		String input = "create example by 05:19am 3-10";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("03 10 " + getCorrectYear("03 10") + " 05 19", DTFormatter));		
		assertEquals(expected, output);
	}

	@Test
	public void createValidDate5() {
		String input = "create example from 06:53AM 4-4 to 12pm 10-10";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("04 04 " + getCorrectYear("4 4") + " 06 53", DTFormatter), LocalDateTime.parse("10 10 " + getCorrectYear("10 10") + " 12 00", DTFormatter));		
		assertEquals(expected, output);
	}

	@Test
	public void createValidDate6() {
		String input = "create example by 7pm 05-11";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("05 11 " + getCorrectYear("05 11") + " 19 00", DTFormatter));		
		assertEquals(expected, output);
	}

	@Test
	public void createValidDate7() {
		String input = "create example by 7PM 6/07/2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("06 07 2015 19 00", DTFormatter));		
		assertEquals(expected, output);
	}

	@Test
	public void createValidDate8() {
		String input = "create example from 19:15 6/7/2015 to 7:25pm 07/8/2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("06 07 2015 19 15", DTFormatter), LocalDateTime.parse("07 08 2015 19 25", DTFormatter));		
		assertEquals(expected, output);
	}

	@Test
	public void createValidDate9() {
		String input = "create example by 07:55PM 09/09/2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("09 09 2015 19 55", DTFormatter));		
		assertEquals(expected, output);
	}

	@Test
	public void createValidDate10() {
		String input = "create example by 10am 11/12";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("11 12 " + getCorrectYear("11 12") + " 10 00", DTFormatter));		
		assertEquals(expected, output);
	}

	@Test
	public void createValidDate11() {
		String input = "create example from 10AM 1/10 to 12PM 10/1";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("01 10 " + getCorrectYear("01 10") + " 10 00", DTFormatter), LocalDateTime.parse("10 01 " + getCorrectYear("10 01") + " 12 00", DTFormatter));		
		assertEquals(expected, output);
	}

	@Test
	public void createValidDate12() {
		String input = "create example by 10:50am 1/1";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("01 01 " + getCorrectYear("01 01") + " 10 50", DTFormatter));		
		assertEquals(expected, output);
	}
	
	@Test
	public void createValidDate13() {
		String input = "create example by 10:50am 31/12";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("example", LocalDateTime.parse("31 12 " + getCorrectYear("31 12") + " 10 50", DTFormatter));		
		assertEquals(expected, output);
	}

	//===================================================================
	// CREATE WITH MONTH IN ENGLISH DATE FORMATS
	//===================================================================
	
	@Test
	public void createDTWithJan1() {
		String input = "create build a sandcastle by 14:45 2 Jan 2016";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("build a sandcastle", 
				LocalDateTime.parse("02 01 2016 14 45", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createDTWithJan2() {
		String input = "create build a sandcastle by 14:45 02 Jan 2016";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("build a sandcastle", 
				LocalDateTime.parse("02 01 2016 14 45", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createDTWithFebruary() {
		String input = "create go to work in my closet company by 28 February 2016 0:00";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("go to work in my closet company", 
				LocalDateTime.parse("28 02 2016 00 00", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createDTWithMarNoYear() {
		String input = "create buy black pumps by 00:00 10 MAR";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("buy black pumps", 
				LocalDateTime.parse("10 03 " + getCorrectYear("10 03") + " 00 00", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createDTWithAprilNoYear() {
		String input = "create watch webcast by 10pm 4 april";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("watch webcast", 
				LocalDateTime.parse("04 04 " + getCorrectYear("04 04") + " 22 00", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createBTWithJuneAndJulNoYear() {
		String input = "create reach nirvana from 15 jun 2016 1:00am to 2am 18 july";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("reach nirvana", 
				LocalDateTime.parse("15 06 2016 01 00", DTFormatter),
				LocalDateTime.parse("18 07 " + getCorrectYear("18 07") + " 02 00", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createDTWithAugust() {
		String input = "create watch national day rally by 9august 2016 12pm";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("watch national day rally", 
				LocalDateTime.parse("09 08 2016 12 00", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createDTWithSep() {
		String input = "create wake me up when september ends by 30sep 2016 23:59";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("wake me up when september ends", 
				LocalDateTime.parse("30 09 2016 23 59", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createDTWithOctoberNoYear1() {
		String input = "create buy tickets for octoberfest by 2october 16:15";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("buy tickets for octoberfest", 
				LocalDateTime.parse("02 10 " + getCorrectYear("02 10") + " 16 15", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createDTWithOctoberNoYear2() {
		String input = "create buy tickets for octoberfest by 02october 16:15";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("buy tickets for octoberfest", 
				LocalDateTime.parse("02 10 " + getCorrectYear("02 10") + " 16 15", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createBTWithNovNoYearAndDecember() {
		String input = "create cruise trip from 8AM 1Nov to 31December 2016 10PM";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("cruise trip", 
				LocalDateTime.parse("01 11 " + getCorrectYear("01 11") + " 08 00", DTFormatter),
				LocalDateTime.parse("31 12 2016 22 00", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createBTWithFebAndAprilNoYear() {
		String input = "create OCIP from 6am 12 feb 2016 to 7am 16april";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("OCIP", 
				LocalDateTime.parse("12 02 2016 06 00", DTFormatter),
				LocalDateTime.parse("16 04 " + getCorrectYear("16 04") + " 07 00", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createBTWithSepAndNovemberNoYear() {
		String input = "create OCIP from 23sep 2016 11am to 28 november 12pm";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("OCIP", 
				LocalDateTime.parse("23 09 2016 11 00", DTFormatter),
				LocalDateTime.parse("28 11 " + getCorrectYear("28 11") + " 12 00", DTFormatter));
		assertEquals(expected, output);
	}
	
	//===================================================================
	// CREATE WITH NATURAL LANGUAGE DATE FORMATS
	//===================================================================
	
	@Test
	public void createDTWithTmr() {
		String input = "create watch dancing with the stars by 4pm TMR";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("watch dancing with the stars", 
				LocalDateTime.parse(stringify(currentDate.plusDays(1)) + " " + "16 00", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createDTWithYesterday() {
		String input = "create should have been done by yesterday 12pm";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("should have been done", 
				LocalDateTime.parse(stringify(currentDate.minusDays(1)) + " " + "12 00", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createDTWithTonight() {
		String input = "create buy dinner ingredients by 5pm tonight";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("buy dinner ingredients", 
				LocalDateTime.parse(stringify(currentDate) + " " + "17 00", DTFormatter));
		assertEquals(expected, output);
	}

	@Test
	public void createBTWithYtdAndTomorrow() {
		String input = "create a special event from ytd 1pm to 6:30pm tomorrow";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("a special event", 
				LocalDateTime.parse(stringify(currentDate.minusDays(1)) + " " + "13 00", DTFormatter),
				LocalDateTime.parse(stringify(currentDate.plusDays(1)) + " " + "18 30", DTFormatter));
		assertEquals(expected, output);
	}

	@Test
	public void createBTWithYesterdayAndTmr() {
		String input = "create staycation from 10pm YESTERDAY to tmr 11pm";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("staycation", 
				LocalDateTime.parse(stringify(currentDate.minusDays(1)) + " " + "22 00", DTFormatter),
				LocalDateTime.parse(stringify(currentDate.plusDays(1)) + " " + "23 00", DTFormatter));
		assertEquals(expected, output);
	}

	@Test
	public void createDTWithNextMonday() {
		String input = "create hand in annual report by 2pm next Monday";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("hand in annual report", LocalDateTime.parse(stringify(currentMon.plusWeeks(1)) + " " + "14 00", DTFormatter));
		assertEquals(expected, output);
	}

	@Test
	public void createDTWithThisFri() {
		String input = "create eat steak by this Fri 15:30";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("eat steak", 
				LocalDateTime.parse(stringify(currentMon.plusDays(4)) + " " + "15 30", DTFormatter));
		assertEquals(expected, output);
	}

	@Test
	public void createDTWithLastSaturday() {
		String input = "create call mom by 7:07 last Saturday";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("call mom", 
				LocalDateTime.parse(stringify(currentMon.minusWeeks(1).plusDays(5)) + " " + "07 07", DTFormatter));
		assertEquals(expected, output);
	}

	@Test
	public void createBTWithLastSunAndNextThursday() {
		String input = "create hibernate like a polar bear from 08:18 last sun to next Thursday 12:16am";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("hibernate like a polar bear", 
				LocalDateTime.parse(stringify(currentMon.minusWeeks(1).plusDays(6)) + " " + "08 18", DTFormatter),
				LocalDateTime.parse(stringify(currentMon.plusWeeks(1).plusDays(3)) + " " + "00 16", DTFormatter));
		assertEquals(expected, output);
	}

	@Test
	public void createBTWithLastSundayAndThisWed() {
		String input = "create meditate from last sunday 7pm to 6:06 THIS WED";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("meditate", 
				LocalDateTime.parse(stringify(currentMon.minusWeeks(1).plusDays(6)) + " " + "19 00", DTFormatter),
				LocalDateTime.parse(stringify(currentMon.plusDays(2)) + " " + "06 06", DTFormatter));
		assertEquals(expected, output);
	}

	@Test
	public void createBTWithTodayAndNextTues() {
		String input = "create renovate house from ToDaY 4:19pm to 09:28am next tUeS";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("renovate house", 
				LocalDateTime.parse(stringify(currentDate) + " " + "16 19", DTFormatter),
				LocalDateTime.parse(stringify(currentMon.plusWeeks(1).plusDays(1)) + " " + "09 28", DTFormatter));
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
		String input = "create test test test by by 09:00am 9/09";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("test test test by", LocalDateTime.parse("09 09 " + getCorrectYear("09 09") + " 09 00", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createDTWithKeyword2() {
		String input = "create test by 9am 05-05 by 9am 05-05";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("test by 9am 05-05", LocalDateTime.parse("05 05 " + getCorrectYear("05 05") + " 09 00", DTFormatter));
		assertEquals(expected, output);
	}

	@Test
	public void createDTWithKeyword3() {
		String input = "create test from 09:00AM 07-7 by 09:00AM 07-7";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("test from 09:00AM 07-7", LocalDateTime.parse("07 07 " + getCorrectYear("07 07") + " 09 00", DTFormatter));
		assertEquals(expected, output);
	}

	@Test
	public void createDTWithKeyword4() {
		String input = "create test to 2-2-2015 09:00 by 02-02-2015 09:00";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("test to 2-2-2015 09:00", LocalDateTime.parse("02 02 2015 09 00", DTFormatter));
		assertEquals(expected, output);
	}

	@Test
	public void createDTWithKeyword5() {
		String input = "create update exam details from /10AM /05-05-2015 /to /10AM /05-05-2015 by 10AM 05-05-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("update exam details from 10AM 05-05-2015 to 10AM 05-05-2015", LocalDateTime.parse("05 05 2015 10 00", DTFormatter));
		assertEquals(expected, output);
	}

	@Test
	public void createBTWithKeyword1() {
		String input = "create test by 10am 5-5-15 from 10am 5-5-2015 to 13:00 05-05-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("test by 10am 5-5-15", LocalDateTime.parse("05 05 2015 10 00", DTFormatter), LocalDateTime.parse("05 05 2015 13 00", DTFormatter));
		assertEquals(expected, output);
	}

	@Test
	public void createBTWithKeyword2() {
		String input = "create test from 10am 5-5-2015 to 10am 5-5-2015 from 10am 5-5-2015 to 13:00 05-05-2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("test from 10am 5-5-2015 to 10am 5-5-2015", LocalDateTime.parse("05 05 2015 10 00", DTFormatter), LocalDateTime.parse("05 05 2015 13 00", DTFormatter));
		assertEquals(expected, output);
	}
	
	//===================================================================
	// TEST SHORTCUT FOR CREATING BOUNDED TASKS WITH SAME START & END DATE
	//===================================================================
	
	@Test
	public void createBTSameDate1() {
		String input = "create networking session from 7pm to 10pm today";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("networking session", LocalDateTime.parse(stringify(currentDate) + " 19 00", DTFormatter), LocalDateTime.parse(stringify(currentDate) + " 22 00", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createBTSameDate2() {
		String input = "create art and crafts time from 15:00 next fri to 17:30";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("art and crafts time", LocalDateTime.parse(stringify(currentMon.plusWeeks(1).plusDays(4)) + " 15 00", DTFormatter), LocalDateTime.parse(stringify(currentMon.plusWeeks(1).plusDays(4)) + " 17 30", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createBTSameDate3() {
		String input = "create lockcity x lockdown from 10AM to 9PM 25 october 2015";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("lockcity x lockdown", LocalDateTime.parse("25 10 2015 10 00", DTFormatter), LocalDateTime.parse("25 10 2015 21 00", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createBTSameDate4() {
		String input = "create find a vampire from 10:57 to 17 sep 14:23";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("find a vampire", LocalDateTime.parse("17 09 " + getCorrectYear("17 09") + " 10 57", DTFormatter), LocalDateTime.parse("17 09 " + getCorrectYear("17 09") + " 14 23", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createBTSameDate5() {
		String input = "create Scrabble competition from 1:30pm 5feb 2016 to 3:30pm";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("Scrabble competition", LocalDateTime.parse("05 02 2016 13 30", DTFormatter), LocalDateTime.parse("05 02 2016 15 30", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createBTSameDate6() {
		String input = "create hockey competition from 10jan 11am to 1pm";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("hockey competition", LocalDateTime.parse("10 01 " + getCorrectYear("10 01") + " 11 00", DTFormatter), LocalDateTime.parse("10 01 " + getCorrectYear("10 01") + " 13 00", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createBTSameDate7() {
		String input = "create stay up late from 2am to 4am 5/5/2016";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("stay up late", LocalDateTime.parse("05 05 2016 02 00", DTFormatter), LocalDateTime.parse("05 05 2016 04 00", DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void createBTSameDate8() {
		String input = "create vacation from 00:00 7/8 to 23:59";
		AbstractCommand output = parser.parseInput(input);
		CreateCommand expected = new CreateCommand("vacation", LocalDateTime.parse("07 08 " + getCorrectYear("07 08") + " 00 00", DTFormatter), LocalDateTime.parse("07 08 " + getCorrectYear("07 08") + " 23 59", DTFormatter));
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
		String input = "edit 5";
		AbstractCommand output = parser.parseInput(input);

		EditCommand expected = new EditCommand(5);
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		expected.setEditFields(editType);

		assertEquals(expected, output);
	}
	
	@Test
	public void editByIndexN() {
		String input = "edit 1 to eat lunch";
		AbstractCommand output = parser.parseInput(input);

		EditCommand expected = new EditCommand(1);
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.NAME);
		expected.setNewName("eat lunch");
		expected.setEditFields(editType);		
		
		assertEquals(expected, output);
	}

	@Test
	public void editByIndexNSTSDETED() {
		String input = "EDIT 2 TO fun and games start 3pm 20-10-2015 end 5pm 20-10-2015";
		AbstractCommand output = parser.parseInput(input);

		EditCommand expected = new EditCommand(2);
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.NAME);
		expected.setNewName("fun and games");
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
	public void editBySearchKeywordNumberN() {
		String input = "edit /1 to eat lunch";
		AbstractCommand output = parser.parseInput(input);

		EditCommand expected = new EditCommand("1");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.NAME);
		expected.setNewName("eat lunch");
		expected.setEditFields(editType);		
		
		assertEquals(expected, output);
	}

	@Test
	public void editBySearchKeyword() {
		String input = "edit birthday";
		AbstractCommand output = parser.parseInput(input);

		EditCommand expected = new EditCommand("birthday");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		expected.setEditFields(editType);

		assertEquals(expected, output);
	}

	@Test
	public void editBySearchKeywordNSTSDETED() {
		String input = "edit anniversary lunch TO anniversary dinner start 4:19PM 6/06 end 5:00am 09/6";
		AbstractCommand output = parser.parseInput(input);
		
		EditCommand expected = new EditCommand("anniversary lunch");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.NAME);
		expected.setNewName("anniversary dinner");
		editType.add(EditCommand.editField.START_TIME);
		expected.setNewStartTime("16 19");
		editType.add(EditCommand.editField.START_DATE);
		expected.setNewStartDate("06 06 " + getCorrectYear("06 06"));
		editType.add(EditCommand.editField.END_TIME);
		expected.setNewEndTime("05 00");
		editType.add(EditCommand.editField.END_DATE);
		expected.setNewEndDate("09 06 " + getCorrectYear("09 06"));
		expected.setEditFields(editType);

		assertEquals(expected, output);
	}

	@Test
	public void eBySearchKeywordNSTSDETED() {
		String input = "E morning swim to lazing in on Sunday start 2-2-2015 07:10 end 02-02-2015 10AM";
		AbstractCommand output = parser.parseInput(input);

		EditCommand expected = new EditCommand("morning swim");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.NAME);
		expected.setNewName("lazing in on Sunday");
		editType.add(EditCommand.editField.START_TIME);
		expected.setNewStartTime("07 10");
		editType.add(EditCommand.editField.START_DATE);
		expected.setNewStartDate("02 02 2015");
		editType.add(EditCommand.editField.END_TIME);
		expected.setNewEndTime("10 00");
		editType.add(EditCommand.editField.END_DATE);
		expected.setNewEndDate("02 02 2015");
		expected.setEditFields(editType);

		assertEquals(expected, output);
	}

	@Test
	public void editBySearchKeywordNSDSTEDET() {
		String input = "EDIT play with cat to /start training dog start 2/09 8:19AM end 18:53 02/09";
		AbstractCommand output = parser.parseInput(input);

		EditCommand expected = new EditCommand("play with cat");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.NAME);
		expected.setNewName("start training dog");
		editType.add(EditCommand.editField.START_TIME);
		expected.setNewStartTime("08 19");
		editType.add(EditCommand.editField.START_DATE);
		expected.setNewStartDate("02 09 " + getCorrectYear("02 09"));
		editType.add(EditCommand.editField.END_TIME);
		expected.setNewEndTime("18 53");
		editType.add(EditCommand.editField.END_DATE);
		expected.setNewEndDate("02 09 " + getCorrectYear("02 09"));
		expected.setEditFields(editType);

		assertEquals(expected, output);
	}

	@Test
	public void editBySearchKeywordNSTSD() {
		String input = "edit travel /to Narnia to /to Kansas instead start 08:10 31/1";
		AbstractCommand output = parser.parseInput(input);

		EditCommand expected = new EditCommand("travel to Narnia");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.NAME);
		expected.setNewName("to Kansas instead");
		editType.add(EditCommand.editField.START_TIME);
		expected.setNewStartTime("08 10");
		editType.add(EditCommand.editField.START_DATE);
		expected.setNewStartDate("31 01 " + getCorrectYear("31 01"));
		expected.setEditFields(editType);
		
		assertEquals(expected, output);
	}

	@Test
	public void editBySearchKeywordNEDET() {
		String input = "edit happy /to to see you end 8/8 10:03PM";
		AbstractCommand output = parser.parseInput(input);

		EditCommand expected = new EditCommand("happy to");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.NAME);
		expected.setNewName("see you");
		editType.add(EditCommand.editField.END_TIME);
		expected.setNewEndTime("22 03");
		editType.add(EditCommand.editField.END_DATE);
		expected.setNewEndDate("08 08 " + getCorrectYear("08 08"));
		expected.setEditFields(editType);

		assertEquals(expected, output);
	}

	@Test
	public void editBySearchKeywordSTSDETED() {
		String input = "edit something start 5:10 2/3/2015 end 04-05-2015 18:40";
		AbstractCommand output = parser.parseInput(input);

		String input2 = "edit something to start 2/03/2015 05:10 end 06:40PM 04/5/2015";
		AbstractCommand output2 = parser.parseInput(input2);

		EditCommand expected = new EditCommand("something");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.START_TIME);
		expected.setNewStartTime("05 10");
		editType.add(EditCommand.editField.START_DATE);
		expected.setNewStartDate("02 03 2015");
		editType.add(EditCommand.editField.END_TIME);
		expected.setNewEndTime("18 40");
		editType.add(EditCommand.editField.END_DATE);
		expected.setNewEndDate("04 05 2015");
		expected.setEditFields(editType);

		assertEquals(expected, output);
		assertEquals(output2, expected);
	}

	@Test
	public void editBySearchKeywordSTET() {
		String input = "edit tuition start 1:10pM end 3:20Pm";
		AbstractCommand output = parser.parseInput(input);

		String input2 = "edit tuition to start 13:10 end 03:20pm";
		AbstractCommand output2 = parser.parseInput(input2);

		EditCommand expected = new EditCommand("tuition");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.START_TIME);
		expected.setNewStartTime("13 10");
		editType.add(EditCommand.editField.END_TIME);
		expected.setNewEndTime("15 20");
		expected.setEditFields(editType);		

		assertEquals(expected, output);
		assertEquals(output2, expected);
	}

	@Test
	public void editBySearchKeywordSDED1() {
		String input = "edit storytime to start 10-10-2015 end 10/10/2015";
		AbstractCommand output = parser.parseInput(input);

		EditCommand expected = new EditCommand("storytime");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.START_DATE);
		expected.setNewStartDate("10 10 2015");
		editType.add(EditCommand.editField.END_DATE);
		expected.setNewEndDate("10 10 2015");
		expected.setEditFields(editType);		

		assertEquals(expected, output);
	}

	@Test
	public void editBySearchKeywordN() {
		String input = "edit /start /to say hello to /end";
		AbstractCommand output = parser.parseInput(input);

		EditCommand expected = new EditCommand("start to say hello");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.NAME);
		expected.setNewName("end");
		expected.setEditFields(editType);		

		assertEquals(expected, output);
	}

	@Test
	public void editBySearchKeywordST() {
		String input = "edit hello monkey start 8pm";
		AbstractCommand output = parser.parseInput(input);

		String input2 = "edit hello monkey TO start 8pm";
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
	public void editBySearchKeywordSD() {
		String input = "edit hello mr. seagull start 19-9-2015";
		AbstractCommand output = parser.parseInput(input);

		String input2 = "edit hello mr. seagull to START 19/09/2015";
		AbstractCommand output2 = parser.parseInput(input2);


		EditCommand expected = new EditCommand("hello mr. seagull");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.START_DATE);
		expected.setNewStartDate("19 09 2015");
		expected.setEditFields(editType);		

		assertEquals(expected, output);
		assertEquals(output2, expected);
	}

	@Test
	public void eBySearchKeywordET() {
		String input = "e sky diving END 12pm";
		AbstractCommand output = parser.parseInput(input);

		String input2 = "E sky diving to end 12:00pm";
		AbstractCommand output2 = parser.parseInput(input2);


		EditCommand expected = new EditCommand("sky diving");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.END_TIME);
		expected.setNewEndTime("12 00");
		expected.setEditFields(editType);		

		assertEquals(expected, output);
		assertEquals(output2, expected);
	}

	@Test
	public void editBySearchKeywordED() {
		String input = "EDIT detox and diet end 7/2/2015";
		AbstractCommand output = parser.parseInput(input);

		String input2 = "edit detox and diet END 07-02-2015";
		AbstractCommand output2 = parser.parseInput(input2);


		EditCommand expected = new EditCommand("detox and diet");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.END_DATE);
		expected.setNewEndDate("07 02 2015");
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
	public void editBySearchKeywordSDEDWithYtdAndTmr() {
		String input = "edit holiday at Maldives start ytd end tmr";
		AbstractCommand output = parser.parseInput(input);

		EditCommand expected = new EditCommand("holiday at Maldives");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.START_DATE);
		expected.setNewStartDate(stringify(currentDate.minusDays(1)));
		editType.add(EditCommand.editField.END_DATE);
		expected.setNewEndDate(stringify(currentDate.plusDays(1)));
		expected.setEditFields(editType);		

		assertEquals(expected, output);
	}

	@Test
	public void editBySearchKeywordSDEDWithYesterdayAndTomorrow() {
		String input = "edit holiday at Maldives start yesterday end tomorrow";
		AbstractCommand output = parser.parseInput(input);

		EditCommand expected = new EditCommand("holiday at Maldives");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.START_DATE);
		expected.setNewStartDate(stringify(currentDate.minusDays(1)));
		editType.add(EditCommand.editField.END_DATE);
		expected.setNewEndDate(stringify(currentDate.plusDays(1)));
		expected.setEditFields(editType);		

		assertEquals(expected, output);
	}

	@Test
	public void editBySearchKeywordNSDEDWithTodayAndNextSun() {
		String input = "edit church conference tmr TO church camp START today END next sun";
		AbstractCommand output = parser.parseInput(input);

		EditCommand expected = new EditCommand("church conference tmr");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.NAME);
		expected.setNewName("church camp");
		editType.add(EditCommand.editField.START_DATE);
		expected.setNewStartDate(stringify(currentDate));
		editType.add(EditCommand.editField.END_DATE);
		expected.setNewEndDate(stringify(currentMon.plusWeeks(1).plusDays(6)));
		expected.setEditFields(editType);		

		assertEquals(expected, output);
	}
	
	@Test
	public void editBySearchKeywordNSDSTEDETFull() {
		String input = "edit watch the day after tomorrow to /start /to /end start ytd 07:26pm end 13:43 next Fri";
		AbstractCommand output = parser.parseInput(input);

		EditCommand expected = new EditCommand("watch the day after tomorrow");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.NAME);
		expected.setNewName("start to end");
		editType.add(EditCommand.editField.START_TIME);
		expected.setNewStartTime("19 26");
		editType.add(EditCommand.editField.START_DATE);
		expected.setNewStartDate(stringify(currentDate.minusDays(1)));
		editType.add(EditCommand.editField.END_TIME);
		expected.setNewEndTime("13 43");
		editType.add(EditCommand.editField.END_DATE);
		expected.setNewEndDate(stringify(currentMon.plusWeeks(1).plusDays(4)));
		expected.setEditFields(editType);

		assertEquals(expected, output);
	}
	
	@Test
	public void editBySearchKeywordSDED2() {
		String input = "edit attend wedding banquet start 2jan 2015 end 03 february 2015";
		AbstractCommand output = parser.parseInput(input);

		EditCommand expected = new EditCommand("attend wedding banquet");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.START_DATE);
		expected.setNewStartDate("02 01 2015");
		editType.add(EditCommand.editField.END_DATE);
		expected.setNewEndDate("03 02 2015");
		expected.setEditFields(editType);

		assertEquals(expected, output);
	}
	
	@Test
	public void editBySearchKeywordSDED3() {
		String input = "edit part time work start 12March end 27 Apr";
		AbstractCommand output = parser.parseInput(input);

		EditCommand expected = new EditCommand("part time work");
		ArrayList<EditCommand.editField> editType = new ArrayList<EditCommand.editField>();
		editType.add(EditCommand.editField.START_DATE);
		expected.setNewStartDate("12 03 " + getCorrectYear("12 03"));
		editType.add(EditCommand.editField.END_DATE);
		expected.setNewEndDate("27 04 " + getCorrectYear("27 04"));
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
		String input = "delete 15";
		AbstractCommand output = parser.parseInput(input);
		DeleteCommand expected = new DeleteCommand(15);
		assertEquals(expected, output);
	}
	
	@Test
	public void deleteBySearchKeywordNumber() {
		String input = "delete /15";
		AbstractCommand output = parser.parseInput(input);
		DeleteCommand expected = new DeleteCommand("15");
		assertEquals(expected, output);
	}

	@Test
	public void deleteBySearchKeywordOneWord() {
		String input = "DELETE meeting";
		AbstractCommand output = parser.parseInput(input);
		DeleteCommand expected = new DeleteCommand("meeting");
		assertEquals(expected, output);
	}

	@Test
	public void dlBySearchKeywordManyWords() {
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
	public void deleteByScopeAll() {
		String input = "delete all";
		AbstractCommand output = parser.parseInput(input);
		DeleteCommand expected = new DeleteCommand(DeleteCommand.Scope.ALL);
		assertEquals(expected, output);
	}

	@Test
	public void dlByScopeDone() {
		String input = "DL done";
		AbstractCommand output = parser.parseInput(input);
		DeleteCommand expected = new DeleteCommand(DeleteCommand.Scope.DONE);
		assertEquals(expected, output);
	}

	@Test
	public void deleteByScopeUndone() {
		String input = "DELETE UNDONE";
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
	public void dpBySearchDate() {
		String input = "DP 6/7/2015";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand(LocalDateTime.parse("06 07 2015" + " " + dummyTime, DTFormatter), DisplayCommand.Type.SEARCHDATE);
		assertEquals(expected, output);
	}
	
	@Test
	public void displayBySearchDate1() {
		String input = "display 12/1";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand(LocalDateTime.parse("12 01 " + getCorrectYear("12 01") + " " + dummyTime, DTFormatter), DisplayCommand.Type.SEARCHDATE);
		assertEquals(expected, output);
	}
	
	@Test
	public void displayBySearchDateTmr() {
		String input = "display TMR";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand(LocalDateTime.parse(stringify(currentDate.plusDays(1)) + " " + dummyTime, DTFormatter), DisplayCommand.Type.SEARCHDATE);
		assertEquals(expected, output);
	}
	
	@Test
	public void displayBySearchKeywordYesterday() {
		String input = "display /Yesterday";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand("Yesterday");
		assertEquals(expected, output);
	}
	
	@Test
	public void displayBySearchDateLastMon() {
		String input = "display last Mon";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand(LocalDateTime.parse(stringify(currentMon.minusWeeks(1)) + " " + dummyTime, DTFormatter), DisplayCommand.Type.SEARCHDATE);
		assertEquals(expected, output);
	}
	
	@Test
	public void displayBySearchDate2() {
		String input = "display 15 sep 2016";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand(LocalDateTime.parse("15 09 2016" + " " + dummyTime, DTFormatter), DisplayCommand.Type.SEARCHDATE);
		assertEquals(expected, output);
	}
	
	@Test
	public void displayBySearchDate3() {
		String input = "display 24june";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand(LocalDateTime.parse("24 06 " + getCorrectYear("24 06") + " " + dummyTime, DTFormatter), DisplayCommand.Type.SEARCHDATE);
		assertEquals(expected, output);
	}
	
	@Test
	public void displayBySearchDateOnwards() {
		String input = "search from tomorrow";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand(LocalDateTime.parse(stringify(currentDate.plusDays(1)) + " " + dummyTime, DTFormatter), DisplayCommand.Type.SEARCHDATEONWARDS);
		assertEquals(expected, output);
	}
	
	@Test
	public void displayBySearchDatePeriod() {
		String input = "search from last mon to 02jan 2025";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand(LocalDateTime.parse(stringify(currentMon.minusWeeks(1)) + " " + dummyTime, DTFormatter), 
																									LocalDateTime.parse("02 01 2025" + " " + dummyTime, DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void displayBySearchKeywordNextFriday() {
		String input = "display /next /friday";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand("next friday");
		assertEquals(expected, output);
	}
	
	@Test
	public void displayBySearchKeywordThisSun() {
		String input = "display /this sun";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand("this sun");
		assertEquals(expected, output);
	}
	
	@Test
	public void displayBySearchKeywordLastSat() {
		String input = "display last /Sat";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand("last Sat");
		assertEquals(expected, output);
	}
	
	@Test
	public void displayBySearchKeywordOneWord() {
		String input = "display meeting";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand("meeting");
		assertEquals(expected, output);
	}

	@Test
	public void dpBySearchKeywordManyWords() {
		String input = "dp group project meeting";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand("group project meeting");
		assertEquals(expected, output);
	}

	@Test
	public void displayByScopeEmpty() {
		String input = "display";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand(DisplayCommand.Scope.DEFAULT);
		assertEquals(expected, output);
	}

	@Test
	public void displayByScopeAll() {
		String input = "DISPLAY all";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand(DisplayCommand.Scope.ALL);
		assertEquals(expected, output);
	}

	@Test
	public void displayByScopeDone() {
		String input = "display Done";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand(DisplayCommand.Scope.DONE);
		assertEquals(expected, output);
	}

	@Test
	public void dpByScopeUndone() {
		String input = "dp undone";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand(DisplayCommand.Scope.UNDONE);
		assertEquals(expected, output);
	}
	
	@Test
	public void displayByScopeFloating() {
		String input = "display FloaTing";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand(DisplayCommand.Scope.FLOATING);
		assertEquals(expected, output);
	}
	
	@Test
	public void displayBySearchKeywordUndone() {
		String input = "display /undone";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand("undone");
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

	@Test
	public void searchBySearchDate1() {
		String input = "search 12/12/2015";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand(LocalDateTime.parse("12 12 2015" + " " + dummyTime, DTFormatter), DisplayCommand.Type.SEARCHDATE);
		assertEquals(expected, output);
	}
	
	@Test
	public void sBySearchDate() {
		String input = "s 11/2";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand(LocalDateTime.parse("11 02 " + getCorrectYear("11 02") + " " + dummyTime, DTFormatter), DisplayCommand.Type.SEARCHDATE);
		assertEquals(expected, output);
	}
	
	@Test
	public void searchBySearchDateToday() {
		String input = "search today";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand(LocalDateTime.parse(stringify(currentDate) + " " + dummyTime, DTFormatter), DisplayCommand.Type.SEARCHDATE);
		assertEquals(expected, output);
	}
	
	@Test
	public void searchBySearchDateNextMon() {
		String input = "Search next Mon";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand(LocalDateTime.parse(stringify(currentMon.plusWeeks(1)) + " " + dummyTime, DTFormatter), DisplayCommand.Type.SEARCHDATE);
		assertEquals(expected, output);
	}
	
	@Test
	public void searchBySearchKeywordTomorrow() {
		String input = "search /tomorrow";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand("tomorrow");
		assertEquals(expected, output);
	}
	
	@Test
	public void sBySearchKeywordThisThurs() {
		String input = "s /this /thurs";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand("this thurs");
		assertEquals(expected, output);
	}
	
	@Test
	public void searchBySearchKeywordLastWednesday() {
		String input = "search /last Wednesday";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand("last Wednesday");
		assertEquals(expected, output);
	}
	
	@Test
	public void searchBySearchKeywordNextSaturday() {
		String input = "search next /saturday";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand("next saturday");
		assertEquals(expected, output);
	}
	
	@Test
	public void searchBySearchKeywordManyWords() {
		String input = "search lord of the rings";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand("lord of the rings");
		assertEquals(expected, output);
	}
	
	@Test
	public void searchBySearchKeywordAll() {
		String input = "search all";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand("all");
		assertEquals(expected, output);
	}
	
	@Test
	public void searchBySearchDate2() {
		String input = "search 08 MAY 2015";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand(LocalDateTime.parse("08 05 2015" + " " + dummyTime, DTFormatter), DisplayCommand.Type.SEARCHDATE);
		assertEquals(expected, output);
	}
	
	@Test
	public void searchBySearchDate3() {
		String input = "search 17 june";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand(LocalDateTime.parse("17 06 " + getCorrectYear("17 06") + " " + dummyTime, DTFormatter), DisplayCommand.Type.SEARCHDATE);
		assertEquals(expected, output);
	}
	
	@Test
	public void searchBySearchDate4() {
		String input = "search 14july 2015";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand(LocalDateTime.parse("14 07 2015" + " " + dummyTime, DTFormatter), DisplayCommand.Type.SEARCHDATE);
		assertEquals(expected, output);
	}
	
	@Test
	public void searchBySearchDate5() {
		String input = "search 3Aug";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand(LocalDateTime.parse("03 08 " + getCorrectYear("03 08") + " " + dummyTime, DTFormatter), DisplayCommand.Type.SEARCHDATE);
		assertEquals(expected, output);
	}
	
	@Test
	public void searchBySearchDateOnwards1() {
		String input = "search from 1/1/2016";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand(LocalDateTime.parse("01 01 2016" + " " + dummyTime, DTFormatter), DisplayCommand.Type.SEARCHDATEONWARDS);
		assertEquals(expected, output);
	}
	
	@Test
	public void searchBySearchDateOnwards2() {
		String input = "search from 26-10";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand(LocalDateTime.parse("26 10 " + getCorrectYear("26 10") + " " + dummyTime, DTFormatter), DisplayCommand.Type.SEARCHDATEONWARDS);
		assertEquals(expected, output);
	}
	
	@Test
	public void searchBySearchDateOnwards3() {
		String input = "search from tmr";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand(LocalDateTime.parse(stringify(currentDate.plusDays(1)) + " " + dummyTime, DTFormatter), DisplayCommand.Type.SEARCHDATEONWARDS);
		assertEquals(expected, output);
	}
	
	@Test
	public void searchBySearchDateOnwards4() {
		String input = "search from last friday";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand(LocalDateTime.parse(stringify(currentMon.minusWeeks(1).plusDays(4)) + " " + dummyTime, DTFormatter), DisplayCommand.Type.SEARCHDATEONWARDS);
		assertEquals(expected, output);
	}
	
	@Test
	public void searchBySearchDateOnwards5() {
		String input = "search from 6 jan 2016";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand(LocalDateTime.parse("06 01 2016" + " " + dummyTime, DTFormatter), DisplayCommand.Type.SEARCHDATEONWARDS);
		assertEquals(expected, output);
	}
	
	@Test
	public void searchBySearchDateOnwards6() {
		String input = "search from 7november";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand(LocalDateTime.parse("07 11 " + getCorrectYear("07 11") + " " + dummyTime, DTFormatter), DisplayCommand.Type.SEARCHDATEONWARDS);
		assertEquals(expected, output);
	}
	
	@Test
	public void searchBySearchKeywordFromTmr() {
		String input = "search /from /tmr";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand("from tmr");
		assertEquals(expected, output);
	}
	
	@Test
	public void searchBySearchKeywordFromToday() {
		String input = "search from /today";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand("from today");
		assertEquals(expected, output);
	}
	
	@Test
	public void searchBySearchKeywordFromYesterday() {
		String input = "search /from yesterday";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand("from yesterday");
		assertEquals(expected, output);
	}
	
	@Test
	public void searchBySearchKeywordFromLastFriday() {
		String input = "search /from /last /friday";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand("from last friday");
		assertEquals(expected, output);
	}
	
	@Test
	public void searchBySearchKeywordFromThisThurs() {
		String input = "search /from this thurs";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand("from this thurs");
		assertEquals(expected, output);
	}
	
	@Test
	public void searchBySearchKeywordFromNextWed() {
		String input = "search from /next Wed";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand("from next Wed");
		assertEquals(expected, output);
	}
	
	@Test
	public void searchBySearchKeywordFromThisTuesday() {
		String input = "search from this /tuesday";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand("from this tuesday");
		assertEquals(expected, output);
	}
	
	@Test
	public void searchBySearchDatePeriod1() {
		String input = "search from today to 31 december";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand(LocalDateTime.parse(stringify(currentDate) + " " + dummyTime, DTFormatter), 
																									LocalDateTime.parse("31 12 " + getCorrectYear("31 12") + " " + dummyTime, DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void searchBySearchDatePeriod2() {
		String input = "search from 3-05-2015 to 07/10";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand(LocalDateTime.parse("03 05 2015" + " " + dummyTime, DTFormatter), 
																									LocalDateTime.parse("07 10 " + getCorrectYear("07 10") + " " + dummyTime, DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void searchBySearchDatePeriod3() {
		String input = "search from this mon to 19oct 2020";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand(LocalDateTime.parse(stringify(currentMon) + " " + dummyTime, DTFormatter), 
																									LocalDateTime.parse("19 10 2020" + " " + dummyTime, DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void searchBySearchDatePeriod4() {
		String input = "search from 13 March to 14mar";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand(LocalDateTime.parse("13 03 " + getCorrectYear("13 03") + " " + dummyTime, DTFormatter), 
																									LocalDateTime.parse("14 03 " + getCorrectYear("14 03") + " " + dummyTime, DTFormatter));
		assertEquals(expected, output);
	}
	
	@Test
	public void searchBySearchDatePeriod5() {
		String input = "search from ytd to next Sunday";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand(LocalDateTime.parse(stringify(currentDate.minusDays(1)) + " " + dummyTime, DTFormatter), 
																									LocalDateTime.parse(stringify(currentMon.plusWeeks(1).plusDays(6)) + " " + dummyTime, DTFormatter));
		assertEquals(expected, output);
	}

	@Test
	public void searchBySearchKeywordFromYtdToTomorrow() {
		String input = "search from ytd /to tomorrow";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand("from ytd to tomorrow");
		assertEquals(expected, output);
	}
	
	@Test
	public void searchBySearchKeywordFromLastTuesToNextThurs() {
		String input = "search from /last tues to next thurs";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand("from last tues to next thurs");
		assertEquals(expected, output);
	}
	
	@Test
	public void searchBySearchKeywordFromThisWednesdayToNextFriday() {
		String input = "search from this wednesday to next /friday";
		AbstractCommand output = parser.parseInput(input);
		DisplayCommand expected = new DisplayCommand("from this wednesday to next friday");
		assertEquals(expected, output);
	}
	
	//*******************************************************************
	//*******************************************************************
	// 	FOR MARK AND UNMARK COMMAND
	//*******************************************************************
	//*******************************************************************
	
	//===================================================================
	// STANDARD MARK AND UNMARK TESTS
	//===================================================================
	
	@Test
	public void markByIndex() {
		String input = "mark 54";
		AbstractCommand output = parser.parseInput(input);
		MarkCommand expected = new MarkCommand(54);
		expected.setMarkField(MarkCommand.markField.MARK);	
		assertEquals(expected, output);
	}
	
	@Test
	public void markBySearchKeywordNumber() {
		String input = "mark /54";
		AbstractCommand output = parser.parseInput(input);
		MarkCommand expected = new MarkCommand("54");
		expected.setMarkField(MarkCommand.markField.MARK);	
		assertEquals(expected, output);
	}
	
	@Test
	public void mBySearchKeywordOneWord() {
		String input = "m meeting";
		AbstractCommand output = parser.parseInput(input);
		MarkCommand expected = new MarkCommand("meeting");
		expected.setMarkField(MarkCommand.markField.MARK);
		assertEquals(expected, output);
	}
	
	@Test
	public void markBySearchKeywordManyWords() {
		String input = "MARK harry potter and the chamber of secrets";
		AbstractCommand output = parser.parseInput(input);
		MarkCommand expected = new MarkCommand("harry potter and the chamber of secrets");
		expected.setMarkField(MarkCommand.markField.MARK);
		assertEquals(expected, output);
	}
	
	@Test
	public void unmarkByIndex() {
		String input = "UNMARK 03";
		AbstractCommand output = parser.parseInput(input);
		MarkCommand expected = new MarkCommand(3);
		expected.setMarkField(MarkCommand.markField.UNMARK);
		assertEquals(expected, output);
	}
	
	@Test
	public void unmarkBySearchKeywordNumber() {
		String input = "UNMARK /03";
		AbstractCommand output = parser.parseInput(input);
		MarkCommand expected = new MarkCommand("03");
		expected.setMarkField(MarkCommand.markField.UNMARK);
		assertEquals(expected, output);
	}
	
	@Test
	public void unmarkBySearchKeywordOneWord() {
		String input = "unmark lecture";
		AbstractCommand output = parser.parseInput(input);
		MarkCommand expected = new MarkCommand("lecture");
		expected.setMarkField(MarkCommand.markField.UNMARK);
		assertEquals(expected, output);
	}
	
	@Test
	public void umBySearchKeywordManyWords() {
		String input = "um this is a long sentence";
		AbstractCommand output = parser.parseInput(input);
		MarkCommand expected = new MarkCommand("this is a long sentence");
		expected.setMarkField(MarkCommand.markField.UNMARK);
		assertEquals(expected, output);
	}
	
	//*******************************************************************
	//*******************************************************************
	// 	FOR UNDO COMMAND
	//*******************************************************************
	//*******************************************************************
	
	//===================================================================
	// STANDARD UNDO TESTS
	//===================================================================
	
	@Test
	public void undo() {
		String input = "undo";
		AbstractCommand output = parser.parseInput(input);
		UndoCommand expected = new UndoCommand();
		assertEquals(expected, output);
	}
	
	@Test
	public void u() {
		String input = "u";
		AbstractCommand output = parser.parseInput(input);
		UndoCommand expected = new UndoCommand();
		assertEquals(expected, output);
	}
	
	//*******************************************************************
	//*******************************************************************
	// 	FOR HELP COMMAND
	//*******************************************************************
	//*******************************************************************

	@Test
	public void help() {
		String input = "help";
		AbstractCommand output = parser.parseInput(input);
		HelpCommand expected = new HelpCommand();
		assertEquals(expected, output);
	}
	
	//*******************************************************************
	//*******************************************************************
	// 	FOR SAVE COMMAND
	//*******************************************************************
	//*******************************************************************
	
	@Test
	public void save() {
		String input = "save ~/Desktop";
		AbstractCommand output = parser.parseInput(input);
		SaveCommand expected = new SaveCommand("~/Desktop");
		assertEquals(expected, output);
	}
	
	//*******************************************************************
	//*******************************************************************
	// 	FOR EXIT COMMAND
	//*******************************************************************
	//*******************************************************************
	
	@Test
	public void exit() {
		String input = "exit";
		AbstractCommand output = parser.parseInput(input);
		ExitCommand expected = new ExitCommand();
		assertEquals(expected, output);
	}
	
	//*******************************************************************
	//*******************************************************************
	// 	FOR UICOMMAND
	//*******************************************************************
	//*******************************************************************
	
	@Test
	public void day() {
		String input = "day";
		AbstractCommand output = parser.parseInput(input);
		UICommand expected = new UICommand();
		assertEquals(expected, output);
	}
	
	@Test
	public void night() {
		String input = "night";
		AbstractCommand output = parser.parseInput(input);
		UICommand expected = new UICommand();
		assertEquals(expected, output);
	}

	@Test
	public void hideYear() {
		String input = "hide year";
		AbstractCommand output = parser.parseInput(input);
		UICommand expected = new UICommand();
		assertEquals(expected, output);
	}
	
	@Test
	public void showYear() {
		String input = "show year";
		AbstractCommand output = parser.parseInput(input);
		UICommand expected = new UICommand();
		assertEquals(expected, output);
	}
	
	@Test
	public void dayExtra() {
		String input = "day extra";
		AbstractCommand output = parser.parseInput(input);
		InvalidCommand expected = new InvalidCommand();
		assertEquals(expected, output);
	}
	
	@Test
	public void showYearExtra() {
		String input = "show year test something test";
		AbstractCommand output = parser.parseInput(input);
		InvalidCommand expected = new InvalidCommand();
		assertEquals(expected, output);
	}
	
	@Test
	public void nightWhiteSpace() {
		String input = "night ";
		AbstractCommand output = parser.parseInput(input);
		UICommand expected = new UICommand();
		assertEquals(expected, output);
	}
	
	@Test
	public void hideYearWhiteSpaces() {
		String input = "hide year     ";
		AbstractCommand output = parser.parseInput(input);
		UICommand expected = new UICommand();
		assertEquals(expected, output);
	}
	
}
