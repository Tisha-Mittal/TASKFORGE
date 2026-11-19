
package com.taskforge.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public class TimeEntry {

    private final String id;
    private final String taskId;
    private final LocalDateTime startTime;
    private LocalDateTime endTime;
    private final String description;

    public TimeEntry(String taskId, String description) {

        this(
                null,
                taskId,
                LocalDateTime.now(),
                null,
                description
        );
    }

    @JsonCreator
    public TimeEntry(
            @JsonProperty("id") String id,
            @JsonProperty("taskId") String taskId,
            @JsonProperty("startTime") LocalDateTime startTime,
            @JsonProperty("endTime") LocalDateTime endTime,
            @JsonProperty("description") String description) {

        if (taskId == null || taskId.isBlank()) {
            throw new IllegalArgumentException("Task ID cannot be empty.");
        }

        if (startTime == null) {
            throw new IllegalArgumentException("Start time cannot be null.");
        }

        this.id = (id == null || id.isBlank())
                ? "TME-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase()
                : id;

        this.taskId = taskId.trim();
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description == null ? "" : description.trim();
    }

    public String getId() {
        return id;
    }

    public String getTaskId() {
        return taskId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getDescription() {
        return description;
    }

    @JsonIgnore
    public long getDurationMinutes() {

        if (endTime == null) {
            return java.time.Duration.between(startTime, LocalDateTime.now()).toMinutes();
        }

        return java.time.Duration.between(startTime, endTime).toMinutes();
    }

    @JsonIgnore
    public boolean isRunning() {
        return endTime == null;
    }

    public void stop() {

        if (endTime != null) {
            throw new IllegalStateException("Time entry is already stopped.");
        }

        this.endTime = LocalDateTime.now();
    }
}
