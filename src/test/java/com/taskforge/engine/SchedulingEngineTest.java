
package com.taskforge.engine;

import com.taskforge.model.Priority;
import com.taskforge.model.Task;
import com.taskforge.service.TaskService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SchedulingEngineTest {

    private TaskService taskService;
    private SchedulingEngine schedulingEngine;

    @BeforeEach
    void setUp() {

        taskService = new TaskService();

        schedulingEngine =
                new SchedulingEngine(taskService);
    }

    private Task createTask(
            String title,
            Priority priority,
            LocalDate deadline) {

        Task task = new Task(
                title,
                "Test task",
                priority,
                deadline
        );

        taskService.addTask(task);

        return task;
    }

    @Test
    void shouldIdentifyOverdueTask() {

        Task task = new Task(
                "Overdue task",
                "Testing urgency",
                Priority.HIGH,
                LocalDate.now().minusDays(2)
        );

        assertEquals(
                TaskUrgency.OVERDUE,
                schedulingEngine.getUrgency(task)
        );
    }

    @Test
    void shouldIdentifyTaskDueToday() {

        Task task = new Task(
                "Today's task",
                "Testing urgency",
                Priority.HIGH,
                LocalDate.now()
        );

        assertEquals(
                TaskUrgency.DUE_TODAY,
                schedulingEngine.getUrgency(task)
        );
    }

    @Test
    void shouldIdentifyTaskWithNoDeadline() {

        Task task = new Task(
                "No deadline",
                "Testing urgency",
                Priority.MEDIUM,
                null
        );

        assertEquals(
                TaskUrgency.NO_DEADLINE,
                schedulingEngine.getUrgency(task)
        );
    }

    @Test
    void shouldCalculatePriorityScore() {

        Task task = new Task(
                "Critical task",
                "Testing score",
                Priority.CRITICAL,
                LocalDate.now()
        );

        assertEquals(
                100,
                schedulingEngine.getPriorityScore(task)
        );
    }

    @Test
    void shouldRecommendHighestScoringTask() {

        Task lowTask = createTask(
                "Low task",
                Priority.LOW,
                LocalDate.now().plusDays(20)
        );

        Task criticalTask = createTask(
                "Critical task",
                Priority.CRITICAL,
                LocalDate.now()
        );

        assertEquals(
                criticalTask.getId(),
                schedulingEngine.getRecommendedNextTask()
                        .orElseThrow()
                        .getId()
        );
    }

    @Test
    void shouldReturnTasksInRecommendedOrder() {

        Task lowTask = createTask(
                "Low task",
                Priority.LOW,
                null
        );

        Task highTask = createTask(
                "High task",
                Priority.HIGH,
                null
        );

        var tasks =
                schedulingEngine.getTasksByRecommendedOrder();

        assertEquals(highTask.getId(), tasks.get(0).getId());
        assertEquals(lowTask.getId(), tasks.get(1).getId());
    }

    @Test
    void shouldFindOverdueTasks() {

        Task overdueTask = createTask(
                "Overdue task",
                Priority.HIGH,
                LocalDate.now().minusDays(1)
        );

        var overdueTasks =
                schedulingEngine.getOverdueTasks();

        assertTrue(
                overdueTasks.stream().anyMatch(
                        task -> task.getId().equals(overdueTask.getId())
                )
        );
    }

    @Test
    void shouldNotRecommendOverdueDependencyBlockedTask() {

        Task prerequisite = createTask(
                "Prerequisite",
                Priority.MEDIUM,
                LocalDate.now().plusDays(5)
        );

        Task dependent = createTask(
                "Dependent task",
                Priority.CRITICAL,
                LocalDate.now()
        );

        taskService.addDependency(
                dependent.getId(),
                prerequisite.getId()
        );

        assertEquals(
                prerequisite.getId(),
                schedulingEngine.getRecommendedNextTask()
                        .orElseThrow()
                        .getId()
        );
    }

    @Test
    void shouldReturnEmptyWhenNoReadyTasksExist() {

        assertTrue(
                schedulingEngine.getRecommendedNextTask().isEmpty()
        );
    }
}
