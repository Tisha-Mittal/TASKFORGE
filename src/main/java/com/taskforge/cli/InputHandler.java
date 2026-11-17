package com.taskforge.cli;

import java.util.Scanner;

public class InputHandler {

    private final Scanner scanner;

    // Used by the actual application
    public InputHandler() {
        this(new Scanner(System.in));
    }

    // Used when we want to provide input ourselves, especially in tests
    public InputHandler(Scanner scanner) {
        this.scanner = scanner;
    }

    public int getMenuChoice() {

        if (!scanner.hasNextInt()) {
            scanner.nextLine();
            return -1;
        }

        int choice = scanner.nextInt();
        scanner.nextLine();

        return choice;
    }

    public void close() {
        scanner.close();
    }
}