//@@author A0133888N
package ui.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javafx.animation.FadeTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import logic.Logic;
import shared.Constants;
import shared.Output;
import shared.Output.Priority;
import storage.Storage;
import ui.Main;

public class OverviewController {

	@FXML
	private AnchorPane back;

	@FXML
	private TextField input;

	@FXML
	private Label returnMessageLabel;

	@FXML
	private Text returnMessageText;

	@FXML
	private Label helpMessageLabel;

	@FXML
	private Text helpMessageText;

	@FXML
	private AnchorPane taskPane;

	@FXML
	private ScrollPane taskScrollPane;

	private static Logger logger = Logger.getLogger("UILogger");
	private FileHandler logFile;

	VBox vbox;

	Main mainApp;

	// By default, the year of a date is hidden.
	private boolean isYearShown = false;

	private ReturnMessage returnMessage;
	private HelpMessage helpMessage;
	private InputRecord inputRecord;

	private Storage storage = new Storage();
	private Logic logic = new Logic(storage);

	/**
	 * Initialize components in the UI.
	 */
	@FXML
	public void initialize() {
		initializeVBox();
		initializeTaskScrollPane();
		initializeMessages();
		initializeLog();
		initializeDisplay();
		initializeInputListener();
		initializeInputTrace();
		setFocus(vbox);

	}

	/**
	 * Initialize the Vbox, which contains all the taskViews.
	 */
	private void initializeVBox() {
		vbox = new VBox(3);
		vbox.setPrefWidth(600);
		vbox.setPrefHeight(705);
		vbox.setStyle(String.format("-fx-background-color: %1$s;", Constants.COLOR_DAY));
	}

	private void initializeTaskScrollPane() {
		assert taskScrollPane != null;

		taskScrollPane.setContent(vbox);
		taskScrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
	}

	private void initializeMessages() {
		helpMessage = new HelpMessage(helpMessageLabel, helpMessageText);
		returnMessage = new ReturnMessage(returnMessageLabel, returnMessageText);
	}

	private void initializeLog() {
		try {
			logFile = new FileHandler("log.txt");
			logFile.setFormatter(new SimpleFormatter());
			logger.addHandler(logFile);
		} catch (SecurityException e) {
			System.out.println("A security violation occurs " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Log file not found " + e.getMessage());
		}
	}

	/**
	 * Display the default view.
	 */
	private void initializeDisplay() {
		logger.log(Level.INFO, "going to initialize the overview");

		try {
			Output output = processInput("display");
			Output lastDisplay = processInput("display");

			assert output != null;
			assert lastDisplay != null;
			display(output, lastDisplay);
		} catch (Exception ex) {
			System.err.println("Initialize default display failed " + ex.getMessage());
			logger.log(Level.WARNING, "display command processing error", ex);
		}

		logger.log(Level.INFO, "end of processing initial display command");
	}

	/**
	 * Initialize an input listener, which caters for immediate help messages.
	 */
	private void initializeInputListener() {
		input.textProperty().addListener((observable, oldValue, newValue) -> {
			clearReturnMessage();
			helpMessage.genereateHelpMessage(newValue);
		});
		logger.log(Level.INFO, "Input listener initialized.");
	}

	private void initializeInputTrace() {

		inputRecord = new InputRecord();
		input.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode().equals(KeyCode.UP)) {
					input.setText(inputRecord.showLastInput());

				} else if (event.getCode().equals(KeyCode.DOWN)) {
					input.setText(inputRecord.showNextInput());
				}
			}
		});
		logger.log(Level.INFO, "Input trace initialized.");
	}

	/**
	 * Set the focus always to the input textField, when clicking inside
	 * Flexi-List. Scrolling the tasks still works.
	 */
	private void setFocus(VBox vbox) {
		vbox.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				input.requestFocus();
				event.consume();
			}
		});
	}

	private void clearReturnMessage() {
		if (returnMessageLabel.getText().equals(null) || helpMessageLabel.getText().equals(null)) {
			return;
		}
		if (returnMessage.hasReturnMessage() && helpMessage.hasHelpMessage()) {
			returnMessage.cleanReturnMessage();
		}
	}

	/**
	 * Specify actions when the user types Enter.
	 */
	public void onEnter() {
		returnMessage.cleanReturnMessage();

		try {
			if (isEmptyInput()) {
				return;
			} else if (isQuitHelpInput()) {
				quitHelpView();
			} else if (isHelpInput()) {
				displayFullHelpMessage();
			} else if (isChangeViewInput() && !isInHelpView()) {
				changeView(input.getText());
			} else if (isYearCommand() && !isInHelpView()) {
				displayYear();
			} else {
				getOutput();
			}
			input.clear();
		} catch (Exception e) {
			logger.log(Level.INFO, "Error in handling user input " + e.getMessage());
			System.err.print("Error in handling user input " + e.getMessage());
		}

	}
	
	private boolean isInHelpView() {
		return !taskScrollPane.getContent().equals(vbox);
	}

	private void quitHelpView() {
		taskScrollPane.setContent(vbox);
		back.setStyle("-fx-background-color: " + Constants.COLOR_DAY + ";");
	}

	private void displayFullHelpMessage() {
		back.setStyle("-fx-background-color: #ffffff;");
		FullHelpView fullHelpView = new FullHelpView();
		taskScrollPane.setContent(fullHelpView);
		setFocus(fullHelpView);
		logger.log(Level.INFO, "Chaning to full help view");
	}

	/**
	 * Change background color of Flexi-List.
	 * 
	 * @param viewCommand
	 *            Day command or Night command.
	 */
	private void changeView(String viewCommand) {
		assert viewCommand != null;

		if (viewCommand.equals(Constants.COMMAND_NIGHT)) {
			vbox.setStyle(String.format("-fx-background-color: %1$s;", Constants.COLOR_NIGHT));
			back.setStyle("-fx-background-color: " + Constants.COLOR_NIGHT + ";");
			helpMessage.changeTheme(viewCommand);
			returnMessage.changeTheme(viewCommand);
			logger.log(Level.INFO, "Changing to night theme");
		}

		if (viewCommand.equals(Constants.COMMAND_DAY)) {
			vbox.setStyle(String.format("-fx-background-color: %1$s;", Constants.COLOR_DAY));
			back.setStyle("-fx-background-color: " + Constants.COLOR_DAY + ";");
			helpMessage.changeTheme(viewCommand);
			returnMessage.changeTheme(viewCommand);
			logger.log(Level.INFO, "Changing to day theme");

		}
	}

	/**
	 * Refresh the tasks being displayed currently, with the years shown.
	 * Floating tasks will not be affected.
	 */
	private void displayYear() {
		assert input.getText() != null;
		if (input.getText().equals(Constants.COMMAND_SHOW_YEAR)) {
			isYearShown = true;
		} else {
			isYearShown = false;
		}
		Output output = logic.getLastDisplayed();
		display(output, output);
		logger.log(Level.INFO, "Display year setting toggled");
	}

	private void getOutput() {
		recordInput();
		Output output = processInput(inputRecord.getCommand());
		Output lastDisplay = logic.getLastDisplayed();
		display(output, lastDisplay);
	}

	private void recordInput() {
		String inputString = input.getText();
		assert inputString != null;
		inputRecord.setCommand(inputString);
		inputRecord.addInputRecord(inputRecord.getCommand());
		inputRecord.setNextPointer();
	}

	public Output processInput(String input) {
		try {
			return logic.processInput(input);
		} catch (Exception e) {
			logger.log(Level.INFO, "Fail to obtain output from logic " + e.getMessage());
			System.err.println("Fail to obtain output from logic " + e.getMessage());
		}
		return null;

	}

	private void display(Output output, Output lastDisplay) {

		returnMessage.cleanReturnMessage();
		helpMessage.cleanHelpMessage();

		String message = output.getReturnMessage();
		returnMessage.setReturnMessage(message);

		Priority priority = output.getPriority();
		returnMessage.flashReturnMessage(priority);

		ArrayList<ArrayList<String>> outputArrayList = lastDisplay.getTasks();
		displayTasks(outputArrayList);
	}

	/**
	 * Display tasks vertically. All the fade-in animation works simultaneously.
	 * 
	 * @param outputArrayList
	 */
	private void displayTasks(ArrayList<ArrayList<String>> outputArrayList) {
		vbox.getChildren().clear();

		if (outputArrayList.size() == 0) {
			return;
		}
		for (ArrayList<String> list : outputArrayList) {
			TaskView taskView;
			try {
				taskView = new TaskView(list, isYearShown);
				vbox.getChildren().add(taskView);
				fadeInTaskView(taskView);
			} catch (Exception e) {
				logger.log(Level.INFO, "Error in creating a taskView " + e.getMessage());
				System.err.println("Error in creating a taskView " + e.getMessage());
			}
		}
	}

	/**
	 * Perform a fade-in animation when tasks are displayed.
	 * 
	 * @param taskView
	 */
	private void fadeInTaskView(TaskView taskView) {
		FadeTransition ft = new FadeTransition(Duration.millis(600), taskView);
		ft.setFromValue(0.0);
		ft.setToValue(1.0);
		ft.play();

	}

	private boolean isYearCommand() {
		return input.getText().equals(Constants.COMMAND_SHOW_YEAR)
				|| input.getText().equals(Constants.COMMAND_HIDE_YEAR);
	}

	private boolean isEmptyInput() {
		return input.getText().equals("");
	}

	private boolean isQuitHelpInput() {
		return input.getText().equals(Constants.COMMAND_QUIT_HELP);
	}

	private boolean isHelpInput() {
		return input.getText().equals(Constants.COMMAND_HELP);
	}

	private boolean isChangeViewInput() {
		return input.getText().equals(Constants.COMMAND_DAY) || input.getText().equals(Constants.COMMAND_NIGHT);
	}

	/**
	 * The method is required for changing focus to the text field.
	 */
	public void onClickScrollPane() {
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;

	}

	public Storage getStorage() {
		return this.storage;
	}

	public Logic getLogic() {
		return this.logic;
	}
}
