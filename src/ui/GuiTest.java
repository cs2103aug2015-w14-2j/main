package ui;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.loadui.testfx.Assertions.verifyThat;
import static org.loadui.testfx.controls.Commons.hasText;

import ui.view.OverviewController;

public class GuiTest {
	
	Main main = new Main();
	OverviewController overviewController = new OverviewController();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// set up TextBuddy
	}

	@Test
	public void testInvalidCommand() {
		overviewController.setMainApp(main);
		assertEquals("Invalid Command!", overviewController.processInput("invalid").getReturnMessage());
	}

}
