package test;

import static org.junit.Assert.assertTrue;

import static org.loadui.testfx.Assertions.verifyThat;
import org.loadui.testfx.GuiTest;
import org.loadui.testfx.utils.FXTestUtils;
import static org.loadui.testfx.controls.Commons.hasText;

import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import ui.Main;

import org.junit.BeforeClass;
import org.junit.Test;

//[IMPORTANT] Please do not move your mouse or type during the tests!

public class TestUI {
	private static GuiTest controller;
	private static Main mainApp;

	@BeforeClass
	public static void setUpClass() {
		FXTestUtils.launchApp(Main.class);
		
		controller = new GuiTest() {
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
	public void testInputClear() {
		pause();
		controller.type("delete all");
		controller.push(KeyCode.ENTER);	
		controller.type("create assignment 1");
		controller.push(KeyCode.ENTER);	
		verifyThat("#input", hasText(""));
	}

	
	@Test
	public void testInvalid() {
		pause();
		controller.type("delete all");
		controller.push(KeyCode.ENTER);	
		controller.type("an invalid input");
		controller.push(KeyCode.ENTER);	
		verifyThat("#returnMessageLabel", hasText("Invalid Command!"));
	}
	
	@Test
	public void testCreate() {
		pause();
		controller.type("delete all");
		controller.push(KeyCode.ENTER);	
		controller.type("create 1");
		controller.push(KeyCode.ENTER);	
		verifyThat("#returnMessageLabel", hasText("\"1\" has been created!"));
	}
	
	@Test
	public void testDeleteAll() {
		pause();
		controller.type("delete all");
		controller.push(KeyCode.ENTER);	
		verifyThat("#returnMessageLabel", hasText("All tasks have been deleted!"));
	}
	
	@Test
	public void testDeleteOne() {
		pause();
		controller.type("delete all");
		controller.push(KeyCode.ENTER);	
		controller.type("create 1");
		controller.push(KeyCode.ENTER);	
		controller.type("delete 1");
		controller.push(KeyCode.ENTER);	
		verifyThat("#returnMessageLabel", hasText("\"1\" has been deleted!"));
	}
	
	@Test
	public void testEditByIndex() {
		pause();
		controller.type("delete all");
		controller.push(KeyCode.ENTER);	
		controller.type("create 1");
		controller.push(KeyCode.ENTER);	
		controller.type("edit 1 to 2");
		controller.push(KeyCode.ENTER);	
		verifyThat("#returnMessageLabel", hasText("\"1\" has been edited!"));
	}

	@Test
	public void testDisplayAll() {
		pause();
		controller.type("create 1");
		controller.push(KeyCode.ENTER);	
		controller.type("display all");
		controller.push(KeyCode.ENTER);	
		verifyThat("#returnMessageLabel", hasText("All tasks are now displayed!"));
	}
	
	@Test
	public void testDisplayNothing() {
		pause();
		controller.type(" delete all");
		controller.push(KeyCode.ENTER);	
		controller.type("display all");
		controller.push(KeyCode.ENTER);	
		verifyThat("#returnMessageLabel", hasText("There are no tasks to display :'("));
	}
	
	@Test
	public void testUndo() {
		pause();
		controller.type("create 1");
		controller.push(KeyCode.ENTER);	
		controller.type("undo");
		controller.push(KeyCode.ENTER);	
		verifyThat("#returnMessageLabel", hasText("\"create\" action has been undone!"));
	}
	
	@Test
	public void testMark() {
		pause();
		controller.type("delete all");
		controller.push(KeyCode.ENTER);	
		controller.type("create a task");
		controller.push(KeyCode.ENTER);	
		controller.type("mark 1");
		controller.push(KeyCode.ENTER);	
		verifyThat("#returnMessageLabel", hasText("\"a task\" has been marked done."));
	}


}
