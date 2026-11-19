
package com.taskforge.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.taskforge.model.AppSettings;
import com.taskforge.model.Priority;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SettingsService {

    private static final String DEFAULT_SETTINGS_FILE = "data/settings.json";

    private final ObjectMapper objectMapper;
    private final Path settingsFile;
    private AppSettings settings;

    public SettingsService() {

        this(DEFAULT_SETTINGS_FILE);
    }

    public SettingsService(String settingsFilePath) {

        this.objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JavaTimeModule());

        objectMapper.disable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
        );

        this.settingsFile = Path.of(settingsFilePath);
        this.settings = loadSettings();
    }

    private AppSettings loadSettings() {

        if (!Files.exists(settingsFile)) {
            return new AppSettings();
        }

        try {

            return objectMapper.readValue(settingsFile.toFile(), AppSettings.class);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to load settings: " + e.getMessage(), e
            );
        }
    }

    private void saveSettings() {

        try {

            Path parent = settingsFile.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(settingsFile.toFile(), settings);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to save settings: " + e.getMessage(), e
            );
        }
    }

    public AppSettings getSettings() {
        return settings;
    }

    public void setDataDirectory(String dataDirectory) {

        if (dataDirectory == null || dataDirectory.isBlank()) {
            throw new IllegalArgumentException("Data directory cannot be empty.");
        }

        settings.setDataDirectory(dataDirectory.trim());

        saveSettings();
    }

    public void setDefaultPriority(String priority) {

        try {

            Priority p = Priority.valueOf(priority.trim().toUpperCase());

            settings.setDefaultPriority(p);

            saveSettings();

        } catch (IllegalArgumentException e) {

            throw new IllegalArgumentException(
                    "Invalid priority. Use: LOW, MEDIUM, HIGH, CRITICAL"
            );
        }
    }

    public void setDefaultDeadlineDays(Integer days) {

        if (days == null || days <= 0) {
            throw new IllegalArgumentException("Default deadline days must be positive.");
        }

        settings.setDefaultDeadlineDays(days);

        saveSettings();
    }

    public String getAppInfo() {

        return "TASKFORGE v1.0.0\n" +
                "Plan smarter. Execute better.\n" +
                "Java Task & Project Manager (CLI)";
    }
}
