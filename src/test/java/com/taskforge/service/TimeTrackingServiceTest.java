
package com.taskforge.service;

import com.taskforge.model.Priority;
import com.taskforge.model.Task;
import com.taskforge.model.TimeEntry;
import com.taskforge.repository.TimeEntryRepository;
import com.taskforge.repository.JsonTimeEntryRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TimeTrackingServiceTest {

    @TempDir
    Path tempDir;

    private TimeTrackingService timeTrackingService;
    private TaskService taskService;

    @BeforeEach
    void setUp() {

        Path timeEntryFile = tempDir.resolve("time-entries.json");
        TimeEntryRepository repo = new JsonTimeEntryRepository(timeEntryFile.toString());

        taskService = new TaskService();

        Task taskA = new Task("Task A", "Desc A", Priority.HIGH, null);
        Task taskB = new Task("Task B", "Desc B", Priority.MEDIUM, null);

        taskService.addTask(taskA);
        taskService.addTask(taskB);

        timeTrackingService = new TimeTrackingService(repo, taskService);
    }

    @Test
    void shouldStartTimer() {

        TimeEntry entry = timeTrackingService.startTimer(
                taskService.getAllTasks().get(0).getId(),
                "Working on it"
        );

        assertNotNull(entry.getId());
        assertTrue(entry.isRunning());
        assertEquals("Working on it", entry.getDescription());
    }

    @Test
    void shouldRejectStartingTimerForNonExistingTask() {

        assertThrows(
                IllegalArgumentException.class,
                () -> timeTrackingService.startTimer("NONEXISTENT", "desc")
        );
    }

    @Test
    void shouldStopTimer() {

        TimeEntry entry = timeTrackingService.startTimer(
                taskService.getAllTasks().get(0).getId(),
                "desc"
        );

        TimeEntry stopped = timeTrackingService.stopTimer(entry.getId());

        assertFalse(stopped.isRunning());
        assertNotNull(stopped.getEndTime());
    }

    @Test
    void shouldRejectStoppingNonRunningTimer() {

        TimeEntry entry = timeTrackingService.startTimer(
                taskService.getAllTasks().get(0).getId(),
                "desc"
        );
        timeTrackingService.stopTimer(entry.getId());

        assertThrows(
                IllegalStateException.class,
                () -> timeTrackingService.stopTimer(entry.getId())
        );
    }

    @Test
    void shouldFindRunningTimeEntries() {

        timeTrackingService.startTimer(
                taskService.getAllTasks().get(0).getId(),
                "desc"
        );

        List<TimeEntry> running = timeTrackingService.getRunningTimeEntries();

        assertEquals(1, running.size());
    }

    @Test
    void shouldGetTimeEntriesForTask() {

        String taskId = taskService.getAllTasks().get(0).getId();

        timeTrackingService.startTimer(taskId, "desc 1");
        timeTrackingService.startTimer(taskId, "desc 2");

        List<TimeEntry> entries = timeTrackingService.getTimeEntriesForTask(taskId);

        assertEquals(2, entries.size());
    }

    @Test
    void shouldGetTotalTimeSpentOnTask() {

        String taskId = taskService.getAllTasks().get(0).getId();

        TimeEntry entry = timeTrackingService.startTimer(taskId, "desc");
        timeTrackingService.stopTimer(entry.getId());

        long total = timeTrackingService.getTotalTimeSpentOnTask(taskId);

        assertTrue(total >= 0);
    }

    @Test
    void shouldFindTimeEntryById() {

        TimeEntry entry = timeTrackingService.startTimer(
                taskService.getAllTasks().get(0).getId(),
                "desc"
        );

        Optional<TimeEntry> found = timeTrackingService.findTimeEntryById(entry.getId());

        assertTrue(found.isPresent());
        assertEquals(entry.getId(), found.get().getId());
    }

    @Test
    void shouldDeleteTimeEntry() {

        TimeEntry entry = timeTrackingService.startTimer(
                taskService.getAllTasks().get(0).getId(),
                "desc"
        );

        timeTrackingService.deleteTimeEntry(entry.getId());

        assertTrue(timeTrackingService.findTimeEntryById(entry.getId()).isEmpty());
    }

    @Test
    void shouldRejectDeletingNonExistingTimeEntry() {

        assertThrows(
                IllegalArgumentException.class,
                () -> timeTrackingService.deleteTimeEntry("NONEXISTENT")
        );
    }
}
