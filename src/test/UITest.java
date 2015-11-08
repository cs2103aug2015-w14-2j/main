//@@author A0133888N
package test;

import static org.loadui.testfx.Assertions.verifyThat;
import static org.loadui.testfx.controls.Commons.hasText;

import org.junit.BeforeClass;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.loadui.testfx.utils.FXTestUtils;

import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import shared.Constants;
import ui.Main;

//[IMPORTANT] Please do not move your mouse or type during the tests!

public class UITest {
	private static GuiTest uiController;
	private static Main mainApp;

	@BeforeClass
	public static void setUpClass() {
		FXTestUtils.launchApp(Main.class);
		
		uiController = new GuiTest() {
			@Override
			protected Parent getRootNode() {
				return mainApp.getPrimaryStage().getScene().getRoot();
			}
		};
	}
	
	/**
	 * The app needs a short time to start, while the testing starts right away.
	 * Therefore, there need to be a bit buffer time, or the test input is not entered in
	 * the app, but somewhere else (e.g. This file), leading to test failure.
	 */
	public static void pause() {
		try {
			Thread.sleep(350);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void uiTest() {
		testInputClear();
		testInvalid();
		testCreate();
		testImmediateHelpMessage();
		testMark();
		testGetLastInput();
		testDeleteAll();
		testDeleteOne();
		testEditByIndex();
		testDisplayAll();
		testDisplayNothing();
		testUndo();
	}

	public void testInputClear() {
		pause();
		uiController.type("tests start");
		uiController.push(KeyCode.ENTER);
		uiController.type("create assignment 1");
		uiController.push(KeyCode.ENTER);
		verifyThat("#input", hasText(""));
	}

	public void testInvalid() {
		pause();
		uiController.type("delete all");
		uiController.push(KeyCode.ENTER);
		uiController.type("an invalid input");
		uiController.push(KeyCode.ENTER);
		verifyThat("#returnMessageLabel", hasText("Invalid Command!"));
	}

	public void testCreate() {
		pause();
		uiController.type("delete all");
		uiController.push(KeyCode.ENTER);
		uiController.type("create event1 from 10am today to 12pm tmr");
		uiController.push(KeyCode.ENTER);
		verifyThat("#returnMessageLabel",
				hasText("\"event1\" has been created!"));
		uiController.type("create event2 from 10am today to 12pm today");
		uiController.push(KeyCode.ENTER);
		verifyThat("#returnMessageLabel",
				hasText("\"event2\" has been created!"));
		uiController.type("create event3 by 11pm today");
		uiController.push(KeyCode.ENTER);
		verifyThat("#returnMessageLabel",
				hasText("\"event3\" has been created!"));
	}

	public void testImmediateHelpMessage() {
		pause();
		uiController.push(KeyCode.ENTER);
		uiController.type("create");
		verifyThat("#helpMessageLabel", hasText(Constants.HELP_MESSAGE_CREATE));
		uiController.push(KeyCode.ENTER);
		uiController.type("edit");
		verifyThat("#helpMessageLabel", hasText(Constants.HELP_MESSAGE_EDIT));
		uiController.push(KeyCode.ENTER);
		uiController.type("display");
		verifyThat("#helpMessageLabel", hasText(Constants.HELP_MESSAGE_DISPLAY));
		uiController.push(KeyCode.ENTER);
		uiController.type("delete");
		verifyThat("#helpMessageLabel", hasText(Constants.HELP_MESSAGE_DELETE));
		uiController.push(KeyCode.ENTER);
		uiController.type("undo");
		verifyThat("#helpMessageLabel", hasText(Constants.HELP_MESSAGE_UNDO));
		uiController.push(KeyCode.ENTER);
		uiController.type("mark");
		verifyThat("#helpMessageLabel", hasText(Constants.HELP_MESSAGE_MARK));
		uiController.push(KeyCode.ENTER);
		uiController.type("search");
		verifyThat("#helpMessageLabel", hasText(Constants.HELP_MESSAGE_SEARCH));
		uiController.push(KeyCode.ENTER);
		uiController.type("save");
		verifyThat("#helpMessageLabel", hasText(Constants.HELP_MESSAGE_SAVE));
		uiController.push(KeyCode.ENTER);
		uiController.type("help");
		verifyThat("#helpMessageLabel", hasText(Constants.HELP_MESSAGE_HELP));
		uiController.push(KeyCode.ENTER);
		uiController.type("quit help");
		uiController.push(KeyCode.ENTER);
	}

	private void testMark() {
		pause();
		uiController.type("delete all");
		uiController.push(KeyCode.ENTER);
		uiController.type("create a task");
		uiController.push(KeyCode.ENTER);
		uiController.type("mark 1");
		uiController.push(KeyCode.ENTER);
		verifyThat("#returnMessageLabel",
				hasText("\"a task\" has been marked done."));
	}

	private void testGetLastInput() {
		pause();
		uiController.type("delete all");
		uiController.push(KeyCode.ENTER);
		uiController.type("create meeting");
		uiController.push(KeyCode.ENTER);
		uiController.push(KeyCode.UP);
		verifyThat("#input", hasText("create meeting"));
		uiController.push(KeyCode.DOWN);
		verifyThat("#input", hasText(""));
	}
	
	public void testDeleteAll() {
		pause();
		uiController.type("delete all");
		uiController.push(KeyCode.ENTER);	
		verifyThat("#returnMessageLabel", hasText("All tasks have been deleted!"));
	}
	
	public void testDeleteOne() {
		pause();
		uiController.type("delete all");
		uiController.push(KeyCode.ENTER);	
		uiController.type("create 1");
		uiController.push(KeyCode.ENTER);	
		uiController.type("delete 1");
		uiController.push(KeyCode.ENTER);	
		verifyThat("#returnMessageLabel", hasText("\"1\" has been deleted!"));
	}
	
	public void testEditByIndex() {
		pause();
		uiController.type("delete all");
		uiController.push(KeyCode.ENTER);	
		uiController.type("create 1");
		uiController.push(KeyCode.ENTER);	
		uiController.type("edit 1 to 2");
		uiController.push(KeyCode.ENTER);	
		verifyThat("#returnMessageLabel", hasText("\"1\" has been edited!"));
	}

	public void testDisplayAll() {
		pause();
		uiController.type("create 1");
		uiController.push(KeyCode.ENTER);	
		uiController.type("display all");
		uiController.push(KeyCode.ENTER);	
		verifyThat("#returnMessageLabel", hasText("All tasks are now displayed!"));
	}
	
	public void testDisplayNothing() {
		pause();
		uiController.type("delete all");
		uiController.push(KeyCode.ENTER);	
		uiController.type("display all");
		uiController.push(KeyCode.ENTER);	
		verifyThat("#returnMessageLabel", hasText("There are no tasks to display :'("));
	}

	public void testUndo() {
		pause();
		uiController.type("create 1");
		uiController.push(KeyCode.ENTER);	
		uiController.type("undo");
		uiController.push(KeyCode.ENTER);	
		verifyThat("#returnMessageLabel", hasText("\"create\" action has been undone!"));
	}
	


}
