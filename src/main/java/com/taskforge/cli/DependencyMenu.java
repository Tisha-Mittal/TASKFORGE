
package com.taskforge.cli;

import com.taskforge.model.Task;
import com.taskforge.service.TaskService;

import java.util.List;
import java.util.Scanner;

public class DependencyMenu {

    private final TaskService taskService;
    private final Scanner scanner;

    public DependencyMenu(TaskService taskService, Scanner scanner) {
        this.taskService = taskService;
        this.scanner = scanner;
    }

    public void display() {

        boolean running = true;

        while (running) {

            System.out.println("\n======= DEPENDENCY MANAGEMENT =======");
            System.out.println("1. Add Task Dependency");
            System.out.println("2. Remove Task Dependency");
            System.out.println("3. View Blocked Tasks");
            System.out.println("4. View Ready Tasks");
            System.out.println("5. View Execution Order");
            System.out.println("6. Check Circular Dependencies");
            System.out.println("7. Return to Main Menu");
            System.out.println("=====================================");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1" -> addDependency();
                case "2" -> removeDependency();
                case "3" -> viewBlockedTasks();
                case "4" -> viewReadyTasks();
                case "5" -> viewExecutionOrder();
                case "6" -> checkCircularDependencies();
                case "7" -> running = false;

                default -> System.out.println("Invalid choice!");
            }
        }
    }

    private void displayAllTasks() {

        List<Task> tasks = taskService.getAllTasks();

        if (tasks.isEmpty()) {
            System.out.println("\nNo tasks available.");
            return;
        }

        System.out.println("\n========== AVAILABLE TASKS ==========");

        for (Task task : tasks) {

            System.out.println(
                    task.getId() + " | " + task.getTitle()
            );
        }
    }

    private void addDependency() {

        if (taskService.getAllTasks().size() < 2) {
            System.out.println(
                    "\nYou need at least two tasks to create a dependency."
            );
            return;
        }

        displayAllTasks();

        System.out.print("\nEnter the dependent Task ID: ");
        String taskId = scanner.nextLine().trim();

        System.out.print("Enter the prerequisite Task ID: ");
        String dependencyId = scanner.nextLine().trim();

        try {

            boolean added = taskService.addDependency(
                    taskId,
                    dependencyId
            );

            if (added) {
                System.out.println(
                        "\nDependency added successfully!"
                );
                System.out.println(
                        "The dependent task must wait until its prerequisite is completed."
                );
            } else {
                System.out.println(
                        "\nDependency could not be added. Check the task IDs or existing dependency."
                );
            }

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "\nError: " + e.getMessage()
            );
        }
    }

    private void removeDependency() {

        displayAllTasks();

        System.out.print("\nEnter the dependent Task ID: ");
        String taskId = scanner.nextLine().trim();

        System.out.print("Enter the prerequisite Task ID to remove: ");
        String dependencyId = scanner.nextLine().trim();

        boolean removed = taskService.removeDependency(
                taskId,
                dependencyId
        );

        if (removed) {
            System.out.println(
                    "\nDependency removed successfully!"
            );
        } else {
            System.out.println(
                    "\nDependency not found or invalid Task ID."
            );
        }
    }

    private void viewBlockedTasks() {

        List<Task> blockedTasks =
                taskService.getBlockedTasks();

        System.out.println("\n========== BLOCKED TASKS ==========");

        if (blockedTasks.isEmpty()) {
            System.out.println("No blocked tasks!");
            return;
        }

        for (Task task : blockedTasks) {

            displayTask(task);

            System.out.println("Waiting for:");

            for (String dependencyId : task.getDependencies()) {

                Task dependency = taskService
                        .findTaskById(dependencyId)
                        .orElse(null);

                if (dependency == null
                        || dependency.getStatus()
                        != com.taskforge.model.TaskStatus.COMPLETED) {

                    System.out.println(
                            "  - " + dependencyId
                            + (dependency == null
                            ? " (Missing task)"
                            : " (" + dependency.getTitle() + ")")
                    );
                }
            }
        }
    }

    private void viewReadyTasks() {

        List<Task> readyTasks =
                taskService.getReadyTasks();

        System.out.println("\n========== READY TASKS ==========");

        if (readyTasks.isEmpty()) {
            System.out.println("No tasks are currently ready.");
            return;
        }

        for (Task task : readyTasks) {
            displayTask(task);
        }
    }

    private void viewExecutionOrder() {

        try {

            List<Task> executionOrder =
                    taskService.getExecutionOrder();

            System.out.println("\n========== EXECUTION ORDER ==========");

            if (executionOrder.isEmpty()) {
                System.out.println("No tasks available.");
                return;
            }

            int position = 1;

            for (Task task : executionOrder) {

                System.out.println(
                        position + ". " + task.getTitle()
                );

                System.out.println(
                        "   ID: " + task.getId()
                );

                position++;
            }

            System.out.println(
                    "\nTasks are arranged so prerequisites appear before dependent tasks."
            );

        } catch (IllegalStateException e) {

            System.out.println(
                    "\nError: " + e.getMessage()
            );
        }
    }

    private void checkCircularDependencies() {

        if (taskService.hasCircularDependency()) {

            System.out.println(
                    "\nCircular dependency detected!"
            );

        } else {

            System.out.println(
                    "\nNo circular dependencies found. Your task graph is valid!"
            );
        }
    }

    private void displayTask(Task task) {

        System.out.println("\n------------------------------------");
        System.out.println("Task ID: " + task.getId());
        System.out.println("Title: " + task.getTitle());
        System.out.println("Status: " + task.getStatus());
        System.out.println("Priority: " + task.getPriority());
        System.out.println("Dependencies: " + task.getDependencies());
        System.out.println("------------------------------------");
    }
}
