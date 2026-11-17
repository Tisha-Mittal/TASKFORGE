
package com.taskforge.cli;

import com.taskforge.model.Task;
import com.taskforge.model.Priority;
import com.taskforge.model.TaskStatus;
import com.taskforge.service.TaskService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.Scanner;

public class TaskMenu {

    private final TaskService taskService;
    private final Scanner scanner;

    public TaskMenu(TaskService taskService, Scanner scanner) {
        this.taskService = taskService;
        this.scanner = scanner;
    }

    public void display() {

        boolean running = true;

        while (running) {

            System.out.println("\n========== TASK MANAGEMENT ==========");
            System.out.println("1. Create Task");
            System.out.println("2. View All Tasks");
            System.out.println("3. Search Task");
            System.out.println("4. Update Task");
            System.out.println("5. Change Task Status");
            System.out.println("6. Delete Task");
            System.out.println("7. Return to Main Menu");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1" -> createTask();
                case "2" -> viewAllTasks();
                case "3" -> searchTask();
                case "4" -> updateTask();
                case "5" -> changeTaskStatus();
                case "6" -> deleteTask();
                case "7" -> running = false;

                default -> System.out.println("Invalid choice!");
            }
        }
    }

    private void createTask() {

        try {

            System.out.print("Enter task title: ");
            String title = scanner.nextLine();

            System.out.print("Enter description: ");
            String description = scanner.nextLine();

            System.out.println("Priority: LOW, MEDIUM, HIGH, CRITICAL");
            System.out.print("Enter priority: ");

            Priority priority = Priority.valueOf(
                    scanner.nextLine().trim().toUpperCase()
            );

            System.out.print("Enter deadline (YYYY-MM-DD): ");
            String dateInput = scanner.nextLine().trim();

            LocalDate deadline = dateInput.isBlank()
                    ? null
                    : LocalDate.parse(dateInput);

            Task task = new Task(title, description, priority, deadline);

            taskService.addTask(task);

            System.out.println("\nTask created successfully!");
            System.out.println("Task ID: " + task.getId());

        } catch (IllegalArgumentException | DateTimeParseException e) {

            System.out.println("Invalid input: " + e.getMessage());
        }
    }

    private void viewAllTasks() {

        if (taskService.getAllTasks().isEmpty()) {
            System.out.println("\nNo tasks available.");
            return;
        }

        System.out.println("\n========== ALL TASKS ==========");

        for (Task task : taskService.getAllTasks()) {

            displayTask(task);
        }
    }

    private void searchTask() {

        System.out.print("Enter Task ID: ");
        String taskId = scanner.nextLine().trim();

        Optional<Task> result = taskService.findTaskById(taskId);

        if (result.isPresent()) {

            displayTask(result.get());

        } else {

            System.out.println("Task not found.");
        }
    }

    private void updateTask() {

        System.out.print("Enter Task ID: ");
        String taskId = scanner.nextLine().trim();

        Optional<Task> result = taskService.findTaskById(taskId);

        if (result.isEmpty()) {
            System.out.println("Task not found.");
            return;
        }

        Task task = result.get();

        System.out.print("Enter new title: ");
        String title = scanner.nextLine();

        System.out.print("Enter new description: ");
        String description = scanner.nextLine();

        try {

            System.out.println("Priority: LOW, MEDIUM, HIGH, CRITICAL");
            System.out.print("Enter new priority: ");

            Priority priority = Priority.valueOf(
                    scanner.nextLine().trim().toUpperCase()
            );

            System.out.print("Enter new deadline (YYYY-MM-DD, blank for none): ");
            String dateInput = scanner.nextLine().trim();

            LocalDate deadline = dateInput.isBlank()
                    ? null
                    : LocalDate.parse(dateInput);

            boolean updated = taskService.updateTask(
                    taskId, title, description, priority, deadline
            );

            if (updated) {
                System.out.println("Task updated successfully!");
            }

        } catch (IllegalArgumentException | DateTimeParseException e) {

            System.out.println("Invalid input: " + e.getMessage());
        }
    }

    private void changeTaskStatus() {

        System.out.print("Enter Task ID: ");
        String taskId = scanner.nextLine().trim();

        System.out.println("Status: TODO, IN_PROGRESS, COMPLETED, CANCELLED");
        System.out.print("Enter new status: ");

        try {

            TaskStatus status = TaskStatus.valueOf(
                    scanner.nextLine().trim().toUpperCase()
            );

            boolean updated = taskService.updateTaskStatus(taskId, status);

            if (updated) {
                System.out.println("Task status updated!");
            } else {
                System.out.println("Task not found.");
            }

        } catch (IllegalArgumentException e) {

            System.out.println("Invalid status!");
        }
    }

    private void deleteTask() {

        System.out.print("Enter Task ID: ");
        String taskId = scanner.nextLine().trim();

        boolean deleted = taskService.deleteTask(taskId);

        if (deleted) {
            System.out.println("Task deleted successfully!");
        } else {
            System.out.println("Task not found.");
        }
    }

    private void displayTask(Task task) {

        System.out.println("\n------------------------------------");
        System.out.println("Task ID: " + task.getId());
        System.out.println("Title: " + task.getTitle());
        System.out.println("Description: " + task.getDescription());
        System.out.println("Priority: " + task.getPriority());
        System.out.println("Status: " + task.getStatus());
        System.out.println("Deadline: " + task.getDeadline());
        System.out.println("Created At: " + task.getCreatedAt());
        System.out.println("------------------------------------");
    }
}
