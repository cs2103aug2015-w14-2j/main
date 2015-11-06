package logic.action;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import logic.ExtendedBoolean;
import logic.TaskList;
import shared.Constants;
import shared.Output;
import shared.Output.Priority;
import shared.command.DisplayCommand;
import shared.command.EditCommand;
import shared.command.EditCommand.Nature;
import shared.command.EditCommand.editField;
import shared.task.AbstractTask;
import shared.task.BoundedTask;
import shared.task.DeadlineTask;
import shared.task.FloatingTask;

//@@author A0124828B
public class EditAction extends AbstractAction {

	private static final String MESSAGE_UPDATE = "\"%1$s\" has been edited!";
	private static final String MESSAGE_UPDATE_WRONG_TYPE = "Invalid: Task specified does not have this operation.";

	private EditCommand editCommand;
	private EditCommand latestComplexEdit;
	private ExtendedBoolean shouldKeepComplexEdit;

	public EditAction(EditCommand editCommand, TaskList taskList,
			TaskList latestDisplayed, DisplayCommand latestDisplayCmd) {
		this.editCommand = editCommand;
		this.taskList = taskList;
		this.latestDisplayedList = latestDisplayed;
		this.latestDisplayCmd = latestDisplayCmd;
	}

	public void setLatestComplexEdit(EditCommand cmd) {
		this.latestComplexEdit = cmd;
	}

	public void setShouldKeepComplexEdit(ExtendedBoolean shouldKeepComplexEdit2) {
		this.shouldKeepComplexEdit = shouldKeepComplexEdit2;
	}

	public Output execute() {
		switch (this.editCommand.getType()) {
		case INDEX:
			return editByIndex(this.editCommand);
		case SEARCHKEYWORD:
			return editByKeyword(this.editCommand);
		default:
			// should not reach this code
			Output feedback = new Output(Constants.MESSAGE_INVALID_COMMAND);
			feedback.setPriority(Priority.HIGH);
			return feedback;
		}
	}

	private Output editByIndex(EditCommand parsedCmd) {
		assert (parsedCmd.getIndex() > 0);

		if (parsedCmd.getIndex() > latestDisplayedList.size()) {
			Output feedback = new Output(Constants.MESSAGE_INVALID_COMMAND);
			feedback.setPriority(Priority.HIGH);
			return feedback;
		}
		int inputTaskIndex = parsedCmd.getIndex() - 1;
		AbstractTask taskToEdit = latestDisplayedList.getTask(inputTaskIndex);
		String originalName = taskToEdit.getName();
		int taskIndexInTaskList = taskList.indexOf(taskToEdit);
		AbstractTask actualTaskToEdit = taskList.getTask(taskIndexInTaskList);
		try {
			if (latestComplexEdit.getNature() == Nature.COMPLEX) {
				performEdit(latestComplexEdit, actualTaskToEdit);
			}
			performEdit(parsedCmd, actualTaskToEdit);
		} catch (IllegalArgumentException e) {
			// Happens when user tries to set start or end date that violates
			// chronological order
			return new Output(e);
		} catch (ClassCastException e) {
			// Happens when user tries to edit a non-existent field in task
			// e.g. edit start time of floating task
			Output feedback = new Output(MESSAGE_UPDATE_WRONG_TYPE);
			feedback.setPriority(Priority.HIGH);
			return feedback;
		}
		return new Output(MESSAGE_UPDATE, originalName);
	}

	private Output editByKeyword(EditCommand parsedCmd) {
		String keyword = parsedCmd.getSearchKeyword();
		TaskList filteredList = this.taskList.filterByName(keyword);
		if (filteredList.size() == 0) {
			Output feedback = new Output(Constants.MESSAGE_INVALID_KEYWORD, keyword);
			feedback.setPriority(Priority.HIGH);
			return feedback;
		} else if (filteredList.size() == 1
				&& filteredList.getTask(0).getName().equals(keyword)) {
			AbstractTask uniqueTask = filteredList.getTask(0);
			String originalName = uniqueTask.getName();
			try {
				performEdit(parsedCmd, uniqueTask);
			} catch (IllegalArgumentException e) {
				// Happens when user tries to set start or end date that
				// violates chronological order
				return new Output(e);
			} catch (ClassCastException e) {
				// Happens when user tries to edit a non-existent field in task
				// e.g. edit start time of floating task
				Output feedback = new Output(MESSAGE_UPDATE_WRONG_TYPE);
				feedback.setPriority(Priority.HIGH);
				return feedback;
			}
			return new Output(MESSAGE_UPDATE, originalName);
		} else {
			// record down additional content given by user
			parsedCmd.setNature(Nature.COMPLEX);
			latestComplexEdit.replaceCmd(parsedCmd);
			shouldKeepComplexEdit.setTrue();
			ArrayList<String> keywords = new ArrayList<String>();
			keywords.add(keyword);
			DisplayCommand searchCmd = new DisplayCommand(keywords);
			DisplayAction searchAction = new DisplayAction(searchCmd, taskList,
					latestDisplayedList, latestDisplayCmd);
			return searchAction.execute();
		}
	}

	/*
	 * Helper methods for editing task fields
	 */

	private void performEdit(EditCommand parsedCmd, AbstractTask taskToEdit)
			throws ClassCastException, IllegalArgumentException {
		ArrayList<editField> editFields = parsedCmd.getEditFields();
		if (taskToEdit instanceof BoundedTask) {
			performBoundedEdit(parsedCmd, (BoundedTask) taskToEdit);
			return;
		}
		if (editFields == null) {
			// only happens with two part edit
			return;
		}
		for (int i = 0; i < editFields.size(); i++) {
			try {
				if (editFields.get(i) == editField.NAME) {
					editTaskName(taskToEdit, parsedCmd.getNewName());
				} else if (editFields.get(i) == editField.START_DATE) {
					editStartDate(taskToEdit, parsedCmd.getNewStartDate());
				} else if (editFields.get(i) == editField.START_TIME) {
					editStartTime(taskToEdit, parsedCmd.getNewStartTime());
				} else if (editFields.get(i) == editField.END_DATE) {
					editEndDate(taskToEdit, parsedCmd.getNewEndDate());
				} else if (editFields.get(i) == editField.END_TIME) {
					editEndTime(taskToEdit, parsedCmd.getNewEndTime());
				}
			} catch (Exception e) {
				throw e;
			}
		}
	}

	private void performBoundedEdit(EditCommand parsedCmd,
			BoundedTask taskToEdit) throws ClassCastException,
			IllegalArgumentException {
		ArrayList<editField> editFields = parsedCmd.getEditFields();
		if (editFields == null) {
			// only happens with two part edit
			return;
		}
		LocalDateTime newStart;
		LocalDateTime newEnd;
		DateTimeFormatter DFormatter = DateTimeFormatter
				.ofPattern("dd MM yyyy");
		DateTimeFormatter TFormatter = DateTimeFormatter.ofPattern("HH mm");
		newStart = taskToEdit.getStartDateTime();
		newEnd = taskToEdit.getEndDateTime();
		for (int i = 0; i < editFields.size(); i++) {
			try {

				if (editFields.get(i) == editField.NAME) {
					editTaskName(taskToEdit, parsedCmd.getNewName());
				} else if (editFields.get(i) == editField.START_DATE) {
					LocalDate newDate = LocalDate.parse(
							parsedCmd.getNewStartDate(), DFormatter);
					newStart = newStart.withDayOfMonth(newDate.getDayOfMonth());
					newStart = newStart.withMonth(newDate.getMonthValue());
					newStart = newStart.withYear(newDate.getYear());
				} else if (editFields.get(i) == editField.START_TIME) {
					LocalTime newTime = LocalTime.parse(
							parsedCmd.getNewStartTime(), TFormatter);
					newStart = newStart.withHour(newTime.getHour());
					newStart = newStart.withMinute(newTime.getMinute());
				} else if (editFields.get(i) == editField.END_DATE) {
					LocalDate newDate = LocalDate.parse(
							parsedCmd.getNewEndDate(), DFormatter);
					newEnd = newEnd.withDayOfMonth(newDate.getDayOfMonth());
					newEnd = newEnd.withMonth(newDate.getMonthValue());
					newEnd = newEnd.withYear(newDate.getYear());
				} else if (editFields.get(i) == editField.END_TIME) {
					LocalTime newTime = LocalTime.parse(
							parsedCmd.getNewEndTime(), TFormatter);
					newEnd = newEnd.withHour(newTime.getHour());
					newEnd = newEnd.withMinute(newTime.getMinute());
				}
			} catch (Exception e) {
				throw e;
			}
		}
		if (newStart.isAfter(taskToEdit.getEndDateTime())) {
			taskToEdit.setEndDate(newEnd.format(DFormatter));
			taskToEdit.setEndTime(newEnd.format(TFormatter));
			taskToEdit.setStartDate(newStart.format(DFormatter));
			taskToEdit.setStartTime(newStart.format(TFormatter));
		} else {
			taskToEdit.setStartDate(newStart.format(DFormatter));
			taskToEdit.setStartTime(newStart.format(TFormatter));
			taskToEdit.setEndDate(newEnd.format(DFormatter));
			taskToEdit.setEndTime(newEnd.format(TFormatter));
		}

	}

	private void editTaskName(AbstractTask task, String name) {
		task.setName(name);
	}

	private void editStartDate(AbstractTask task, String startDate)
			throws ClassCastException, IllegalArgumentException {
		((BoundedTask) task).setStartDate(startDate);
	}

	private void editStartTime(AbstractTask task, String startTime)
			throws ClassCastException, IllegalArgumentException {
		((BoundedTask) task).setStartTime(startTime);
	}

	private void editEndDate(AbstractTask task, String endDate)
			throws ClassCastException, IllegalArgumentException {
		if (task instanceof FloatingTask) {
			throw new ClassCastException();
		} else if (task instanceof DeadlineTask) {
			((DeadlineTask) task).setEndDate(endDate);
		} else if (task instanceof BoundedTask) {
			((BoundedTask) task).setEndDate(endDate);
		}
	}

	private void editEndTime(AbstractTask task, String endTime)
			throws ClassCastException, IllegalArgumentException {
		if (task instanceof FloatingTask) {
			throw new ClassCastException();
		} else if (task instanceof DeadlineTask) {
			((DeadlineTask) task).setEndTime(endTime);
		} else if (task instanceof BoundedTask) {
			((BoundedTask) task).setEndTime(endTime);
		}
	}

}
