
package com.taskforge.repository;

import com.taskforge.model.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository {

    void saveAll(List<Project> projects);

    List<Project> findAll();

    Optional<Project> findById(String projectId);

    void deleteById(String projectId);
}
