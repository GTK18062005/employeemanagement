package com.staffmanagement.repository;

import com.staffmanagement.model.Task;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAssignedTo(String username);

    List<Task> findByProject_Id(Long projectId);
}
