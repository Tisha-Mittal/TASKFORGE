
package com.taskforge.service;

import com.taskforge.model.Priority;
import com.taskforge.model.Task;
import com.taskforge.model.TaskStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DependencyServiceTest {

    private TaskService taskService;

    private Task taskA;
    private Task taskB;
    private Task taskC;

    @BeforeEach
    void setUp() {

        taskService = new TaskService();

        taskA = new Task(
                "Design Database",
                "Create database structure",
                Priority.HIGH,
                null
        );

        taskB = new Task(
                "Build Backend",
                "Develop backend APIs",
                Priority.MEDIUM,
                null
        );

        taskC = new Task(
                "Build Frontend",
                "Develop frontend",
                Priority.LOW,
                null
        );

        taskService.addTask(taskA);
        taskService.addTask(taskB);
        taskService.addTask(taskC);
    }

    @Test
    void shouldAddDependency() {

        boolean result = taskService.addDependency(
                taskB.getId(),
                taskA.getId()
        );

        assertTrue(result);

        assertTrue(
                taskB.getDependencies().contains(taskA.getId())
        );
    }

    @Test
    void shouldRejectSelfDependency() {

        assertThrows(
                IllegalArgumentException.class,
                () -> taskService.addDependency(
                        taskA.getId(),
                        taskA.getId()
                )
        );
    }

    @Test
    void shouldRejectNonExistingDependency() {

        assertFalse(
                taskService.addDependency(
                        taskA.getId(),
                        "INVALID-ID"
                )
        );
    }

    @Test
    void shouldPreventCircularDependency() {

        taskService.addDependency(
                taskB.getId(),
                taskA.getId()
        );

        taskService.addDependency(
                taskC.getId(),
                taskB.getId()
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> taskService.addDependency(
                        taskA.getId(),
                        taskC.getId()
                )
        );

        assertFalse(taskService.hasCircularDependency());
    }

    @Test
    void shouldIdentifyBlockedTasks() {

        taskService.addDependency(
                taskB.getId(),
                taskA.getId()
        );

        List<Task> blockedTasks =
                taskService.getBlockedTasks();

        assertEquals(1, blockedTasks.size());

        assertEquals(
                taskB.getId(),
                blockedTasks.get(0).getId()
        );
    }

    @Test
    void shouldUnlockTaskAfterDependencyCompletion() {

        taskService.addDependency(
                taskB.getId(),
                taskA.getId()
        );

        assertTrue(taskService.isTaskBlocked(taskB));

        taskService.updateTaskStatus(
                taskA.getId(),
                TaskStatus.COMPLETED
        );

        assertFalse(taskService.isTaskBlocked(taskB));
    }

    @Test
    void shouldRemoveDependency() {

        taskService.addDependency(
                taskB.getId(),
                taskA.getId()
        );

        boolean removed = taskService.removeDependency(
                taskB.getId(),
                taskA.getId()
        );

        assertTrue(removed);

        assertTrue(taskB.getDependencies().isEmpty());
    }

    @Test
    void shouldGenerateCorrectExecutionOrder() {

        taskService.addDependency(
                taskB.getId(),
                taskA.getId()
        );

        taskService.addDependency(
                taskC.getId(),
                taskB.getId()
        );

        List<Task> order =
                taskService.getExecutionOrder();

        assertEquals(3, order.size());

        assertTrue(
                order.indexOf(taskA) < order.indexOf(taskB)
        );

        assertTrue(
                order.indexOf(taskB) < order.indexOf(taskC)
        );
    }

    @Test
    void shouldPreventDeletingTaskWithDependents() {

        taskService.addDependency(
                taskB.getId(),
                taskA.getId()
        );

        assertThrows(
                IllegalStateException.class,
                () -> taskService.deleteTask(taskA.getId())
        );
    }

    @Test
    void shouldAllowDeletingTaskWithoutDependents() {

        assertTrue(
                taskService.deleteTask(taskC.getId())
        );

        assertTrue(
                taskService.findTaskById(taskC.getId()).isEmpty()
        );
    }

    @Test
    void shouldGenerateExecutionOrderIgnoringDanglingDependencies() {

        TaskService isolatedService = new TaskService();

        Task taskA = new Task(
                "Task A", "", Priority.HIGH, null
        );
        Task taskB = new Task(
                "Task B", "", Priority.MEDIUM, null
        );

        isolatedService.addTask(taskA);
        isolatedService.addTask(taskB);

        // Add a dependency on a task that does not exist (dangling reference).
        taskB.addDependency("NONEXISTENT-ID");

        // Should not throw and should still produce a valid order.
        List<Task> order = isolatedService.getExecutionOrder();

        assertEquals(2, order.size());
        assertTrue(order.contains(taskA));
        assertTrue(order.contains(taskB));
    }
}
