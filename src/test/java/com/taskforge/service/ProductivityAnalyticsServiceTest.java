
package com.taskforge.service;

import com.taskforge.model.Priority;
import com.taskforge.model.Task;
import com.taskforge.model.TaskStatus;
import com.taskforge.model.TimeEntry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProductivityAnalyticsServiceTest {

    private TaskService taskService;
    private TimeTrackingService timeTrackingService;
    private ProductivityAnalyticsService analyticsService;

    @BeforeEach
    void setUp() {

        taskService = new TaskService();

        PathTimeEntryRepository repo = new PathTimeEntryRepository();
        timeTrackingService = new TimeTrackingService(repo, taskService);

        analyticsService = new ProductivityAnalyticsService(taskService, timeTrackingService);

        // Add tasks with different statuses and priorities
        Task task1 = new Task("Todo High", "", Priority.HIGH, LocalDate.now().minusDays(1));
        taskService.addTask(task1);

        Task task2 = new Task("In Progress Med", "", Priority.MEDIUM, null);
        taskService.addTask(task2);
        taskService.updateTaskStatus(task2.getId(), TaskStatus.IN_PROGRESS);

        Task task3 = new Task("Completed Low", "", Priority.LOW, null);
        taskService.addTask(task3);
        taskService.updateTaskStatus(task3.getId(), TaskStatus.COMPLETED);

        Task task4 = new Task("Cancelled", "", Priority.CRITICAL, null);
        taskService.addTask(task4);
        taskService.updateTaskStatus(task4.getId(), TaskStatus.CANCELLED);
    }

    @Test
    void shouldGetTasksByStatusCount() {

        Map<TaskStatus, Long> counts = analyticsService.getTasksByStatusCount();

        assertEquals(1L, counts.get(TaskStatus.TODO));
        assertEquals(1L, counts.get(TaskStatus.IN_PROGRESS));
        assertEquals(1L, counts.get(TaskStatus.COMPLETED));
        assertEquals(1L, counts.get(TaskStatus.CANCELLED));
    }

    @Test
    void shouldGetTasksByPriorityCount() {

        Map<Priority, Long> counts = analyticsService.getTasksByPriorityCount();

        assertEquals(1L, counts.get(Priority.HIGH));
        assertEquals(1L, counts.get(Priority.MEDIUM));
        assertEquals(1L, counts.get(Priority.LOW));
        assertEquals(1L, counts.get(Priority.CRITICAL));
    }

    @Test
    void shouldGetCompletedTasks() {

        List<Task> completed = analyticsService.getCompletedTasks();

        assertEquals(1, completed.size());
        assertEquals("Completed Low", completed.get(0).getTitle());
    }

    @Test
    void shouldGetOverdueTasks() {

        List<Task> overdue = analyticsService.getOverdueTasks();

        assertEquals(1, overdue.size());
        assertEquals("Todo High", overdue.get(0).getTitle());
    }

    @Test
    void shouldGetCompletionRate() {

        double rate = analyticsService.getCompletionRate();

        assertEquals(25.0, rate);
    }

    @Test
    void shouldReturnZeroCompletionRateWhenNoTasks() {

        TaskService emptyService = new TaskService();
        TimeTrackingService emptyTimeService = new TimeTrackingService(
                new PathTimeEntryRepository(), emptyService
        );
        ProductivityAnalyticsService emptyAnalytics =
                new ProductivityAnalyticsService(emptyService, emptyTimeService);

        assertEquals(0.0, emptyAnalytics.getCompletionRate());
    }

    @Test
    void shouldGetTotalTaskCount() {

        assertEquals(4, analyticsService.getTotalTaskCount());
    }

    @Test
    void shouldGetActiveTaskCount() {

        assertEquals(2, analyticsService.getActiveTaskCount());
    }

    // In-memory repository for testing
    static class PathTimeEntryRepository implements com.taskforge.repository.TimeEntryRepository {

        private final java.util.List<TimeEntry> entries = new java.util.ArrayList<>();

        @Override
        public void saveAll(java.util.List<TimeEntry> timeEntries) {
            entries.clear();
            entries.addAll(timeEntries);
        }

        @Override
        public java.util.List<TimeEntry> findAll() {
            return new java.util.ArrayList<>(entries);
        }

        @Override
        public java.util.Optional<TimeEntry> findById(String timeEntryId) {
            return entries.stream()
                    .filter(e -> e.getId().equals(timeEntryId))
                    .findFirst();
        }

        @Override
        public void deleteById(String timeEntryId) {
            entries.removeIf(e -> e.getId().equals(timeEntryId));
        }

        @Override
        public java.util.List<TimeEntry> findByTaskId(String taskId) {
            return entries.stream()
                    .filter(e -> taskId.equals(e.getTaskId()))
                    .toList();
        }

        @Override
        public java.util.List<TimeEntry> findRunningEntries() {
            return entries.stream()
                    .filter(TimeEntry::isRunning)
                    .toList();
        }
    }
}
