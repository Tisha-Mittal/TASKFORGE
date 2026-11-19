
package com.taskforge.cli;

import com.taskforge.model.AppSettings;
import com.taskforge.service.SettingsService;

import java.util.Scanner;

public class SettingsMenu {

    private final SettingsService settingsService;
    private final Scanner scanner;

    public SettingsMenu(SettingsService settingsService, Scanner scanner) {

        this.settingsService = settingsService;
        this.scanner = scanner;
    }

    public void display() {

        boolean running = true;

        while (running) {

            System.out.println("\n========== SETTINGS ==========");
            System.out.println("1. View App Info");
            System.out.println("2. View Current Settings");
            System.out.println("3. Change Data Directory");
            System.out.println("4. Change Default Priority");
            System.out.println("5. Change Default Deadline Days");
            System.out.println("6. Return to Main Menu");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1" -> viewAppInfo();

                case "2" -> viewCurrentSettings();

                case "3" -> changeDataDirectory();

                case "4" -> changeDefaultPriority();

                case "5" -> changeDefaultDeadlineDays();

                case "6" -> running = false;

                default -> System.out.println("Invalid choice!");
            }
        }
    }

    private void viewAppInfo() {

        System.out.println("\n========== APP INFO ==========");
        System.out.println(settingsService.getAppInfo());
    }

    private void viewCurrentSettings() {

        System.out.println("\n========== CURRENT SETTINGS ==========");

        AppSettings settings = settingsService.getSettings();

        System.out.println("Data Directory: " + settings.getDataDirectory());
        System.out.println("Default Priority: " + settings.getDefaultPriority());
        System.out.println("Default Deadline Days: " + settings.getDefaultDeadlineDays());
    }

    private void changeDataDirectory() {

        System.out.print("\nEnter new data directory (current: " +
                settingsService.getSettings().getDataDirectory() + "): ");

        String directory = scanner.nextLine().trim();

        try {

            settingsService.setDataDirectory(directory);

            System.out.println("Data directory updated to: " + directory);

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void changeDefaultPriority() {

        System.out.println("\nAvailable priorities: LOW, MEDIUM, HIGH, CRITICAL");
        System.out.print("Enter new default priority (current: " +
                settingsService.getSettings().getDefaultPriority() + "): ");

        String priority = scanner.nextLine().trim();

        try {

            settingsService.setDefaultPriority(priority);

            System.out.println("Default priority updated to: " + priority.toUpperCase());

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void changeDefaultDeadlineDays() {

        System.out.print("\nEnter new default deadline in days (current: " +
                settingsService.getSettings().getDefaultDeadlineDays() + "): ");

        String input = scanner.nextLine().trim();

        try {

            int days = Integer.parseInt(input);

            settingsService.setDefaultDeadlineDays(days);

            System.out.println("Default deadline days updated to: " + days);

        } catch (NumberFormatException e) {

            System.out.println("Error: Please enter a valid number.");

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
