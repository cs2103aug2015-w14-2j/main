package logic;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import shared.task.AbstractTask;
import shared.task.BoundedTask;
import shared.task.DeadlineTask;
import shared.task.AbstractTask.Status;

//@@author A0124828B
public class TaskList {

	private ArrayList<AbstractTask> tasks;
	
	public TaskList() {
		this.tasks = new ArrayList<AbstractTask>();
	}
	
	public TaskList(ArrayList<AbstractTask> tasks) {
		this.tasks = tasks;
	}
	
	public void addTask(AbstractTask task) {
		assert task != null;
		this.tasks.add(task);
	}
	
	public void addTask(int index, AbstractTask task) {
		assert index > -1 && index < tasks.size();
		assert task != null;
		this.tasks.add(index, task);
	}
	
	public void addAll(TaskList taskList) {
		this.tasks.addAll(taskList.getTasks());
	}
	
	public AbstractTask getTask(int index) {
		assert index > -1 && index < tasks.size();
		return this.tasks.get(index);
	}
	
	public ArrayList<AbstractTask> getTasks() {
		assert this.tasks != null;
		return this.tasks;
	}
	
	public int indexOf(AbstractTask task) {
		assert task != null;
		return this.tasks.indexOf(task);
	}
	
	public void removeTask(AbstractTask task) {
		assert task != null;
		this.tasks.remove(task);
	}
	
	public void clear() {
		this.tasks.clear();
	}
	
	public void replaceContents(TaskList newContents) {
		this.tasks = newContents.getTasks();
	}
	
	public TaskList clone() {
		TaskList clonedList = new TaskList();
		for (AbstractTask task : this.tasks) {
			clonedList.addTask(task.clone());
		}
		return clonedList;
	}
	
	public TaskList subList(int indexStart, int indexEnd) {
		List<AbstractTask> subList = this.tasks.subList(indexStart, indexEnd);
		ArrayList<AbstractTask> arraySubList = new ArrayList<AbstractTask>(subList);
		return new TaskList(arraySubList);
	}
	
	public int size() {
		return this.tasks.size();
	}
	
	/*
	 * Filter functions
	 */
	
	public TaskList getDateSortedClone() {
		TaskList clonedList = this.clone();
		Collections.sort(clonedList.getTasks());
		return clonedList;
	}
	
	public TaskList filterByName(String keyword) {
		TaskList filteredList = new TaskList();
		for (AbstractTask task : this.tasks) {
			if (task.getName().contains(keyword)) {
				filteredList.addTask(task);
			}
		}
		return filteredList;
	}

	public TaskList filterByDate(LocalDate queryDate) {
		TaskList filteredList = new TaskList();
		for (AbstractTask task : this.tasks) {
			if (task instanceof DeadlineTask
					&& isSameDate((DeadlineTask) task, queryDate)) {
				filteredList.addTask(task);
			} else if (task instanceof BoundedTask
					&& isSameDate((BoundedTask) task, queryDate)) {
				filteredList.addTask(task);
			}
		}
		return filteredList;
	}

	public TaskList filterInclusiveAfterDate(LocalDate queryDate) {
		TaskList sortedMasterList = this.getDateSortedClone();
		TaskList filteredList = new TaskList();
		for (AbstractTask task : sortedMasterList.getTasks()) {
			if (task instanceof DeadlineTask
					&& isInclusiveAfterDate((DeadlineTask) task, queryDate)) {
				filteredList.addTask(task);
			} else if (task instanceof BoundedTask
					&& isInclusiveAfterDate((BoundedTask) task, queryDate)) {
				filteredList.addTask(task);
			}
		}
		return filteredList;
	}

	public TaskList filterByStatus(Status status) {
		TaskList filteredList = new TaskList();
		for (AbstractTask task : this.tasks) {
			if (task.getStatus().equals(status)) {
				filteredList.addTask(task);
			}
		}
		return filteredList;
	}

	public TaskList filterByOverdue(boolean state) {
		TaskList filteredList = new TaskList();
		for (AbstractTask task : this.tasks) {
			if (task instanceof DeadlineTask) {
				DeadlineTask deadlineTask = (DeadlineTask) task;
				if (deadlineTask.isOverdue() == state) {
					filteredList.addTask(deadlineTask);
				}
			}
		}
		return filteredList;
	}

	private boolean isSameDate(DeadlineTask task, LocalDate queryDate) {
		return Objects.equals(task.getEndDateTime().toLocalDate(), queryDate);
	}

	private boolean isSameDate(BoundedTask task, LocalDate queryDate) {
		boolean startDateCheck = Objects.equals(task.getStartDateTime()
				.toLocalDate(), queryDate);
		boolean endDateCheck = Objects.equals(task.getEndDateTime()
				.toLocalDate(), queryDate);
		return startDateCheck || endDateCheck;
	}

	private boolean isInclusiveAfterDate(DeadlineTask task, LocalDate queryDate) {
		return task.getEndDateTime().toLocalDate().isAfter(queryDate)
				|| isSameDate(task, queryDate);
	}

	private boolean isInclusiveAfterDate(BoundedTask task, LocalDate queryDate) {
		return task.getStartDateTime().toLocalDate().isAfter(queryDate)
				|| isSameDate(task, queryDate);
	}
	
	/*
	 * For comparison
	 */
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TaskList)) {
			return false;
		} else {
			TaskList that = (TaskList) obj;
			return Objects.equals(this.getTasks(), that.getTasks());
		}
	}
}
