
package com.taskforge.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class Task {

    private final String id;
    private String title;
    private String description;
    private Priority priority;
    private TaskStatus status;
    private LocalDate deadline;
    private String projectId;
    private final LocalDateTime createdAt;

    // Stores IDs of tasks that must be completed first
    private Set<String> dependencies = new LinkedHashSet<>();

    // Constructor used when creating a new task
    public Task(String title, String description,
                Priority priority, LocalDate deadline) {

        this(
                null,
                title,
                description,
                priority,
                TaskStatus.TODO,
                deadline,
                null
        );
    }

    // Constructor used by Jackson
    @JsonCreator
    public Task(
            @JsonProperty("id") String id,
            @JsonProperty("title") String title,
            @JsonProperty("description") String description,
            @JsonProperty("priority") Priority priority,
            @JsonProperty("status") TaskStatus status,
            @JsonProperty("deadline") LocalDate deadline,
            @JsonProperty("createdAt") LocalDateTime createdAt) {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "Task title cannot be empty."
            );
        }

        if (priority == null) {
            throw new IllegalArgumentException(
                    "Priority cannot be null."
            );
        }

        this.id = (id == null || id.isBlank())
                ? "TSK-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase()
                : id;

        this.title = title.trim();

        this.description = description == null
                ? ""
                : description.trim();

        this.priority = priority;

        this.status = status == null
                ? TaskStatus.TODO
                : status;

        this.deadline = deadline;

        this.createdAt = createdAt == null
                ? LocalDateTime.now()
                : createdAt;
    }

    // Getters

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Priority getPriority() {
        return priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getProjectId() {
        return projectId;
    }

    public Set<String> getDependencies() {
        return Set.copyOf(dependencies);
    }

    // Setters

    public void setTitle(String title) {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "Task title cannot be empty."
            );
        }

        this.title = title.trim();
    }

    public void setDescription(String description) {

        this.description = description == null
                ? ""
                : description.trim();
    }

    public void setPriority(Priority priority) {

        if (priority == null) {
            throw new IllegalArgumentException(
                    "Priority cannot be null."
            );
        }

        this.priority = priority;
    }

    public void setStatus(TaskStatus status) {

        if (status == null) {
            throw new IllegalArgumentException(
                    "Status cannot be null."
            );
        }

        this.status = status;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public void setProjectId(String projectId) {

        this.projectId = (projectId == null || projectId.isBlank())
                ? null
                : projectId.trim();
    }

    // Dependency management methods

    public void addDependency(String taskId) {

        if (taskId == null || taskId.isBlank()) {
            throw new IllegalArgumentException(
                    "Dependency task ID cannot be empty."
            );
        }

        if (id.equals(taskId)) {
            throw new IllegalArgumentException(
                    "A task cannot depend on itself."
            );
        }

        dependencies.add(taskId.trim());
    }

    public boolean removeDependency(String taskId) {
        return dependencies.remove(taskId);
    }

    public void setDependencies(Set<String> dependencies) {

        this.dependencies = dependencies == null
                ? new LinkedHashSet<>()
                : new LinkedHashSet<>(dependencies);
    }

}
