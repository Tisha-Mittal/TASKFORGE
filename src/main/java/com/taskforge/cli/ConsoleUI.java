
package com.taskforge.cli;

import com.taskforge.service.TaskService;
import com.taskforge.service.ProjectService;

import com.taskforge.repository.JsonTaskRepository;
import com.taskforge.repository.JsonProjectRepository;

import java.util.Scanner;

public class ConsoleUI {

    private final Menu menu;
    private final InputHandler inputHandler;

    private final TaskService taskService;
    private final ProjectService projectService;

    private final TaskMenu taskMenu;
    private final ProjectMenu projectMenu;
    private final DependencyMenu dependencyMenu;

    private final Scanner scanner;

    public ConsoleUI() {

        menu = new Menu();

        scanner = new Scanner(System.in);

        inputHandler = new InputHandler(scanner);

        JsonTaskRepository taskRepository =
                new JsonTaskRepository("data/tasks.json");

        JsonProjectRepository projectRepository =
                new JsonProjectRepository("data/projects.json");

        projectService = new ProjectService(projectRepository);

        taskService = new TaskService(
                taskRepository,
                projectService
        );

        taskMenu = new TaskMenu(
                taskService,
                projectService,
                scanner
        );

        projectMenu = new ProjectMenu(
                projectService,
                scanner
        );

        dependencyMenu = new DependencyMenu(
                taskService,
                scanner
        );
    }

    public void start() {

        boolean running = true;

        System.out.println("\nWelcome to TASKFORGE!");

        while (running) {

            menu.displayMenu();

            int choice = inputHandler.getMenuChoice();

            switch (choice) {

                case 1 -> taskMenu.display();

                case 2 -> projectMenu.display();

                case 3 ->
                        System.out.println(
                                "\nScheduling will be implemented next!"
                        );

                case 4 -> dependencyMenu.display();

                case 5, 6, 7 ->
                        System.out.println(
                                "\nThis feature is coming soon!"
                        );

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

        inputHandler.close();
    }
}
