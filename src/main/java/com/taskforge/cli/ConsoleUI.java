
package com.taskforge.cli;

import com.taskforge.service.TaskService;
import com.taskforge.repository.JsonTaskRepository;

import java.util.Scanner;

public class ConsoleUI {

    private final Menu menu;
    private final InputHandler inputHandler;
    private final TaskService taskService;
    private final TaskMenu taskMenu;
    private final Scanner scanner;

    public ConsoleUI() {

        menu = new Menu();

        scanner = new Scanner(System.in);

        inputHandler = new InputHandler(scanner);

        JsonTaskRepository repository =
                new JsonTaskRepository("data/tasks.json");

        taskService = new TaskService(repository);

        taskMenu = new TaskMenu(taskService, scanner);
    }

    public void start() {

        boolean running = true;

        System.out.println("\nWelcome to TASKFORGE!");

        while (running) {

            menu.displayMenu();

            int choice = inputHandler.getMenuChoice();

            switch (choice) {

                case 1 -> taskMenu.display();

                case 2, 3, 4, 5, 6, 7 ->
                    System.out.println("\nThis feature is coming soon!");

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
