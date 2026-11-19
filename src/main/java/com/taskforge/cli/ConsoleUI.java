
package com.taskforge.cli;

import com.taskforge.service.TaskService;
import com.taskforge.service.ProjectService;
import com.taskforge.service.TimeTrackingService;
import com.taskforge.service.ProductivityAnalyticsService;
import com.taskforge.service.SettingsService;

import com.taskforge.repository.JsonTaskRepository;
import com.taskforge.repository.JsonProjectRepository;
import com.taskforge.repository.JsonTimeEntryRepository;

import com.taskforge.engine.SchedulingEngine;
import com.taskforge.model.Task;

import java.util.Scanner;
import java.util.List;

public class ConsoleUI {

    private final Menu menu;
    private final InputHandler inputHandler;

    private final TaskService taskService;
    private final ProjectService projectService;
    private final SchedulingEngine schedulingEngine;
    private final TimeTrackingService timeTrackingService;
    private final ProductivityAnalyticsService analyticsService;
    private final SettingsService settingsService;

    private final TaskMenu taskMenu;
    private final ProjectMenu projectMenu;
    private final DependencyMenu dependencyMenu;
    private final TimeTrackingMenu timeTrackingMenu;
    private final ProductivityAnalyticsMenu analyticsMenu;
    private final SettingsMenu settingsMenu;

    private final Scanner scanner;

    public ConsoleUI() {

        menu = new Menu();

        scanner = new Scanner(System.in);

        inputHandler = new InputHandler(scanner);

        String dataDir = System.getProperty("taskforge.data.dir", "data");

        JsonTaskRepository taskRepository =
                new JsonTaskRepository(dataDir + "/tasks.json");

        JsonProjectRepository projectRepository =
                new JsonProjectRepository(dataDir + "/projects.json");

        JsonTimeEntryRepository timeEntryRepository =
                new JsonTimeEntryRepository(dataDir + "/time-entries.json");

        projectService = new ProjectService(projectRepository);

        taskService = new TaskService(
                taskRepository,
                projectService
        );

        schedulingEngine = new SchedulingEngine(taskService);

        timeTrackingService = new TimeTrackingService(
                timeEntryRepository,
                taskService
        );

        analyticsService = new ProductivityAnalyticsService(
                taskService,
                timeTrackingService
        );

        settingsService = new SettingsService();

        taskMenu = new TaskMenu(
                taskService,
                projectService,
                scanner
        );

        projectMenu = new ProjectMenu(
                projectService,
                taskService,
                scanner
        );

        dependencyMenu = new DependencyMenu(
                taskService,
                scanner
        );

        timeTrackingMenu = new TimeTrackingMenu(
                timeTrackingService,
                taskService,
                scanner
        );

        analyticsMenu = new ProductivityAnalyticsMenu(
                analyticsService,
                taskService,
                scanner
        );

        settingsMenu = new SettingsMenu(
                settingsService,
                scanner
        );
    }

    public void start() {

        boolean running = true;

        System.out.println("\nWelcome to TASKFORGE!");

        try {

            while (running) {

                menu.displayMenu();

                int choice = inputHandler.getMenuChoice();

                switch (choice) {

                    case 1 -> taskMenu.display();

                    case 2 -> projectMenu.display();

                    case 3 -> displayScheduling();

                    case 4 -> dependencyMenu.display();

                    case 5 -> timeTrackingMenu.display();

                    case 6 -> analyticsMenu.display();

                    case 7 -> settingsMenu.display();

                    case 8 -> {
                        System.out.println(
                                "\nThank you for using TASKFORGE!"
                        );
                        running = false;
                    }

                    default ->
                            System.out.println(
                                    "\nInvalid choice! Please enter a number between 1 and 8."
                            );
                }
            }
        } finally {

            inputHandler.close();
        }
    }

    private void displayScheduling() {

        boolean running = true;

        while (running) {

            System.out.println("\n========== SCHEDULING ==========");
            System.out.println("1. Recommended Next Task");
            System.out.println("2. View Overdue Tasks");
            System.out.println("3. View Tasks by Recommended Order");
            System.out.println("4. Return to Main Menu");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1" -> showRecommendedNextTask();

                case "2" -> showOverdueTasks();

                case "3" -> showRecommendedOrder();

                case "4" -> running = false;

                default -> System.out.println("Invalid choice!");
            }
        }
    }

    private void showRecommendedNextTask() {

        var recommended = schedulingEngine.getRecommendedNextTask();

        if (recommended.isEmpty()) {
            System.out.println("\nNo tasks are ready to work on right now.");
            return;
        }

        Task task = recommended.get();
        System.out.println("\n========== RECOMMENDED NEXT TASK ==========");
        System.out.println("Task ID: " + task.getId());
        System.out.println("Title: " + task.getTitle());
        System.out.println("Description: " + task.getDescription());
        System.out.println("Priority: " + task.getPriority());
        System.out.println("Status: " + task.getStatus());
        System.out.println("Deadline: " +
                (task.getDeadline() == null ? "Not set" : task.getDeadline()));
        System.out.println("Urgency: " + schedulingEngine.getUrgency(task));
        System.out.println("Priority Score: " + schedulingEngine.getPriorityScore(task));
    }

    private void showOverdueTasks() {

        List<Task> overdue = schedulingEngine.getOverdueTasks();

        System.out.println("\n========== OVERDUE TASKS ==========");

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
    }

    private void showRecommendedOrder() {

        List<Task> order = schedulingEngine.getTasksByRecommendedOrder();

        System.out.println("\n========== TASKS BY RECOMMENDED ORDER ==========");

        if (order.isEmpty()) {
            System.out.println("No tasks are ready to work on right now.");
            return;
        }

        int position = 1;
        for (Task task : order) {
            System.out.println(
                    position + ". " + task.getTitle() +
                            " (ID: " + task.getId() +
                            ", Score: " + schedulingEngine.getPriorityScore(task) +
                            ", Urgency: " + schedulingEngine.getUrgency(task) + ")"
            );
            position++;
        }
    }
}
