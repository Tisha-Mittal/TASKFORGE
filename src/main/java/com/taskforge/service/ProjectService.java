
package com.taskforge.service;

import com.taskforge.model.Project;
import com.taskforge.model.ProjectStatus;
import com.taskforge.repository.ProjectRepository;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProjectService {

    private final Map<String, Project> projects;
    private final ProjectRepository repository;

    // In-memory constructor for testing
    public ProjectService() {

        projects = new LinkedHashMap<>();
        repository = null;
    }

    // Constructor with repository injection
    public ProjectService(ProjectRepository repository) {

        if (repository == null) {
            throw new IllegalArgumentException(
                    "Repository cannot be null."
            );
        }

        this.repository = repository;
        this.projects = new LinkedHashMap<>();

        loadProjects();
    }

    // Load saved projects
    private void loadProjects() {

        List<Project> savedProjects = repository.findAll();

        for (Project project : savedProjects) {

            if (projects.containsKey(project.getId())) {
                throw new IllegalStateException(
                        "Duplicate project ID found: " + project.getId()
                );
            }

            projects.put(project.getId(), project);
        }
    }

    // Save projects
    private void saveProjects() {

        if (repository != null) {

            repository.saveAll(
                    List.copyOf(projects.values())
            );
        }
    }

    // Create project
    public void addProject(Project project) {

        if (project == null) {
            throw new IllegalArgumentException(
                    "Project cannot be null."
            );
        }

        if (projects.containsKey(project.getId())) {
            throw new IllegalArgumentException(
                    "Project already exists."
            );
        }

        projects.put(project.getId(), project);

        saveProjects();
    }

    // Retrieve all projects
    public List<Project> getAllProjects() {

        return List.copyOf(projects.values());
    }

    // Find project by ID
    public Optional<Project> findProjectById(String projectId) {

        return Optional.ofNullable(projects.get(projectId));
    }

    // Delete project
    public boolean deleteProject(String projectId) {

        Project removedProject = projects.remove(projectId);

        if (removedProject != null) {
            saveProjects();
            return true;
        }

        return false;
    }

    // Update project details
    public boolean updateProject(
            String projectId,
            String name,
            String description,
            LocalDate deadline) {

        Optional<Project> result = findProjectById(projectId);

        if (result.isEmpty()) {
            return false;
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Project name cannot be empty."
            );
        }

        Project project = result.get();

        project.setName(name);
        project.setDescription(description);
        project.setDeadline(deadline);

        saveProjects();

        return true;
    }

    // Update project status
    public boolean updateProjectStatus(
            String projectId,
            ProjectStatus status) {

        Optional<Project> result = findProjectById(projectId);

        if (result.isEmpty()) {
            return false;
        }

        if (status == null) {
            throw new IllegalArgumentException(
                    "Project status cannot be null."
            );
        }

        result.get().setStatus(status);

        saveProjects();

        return true;
    }
}
