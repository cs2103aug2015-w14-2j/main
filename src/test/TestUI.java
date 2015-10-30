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
	public static void sleep() {
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testInputClear() {
		sleep();
		controller.type("delete all");
		controller.push(KeyCode.ENTER);	
		controller.type("create assignment 1");
		controller.push(KeyCode.ENTER);	
		verifyThat("#input", hasText(""));
	}

	
	@Test
	public void testInvalid() {
		sleep();
		controller.type("delete all");
		controller.push(KeyCode.ENTER);	
		controller.type("an invalid input");
		controller.push(KeyCode.ENTER);	
		verifyThat("#returnMessage", hasText("Invalid Command!"));
	}
	
	@Test
	public void testCreate() {
		sleep();
		controller.type("delete all");
		controller.push(KeyCode.ENTER);	
		controller.type("create 1");
		controller.push(KeyCode.ENTER);	
		verifyThat("#returnMessage", hasText("\"1\" has been created!"));
	}
	
	@Test
	public void testDeleteAll() {
		sleep();
		controller.type("delete all");
		controller.push(KeyCode.ENTER);	
		verifyThat("#returnMessage", hasText("All tasks have been deleted!"));
	}
	
	@Test
	public void testDeleteOne() {
		sleep();
		controller.type("delete all");
		controller.push(KeyCode.ENTER);	
		controller.type("create 1");
		controller.push(KeyCode.ENTER);	
		controller.type("delete 1");
		controller.push(KeyCode.ENTER);	
		verifyThat("#returnMessage", hasText("\"1\" has been deleted!"));
	}
	
	@Test
	public void testEditByIndex() {
		sleep();
		controller.type("delete all");
		controller.push(KeyCode.ENTER);	
		controller.type("create 1");
		controller.push(KeyCode.ENTER);	
		controller.type("edit 1 to 2");
		controller.push(KeyCode.ENTER);	
		verifyThat("#returnMessage", hasText("\"1\" has been edited!"));
	}



}
