
package com.taskforge.cli;

import com.taskforge.model.Project;
import com.taskforge.model.ProjectStatus;
import com.taskforge.service.ProjectService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class ProjectMenu {

    private final ProjectService projectService;
    private final Scanner scanner;

    public ProjectMenu(ProjectService projectService, Scanner scanner) {
        this.projectService = projectService;
        this.scanner = scanner;
    }

    public void display() {

        boolean running = true;

        while (running) {

            System.out.println("\n====================================");
            System.out.println("        PROJECT MANAGEMENT");
            System.out.println("====================================");
            System.out.println("1. Create Project");
            System.out.println("2. View All Projects");
            System.out.println("3. Search Project");
            System.out.println("4. Update Project");
            System.out.println("5. Change Project Status");
            System.out.println("6. Delete Project");
            System.out.println("7. Return to Main Menu");
            System.out.println("====================================");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine();

            switch (choice) {

                case "1" -> createProject();

                case "2" -> viewProjects();

                case "3" -> searchProject();

                case "4" -> updateProject();

                case "5" -> changeProjectStatus();

                case "6" -> deleteProject();

                case "7" -> running = false;

                default -> System.out.println("\nInvalid choice!");
            }
        }
    }

    private void createProject() {

        try {
            System.out.println("\n--- CREATE PROJECT ---");

            System.out.print("Enter project name: ");
            String name = scanner.nextLine();

            System.out.print("Enter project description: ");
            String description = scanner.nextLine();

            System.out.print("Enter deadline (YYYY-MM-DD or leave blank): ");
            String deadlineInput = scanner.nextLine();

            LocalDate deadline = deadlineInput.isBlank()
                    ? null
                    : LocalDate.parse(deadlineInput);

            Project project = new Project(name, description, deadline);

            projectService.addProject(project);

            System.out.println("\nProject created successfully!");
            System.out.println("Project ID: " + project.getId());

        } catch (DateTimeParseException e) {
            System.out.println("\nInvalid date format! Use YYYY-MM-DD.");

        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private void viewProjects() {

        System.out.println("\n--- ALL PROJECTS ---");

        if (projectService.getAllProjects().isEmpty()) {
            System.out.println("No projects found.");
            return;
        }

        for (Project project : projectService.getAllProjects()) {

            System.out.println("\nProject ID: " + project.getId());
            System.out.println("Name: " + project.getName());
            System.out.println("Description: " + project.getDescription());
            System.out.println("Deadline: " +
                    (project.getDeadline() == null
                            ? "Not set"
                            : project.getDeadline()));
            System.out.println("Status: " + project.getStatus());
            System.out.println("------------------------------------");
        }
    }

    private void searchProject() {

        System.out.print("\nEnter Project ID: ");
        String projectId = scanner.nextLine();

        projectService.findProjectById(projectId)
                .ifPresentOrElse(
                        project -> {
                            System.out.println("\nProject found!");
                            System.out.println("Name: " + project.getName());
                            System.out.println("Description: " + project.getDescription());
                            System.out.println("Deadline: " + project.getDeadline());
                            System.out.println("Status: " + project.getStatus());
                        },
                        () -> System.out.println("\nProject not found.")
                );
    }

    private void updateProject() {

        System.out.print("\nEnter Project ID to update: ");
        String projectId = scanner.nextLine();

        if (projectService.findProjectById(projectId).isEmpty()) {
            System.out.println("\nProject not found.");
            return;
        }

        try {
            System.out.print("Enter new project name: ");
            String name = scanner.nextLine();

            System.out.print("Enter new description: ");
            String description = scanner.nextLine();

            System.out.print("Enter new deadline (YYYY-MM-DD or leave blank): ");
            String deadlineInput = scanner.nextLine();

            LocalDate deadline = deadlineInput.isBlank()
                    ? null
                    : LocalDate.parse(deadlineInput);

            boolean updated = projectService.updateProject(
                    projectId, name, description, deadline
            );

            if (updated) {
                System.out.println("\nProject updated successfully!");
            }

        } catch (DateTimeParseException e) {
            System.out.println("\nInvalid date format! Use YYYY-MM-DD.");

        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private void changeProjectStatus() {

        System.out.print("\nEnter Project ID: ");
        String projectId = scanner.nextLine();

        if (projectService.findProjectById(projectId).isEmpty()) {
            System.out.println("\nProject not found.");
            return;
        }

        System.out.println("\nAvailable statuses:");

        for (ProjectStatus status : ProjectStatus.values()) {
            System.out.println("- " + status);
        }

        System.out.print("Enter new status: ");
        String statusInput = scanner.nextLine();

        try {
            ProjectStatus status =
                    ProjectStatus.valueOf(statusInput.trim().toUpperCase());

            projectService.updateProjectStatus(projectId, status);

            System.out.println("\nProject status updated successfully!");

        } catch (IllegalArgumentException e) {
            System.out.println("\nInvalid project status!");
        }
    }

    private void deleteProject() {

        System.out.print("\nEnter Project ID to delete: ");
        String projectId = scanner.nextLine();

        boolean deleted = projectService.deleteProject(projectId);

        if (deleted) {
            System.out.println("\nProject deleted successfully!");
        } else {
            System.out.println("\nProject not found.");
        }
    }
}
