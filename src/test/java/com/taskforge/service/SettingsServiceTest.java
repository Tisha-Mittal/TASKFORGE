
package com.taskforge.service;

import com.taskforge.model.AppSettings;
import com.taskforge.model.Priority;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SettingsServiceTest {

    @TempDir
    Path tempDir;

    private SettingsService service;
    private Path settingsFile;

    @BeforeEach
    void setUp() {

        settingsFile = tempDir.resolve("settings.json");
        service = new SettingsService(settingsFile.toString());
    }

    @Test
    void shouldHaveDefaultSettings() {

        AppSettings settings = service.getSettings();

        assertEquals("data", settings.getDataDirectory());
        assertEquals(Priority.MEDIUM, settings.getDefaultPriority());
        assertEquals(7, settings.getDefaultDeadlineDays());
    }

    @Test
    void shouldUpdateDataDirectory() {

        service.setDataDirectory("custom/data");

        assertEquals("custom/data", service.getSettings().getDataDirectory());
    }

    @Test
    void shouldRejectEmptyDataDirectory() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.setDataDirectory("")
        );
    }

    @Test
    void shouldUpdateDefaultPriority() {

        service.setDefaultPriority("CRITICAL");

        assertEquals(Priority.CRITICAL, service.getSettings().getDefaultPriority());
    }

    @Test
    void shouldRejectInvalidPriority() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.setDefaultPriority("INVALID")
        );
    }

    @Test
    void shouldUpdateDefaultDeadlineDays() {

        service.setDefaultDeadlineDays(30);

        assertEquals(30, service.getSettings().getDefaultDeadlineDays());
    }

    @Test
    void shouldRejectNonPositiveDeadlineDays() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.setDefaultDeadlineDays(0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.setDefaultDeadlineDays(-5)
        );
    }

    @Test
    void shouldGetAppInfo() {

        String info = service.getAppInfo();

        assertTrue(info.contains("TASKFORGE"));
        assertTrue(info.contains("v1.0.0"));
    }

    @Test
    void shouldPersistSettingsAcrossInstances() {

        service.setDataDirectory("persisted/data");
        service.setDefaultPriority("HIGH");

        SettingsService service2 = new SettingsService(settingsFile.toString());

        assertEquals("persisted/data", service2.getSettings().getDataDirectory());
        assertEquals(Priority.HIGH, service2.getSettings().getDefaultPriority());
    }
}
