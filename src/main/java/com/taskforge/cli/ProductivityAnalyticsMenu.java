
package com.taskforge.cli;

import com.taskforge.model.Priority;
import com.taskforge.model.Task;
import com.taskforge.model.TaskStatus;
import com.taskforge.service.ProductivityAnalyticsService;
import com.taskforge.service.TaskService;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ProductivityAnalyticsMenu {

    private final ProductivityAnalyticsService analyticsService;
    private final TaskService taskService;
    private final Scanner scanner;

    public ProductivityAnalyticsMenu(
            ProductivityAnalyticsService analyticsService,
            TaskService taskService,
            Scanner scanner) {

        this.analyticsService = analyticsService;
        this.taskService = taskService;
        this.scanner = scanner;
    }

    public void display() {

        boolean running = true;

        while (running) {

            System.out.println("\n========== PRODUCTIVITY ANALYTICS ==========");
            System.out.println("1. Task Summary");
            System.out.println("2. Tasks by Status");
            System.out.println("3. Tasks by Priority");
            System.out.println("4. Overdue Tasks");
            System.out.println("5. Completed Tasks");
            System.out.println("6. Time Tracking Summary");
            System.out.println("7. Return to Main Menu");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1" -> showTaskSummary();

                case "2" -> showTasksByStatus();

                case "3" -> showTasksByPriority();

                case "4" -> showOverdueTasks();

                case "5" -> showCompletedTasks();

                case "6" -> showTimeTrackingSummary();

                case "7" -> running = false;

                default -> System.out.println("Invalid choice!");
            }
        }
    }

    private void showTaskSummary() {

        System.out.println("\n========== TASK SUMMARY ==========");

        int total = analyticsService.getTotalTaskCount();
        int active = analyticsService.getActiveTaskCount();
        double rate = analyticsService.getCompletionRate();

        System.out.println("Total tasks: " + total);
        System.out.println("Active tasks (TODO + IN_PROGRESS): " + active);
        System.out.println("Completion rate: " + String.format("%.1f%%", rate));
    }

    private void showTasksByStatus() {

        System.out.println("\n========== TASKS BY STATUS ==========");

        Map<TaskStatus, Long> counts = analyticsService.getTasksByStatusCount();

        if (counts.isEmpty()) {
            System.out.println("No tasks available.");
            return;
        }

        for (TaskStatus status : TaskStatus.values()) {

            Long count = counts.get(status);

            System.out.println(status + ": " + (count == null ? 0 : count));
        }
    }

    private void showTasksByPriority() {

        System.out.println("\n========== TASKS BY PRIORITY ==========");

        Map<Priority, Long> counts = analyticsService.getTasksByPriorityCount();

        if (counts.isEmpty()) {
            System.out.println("No tasks available.");
            return;
        }

        for (Priority priority : Priority.values()) {

            Long count = counts.get(priority);

            System.out.println(priority + ": " + (count == null ? 0 : count));
        }
    }

    private void showOverdueTasks() {

        System.out.println("\n========== OVERDUE TASKS ==========");

        List<Task> overdue = analyticsService.getOverdueTasks();

        if (overdue.isEmpty()) {
            System.out.println("No overdue tasks!");
            return;
        }

        for (Task task : overdue) {

            System.out.println(
                    task.getId() + " | " + task.getTitle() +
                            " | Priority: " + task.getPriority() +
                            " | Deadline: " + task.getDeadline()
            );
        }

        System.out.println("\nTotal overdue: " + overdue.size());
    }

    private void showCompletedTasks() {

        System.out.println("\n========== COMPLETED TASKS ==========");

        List<Task> completed = analyticsService.getCompletedTasks();

        if (completed.isEmpty()) {
            System.out.println("No completed tasks.");
            return;
        }

        for (Task task : completed) {

            System.out.println(
                    task.getId() + " | " + task.getTitle() +
                            " | Created: " + task.getCreatedAt()
            );
        }

        System.out.println("\nTotal completed: " + completed.size());
    }

    private void showTimeTrackingSummary() {

        System.out.println("\n========== TIME TRACKING SUMMARY ==========");

        long totalMinutes = analyticsService.getTotalTimeTrackedMinutes();
        long todayMinutes = analyticsService.getTimeTrackedTodayMinutes();

        System.out.println("Total time tracked: " + totalMinutes + " minutes (" +
                formatHoursMinutes(totalMinutes) + ")");
        System.out.println("Time tracked today: " + todayMinutes + " minutes (" +
                formatHoursMinutes(todayMinutes) + ")");

        Map<String, Long> byTask = analyticsService.getTimeSpentByTask();

        if (!byTask.isEmpty()) {

            System.out.println("\nTime spent by task:");

            byTask.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .forEach(entry -> {

                        Task task = taskService.findTaskById(entry.getKey()).orElse(null);

                        System.out.println(
                                "  " + (task != null ? task.getTitle() : entry.getKey()) +
                                        ": " + entry.getValue() + " min (" +
                                        formatHoursMinutes(entry.getValue()) + ")"
                        );
                    });
        }
    }

    private String formatHoursMinutes(long minutes) {

        long hours = minutes / 60;
        long mins = minutes % 60;

        return hours + "h " + mins + "m";
    }
}
