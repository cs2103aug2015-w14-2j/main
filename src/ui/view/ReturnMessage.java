//@@author A0133888N
package ui.view;

import javafx.animation.FillTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import shared.Output.Priority;

/**
 * This class is used to display return messages.
 */
public class ReturnMessage {

	private static final int MESSAGE_FONT = 14;

	private Label returnMessageLabel;
	private Text returnMessageText;
	private String returnMessage;

	public ReturnMessage(Label returnMessageLabel, Text returnMessageText) {
		initialize(returnMessageLabel, returnMessageText);
	}

	private void initialize(Label returnMessageLabel, Text returnMessageText) {
		this.returnMessageLabel = returnMessageLabel;
		this.returnMessageText = returnMessageText;
		this.returnMessageLabel.setFont(Font.font(MESSAGE_FONT));
		this.returnMessageText.setFont(Font.font(MESSAGE_FONT));
		returnMessageLabel.setTextFill(Color.TRANSPARENT);
	}

	protected void setReturnMessage(String returnMessage) {
		if (returnMessage == null) {
			this.returnMessage = "";
		} else {
			this.returnMessage = returnMessage;
		}

		returnMessageLabel.setText(this.returnMessage);
		returnMessageText.setText(this.returnMessage);

	}

	/**
	 * If the priority of a return message is high, flash it in red. 
	 * If the priority of a return message is low, flash it in green.
	 * 
	 * @param priority
	 */
	protected void flashReturnMessage(Priority priority) {
		Color color;

		switch (priority) {
		case LOW:
			color = Color.GREEN;
			break;
		case HIGH:
			color = Color.RED;
			break;
		default:
			color = Color.BLACK;
			break;
		}

		FillTransition textWait = new FillTransition(Duration.millis(600), returnMessageText, Color.BLACK, Color.BLACK);
		textWait.setCycleCount(1);

		FillTransition textHighlight = new FillTransition(Duration.millis(1200), returnMessageText, Color.BLACK, color);
		textHighlight.setCycleCount(1);

		FillTransition textBlack = new FillTransition(Duration.millis(1200), returnMessageText, color, Color.BLACK);
		textBlack.setCycleCount(1);

		SequentialTransition sT = new SequentialTransition(textWait, textHighlight, textBlack);
		sT.play();
	}

	protected void cleanReturnMessage() {
		returnMessageLabel.setText("");
		returnMessageText.setText("");
		returnMessage = "";
	}

	protected boolean hasReturnMessage() {
		return returnMessageLabel.getText().length() > 0;
	}

}
