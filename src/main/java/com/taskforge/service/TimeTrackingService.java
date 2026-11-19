
package com.taskforge.service;

import com.taskforge.model.TimeEntry;
import com.taskforge.repository.TimeEntryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TimeTrackingService {

    private final TimeEntryRepository repository;
    private final TaskService taskService;

    public TimeTrackingService(TimeEntryRepository repository, TaskService taskService) {

        if (repository == null) {
            throw new IllegalArgumentException("Repository cannot be null.");
        }

        if (taskService == null) {
            throw new IllegalArgumentException("Task service cannot be null.");
        }

        this.repository = repository;
        this.taskService = taskService;

        loadTimeEntries();
    }

    private void loadTimeEntries() {

        List<TimeEntry> entries = repository.findAll();

        for (TimeEntry entry : entries) {

            if (taskService.findTaskById(entry.getTaskId()).isEmpty()) {

                throw new IllegalStateException(
                        "Time entry references non-existing task: " + entry.getTaskId()
                );
            }
        }
    }

    private void saveTimeEntries() {

        repository.saveAll(List.copyOf(findAllTimeEntries()));
    }

    public List<TimeEntry> findAllTimeEntries() {

        return repository.findAll();
    }

    public Optional<TimeEntry> findTimeEntryById(String timeEntryId) {

        return repository.findById(timeEntryId);
    }

    public TimeEntry startTimer(String taskId, String description) {

        if (taskId == null || taskId.isBlank()) {
            throw new IllegalArgumentException("Task ID cannot be empty.");
        }

        if (taskService.findTaskById(taskId).isEmpty()) {
            throw new IllegalArgumentException("Task does not exist.");
        }

        TimeEntry entry = new TimeEntry(taskId, description);

        repository.saveAll(
                java.util.stream.Stream.concat(
                        repository.findAll().stream(),
                        java.util.stream.Stream.of(entry)
                ).toList()
        );

        return entry;
    }

    public TimeEntry stopTimer(String timeEntryId) {

        Optional<TimeEntry> result = repository.findById(timeEntryId);

        if (result.isEmpty()) {
            throw new IllegalArgumentException("Time entry not found.");
        }

        TimeEntry entry = result.get();

        if (!entry.isRunning()) {
            throw new IllegalStateException("Time entry is already stopped.");
        }

        entry.stop();

        List<TimeEntry> updated = repository.findAll().stream()
                .map(e -> e.getId().equals(timeEntryId) ? entry : e)
                .toList();

        repository.saveAll(updated);

        return entry;
    }

    public List<TimeEntry> getTimeEntriesForTask(String taskId) {

        if (taskId == null || taskId.isBlank()) {
            throw new IllegalArgumentException("Task ID cannot be empty.");
        }

        return repository.findByTaskId(taskId);
    }

    public List<TimeEntry> getRunningTimeEntries() {

        return repository.findRunningEntries();
    }

    public long getTotalTimeSpentOnTask(String taskId) {

        return repository.findByTaskId(taskId).stream()
                .mapToLong(TimeEntry::getDurationMinutes)
                .sum();
    }

    public long getTotalTimeSpentToday() {

        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();

        return repository.findAll().stream()
                .filter(entry -> !entry.getStartTime().isBefore(todayStart))
                .mapToLong(TimeEntry::getDurationMinutes)
                .sum();
    }

    public void deleteTimeEntry(String timeEntryId) {

        if (repository.findById(timeEntryId).isEmpty()) {
            throw new IllegalArgumentException("Time entry not found.");
        }

        repository.deleteById(timeEntryId);
    }
}
