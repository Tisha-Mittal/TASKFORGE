
package com.taskforge.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AppSettings {

    private String dataDirectory;
    private Priority defaultPriority;
    private Integer defaultDeadlineDays;

    public AppSettings() {

        this.dataDirectory = "data";
        this.defaultPriority = Priority.MEDIUM;
        this.defaultDeadlineDays = 7;
    }

    @JsonCreator
    public AppSettings(
            @JsonProperty("dataDirectory") String dataDirectory,
            @JsonProperty("defaultPriority") Priority defaultPriority,
            @JsonProperty("defaultDeadlineDays") Integer defaultDeadlineDays) {

        this.dataDirectory = (dataDirectory == null || dataDirectory.isBlank())
                ? "data"
                : dataDirectory.trim();

        this.defaultPriority = defaultPriority == null
                ? Priority.MEDIUM
                : defaultPriority;

        this.defaultDeadlineDays = defaultDeadlineDays == null
                ? 7
                : defaultDeadlineDays;
    }

    public String getDataDirectory() {
        return dataDirectory;
    }

    public void setDataDirectory(String dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public Priority getDefaultPriority() {
        return defaultPriority;
    }

    public void setDefaultPriority(Priority defaultPriority) {
        this.defaultPriority = defaultPriority;
    }

    public Integer getDefaultDeadlineDays() {
        return defaultDeadlineDays;
    }

    public void setDefaultDeadlineDays(Integer defaultDeadlineDays) {
        this.defaultDeadlineDays = defaultDeadlineDays;
    }
}
