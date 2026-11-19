
package com.taskforge.cli;

import com.taskforge.model.TimeEntry;
import com.taskforge.service.TaskService;
import com.taskforge.service.TimeTrackingService;

import java.util.List;
import java.util.Scanner;

public class TimeTrackingMenu {

    private final TimeTrackingService timeTrackingService;
    private final TaskService taskService;
    private final Scanner scanner;

    public TimeTrackingMenu(
            TimeTrackingService timeTrackingService,
            TaskService taskService,
            Scanner scanner) {

        this.timeTrackingService = timeTrackingService;
        this.taskService = taskService;
        this.scanner = scanner;
    }

    public void display() {

        boolean running = true;

        while (running) {

            System.out.println("\n========== TIME TRACKING ==========");
            System.out.println("1. Start Timer");
            System.out.println("2. Stop Timer");
            System.out.println("3. View Running Timers");
            System.out.println("4. View Time Entries for Task");
            System.out.println("5. View All Time Entries");
            System.out.println("6. Delete Time Entry");
            System.out.println("7. Return to Main Menu");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1" -> startTimer();

                case "2" -> stopTimer();

                case "3" -> viewRunningTimers();

                case "4" -> viewTimeEntriesForTask();

                case "5" -> viewAllTimeEntries();

                case "6" -> deleteTimeEntry();

                case "7" -> running = false;

                default -> System.out.println("Invalid choice!");
            }
        }
    }

    private void startTimer() {

        System.out.println("\n--- START TIMER ---");

        if (taskService.getAllTasks().isEmpty()) {
            System.out.println("No tasks available. Create a task first.");
            return;
        }

        System.out.print("Enter Task ID: ");
        String taskId = scanner.nextLine().trim();

        if (taskService.findTaskById(taskId).isEmpty()) {
            System.out.println("Task not found.");
            return;
        }

        var runningEntries = timeTrackingService.getRunningTimeEntries();
        var alreadyRunning = runningEntries.stream()
                .anyMatch(entry -> taskId.equals(entry.getTaskId()));

        if (alreadyRunning) {
            System.out.println("A timer is already running for this task.");
            return;
        }

        System.out.print("Enter description (optional): ");
        String description = scanner.nextLine().trim();

        try {

            TimeEntry entry = timeTrackingService.startTimer(taskId, description);

            System.out.println("\nTimer started!");
            System.out.println("Time Entry ID: " + entry.getId());
            System.out.println("Task ID: " + entry.getTaskId());
            System.out.println("Start Time: " + entry.getStartTime());

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void stopTimer() {

        System.out.println("\n--- STOP TIMER ---");

        var runningEntries = timeTrackingService.getRunningTimeEntries();

        if (runningEntries.isEmpty()) {
            System.out.println("No running timers.");
            return;
        }

        System.out.println("Running Timers:");

        for (TimeEntry entry : runningEntries) {

            System.out.println(
                    "ID: " + entry.getId() +
                            " | Task: " + entry.getTaskId() +
                            " | Started: " + entry.getStartTime() +
                            " | Elapsed: " + entry.getDurationMinutes() + " min"
            );
        }

        System.out.print("\nEnter Time Entry ID to stop: ");
        String timeEntryId = scanner.nextLine().trim();

        try {

            TimeEntry entry = timeTrackingService.stopTimer(timeEntryId);

            System.out.println("\nTimer stopped!");
            System.out.println("Time Entry ID: " + entry.getId());
            System.out.println("Duration: " + entry.getDurationMinutes() + " minutes");

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewRunningTimers() {

        System.out.println("\n========== RUNNING TIMERS ==========");

        var entries = timeTrackingService.getRunningTimeEntries();

        if (entries.isEmpty()) {
            System.out.println("No running timers.");
            return;
        }

        for (TimeEntry entry : entries) {

            System.out.println(
                    "ID: " + entry.getId() +
                            " | Task: " + entry.getTaskId() +
                            " | Started: " + entry.getStartTime() +
                            " | Elapsed: " + entry.getDurationMinutes() + " min"
            );
        }
    }

    private void viewTimeEntriesForTask() {

        System.out.print("\nEnter Task ID: ");
        String taskId = scanner.nextLine().trim();

        if (taskService.findTaskById(taskId).isEmpty()) {
            System.out.println("Task not found.");
            return;
        }

        List<TimeEntry> entries = timeTrackingService.getTimeEntriesForTask(taskId);

        System.out.println("\n========== TIME ENTRIES FOR TASK: " + taskId + " ==========");

        if (entries.isEmpty()) {
            System.out.println("No time entries for this task.");
            return;
        }

        long totalMinutes = 0;

        for (TimeEntry entry : entries) {

            System.out.println(
                    "ID: " + entry.getId() +
                            " | Start: " + entry.getStartTime() +
                            " | End: " + (entry.getEndTime() == null ? "Running" : entry.getEndTime()) +
                            " | Duration: " + entry.getDurationMinutes() + " min" +
                            (entry.getDescription().isBlank() ? "" : " | " + entry.getDescription())
            );

            totalMinutes += entry.getDurationMinutes();
        }

        System.out.println("\nTotal time spent: " + totalMinutes + " minutes");
    }

    private void viewAllTimeEntries() {

        List<TimeEntry> entries = timeTrackingService.findAllTimeEntries();

        System.out.println("\n========== ALL TIME ENTRIES ==========");

        if (entries.isEmpty()) {
            System.out.println("No time entries available.");
            return;
        }

        long totalMinutes = 0;

        for (TimeEntry entry : entries) {

            System.out.println(
                    "ID: " + entry.getId() +
                            " | Task: " + entry.getTaskId() +
                            " | Start: " + entry.getStartTime() +
                            " | End: " + (entry.getEndTime() == null ? "Running" : entry.getEndTime()) +
                            " | Duration: " + entry.getDurationMinutes() + " min"
            );

            totalMinutes += entry.getDurationMinutes();
        }

        System.out.println("\nTotal time tracked: " + totalMinutes + " minutes");
    }

    private void deleteTimeEntry() {

        System.out.print("\nEnter Time Entry ID to delete: ");
        String timeEntryId = scanner.nextLine().trim();

        try {

            timeTrackingService.deleteTimeEntry(timeEntryId);

            System.out.println("Time entry deleted successfully!");

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
