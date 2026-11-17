
package com.taskforge.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Project {

    private final String id;
    private String name;
    private String description;
    private LocalDate deadline;
    private ProjectStatus status;
    private final LocalDateTime createdAt;

    // Constructor for creating a new project
    public Project(String name, String description, LocalDate deadline) {

        this(
                null,
                name,
                description,
                deadline,
                ProjectStatus.ACTIVE,
                null
        );
    }

    // Constructor for restoring a project from JSON
    @JsonCreator
    public Project(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("deadline") LocalDate deadline,
            @JsonProperty("status") ProjectStatus status,
            @JsonProperty("createdAt") LocalDateTime createdAt) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Project name cannot be empty."
            );
        }

        this.id = (id == null || id.isBlank())
                ? "PRJ-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase()
                : id;

        this.name = name.trim();

        this.description = description == null
                ? ""
                : description.trim();

        this.deadline = deadline;

        this.status = status == null
                ? ProjectStatus.ACTIVE
                : status;

        this.createdAt = createdAt == null
                ? LocalDateTime.now()
                : createdAt;
    }

    // Getters

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters

    public void setName(String name) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Project name cannot be empty."
            );
        }

        this.name = name.trim();
    }

    public void setDescription(String description) {

        this.description = description == null
                ? ""
                : description.trim();
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public void setStatus(ProjectStatus status) {

        if (status == null) {
            throw new IllegalArgumentException(
                    "Project status cannot be null."
            );
        }

        this.status = status;
    }
}
