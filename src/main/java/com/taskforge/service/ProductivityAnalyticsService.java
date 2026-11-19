
package com.taskforge.service;

import com.taskforge.model.Priority;
import com.taskforge.model.Task;
import com.taskforge.model.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductivityAnalyticsService {

    private final TaskService taskService;
    private final TimeTrackingService timeTrackingService;

    public ProductivityAnalyticsService(
            TaskService taskService,
            TimeTrackingService timeTrackingService) {

        if (taskService == null) {
            throw new IllegalArgumentException("Task service cannot be null.");
        }

        if (timeTrackingService == null) {
            throw new IllegalArgumentException("Time tracking service cannot be null.");
        }

        this.taskService = taskService;
        this.timeTrackingService = timeTrackingService;
    }

    public Map<TaskStatus, Long> getTasksByStatusCount() {

        return taskService.getAllTasks().stream()
                .collect(
                        Collectors.groupingBy(
                                Task::getStatus,
                                Collectors.counting()
                        )
                );
    }

    public Map<Priority, Long> getTasksByPriorityCount() {

        return taskService.getAllTasks().stream()
                .collect(
                        Collectors.groupingBy(
                                Task::getPriority,
                                Collectors.counting()
                        )
                );
    }

    public List<Task> getCompletedTasks() {

        return taskService.getAllTasks().stream()
                .filter(task -> task.getStatus() == TaskStatus.COMPLETED)
                .sorted(Comparator.comparing(Task::getCreatedAt))
                .collect(Collectors.toList());
    }

    public List<Task> getOverdueTasks() {

        return taskService.getAllTasks().stream()
                .filter(task -> task.getDeadline() != null)
                .filter(task -> task.getDeadline().isBefore(LocalDate.now()))
                .filter(task -> task.getStatus() != TaskStatus.COMPLETED
                        && task.getStatus() != TaskStatus.CANCELLED)
                .collect(Collectors.toList());
    }

    public double getCompletionRate() {

        List<Task> allTasks = taskService.getAllTasks();

        if (allTasks.isEmpty()) {
            return 0.0;
        }

        long completed = allTasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.COMPLETED)
                .count();

        return (double) completed / allTasks.size() * 100.0;
    }

    public Map<String, Long> getTimeSpentByTask() {

        return taskService.getAllTasks().stream()
                .collect(
                        Collectors.toMap(
                                Task::getId,
                                task -> timeTrackingService.getTotalTimeSpentOnTask(task.getId())
                        )
                );
    }

    public long getTotalTimeTrackedMinutes() {

        return taskService.getAllTasks().stream()
                .mapToLong(task -> timeTrackingService.getTotalTimeSpentOnTask(task.getId()))
                .sum();
    }

    public long getTimeTrackedTodayMinutes() {

        return timeTrackingService.getTotalTimeSpentToday();
    }

    public List<Task> getTasksCompletedToday() {

        LocalDate today = LocalDate.now();

        return taskService.getAllTasks().stream()
                .filter(task -> task.getStatus() == TaskStatus.COMPLETED)
                .filter(task -> task.getCreatedAt().toLocalDate().isEqual(today))
                .collect(Collectors.toList());
    }

    public int getTotalTaskCount() {
        return taskService.getAllTasks().size();
    }

    public int getActiveTaskCount() {

        return (int) taskService.getAllTasks().stream()
                .filter(task -> task.getStatus() == TaskStatus.TODO
                        || task.getStatus() == TaskStatus.IN_PROGRESS)
                .count();
    }
}
