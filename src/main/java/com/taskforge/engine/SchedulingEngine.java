
package com.taskforge.engine;

import com.taskforge.model.Task;
import com.taskforge.model.Priority;
import com.taskforge.model.TaskStatus;
import com.taskforge.service.TaskService;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class SchedulingEngine {

    private final TaskService taskService;

    public SchedulingEngine(TaskService taskService) {
        this.taskService = taskService;
    }

    // Determine the urgency of a task
    public TaskUrgency getUrgency(Task task) {

        if (task.getDeadline() == null) {
            return TaskUrgency.NO_DEADLINE;
        }

        LocalDate today = LocalDate.now();
        LocalDate deadline = task.getDeadline();

        if (deadline.isBefore(today)) {
            return TaskUrgency.OVERDUE;
        }

        if (deadline.isEqual(today)) {
            return TaskUrgency.DUE_TODAY;
        }

        long daysLeft = today.until(deadline).getDays();

        if (daysLeft <= 3) {
            return TaskUrgency.HIGH;
        }

        if (daysLeft <= 7) {
            return TaskUrgency.MEDIUM;
        }

        return TaskUrgency.LOW;
    }

    // Calculate a task's priority score
    public int getPriorityScore(Task task) {

        int score = 0;

        if (task.getPriority() == Priority.LOW) {
            score += 10;
        } else if (task.getPriority() == Priority.MEDIUM) {
            score += 25;
        } else if (task.getPriority() == Priority.HIGH) {
            score += 40;
        } else if (task.getPriority() == Priority.CRITICAL) {
            score += 55;
        }

        TaskUrgency urgency = getUrgency(task);

        switch (urgency) {

            case OVERDUE -> score += 50;
            case DUE_TODAY -> score += 45;
            case HIGH -> score += 30;
            case MEDIUM -> score += 20;
            case LOW -> score += 10;
            case NO_DEADLINE -> score += 0;
        }

        return score;
    }

    // Get tasks in recommended order
    public List<Task> getTasksByRecommendedOrder() {

        return taskService.getReadyTasks()
                .stream()
                .sorted(
                        Comparator
                                .comparingInt(this::getPriorityScore)
                                .reversed()
                                .thenComparing(
                                        Task::getDeadline,
                                        Comparator.nullsLast(
                                                Comparator.naturalOrder()
                                        )
                                )
                )
                .toList();
    }

    // Recommend the best task to work on next
    public Optional<Task> getRecommendedNextTask() {

        List<Task> recommendedTasks =
                getTasksByRecommendedOrder();

        if (recommendedTasks.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(recommendedTasks.get(0));
    }

    // Get all overdue tasks
    public List<Task> getOverdueTasks() {

        LocalDate today = LocalDate.now();

        return taskService.getAllTasks()
                .stream()
                .filter(task -> task.getDeadline() != null)
                .filter(task -> task.getDeadline().isBefore(today))
                .filter(task ->
                        task.getStatus() != TaskStatus.COMPLETED
                        && task.getStatus() != TaskStatus.CANCELLED
                )
                .toList();
    }
}
